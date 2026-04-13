<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="vn.iotstar.coolenglish.entity.Course" %>
<%@ page import="vn.iotstar.coolenglish.entity.Enrollment" %>
<%@ page import="vn.iotstar.coolenglish.entity.EnglishClass" %>
<%@ page import="vn.iotstar.coolenglish.entity.ExamResult" %>
<%@ page import="vn.iotstar.coolenglish.entity.Room" %>
<%@ page import="vn.iotstar.coolenglish.entity.Schedule" %>
<%@ page import="vn.iotstar.coolenglish.entity.Session" %>
<%@ page import="vn.iotstar.coolenglish.entity.Student" %>
<%@ page import="vn.iotstar.coolenglish.entity.Teacher" %>
<%
    Student student = (Student) request.getAttribute("student");
    List<Enrollment> enrollments = (List<Enrollment>) request.getAttribute("enrollments");
    List<ExamResult> examResults = (List<ExamResult>) request.getAttribute("examResults");
    Map<String, List<ExamResult>> resultsByClassID = (Map<String, List<ExamResult>>) request.getAttribute("resultsByClassID");
    String message = (String) request.getAttribute("message");
    Integer totalClasses = (Integer) request.getAttribute("totalClasses");
    Integer gradedTests = (Integer) request.getAttribute("gradedTests");
    String studentName = student != null && student.getFullName() != null ? student.getFullName() : "Hoc vien";
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>CoolEnglish - Lich hoc va ket qua cua toi</title>
</head>
<body>
<div class="container py-4">
    <div class="app-section list-card">
        <div class="d-flex flex-column flex-lg-row justify-content-between align-items-lg-center gap-3 mb-4">
            <div>
                <h1 class="h3 app-title mb-1">Khong gian hoc tap ca nhan</h1>
                <p class="app-subtle mb-0">Xin chao <strong><%= studentName %></strong>, ban co the xem lich hoc cua cac lop da ghi danh va ket qua cac bai test giao vien da giao.</p>
            </div>
            <div class="d-flex gap-3 flex-wrap">
                <div class="app-kpi">
                    <div class="app-subtle">Lop da ghi danh</div>
                    <div class="app-kpi-value"><%= totalClasses == null ? 0 : totalClasses %></div>
                </div>
                <div class="app-kpi">
                    <div class="app-subtle">Bai test da co diem</div>
                    <div class="app-kpi-value"><%= gradedTests == null ? 0 : gradedTests %></div>
                </div>
            </div>
        </div>

        <% if (message != null) { %>
        <div class="alert alert-success"><%= message %></div>
        <% } %>

        <section id="schedule-section" class="app-anchor-offset mb-4">
            <div class="d-flex justify-content-between align-items-center mb-3">
                <div>
                    <h2 class="h4 mb-1">Lich hoc da ghi danh</h2>
                    <p class="app-subtle mb-0">Thong tin lop hoc, khoa hoc, phong hoc va tung buoi hoc cua ban.</p>
                </div>
            </div>

            <% if (enrollments == null || enrollments.isEmpty()) { %>
            <div class="alert alert-info mb-0">Ban chua ghi danh lop hoc nao.</div>
            <% } else { %>
            <div class="app-grid">
                <% for (Enrollment enrollment : enrollments) {
                    EnglishClass englishClass = enrollment.getEnglishClass();
                    Course course = englishClass != null ? englishClass.getCourse() : null;
                    Room room = englishClass != null ? englishClass.getRoom() : null;
                    Teacher teacher = englishClass != null ? englishClass.getTeacher() : null;
                    Schedule schedule = englishClass != null ? englishClass.getSchedule() : null;
                    List<ExamResult> classResults = resultsByClassID == null || englishClass == null
                            ? null
                            : resultsByClassID.get(englishClass.getClassID());
                %>
                <div class="border rounded-3 p-4 bg-white">
                    <div class="row g-4">
                        <div class="col-lg-5">
                            <h3 class="h5 mb-3"><%= englishClass == null ? "Lop hoc" : englishClass.getClassName() %></h3>
                            <div class="small">
                                <p class="mb-2"><strong>Ma lop:</strong> <%= englishClass == null ? "--" : englishClass.getClassID() %></p>
                                <p class="mb-2"><strong>Khoa hoc:</strong> <%= course == null ? "--" : course.getCourseName() %></p>
                                <p class="mb-2"><strong>Trinh do:</strong> <%= course == null || course.getLevel() == null ? "--" : course.getLevel() %></p>
                                <p class="mb-2"><strong>Thoi gian khoa hoc:</strong>
                                    <%= englishClass == null || englishClass.getStartDate() == null ? "--" : englishClass.getStartDate() %>
                                    -
                                    <%= englishClass == null || englishClass.getEndDate() == null ? "--" : englishClass.getEndDate() %>
                                </p>
                                <p class="mb-2"><strong>Phong hoc:</strong>
                                    <%= room == null ? "--" : room.getRoomName() %>
                                    <% if (room != null && room.getLocation() != null) { %>
                                        (<%= room.getLocation() %>)
                                    <% } %>
                                </p>
                                <p class="mb-2"><strong>Giao vien:</strong> <%= teacher == null ? "--" : teacher.getFullName() %></p>
                                <p class="mb-0"><strong>Trang thai ghi danh:</strong> <span class="status-pill"><%= enrollment.getStatus() %></span></p>
                            </div>
                        </div>
                        <div class="col-lg-7">
                            <div class="d-flex justify-content-between align-items-center mb-2">
                                <h4 class="h6 mb-0">Chi tiet buoi hoc</h4>
                                <span class="app-subtle"><%= schedule == null || schedule.getSessions() == null ? 0 : schedule.getSessions().size() %> buoi</span>
                            </div>
                            <div class="table-responsive">
                                <table class="table table-bordered align-middle mb-0">
                                    <thead>
                                    <tr>
                                        <th>Buoi hoc</th>
                                        <th>Ngay</th>
                                        <th>Gio hoc</th>
                                        <th>Phong</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <% if (schedule != null && schedule.getSessions() != null && !schedule.getSessions().isEmpty()) {
                                        for (Session sessionItem : schedule.getSessions()) { %>
                                    <tr>
                                        <td><%= sessionItem.getSessionName() == null ? sessionItem.getSessionID() : sessionItem.getSessionName() %></td>
                                        <td><%= sessionItem.getSessionDate() == null ? "--" : sessionItem.getSessionDate() %></td>
                                        <td>
                                            <%= sessionItem.getStartTime() == null ? "--" : sessionItem.getStartTime() %>
                                            -
                                            <%= sessionItem.getEndTime() == null ? "--" : sessionItem.getEndTime() %>
                                        </td>
                                        <td><%= sessionItem.getRoom() == null ? (room == null ? "--" : room.getRoomName()) : sessionItem.getRoom().getRoomName() %></td>
                                    </tr>
                                    <% }} else { %>
                                    <tr>
                                        <td colspan="4" class="text-center app-subtle">Lop nay chua co lich hoc chi tiet.</td>
                                    </tr>
                                    <% } %>
                                    </tbody>
                                </table>
                            </div>

                            <div class="mt-3 small">
                                <strong>So bai test cua lop:</strong>
                                <%= classResults == null ? 0 : classResults.size() %>
                            </div>
                        </div>
                    </div>
                </div>
                <% } %>
            </div>
            <% } %>
        </section>

        <section id="result-section" class="app-anchor-offset">
            <div class="d-flex justify-content-between align-items-center mb-3">
                <div>
                    <h2 class="h4 mb-1">Ket qua bai test</h2>
                    <p class="app-subtle mb-0">Danh sach diem cac bai test ma giao vien da giao cho ban.</p>
                </div>
            </div>

            <div class="table-responsive">
                <table class="table table-bordered align-middle mb-0">
                    <thead>
                    <tr>
                        <th>Lop hoc</th>
                        <th>Ma bai test</th>
                        <th>Loai bai test</th>
                        <th>He diem</th>
                        <th>Ngay test</th>
                        <th>Listening</th>
                        <th>Reading</th>
                        <th>Speaking</th>
                        <th>Writing</th>
                        <th>Diem tong</th>
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
                        <td><%= className == null ? "--" : className %></td>
                        <td><%= result.getExamCode() == null ? "--" : result.getExamCode() %></td>
                        <td><%= result.getTestType() == null ? "--" : result.getTestType() %></td>
                        <td><%= result.getExamFormat() == null ? "--" : result.getExamFormat() %></td>
                        <td><%= result.getTakenAt() == null ? "--" : result.getTakenAt() %></td>
                        <td><%= result.getDisplayListeningScore() %></td>
                        <td><%= result.getDisplayReadingScore() %></td>
                        <td><%= result.getDisplaySpeakingScore() %></td>
                        <td><%= result.getDisplayWritingScore() %></td>
                        <td><%= result.hasRecordedScore() ? result.getScore() : "--" %></td>
                        <td>
                            <span class="status-pill"><%= result.hasRecordedScore() ? "Da co diem" : "Dang cho giao vien nhap diem" %></span>
                        </td>
                    </tr>
                    <% }} else { %>
                    <tr>
                        <td colspan="10" class="text-center app-subtle">Chua co bai test nao duoc giao cho ban.</td>
                    </tr>
                    <% } %>
                    </tbody>
                </table>
            </div>
        </section>
    </div>
</div>
</body>
</html>
