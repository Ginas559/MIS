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
            CoolEnglish giúp quản lý khóa học và phòng học gọn gàng, dễ sử dụng cho đội ngũ vận hành.
            Sau khi đăng nhập, bạn sẽ vào trang danh sách khóa học.
        </p>

        <div class="row g-4">
            <div class="col-md-4">
                <h2 class="h5 app-title">Khóa học</h2>
                <p class="app-subtle mb-0">Quản lý thông tin khóa học và học phí theo nhu cầu trung tâm.</p>
            </div>
            <div class="col-md-4">
                <h2 class="h5 app-title">Phòng học</h2>
                <p class="app-subtle mb-0">Theo dõi sức chứa, vị trí và trạng thái sử dụng của từng phòng.</p>
            </div>
            <div class="col-md-4">
                <h2 class="h5 app-title">Phân quyền</h2>
                <p class="app-subtle mb-0">Người dùng STAFF và ADMIN có khu vực quản lý riêng cho chức năng thay đổi dữ liệu.</p>
            </div>
        </div>
    </section>
</div>

<script src="${pageContext.request.contextPath}/assets/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
</body>
</html>
