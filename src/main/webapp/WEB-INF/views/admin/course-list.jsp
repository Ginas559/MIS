<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Set" %>
<%@ page import="vn.iotstar.coolenglish.entity.Course" %>
<%@ page import="vn.iotstar.coolenglish.entity.EnglishClass" %>
<%@ page import="vn.iotstar.coolenglish.entity.UserAccount" %>
<%@ page import="vn.iotstar.coolenglish.enums.UserRole" %>
<%
    List<Course> courses = (List<Course>) request.getAttribute("courses");
    Set<String> enrolledCourseIds = (Set<String>) request.getAttribute("enrolledCourseIds");
    Map<String, List<EnglishClass>> teacherClassesByCourse =
            (Map<String, List<EnglishClass>>) request.getAttribute("teacherClassesByCourse");
    UserAccount currentUser = (UserAccount) session.getAttribute("user");
    boolean managementView = Boolean.TRUE.equals(request.getAttribute("managementView"));
    boolean canManage = currentUser != null
            && (currentUser.getRole() == UserRole.ADMIN || currentUser.getRole() == UserRole.STAFF);
    boolean isTeacher = currentUser != null && currentUser.getRole() == UserRole.TEACHER;
    boolean isStudent = currentUser != null && currentUser.getRole() == UserRole.STUDENT;
    Integer currentPage = (Integer) request.getAttribute("currentPage");
    Integer totalPages = (Integer) request.getAttribute("totalPages");
    Integer totalItems = (Integer) request.getAttribute("totalItems");
    String paginationPath = (String) request.getAttribute("paginationPath");

    if (currentPage == null) currentPage = 1;
    if (totalPages == null) totalPages = 1;
    if (totalItems == null) totalItems = 0;
    if (paginationPath == null || paginationPath.isBlank()) paginationPath = "/admin/course";
    int tableColumnCount = (canManage && managementView) ? 8 : 7;
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Danh sach khoa hoc</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/vendor/bootstrap/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/app.css">
</head>
<body class="app-body">
<nav class="navbar app-navbar">
    <div class="container py-2">
        <a class="app-brand" href="${pageContext.request.contextPath}/index.jsp">CoolEnglish</a>
        <div class="d-flex gap-2 align-items-center">
            <% if (canManage) { %>
                <a class="btn btn-outline-sky" href="${pageContext.request.contextPath}/admin/dashboard">Bảng điều khiển</a>
                <a class="btn btn-outline-sky" href="${pageContext.request.contextPath}/admin/course">Quan ly khoa hoc</a>
                <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/admin/room">Quan ly phong hoc</a>
                <a class="btn btn-outline-dark" href="${pageContext.request.contextPath}/admin/class">Quan ly lop hoc</a>
                <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/admin/schedule">Quan ly lich hoc</a>
                <a class="btn btn-outline-dark" href="${pageContext.request.contextPath}/admin/payment/cash">Xac nhan tien mat</a>
                <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/admin/roadmap-grants">Cap quyen roadmap</a>
                <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/admin/roadmap-management">Quan ly roadmap</a>
                <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/admin/integration/exam-results">Dong bo ket qua</a>
                <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/admin/audit-logs">Audit logs</a>
                <% if (currentUser != null && currentUser.getRole() == UserRole.ADMIN) { %>
                    <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/admin/users">Quan ly nguoi dung</a>
                <% } %>
            <% } %>
            <% if (currentUser != null && currentUser.getRole() == UserRole.TEACHER) { %>
                <a class="btn btn-outline-sky" href="${pageContext.request.contextPath}/teacher/classes">Diem danh</a>
            <% } %>
            <% if (isStudent) { %>
                <a class="btn btn-outline-sky" href="${pageContext.request.contextPath}/student/schedule">Lich hoc</a>
                <a class="btn btn-sky" href="${pageContext.request.contextPath}/student/results">Ket qua hoc tap</a>
            <% } %>
            <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/profile">Ho so ca nhan</a>
            <% if (currentUser != null) { %>
                <form method="post" action="${pageContext.request.contextPath}/logout" class="m-0">
                    <button type="submit" class="btn btn-outline-dark">Dang xuat</button>
                </form>
            <% } else { %>
                <a class="btn btn-outline-dark" href="${pageContext.request.contextPath}/login">Dang nhap</a>
            <% } %>
        </div>
    </div>
</nav>

<div class="container py-4">
    <div class="app-section list-card">
        <div class="d-flex justify-content-between align-items-center mb-3">
            <div>
                <h1 class="h4 app-title mb-1">Danh sach khoa hoc</h1>
                <p class="app-subtle mb-0">Nguoi dung dang nhap co the xem danh sach khoa hoc.</p>
                <% if ("already_enrolled".equals(request.getParameter("msg"))) { %>
                    <p class="text-success mb-0">Ban da ghi danh khoa hoc nay, khong can thanh toan lai.</p>
                <% } %>
            </div>
            <div class="inline-actions">
                <a class="btn btn-outline-sky" href="${pageContext.request.contextPath}/roadmaps">Trang roadmap tu hoc</a>
            <% if (isStudent) { %>
                <a class="btn btn-outline-sky" href="${pageContext.request.contextPath}/student/schedule">Lich hoc</a>
                <a class="btn btn-sky" href="${pageContext.request.contextPath}/student/results">Ket qua hoc tap</a>
            <% } %>
            <% if (canManage && !managementView) { %>
                <a class="btn btn-sky" href="/MISEnglish/admin/course">Mo trang quan ly</a>
            <% } %>
            </div>
        </div>

        <% if (canManage && managementView) { %>
        <div class="mb-3 inline-actions">
            <a class="btn btn-sky btn-sm" href="${pageContext.request.contextPath}/admin/course/add">Them khoa hoc</a>
            <a class="btn btn-outline-secondary btn-sm" href="${pageContext.request.contextPath}/admin/room">Danh sach phong hoc</a>
            <a class="btn btn-outline-dark btn-sm" href="${pageContext.request.contextPath}/admin/class">Danh sach lop hoc</a>
            <a class="btn btn-outline-secondary btn-sm" href="${pageContext.request.contextPath}/admin/schedule">Quan ly lich hoc</a>
            <a class="btn btn-outline-dark btn-sm" href="${pageContext.request.contextPath}/admin/payment/cash">Xac nhan tien mat</a>
            <a class="btn btn-outline-dark btn-sm" href="${pageContext.request.contextPath}/staff/results?mode=input">Nhap diem dau vao / thi thu / cuoi khoa</a>
            <a class="btn btn-outline-secondary btn-sm" href="${pageContext.request.contextPath}/staff/results?mode=view">Xem bang diem tong hop</a>
            <a class="btn btn-outline-secondary btn-sm" href="${pageContext.request.contextPath}/admin/roadmap-grants">Cap quyen roadmap</a>
            <a class="btn btn-outline-secondary btn-sm" href="${pageContext.request.contextPath}/admin/roadmap-management">CRUD roadmap</a>
            <a class="btn btn-outline-secondary btn-sm" href="${pageContext.request.contextPath}/admin/integration/exam-results">Dong bo ket qua</a>
            <% if (currentUser != null && currentUser.getRole() == UserRole.ADMIN) { %>
            <a class="btn btn-outline-secondary btn-sm" href="${pageContext.request.contextPath}/admin/users">Danh sach nguoi dung</a>
            <% } %>
        </div>

        <form method="post" action="${pageContext.request.contextPath}/admin/course/update-fee" class="row g-2 mb-3">
            <div class="col-md-3">
                <input type="text" class="form-control" name="courseID" placeholder="Ma khoa hoc" required>
            </div>
            <div class="col-md-3">
                <input type="number" step="0.01" class="form-control" name="newFee" placeholder="Hoc phi moi" required>
            </div>
            <div class="col-md-3">
                <button type="submit" class="btn btn-outline-dark">Cap nhat hoc phi</button>
            </div>
        </form>
        <% } %>

        <div class="table-responsive">
            <table class="table table-bordered align-middle">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Ten khoa hoc</th>
                        <th>Mo ta</th>
                        <th>Level</th>
                        <th>Duration</th>
                        <th>Fee</th>
                        <th>Status</th>
                        <% if (canManage && managementView) { %>
                        <th>Thao tac</th>
                        <% } else if (isTeacher) { %>
                        <th>Bang diem</th>
                        <% } else { %>
                        <th>Mua khoa hoc</th>
                        <% } %>
                    </tr>
                </thead>
                <tbody>
                    <%
                        if (courses != null && !courses.isEmpty()) {
                            for (Course course : courses) {
                    %>
                    <tr>
                        <td><%= course.getCourseID() %></td>
                        <td><%= course.getCourseName() %></td>
                        <td><%= course.getDescription() %></td>
                        <td><%= course.getLevel() %></td>
                        <td><%= course.getDuration() %></td>
                        <td><%= course.getFee() %></td>
                        <td><span class="status-pill"><%= course.getStatus() %></span></td>
                        <% if (canManage && managementView) { %>
                        <td>
                            <a class="btn btn-outline-secondary btn-sm" href="${pageContext.request.contextPath}/admin/course/update?id=<%= course.getCourseID() %>">Sua</a>
                            <% if (currentUser.getRole() == UserRole.ADMIN) { %>
                            <a class="btn btn-outline-dark btn-sm" href="${pageContext.request.contextPath}/admin/course/delete?id=<%= course.getCourseID() %>"
                               onclick="return confirm('Xoa khoa hoc nay?');">Xoa</a>
                            <% } %>
                        </td>
                        <% } else if (isTeacher) { %>
                        <td>
                            <%
                                List<EnglishClass> assignedClasses = teacherClassesByCourse == null
                                        ? null
                                        : teacherClassesByCourse.get(course.getCourseID());
                            %>
                            <% if (assignedClasses != null && !assignedClasses.isEmpty()) { %>
                                <% for (EnglishClass assignedClass : assignedClasses) { %>
                                    <div class="mb-2">
                                        <span class="app-subtle d-block">Lop <%= assignedClass.getClassID() %> - <%= assignedClass.getClassName() %></span>
                                        <a class="btn btn-sky btn-sm"
                                           href="${pageContext.request.contextPath}/teacher/results?mode=input&classID=<%= assignedClass.getClassID() %>">
                                            Nhap diem
                                        </a>
                                        <a class="btn btn-outline-secondary btn-sm"
                                           href="${pageContext.request.contextPath}/teacher/results?mode=view&classID=<%= assignedClass.getClassID() %>">
                                            Xem diem
                                        </a>
                                    </div>
                                <% } %>
                            <% } else { %>
                                <span class="btn btn-outline-secondary btn-sm disabled">Chua duoc phan lop</span>
                            <% } %>
                        </td>
                        <% } else { %>
                        <td>
                            <% boolean alreadyEnrolled = enrolledCourseIds != null && enrolledCourseIds.contains(course.getCourseID()); %>
                            <% if (isStudent && alreadyEnrolled) { %>
                                <span class="btn btn-success btn-sm disabled">Da ghi danh</span>
                            <% } else { %>
                            <a class="btn btn-sky btn-sm"
                               href="${pageContext.request.contextPath}/student/payment/detail?courseID=<%= course.getCourseID() %>">Mua</a>
                            <% } %>
                        </td>
                        <% } %>
                    </tr>
                    <%
                            }
                        } else {
                    %>
                    <tr>
                        <td colspan="<%= tableColumnCount %>" class="text-center app-subtle">Chua co du lieu khoa hoc.</td>
                    </tr>
                    <%
                        }
                    %>
                </tbody>
            </table>
        </div>

        <% if (totalPages > 1) { %>
        <nav aria-label="Course pagination" class="mt-3">
            <ul class="pagination mb-0">
                <li class="page-item <%= currentPage <= 1 ? "disabled" : "" %>">
                    <a class="page-link" href="${pageContext.request.contextPath}<%= paginationPath %>?page=<%= currentPage - 1 %>">Truoc</a>
                </li>
                <% for (int pageNumber = 1; pageNumber <= totalPages; pageNumber++) { %>
                <li class="page-item <%= pageNumber == currentPage ? "active" : "" %>">
                    <a class="page-link" href="${pageContext.request.contextPath}<%= paginationPath %>?page=<%= pageNumber %>"><%= pageNumber %></a>
                </li>
                <% } %>
                <li class="page-item <%= currentPage >= totalPages ? "disabled" : "" %>">
                    <a class="page-link" href="${pageContext.request.contextPath}<%= paginationPath %>?page=<%= currentPage + 1 %>">Sau</a>
                </li>
            </ul>
        </nav>
        <p class="app-subtle mb-0 mt-2">Tong: <%= totalItems %> ban ghi (30 dong/trang).</p>
        <% } %>
    </div>
</div>
<script src="${pageContext.request.contextPath}/assets/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
</body>
</html>
