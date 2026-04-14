<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Danh sách lớp học</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/vendor/bootstrap/css/bootstrap.min.css">
</head>
<body>
    <div class="container mt-5">
        <h2>Danh sách lớp học của bạn</h2>
        <c:if test="${param.msg == 'login_success'}">
            <div class="alert alert-success">Đăng nhập thành công. Chọn lớp bên dưới để điểm danh.</div>
        </c:if>
        <c:if test="${param.msg == 'attendance_saved'}">
            <div class="alert alert-success">Đã lưu điểm danh.</div>
        </c:if>
        <c:if test="${not empty message}">
            <div class="alert alert-success">${message}</div>
        </c:if>
        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>
        <c:choose>
            <c:when test="${empty classes}">
                <p class="text-muted">Bạn chưa được phân công lớp nào.</p>
            </c:when>
            <c:otherwise>
                <table class="table table-striped">
                    <thead>
                        <tr>
                            <th>Mã lớp</th>
                            <th>Tên lớp</th>
                            <th>Khóa học</th>
                            <th>Lịch dạy</th>
                            <th>Trạng thái</th>
                            <th>Hành động</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="clazz" items="${classes}">
                            <tr>
                                <td>${clazz.classID}</td>
                                <td>${clazz.className}</td>
                                <td>${clazz.course != null ? clazz.course.courseName : '—'}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${not empty clazz.schedule and not empty clazz.schedule.sessions}">
                                            <ul class="mb-0 ps-3">
                                                <c:forEach var="sess" items="${clazz.schedule.sessions}">
                                                    <li>
                                                        <strong>${sess.sessionDate}</strong>
                                                        <c:if test="${not empty sess.startTime}"> ${sess.startTime} - ${sess.endTime}</c:if>
                                                        <c:if test="${not empty sess.room}"> | ${sess.room.roomName}</c:if>
                                                    </li>
                                                </c:forEach>
                                            </ul>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="text-muted">Chưa có lịch</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>${clazz.status}</td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/teacher/attendance?classId=${clazz.classID}" class="btn btn-primary me-2">Điểm danh</a>
                                    <c:choose>
                                        <c:when test="${not empty clazz.schedule and not empty clazz.schedule.sessions}">
                                            <a href="${pageContext.request.contextPath}/teacher/attendance/report?classId=${clazz.classID}&amp;sessionId=${clazz.schedule.sessions[0].sessionID}" class="btn btn-outline-secondary">Xem báo cáo</a>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="btn btn-outline-secondary disabled">Chưa có báo cáo</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
        <a href="${pageContext.request.contextPath}/course" class="btn btn-secondary">Quay lại khóa học</a>
    </div>
</body>
</html>