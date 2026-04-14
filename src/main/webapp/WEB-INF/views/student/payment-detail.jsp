<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="vn.iotstar.coolenglish.entity.Course" %>
<%@ page import="vn.iotstar.coolenglish.entity.EnglishClass" %>
<%
    Course course = (Course) request.getAttribute("course");
    EnglishClass classroom = (EnglishClass) request.getAttribute("classroom");
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Chi tiet lop hoc</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/vendor/bootstrap/css/bootstrap.min.css">
</head>
<body class="container py-4">
    <h1 class="h4 mb-3">Thong tin chi tiet lop hoc</h1>
    <% if (course == null) { %>
        <div class="alert alert-danger">Khong tim thay khoa hoc.</div>
    <% } else { %>
        <div class="card mb-3">
            <div class="card-body">
                <p><strong>Khoa hoc:</strong> <%= course.getCourseName() %> (<%= course.getCourseID() %>)</p>
                <p><strong>Hoc phi:</strong> <%= course.getFee() %></p>
                <p><strong>Mo ta:</strong> <%= course.getDescription() %></p>
                <% if (classroom != null) { %>
                    <p><strong>Lop:</strong> <%= classroom.getClassName() %> - <%= classroom.getClassID() %></p>
                    <p><strong>Giao vien:</strong> <%= classroom.getTeacherName() == null ? "--" : classroom.getTeacherName() %></p>
                    <p><strong>Ngay bat dau:</strong> <%= classroom.getStartDate() == null ? "--" : classroom.getStartDate() %></p>
                    <p><strong>Ngay ket thuc:</strong> <%= classroom.getEndDate() == null ? "--" : classroom.getEndDate() %></p>
                    <p><strong>Si so:</strong> <%= classroom.getCurrentEnrollment() %>/<%= classroom.getMaxCapacity() %></p>
                <% } else { %>
                    <div class="alert alert-warning mb-0">Hien chua co lop OPEN cho khoa hoc nay.</div>
                <% } %>
            </div>
        </div>

        <a href="${pageContext.request.contextPath}/course" class="btn btn-outline-secondary">Quay lai</a>
        <% if (classroom != null) { %>
            <a href="${pageContext.request.contextPath}/student/payment/method?courseID=<%= course.getCourseID() %>"
               class="btn btn-primary">Thanh toan</a>
        <% } %>
    <% } %>
</body>
</html>
