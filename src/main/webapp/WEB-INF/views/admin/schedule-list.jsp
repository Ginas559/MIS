<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="vn.iotstar.coolenglish.entity.Schedule" %>
<%@ page import="vn.iotstar.coolenglish.entity.UserAccount" %>
<%@ page import="vn.iotstar.coolenglish.enums.UserRole" %>
<%
    Schedule schedule = (Schedule) request.getAttribute("schedule");
    UserAccount currentUser = (UserAccount) session.getAttribute("user");
    boolean canManage = currentUser != null
            && (currentUser.getRole() == UserRole.ADMIN || currentUser.getRole() == UserRole.STAFF);
    Integer currentPage = (Integer) request.getAttribute("currentPage");
    Integer totalPages = (Integer) request.getAttribute("totalPages");
    Integer totalItems = (Integer) request.getAttribute("totalItems");
    String paginationPath = (String) request.getAttribute("paginationPath");
    
    if (currentPage == null) currentPage = 1;
    if (totalPages == null) totalPages = 1;
    if (totalItems == null) totalItems = 0;
    if (paginationPath == null || paginationPath.isBlank()) paginationPath = "/admin/schedule";
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Danh sach lich hoc</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/vendor/bootstrap/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/app.css">
</head>
<body class="app-body">
<nav class="navbar app-navbar">
    <div class="container py-2">
        <a class="app-brand" href="${pageContext.request.contextPath}/index.jsp">CoolEnglish</a>
        <div class="d-flex gap-2">
            <% if (canManage) { %>
            <a class="btn btn-outline-sky" href="${pageContext.request.contextPath}/admin/dashboard">Bảng điều khiển</a>
            <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/admin/course">Quan ly khoa hoc</a>
            <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/admin/room">Quan ly phong hoc</a>
            <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/admin/class">Quan ly lop hoc</a>
            <a class="btn btn-outline-sky" href="${pageContext.request.contextPath}/admin/schedule">Quan ly lich hoc</a>
            <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/admin/integration/exam-results">Dong bo ket qua</a>
            <% } %>
            <form method="post" action="${pageContext.request.contextPath}/logout">
                <button type="submit" class="btn btn-outline-dark">Dang xuat</button>
            </form>
        </div>
    </div>
</nav>

<div class="container py-4">
    <div class="app-section list-card">
        <!-- Header -->
        <div class="d-flex justify-content-between align-items-center mb-4">
            <div>
                <h1 class="h4 app-title mb-1">Danh sach lich hoc</h1>
                <p class="app-subtle mb-0">Quan ly va xem chi tiet cac lich hoc da tao.</p>
            </div>
            <% if (canManage) { %>
            <a class="btn btn-sky btn-sm" href="${pageContext.request.contextPath}/admin/schedule/add">Tao lich moi</a>
            <% } %>
        </div>

        <% String error = (String) request.getAttribute("error"); %>
        <% String success = (String) request.getAttribute("success"); %>

        <% if (error != null && !error.isEmpty()) { %>
        <div class="alert alert-danger">⚠️ <%= error %></div>
        <% } %>

        <% if (success != null && !success.isEmpty()) { %>
        <div class="alert alert-success">✓ <%= success %></div>
        <% } %>

        <% List<Schedule> schedules = (List<Schedule>) request.getAttribute("schedules"); %>

        <% if (schedules == null || schedules.isEmpty()) { %>
        <div class="alert alert-info text-center py-5">
            <p class="mb-2">Chua co lich hoc nao trong he thong.</p>
            <% if (canManage) { %>
            <a href="${pageContext.request.contextPath}/admin/schedule/add" class="btn btn-sky btn-sm">Tao lich moi</a>
            <% } %>
        </div>
        <% } else { %>
        <div class="table-responsive">
            <table class="table table-bordered align-middle">
                <thead class="table-light">
                    <tr>
                        <th>Ma lich</th>
                        <th>Lop hoc</th>
                        <th>Mo ta</th>
                        <th>So buoi</th>
                        <th>Ngay tao</th>
                        <% if (canManage) { %><th>Thao tac</th><% } %>
                    </tr>
                </thead>
                <tbody>
                    <% for (Schedule sch : schedules) { %>
                    <tr>
                        <td><strong><%= sch.getScheduleID() %></strong></td>
                        <td><%= sch.getEnglishClass() != null ? sch.getEnglishClass().getClassID() : "---" %></td>
                        <td><%= sch.getDescription() != null ? sch.getDescription() : "---" %></td>
                        <td><span class="badge bg-info"><%= sch.getTotalSessions() != null ? sch.getTotalSessions() : 0 %> buoi</span></td>
                        <td><%= sch.getCreateDate() %></td>
                        <% if (canManage) { %>
                        <td>
                            <a href="${pageContext.request.contextPath}/admin/schedule/view?scheduleID=<%= sch.getScheduleID() %>" class="btn btn-outline-secondary btn-sm">Xem</a>
                            <a href="${pageContext.request.contextPath}/admin/schedule/edit?scheduleID=<%= sch.getScheduleID() %>" class="btn btn-outline-warning btn-sm">Sua</a>
                            <button type="button" class="btn btn-outline-danger btn-sm" onclick="confirmDelete('<%= sch.getScheduleID() %>')">Xoa</button>
                        </td>
                        <% } %>
                    </tr>
                    <% } %>
                </tbody>
            </table>
        </div>

        <% if (totalPages > 1) { %>
        <nav aria-label="Schedule pagination" class="mt-3">
            <ul class="pagination mb-0">
                <li class="page-item <%= currentPage <= 1 ? "disabled" : "" %>">
                    <a class="page-link" href="${pageContext.request.contextPath}<%= paginationPath %>?page=<%= currentPage - 1 %>">Truoc</a>
                </li>
                <% for (int pageNumber = 1; pageNumber <= totalPages; pageNumber++) { %>
                <li class="page-item <%= pageNumber == currentPage ? "active" : "" %>">
                    <a class="page-link" href="${pageContext.request.contextPath}<%= paginationPath %>?page=<%= pageNumber %>"><%= pageNumber %></a>
                </li>
                <% } %>
                <li class="page-item <%= currentPage >= totalPages ? "disabled" : "" %>">
                    <a class="page-link" href="${pageContext.request.contextPath}<%= paginationPath %>?page=<%= currentPage + 1 %>">Sau</a>
                </li>
            </ul>
        </nav>
        <p class="app-subtle mb-0 mt-2">Tong: <%= totalItems %> ban ghi (30 dong/trang).</p>
        <% } %>
        <% } %>
    </div>
</div>

<script src="${pageContext.request.contextPath}/assets/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
<script>
    function confirmDelete(scheduleID) {
        if (confirm('Ban chac chan muon xoa lich hoc nay khong?')) {
            window.location.href = '${pageContext.request.contextPath}/admin/schedule/delete?scheduleID=' + scheduleID;
        }
    }
</script>
</body>
</html>
