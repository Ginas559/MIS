<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="vn.iotstar.coolenglish.entity.Schedule" %>
<%@ page import="vn.iotstar.coolenglish.entity.Session" %>
<%@ page import="vn.iotstar.coolenglish.entity.UserAccount" %>
<%@ page import="vn.iotstar.coolenglish.enums.UserRole" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%
    Schedule schedule = (Schedule) request.getAttribute("schedule");
    UserAccount currentUser = (UserAccount) session.getAttribute("user");
    boolean canManage = currentUser != null
            && (currentUser.getRole() == UserRole.ADMIN || currentUser.getRole() == UserRole.STAFF);
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chi Tiet Lich Hoc</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/vendor/bootstrap/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/app.css">
</head>
<body class="app-body">
<nav class="navbar app-navbar">
    <div class="container py-2">
        <a class="app-brand" href="${pageContext.request.contextPath}/index.jsp">CoolEnglish</a>
        <div class="d-flex gap-2">
            <% if (canManage) { %>
            <a class="btn btn-outline-sky" href="${pageContext.request.contextPath}/admin/dashboard">Bảng điều khiển</a>
            <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/admin/course">Quan ly khoa hoc</a>
            <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/admin/room">Quan ly phong hoc</a>
            <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/admin/class">Quan ly lop hoc</a>
            <a class="btn btn-outline-sky" href="${pageContext.request.contextPath}/admin/schedule">Quan ly lich hoc</a>
            <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/admin/integration/exam-results">Dong bo ket qua</a>
            <% } %>
            <form method="post" action="${pageContext.request.contextPath}/logout">
                <button type="submit" class="btn btn-outline-dark">Dang xuat</button>
            </form>
        </div>
    </div>
</nav>

<div class="container py-4">
    <div class="app-section list-card">
        <% if (schedule == null) { %>
        <div class="alert alert-warning">
            <h5>Khong tim thay lich hoc</h5>
            <p>Lich hoc ban tim kiem khong ton tai hoac da bi xoa.</p>
            <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-outline-secondary btn-sm">Bảng điều khiển</a>
            <a href="${pageContext.request.contextPath}/admin/schedule" class="btn btn-outline-secondary btn-sm">Quay ve danh sach</a>
        </div>
        <% } else { %>

        <!-- Header -->
        <div class="d-flex justify-content-between align-items-center mb-4">
            <div>
                <h1 class="h4 app-title mb-1">Chi tiet lich hoc</h1>
                <p class="app-subtle mb-0">Lich ID: <strong><%= schedule.getScheduleID() %></strong></p>
            </div>
            <div class="d-flex gap-2">
                <a class="btn btn-outline-secondary btn-sm" href="${pageContext.request.contextPath}/admin/dashboard">Bảng điều khiển</a>
                <a class="btn btn-outline-secondary btn-sm" href="${pageContext.request.contextPath}/admin/schedule">Quay ve</a>
                <% if (canManage) { %>
                <a class="btn btn-sky btn-sm" href="${pageContext.request.contextPath}/admin/schedule/add">Tao lich moi</a>
                <% } %>
            </div>
        </div>

        <!-- Schedule Info Section -->
        <div class="row mb-4">
            <div class="col-md-6">
                <div class="card border-0 shadow-sm">
                    <div class="card-body">
                        <h5 class="card-title mb-3">Thong tin lich hoc</h5>
                        <div class="mb-3">
                            <label class="form-label text-muted">Ma lich:</label>
                            <p class="mb-0"><strong><%= schedule.getScheduleID() %></strong></p>
                        </div>
                        <div class="mb-3">
                            <label class="form-label text-muted">Lop hoc (classID):</label>
                            <p class="mb-0"><strong><%= schedule.getEnglishClass() != null ? schedule.getEnglishClass().getClassID() : "---" %></strong>
                            <% if (schedule.getEnglishClass() != null && schedule.getEnglishClass().getClassName() != null) { %>
                            <span class="text-muted"> — <%= schedule.getEnglishClass().getClassName() %></span>
                            <% } %>
                            </p>
                        </div>
                        <div class="mb-3">
                            <label class="form-label text-muted">Ngay tao:</label>
                            <p class="mb-0"><%= schedule.getCreateDate() %></p>
                        </div>
                        <div class="mb-3">
                            <label class="form-label text-muted">So buoi hoc:</label>
                            <p class="mb-0"><span class="badge bg-info"><%= schedule.getTotalSessions() != null ? schedule.getTotalSessions() : 0 %> buoi</span></p>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-md-6">
                <div class="card border-0 shadow-sm">
                    <div class="card-body">
                        <h5 class="card-title mb-3">Mo ta</h5>
                        <p class="mb-0">
                            <% if (schedule.getDescription() != null && !schedule.getDescription().isEmpty()) { %>
                            <%= schedule.getDescription() %>
                            <% } else { %>
                            <span class="text-muted fst-italic">Chua co mo ta</span>
                            <% } %>
                        </p>
                    </div>
                </div>
            </div>
        </div>

        <!-- Sessions Table -->
        <div class="card border-0 shadow-sm">
            <div class="card-body">
                <h5 class="card-title mb-3">Danh sach cac buoi hoc</h5>

                <% List<Session> sessions = schedule.getSessions(); %>
                <% if (sessions == null || sessions.isEmpty()) { %>
                <div class="alert alert-info mb-0">
                    Chua co buoi hoc nao trong lich nay.
                </div>
                <% } else { %>
                <div class="table-responsive">
                    <table class="table table-bordered table-hover align-middle mb-0">
                        <thead class="table-light">
                            <tr>
                                <th>Ma buoi</th>
                                <th>Ten buoi</th>
                                <th>Ngay hoc</th>
                                <th>Gio bat dau</th>
                                <th>Gio ket thuc</th>
                                <th>Phong hoc</th>
                                <th>Giao vien</th>
                                <th>Trang thai</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="session" items="${schedule.sessions}">
                            <tr>
                                <td><strong><c:out value="${session.sessionID}" /></strong></td>
                                <td><c:out value="${session.sessionName}" default="---" /></td>
                                <td><c:out value="${session.sessionDate}" default="---" /></td>
                                <td><c:out value="${session.startTime}" default="---" /></td>
                                <td><c:out value="${session.endTime}" default="---" /></td>
                                <td><c:out value="${session.room != null ? session.room.roomName : '---'}" /></td>
                                <td><c:out value="${session.teacher != null ? session.teacher.fullName : '---'}" /></td>
                                <td>
                                    <c:if test="${session.status != null}">
                                        <span class="status-pill">
                                            <c:out value="${session.status.name()}" />
                                        </span>
                                    </c:if>
                                    <c:if test="${session.status == null}">
                                        <span class="status-pill">SCHEDULED</span>
                                    </c:if>
                                </td>
                            </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
                <% } %>
            </div>
        </div>

        <% } %>
    </div>
</div>

<script src="${pageContext.request.contextPath}/assets/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
</body>
</html>
