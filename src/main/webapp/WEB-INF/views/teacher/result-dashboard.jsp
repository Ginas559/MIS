<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="vn.iotstar.coolenglish.entity.EnglishClass" %>
<%@ page import="vn.iotstar.coolenglish.entity.Enrollment" %>
<%@ page import="vn.iotstar.coolenglish.entity.ExamResult" %>
<%
    List<EnglishClass> classes = (List<EnglishClass>) request.getAttribute("classes");
    EnglishClass selectedClass = (EnglishClass) request.getAttribute("selectedClass");
    List<Enrollment> enrollments = (List<Enrollment>) request.getAttribute("enrollments");
    List<ExamResult> testSummaries = (List<ExamResult>) request.getAttribute("testSummaries");
    List<ExamResult> selectedExamResults = (List<ExamResult>) request.getAttribute("selectedExamResults");
    List<ExamResult> classResults = (List<ExamResult>) request.getAttribute("classResults");
    String selectedClassID = (String) request.getAttribute("selectedClassID");
    String selectedExamCode = (String) request.getAttribute("selectedExamCode");
    String mode = (String) request.getAttribute("mode");
    String message = (String) request.getAttribute("message");
    String error = (String) request.getAttribute("error");
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Quan ly diem hoc vien</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/vendor/bootstrap/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/app.css">
</head>
<body class="app-body">
<nav class="navbar app-navbar">
    <div class="container py-2">
        <a class="app-brand" href="${pageContext.request.contextPath}/index.jsp">CoolEnglish</a>
        <div class="d-flex gap-2">
            <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/teacher/results">Bang diem giao vien</a>
            <a class="btn btn-outline-dark" href="${pageContext.request.contextPath}/course">Danh sach khoa hoc</a>
            <form method="post" action="${pageContext.request.contextPath}/logout">
                <button type="submit" class="btn btn-outline-secondary">Dang xuat</button>
            </form>
        </div>
    </div>
</nav>

<div class="container py-4">
    <div class="app-section list-card">
        <div class="d-flex justify-content-between align-items-center mb-3">
            <div>
                <h1 class="h4 app-title mb-1">Quan ly bai test va bang diem</h1>
                <p class="app-subtle mb-0">Giao vien tao bai test, nhap diem va xem ket qua hoc sinh trong lop dang day.</p>
            </div>
        </div>

        <% if (message != null) { %>
        <div class="alert alert-success"><%= message %></div>
        <% } %>
        <% if (error != null) { %>
        <div class="alert alert-danger"><%= error %></div>
        <% } %>

        <form method="get" action="${pageContext.request.contextPath}/teacher/results" class="row g-3 align-items-end mb-4">
            <input type="hidden" name="mode" value="<%= mode == null ? "input" : mode %>">
            <div class="col-md-6">
                <label class="form-label">Chon lop hoc</label>
                <select name="classID" class="form-select" onchange="this.form.submit()">
                    <option value="">-- Chon lop --</option>
                    <% if (classes != null) {
                        for (EnglishClass englishClass : classes) { %>
                    <option value="<%= englishClass.getClassID() %>" <%= englishClass.getClassID().equals(selectedClassID) ? "selected" : "" %>>
                        <%= englishClass.getClassName() %> (<%= englishClass.getClassID() %>)
                    </option>
                    <% }} %>
                </select>
            </div>
            <div class="col-md-6">
                <% if (selectedClass != null) { %>
                <div class="border rounded p-3 bg-light">
                    <div><strong>Course:</strong> <%= selectedClass.getCourseID() == null ? "--" : selectedClass.getCourseID() %></div>
                    <div><strong>Hoc vien da ghi danh:</strong> <%= enrollments == null ? 0 : enrollments.size() %></div>
                    <div><strong>Trang thai:</strong> <%= selectedClass.getStatus() %></div>
                </div>
                <% } %>
            </div>
        </form>

        <% if (selectedClass != null) { %>
        <% if ("input".equals(mode)) { %>
        <div class="row g-4">
            <div class="col-lg-4">
                <div class="border rounded p-3 h-100">
                    <h2 class="h5 mb-3">Tao bai test</h2>
                    <form method="post" action="${pageContext.request.contextPath}/teacher/results/create-test" class="row g-3">
                        <input type="hidden" name="classID" value="<%= selectedClassID %>">
                        <div class="col-12">
                            <label class="form-label">Loai bai test</label>
                            <input type="text" name="testType" class="form-control" placeholder="Vi du: Quiz 1, Midterm, Final" required>
                        </div>
                        <div class="col-12">
                            <label class="form-label">Ngay test</label>
                            <input type="date" name="takenAt" class="form-control">
                        </div>
                        <div class="col-12">
                            <button type="submit" class="btn btn-sky w-100">Tao bai test cho ca lop</button>
                        </div>
                    </form>
                </div>
            </div>

            <div class="col-lg-8">
                <div class="border rounded p-3 h-100">
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <h2 class="h5 mb-0">Danh sach bai test</h2>
                        <span class="app-subtle"><%= testSummaries == null ? 0 : testSummaries.size() %> bai test</span>
                    </div>

                    <div class="table-responsive">
                        <table class="table table-bordered align-middle">
                            <thead>
                            <tr>
                                <th>Ma bai test</th>
                                <th>Loai</th>
                                <th>Ngay test</th>
                                <th>Thao tac</th>
                            </tr>
                            </thead>
                            <tbody>
                            <% if (testSummaries != null && !testSummaries.isEmpty()) {
                                for (ExamResult item : testSummaries) { %>
                            <tr>
                                <td><%= item.getExamCode() %></td>
                                <td><%= item.getTestType() == null ? "--" : item.getTestType() %></td>
                                <td><%= item.getTakenAt() == null ? "--" : item.getTakenAt() %></td>
                                <td>
                                    <a class="btn btn-outline-secondary btn-sm"
                                       href="${pageContext.request.contextPath}/teacher/results?classID=<%= selectedClassID %>&examCode=<%= item.getExamCode() %>">
                                        Nhap diem / Xem diem
                                    </a>
                                </td>
                            </tr>
                            <% }} else { %>
                            <tr>
                                <td colspan="4" class="text-center app-subtle">Chua co bai test nao cho lop nay.</td>
                            </tr>
                            <% } %>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>

        <% if (selectedExamCode != null && selectedExamResults != null && !selectedExamResults.isEmpty()) { %>
        <div class="border rounded p-3 mt-4">
            <div class="d-flex justify-content-between align-items-center mb-3">
                <h2 class="h5 mb-0">Nhap diem cho bai test: <%= selectedExamCode %></h2>
                <span class="app-subtle">Diem se tu dong xep loai khi luu</span>
            </div>
            <form method="post" action="${pageContext.request.contextPath}/teacher/results/save-scores">
                <input type="hidden" name="classID" value="<%= selectedClassID %>">
                <input type="hidden" name="examCode" value="<%= selectedExamCode %>">
                <div class="table-responsive">
                    <table class="table table-bordered align-middle">
                        <thead>
                        <tr>
                            <th>Hoc sinh</th>
                            <th>Email</th>
                            <th>Loai bai test</th>
                            <th>Diem</th>
                            <th>Xep loai</th>
                        </tr>
                        </thead>
                        <tbody>
                        <% for (ExamResult result : selectedExamResults) { %>
                        <tr>
                            <td>
                                <%
                                    String studentName = result.getStudentEmail();
                                    if (enrollments != null) {
                                        for (Enrollment enrollment : enrollments) {
                                            if (enrollment.getStudent() != null
                                                    && result.getStudentEmail().equalsIgnoreCase(enrollment.getStudent().getEmail())) {
                                                studentName = enrollment.getStudent().getFullName();
                                                break;
                                            }
                                        }
                                    }
                                %>
                                <%= studentName %>
                            </td>
                            <td>
                                <%= result.getStudentEmail() %>
                                <input type="hidden" name="studentEmail" value="<%= result.getStudentEmail() %>">
                            </td>
                            <td><%= result.getTestType() == null ? "--" : result.getTestType() %></td>
                            <td style="width: 180px;">
                                <input type="number" name="score" class="form-control" min="0" max="10" step="0.1"
                                       value="<%= result.hasRecordedScore() ? result.getScore() : "" %>">
                            </td>
                            <td><%= result.getGrade() == null ? "Chua xep loai" : result.getGrade() %></td>
                        </tr>
                        <% } %>
                        </tbody>
                    </table>
                </div>
                <button type="submit" class="btn btn-sky">Luu diem va xep loai</button>
            </form>
        </div>
        <% } %>
        <% } %>

        <% if ("view".equals(mode)) { %>
        <div class="border rounded p-3 mt-4">
            <div class="d-flex justify-content-between align-items-center mb-3">
                <h2 class="h5 mb-0">Bang diem hoc sinh trong lop</h2>
                <span class="app-subtle">Tat ca bai test da tao cho lop dang chon</span>
            </div>
            <div class="table-responsive">
                <table class="table table-bordered align-middle">
                    <thead>
                    <tr>
                        <th>Hoc sinh</th>
                        <th>Email</th>
                        <th>Bai test</th>
                        <th>Ngay test</th>
                        <th>Diem</th>
                        <th>Xep loai</th>
                    </tr>
                    </thead>
                    <tbody>
                    <% if (classResults != null && !classResults.isEmpty()) {
                        for (ExamResult result : classResults) { %>
                    <tr>
                        <td>
                            <%
                                String studentName = result.getStudentEmail();
                                if (enrollments != null) {
                                    for (Enrollment enrollment : enrollments) {
                                        if (enrollment.getStudent() != null
                                                && result.getStudentEmail().equalsIgnoreCase(enrollment.getStudent().getEmail())) {
                                            studentName = enrollment.getStudent().getFullName();
                                            break;
                                        }
                                    }
                                }
                            %>
                            <%= studentName %>
                        </td>
                        <td><%= result.getStudentEmail() %></td>
                        <td><%= result.getTestType() == null ? result.getExamCode() : result.getTestType() %></td>
                        <td><%= result.getTakenAt() == null ? "--" : result.getTakenAt() %></td>
                        <td><%= result.hasRecordedScore() ? result.getScore() : "Chua nhap" %></td>
                        <td><%= result.getGrade() == null ? "Chua xep loai" : result.getGrade() %></td>
                    </tr>
                    <% }} else { %>
                    <tr>
                        <td colspan="6" class="text-center app-subtle">Chua co du lieu diem cho lop nay.</td>
                    </tr>
                    <% } %>
                    </tbody>
                </table>
            </div>
        </div>
        <% } %>
        <% } else { %>
        <div class="alert alert-info mb-0">Giao vien nay chua duoc phan cong lop nao.</div>
        <% } %>
    </div>
</div>

<script src="${pageContext.request.contextPath}/assets/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
</body>
</html>
