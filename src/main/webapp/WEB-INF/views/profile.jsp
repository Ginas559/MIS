<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="vn.iotstar.coolenglish.entity.Person" %>
<%@ page import="vn.iotstar.coolenglish.entity.UserAccount" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>CoolEnglish Academy - Hồ sơ</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <style>
        :root {
            --primary-color: #007bff; /* Màu xanh Academy */
            --bg-color: #f0f2f5;
            --text-main: #2d3436;
        }
        body {
            font-family: 'Inter', sans-serif;
            background-color: var(--bg-color);
            color: var(--text-main);
            margin: 0; padding: 40px 0;
        }
        .container { max-width: 900px; margin: auto; padding: 0 20px; }

        .profile-wrapper {
            background: #fff;
            border-radius: 20px;
            overflow: hidden;
            box-shadow: 0 10px 30px rgba(0,0,0,0.08);
            display: flex;
            flex-wrap: wrap;
        }

        /* Phần bên trái: Avatar */
        .sidebar-profile {
            background: linear-gradient(135deg, #007bff 0%, #0056b3 100%);
            color: #fff;
            padding: 40px 30px;
            width: 35%;
            text-align: center;
            display: flex; flex-direction: column; align-items: center;
        }
        .avatar-container { position: relative; margin-bottom: 20px; }
        .avatar-preview {
            width: 150px; height: 150px;
            border-radius: 50%;
            border: 5px solid rgba(255,255,255,0.2);
            object-fit: cover;
            background: #fff;
        }
        .avatar-placeholder {
            width: 150px; height: 150px; border-radius: 50%;
            background: rgba(255,255,255,0.1);
            display: flex; align-items: center; justify-content: center;
            border: 2px dashed #fff;
        }

        /* Phần bên phải: Form */
        .main-content { padding: 40px; width: 65%; box-sizing: border-box; }
        .form-group { margin-bottom: 20px; }
        .form-label { display: block; font-weight: 600; margin-bottom: 8px; font-size: 14px; color: #636e72; }
        .form-input {
            width: 100%; padding: 12px 15px;
            border: 1.5px solid #e0e0e0; border-radius: 10px;
            font-size: 15px; box-sizing: border-box; transition: all 0.3s;
        }
        .form-input:focus { border-color: var(--primary-color); outline: none; box-shadow: 0 0 0 3px rgba(0,123,255,0.1); }
        .form-input[readonly] { background: #f8f9fa; color: #b2bec3; cursor: not-allowed; }

        /* Nút bấm */
        .btn-group { display: flex; gap: 15px; margin-top: 30px; }
        .btn-save {
            background: var(--primary-color); color: #fff;
            border: none; padding: 12px 30px; border-radius: 10px;
            font-weight: 600; cursor: pointer; transition: 0.3s;
        }
        .btn-save:hover { background: #0056b3; transform: translateY(-2px); }
        .btn-cancel {
            text-decoration: none; color: #636e72;
            padding: 12px 20px; font-weight: 500;
        }

        @media (max-width: 768px) {
            .sidebar-profile, .main-content { width: 100%; }
        }
    </style>
</head>
<body>
<%
    // Logic giữ nguyên như cũ
    Person person = (Person) request.getAttribute("person");
    UserAccount user = (UserAccount) session.getAttribute("user");
    String error = (String) request.getAttribute("error");
    String msg = request.getParameter("msg");

    String fullNameValue = (person != null && person.getFullName() != null) ? person.getFullName() : "";
    String phoneValue = (person != null && person.getPhone() != null) ? person.getPhone() : "";
    String emailValue = (user != null) ? user.getEmail() : "";
    String genderValue = (person != null && person.getGender() != null) ? person.getGender().name() : "";
    String avatarValue = (person != null && person.getAvatar() != null) ? person.getAvatar() : "";
%>

<div class="container">
    <div class="profile-wrapper">
        <form method="post" action="${pageContext.request.contextPath}/profile" enctype="multipart/form-data" style="display: contents;">

            <div class="sidebar-profile">
                <div class="avatar-container">
                    <% if (!avatarValue.isEmpty()) { %>
                        <img src="<%= avatarValue %>" alt="Avatar" class="avatar-preview">
                    <% } else { %>
                        <div class="avatar-placeholder">No Photo</div>
                    <% } %>
                </div>
                <h3 style="margin: 10px 0 5px 0;"><%= fullNameValue.isEmpty() ? "Học viên" : fullNameValue %></h3>
                <p style="font-size: 13px; opacity: 0.8;">Cập nhật ảnh đại diện mới</p>
                <input type="file" name="avatar" class="form-input" style="font-size: 12px; background: #fff; padding: 8px;" accept="image/*">
            </div>

            <div class="main-content">
                <h2 style="margin-top: 0; margin-bottom: 25px;">Thông tin tài khoản</h2>

                <% if (error != null) { %><div style="color: red; margin-bottom: 15px;"><%= error %></div><% } %>
                <% if ("updated".equals(msg)) { %><div style="color: green; margin-bottom: 15px;">Đã lưu thành công!</div><% } %>

                <div class="form-group">
                    <label class="form-label">Họ và tên</label>
                    <input type="text" name="fullName" class="form-input" value="<%= fullNameValue %>" required>
                </div>

                <div style="display: flex; gap: 20px;">
                    <div class="form-group" style="flex: 1;">
                        <label class="form-label">Email</label>
                        <input type="email" class="form-input" value="<%= emailValue %>" readonly>
                    </div>
                    <div class="form-group" style="flex: 1;">
                        <label class="form-label">Số điện thoại</label>
                        <input type="text" name="phone" class="form-input" value="<%= phoneValue %>">
                    </div>
                </div>

                <div class="form-group">
                    <label class="form-label">Giới tính</label>
                    <select name="gender" class="form-input">
                        <option value="MALE" <%= "MALE".equals(genderValue) ? "selected" : "" %>>Nam</option>
                        <option value="FEMALE" <%= "FEMALE".equals(genderValue) ? "selected" : "" %>>Nữ</option>
                        <option value="OTHER" <%= "OTHER".equals(genderValue) ? "selected" : "" %>>Khác</option>
                    </select>
                </div>

                <div class="btn-group">
                    <button type="submit" class="btn-save">Lưu thay đổi</button>
                    <a href="${pageContext.request.contextPath}/course" class="btn-cancel">Quay lại</a>
                </div>
            </div>
        </form>
    </div>
</div>

</body>
</html>