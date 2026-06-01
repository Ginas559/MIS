<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>CoolEnglish - Đăng Nhập</title>
</head>
<body>
<div class="container">
    <div class="app-section auth-card">
        <h1 class="h3 app-title text-center mb-4">Đăng Nhập</h1>

        <%
            String error = (String) request.getAttribute("error");
            String msg = request.getParameter("msg");
        %>

        <% if ("register_success".equals(msg)) { %>
            <div class="alert alert-success">Đăng ký thành công! Vui lòng đăng nhập.</div>
        <% } else if ("register_success_no_otp".equals(msg)) { %>
            <div class="alert alert-success">Đăng ký thành công (hệ thống đang tạm tắt OTP email). Vui lòng đăng nhập.</div>
        <% } else if ("logout_success".equals(msg)) { %>
            <div class="alert alert-success">Đăng xuất thành công!</div>
        <% } else if ("login_success".equals(msg)) { %>
            <div class="alert alert-success">Đăng nhập thành công!</div>
        <% } else if ("require_login".equals(msg)) { %>
            <div class="alert alert-warning">Vui lòng đăng nhập để truy cập trang này.</div>
        <% } else if ("forbidden".equals(msg)) { %>
            <div class="alert alert-danger">Tài khoản của bạn không có quyền truy cập khu vực quản trị.</div>
        <% } %>

        <% if (error != null && !error.isEmpty()) { %>
            <div class="alert alert-danger"><%= error %></div>
        <% } %>

        <form method="post" action="${pageContext.request.contextPath}/login" class="vstack gap-3">
            <div>
                <label for="email" class="form-label">Email</label>
                <input type="email" id="email" name="email" class="form-control" required
                       placeholder="Nhập email của bạn"
                       value="<%= request.getParameter("email") != null ? request.getParameter("email") : "" %>">
            </div>
            <div>
                <label for="password" class="form-label">Mật Khẩu</label>
                <input type="password" id="password" name="password" class="form-control" required
                       placeholder="Nhập mật khẩu">
            </div>
            <div class="remember-me">
                <input type="checkbox" id="remember" name="remember" value="true">
                <label for="remember">Ghi nhớ đăng nhập</label>
            </div>
            <div class="forgot-password">
                <a href="#">Quên mật khẩu?</a>
            </div>
            <button type="submit" class="btn btn-sky">Đăng Nhập</button>
        </form>

        <p class="app-subtle text-center mt-3 mb-0">
            Chưa có tài khoản?
            <a href="${pageContext.request.contextPath}/signup">Đăng ký ngay</a>
        </p>
    </div>
</div>
</body>
</html>

