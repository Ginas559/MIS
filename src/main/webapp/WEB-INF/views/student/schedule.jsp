<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="vn.iotstar.coolenglish.entity.Course" %>
<%@ page import="vn.iotstar.coolenglish.entity.Enrollment" %>
<%@ page import="vn.iotstar.coolenglish.entity.EnglishClass" %>
<%@ page import="vn.iotstar.coolenglish.entity.ExamResult" %>
<%@ page import="vn.iotstar.coolenglish.entity.Schedule" %>
<%@ page import="vn.iotstar.coolenglish.entity.Session" %>
<%@ page import="vn.iotstar.coolenglish.entity.Student" %>
<%@ page import="vn.iotstar.coolenglish.entity.Teacher" %>
<%@ page import="vn.iotstar.coolenglish.entity.UserAccount" %>
<%@ page import="vn.iotstar.coolenglish.enums.UserRole" %>
<%
    Student student = (Student) request.getAttribute("student");
    List<Enrollment> enrollments = (List<Enrollment>) request.getAttribute("enrollments");
    Map<String, List<ExamResult>> resultsByClassID = (Map<String, List<ExamResult>>) request.getAttribute("resultsByClassID");
    Integer totalClasses = (Integer) request.getAttribute("totalClasses");
    String message = (String) request.getAttribute("message");
    String studentName = student != null && student.getFullName() != null ? student.getFullName() : "Hoc vien";
    UserAccount currentUser = (UserAccount) session.getAttribute("user");
    boolean isStudent = currentUser != null && currentUser.getRole() == UserRole.STUDENT;
    int totalSessions = 0;
    if (enrollments != null) {
        for (Enrollment enrollment : enrollments) {
            EnglishClass englishClass = enrollment.getEnglishClass();
            Schedule schedule = englishClass != null ? englishClass.getSchedule() : null;
            if (schedule != null && schedule.getSessions() != null) {
                totalSessions += schedule.getSessions().size();
            }
        }
    }
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Lich hoc cua toi</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/vendor/bootstrap/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/app.css">
</head>
<body class="app-body">
<nav class="navbar app-navbar">
    <div class="container py-2">
        <a class="app-brand" href="${pageContext.request.contextPath}/index.jsp">CoolEnglish</a>
        <div class="d-flex gap-2">
            <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/course">Danh sach khoa hoc</a>
            <% if (isStudent) { %>
            <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/student/schedule">Lich hoc</a>
            <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/student/results">Ket qua hoc tap</a>
            <% } %>
            <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/profile">Ho so ca nhan</a>
            <form method="post" action="${pageContext.request.contextPath}/logout">
                <button type="submit" class="btn btn-outline-dark">Dang xuat</button>
            </form>
        </div>
    </div>
</nav>

<div class="container py-4">
    <div class="app-section list-card mb-4">
        <div class="d-flex justify-content-between align-items-center flex-wrap gap-2">
            <div>
                <h1 class="h4 app-title mb-1">Lich hoc cua toi</h1>
                <p class="app-subtle mb-0">Xem lich hoc tung lop, giao vien va danh sach cac buoi hoc.</p>
            </div>
            <div class="d-flex gap-2 flex-wrap">
                <a class="btn btn-outline-secondary btn-sm" href="${pageContext.request.contextPath}/course">Danh sach khoa hoc</a>
                <a class="btn btn-outline-dark btn-sm" href="${pageContext.request.contextPath}/student/results">Ket qua hoc tap</a>
            </div>
        </div>
    </div>

    <% if (message != null) { %>
    <div class="alert alert-success"><%= message %></div>
    <% } %>

    <div class="row g-3 mb-4">
        <div class="col-md-4">
            <div class="app-section p-3 h-100">
                <div class="app-subtle">Hoc vien</div>
                <div class="h4 mb-0"><%= studentName %></div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="app-section p-3 h-100">
                <div class="app-subtle">So lop da ghi danh</div>
                <div class="h4 mb-0"><%= totalClasses == null ? 0 : totalClasses %></div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="app-section p-3 h-100">
                <div class="app-subtle">Tong so buoi hoc</div>
                <div class="h4 mb-0"><%= totalSessions %></div>
            </div>
        </div>
    </div>

    <div class="card mb-4">
        <div class="card-header bg-light">
            <strong>Lich hoc tong hop</strong>
        </div>
        <div class="card-body small">
            Trang nay hien thi toan bo lich hoc cua cac lop ban da ghi danh, bao gom khoa hoc, giao vien va cac buoi hoc chi tiet.
        </div>
    </div>

    <div class="card">
        <div class="card-header bg-primary text-white d-flex justify-content-between align-items-center">
            <span>Danh sach lich hoc</span>
            <span class="badge text-bg-light"><%= totalClasses == null ? 0 : totalClasses %> lop</span>
        </div>
        <div class="table-responsive">
            <table class="table table-bordered table-striped align-middle mb-0">
                <thead>
                    <tr>
                        <th>Lop hoc</th>
                        <th>Khoa hoc</th>
                        <th>Giao vien</th>
                        <th>Thoi gian khoa hoc</th>
                        <th>Chi tiet buoi hoc</th>
                        <th>So bai test</th>
                        <th>Trang thai</th>
                    </tr>
                </thead>
                <tbody>
                    <% if (enrollments != null && !enrollments.isEmpty()) {
                        for (Enrollment enrollment : enrollments) {
                            EnglishClass englishClass = enrollment.getEnglishClass();
                            Course course = englishClass != null ? englishClass.getCourse() : null;
                            Teacher teacher = englishClass != null ? englishClass.getTeacher() : null;
                            Schedule schedule = englishClass != null ? englishClass.getSchedule() : null;
                            List<ExamResult> classResults = resultsByClassID == null || englishClass == null
                                    ? null
                                    : resultsByClassID.get(englishClass.getClassID());
                    %>
                    <tr>
                        <td>
                            <div><strong><%= englishClass == null ? "--" : englishClass.getClassName() %></strong></div>
                            <div class="text-muted"><%= englishClass == null ? "--" : englishClass.getClassID() %></div>
                        </td>
                        <td>
                            <div><strong><%= course == null ? "--" : course.getCourseName() %></strong></div>
                            <div class="text-muted"><%= course == null || course.getLevel() == null ? "--" : course.getLevel() %></div>
                        </td>
                        <td><%= teacher == null ? "--" : teacher.getFullName() %></td>
                        <td>
                            <%= englishClass == null || englishClass.getStartDate() == null ? "--" : englishClass.getStartDate() %>
                            <br>
                            <span class="text-muted">den <%= englishClass == null || englishClass.getEndDate() == null ? "--" : englishClass.getEndDate() %></span>
                        </td>
                        <td>
                            <% if (schedule != null && schedule.getSessions() != null && !schedule.getSessions().isEmpty()) { %>
                                <% for (Session sessionItem : schedule.getSessions()) { %>
                                <div class="mb-2">
                                    <div><strong><%= sessionItem.getSessionName() == null ? sessionItem.getSessionID() : sessionItem.getSessionName() %></strong></div>
                                    <div class="text-muted">
                                        <%= sessionItem.getSessionDate() == null ? "--" : sessionItem.getSessionDate() %>
                                        |
                                        <%= sessionItem.getStartTime() == null ? "--" : sessionItem.getStartTime() %>
                                        -
                                        <%= sessionItem.getEndTime() == null ? "--" : sessionItem.getEndTime() %>
                                    </div>
                                </div>
                                <% } %>
                            <% } else { %>
                            <span class="text-muted">Lop nay chua co lich hoc chi tiet.</span>
                            <% } %>
                        </td>
                        <td class="text-center"><%= classResults == null ? 0 : classResults.size() %></td>
                        <td><span class="status-pill"><%= enrollment.getStatus() %></span></td>
                    </tr>
                    <% }} else { %>
                    <tr>
                        <td colspan="7" class="text-center text-muted py-4">Ban chua ghi danh lop hoc nao.</td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </div>
</div>
<footer class="container pb-4">
    <p class="app-subtle mb-0 text-center">CoolEnglish - Learning Management</p>
</footer>

<script src="${pageContext.request.contextPath}/assets/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
</body>
</html>
