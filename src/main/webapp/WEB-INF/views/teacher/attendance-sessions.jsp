<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Chọn buổi điểm danh</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/vendor/bootstrap/css/bootstrap.min.css">
</head>
<body>
    <div class="container mt-5">
        <h2>Chọn buổi học — ${englishClass.className}</h2>
        <p class="text-muted">Mã lớp: ${englishClass.classID}</p>

        <c:choose>
            <c:when test="${empty englishClass.schedule or empty englishClass.schedule.sessions}">
                <div class="alert alert-warning">Lớp chưa có lịch hoặc chưa có buổi học trong hệ thống.</div>
            </c:when>
            <c:otherwise>
                <table class="table table-striped">
                    <thead>
                        <tr>
                            <th>Buổi</th>
                            <th>Ngày</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="sess" items="${englishClass.schedule.sessions}">
                            <tr>
                                <td><c:out value="${sess.sessionName != null ? sess.sessionName : sess.sessionID}" /></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${sess.sessionDate != null}">
                                            <fmt:formatDate value="${sess.sessionDate}" pattern="dd/MM/yyyy" />
                                        </c:when>
                                        <c:otherwise>—</c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <a class="btn btn-primary btn-sm"
                                       href="${pageContext.request.contextPath}/teacher/attendance?classId=${englishClass.classID}&sessionId=${sess.sessionID}">
                                        Điểm danh buổi này
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>

        <a href="${pageContext.request.contextPath}/teacher/classes" class="btn btn-secondary mt-3">Quay lại danh sách lớp</a>
    </div>
</body>
</html>
