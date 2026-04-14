<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="vn.iotstar.coolenglish.entity.Enrollment" %>
<%@ page import="vn.iotstar.coolenglish.entity.ExamResult" %>
<%@ page import="vn.iotstar.coolenglish.entity.Student" %>
<%@ page import="vn.iotstar.coolenglish.entity.UserAccount" %>
<%@ page import="vn.iotstar.coolenglish.enums.UserRole" %>
<%
    Student student = (Student) request.getAttribute("student");
    List<Enrollment> enrollments = (List<Enrollment>) request.getAttribute("enrollments");
    List<ExamResult> examResults = (List<ExamResult>) request.getAttribute("examResults");
    Integer gradedTests = (Integer) request.getAttribute("gradedTests");
    String message = (String) request.getAttribute("message");
    String studentName = student != null && student.getFullName() != null ? student.getFullName() : "Hoc vien";
    UserAccount currentUser = (UserAccount) session.getAttribute("user");
    boolean isStudent = currentUser != null && currentUser.getRole() == UserRole.STUDENT;
    int waitingTests = 0;
    if (examResults != null) {
        for (ExamResult result : examResults) {
            if (!result.hasRecordedScore()) {
                waitingTests++;
            }
        }
    }
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Ket qua hoc tap cua toi</title>
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
                <h1 class="h4 app-title mb-1">Ket qua hoc tap</h1>
                <p class="app-subtle mb-0">Xem diem tung bai test, he diem va tien do cham diem.</p>
            </div>
            <div class="d-flex gap-2 flex-wrap">
                <a class="btn btn-outline-secondary btn-sm" href="${pageContext.request.contextPath}/course">Danh sach khoa hoc</a>
                <a class="btn btn-outline-dark btn-sm" href="${pageContext.request.contextPath}/student/schedule">Lich hoc</a>
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
                <div class="app-subtle">Bai test da co diem</div>
                <div class="h4 mb-0"><%= gradedTests == null ? 0 : gradedTests %></div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="app-section p-3 h-100">
                <div class="app-subtle">Bai test dang cho cham</div>
                <div class="h4 mb-0"><%= waitingTests %></div>
            </div>
        </div>
    </div>

    <div class="card mb-4">
        <div class="card-header bg-light">
            <strong>Bang diem tong hop</strong>
        </div>
        <div class="card-body small">
            Trang nay hien thi toan bo diem bai test va trang thai cham diem cua tung bai.
        </div>
    </div>

    <div class="card">
        <div class="card-header bg-primary text-white d-flex justify-content-between align-items-center">
            <span>Danh sach ket qua bai test</span>
            <span class="badge text-bg-light"><%= examResults == null ? 0 : examResults.size() %> bai</span>
        </div>
        <div class="table-responsive">
            <table class="table table-bordered table-striped align-middle mb-0">
                <thead>
                    <tr>
                        <th>Lop hoc</th>
                        <th>Ma bai test</th>
                        <th>Loai bai test</th>
                        <th>He diem</th>
                        <th>Ngay test</th>
                        <th class="text-center">Listening</th>
                        <th class="text-center">Reading</th>
                        <th class="text-center">Speaking</th>
                        <th class="text-center">Writing</th>
                        <th class="text-center">Diem tong</th>
                        <th>Trang thai</th>
                    </tr>
                </thead>
                <tbody>
                    <% if (examResults != null && !examResults.isEmpty()) {
                        for (ExamResult result : examResults) {
                            String className = result.getClassID();
                            if (enrollments != null) {
                                for (Enrollment enrollment : enrollments) {
                                    if (enrollment.getEnglishClass() != null
                                            && result.getClassID() != null
                                            && result.getClassID().equals(enrollment.getEnglishClass().getClassID())) {
                                        className = enrollment.getEnglishClass().getClassName();
                                        break;
                                    }
                                }
                            }
                    %>
                    <tr>
                        <td>
                            <div><strong><%= className == null ? "--" : className %></strong></div>
                            <div class="text-muted"><%= result.getClassID() == null ? "--" : result.getClassID() %></div>
                        </td>
                        <td><%= result.getExamCode() == null ? "--" : result.getExamCode() %></td>
                        <td><%= result.getTestType() == null ? "--" : result.getTestType() %></td>
                        <td><%= result.getExamFormat() == null ? "--" : result.getExamFormat() %></td>
                        <td><%= result.getTakenAt() == null ? "--" : result.getTakenAt() %></td>
                        <td class="text-center"><%= result.getDisplayListeningScore() %></td>
                        <td class="text-center"><%= result.getDisplayReadingScore() %></td>
                        <td class="text-center"><%= result.getDisplaySpeakingScore() %></td>
                        <td class="text-center"><%= result.getDisplayWritingScore() %></td>
                        <td class="text-center fw-bold text-primary"><%= result.hasRecordedScore() ? result.getScore() : "--" %></td>
                        <td>
                            <% if (result.hasRecordedScore()) { %>
                            <span class="badge text-bg-success">Da co diem</span>
                            <% } else { %>
                            <span class="badge text-bg-warning">Dang cho giao vien nhap diem</span>
                            <% } %>
                        </td>
                    </tr>
                    <% }} else { %>
                    <tr>
                        <td colspan="11" class="text-center text-muted py-4">Chua co bai test nao duoc giao cho ban.</td>
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
