<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="vn.iotstar.coolenglish.entity.Person" %>
<%@ page import="vn.iotstar.coolenglish.entity.UserAccount" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>CoolEnglish - Ho so ca nhan</title>
</head>
<body>
<%
    Person person = (Person) request.getAttribute("person");
    UserAccount user = (UserAccount) session.getAttribute("user");
    String error = (String) request.getAttribute("error");
    String msg = request.getParameter("msg");

    String fullNameValue = request.getParameter("fullName") != null
            ? request.getParameter("fullName")
            : (person != null && person.getFullName() != null ? person.getFullName() : "");
    String phoneValue = request.getParameter("phone") != null
            ? request.getParameter("phone")
            : (person != null && person.getPhone() != null ? person.getPhone() : "");
    String emailValue = user != null && user.getEmail() != null
            ? user.getEmail()
            : (person != null && person.getEmail() != null ? person.getEmail() : "");
    String genderValue = request.getParameter("gender") != null
            ? request.getParameter("gender")
            : (person != null && person.getGender() != null ? person.getGender().name() : "");
%>
<div class="container">
    <div class="app-section auth-card">
        <h1 class="h3 app-title text-center mb-4">Ho so ca nhan</h1>

        <% if (error != null && !error.isEmpty()) { %>
            <div class="alert alert-danger"><%= error %></div>
        <% } %>

        <% if ("updated".equals(msg)) { %>
            <div class="alert alert-success">Da cap nhat ho so thanh cong.</div>
        <% } %>

        <form method="post" action="${pageContext.request.contextPath}/profile" class="vstack gap-3">
            <div>
                <label for="fullName" class="form-label">Ho va ten</label>
                <input type="text" id="fullName" name="fullName" class="form-control" required value="<%= fullNameValue %>">
            </div>

            <div>
                <label for="email" class="form-label">Email dang nhap</label>
                <input type="email" id="email" class="form-control" value="<%= emailValue %>" readonly>
                <div class="form-text">Email dang nhap duoc dong bo tu tai khoan.</div>
            </div>

            <div>
                <label for="phone" class="form-label">So dien thoai</label>
                <input type="text" id="phone" name="phone" class="form-control" value="<%= phoneValue %>">
            </div>

            <div>
                <label for="gender" class="form-label">Gioi tinh</label>
                <select id="gender" name="gender" class="form-select">
                    <option value="" <%= genderValue.isEmpty() ? "selected" : "" %>>-- Chon gioi tinh --</option>
                    <option value="MALE" <%= "MALE".equals(genderValue) ? "selected" : "" %>>Nam</option>
                    <option value="FEMALE" <%= "FEMALE".equals(genderValue) ? "selected" : "" %>>Nu</option>
                    <option value="OTHER" <%= "OTHER".equals(genderValue) ? "selected" : "" %>>Khac</option>
                </select>
            </div>

            <div class="d-grid gap-2 d-md-flex">
                <button type="submit" class="btn btn-sky me-md-2">Cap nhat</button>
                <a href="${pageContext.request.contextPath}/course" class="btn btn-outline-secondary">Quay lai</a>
            </div>
        </form>
    </div>
</div>
</body>
</html>

