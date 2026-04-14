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
    ExamResult selectedTestSummary = (ExamResult) request.getAttribute("selectedTestSummary");
    String selectedClassID = (String) request.getAttribute("selectedClassID");
    String selectedExamCode = (String) request.getAttribute("selectedExamCode");
    String mode = (String) request.getAttribute("mode");
    String message = (String) request.getAttribute("message");
    String error = (String) request.getAttribute("error");
    String dashboardPath = (String) request.getAttribute("dashboardPath");
    boolean isTeacherView = Boolean.TRUE.equals(request.getAttribute("isTeacherView"));
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
            <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}<%= dashboardPath %>">
                <%= isTeacherView ? "Bang diem giao vien" : "Bang diem staff" %>
            </a>
            <a class="btn btn-outline-dark" href="${pageContext.request.contextPath}/course">Danh sach khoa hoc</a>
            <form method="post" action="${pageContext.request.contextPath}/logout">
                <button type="submit" class="btn btn-outline-secondary">Dang xuat</button>
            </form>
        </div>
    </div>
</nav>

<div class="container py-4">
    <div class="app-section list-card">
        <div class="mb-3">
            <h1 class="h4 app-title mb-1">Quan ly bai test va bang diem</h1>
            <p class="app-subtle mb-0">
                <%= isTeacherView
                        ? "Teacher tao bai test nho, chon TOEIC hoac IELTS va chon ky nang can nhap diem."
                        : "Staff tao bai theo 3 mau TOEIC 2 ky nang, TOEIC 4 ky nang hoac IELTS." %>
            </p>
        </div>

        <% if (message != null) { %>
        <div class="alert alert-success"><%= message %></div>
        <% } %>
        <% if (error != null) { %>
        <div class="alert alert-danger"><%= error %></div>
        <% } %>

        <form method="get" action="${pageContext.request.contextPath}<%= dashboardPath %>" class="row g-3 align-items-end mb-4">
            <input type="hidden" name="mode" value="<%= mode == null ? "input" : mode %>">
            <div class="col-md-6">
                <label class="form-label">Chon lop hoc</label>
                <select name="classID" class="form-select" onchange="this.form.submit()">
                    <option value="">-- Chon lop --</option>
                    <% if (classes != null) {
                        for (EnglishClass englishClass : classes) { %>
                    <option value="<%= englishClass.getClassID() %>" <%= englishClass.getClassID().equals(selectedClassID) ? "selected" : "" %>>
                        <%= englishClass.getClassName() %> (<%= englishClass.getClassID() %>) - <%= englishClass.getTeacherName() == null ? "--" : englishClass.getTeacherName() %>
                    </option>
                    <% }} %>
                </select>
            </div>
            <div class="col-md-6">
                <% if (selectedClass != null) { %>
                <div class="border rounded p-3 bg-light">
                    <div><strong>Course:</strong> <%= selectedClass.getCourseID() == null ? "--" : selectedClass.getCourseID() %></div>
                    <div><strong>Giao vien:</strong> <%= selectedClass.getTeacherName() == null ? "--" : selectedClass.getTeacherName() %></div>
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
                    <form method="post" action="${pageContext.request.contextPath}<%= dashboardPath %>/create-test" class="row g-3">
                        <input type="hidden" name="classID" value="<%= selectedClassID %>">
                        <div class="col-12">
                            <label class="form-label"><%= isTeacherView ? "Ten bai test" : "Loai bai test" %></label>
                            <% if (isTeacherView) { %>
                            <input type="text" name="testType" class="form-control" placeholder="Vi du: Quiz 1, Progress Test 2" required>
                            <% } else { %>
                            <select name="testType" class="form-select" required>
                                <option value="">-- Chon loai --</option>
                                <option value="TOEIC 2 ky nang">TOEIC 2 ky nang</option>
                                <option value="TOEIC 4 ky nang">TOEIC 4 ky nang</option>
                                <option value="IELTS">IELTS</option>
                            </select>
                            <% } %>
                        </div>

                        <% if (isTeacherView) { %>
                        <div class="col-12">
                            <label class="form-label">He diem bai test</label>
                            <select name="examFormat" class="form-select" required>
                                <option value="">-- Chon he diem --</option>
                                <option value="TOEIC">TOEIC</option>
                                <option value="IELTS">IELTS</option>
                            </select>
                        </div>
                        <div class="col-12">
                            <label class="form-label d-block">Ky nang ap dung</label>
                            <div class="form-check">
                                <input class="form-check-input" type="checkbox" name="hasListening" id="hasListening">
                                <label class="form-check-label" for="hasListening">Listening</label>
                            </div>
                            <div class="form-check">
                                <input class="form-check-input" type="checkbox" name="hasReading" id="hasReading">
                                <label class="form-check-label" for="hasReading">Reading</label>
                            </div>
                            <div class="form-check">
                                <input class="form-check-input" type="checkbox" name="hasSpeaking" id="hasSpeaking">
                                <label class="form-check-label" for="hasSpeaking">Speaking</label>
                            </div>
                            <div class="form-check">
                                <input class="form-check-input" type="checkbox" name="hasWriting" id="hasWriting">
                                <label class="form-check-label" for="hasWriting">Writing</label>
                            </div>
                            <div class="form-text">
                                Bang diem se luon hien du 4 cot ky nang. Ky nang khong co trong bai test se hien 0.
                            </div>
                        </div>
                        <% } else { %>
                        <div class="col-12">
                            <div class="form-text">
                                TOEIC 2 ky nang = Listening + Reading. TOEIC 4 ky nang va IELTS = du 4 ky nang.
                            </div>
                        </div>
                        <% } %>

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
                                <th>He diem</th>
                                <th>Ky nang</th>
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
                                <td><%= item.getExamFormat() == null ? "--" : item.getExamFormat() %></td>
                                <td><%= item.getSkillSummary() %></td>
                                <td><%= item.getTakenAt() == null ? "--" : item.getTakenAt() %></td>
                                <td>
                                    <a class="btn btn-outline-secondary btn-sm"
                                       href="${pageContext.request.contextPath}<%= dashboardPath %>?mode=input&classID=<%= selectedClassID %>&examCode=<%= item.getExamCode() %>">
                                        Nhap diem / Xem diem
                                    </a>
                                </td>
                            </tr>
                            <% }} else { %>
                            <tr>
                                <td colspan="6" class="text-center app-subtle">Chua co bai test nao cho lop nay.</td>
                            </tr>
                            <% } %>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>

        <% if (selectedExamCode != null && selectedExamResults != null && !selectedExamResults.isEmpty() && selectedTestSummary != null) { %>
        <div class="border rounded p-3 mt-4">
            <div class="d-flex justify-content-between align-items-center mb-3">
                <div>
                    <h2 class="h5 mb-0">Nhap diem cho bai test: <%= selectedExamCode %></h2>
                    <div class="app-subtle">
                        <strong>Loai:</strong> <%= selectedTestSummary.getTestType() == null ? "--" : selectedTestSummary.getTestType() %>
                        |
                        <strong>He diem:</strong> <%= selectedTestSummary.getExamFormat() == null ? "--" : selectedTestSummary.getExamFormat() %>
                        |
                        <strong>Ky nang:</strong> <%= selectedTestSummary.getSkillSummary() %>
                    </div>
                </div>
                <span class="app-subtle">
                    <%= "IELTS".equalsIgnoreCase(selectedTestSummary.getExamFormat())
                            ? "IELTS nhap band score tung ky nang"
                            : "TOEIC nhap so cau dung/phan dung, he thong tu quy doi ra diem TOEIC" %>
                </span>
            </div>
            <form method="post" action="${pageContext.request.contextPath}<%= dashboardPath %>/save-scores">
                <input type="hidden" name="classID" value="<%= selectedClassID %>">
                <input type="hidden" name="examCode" value="<%= selectedExamCode %>">
                <div class="table-responsive">
                    <table class="table table-bordered align-middle">
                        <thead>
                        <tr>
                            <th>Hoc sinh</th>
                            <th>Email</th>
                            <th><%= "IELTS".equalsIgnoreCase(selectedTestSummary.getExamFormat()) ? "Listening" : "Listening (0-100)" %></th>
                            <th><%= "IELTS".equalsIgnoreCase(selectedTestSummary.getExamFormat()) ? "Reading" : "Reading (0-100)" %></th>
                            <th><%= "IELTS".equalsIgnoreCase(selectedTestSummary.getExamFormat()) ? "Speaking" : "Speaking (0-11)" %></th>
                            <th><%= "IELTS".equalsIgnoreCase(selectedTestSummary.getExamFormat()) ? "Writing" : "Writing (0-8)" %></th>
                            <th>Diem tong</th>
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
                            <td style="width: 140px;">
                                <% if (selectedTestSummary.usesListening()) { %>
                                <input type="number" name="listeningScore" class="form-control"
                                       min="0" max="<%= "IELTS".equalsIgnoreCase(selectedTestSummary.getExamFormat()) ? "9" : "100" %>"
                                       step="<%= "IELTS".equalsIgnoreCase(selectedTestSummary.getExamFormat()) ? "0.5" : "1" %>"
                                       value="<%= "IELTS".equalsIgnoreCase(selectedTestSummary.getExamFormat())
                                               ? (result.getListeningScore() == null ? "" : result.getListeningScore())
                                               : (result.getListeningInputValue() == null ? "" : result.getListeningInputValue()) %>">
                                <% } else { %>
                                <input type="text" class="form-control" value="0" readonly>
                                <% } %>
                            </td>
                            <td style="width: 140px;">
                                <% if (selectedTestSummary.usesReading()) { %>
                                <input type="number" name="readingScore" class="form-control"
                                       min="0" max="<%= "IELTS".equalsIgnoreCase(selectedTestSummary.getExamFormat()) ? "9" : "100" %>"
                                       step="<%= "IELTS".equalsIgnoreCase(selectedTestSummary.getExamFormat()) ? "0.5" : "1" %>"
                                       value="<%= "IELTS".equalsIgnoreCase(selectedTestSummary.getExamFormat())
                                               ? (result.getReadingScore() == null ? "" : result.getReadingScore())
                                               : (result.getReadingInputValue() == null ? "" : result.getReadingInputValue()) %>">
                                <% } else { %>
                                <input type="text" class="form-control" value="0" readonly>
                                <% } %>
                            </td>
                            <td style="width: 140px;">
                                <% if (selectedTestSummary.usesSpeaking()) { %>
                                <input type="number" name="speakingScore" class="form-control"
                                       min="0" max="<%= "IELTS".equalsIgnoreCase(selectedTestSummary.getExamFormat()) ? "9" : "11" %>"
                                       step="<%= "IELTS".equalsIgnoreCase(selectedTestSummary.getExamFormat()) ? "0.5" : "1" %>"
                                       value="<%= "IELTS".equalsIgnoreCase(selectedTestSummary.getExamFormat())
                                               ? (result.getSpeakingScore() == null ? "" : result.getSpeakingScore())
                                               : (result.getSpeakingInputValue() == null ? "" : result.getSpeakingInputValue()) %>">
                                <% } else { %>
                                <input type="text" class="form-control" value="0" readonly>
                                <% } %>
                            </td>
                            <td style="width: 140px;">
                                <% if (selectedTestSummary.usesWriting()) { %>
                                <input type="number" name="writingScore" class="form-control"
                                       min="0" max="<%= "IELTS".equalsIgnoreCase(selectedTestSummary.getExamFormat()) ? "9" : "8" %>"
                                       step="<%= "IELTS".equalsIgnoreCase(selectedTestSummary.getExamFormat()) ? "0.5" : "1" %>"
                                       value="<%= "IELTS".equalsIgnoreCase(selectedTestSummary.getExamFormat())
                                               ? (result.getWritingScore() == null ? "" : result.getWritingScore())
                                               : (result.getWritingInputValue() == null ? "" : result.getWritingInputValue()) %>">
                                <% } else { %>
                                <input type="text" class="form-control" value="0" readonly>
                                <% } %>
                            </td>
                            <td><%= result.hasRecordedScore() ? result.getScore() : "Chua du diem" %></td>
                        </tr>
                        <% } %>
                        </tbody>
                    </table>
                </div>
                <button type="submit" class="btn btn-sky">Luu diem</button>
            </form>
        </div>
        <% } %>
        <% } %>

        <% if ("view".equals(mode)) { %>
        <div class="border rounded p-3 mt-4">
            <div class="d-flex justify-content-between align-items-center mb-3">
                <h2 class="h5 mb-0">Bang diem hoc sinh trong lop</h2>
                <span class="app-subtle">Bang diem hien du 4 ky nang va diem tong</span>
            </div>
            <div class="table-responsive">
                <table class="table table-bordered align-middle">
                    <thead>
                    <tr>
                        <th>Hoc sinh</th>
                        <th>Email</th>
                        <th>Bai test</th>
                        <th>He diem</th>
                        <th>Ngay test</th>
                        <th>Listening</th>
                        <th>Reading</th>
                        <th>Speaking</th>
                        <th>Writing</th>
                        <th>Diem tong</th>
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
                        <td><%= result.getExamFormat() == null ? "--" : result.getExamFormat() %></td>
                        <td><%= result.getTakenAt() == null ? "--" : result.getTakenAt() %></td>
                        <td><%= result.getDisplayListeningScore() %></td>
                        <td><%= result.getDisplayReadingScore() %></td>
                        <td><%= result.getDisplaySpeakingScore() %></td>
                        <td><%= result.getDisplayWritingScore() %></td>
                        <td><%= result.hasRecordedScore() ? result.getScore() : "Chua du diem" %></td>
                    </tr>
                    <% }} else { %>
                    <tr>
                        <td colspan="10" class="text-center app-subtle">Chua co du lieu diem cho lop nay.</td>
                    </tr>
                    <% } %>
                    </tbody>
                </table>
            </div>
        </div>
        <% } %>
        <% } else { %>
        <div class="alert alert-info mb-0">
            <%= isTeacherView ? "Giao vien nay chua duoc phan cong lop nao." : "Chua co lop hoc nao trong he thong." %>
        </div>
        <% } %>
    </div>
</div>

<script src="${pageContext.request.contextPath}/assets/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
</body>
</html>
