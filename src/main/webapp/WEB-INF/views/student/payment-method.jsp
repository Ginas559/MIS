<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="vn.iotstar.coolenglish.entity.Course" %>
<%@ page import="vn.iotstar.coolenglish.entity.EnglishClass" %>
<%
    Course course = (Course) request.getAttribute("course");
    EnglishClass classroom = (EnglishClass) request.getAttribute("classroom");
    String error = (String) request.getAttribute("error");
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Chon phuong thuc thanh toan</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/vendor/bootstrap/css/bootstrap.min.css">
</head>
<body class="container py-4">
    <h1 class="h4 mb-3">Chon phuong thuc thanh toan</h1>
    <% if (error != null) { %>
        <div class="alert alert-danger"><%= error %></div>
    <% } %>
    <% if (course != null && classroom != null) { %>
        <p><strong>Khoa hoc:</strong> <%= course.getCourseName() %> - <%= classroom.getClassName() %></p>
        <p><strong>Hoc phi:</strong> <%= course.getFee() %></p>
        <form method="post" action="${pageContext.request.contextPath}/student/payment/start">
            <input type="hidden" name="courseID" value="<%= course.getCourseID() %>">
            <div class="mb-3">
                <label class="form-label">Phuong thuc</label>
                <select class="form-select" name="method" required>
                    <option value="VNPAY">VNPay</option>
                    <option value="MB_BANK">MB Bank</option>
                    <option value="CASH">Tien mat</option>
                </select>
            </div>
            <a href="${pageContext.request.contextPath}/student/payment/detail?courseID=<%= course.getCourseID() %>"
               class="btn btn-outline-secondary">Quay lai</a>
            <button type="submit" class="btn btn-primary">Xac nhan thanh toan</button>
        </form>
    <% } else { %>
        <div class="alert alert-warning">Khong du thong tin de thanh toan.</div>
    <% } %>
</body>
</html>
