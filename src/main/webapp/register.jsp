<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>CoolEnglish - Đăng Ký</title>
</head>
<body>
<div class="container">
    <div class="app-section auth-card">
        <h1 class="h3 app-title text-center mb-4">Đăng ký tài khoản</h1>

        <%
            String error = (String) request.getAttribute("error");
            String msg = request.getParameter("msg");
        %>

        <% if (error != null && !error.isEmpty()) { %>
            <div class="alert alert-danger"><%= error %></div>
        <% } %>

        <% if ("otp_session_expired".equals(msg) || "otp_expired".equals(msg)) { %>
            <div class="alert alert-warning">Phiên xác thực OTP đã hết hạn. Vui lòng đăng ký lại.</div>
        <% } else if ("email_exists".equals(msg)) { %>
            <div class="alert alert-danger">Email đã được đăng ký trước đó.</div>
        <% } else if ("invalid_role".equals(msg)) { %>
            <div class="alert alert-danger">Vai trò đăng ký không hợp lệ. Vui lòng thử lại.</div>
        <% } %>

        <form method="post" action="${pageContext.request.contextPath}/signup" class="vstack gap-3">
            <div>
                <label for="name" class="form-label">Họ và tên</label>
                <input type="text" id="name" name="name" class="form-control" required
                       value="<%= request.getParameter("name") != null ? request.getParameter("name") : "" %>">
            </div>

            <div>
                <label for="email" class="form-label">Email</label>
                <input type="email" id="email" name="email" class="form-control" required
                       value="<%= request.getParameter("email") != null ? request.getParameter("email") : "" %>">
            </div>

            <div>
                <label for="password" class="form-label">Mật khẩu</label>
                <input type="password" id="password" name="password" class="form-control" required>
                <div class="form-text">Mật khẩu phải dài ít nhất 6 ký tự.</div>
            </div>

            <div>
                <label for="confirmPassword" class="form-label">Xác nhận mật khẩu</label>
                <input type="password" id="confirmPassword" name="confirmPassword" class="form-control" required>
            </div>

            <div>
                <label class="form-label d-block">Vai trò</label>
                <div class="form-check form-check-inline">
                    <input class="form-check-input" type="radio" id="student" name="role" value="STUDENT"
                           <% if ("STUDENT".equals(request.getParameter("role")) || request.getParameter("role") == null) { %>checked<% } %> required>
                    <label class="form-check-label" for="student">Học sinh</label>
                </div>
                <div class="form-check form-check-inline">
                    <input class="form-check-input" type="radio" id="teacher" name="role" value="TEACHER"
                           <% if ("TEACHER".equals(request.getParameter("role"))) { %>checked<% } %> required>
                    <label class="form-check-label" for="teacher">Giáo viên</label>
                </div>
                <div class="form-check form-check-inline">
                    <input class="form-check-input" type="radio" id="staff" name="role" value="STAFF"
                           <% if ("STAFF".equals(request.getParameter("role"))) { %>checked<% } %> required>
                    <label class="form-check-label" for="staff">Nhân viên</label>
                </div>
            </div>

            <div id="secret-code-group">
                <label for="secretCode" class="form-label">Mã bí mật (chỉ cho Giáo viên/Nhân viên)</label>
                <input type="password" id="secretCode" name="secretCode" class="form-control"
                       value="<%= request.getParameter("secretCode") != null ? request.getParameter("secretCode") : "" %>">
            </div>

            <div class="d-grid gap-2 d-md-flex">
                <button type="submit" class="btn btn-sky me-md-2">Đăng ký</button>
                <button type="reset" class="btn btn-outline-secondary">Xóa form</button>
            </div>
        </form>

        <p class="app-subtle text-center mt-3 mb-0">
            Đã có tài khoản?
            <a href="${pageContext.request.contextPath}/login">Đăng nhập</a>
        </p>
    </div>
</div>
<script>
(function () {
    const roleInputs = document.querySelectorAll('input[name="role"]');
    const secretCodeGroup = document.getElementById('secret-code-group');
    const secretCodeInput = document.getElementById('secretCode');

    function toggleSecretCode() {
        const selectedRole = document.querySelector('input[name="role"]:checked');
        const needsSecretCode = selectedRole && selectedRole.value !== 'STUDENT';

        secretCodeGroup.style.display = needsSecretCode ? 'block' : 'none';
        secretCodeInput.required = needsSecretCode;

        if (!needsSecretCode) {
            secretCodeInput.value = '';
        }
    }

    roleInputs.forEach(function (input) {
        input.addEventListener('change', toggleSecretCode);
    });

    toggleSecretCode();
})();
</script>
</body>
</html>
