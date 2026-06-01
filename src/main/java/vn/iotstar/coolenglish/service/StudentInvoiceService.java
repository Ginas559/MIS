package vn.iotstar.coolenglish.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

import vn.iotstar.coolenglish.dao.impl.EnglishClassDAO;
import vn.iotstar.coolenglish.dao.impl.PaymentDAO;
import vn.iotstar.coolenglish.dao.impl.StudentDAO;
import vn.iotstar.coolenglish.entity.Course;
import vn.iotstar.coolenglish.entity.EnglishClass;
import vn.iotstar.coolenglish.entity.Enrollment;
import vn.iotstar.coolenglish.entity.Invoice;
import vn.iotstar.coolenglish.entity.Payment;
import vn.iotstar.coolenglish.entity.Schedule;
import vn.iotstar.coolenglish.entity.Student;
import vn.iotstar.coolenglish.enums.InvoiceStatus;
import vn.iotstar.coolenglish.enums.PaymentStatus;
import vn.iotstar.coolenglish.invoice.BuyerInfo;
import vn.iotstar.coolenglish.invoice.ClassEnrollmentSection;
import vn.iotstar.coolenglish.invoice.InvoiceBuilder;
import vn.iotstar.coolenglish.invoice.InvoiceDocument;
import vn.iotstar.coolenglish.invoice.InvoiceHeaderInfo;
import vn.iotstar.coolenglish.invoice.InvoiceLineItem;
import vn.iotstar.coolenglish.invoice.InvoiceTotals;
import vn.iotstar.coolenglish.invoice.PaymentTransactionSection;

/**
 * Lắp ráp {@link InvoiceDocument} từ giao dịch thanh toán bằng {@link InvoiceBuilder}.
 */
public class StudentInvoiceService {

    private static final double VAT_RATE_PERCENT = 10.0;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final PaymentDAO paymentDAO = new PaymentDAO();
    private final EnglishClassDAO englishClassDAO = new EnglishClassDAO();
    private final StudentDAO studentDAO = new StudentDAO();

    public InvoiceDocument buildForStudent(String transactionRef, String studentEmail) {
        Objects.requireNonNull(transactionRef, "transactionRef");
        Objects.requireNonNull(studentEmail, "studentEmail");
        Payment payment = paymentDAO.findByTransactionRefWithInvoiceContext(transactionRef);
        if (payment == null) {
            throw new IllegalArgumentException("Khong tim thay giao dich.");
        }
        if (!studentEmail.trim().equalsIgnoreCase(trim(payment.getStudentEmail()))) {
            throw new SecurityException("Ban khong co quyen xem hoa don nay.");
        }
        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new IllegalStateException("Chi xem hoa don khi thanh toan thanh cong.");
        }
        Invoice invoice = payment.getInvoice();
        if (invoice == null) {
            throw new IllegalStateException("Giao dich chua co hoa don.");
        }

        double grandTotal = nz(invoice.getTotalAmount(), nz(payment.getAmount(), 0.0));
        double subtotalExVat = round2(grandTotal / (1.0 + VAT_RATE_PERCENT / 100.0));
        double vatAmount = round2(grandTotal - subtotalExVat);

        EnglishClass clazz = resolveClass(payment, invoice);
        Student student = resolveStudent(payment, invoice);
        Course course = clazz != null && clazz.getCourse() != null ? clazz.getCourse() : null;

        ClassEnrollmentSection classSection = buildClassSection(clazz, course);
        BuyerInfo buyer = buildBuyer(student, payment);

        InvoiceHeaderInfo header = new InvoiceHeaderInfo(
                "Trung tâm Tiếng Anh CoolEnglish",
                "Khu đô thị mẫu — TP. Hồ Chí Minh",
                "0312345678",
                "1900-xxxx",
                "HÓA ĐƠN THANH TOÁN KHÓA HỌC",
                resolveIssueDate(invoice));

        InvoiceLineItem tuition = new InvoiceLineItem("COURSE", "Học phí khóa học (chưa bao gồm VAT "
                + (int) VAT_RATE_PERCENT + "%)", 1, subtotalExVat, subtotalExVat);
        InvoiceLineItem materials = new InvoiceLineItem("MATERIALS", "Tài liệu & nền tảng học trực tuyến", 1, 0.0, 0.0);

        InvoiceTotals totals = new InvoiceTotals(subtotalExVat, VAT_RATE_PERCENT, vatAmount, 0.0, grandTotal);

        PaymentTransactionSection payBlock = new PaymentTransactionSection(
                mapPaymentMethod(payment.getMethod()),
                payment.getTransactionRef(),
                payment.getPaymentDate(),
                mapPaymentStatus(payment.getStatus()),
                mapInvoiceStatus(invoice.getStatus()));

        return new InvoiceBuilder()
                .invoiceNumber(invoice.getInvoiceNumber())
                .header(header)
                .buyer(buyer)
                .classSection(classSection)
                .addLineItem(tuition)
                .addLineItem(materials)
                .totals(totals)
                .paymentSection(payBlock)
                .addFootnote("Hóa đơn được lập tự động từ hệ thống quản lý sau khi thanh toán thành công.")
                .addFootnote("Mọi thắc mắc vui lòng liên hệ hotline " + header.getIssuerHotline() + " kèm mã giao dịch.")
                .build();
    }

    private static LocalDate resolveIssueDate(Invoice invoice) {
        if (invoice.getPaidAt() != null) {
            return invoice.getPaidAt().toLocalDate();
        }
        if (invoice.getCreatedAt() != null) {
            return invoice.getCreatedAt().toLocalDate();
        }
        return LocalDate.now();
    }

    private EnglishClass resolveClass(Payment payment, Invoice invoice) {
        Enrollment enrollment = invoice.getEnrollment();
        if (enrollment != null && enrollment.getEnglishClass() != null) {
            return enrollment.getEnglishClass();
        }
        String classId = payment.getClassID();
        if (classId != null && !classId.isBlank()) {
            return englishClassDAO.findByClassIdWithInvoiceRelations(classId.trim());
        }
        return null;
    }

    private Student resolveStudent(Payment payment, Invoice invoice) {
        Enrollment enrollment = invoice.getEnrollment();
        if (enrollment != null && enrollment.getStudent() != null) {
            return enrollment.getStudent();
        }
        String email = payment.getStudentEmail();
        if (email != null && !email.isBlank()) {
            return studentDAO.findByEmail(email.trim());
        }
        return null;
    }

    private ClassEnrollmentSection buildClassSection(EnglishClass clazz, Course course) {
        if (clazz == null) {
            return new ClassEnrollmentSection("—", "—", "—", "—", "—", null, null, null, "—", "—");
        }
        String courseName = course != null && course.getCourseName() != null ? course.getCourseName() : "—";
        String courseId = course != null && course.getCourseID() != null ? course.getCourseID()
                : (clazz.getCourseID() != null ? clazz.getCourseID() : "—");
        String level = course != null && course.getLevel() != null ? course.getLevel() : "—";
        Integer duration = course != null ? course.getDuration() : null;
        String roomLabel = buildRoomLabel(clazz);
        String scheduleSummary = buildScheduleSummary(clazz.getSchedule());
        return new ClassEnrollmentSection(
                clazz.getClassID(),
                nzStr(clazz.getClassName(), "—"),
                courseId,
                courseName,
                level,
                duration,
                clazz.getStartDate(),
                clazz.getEndDate(),
                roomLabel,
                scheduleSummary);
    }

    private static String buildRoomLabel(EnglishClass clazz) {
        if (clazz.getRoom() != null) {
            String name = nzStr(clazz.getRoom().getRoomName(), clazz.getRoom().getRoomID());
            return name + " (" + clazz.getRoom().getRoomID() + ")";
        }
        return nzStr(clazz.getRoomID(), "—");
    }

    private static String buildScheduleSummary(Schedule schedule) {
        if (schedule == null) {
            return "—";
        }
        StringBuilder sb = new StringBuilder();
        if (schedule.getDescription() != null && !schedule.getDescription().isBlank()) {
            sb.append(schedule.getDescription().trim());
        }
        if (schedule.getTotalSessions() != null) {
            if (sb.length() > 0) {
                sb.append(" — ");
            }
            sb.append("Tổng buổi: ").append(schedule.getTotalSessions());
        }
        return sb.length() > 0 ? sb.toString() : "—";
    }

    private BuyerInfo buildBuyer(Student student, Payment payment) {
        if (student != null) {
            return new BuyerInfo(
                    nzStr(student.getFullName(), "Học viên"),
                    nzStr(student.getEmail(), trim(payment.getStudentEmail())),
                    student.getPhone() != null ? student.getPhone() : "—",
                    student.getStudentID() != null ? student.getStudentID() : "—");
        }
        return new BuyerInfo(
                "Học viên",
                trim(payment.getStudentEmail()),
                "—",
                "—");
    }

    private static String mapPaymentMethod(String method) {
        if (method == null) {
            return "—";
        }
        return switch (method) {
            case "VNPAY" -> "VNPay";
            case "MB_BANK" -> "Chuyển khoản MB Bank";
            case "CASH" -> "Tiền mặt tại quầy";
            default -> method;
        };
    }

    private static String mapPaymentStatus(PaymentStatus status) {
        if (status == null) {
            return "—";
        }
        return switch (status) {
            case COMPLETED -> "Hoàn tất";
            case PENDING -> "Đang xử lý";
            case FAILED -> "Thất bại";
            case REFUNDED -> "Đã hoàn tiền";
        };
    }

    private static String mapInvoiceStatus(InvoiceStatus status) {
        if (status == null) {
            return "—";
        }
        return switch (status) {
            case PAID -> "Đã thanh toán";
            case UNPAID -> "Chưa thanh toán";
            case PARTIALLY_PAID -> "Thanh toán một phần";
            case CANCELLED -> "Đã hủy";
        };
    }

    private static double nz(Double v, double d) {
        return v == null ? d : v;
    }

    private static double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    private static String nzStr(String s, String fallback) {
        return s == null || s.isBlank() ? fallback : s;
    }

    private static String trim(String s) {
        return s == null ? null : s.trim();
    }

    /** Định dạng tiền tệ hiển thị (JSP). */
    public static String formatMoney(double amount) {
        return String.format(Locale.forLanguageTag("vi-VN"), "%,.0f ₫", amount);
    }

    public static String formatDate(LocalDate date) {
        return date == null ? "—" : DATE_FMT.format(date);
    }
}
