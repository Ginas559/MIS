package vn.iotstar.coolenglish.facade;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import vn.iotstar.coolenglish.dao.impl.AuditLogDAO;
import vn.iotstar.coolenglish.dao.impl.EnglishClassDAO;
import vn.iotstar.coolenglish.dao.impl.EnrollmentDAO;
import vn.iotstar.coolenglish.dao.impl.NotificationDAO;
import vn.iotstar.coolenglish.dao.impl.PaymentDAO;
import vn.iotstar.coolenglish.dao.impl.RoomDAO;
import vn.iotstar.coolenglish.dao.impl.ScheduleDAO;
import vn.iotstar.coolenglish.entity.Notification;
import vn.iotstar.coolenglish.entity.Payment;
import vn.iotstar.coolenglish.entity.UserAccount;
import vn.iotstar.coolenglish.enums.PaymentStatus;
import vn.iotstar.coolenglish.factory.SecurityAccessFactory;
import vn.iotstar.coolenglish.invoice.InvoiceDocument;
import vn.iotstar.coolenglish.service.StudentInvoiceService;

/**
 * Dashboard Facade (Compound Pattern):
 * - DAO: đọc dữ liệu tổng hợp.
 * - Proxy: truy cập course qua SecurityAccessFactory/CourseServiceProxy.
 * - Builder: dựng preview hóa đơn qua StudentInvoiceService + InvoiceBuilder.
 * - Observer: đọc kết quả notification đã được Observer ghi ra DB.
 */
public class DashboardFacade {

    private final EnglishClassDAO englishClassDAO = new EnglishClassDAO();
    private final EnrollmentDAO enrollmentDAO = new EnrollmentDAO();
    private final RoomDAO roomDAO = new RoomDAO();
    private final ScheduleDAO scheduleDAO = new ScheduleDAO();
    private final PaymentDAO paymentDAO = new PaymentDAO();
    private final AuditLogDAO auditLogDAO = new AuditLogDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();
    private final StudentInvoiceService studentInvoiceService = new StudentInvoiceService();

    public DashboardData buildDashboard(UserAccount actor) {
        int totalCourses = SecurityAccessFactory.getInstance().getCourseService(actor).findAll().size();
        int totalClasses = englishClassDAO.findAll(vn.iotstar.coolenglish.entity.EnglishClass.class).size();
        int totalEnrollments = enrollmentDAO.findAll(vn.iotstar.coolenglish.entity.Enrollment.class).size();
        int totalRooms = roomDAO.findAll(vn.iotstar.coolenglish.entity.Room.class).size();
        int totalSchedules = scheduleDAO.findAll(vn.iotstar.coolenglish.entity.Schedule.class).size();
        int pendingCashApprovals = paymentDAO.findPendingCashPayments().size();
        int totalAuditEvents = auditLogDAO.findAll(vn.iotstar.coolenglish.entity.AuditLog.class).size();

        List<Notification> notifications = notificationDAO.findAll(Notification.class);
        notifications.sort(Comparator
                .comparing(Notification::getSendDate, Comparator.nullsLast(Comparator.reverseOrder()))
                .thenComparing(Notification::getNotificationID, Comparator.nullsLast(Comparator.reverseOrder())));

        List<NotificationRow> latestNotifications = new ArrayList<>();
        for (int i = 0; i < notifications.size() && i < 5; i++) {
            Notification item = notifications.get(i);
            latestNotifications.add(new NotificationRow(
                    item.getNotificationID(),
                    item.getRelatedID(),
                    item.getContent(),
                    item.getSendDate()));
        }

        List<InvoicePreview> latestInvoicePreviews = buildLatestInvoicePreviews(5);
        InvoicePreview latestInvoicePreview = latestInvoicePreviews.isEmpty() ? null : latestInvoicePreviews.get(0);

        return new DashboardData(
                totalCourses,
                totalClasses,
                totalEnrollments,
                totalRooms,
                totalSchedules,
                pendingCashApprovals,
                totalAuditEvents,
                notifications.size(),
                latestNotifications,
                latestInvoicePreview,
                latestInvoicePreviews,
                EnrollmentFacade.getInstance().getClass().getSimpleName());
    }

    private List<InvoicePreview> buildLatestInvoicePreviews(int limit) {
        List<InvoicePreview> previews = new ArrayList<>();
        if (limit <= 0) {
            return previews;
        }

        List<Payment> payments = paymentDAO.findCompletedPaymentsWithContext();

        for (Payment payment : payments) {
            if (previews.size() >= limit) {
                break;
            }
            if (payment.getStatus() != PaymentStatus.COMPLETED) {
                continue;
            }
            String studentEmail = payment.getStudent() == null ? null : payment.getStudent().getEmail();
            if (isBlank(payment.getTransactionRef()) || isBlank(studentEmail)) {
                continue;
            }
            try {
                InvoiceDocument invoiceDoc = studentInvoiceService.buildForStudent(
                        payment.getTransactionRef(),
                        studentEmail);
                previews.add(new InvoicePreview(
                        payment.getTransactionRef(),
                        studentEmail,
                        invoiceDoc.getInvoiceNumber(),
                        invoiceDoc.getClassSection().getClassId(),
                        invoiceDoc.getClassSection().getClassName(),
                        StudentInvoiceService.formatMoney(invoiceDoc.getTotals().getGrandTotal())));
            } catch (RuntimeException ignored) {
                // Bỏ qua payment lỗi context, thử payment tiếp theo.
            }
        }
        return previews;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    public static final class DashboardData {
        private final int totalCourses;
        private final int totalClasses;
        private final int totalEnrollments;
        private final int totalRooms;
        private final int totalSchedules;
        private final int pendingCashApprovals;
        private final int totalAuditEvents;
        private final int totalNotifications;
        private final List<NotificationRow> latestNotifications;
        private final InvoicePreview latestInvoicePreview;
        private final List<InvoicePreview> latestInvoicePreviews;
        private final String enrollmentFacadeName;

        public DashboardData(int totalCourses, int totalClasses, int totalEnrollments, int totalRooms, int totalSchedules,
                int pendingCashApprovals, int totalAuditEvents, int totalNotifications,
                List<NotificationRow> latestNotifications, InvoicePreview latestInvoicePreview,
                List<InvoicePreview> latestInvoicePreviews, String enrollmentFacadeName) {
            this.totalCourses = totalCourses;
            this.totalClasses = totalClasses;
            this.totalEnrollments = totalEnrollments;
            this.totalRooms = totalRooms;
            this.totalSchedules = totalSchedules;
            this.pendingCashApprovals = pendingCashApprovals;
            this.totalAuditEvents = totalAuditEvents;
            this.totalNotifications = totalNotifications;
            this.latestNotifications = latestNotifications;
            this.latestInvoicePreview = latestInvoicePreview;
            this.latestInvoicePreviews = latestInvoicePreviews;
            this.enrollmentFacadeName = enrollmentFacadeName;
        }

        public int getTotalCourses() {
            return totalCourses;
        }

        public int getTotalClasses() {
            return totalClasses;
        }

        public int getTotalEnrollments() {
            return totalEnrollments;
        }

        public int getTotalRooms() {
            return totalRooms;
        }

        public int getTotalSchedules() {
            return totalSchedules;
        }

        public int getPendingCashApprovals() {
            return pendingCashApprovals;
        }

        public int getTotalAuditEvents() {
            return totalAuditEvents;
        }

        public int getTotalNotifications() {
            return totalNotifications;
        }

        public List<NotificationRow> getLatestNotifications() {
            return latestNotifications;
        }

        public InvoicePreview getLatestInvoicePreview() {
            return latestInvoicePreview;
        }

        public List<InvoicePreview> getLatestInvoicePreviews() {
            return latestInvoicePreviews;
        }

        public String getEnrollmentFacadeName() {
            return enrollmentFacadeName;
        }
    }

    public static final class NotificationRow {
        private final String notificationId;
        private final String relatedId;
        private final String content;
        private final LocalDateTime sendDate;

        public NotificationRow(String notificationId, String relatedId, String content, LocalDateTime sendDate) {
            this.notificationId = notificationId;
            this.relatedId = relatedId;
            this.content = content;
            this.sendDate = sendDate;
        }

        public String getNotificationId() {
            return notificationId;
        }

        public String getRelatedId() {
            return relatedId;
        }

        public String getContent() {
            return content;
        }

        public LocalDateTime getSendDate() {
            return sendDate;
        }
    }

    public static final class InvoicePreview {
        private final String transactionRef;
        private final String studentEmail;
        private final String invoiceNumber;
        private final String classId;
        private final String className;
        private final String totalAmountLabel;

        public InvoicePreview(String transactionRef, String studentEmail, String invoiceNumber,
                String classId, String className, String totalAmountLabel) {
            this.transactionRef = transactionRef;
            this.studentEmail = studentEmail;
            this.invoiceNumber = invoiceNumber;
            this.classId = classId;
            this.className = className;
            this.totalAmountLabel = totalAmountLabel;
        }

        public String getTransactionRef() {
            return transactionRef;
        }

        public String getStudentEmail() {
            return studentEmail;
        }

        public String getInvoiceNumber() {
            return invoiceNumber;
        }

        public String getClassId() {
            return classId;
        }

        public String getClassName() {
            return className;
        }

        public String getTotalAmountLabel() {
            return totalAmountLabel;
        }
    }
}
