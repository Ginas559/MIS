<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.util.List" %>
<%@ page import="vn.iotstar.coolenglish.entity.UserAccount" %>
<%@ page import="vn.iotstar.coolenglish.enums.UserRole" %>
<%@ page import="vn.iotstar.coolenglish.facade.DashboardFacade.DashboardData" %>
<%@ page import="vn.iotstar.coolenglish.facade.DashboardFacade.InvoicePreview" %>
<%@ page import="vn.iotstar.coolenglish.facade.DashboardFacade.NotificationRow" %>
<%
    UserAccount currentUser = (UserAccount) request.getAttribute("currentUser");
    DashboardData dashboard = (DashboardData) request.getAttribute("dashboard");
    String message = (String) request.getAttribute("message");
    List<NotificationRow> notifications = dashboard == null ? java.util.List.of() : dashboard.getLatestNotifications();
    List<InvoicePreview> invoicePreviews = dashboard == null ? java.util.List.of() : dashboard.getLatestInvoicePreviews();
    InvoicePreview invoicePreview = dashboard == null ? null : dashboard.getLatestInvoicePreview();
    DateTimeFormatter dateTimeFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    boolean isAdmin = currentUser != null && currentUser.getRole() == UserRole.ADMIN;
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Bảng điều khiển vận hành - CoolEnglish</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/vendor/bootstrap/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/app.css">
</head>
<body class="app-body">
<nav class="navbar app-navbar">
    <div class="container py-2">
        <a class="app-brand" href="${pageContext.request.contextPath}/index.jsp">CoolEnglish</a>
        <div class="d-flex gap-2">
            <a class="btn btn-outline-sky" href="${pageContext.request.contextPath}/admin/dashboard">Bảng điều khiển</a>
            <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/profile">Hồ sơ cá nhân</a>
            <form method="post" action="${pageContext.request.contextPath}/logout" class="m-0">
                <button type="submit" class="btn btn-outline-dark">Đăng xuất</button>
            </form>
        </div>
    </div>
</nav>

<div class="container py-4">
    <div class="app-section list-card mb-4">
        <div class="d-flex justify-content-between align-items-center flex-wrap gap-2">
            <div>
                <h1 class="h4 app-title mb-1">Bảng điều khiển Admin/Staff</h1>
                <p class="app-subtle mb-0">
                    Xin chào <strong><%= currentUser == null ? "Người dùng" : currentUser.getUsername() %></strong>
                    (<%= currentUser == null || currentUser.getRole() == null ? "-" : currentUser.getRole() %>).
                </p>
            </div>
            <div class="d-flex gap-2 flex-wrap">
                <a class="btn btn-outline-dark btn-sm" href="${pageContext.request.contextPath}/admin/course">Khóa học</a>
                <a class="btn btn-outline-dark btn-sm" href="${pageContext.request.contextPath}/admin/class">Lớp học</a>
                <a class="btn btn-outline-dark btn-sm" href="${pageContext.request.contextPath}/admin/room">Phòng học</a>
                <a class="btn btn-outline-dark btn-sm" href="${pageContext.request.contextPath}/admin/schedule">Lịch học</a>
                <a class="btn btn-outline-dark btn-sm" href="${pageContext.request.contextPath}/admin/payment/cash">Tiền mặt</a>
                <a class="btn btn-outline-dark btn-sm" href="${pageContext.request.contextPath}/admin/integration/exam-results">Đồng bộ kết quả</a>
                <a class="btn btn-outline-dark btn-sm" href="${pageContext.request.contextPath}/admin/roadmap-grants">Cấp quyền roadmap</a>
                <a class="btn btn-outline-dark btn-sm" href="${pageContext.request.contextPath}/admin/roadmap-management">CRUD roadmap</a>
                <a class="btn btn-outline-dark btn-sm" href="${pageContext.request.contextPath}/admin/audit-logs">Audit logs</a>
            </div>
        </div>
    </div>
    <% if (message != null && !message.isBlank()) { %>
    <div class="alert alert-success"><%= message %></div>
    <% } %>

    <div class="row g-3 mb-4">
        <div class="col-md-3"><div class="app-section p-3 h-100"><div class="app-subtle">Tổng khóa học</div><div class="h3 mb-0"><%= dashboard == null ? 0 : dashboard.getTotalCourses() %></div></div></div>
        <div class="col-md-3"><div class="app-section p-3 h-100"><div class="app-subtle">Tổng lớp học</div><div class="h3 mb-0"><%= dashboard == null ? 0 : dashboard.getTotalClasses() %></div></div></div>
        <div class="col-md-3"><div class="app-section p-3 h-100"><div class="app-subtle">Tổng ghi danh</div><div class="h3 mb-0"><%= dashboard == null ? 0 : dashboard.getTotalEnrollments() %></div></div></div>
        <div class="col-md-3"><div class="app-section p-3 h-100"><div class="app-subtle">Tiền mặt chờ duyệt</div><div class="h3 mb-0"><%= dashboard == null ? 0 : dashboard.getPendingCashApprovals() %></div></div></div>
        <div class="col-md-3"><div class="app-section p-3 h-100"><div class="app-subtle">Phòng học</div><div class="h3 mb-0"><%= dashboard == null ? 0 : dashboard.getTotalRooms() %></div></div></div>
        <div class="col-md-3"><div class="app-section p-3 h-100"><div class="app-subtle">Lịch học</div><div class="h3 mb-0"><%= dashboard == null ? 0 : dashboard.getTotalSchedules() %></div></div></div>
        <div class="col-md-3"><div class="app-section p-3 h-100"><div class="app-subtle">Sự kiện audit</div><div class="h3 mb-0"><%= dashboard == null ? 0 : dashboard.getTotalAuditEvents() %></div></div></div>
        <div class="col-md-3"><div class="app-section p-3 h-100"><div class="app-subtle">Thông báo kết quả</div><div class="h3 mb-0"><%= dashboard == null ? 0 : dashboard.getTotalNotifications() %></div></div></div>
    </div>

    <div class="row g-4">
        <div class="col-lg-7">
            <div class="app-section list-card h-100">
                <h2 class="h5 app-title mb-3">Thông tin vận hành nổi bật</h2>
                <p class="app-subtle mb-3">
                    Dữ liệu được tổng hợp tự động từ các module quản lý lớp, thanh toán, audit và kết quả học tập.
                </p>
                <% if (invoicePreview != null) { %>
                <div class="mt-3 p-3 border rounded-3 bg-white">
                    <h3 class="h6 mb-2">Xem nhanh hóa đơn gần nhất</h3>
                    <div class="small">
                        <div><strong>Invoice:</strong> <%= invoicePreview.getInvoiceNumber() %></div>
                        <div><strong>Transaction:</strong> <%= invoicePreview.getTransactionRef() %></div>
                        <div><strong>Học viên:</strong> <%= invoicePreview.getStudentEmail() %></div>
                        <div><strong>Lớp:</strong> <%= invoicePreview.getClassId() %> - <%= invoicePreview.getClassName() %></div>
                        <div><strong>Tổng tiền:</strong> <%= invoicePreview.getTotalAmountLabel() %></div>
                    </div>
                </div>
                <% if (invoicePreviews != null && !invoicePreviews.isEmpty()) { %>
                <div class="mt-3">
                    <h3 class="h6 mb-2">5 hóa đơn hoàn tất gần nhất</h3>
                    <div class="table-responsive">
                        <table class="table table-bordered align-middle mb-0">
                            <thead>
                            <tr>
                                <th>Invoice</th>
                                <th>Transaction</th>
                                <th>Học viên</th>
                                <th>Lớp</th>
                                <th>Tổng tiền</th>
                            </tr>
                            </thead>
                            <tbody>
                            <% for (InvoicePreview row : invoicePreviews) { %>
                            <tr>
                                <td><%= row.getInvoiceNumber() %></td>
                                <td><%= row.getTransactionRef() %></td>
                                <td><%= row.getStudentEmail() %></td>
                                <td><%= row.getClassId() %> - <%= row.getClassName() %></td>
                                <td><%= row.getTotalAmountLabel() %></td>
                            </tr>
                            <% } %>
                            </tbody>
                        </table>
                    </div>
                </div>
                <% } %>
                <% } else { %>
                <div class="alert alert-warning mt-3 mb-0">
                    Chưa đủ dữ liệu để dựng xem nhanh hóa đơn (cần payment COMPLETED hợp lệ).
                </div>
                <% } %>
            </div>
        </div>
        <div class="col-lg-5">
            <div class="app-section list-card h-100">
                <h2 class="h5 app-title mb-3">Thông báo kết quả học tập mới nhất</h2>
                <div class="table-responsive">
                    <table class="table table-bordered align-middle mb-0">
                        <thead>
                        <tr>
                            <th>ID</th>
                            <th>Lop</th>
                            <th>Noi dung</th>
                            <th>Thoi gian</th>
                        </tr>
                        </thead>
                        <tbody>
                        <% if (notifications != null && !notifications.isEmpty()) {
                            for (NotificationRow row : notifications) { %>
                        <tr>
                            <td><%= row.getNotificationId() %></td>
                            <td><%= row.getRelatedId() == null ? "" : row.getRelatedId() %></td>
                            <td><%= row.getContent() == null ? "" : row.getContent() %></td>
                            <td><%= row.getSendDate() == null ? "" : dateTimeFmt.format(row.getSendDate()) %></td>
                        </tr>
                        <% }} else { %>
                        <tr><td colspan="4" class="text-center app-subtle">Chưa có thông báo nào.</td></tr>
                        <% } %>
                        </tbody>
                    </table>
                </div>
                <div class="mt-3 d-flex gap-2 flex-wrap">
                    <a class="btn btn-outline-secondary btn-sm" href="${pageContext.request.contextPath}/staff/results?mode=input">Nhập điểm</a>
                    <a class="btn btn-outline-secondary btn-sm" href="${pageContext.request.contextPath}/staff/results?mode=view">Bảng điểm tổng hợp</a>
                    <% if (isAdmin) { %>
                    <a class="btn btn-outline-secondary btn-sm" href="${pageContext.request.contextPath}/admin/audit-logs">Kiểm tra audit</a>
                    <% } %>
                </div>
            </div>
        </div>
    </div>
</div>
<script src="${pageContext.request.contextPath}/assets/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
</body>
</html>
