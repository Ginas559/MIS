package vn.iotstar.coolenglish.web;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
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
import vn.iotstar.coolenglish.enums.GradingSystem;
import vn.iotstar.coolenglish.enums.SkillType;
import vn.iotstar.coolenglish.enums.UserRole;
import vn.iotstar.coolenglish.service.ResultSubject;

@WebServlet(urlPatterns = {
        "/teacher/results",
        "/teacher/results/create-test",
        "/teacher/results/save-scores",
        "/staff/results",
        "/staff/results/create-test",
        "/staff/results/save-scores"
})
public class TeacherResultController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final String TEST_TOEIC_2_SKILLS = "TOEIC 2 ky nang";
    private static final String TEST_TOEIC_4_SKILLS = "TOEIC 4 ky nang";
    private static final String TEST_IELTS = GradingSystem.IELTS.name();

    private final EnglishClassDAO englishClassDAO = new EnglishClassDAO();
    private final EnrollmentDAO enrollmentDAO = new EnrollmentDAO();
    private final ExamResultDAO examResultDAO = new ExamResultDAO();
    private final PersonDAO personDAO = new PersonDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserAccount actor = resolveActor(req, resp);
        if (actor == null) {
            return;
        }
        showDashboard(req, resp, actor);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        UserAccount actor = resolveActor(req, resp);
        if (actor == null) {
            return;
        }

        String servletPath = req.getServletPath();
        if (servletPath.endsWith("/create-test")) {
            try {
                createTest(req, resp, actor);
            } catch (IllegalArgumentException e) {
                redirectWithError(req, resp, actor, trim(req.getParameter("classID")), null, e.getMessage());
            }
            return;
        }
        if (servletPath.endsWith("/save-scores")) {
            try {
                saveScores(req, resp, actor);
            } catch (IllegalArgumentException e) {
                redirectWithError(req, resp, actor, trim(req.getParameter("classID")), trim(req.getParameter("examCode")),
                        e.getMessage());
            }
            return;
        }
        showDashboard(req, resp, actor);
    }

    private void showDashboard(HttpServletRequest req, HttpServletResponse resp, UserAccount actor)
            throws ServletException, IOException {
        boolean teacherView = actor.getRole() == UserRole.TEACHER;
        List<EnglishClass> classes = teacherView
                ? findTeacherClasses(actor)
                : englishClassDAO.findAllWithDetails();

        req.setAttribute("classes", classes);
        req.setAttribute("message", resolveMessage(req.getParameter("msg")));
        req.setAttribute("error", req.getParameter("error"));
        req.setAttribute("isTeacherView", teacherView);
        req.setAttribute("isStaffView", !teacherView);
        req.setAttribute("dashboardPath", resolveDashboardPath(actor));
        req.setAttribute("gradingSystems", GradingSystem.values());
        req.setAttribute("skillTypes", SkillType.values());

        String mode = trim(req.getParameter("mode"));
        if (mode == null || (!"input".equals(mode) && !"view".equals(mode))) {
            mode = "input";
        }
        req.setAttribute("mode", mode);

        String selectedClassID = trim(req.getParameter("classID"));
        EnglishClass selectedClass = findSelectedClass(classes, selectedClassID);
        if (selectedClass == null && !classes.isEmpty()) {
            selectedClass = classes.get(0);
            selectedClassID = selectedClass.getClassID();
        }

        req.setAttribute("selectedClass", selectedClass);
        req.setAttribute("selectedClassID", selectedClassID);

        List<Enrollment> enrollments = selectedClassID == null ? List.of() : enrollmentDAO.findByClassID(selectedClassID);
        List<ExamResult> classResults = selectedClassID == null ? List.of() : examResultDAO.findByClassID(selectedClassID);
        List<ExamResult> testSummaries = buildTestSummaries(classResults);
        req.setAttribute("enrollments", enrollments);
        req.setAttribute("classResults", classResults);
        req.setAttribute("testSummaries", testSummaries);

        String selectedExamCode = trim(req.getParameter("examCode"));
        if (selectedExamCode == null && !testSummaries.isEmpty()) {
            selectedExamCode = testSummaries.get(0).getExamCode();
        }

        List<ExamResult> selectedExamResults = selectedClassID == null || selectedExamCode == null
                ? List.of()
                : examResultDAO.findByClassIDAndExamCode(selectedClassID, selectedExamCode);
        req.setAttribute("selectedExamCode", selectedExamCode);
        req.setAttribute("selectedExamResults", selectedExamResults);
        req.setAttribute("selectedTestSummary", selectedExamResults.isEmpty() ? null : selectedExamResults.get(0));

        RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/teacher/result-dashboard.jsp");
        dispatcher.forward(req, resp);
    }

    private void createTest(HttpServletRequest req, HttpServletResponse resp, UserAccount actor)
            throws IOException {
        String classID = trim(req.getParameter("classID"));
        ensureActorCanManageClass(actor, classID, resp, req);
        if (resp.isCommitted()) {
            return;
        }

        List<Enrollment> enrollments = enrollmentDAO.findByClassID(classID);
        if (enrollments.isEmpty()) {
            redirectWithError(req, resp, actor, classID, null, "Lop hoc chua co hoc sinh dang ghi danh");
            return;
        }

        String testType = resolveSubmittedTestType(req, actor);
        String examFormat = resolveExamFormat(req, actor, testType);
        SkillSelection skillSelection = resolveSkillSelection(req, actor, testType);
        LocalDate takenAt = parseDate(req.getParameter("takenAt"));
        if (takenAt == null) {
            takenAt = LocalDate.now();
        }

        String examCode = buildExamCode(classID, testType, examFormat, takenAt);
        if (examResultDAO.existsByClassIDAndExamCode(classID, examCode)) {
            redirectWithError(req, resp, actor, classID, examCode, "Bai test nay da ton tai trong lop");
            return;
        }

        List<ExamResult> results = new ArrayList<>();
        for (Enrollment enrollment : enrollments) {
            ExamResult result = new ExamResult();
            result.setPartnerCode("INTERNAL");
            result.setClassID(classID);
            result.setTeacherID(resolveTeacherPersonId(actor));
            result.setStudentEmail(enrollment.getStudent().getEmail());
            result.setExamCode(examCode);
            result.setTestType(testType);
            result.setExamFormat(examFormat);
            result.setHasListening(skillSelection.listening());
            result.setHasReading(skillSelection.reading());
            result.setHasSpeaking(skillSelection.speaking());
            result.setHasWriting(skillSelection.writing());
            result.setTakenAt(takenAt);
            results.add(result);
        }

        examResultDAO.insertBatch(results);
        resp.sendRedirect(req.getContextPath() + buildDashboardTarget(actor, classID, examCode, "test_created"));
    }

    private void saveScores(HttpServletRequest req, HttpServletResponse resp, UserAccount actor)
            throws IOException {
        String classID = trim(req.getParameter("classID"));
        String examCode = trim(req.getParameter("examCode"));
        ensureActorCanManageClass(actor, classID, resp, req);
        if (resp.isCommitted()) {
            return;
        }

        if (examCode == null) {
            redirectWithError(req, resp, actor, classID, null, "Chon bai test truoc khi luu diem");
            return;
        }

        String[] studentEmails = req.getParameterValues("studentEmail");
        if (studentEmails == null || studentEmails.length == 0) {
            redirectWithError(req, resp, actor, classID, examCode, "Khong tim thay danh sach hoc vien");
            return;
        }

        List<ExamResult> existingResults = examResultDAO.findByClassIDAndExamCode(classID, examCode);
        if (existingResults.isEmpty()) {
            redirectWithError(req, resp, actor, classID, examCode, "Khong tim thay bai test can nhap diem");
            return;
        }

        ExamResult template = existingResults.get(0);
        Map<String, ExamResult> existingResultMap = new HashMap<>();
        for (ExamResult existingResult : existingResults) {
            existingResultMap.put(normalizeEmailKey(existingResult.getStudentEmail()), existingResult);
        }
        String[] listeningScores = req.getParameterValues("listeningScore");
        String[] readingScores = req.getParameterValues("readingScore");
        String[] speakingScores = req.getParameterValues("speakingScore");
        String[] writingScores = req.getParameterValues("writingScore");

        validateArrayLength(template.usesListening(), studentEmails, listeningScores, "Listening");
        validateArrayLength(template.usesReading(), studentEmails, readingScores, "Reading");
        validateArrayLength(template.usesSpeaking(), studentEmails, speakingScores, "Speaking");
        validateArrayLength(template.usesWriting(), studentEmails, writingScores, "Writing");

        List<ExamResult> resultsToSave = new ArrayList<>();
        for (int i = 0; i < studentEmails.length; i++) {
            String studentEmail = trim(studentEmails[i]);
            ExamResult result = existingResultMap.get(normalizeEmailKey(studentEmail));
            if (result == null) {
                result = new ExamResult();
                result.setPartnerCode("INTERNAL");
                result.setClassID(classID);
                result.setTeacherID(resolveTeacherPersonId(actor));
                result.setStudentEmail(studentEmail);
                result.setExamCode(examCode);
                result.setTestType(template.getTestType());
                result.setExamFormat(template.getExamFormat());
                result.setHasListening(template.getHasListening());
                result.setHasReading(template.getHasReading());
                result.setHasSpeaking(template.getHasSpeaking());
                result.setHasWriting(template.getHasWriting());
                result.setTakenAt(template.getTakenAt());
            }

            applySkillScores(result, template.getExamFormat(), template,
                    template.usesListening() ? listeningScores[i] : null,
                    template.usesReading() ? readingScores[i] : null,
                    template.usesSpeaking() ? speakingScores[i] : null,
                    template.usesWriting() ? writingScores[i] : null);
            resultsToSave.add(result);
        }

        examResultDAO.saveBatch(resultsToSave);

        for (ExamResult result : resultsToSave) {
            ResultSubject.getInstance().notifyObservers(result);
        }

        resp.sendRedirect(req.getContextPath() + buildDashboardTarget(actor, classID, examCode, "scores_saved"));
    }

    private void applySkillScores(ExamResult result, String examFormat, ExamResult template,
            String listeningValue, String readingValue, String speakingValue, String writingValue) {
        result.setExamFormat(examFormat);
        result.setHasListening(template.getHasListening());
        result.setHasReading(template.getHasReading());
        result.setHasSpeaking(template.getHasSpeaking());
        result.setHasWriting(template.getHasWriting());
        if (GradingSystem.IELTS.name().equalsIgnoreCase(examFormat)) {
            result.setListeningRaw(null);
            result.setReadingRaw(null);
            result.setSpeakingRaw(null);
            result.setWritingRaw(null);
            result.setListeningScore(template.usesListening() ? parseIeltsBand(listeningValue, "Listening") : null);
            result.setReadingScore(template.usesReading() ? parseIeltsBand(readingValue, "Reading") : null);
            result.setSpeakingScore(template.usesSpeaking() ? parseIeltsBand(speakingValue, "Speaking") : null);
            result.setWritingScore(template.usesWriting() ? parseIeltsBand(writingValue, "Writing") : null);
            return;
        }

        result.setListeningScore(null);
        result.setReadingScore(null);
        result.setSpeakingScore(null);
        result.setWritingScore(null);
        result.setListeningRaw(template.usesListening() ? parseToeicRaw(listeningValue, "Listening") : null);
        result.setReadingRaw(template.usesReading() ? parseToeicRaw(readingValue, "Reading") : null);
        result.setSpeakingRaw(template.usesSpeaking() ? parseToeicRaw(speakingValue, "Speaking") : null);
        result.setWritingRaw(template.usesWriting() ? parseToeicRaw(writingValue, "Writing") : null);
    }

    private void ensureActorCanManageClass(UserAccount actor, String classID, HttpServletResponse resp,
            HttpServletRequest req) throws IOException {
        if (classID == null) {
            redirectWithError(req, resp, actor, null, null, "Chua chon lop hoc");
            return;
        }

        if (actor.getRole() != UserRole.TEACHER) {
            return;
        }

        EnglishClass englishClass = englishClassDAO.findByClassID(classID);
        Long teacherPersonId = resolveTeacherPersonId(actor);
        if (englishClass == null || teacherPersonId == null || englishClass.getTeacher() == null
                || !teacherPersonId.equals(englishClass.getTeacher().getId())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Ban khong co quyen quan ly lop hoc nay");
        }
    }

    private Long resolveTeacherPersonId(UserAccount actor) {
        if (actor.getRelatedID() != null) {
            return actor.getRelatedID();
        }

        Person person = personDAO.findByEmail(actor.getEmail());
        if (person instanceof Teacher teacher) {
            return teacher.getId();
        }
        return null;
    }

    private UserAccount resolveActor(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp?msg=require_login");
            return null;
        }

        Object currentUser = session.getAttribute("user");
        if (!(currentUser instanceof UserAccount user)) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp?msg=forbidden");
            return null;
        }

        if (user.getRole() != UserRole.TEACHER && user.getRole() != UserRole.STAFF && user.getRole() != UserRole.ADMIN) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp?msg=forbidden");
            return null;
        }
        return user;
    }

    private List<EnglishClass> findTeacherClasses(UserAccount actor) {
        Long teacherPersonId = resolveTeacherPersonId(actor);
        if (teacherPersonId == null) {
            return List.of();
        }
        return englishClassDAO.findByTeacherID(teacherPersonId);
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

    private String resolveSubmittedTestType(HttpServletRequest req, UserAccount actor) {
        String testType = trim(req.getParameter("testType"));
        if (testType == null) {
            throw new IllegalArgumentException("Loai bai test khong duoc de trong");
        }
        if (actor.getRole() != UserRole.TEACHER) {
            if (!TEST_TOEIC_2_SKILLS.equals(testType) && !TEST_TOEIC_4_SKILLS.equals(testType) && !TEST_IELTS.equals(testType)) {
                throw new IllegalArgumentException("Staff chi duoc tao bai TOEIC 2 ky nang, TOEIC 4 ky nang hoac IELTS");
            }
        }
        if (actor.getRole() == UserRole.TEACHER && testType.length() > 100) {
            throw new IllegalArgumentException("Ten bai test khong duoc vuot qua 100 ky tu");
        }
        return testType;
    }

    private String resolveExamFormat(HttpServletRequest req, UserAccount actor, String testType) {
        if (actor.getRole() != UserRole.TEACHER) {
            return TEST_IELTS.equals(testType) ? GradingSystem.IELTS.name() : GradingSystem.TOEIC.name();
        }
        return normalizeExamFormat(req.getParameter("examFormat"));
    }

    private String normalizeExamFormat(String value) {
        String examFormat = trim(value);
        if (examFormat == null) {
            throw new IllegalArgumentException("Chon he diem TOEIC hoac IELTS cho bai test");
        }

        if (GradingSystem.TOEIC.name().equalsIgnoreCase(examFormat)) {
            return GradingSystem.TOEIC.name();
        }
        if (GradingSystem.IELTS.name().equalsIgnoreCase(examFormat)) {
            return GradingSystem.IELTS.name();
        }
        throw new IllegalArgumentException("He diem bai test khong hop le");
    }

    private SkillSelection resolveSkillSelection(HttpServletRequest req, UserAccount actor, String testType) {
        if (actor.getRole() != UserRole.TEACHER) {
            if (TEST_TOEIC_2_SKILLS.equals(testType)) {
                return new SkillSelection(true, true, false, false);
            }
            if (TEST_TOEIC_4_SKILLS.equals(testType) || TEST_IELTS.equals(testType)) {
                return new SkillSelection(true, true, true, true);
            }
        }
        boolean listening = req.getParameter(toSkillParameterName(SkillType.LISTENING)) != null;
        boolean reading = req.getParameter(toSkillParameterName(SkillType.READING)) != null;
        boolean speaking = req.getParameter(toSkillParameterName(SkillType.SPEAKING)) != null;
        boolean writing = req.getParameter(toSkillParameterName(SkillType.WRITING)) != null;
        if (!listening && !reading && !speaking && !writing) {
            throw new IllegalArgumentException("Can chon it nhat mot ky nang cho bai test");
        }
        return new SkillSelection(listening, reading, speaking, writing);
    }

    private String buildExamCode(String classID, String testType, String examFormat, LocalDate takenAt) {
        String normalizedType = testType.toUpperCase().replaceAll("[^A-Z0-9]+", "_").replaceAll("^_+|_+$", "");
        String normalizedFormat = examFormat.toUpperCase().replaceAll("[^A-Z0-9]+", "_");
        if (normalizedType.isBlank()) {
            normalizedType = "TEST";
        }
        return classID + "_" + normalizedFormat + "_" + normalizedType + "_" + takenAt;
    }

    private LocalDate parseDate(String value) {
        String trimmed = trim(value);
        if (trimmed == null) {
            return null;
        }
        return LocalDate.parse(trimmed);
    }

    private Double parseIeltsBand(String value, String skillName) {
        String trimmed = trim(value);
        if (trimmed == null) {
            return null;
        }

        Double score = Double.valueOf(trimmed);
        if (score < 0d || score > 9d) {
            throw new IllegalArgumentException(skillName + " cua IELTS phai nam trong khoang 0 den 9");
        }
        double remainder = score * 2d - Math.floor(score * 2d);
        if (remainder > 0d) {
            throw new IllegalArgumentException(skillName + " cua IELTS chi nhan buoc 0.5");
        }
        return score;
    }

    private Integer parseToeicRaw(String value, String skillName) {
        String trimmed = trim(value);
        if (trimmed == null) {
            return null;
        }
        Integer raw = Integer.valueOf(trimmed);
        int maxRaw;
        if ("Listening".equalsIgnoreCase(skillName) || "Reading".equalsIgnoreCase(skillName)) {
            maxRaw = 100;
        } else if ("Speaking".equalsIgnoreCase(skillName)) {
            maxRaw = 11;
        } else {
            maxRaw = 8;
        }
        if (raw < 0 || raw > maxRaw) {
            throw new IllegalArgumentException(skillName + " cua TOEIC phai nam trong khoang 0 den " + maxRaw);
        }
        return raw;
    }

    private void validateArrayLength(boolean enabled, String[] studentEmails, String[] values, String skillName) {
        if (!enabled) {
            return;
        }
        if (values == null || values.length != studentEmails.length) {
            throw new IllegalArgumentException("Du lieu diem " + skillName + " khong hop le");
        }
    }

    private void redirectWithError(HttpServletRequest req, HttpServletResponse resp, UserAccount actor, String classID,
            String examCode, String error) throws IOException {
        StringBuilder target = new StringBuilder(req.getContextPath()).append(resolveDashboardPath(actor));
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
            return "Dang nhap thanh cong va san sang quan ly diem";
        }
        if ("test_created".equals(msg)) {
            return "Da tao bai test va khoi tao bang diem theo tung ky nang";
        }
        if ("scores_saved".equals(msg)) {
            return "Da luu diem tung ky nang va cap nhat diem tong";
        }
        return null;
    }

    private String buildDashboardTarget(UserAccount actor, String classID, String examCode, String msg) {
        return new StringBuilder(resolveDashboardPath(actor))
                .append("?mode=input&classID=")
                .append(classID)
                .append("&examCode=")
                .append(examCode)
                .append("&msg=")
                .append(msg)
                .toString();
    }

    private String resolveDashboardPath(UserAccount actor) {
        return actor.getRole() == UserRole.TEACHER ? "/teacher/results" : "/staff/results";
    }

    private String trim(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String toSkillParameterName(SkillType skillType) {
        String normalized = skillType.name().charAt(0) + skillType.name().substring(1).toLowerCase();
        return "has" + normalized;
    }

    private String normalizeEmailKey(String email) {
        String trimmedEmail = trim(email);
        return trimmedEmail == null ? null : trimmedEmail.toLowerCase();
    }

    private record SkillSelection(boolean listening, boolean reading, boolean speaking, boolean writing) {
    }
}
