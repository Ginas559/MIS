<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Điểm danh lớp học</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/vendor/bootstrap/css/bootstrap.min.css">
</head>
<body>
    <div class="container mt-5">
        <h2>Điểm danh cho lớp: ${englishClass.className} - Buổi: ${session.sessionName}</h2>
        <form action="${pageContext.request.contextPath}/teacher/attendance/mark" method="post">
            <input type="hidden" name="sessionId" value="${session.sessionID}">
            <input type="hidden" name="classId" value="${englishClass.classID}">
            <table class="table table-striped">
                <thead>
                    <tr>
                        <th>Học sinh</th>
                        <th>Có mặt</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="enrollment" items="${enrollments}">
                        <tr>
                            <td>${enrollment.student.fullName}</td>
                            <td>
                                <input type="checkbox" name="present" value="${enrollment.id}">
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
            <button type="submit" class="btn btn-success">Lưu điểm danh</button>
        </form>
        <a href="${pageContext.request.contextPath}/teacher/classes" class="btn btn-secondary">Quay lại</a>
    </div>
</body>
</html>