<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="vn.iotstar.coolenglish.entity.EnglishClass" %>
<%
    List<EnglishClass> classes = (List<EnglishClass>) request.getAttribute("classes");
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Quan ly lop hoc</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/vendor/bootstrap/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/app.css">
</head>
<body class="app-body">
<nav class="navbar app-navbar">
    <div class="container py-2">
        <a class="app-brand" href="${pageContext.request.contextPath}/index.jsp">CoolEnglish</a>
        <div class="d-flex gap-2">
            <a class="btn btn-outline-sky" href="${pageContext.request.contextPath}/admin/course">Quan ly khoa hoc</a>
            <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/admin/room">Quan ly phong hoc</a>
            <a class="btn btn-outline-dark" href="${pageContext.request.contextPath}/course">Danh sach khoa hoc</a>
        </div>
    </div>
</nav>

<div class="container py-4">
    <div class="app-section list-card">
        <div class="d-flex justify-content-between align-items-center mb-3">
            <h1 class="h4 app-title mb-0">Quan ly lop hoc</h1>
            <a class="btn btn-sky" href="${pageContext.request.contextPath}/admin/class/add">Them lop hoc</a>
        </div>

        <div class="table-responsive">
            <table class="table table-bordered align-middle">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Ten lop</th>
                    <th>CourseID</th>
                    <th>RoomID</th>
                    <th>Hoc vien</th>
                    <th>Trang thai</th>
                    <th>Ghi danh hoc vien</th>
                    <th>Thao tac</th>
                </tr>
                </thead>
                <tbody>
                <%
                    if (classes != null && !classes.isEmpty()) {
                        for (EnglishClass clazz : classes) {
                %>
                <tr>
                    <td><%= clazz.getClassID() %></td>
                    <td><%= clazz.getClassName() %></td>
                    <td><%= clazz.getCourseID() == null ? "" : clazz.getCourseID() %></td>
                    <td><%= clazz.getRoomID() == null ? "" : clazz.getRoomID() %></td>
                    <td><%= clazz.getCurrentEnrollment() == null ? 0 : clazz.getCurrentEnrollment() %> / <%= clazz.getMaxCapacity() == null ? "-" : clazz.getMaxCapacity() %></td>
                    <td><span class="status-pill"><%= clazz.getStatus() %></span></td>
                    <td>
                        <form method="post" action="${pageContext.request.contextPath}/admin/class/register" class="d-flex gap-2">
                            <input type="hidden" name="classID" value="<%= clazz.getClassID() %>">
                            <input type="email" name="studentEmail" class="form-control form-control-sm" placeholder="student@..." required>
                            <button type="submit" class="btn btn-outline-secondary btn-sm">Ghi danh</button>
                        </form>
                    </td>
                    <td>
                        <a class="btn btn-outline-secondary btn-sm" href="${pageContext.request.contextPath}/admin/class/update?id=<%= clazz.getClassID() %>">Sua</a>
                        <a class="btn btn-outline-dark btn-sm" href="${pageContext.request.contextPath}/admin/class/delete?id=<%= clazz.getClassID() %>"
                           onclick="return confirm('Xoa lop hoc nay?');">Xoa</a>
                    </td>
                </tr>
                <%
                        }
                    } else {
                %>
                <tr>
                    <td colspan="8" class="text-center app-subtle">Chua co du lieu lop hoc.</td>
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

