<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="vn.iotstar.coolenglish.entity.Course" %>
<%@ page import="vn.iotstar.coolenglish.entity.EnglishClass" %>
<%@ page import="vn.iotstar.coolenglish.entity.Room" %>
<%@ page import="vn.iotstar.coolenglish.entity.Teacher" %>
<%@ page import="vn.iotstar.coolenglish.enums.ClassStatus" %>
<%
    EnglishClass classroom = (EnglishClass) request.getAttribute("classroom");
    List<Course> courses = (List<Course>) request.getAttribute("courses");
    List<Room> rooms = (List<Room>) request.getAttribute("rooms");
    List<Teacher> teachers = (List<Teacher>) request.getAttribute("teachers");
    boolean editing = classroom != null && classroom.getClassID() != null && !classroom.getClassID().isBlank();
    String actionUrl = editing ? request.getContextPath() + "/admin/class/update" : request.getContextPath() + "/admin/class/add";
    ClassStatus selectedStatus = classroom == null || classroom.getStatus() == null ? ClassStatus.PLANNED : classroom.getStatus();
    String selectedCourseID = classroom == null ? null : classroom.getCourseID();
    String selectedRoomID = classroom == null ? null : classroom.getRoomID();
    Long selectedTeacherID = classroom == null ? null : classroom.getTeacherID();
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title><%= editing ? "Sua lop hoc" : "Them lop hoc" %></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/vendor/bootstrap/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/app.css">
</head>
<body class="app-body">
<div class="container py-4">
    <div class="app-section app-card">
        <h1 class="h4 app-title mb-3"><%= editing ? "Sua lop hoc" : "Them lop hoc" %></h1>
        <form method="post" action="<%= actionUrl %>" class="vstack gap-3">
            <div>
                <label class="form-label">Ma lop hoc</label>
                <input type="text" class="form-control" name="classID"
                       value="<%= classroom == null || classroom.getClassID() == null ? "" : classroom.getClassID() %>" <%= editing ? "readonly" : "" %> required>
            </div>

            <div>
                <label class="form-label">Ten lop hoc</label>
                <input type="text" class="form-control" name="className"
                       value="<%= classroom == null || classroom.getClassName() == null ? "" : classroom.getClassName() %>" required>
            </div>

            <div>
                <label class="form-label">Ma khoa hoc</label>
                <select class="form-select" name="courseID">
                    <option value="">-- Chon khoa hoc --</option>
                    <% if (courses != null) { %>
                        <% for (Course course : courses) { %>
                            <option value="<%= course.getCourseID() %>"
                                <%= selectedCourseID != null && selectedCourseID.equals(course.getCourseID()) ? "selected" : "" %>>
                                <%= course.getCourseID() %> - <%= course.getCourseName() == null ? "N/A" : course.getCourseName() %>
                            </option>
                        <% } %>
                    <% } %>
                </select>
            </div>

            <div>
                <label class="form-label">Ma phong hoc</label>
                <select class="form-select" name="roomID">
                    <option value="">-- Chon phong hoc --</option>
                    <% if (rooms != null) { %>
                        <% for (Room room : rooms) { %>
                            <option value="<%= room.getRoomID() %>"
                                <%= selectedRoomID != null && selectedRoomID.equals(room.getRoomID()) ? "selected" : "" %>>
                                <%= room.getRoomID() %> - <%= room.getRoomName() == null ? "N/A" : room.getRoomName() %>
                            </option>
                        <% } %>
                    <% } %>
                </select>
            </div>

            <div>
                <label class="form-label">Suc chua toi da</label>
                <input type="number" class="form-control" name="maxCapacity"
                       value="<%= classroom == null || classroom.getMaxCapacity() == null ? "" : classroom.getMaxCapacity() %>" min="1">
            </div>

            <div>
                <label class="form-label">So hoc vien hien tai</label>
                <input type="number" class="form-control" name="currentEnrollment"
                       value="<%= classroom == null || classroom.getCurrentEnrollment() == null ? "0" : classroom.getCurrentEnrollment() %>" min="0">
            </div>

            <div>
                <label class="form-label">Trang thai</label>
                <select class="form-select" name="status" required>
                    <% for (ClassStatus status : ClassStatus.values()) { %>
                    <option value="<%= status.name() %>" <%= status == selectedStatus ? "selected" : "" %>><%= status.name() %></option>
                    <% } %>
                </select>
            </div>

            <div>
                <label class="form-label">Giao vien phu trach</label>
                <select class="form-select" name="teacherID">
                    <option value="">-- Chua gan giao vien --</option>
                    <% if (teachers != null) { %>
                        <% for (Teacher teacher : teachers) { %>
                            <option value="<%= teacher.getId() %>"
                                <%= selectedTeacherID != null && selectedTeacherID.equals(teacher.getId()) ? "selected" : "" %>>
                                <%= teacher.getFullName() %> (<%= teacher.getTeacherID() == null ? "N/A" : teacher.getTeacherID() %>)
                            </option>
                        <% } %>
                    <% } %>
                </select>
            </div>

            <div class="d-flex gap-2">
                <button type="submit" class="btn btn-sky"><%= editing ? "Cap nhat" : "Them moi" %></button>
                <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/admin/class">Quay lai</a>
            </div>
        </form>
    </div>
</div>
<script src="${pageContext.request.contextPath}/assets/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
</body>
</html>

