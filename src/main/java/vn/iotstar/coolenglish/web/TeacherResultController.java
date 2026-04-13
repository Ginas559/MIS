package vn.iotstar.coolenglish.web;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.coolenglish.dao.impl.EnglishClassDAO;
import vn.iotstar.coolenglish.dao.impl.EnrollmentDAO;
import vn.iotstar.coolenglish.dao.impl.ExamResultDAO;
import vn.iotstar.coolenglish.dao.impl.PersonDAO;
import vn.iotstar.coolenglish.entity.EnglishClass;
import vn.iotstar.coolenglish.entity.Enrollment;
import vn.iotstar.coolenglish.entity.ExamResult;
import vn.iotstar.coolenglish.entity.Person;
import vn.iotstar.coolenglish.entity.Teacher;
import vn.iotstar.coolenglish.entity.UserAccount;
import vn.iotstar.coolenglish.enums.UserRole;

@WebServlet(urlPatterns = {
        "/teacher/results",
        "/teacher/results/create-test",
        "/teacher/results/save-scores"
})
public class TeacherResultController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final EnglishClassDAO englishClassDAO = new EnglishClassDAO();
    private final EnrollmentDAO enrollmentDAO = new EnrollmentDAO();
    private final ExamResultDAO examResultDAO = new ExamResultDAO();
    private final PersonDAO personDAO = new PersonDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserAccount teacherAccount = resolveTeacher(req, resp);
        if (teacherAccount == null) {
            return;
        }
        showDashboard(req, resp, teacherAccount);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        UserAccount teacherAccount = resolveTeacher(req, resp);
        if (teacherAccount == null) {
            return;
        }

        String servletPath = req.getServletPath();
        if (servletPath.endsWith("/create-test")) {
            try {
                createTest(req, resp, teacherAccount);
            } catch (IllegalArgumentException e) {
                redirectWithError(req, resp, trim(req.getParameter("classID")), null, e.getMessage());
            }
            return;
        }
        if (servletPath.endsWith("/save-scores")) {
            try {
                saveScores(req, resp, teacherAccount);
            } catch (IllegalArgumentException e) {
                redirectWithError(req, resp, trim(req.getParameter("classID")), trim(req.getParameter("examCode")),
                        e.getMessage());
            }
            return;
        }
        showDashboard(req, resp, teacherAccount);
    }

    private void showDashboard(HttpServletRequest req, HttpServletResponse resp, UserAccount teacherAccount)
            throws ServletException, IOException {
        Long teacherPersonId = resolveTeacherPersonId(teacherAccount);
        List<EnglishClass> teachingClasses = teacherPersonId == null
                ? List.of()
                : englishClassDAO.findByTeacherID(teacherPersonId);
        req.setAttribute("classes", teachingClasses);
        req.setAttribute("message", resolveMessage(req.getParameter("msg")));
        req.setAttribute("error", req.getParameter("error"));
        String mode = trim(req.getParameter("mode"));
        if (mode == null || (!"input".equals(mode) && !"view".equals(mode))) {
            mode = "input";
        }
        req.setAttribute("mode", mode);

        String selectedClassID = trim(req.getParameter("classID"));
        EnglishClass selectedClass = findSelectedClass(teachingClasses, selectedClassID);
        if (selectedClass == null && !teachingClasses.isEmpty()) {
            selectedClass = teachingClasses.get(0);
            selectedClassID = selectedClass.getClassID();
        }

        req.setAttribute("selectedClass", selectedClass);
        req.setAttribute("selectedClassID", selectedClassID);

        List<Enrollment> enrollments = selectedClassID == null ? List.of() : enrollmentDAO.findByClassID(selectedClassID);
        List<ExamResult> classResults = selectedClassID == null ? List.of() : examResultDAO.findByClassID(selectedClassID);
        req.setAttribute("enrollments", enrollments);
        req.setAttribute("classResults", classResults);
        req.setAttribute("testSummaries", buildTestSummaries(classResults));

        String selectedExamCode = trim(req.getParameter("examCode"));
        if (selectedExamCode == null && !classResults.isEmpty()) {
            selectedExamCode = classResults.get(0).getExamCode();
        }

        req.setAttribute("selectedExamCode", selectedExamCode);
        req.setAttribute("selectedExamResults",
                selectedClassID == null || selectedExamCode == null
                        ? List.of()
                        : examResultDAO.findByClassIDAndExamCode(selectedClassID, selectedExamCode));

        RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/teacher/result-dashboard.jsp");
        dispatcher.forward(req, resp);
    }

    private void createTest(HttpServletRequest req, HttpServletResponse resp, UserAccount teacherAccount)
            throws IOException {
        String classID = trim(req.getParameter("classID"));
        ensureTeacherOwnsClass(teacherAccount, classID, resp, req);
        if (resp.isCommitted()) {
            return;
        }

        List<Enrollment> enrollments = enrollmentDAO.findByClassID(classID);
        if (enrollments.isEmpty()) {
            redirectWithError(req, resp, classID, null, "Lop hoc chua co hoc sinh dang ghi danh");
            return;
        }

        String testType = trim(req.getParameter("testType"));
        if (testType == null) {
            redirectWithError(req, resp, classID, null, "Loai bai test khong duoc de trong");
            return;
        }

        LocalDate takenAt = parseDate(req.getParameter("takenAt"));
        if (takenAt == null) {
            takenAt = LocalDate.now();
        }

        String examCode = buildExamCode(classID, testType, takenAt);
        if (examResultDAO.existsByClassIDAndExamCode(classID, examCode)) {
            redirectWithError(req, resp, classID, examCode, "Bai test nay da ton tai trong lop");
            return;
        }

        List<ExamResult> results = new ArrayList<>();
        for (Enrollment enrollment : enrollments) {
            ExamResult result = new ExamResult();
            result.setPartnerCode("INTERNAL");
            result.setClassID(classID);
            result.setTeacherID(resolveTeacherPersonId(teacherAccount));
            result.setStudentEmail(enrollment.getStudent().getEmail());
            result.setExamCode(examCode);
            result.setTestType(testType);
            result.setTakenAt(takenAt);
            result.setScore(-1d);
            results.add(result);
        }

        examResultDAO.insertBatch(results);
        resp.sendRedirect(req.getContextPath() + "/teacher/results?mode=input&classID=" + classID + "&examCode="
                + examCode + "&msg=test_created");
    }

    private void saveScores(HttpServletRequest req, HttpServletResponse resp, UserAccount teacherAccount)
            throws IOException {
        String classID = trim(req.getParameter("classID"));
        String examCode = trim(req.getParameter("examCode"));
        ensureTeacherOwnsClass(teacherAccount, classID, resp, req);
        if (resp.isCommitted()) {
            return;
        }

        if (examCode == null) {
            redirectWithError(req, resp, classID, null, "Chon bai test truoc khi luu diem");
            return;
        }

        String[] studentEmails = req.getParameterValues("studentEmail");
        String[] scores = req.getParameterValues("score");
        if (studentEmails == null || scores == null || studentEmails.length != scores.length) {
            redirectWithError(req, resp, classID, examCode, "Du lieu diem khong hop le");
            return;
        }

        List<ExamResult> existingResults = examResultDAO.findByClassIDAndExamCode(classID, examCode);
        String testType = existingResults.isEmpty() ? "Teacher Test" : existingResults.get(0).getTestType();
        LocalDate takenAt = existingResults.isEmpty() || existingResults.get(0).getTakenAt() == null
                ? LocalDate.now()
                : existingResults.get(0).getTakenAt();

        for (int i = 0; i < studentEmails.length; i++) {
            String studentEmail = trim(studentEmails[i]);
            Double score = parseScore(scores[i]);
            ExamResult result = examResultDAO.findByClassIDAndStudentEmailAndExamCode(classID, studentEmail, examCode);
            if (result == null) {
                result = new ExamResult();
                result.setPartnerCode("INTERNAL");
                result.setClassID(classID);
                result.setTeacherID(resolveTeacherPersonId(teacherAccount));
                result.setStudentEmail(studentEmail);
                result.setExamCode(examCode);
                result.setTestType(testType);
                result.setTakenAt(takenAt);
                result.setScore(score);
                examResultDAO.insert(result);
            } else {
                result.setScore(score);
                examResultDAO.update(result);
            }
        }

        resp.sendRedirect(req.getContextPath() + "/teacher/results?mode=input&classID=" + classID + "&examCode="
                + examCode + "&msg=scores_saved");
    }

    private void ensureTeacherOwnsClass(UserAccount teacherAccount, String classID, HttpServletResponse resp,
            HttpServletRequest req) throws IOException {
        if (classID == null) {
            redirectWithError(req, resp, null, null, "Chua chon lop hoc");
            return;
        }

        EnglishClass englishClass = englishClassDAO.findByClassID(classID);
        Long teacherPersonId = resolveTeacherPersonId(teacherAccount);
        if (englishClass == null || teacherPersonId == null || !teacherPersonId.equals(englishClass.getTeacherID())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Ban khong co quyen quan ly lop hoc nay");
        }
    }

    private Long resolveTeacherPersonId(UserAccount teacherAccount) {
        if (teacherAccount.getRelatedID() != null) {
            return teacherAccount.getRelatedID();
        }

        Person person = personDAO.findByEmail(teacherAccount.getEmail());
        if (person instanceof Teacher teacher) {
            return teacher.getId();
        }
        return null;
    }

    private UserAccount resolveTeacher(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp?msg=require_login");
            return null;
        }

        Object currentUser = session.getAttribute("user");
        if (!(currentUser instanceof UserAccount user) || user.getRole() != UserRole.TEACHER) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp?msg=forbidden");
            return null;
        }
        return user;
    }

    private EnglishClass findSelectedClass(List<EnglishClass> classes, String classID) {
        if (classID == null) {
            return null;
        }
        for (EnglishClass englishClass : classes) {
            if (classID.equals(englishClass.getClassID())) {
                return englishClass;
            }
        }
        return null;
    }

    private List<ExamResult> buildTestSummaries(List<ExamResult> classResults) {
        Map<String, ExamResult> summaries = new LinkedHashMap<>();
        for (ExamResult result : classResults) {
            summaries.putIfAbsent(result.getExamCode(), result);
        }
        return new ArrayList<>(summaries.values());
    }

    private String buildExamCode(String classID, String testType, LocalDate takenAt) {
        String normalized = testType.toUpperCase().replaceAll("[^A-Z0-9]+", "_").replaceAll("^_+|_+$", "");
        if (normalized.isBlank()) {
            normalized = "TEST";
        }
        return classID + "_" + normalized + "_" + takenAt;
    }

    private LocalDate parseDate(String value) {
        String trimmed = trim(value);
        if (trimmed == null) {
            return null;
        }
        return LocalDate.parse(trimmed);
    }

    private Double parseScore(String value) {
        String trimmed = trim(value);
        if (trimmed == null) {
            return null;
        }
        Double score = Double.valueOf(trimmed);
        if (score < 0d || score > 10d) {
            throw new IllegalArgumentException("Diem phai nam trong khoang 0 den 10");
        }
        return score;
    }

    private void redirectWithError(HttpServletRequest req, HttpServletResponse resp, String classID, String examCode,
            String error) throws IOException {
        StringBuilder target = new StringBuilder(req.getContextPath()).append("/teacher/results");
        boolean hasQuery = false;
        String mode = trim(req.getParameter("mode"));
        if (mode != null) {
            target.append("?mode=").append(mode);
            hasQuery = true;
        }
        if (classID != null) {
            target.append(hasQuery ? "&" : "?").append("classID=").append(classID);
            hasQuery = true;
        }
        if (examCode != null) {
            target.append(hasQuery ? "&" : "?").append("examCode=").append(examCode);
            hasQuery = true;
        }
        target.append(hasQuery ? "&" : "?")
                .append("error=")
                .append(URLEncoder.encode(error, StandardCharsets.UTF_8));
        resp.sendRedirect(target.toString());
    }

    private String resolveMessage(String msg) {
        if ("login_success".equals(msg)) {
            return "Dang nhap thanh cong voi vai tro giao vien";
        }
        if ("test_created".equals(msg)) {
            return "Da tao bai test cho lop hoc va san sang nhap diem";
        }
        if ("scores_saved".equals(msg)) {
            return "Da luu diem va tu dong xep loai";
        }
        return null;
    }

    private String trim(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
