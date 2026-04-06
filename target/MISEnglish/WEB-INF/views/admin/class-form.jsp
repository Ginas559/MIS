<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="vn.iotstar.coolenglish.entity.EnglishClass" %>
<%@ page import="vn.iotstar.coolenglish.enums.ClassStatus" %>
<%
    EnglishClass classroom = (EnglishClass) request.getAttribute("classroom");
    boolean editing = classroom != null && classroom.getClassID() != null && !classroom.getClassID().isBlank();
    String actionUrl = editing ? request.getContextPath() + "/admin/class/update" : request.getContextPath() + "/admin/class/add";
    ClassStatus selectedStatus = classroom == null || classroom.getStatus() == null ? ClassStatus.OPEN : classroom.getStatus();
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title><%= editing ? "Sua lop hoc" : "Them lop hoc" %></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/vendor/bootstrap/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/app.css">
</head>
<body class="app-body">
<div class="container py-4">
    <div class="app-section app-card">
        <h1 class="h4 app-title mb-3"><%= editing ? "Sua lop hoc" : "Them lop hoc" %></h1>
        <form method="post" action="<%= actionUrl %>" class="vstack gap-3">
            <div>
                <label class="form-label">Ma lop hoc</label>
                <input type="text" class="form-control" name="classID"
                       value="<%= classroom == null || classroom.getClassID() == null ? "" : classroom.getClassID() %>" <%= editing ? "readonly" : "" %> required>
            </div>

            <div>
                <label class="form-label">Ten lop hoc</label>
                <input type="text" class="form-control" name="className"
                       value="<%= classroom == null || classroom.getClassName() == null ? "" : classroom.getClassName() %>" required>
            </div>

            <div>
                <label class="form-label">Ma khoa hoc</label>
                <input type="text" class="form-control" name="courseID"
                       value="<%= classroom == null || classroom.getCourseID() == null ? "" : classroom.getCourseID() %>">
            </div>

            <div>
                <label class="form-label">Ma phong hoc</label>
                <input type="text" class="form-control" name="roomID"
                       value="<%= classroom == null || classroom.getRoomID() == null ? "" : classroom.getRoomID() %>">
            </div>

            <div>
                <label class="form-label">Suc chua toi da</label>
                <input type="number" class="form-control" name="maxCapacity"
                       value="<%= classroom == null || classroom.getMaxCapacity() == null ? "" : classroom.getMaxCapacity() %>" min="1">
            </div>

            <div>
                <label class="form-label">So hoc vien hien tai</label>
                <input type="number" class="form-control" name="currentEnrollment"
                       value="<%= classroom == null || classroom.getCurrentEnrollment() == null ? "0" : classroom.getCurrentEnrollment() %>" min="0">
            </div>

            <div>
                <label class="form-label">Trang thai</label>
                <select class="form-select" name="status" required>
                    <% for (ClassStatus status : ClassStatus.values()) { %>
                    <option value="<%= status.name() %>" <%= status == selectedStatus ? "selected" : "" %>><%= status.name() %></option>
                    <% } %>
                </select>
            </div>

            <div class="d-flex gap-2">
                <button type="submit" class="btn btn-sky"><%= editing ? "Cap nhat" : "Them moi" %></button>
                <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/admin/class">Quay lai</a>
            </div>
        </form>
    </div>
</div>
<script src="${pageContext.request.contextPath}/assets/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
</body>
</html>

