<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Roadmap tu hoc</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/vendor/bootstrap/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/app.css">
</head>
<body class="app-body">
<nav class="navbar app-navbar">
    <div class="container py-2">
        <a class="app-brand" href="${pageContext.request.contextPath}/index.jsp">CoolEnglish</a>
        <div class="d-flex gap-2">
            <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/course">Danh sach khoa hoc</a>
            <c:if test="${canManageGrants}">
                <a class="btn btn-outline-dark" href="${pageContext.request.contextPath}/admin/roadmap-grants">Cap quyen roadmap</a>
                <a class="btn btn-outline-dark" href="${pageContext.request.contextPath}/admin/roadmap-management">CRUD roadmap</a>
            </c:if>
            <form method="post" action="${pageContext.request.contextPath}/logout">
                <button type="submit" class="btn btn-outline-dark">Dang xuat</button>
            </form>
        </div>
    </div>
</nav>

<div class="container py-5">
    <section class="app-section p-4 p-md-5">
        <h1 class="app-title mb-3">Roadmap tu hoc</h1>
        <p class="app-subtle mb-4">Chon roadmap phu hop, he thong se tu dong kiem tra quyen truy cap cua ban.</p>

        <c:choose>
            <c:when test="${empty roadmaps}">
                <div class="alert alert-light border mb-0" role="status">Chưa có roadmap</div>
            </c:when>
            <c:otherwise>
                <div class="row g-4">
                    <c:forEach items="${roadmaps}" var="roadmap">
                        <div class="col-md-4">
                            <h2 class="h5 app-title"><c:out value="${roadmap.title}" /></h2>
                            <p class="app-subtle mb-3">
                                <c:out value="${empty roadmap.description ? 'Roadmap chua co mo ta.' : roadmap.description}" />
                            </p>
                            <a class="btn btn-outline-sky"
                               href="${pageContext.request.contextPath}/learning?roadmapCode=${roadmap.roadmapCode}">Vao roadmap</a>
                        </div>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>
    </section>
</div>

<script src="${pageContext.request.contextPath}/assets/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
</body>
</html>


