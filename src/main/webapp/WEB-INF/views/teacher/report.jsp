<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Báo cáo điểm danh</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/vendor/bootstrap/css/bootstrap.min.css">
</head>
<body>
    <div class="container mt-5">
        <h2>Báo cáo điểm danh</h2>
        <p class="mb-1">Lớp: <strong>${englishClass.className}</strong> (Mã lớp: ${englishClass.classID})</p>
        <p>Buổi: <strong>${session.sessionName}</strong> - <fmt:formatDate value="${session.sessionDate}" pattern="dd/MM/yyyy" /></p>

        <div class="alert alert-success">Điểm danh đã được lưu thành công. Dưới đây là báo cáo chi tiết.</div>

        <c:choose>
            <c:when test="${empty reportItems}">
                <div class="alert alert-warning">Chưa có thông tin điểm danh cho buổi này.</div>
            </c:when>
            <c:otherwise>
                <form method="post" action="${pageContext.request.contextPath}/teacher/attendance/mark">
                    <input type="hidden" name="classId" value="${englishClass.classID}">
                    <input type="hidden" name="sessionId" value="${session.sessionID}">
                    <table class="table table-bordered table-striped">
                        <thead>
                            <tr>
                                <th>Học sinh</th>
                                <th>Có mặt</th>
                                <th>Trạng thái</th>
                                <th>Giờ điểm danh</th>
                                <th>Ghi chú</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="item" items="${reportItems}">
                                <tr>
                                    <td>${item.studentName}</td>
                                    <td class="text-center">
                                        <input type="checkbox" name="present" value="${item.enrollmentId}" <c:if test="${item.present}">checked</c:if> />
                                    </td>
                                    <td>${item.status}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty item.checkinTime}">
                                                <fmt:formatDate value="${item.checkinTime}" pattern="dd/MM/yyyy HH:mm:ss" />
                                            </c:when>
                                            <c:otherwise>—</c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td><c:out value="${item.note}" default="—" /></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                    <div class="d-flex gap-2">
                        <button type="submit" class="btn btn-success">Lưu/ Cập nhật điểm danh</button>
                        <a class="btn btn-secondary" href="${pageContext.request.contextPath}/teacher/classes">Về danh sách lớp</a>
                    </div>
                </form>
            </c:otherwise>
        </c:choose>
    </div>
</body>
</html>
