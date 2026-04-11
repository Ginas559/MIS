<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>CoolEnglish - Giới thiệu</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/vendor/bootstrap/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/app.css">
</head>
<body class="app-body">
<nav class="navbar app-navbar">
    <div class="container py-2">
        <a class="app-brand" href="${pageContext.request.contextPath}/index.jsp">CoolEnglish</a>
        <div class="d-flex gap-2">
            <c:choose>
                <c:when test="${not empty sessionScope.user}">
                    <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/course">Danh sách khóa học</a>
                    <a class="btn btn-outline-sky" href="${pageContext.request.contextPath}/roadmaps">Roadmap tu hoc</a>
                    <form method="post" action="${pageContext.request.contextPath}/logout">
                        <button type="submit" class="btn btn-outline-dark">Đăng xuất</button>
                    </form>
                </c:when>
                <c:otherwise>
                    <a class="btn btn-outline-sky" href="${pageContext.request.contextPath}/login">Đăng nhập</a>
                    <a class="btn btn-sky" href="${pageContext.request.contextPath}/signup">Đăng ký</a>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</nav>

<div class="container py-5">
    <section class="app-section p-4 p-md-5">
        <h1 class="app-title mb-3">Nền tảng quản lý học tiếng Anh</h1>
        <p class="app-subtle mb-4">
            Quan ly khoa hoc va lop hoc tren mot nen tang thong nhat. Roadmap tu hoc duoc dat o trang rieng de hoc vien truy cap de dang.
        </p>

        <div class="row g-4">
            <div class="col-md-4">
                <h2 class="h5 app-title">Khóa học</h2>
                <p class="app-subtle mb-0">Quan ly thong tin khoa hoc va hoc phi theo nhu cau trung tam.</p>
            </div>
            <div class="col-md-4">
                <h2 class="h5 app-title">Phòng học</h2>
                <p class="app-subtle mb-0">Theo doi suc chua, vi tri va trang thai su dung cua tung phong.</p>
            </div>
            <div class="col-md-4">
                <h2 class="h5 app-title">Roadmap tự học</h2>
                <p class="app-subtle mb-3">Trang rieng gom TOEIC RL, TOEIC SW va IELTS theo quyen truy cap.</p>
                <a class="btn btn-outline-sky" href="${pageContext.request.contextPath}/roadmaps">Mo trang roadmap</a>
            </div>
        </div>
    </section>
</div>

<script src="${pageContext.request.contextPath}/assets/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
</body>
</html>
