<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>CoolEnglish - Xac thuc OTP</title>
</head>
<body>
<div class="container">
    <div class="app-section app-card">
        <h1 class="h4 app-title text-center mb-3">Xac thuc OTP</h1>

        <%
            String error = (String) request.getAttribute("error");
            String email = (String) request.getAttribute("email");
        %>

        <% if (error != null && !error.isEmpty()) { %>
            <div class="alert alert-danger"><%= error %></div>
        <% } %>

        <p class="app-subtle">
            Ma OTP da duoc gui den email:
            <strong><%= email != null ? email : "(khong co du lieu)" %></strong>
        </p>

        <form method="post" action="${pageContext.request.contextPath}/verify-otp" class="vstack gap-3">
            <div>
                <label for="otp" class="form-label">Nhap ma gom 6 chu so</label>
                <input id="otp" name="otp" type="text" maxlength="6" pattern="[0-9]{6}" required
                       class="form-control text-center" placeholder="123456">
            </div>
            <button class="btn btn-sky" type="submit">Xac nhan</button>
        </form>

        <div class="text-center mt-3">
            <a href="${pageContext.request.contextPath}/signup">Quay lai dang ky</a>
        </div>
    </div>
</div>
</body>
</html>

