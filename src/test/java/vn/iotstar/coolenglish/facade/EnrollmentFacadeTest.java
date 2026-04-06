package vn.iotstar.coolenglish.facade;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import vn.iotstar.coolenglish.config.JPAUtil;
import vn.iotstar.coolenglish.entity.Course;
import vn.iotstar.coolenglish.entity.EnglishClass;
import vn.iotstar.coolenglish.entity.Enrollment;
import vn.iotstar.coolenglish.entity.Invoice;
import vn.iotstar.coolenglish.entity.Student;
import vn.iotstar.coolenglish.enums.ClassStatus;
import vn.iotstar.coolenglish.enums.InvoiceStatus;
import vn.iotstar.coolenglish.enums.StudentStatus;

class EnrollmentFacadeTest {

    @AfterEach
    void cleanUpEntityManagerFactory() {
        JPAUtil.shutDown();
    }

    @Test
    void singletonStructureShouldBeValid() {
        Constructor<?>[] constructors = EnrollmentFacade.class.getDeclaredConstructors();
        assertEquals(1, constructors.length);
        assertTrue(Modifier.isPrivate(constructors[0].getModifiers()));

        EnrollmentFacade facade1 = EnrollmentFacade.getInstance();
        EnrollmentFacade facade2 = EnrollmentFacade.getInstance();
        assertSame(facade1, facade2);
    }

    @Test
    void scenarioShouldEnrollSuccessfullyWhenClassHasCapacity() {
        TestData data = createBaseData("SCN_OK_", ClassStatus.OPEN, 2, 0);
        try {
            EnrollmentFacade.EnrollmentResult result = EnrollmentFacade.getInstance()
                    .enrollStudent(data.classID, data.studentEmail);

            assertNotNull(result);
            assertNotNull(result.getEnrollment());
            assertNotNull(result.getInvoice());
            assertNotNull(result.getEnrollment().getId());
            assertEquals(InvoiceStatus.UNPAID, result.getInvoice().getStatus());
            assertEquals(data.courseFee, result.getInvoice().getTotalAmount());
        } finally {
            purgeByClassAndStudent(data.classID, data.studentEmail, data.courseID);
        }
    }

    @Test
    void pathShouldBlockEnrollmentWhenClassIsFull() {
        TestData data = createBaseData("SCN_FULL_", ClassStatus.OPEN, 1, 1);
        try {
            assertThrows(IllegalStateException.class,
                    () -> EnrollmentFacade.getInstance().enrollStudent(data.classID, data.studentEmail));
        } finally {
            purgeByClassAndStudent(data.classID, data.studentEmail, data.courseID);
        }
    }

    @Test
    void dataIntegrityShouldPersistEnrollmentAndInvoiceTogether() {
        TestData data = createBaseData("SCN_TX_", ClassStatus.OPEN, 3, 0);
        try {
            EnrollmentFacade.EnrollmentResult result = EnrollmentFacade.getInstance()
                    .enrollStudent(data.classID, data.studentEmail);

            assertNotNull(result.getEnrollment().getId());

            EntityManager em = JPAUtil.getEntityManager();
            try {
                Enrollment enrollment = em.find(Enrollment.class, result.getEnrollment().getId());
                assertNotNull(enrollment);

                TypedQuery<Invoice> invoiceQuery = em.createQuery(
                        "SELECT i FROM Invoice i WHERE i.enrollment.id = :enrollmentId", Invoice.class);
                invoiceQuery.setParameter("enrollmentId", enrollment.getId());
                java.util.List<Invoice> invoices = invoiceQuery.getResultList();

                assertFalse(invoices.isEmpty());
                assertEquals(1, invoices.size());
                assertEquals(InvoiceStatus.UNPAID, invoices.get(0).getStatus());
            } finally {
                em.close();
            }
        } finally {
            purgeByClassAndStudent(data.classID, data.studentEmail, data.courseID);
        }
    }

    private TestData createBaseData(String prefix, ClassStatus classStatus, int maxCapacity, int currentEnrollment) {
        String token = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String courseID = prefix + "COURSE_" + token;
        String classID = prefix + "CLASS_" + token;
        String studentEmail = (prefix + token + "@test.local").toLowerCase();
        double fee = 1234567.0;

        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            Course course = new Course();
            course.setCourseID(courseID);
            course.setCourseName("Facade Test Course");
            course.setDescription("test");
            course.setLevel("BEGINNER");
            course.setDuration(20);
            course.setFee(fee);
            course.setStatus("ACTIVE");
            em.persist(course);

            EnglishClass clazz = new EnglishClass();
            clazz.setClassID(classID);
            clazz.setClassName("Facade Test Class");
            clazz.setCourseID(courseID);
            clazz.setMaxCapacity(maxCapacity);
            clazz.setCurrentEnrollment(currentEnrollment);
            clazz.setStatus(classStatus);
            clazz.updateInternalState();
            em.persist(clazz);

            Student student = new Student();
            student.setFullName("Facade Test Student");
            student.setEmail(studentEmail);
            student.setStudentID("STU_" + token);
            student.setStatus(StudentStatus.ACTIVE);
            em.persist(student);

            em.getTransaction().commit();
            return new TestData(courseID, classID, studentEmail, fee);
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    private void purgeByClassAndStudent(String classID, String studentEmail, String courseID) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            TypedQuery<Student> studentQuery = em.createQuery("SELECT s FROM Student s WHERE s.email = :email", Student.class);
            studentQuery.setParameter("email", studentEmail);
            java.util.List<Student> students = studentQuery.getResultList();
            Long studentId = students.isEmpty() ? null : students.get(0).getId();

            em.createNativeQuery("DELETE FROM Invoices WHERE enrollment_id IN (SELECT id FROM Enrollments WHERE class_id = ?)")
                    .setParameter(1, classID)
                    .executeUpdate();
            if (studentId != null) {
                em.createNativeQuery("DELETE FROM Enrollments WHERE class_id = ? AND student_person_id = ?")
                        .setParameter(1, classID)
                        .setParameter(2, studentId)
                        .executeUpdate();
            } else {
                em.createNativeQuery("DELETE FROM Enrollments WHERE class_id = ?")
                        .setParameter(1, classID)
                        .executeUpdate();
            }

            em.createNativeQuery("DELETE FROM Classes WHERE classID = ?")
                    .setParameter(1, classID)
                    .executeUpdate();

            if (courseID != null) {
                em.createNativeQuery("DELETE FROM Courses WHERE courseID = ?")
                        .setParameter(1, courseID)
                        .executeUpdate();
            }

            if (studentId != null) {
                em.createNativeQuery("DELETE FROM person WHERE id = ?")
                        .setParameter(1, studentId)
                        .executeUpdate();
            }

            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    private static final class TestData {
        private final String courseID;
        private final String classID;
        private final String studentEmail;
        private final Double courseFee;

        private TestData(String courseID, String classID, String studentEmail, Double courseFee) {
            this.courseID = courseID;
            this.classID = classID;
            this.studentEmail = studentEmail;
            this.courseFee = courseFee;
        }
    }
}

