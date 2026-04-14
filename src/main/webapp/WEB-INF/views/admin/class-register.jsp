<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="vn.iotstar.coolenglish.entity.EnglishClass" %>
<%@ page import="vn.iotstar.coolenglish.enums.ClassStatus" %>
<%
    EnglishClass classroom = (EnglishClass) request.getAttribute("classroom");
    String registerError = (String) request.getAttribute("registerError");
    String registerSuccess = (String) request.getAttribute("registerSuccess");
    String studentEmail = (String) request.getAttribute("studentEmail");

    if (studentEmail == null || studentEmail.isBlank()) {
        studentEmail = "";
    }

    boolean canEnroll = classroom != null && classroom.getStatus() == ClassStatus.OPEN;
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Ghi danh hoc vien</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/vendor/bootstrap/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/app.css">
</head>
<body class="app-body">
<div class="container py-4">
    <div class="app-section app-card">
        <h1 class="h4 app-title mb-3">Ghi danh hoc vien</h1>

        <% if (registerSuccess != null && !registerSuccess.isBlank()) { %>
        <div class="alert alert-success" role="alert"><%= registerSuccess %></div>
        <% } %>

        <% if (registerError != null && !registerError.isBlank()) { %>
        <div class="alert alert-danger" role="alert"><%= registerError %></div>
        <% } %>

        <% if (classroom == null) { %>
        <div class="alert alert-warning" role="alert">Khong tim thay lop hoc.</div>
        <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/admin/class">Quay lai</a>
        <% } else { %>
        <div class="mb-3">
            <div><strong>Ma lop:</strong> <%= classroom.getClassID() %></div>
            <div><strong>Ten lop:</strong> <%= classroom.getClassName() == null ? "" : classroom.getClassName() %></div>
            <div><strong>Giao vien:</strong> <%= classroom.getTeacherName() == null ? "--" : classroom.getTeacherName() %></div>
            <div><strong>Ngay bat dau:</strong> <%= classroom.getStartDate() == null ? "--" : classroom.getStartDate() %></div>
            <div><strong>Ngay ket thuc:</strong> <%= classroom.getEndDate() == null ? "--" : classroom.getEndDate() %></div>
            <div><strong>Trang thai:</strong> <%= classroom.getStatus() %></div>
            <div><strong>Hoc vien:</strong> <%= classroom.getCurrentEnrollment() == null ? 0 : classroom.getCurrentEnrollment() %> / <%= classroom.getMaxCapacity() == null ? "-" : classroom.getMaxCapacity() %></div>
        </div>

        <form method="post" action="${pageContext.request.contextPath}/admin/class/register" class="vstack gap-3">
            <input type="hidden" name="classID" value="<%= classroom.getClassID() %>">

            <div>
                <label class="form-label">Email hoc vien</label>
                <input type="email" class="form-control" name="studentEmail" value="<%= studentEmail %>" placeholder="student@example.com" required>
            </div>

            <% if (!canEnroll) { %>
            <div class="alert alert-info mb-0" role="alert">
                Lop hien khong o trang thai OPEN, he thong se tu choi ghi danh theo State Pattern.
            </div>
            <% } %>

            <div class="d-flex gap-2">
                <button type="submit" class="btn btn-sky" <%= canEnroll ? "" : "disabled" %>>Ghi danh</button>
                <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/admin/class">Quay lai danh sach</a>
            </div>
        </form>
        <% } %>
    </div>
</div>
<script src="${pageContext.request.contextPath}/assets/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
</body>
</html>

