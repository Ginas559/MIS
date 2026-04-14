package vn.iotstar.coolenglish.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import vn.iotstar.coolenglish.dao.impl.CourseDAO;
import vn.iotstar.coolenglish.dao.impl.EnglishClassDAO;
import vn.iotstar.coolenglish.dao.impl.InvoiceDAO;
import vn.iotstar.coolenglish.dao.impl.PaymentDAO;
import vn.iotstar.coolenglish.dao.impl.StudentDAO;
import vn.iotstar.coolenglish.dao.impl.UserAccountDAO;
import vn.iotstar.coolenglish.entity.Course;
import vn.iotstar.coolenglish.entity.EnglishClass;
import vn.iotstar.coolenglish.entity.Invoice;
import vn.iotstar.coolenglish.entity.Payment;
import vn.iotstar.coolenglish.entity.Student;
import vn.iotstar.coolenglish.entity.UserAccount;
import vn.iotstar.coolenglish.enums.InvoiceStatus;
import vn.iotstar.coolenglish.enums.PaymentMethod;
import vn.iotstar.coolenglish.enums.PaymentStatus;
import vn.iotstar.coolenglish.enums.StudentStatus;
import vn.iotstar.coolenglish.enums.UserRole;
import vn.iotstar.coolenglish.facade.EnrollmentFacade;
import vn.iotstar.coolenglish.payment.PaymentProcessor;
import vn.iotstar.coolenglish.payment.PaymentStrategy;

public class PaymentService {

    private final PaymentDAO paymentDAO = new PaymentDAO();
    private final InvoiceDAO invoiceDAO = new InvoiceDAO();
    private final CourseDAO courseDAO = new CourseDAO();
    private final EnglishClassDAO classDAO = new EnglishClassDAO();
    private final StudentDAO studentDAO = new StudentDAO();
    private final UserAccountDAO userAccountDAO = new UserAccountDAO();
    private final PaymentProcessor paymentProcessor = new PaymentProcessor();

    public Payment createAndPay(Double amount, PaymentMethod method, String courseID, String studentEmail) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Payment amount must be greater than 0.");
        }
        if (method == null) {
            throw new IllegalArgumentException("Payment method is required.");
        }
        if (courseID == null || courseID.isBlank() || studentEmail == null || studentEmail.isBlank()) {
            throw new IllegalArgumentException("courseID and studentEmail are required.");
        }

        Invoice invoice = createPendingInvoice(amount);
        Course course = courseDAO.findById(courseID, Course.class);
        if (course == null) {
            throw new IllegalArgumentException("Course not found: " + courseID);
        }
        Student student = resolveOrCreateStudentByEmail(studentEmail);

        Payment payment = new Payment();
        payment.setPaymentID(generatePaymentId());
        payment.setInvoice(invoice);
        payment.setCourse(course);
        payment.setStudent(student);
        payment.setAmount(amount);
        payment.setMethod(method.name());
        payment.setTransactionRef(generateTransactionRef(method));
        payment.setPaymentDate(LocalDateTime.now());
        payment.setStatus(PaymentStatus.PENDING);
        paymentDAO.insert(payment);

        PaymentStrategy strategy = paymentProcessor.getStrategy(method);
        strategy.pay(payment);
        paymentDAO.update(payment);
        return payment;
    }

    public Payment createAndPay(Invoice invoice, PaymentMethod method) {
        if (invoice == null) {
            throw new IllegalArgumentException("Invoice is required.");
        }
        if (method == null) {
            throw new IllegalArgumentException("Payment method is required.");
        }

        Payment payment = new Payment();
        payment.setPaymentID(generatePaymentId());
        payment.setInvoice(invoice);
        payment.setAmount(invoice.getTotalAmount());
        payment.setMethod(method.name());
        payment.setTransactionRef(generateTransactionRef(method));
        payment.setPaymentDate(LocalDateTime.now());
        payment.setStatus(PaymentStatus.PENDING);
        paymentDAO.insert(payment);

        PaymentStrategy strategy = paymentProcessor.getStrategy(method);
        strategy.pay(payment);
        paymentDAO.update(payment);
        return payment;
    }

    public Payment handleCallback(String transactionRef, Map<String, String> callbackData) {
        Payment payment = paymentDAO.findByTransactionRefWithInvoice(transactionRef);
        if (payment == null) {
            throw new IllegalArgumentException("Payment not found by transactionRef: " + transactionRef);
        }
        PaymentMethod method = PaymentMethod.valueOf(payment.getMethod());
        PaymentStrategy strategy = paymentProcessor.getStrategy(method);
        strategy.handleCallback(payment, callbackData);
        finalizeEnrollmentIfPaid(payment);
        syncInvoiceStatus(payment);
        paymentDAO.update(payment);
        return payment;
    }

    public Payment confirmCashPayment(String transactionRef) {
        Payment payment = paymentDAO.findByTransactionRefWithInvoice(transactionRef);
        if (payment == null) {
            throw new IllegalArgumentException("Payment not found by transactionRef: " + transactionRef);
        }
        PaymentMethod method = PaymentMethod.valueOf(payment.getMethod());
        PaymentStrategy strategy = paymentProcessor.getStrategy(method);
        strategy.confirm(payment);
        finalizeEnrollmentIfPaid(payment);
        syncInvoiceStatus(payment);
        paymentDAO.update(payment);
        return payment;
    }

    public Payment refund(String transactionRef) {
        Payment payment = paymentDAO.findByTransactionRefWithInvoice(transactionRef);
        if (payment == null) {
            throw new IllegalArgumentException("Payment not found by transactionRef: " + transactionRef);
        }
        PaymentMethod method = PaymentMethod.valueOf(payment.getMethod());
        PaymentStrategy strategy = paymentProcessor.getStrategy(method);
        strategy.refund(payment);
        syncInvoiceStatus(payment);
        paymentDAO.update(payment);
        return payment;
    }

    public List<Payment> getPendingCashPayments() {
        return paymentDAO.findPendingCashPayments();
    }

    public Payment staffUpdateCashPayment(String transactionRef, boolean paid) {
        Payment payment = paymentDAO.findByTransactionRefWithInvoice(transactionRef);
        if (payment == null) {
            throw new IllegalArgumentException("Payment not found by transactionRef: " + transactionRef);
        }
        if (!"CASH".equals(payment.getMethod())) {
            throw new IllegalStateException("Chi xu ly duyet thu cong cho phuong thuc CASH.");
        }
        if (paid) {
            return confirmCashPayment(transactionRef);
        }
        payment.setStatus(PaymentStatus.FAILED);
        payment.setNote("Cash payment not received. Marked as failed by staff.");
        syncInvoiceStatus(payment);
        paymentDAO.update(payment);
        return payment;
    }

    private void syncInvoiceStatus(Payment payment) {
        Invoice invoiceRef = payment.getInvoice();
        if (invoiceRef == null || invoiceRef.getId() == null) {
            return;
        }
        Invoice invoice = invoiceDAO.findById(invoiceRef.getId(), Invoice.class);
        if (invoice == null) {
            return;
        }

        if (payment.getStatus() == PaymentStatus.COMPLETED) {
            invoice.setStatus(InvoiceStatus.PAID);
            invoice.setPaidAt(LocalDateTime.now());
            invoiceDAO.update(invoice);
            return;
        }

        if (payment.getStatus() == PaymentStatus.REFUNDED) {
            invoice.setStatus(InvoiceStatus.CANCELLED);
            invoiceDAO.update(invoice);
            return;
        }

        invoice.setStatus(InvoiceStatus.UNPAID);
        invoiceDAO.update(invoice);
    }

    private void finalizeEnrollmentIfPaid(Payment payment) {
        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            return;
        }
        Course course = payment.getCourse();
        Student student = payment.getStudent();
        String courseID = course == null ? null : course.getCourseID();
        String studentEmail = student == null ? null : student.getEmail();
        if (courseID == null || courseID.isBlank() || studentEmail == null || studentEmail.isBlank()) {
            return;
        }
        EnglishClass clazz = classDAO.findFirstOpenClassByCourseID(courseID);
        if (clazz == null) {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setNote("Payment done but enrollment failed: Khong tim thay lop OPEN cho khoa hoc.");
            return;
        }
        try {
            EnrollmentFacade.EnrollmentResult result = EnrollmentFacade.getInstance()
                    .enrollStudent(clazz.getClassID(), studentEmail);
            if (result != null && result.getInvoice() != null) {
                payment.setInvoice(result.getInvoice());
                payment.setNote("Payment completed and enrollment created.");
            }
        } catch (IllegalStateException | IllegalArgumentException ex) {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setNote("Payment done but enrollment failed: " + ex.getMessage());
        }
    }

    private String generateTransactionRef(PaymentMethod method) {
        return method.name() + "-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }

    private Invoice createPendingInvoice(Double amount) {
        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber("INV-PAY-" + System.currentTimeMillis() + "-"
                + UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase());
        invoice.setTotalAmount(amount);
        invoice.setStatus(InvoiceStatus.UNPAID);
        invoice.setCreatedAt(LocalDateTime.now());
        invoiceDAO.insert(invoice);
        return invoice;
    }

    private String generatePaymentId() {
        return "PAY-" + System.currentTimeMillis() + "-"
                + UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
    }

    private Student resolveOrCreateStudentByEmail(String email) {
        Student existingStudent = studentDAO.findByEmail(email);
        if (existingStudent != null) {
            return existingStudent;
        }

        UserAccount account = userAccountDAO.findByEmail(email);
        if (account == null || account.getRole() != UserRole.STUDENT) {
            throw new IllegalArgumentException("Khong tim thay hoc vien voi email: " + email);
        }

        if (account.getRelatedID() != null) {
            Student linkedStudent = studentDAO.findById(account.getRelatedID(), Student.class);
            if (linkedStudent != null) {
                return linkedStudent;
            }
        }

        Student student = new Student();
        String username = account.getUsername();
        student.setFullName(username == null || username.isBlank() ? account.getEmail() : username);
        student.setEmail(account.getEmail());
        student.setStudentID("STU-" + System.currentTimeMillis());
        student.setStatus(StudentStatus.ACTIVE);
        studentDAO.insert(student);

        account.setRelatedID(student.getId());
        userAccountDAO.update(account);
        return student;
    }
}
