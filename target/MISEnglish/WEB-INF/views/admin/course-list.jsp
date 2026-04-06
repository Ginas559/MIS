<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="vn.iotstar.coolenglish.entity.Course" %>
<%@ page import="vn.iotstar.coolenglish.entity.UserAccount" %>
<%@ page import="vn.iotstar.coolenglish.enums.UserRole" %>
<%
    List<Course> courses = (List<Course>) request.getAttribute("courses");
    UserAccount currentUser = (UserAccount) session.getAttribute("user");
    boolean managementView = Boolean.TRUE.equals(request.getAttribute("managementView"));
    boolean canManage = currentUser != null
            && (currentUser.getRole() == UserRole.ADMIN || currentUser.getRole() == UserRole.STAFF);
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Danh sach khoa hoc</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/vendor/bootstrap/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/app.css">
</head>
<body class="app-body">
<nav class="navbar app-navbar">
    <div class="container py-2">
        <a class="app-brand" href="${pageContext.request.contextPath}/index.jsp">CoolEnglish</a>
        <div class="d-flex gap-2">
            <% if (canManage) { %>
                <a class="btn btn-outline-sky" href="${pageContext.request.contextPath}/admin/course">Quan ly khoa hoc</a>
                <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/admin/room">Quan ly phong hoc</a>
                <a class="btn btn-outline-dark" href="${pageContext.request.contextPath}/admin/class">Quan ly lop hoc</a>
            <% } %>
            <form method="post" action="${pageContext.request.contextPath}/logout">
                <button type="submit" class="btn btn-outline-dark">Dang xuat</button>
            </form>
        </div>
    </div>
</nav>

<div class="container py-4">
    <div class="app-section list-card">
        <div class="d-flex justify-content-between align-items-center mb-3">
            <div>
                <h1 class="h4 app-title mb-1">Danh sach khoa hoc</h1>
                <p class="app-subtle mb-0">Nguoi dung dang nhap co the xem danh sach khoa hoc.</p>
            </div>
            <% if (canManage && !managementView) { %>
                <a class="btn btn-sky" href="${pageContext.request.contextPath}/admin/course">Mo trang quan ly</a>
            <% } %>
        </div>

        <% if (canManage && managementView) { %>
        <div class="mb-3 inline-actions">
            <a class="btn btn-sky btn-sm" href="${pageContext.request.contextPath}/admin/course/add">Them khoa hoc</a>
            <a class="btn btn-outline-secondary btn-sm" href="${pageContext.request.contextPath}/admin/room">Danh sach phong hoc</a>
            <a class="btn btn-outline-dark btn-sm" href="${pageContext.request.contextPath}/admin/class">Danh sach lop hoc</a>
        </div>

        <form method="post" action="${pageContext.request.contextPath}/admin/course/update-fee" class="row g-2 mb-3">
            <div class="col-md-3">
                <input type="text" class="form-control" name="courseID" placeholder="Ma khoa hoc" required>
            </div>
            <div class="col-md-3">
                <input type="number" step="0.01" class="form-control" name="newFee" placeholder="Hoc phi moi" required>
            </div>
            <div class="col-md-3">
                <button type="submit" class="btn btn-outline-dark">Cap nhat hoc phi</button>
            </div>
        </form>
        <% } %>

        <div class="table-responsive">
            <table class="table table-bordered align-middle">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Ten khoa hoc</th>
                        <th>Mo ta</th>
                        <th>Level</th>
                        <th>Duration</th>
                        <th>Fee</th>
                        <th>Status</th>
                        <% if (canManage && managementView) { %><th>Thao tac</th><% } %>
                    </tr>
                </thead>
                <tbody>
                    <%
                        if (courses != null && !courses.isEmpty()) {
                            for (Course course : courses) {
                    %>
                    <tr>
                        <td><%= course.getCourseID() %></td>
                        <td><%= course.getCourseName() %></td>
                        <td><%= course.getDescription() %></td>
                        <td><%= course.getLevel() %></td>
                        <td><%= course.getDuration() %></td>
                        <td><%= course.getFee() %></td>
                        <td><span class="status-pill"><%= course.getStatus() %></span></td>
                        <% if (canManage && managementView) { %>
                        <td>
                            <a class="btn btn-outline-secondary btn-sm" href="${pageContext.request.contextPath}/admin/course/update?id=<%= course.getCourseID() %>">Sua</a>
                            <% if (currentUser.getRole() == UserRole.ADMIN) { %>
                            <a class="btn btn-outline-dark btn-sm" href="${pageContext.request.contextPath}/admin/course/delete?id=<%= course.getCourseID() %>"
                               onclick="return confirm('Xoa khoa hoc nay?');">Xoa</a>
                            <% } %>
                        </td>
                        <% } %>
                    </tr>
                    <%
                            }
                        } else {
                    %>
                    <tr>
                        <td colspan="8" class="text-center app-subtle">Chua co du lieu khoa hoc.</td>
                    </tr>
                    <%
                        }
                    %>
                </tbody>
            </table>
        </div>
    </div>
</div>
<script src="${pageContext.request.contextPath}/assets/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
</body>
</html>
