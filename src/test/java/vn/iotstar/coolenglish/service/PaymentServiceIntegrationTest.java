package vn.iotstar.coolenglish.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import vn.iotstar.coolenglish.config.JPAUtil;
import vn.iotstar.coolenglish.entity.Course;
import vn.iotstar.coolenglish.entity.EnglishClass;
import vn.iotstar.coolenglish.entity.Payment;
import vn.iotstar.coolenglish.entity.Student;
import vn.iotstar.coolenglish.entity.Teacher;
import vn.iotstar.coolenglish.entity.UserAccount;
import vn.iotstar.coolenglish.enums.ClassStatus;
import vn.iotstar.coolenglish.enums.PaymentMethod;
import vn.iotstar.coolenglish.enums.PaymentStatus;
import vn.iotstar.coolenglish.enums.StudentStatus;
import vn.iotstar.coolenglish.enums.TeacherStatus;
import vn.iotstar.coolenglish.enums.UserRole;

class PaymentServiceIntegrationTest {

    @AfterEach
    void cleanUpEntityManagerFactory() {
        JPAUtil.shutDown();
    }

    @Test
    void shouldCreateCashPaymentForOpenClass() {
        TestData data = createBaseData();
        try {
            PaymentService service = new PaymentService();
            Payment payment = service.createAndPay(data.fee, PaymentMethod.CASH, data.courseID, data.studentEmail);

            assertNotNull(payment);
            assertNotNull(payment.getPaymentID());
            assertNotNull(payment.getCourse());
            assertEquals(data.courseID, payment.getCourse().getCourseID());
            assertEquals(data.studentEmail, payment.getStudent().getEmail());
            assertEquals(PaymentStatus.PENDING, payment.getStatus());
        } finally {
            purgeData(data);
        }
    }

    private TestData createBaseData() {
        String token = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String courseID = "PAY_IT_COURSE_" + token;
        String classID = "PAY_IT_CLASS_" + token;
        String teacherEmail = ("pay_it_teacher_" + token + "@test.local").toLowerCase();
        String studentEmail = ("pay_it_student_" + token + "@test.local").toLowerCase();
        double fee = 456789.0;

        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            Course course = new Course();
            course.setCourseID(courseID);
            course.setCourseName("Payment Test Course");
            course.setDescription("test");
            course.setLevel("BEGINNER");
            course.setDuration(20);
            course.setFee(fee);
            course.setStatus("ACTIVE");
            em.persist(course);

            Teacher teacher = new Teacher();
            teacher.setFullName("Payment Test Teacher");
            teacher.setEmail(teacherEmail);
            teacher.setTeacherID("PAYTCH_" + token);
            teacher.setStatus(TeacherStatus.ACTIVE);
            em.persist(teacher);

            EnglishClass clazz = new EnglishClass();
            clazz.setClassID(classID);
            clazz.setClassName("Payment Test Class");
            clazz.setCourseID(courseID);
            clazz.setMaxCapacity(20);
            clazz.setCurrentEnrollment(0);
            clazz.setStatus(ClassStatus.OPEN);
            clazz.setTeacher(teacher);
            clazz.updateInternalState();
            em.persist(clazz);

            UserAccount account = new UserAccount(studentEmail, "payment_test_" + token.toLowerCase(), "123456",
                    UserRole.STUDENT);
            em.persist(account);

            em.getTransaction().commit();
            return new TestData(courseID, classID, studentEmail, teacherEmail, fee);
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    private void purgeData(TestData data) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            TypedQuery<Payment> paymentQuery = em.createQuery(
                    "SELECT p FROM Payment p WHERE p.transactionRef LIKE :txnPrefix AND p.student.email = :studentEmail",
                    Payment.class);
            paymentQuery.setParameter("txnPrefix", "CASH-%");
            paymentQuery.setParameter("studentEmail", data.studentEmail);
            for (Payment payment : paymentQuery.getResultList()) {
                Payment managedPayment = em.contains(payment) ? payment : em.merge(payment);
                em.remove(managedPayment);
            }

            em.createNativeQuery("DELETE FROM user_account WHERE email = ?")
                    .setParameter(1, data.studentEmail)
                    .executeUpdate();

            TypedQuery<Student> studentQuery = em.createQuery(
                    "SELECT s FROM Student s WHERE s.email = :email",
                    Student.class);
            studentQuery.setParameter("email", data.studentEmail);
            for (Student student : studentQuery.getResultList()) {
                em.createNativeQuery("DELETE FROM person WHERE id = ?")
                        .setParameter(1, student.getId())
                        .executeUpdate();
            }

            em.createNativeQuery("DELETE FROM Classes WHERE classID = ?")
                    .setParameter(1, data.classID)
                    .executeUpdate();

            em.createNativeQuery("DELETE FROM Courses WHERE courseID = ?")
                    .setParameter(1, data.courseID)
                    .executeUpdate();

            em.createNativeQuery("DELETE FROM person WHERE person_type = 'TEACHER' AND email = ?")
                    .setParameter(1, data.teacherEmail)
                    .executeUpdate();

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

    private record TestData(String courseID, String classID, String studentEmail, String teacherEmail, Double fee) {
    }
}
