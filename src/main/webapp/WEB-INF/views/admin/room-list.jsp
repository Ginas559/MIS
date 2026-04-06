<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="vn.iotstar.coolenglish.entity.Room" %>
<%@ page import="vn.iotstar.coolenglish.entity.UserAccount" %>
<%@ page import="vn.iotstar.coolenglish.enums.UserRole" %>
<%
    List<Room> rooms = (List<Room>) request.getAttribute("rooms");
    UserAccount currentUser = (UserAccount) session.getAttribute("user");
    boolean canManage = currentUser != null
            && (currentUser.getRole() == UserRole.ADMIN || currentUser.getRole() == UserRole.STAFF);
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Danh sach phong hoc</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/vendor/bootstrap/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/app.css">
</head>
<body class="app-body">
<nav class="navbar app-navbar">
    <div class="container py-2">
        <a class="app-brand" href="${pageContext.request.contextPath}/index.jsp">CoolEnglish</a>
        <div class="d-flex gap-2">
            <a class="btn btn-outline-sky" href="${pageContext.request.contextPath}/admin/course">Quan ly khoa hoc</a>
            <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/course">Danh sach khoa hoc</a>
            <a class="btn btn-outline-dark" href="${pageContext.request.contextPath}/admin/class">Quan ly lop hoc</a>
        </div>
    </div>
</nav>

<div class="container py-4">
    <div class="app-section list-card">
        <div class="d-flex justify-content-between align-items-center mb-3">
            <h1 class="h4 app-title mb-0">Quan ly phong hoc</h1>
            <% if (canManage) { %>
                <a class="btn btn-sky" href="${pageContext.request.contextPath}/admin/room/add">Them phong hoc</a>
            <% } %>
        </div>

        <div class="table-responsive">
            <table class="table table-bordered align-middle">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Ten phong</th>
                        <th>Suc chua</th>
                        <th>Vi tri</th>
                        <th>Trang thai</th>
                        <th>Thao tac</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        if (rooms != null && !rooms.isEmpty()) {
                            for (Room room : rooms) {
                    %>
                    <tr>
                        <td><%= room.getRoomID() %></td>
                        <td><%= room.getRoomName() %></td>
                        <td><%= room.getCapacity() %></td>
                        <td><%= room.getLocation() %></td>
                        <td><span class="status-pill"><%= room.getStatus() %></span></td>
                        <td>
                            <a class="btn btn-outline-secondary btn-sm" href="${pageContext.request.contextPath}/admin/room/update?id=<%= room.getRoomID() %>">Sua</a>
                            <a class="btn btn-outline-dark btn-sm" href="${pageContext.request.contextPath}/admin/room/delete?id=<%= room.getRoomID() %>"
                               onclick="return confirm('Xoa phong hoc nay?');">Xoa</a>
                        </td>
                    </tr>
                    <%
                            }
                        } else {
                    %>
                    <tr>
                        <td colspan="6" class="text-center app-subtle">Chua co du lieu phong hoc.</td>
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
