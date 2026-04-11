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

        <div class="row g-4">
            <div class="col-md-4">
                <h2 class="h5 app-title">TOEIC 2 ky nang RL</h2>
                <p class="app-subtle mb-3">Lo trinh Reading + Listening voi bai hoc nen tang va luyen de.</p>
                <a class="btn btn-outline-sky" href="${pageContext.request.contextPath}/learning?roadmapCode=TOEIC_RL">Vao roadmap</a>
            </div>
            <div class="col-md-4">
                <h2 class="h5 app-title">TOEIC 2 ky nang SW</h2>
                <p class="app-subtle mb-3">Lo trinh Speaking + Writing theo tinh huong giao tiep thuc te.</p>
                <a class="btn btn-outline-sky" href="${pageContext.request.contextPath}/learning?roadmapCode=TOEIC_SW">Vao roadmap</a>
            </div>
            <div class="col-md-4">
                <h2 class="h5 app-title">IELTS</h2>
                <p class="app-subtle mb-3">Lo trinh IELTS 4 ky nang ket hop noi dung noi bo va doi tac.</p>
                <a class="btn btn-outline-sky" href="${pageContext.request.contextPath}/learning?roadmapCode=IELTS">Vao roadmap</a>
            </div>
        </div>
    </section>
</div>

<script src="${pageContext.request.contextPath}/assets/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
</body>
</html>


