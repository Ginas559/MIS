package vn.iotstar.coolenglish.facade;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import vn.iotstar.coolenglish.config.JPAUtil;
import vn.iotstar.coolenglish.entity.Course;
import vn.iotstar.coolenglish.entity.EnglishClass;
import vn.iotstar.coolenglish.entity.Enrollment;
import vn.iotstar.coolenglish.entity.Invoice;
import vn.iotstar.coolenglish.entity.Student;
import vn.iotstar.coolenglish.enums.EnrollmentStatus;
import vn.iotstar.coolenglish.enums.InvoiceStatus;

/**
 * EnrollmentFacade - Singleton Facade Pattern
 * 
 * Điều phối luồng ghi danh phức tạp:
 * 1. Kiểm tra chỗ trống (CheckCapacity)
 * 2. Tạo đơn ghi danh (Enrollment)
 * 3. Tính phí dựa trên khóa học (Course Fee)
 * 4. Xuất hóa đơn (Invoice)
 * 
 * Tái sử dụng:
 * - Singleton (Task C)
 * - State Pattern (Task H)
 * - Template Method DAO (Task D)
 */
public class EnrollmentFacade {

    // ========== SINGLETON PATTERN ==========
    private static EnrollmentFacade instance;

    // ========== PRIVATE CONSTRUCTOR ==========
    private EnrollmentFacade() {
    }

    // ========== SINGLETON GETTER ==========
    public static synchronized EnrollmentFacade getInstance() {
        if (instance == null) {
            instance = new EnrollmentFacade();
        }
        return instance;
    }

    // ========== FACADE METHOD ==========
    /**
     * Quy trình ghi danh học viên (Enrollment Process)
     * 
     * 1. Kiểm tra xem lớp có chỗ trống không (Check Capacity)
     * 2. Tạo Enrollment
     * 3. Tính phí từ Course
     * 4. Tạo Invoice
     * 5. Lưu vào DB
     * 
     * @param classID Mã lớp học
     * @param studentEmail Email học viên
     * @return EnrollmentResult chứa enrollment + invoice
     * @throws IllegalArgumentException nếu lớp không tồn tại, học viên không tồn tại, v.v.
     * @throws IllegalStateException nếu lớp CLOSED hoặc đã đóng
     */
    public EnrollmentResult enrollStudent(String classID, String studentEmail) {
        String normalizedClassID = normalize(classID);
        String normalizedEmail = normalize(studentEmail);

        if (normalizedClassID == null || normalizedEmail == null) {
            throw new IllegalArgumentException("classID và studentEmail là bắt buộc.");
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            // ========== BƯỚC 1: CHECK CHỖ TRỐNG + STATE ==========
            EnglishClass clazz = em.find(EnglishClass.class, normalizedClassID);
            if (clazz == null) {
                throw new IllegalArgumentException("Lớp học không tồn tại: " + normalizedClassID);
            }
            clazz.updateInternalState();

            int currentEnrollment = clazz.getCurrentEnrollment() == null ? 0 : clazz.getCurrentEnrollment();
            int maxCapacity = clazz.getMaxCapacity() == null ? Integer.MAX_VALUE : clazz.getMaxCapacity();
            if (currentEnrollment >= maxCapacity) {
                throw new IllegalStateException("Lớp học đã đầy. Không thể ghi danh.");
            }

            TypedQuery<Student> studentQuery = em.createQuery(
                    "SELECT s FROM Student s WHERE s.email = :email", Student.class);
            studentQuery.setParameter("email", normalizedEmail);
            java.util.List<Student> students = studentQuery.getResultList();
            Student student = students.isEmpty() ? null : students.get(0);
            if (student == null) {
                throw new IllegalArgumentException("Học viên không tồn tại: " + normalizedEmail);
            }

            TypedQuery<Long> duplicateQuery = em.createQuery(
                    "SELECT COUNT(e) FROM Enrollment e WHERE e.englishClass.classID = :classID AND e.student.id = :studentId",
                    Long.class);
            duplicateQuery.setParameter("classID", normalizedClassID);
            duplicateQuery.setParameter("studentId", student.getId());
            if (duplicateQuery.getSingleResult() > 0) {
                throw new IllegalStateException("Học viên đã ghi danh lớp này.");
            }

            clazz.register(student);

            // ========== BƯỚC 2: TẠO ENROLLMENT ==========
            Enrollment enrollment = new Enrollment();
            enrollment.setEnglishClass(clazz);
            enrollment.setStudent(student);
            enrollment.setStatus(EnrollmentStatus.ENROLLED);
            enrollment.setEnrolledAt(LocalDateTime.now());
            em.persist(enrollment);
            em.flush();

            // ========== BƯỚC 3: TÍNH PHÍ TỪ COURSE ==========
            Double totalFee = 0.0;
            String courseID = clazz.getCourseID();
            if (courseID != null && !courseID.isBlank()) {
                Course course = em.find(Course.class, courseID);
                if (course != null && course.getFee() != null) {
                    totalFee = course.getFee();
                }
            }

            // ========== BƯỚC 4: TẠO INVOICE ==========
            Invoice invoice = new Invoice();
            invoice.setInvoiceNumber(generateInvoiceNumber());
            invoice.setEnrollment(enrollment);
            enrollment.setInvoice(invoice);
            invoice.setTotalAmount(totalFee);
            invoice.setStatus(InvoiceStatus.UNPAID);
            invoice.setCreatedAt(LocalDateTime.now());
            em.persist(invoice);

            // ========== BƯỚC 5: COMMIT ĐỒNG THỜI ==========
            em.merge(clazz);
            em.getTransaction().commit();
            return new EnrollmentResult(enrollment, invoice);
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    // ========== HELPER METHODS ==========

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String generateInvoiceNumber() {
        return "INV-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    // ========== RESULT CLASS ==========
    /**
     * Kết quả của quá trình ghi danh
     */
    public static class EnrollmentResult {
        private final Enrollment enrollment;
        private final Invoice invoice;

        public EnrollmentResult(Enrollment enrollment, Invoice invoice) {
            this.enrollment = enrollment;
            this.invoice = invoice;
        }

        public Enrollment getEnrollment() {
            return enrollment;
        }

        public Invoice getInvoice() {
            return invoice;
        }

        @Override
        public String toString() {
            return "EnrollmentResult{" +
                    "enrollment=" + (enrollment != null ? enrollment.getId() : null) +
                    ", invoice=" + (invoice != null ? invoice.getInvoiceNumber() : null) +
                    ", totalAmount=" + (invoice != null ? invoice.getTotalAmount() : 0) +
                    "}";
        }
    }
}

