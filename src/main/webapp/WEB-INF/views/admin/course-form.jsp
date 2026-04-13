<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="vn.iotstar.coolenglish.entity.Course" %>
<%
    Course course = (Course) request.getAttribute("course");
    boolean editing = course != null && course.getCourseID() != null && !course.getCourseID().isBlank();
    String actionUrl = editing ? request.getContextPath() + "/admin/course/update" : request.getContextPath() + "/admin/course/add";
    String courseStatus = course == null || course.getStatus() == null ? "ACTIVE" : course.getStatus();
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title><%= editing ? "Sua khoa hoc" : "Them khoa hoc" %></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/vendor/bootstrap/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/app.css">
    <style>
        .image-preview {
            max-width: 300px;
            max-height: 300px;
            margin-bottom: 1rem;
            border-radius: 8px;
            object-fit: cover;
        }
        .image-input-wrapper {
            display: flex;
            flex-direction: column;
            gap: 1rem;
        }
        .form-file {
            display: block;
        }
        .form-file input[type="file"] {
            display: block;
            width: 100%;
            padding: 0.375rem 0.75rem;
            font-size: 1rem;
            line-height: 1.5;
            color: #212529;
            background-color: #fff;
            background-clip: padding-box;
            border: 1px solid #dee2e6;
            border-radius: 0.25rem;
            transition: border-color 0.15s ease-in-out;
        }
        .form-file input[type="file"]:focus {
            border-color: #80bdff;
            outline: 0;
            box-shadow: 0 0 0 0.2rem rgba(0, 123, 255, 0.25);
        }
        .image-info {
            font-size: 0.875rem;
            color: #6c757d;
        }
    </style>
</head>
<body class="app-body">
<div class="container py-4">
    <div class="app-section app-card">
        <h1 class="h4 app-title mb-3"><%= editing ? "Sua khoa hoc" : "Them khoa hoc" %></h1>
        <form method="post" action="<%= actionUrl %>" class="vstack gap-3" enctype="multipart/form-data">
            <div>
                <label class="form-label">Ma khoa hoc</label>
                <input type="text" class="form-control" name="courseID"
                       value="<%= course == null || course.getCourseID() == null ? "" : course.getCourseID() %>" <%= editing ? "readonly" : "" %> required>
            </div>

            <div>
                <label class="form-label">Ten khoa hoc</label>
                <input type="text" class="form-control" name="courseName"
                       value="<%= course == null || course.getCourseName() == null ? "" : course.getCourseName() %>" required>
            </div>

            <div>
                <label class="form-label">Mo ta</label>
                <textarea class="form-control" rows="4" name="description"><%= course == null || course.getDescription() == null ? "" : course.getDescription() %></textarea>
            </div>

            <div class="image-input-wrapper">
                <label class="form-label">Anh khoa hoc</label>
                <% if (course != null && course.getImage() != null && !course.getImage().isEmpty()) { %>
                    <img src="<%= course.getImage() %>" alt="Course Image" class="image-preview">
                <% } %>
                <div class="form-file">
                    <input type="file" id="courseImage" name="courseImage" class="form-control" accept="image/*">
                    <small class="image-info">Dinh dang: JPG, PNG. Dung luong toi da: 5MB</small>
                </div>
            </div>

            <div>
                <label class="form-label">Level</label>
                <input type="text" class="form-control" name="level"
                       value="<%= course == null || course.getLevel() == null ? "" : course.getLevel() %>">
            </div>

            <div>
                <label class="form-label">Duration</label>
                <input type="number" class="form-control" name="duration"
                       value="<%= course == null || course.getDuration() == null ? "" : course.getDuration() %>">
            </div>

            <div>
                <label class="form-label">Hoc phi</label>
                <input type="number" step="0.01" class="form-control" name="fee"
                       value="<%= course == null || course.getFee() == null ? "" : course.getFee() %>">
            </div>

            <div>
                <label class="form-label">Trang thai</label>
                <select class="form-select" name="status" required>
                    <option value="ACTIVE" <%= "ACTIVE".equalsIgnoreCase(courseStatus) ? "selected" : "" %>>ACTIVE</option>
                    <option value="INACTIVE" <%= "INACTIVE".equalsIgnoreCase(courseStatus) ? "selected" : "" %>>INACTIVE</option>
                </select>
            </div>

            <div class="d-flex gap-2">
                <button type="submit" class="btn btn-sky"><%= editing ? "Cap nhat" : "Them moi" %></button>
                <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/admin/course">Quay lai</a>
            </div>
        </form>
    </div>
</div>
<script src="${pageContext.request.contextPath}/assets/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
</body>
</html>
