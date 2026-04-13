package vn.iotstar.coolenglish.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.coolenglish.dao.impl.EnglishClassDAO;
import vn.iotstar.coolenglish.dao.impl.EnrollmentDAO;
import vn.iotstar.coolenglish.dao.impl.PersonDAO;
import vn.iotstar.coolenglish.dao.impl.SessionDAO;
import vn.iotstar.coolenglish.dao.impl.TeacherDAO;
import vn.iotstar.coolenglish.entity.Attendance;
import vn.iotstar.coolenglish.entity.EnglishClass;
import vn.iotstar.coolenglish.entity.Enrollment;
import vn.iotstar.coolenglish.entity.Person;
import vn.iotstar.coolenglish.entity.Session;
import vn.iotstar.coolenglish.entity.Teacher;
import vn.iotstar.coolenglish.entity.UserAccount;
import vn.iotstar.coolenglish.enums.AttendanceStatus;
import vn.iotstar.coolenglish.enums.UserRole;
import vn.iotstar.coolenglish.service.AttendanceService;

@WebServlet(urlPatterns = {
        "/teacher/classes",
        "/teacher/attendance",
        "/teacher/attendance/mark",
        "/teacher/attendance/report"
})
public class AttendanceController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final EnglishClassDAO englishClassDAO = new EnglishClassDAO();
    private final EnrollmentDAO enrollmentDAO = new EnrollmentDAO();
    private final SessionDAO sessionDAO = new SessionDAO();
    private final PersonDAO personDAO = new PersonDAO();
    private final TeacherDAO teacherDAO = new TeacherDAO();
    private final AttendanceService attendanceService = new AttendanceService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        HttpSession session = req.getSession();
        UserAccount user = (UserAccount) session.getAttribute("user");

        if (user == null || user.getRole() != UserRole.TEACHER) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        Teacher teacher = resolveTeacher(user);
        if (teacher == null) {
            resp.sendRedirect(req.getContextPath() + "/login?msg=teacher_profile_missing");
            return;
        }

        if ("/teacher/classes".equals(path)) {
            List<EnglishClass> classes = englishClassDAO.findByTeacherIdWithScheduleSessions(teacher.getId());
            req.setAttribute("classes", classes);
            RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/teacher/classes.jsp");
            dispatcher.forward(req, resp);
            return;
        }

        if ("/teacher/attendance".equals(path)) {
            String classId = req.getParameter("classId");
            String sessionId = req.getParameter("sessionId");

            if (classId == null || classId.isBlank()) {
                resp.sendRedirect(req.getContextPath() + "/teacher/classes");
                return;
            }

            EnglishClass englishClass = englishClassDAO.findByClassIdWithScheduleSessions(classId);
            if (englishClass == null || !teacher.getId().equals(englishClass.getTeacherID())) {
                resp.sendRedirect(req.getContextPath() + "/teacher/classes");
                return;
            }

            if (sessionId == null || sessionId.isBlank()) {
                req.setAttribute("englishClass", englishClass);
                RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/teacher/attendance-sessions.jsp");
                dispatcher.forward(req, resp);
                return;
            }

            Session sess = sessionDAO.findByIdWithSchedule(sessionId);
            if (sess == null || !sessionBelongsToClass(englishClass, sess)) {
                resp.sendRedirect(req.getContextPath() + "/teacher/attendance?classId=" + classId);
                return;
            }

            List<Enrollment> enrollments = enrollmentDAO.findByClassIdWithStudent(classId);
            req.setAttribute("enrollments", enrollments);
            req.setAttribute("session", sess);
            req.setAttribute("englishClass", englishClass);
            RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/teacher/attendance.jsp");
            dispatcher.forward(req, resp);
            return;
        }

        if ("/teacher/attendance/report".equals(path)) {
            String classId = req.getParameter("classId");
            String sessionId = req.getParameter("sessionId");

            if (classId == null || classId.isBlank() || sessionId == null || sessionId.isBlank()) {
                resp.sendRedirect(req.getContextPath() + "/teacher/classes");
                return;
            }

            EnglishClass englishClass = englishClassDAO.findByClassIdWithScheduleSessions(classId);
            if (englishClass == null || !teacher.getId().equals(englishClass.getTeacherID())) {
                resp.sendRedirect(req.getContextPath() + "/teacher/classes");
                return;
            }

            Session sess = sessionDAO.findByIdWithSchedule(sessionId);
            if (sess == null || !sessionBelongsToClass(englishClass, sess)) {
                resp.sendRedirect(req.getContextPath() + "/teacher/attendance?classId=" + classId);
                return;
            }

            List<Enrollment> enrollmentsWithStudent = enrollmentDAO.findByClassIdWithStudent(classId);
            List<Map<String, Object>> reportItems = new ArrayList<>();
            for (Enrollment enrollment : enrollmentsWithStudent) {
                Attendance attendance = attendanceService.findByEnrollmentIdAndSessionId(enrollment.getId(), sessionId);
                boolean present = attendance != null && attendance.getStatus() == AttendanceStatus.PRESENT;
                String status = present ? "Có mặt" : (attendance != null ? "Vắng" : "Chưa điểm danh");
                String note = attendance != null ? attendance.getNote() : "";
                Map<String, Object> item = new HashMap<>();
                item.put("enrollmentId", enrollment.getId());
                item.put("studentName", enrollment.getStudent().getFullName());
                item.put("present", present);
                item.put("status", status);
                item.put("checkinTime", attendance != null ? attendance.getCheckinTime() : null);
                item.put("note", note);
                reportItems.add(item);
            }

            req.setAttribute("englishClass", englishClass);
            req.setAttribute("session", sess);
            req.setAttribute("reportItems", reportItems);
            RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/teacher/report.jsp");
            dispatcher.forward(req, resp);
            return;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        HttpSession session = req.getSession();
        UserAccount user = (UserAccount) session.getAttribute("user");

        if (user == null || user.getRole() != UserRole.TEACHER) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        Teacher teacher = resolveTeacher(user);
        if (teacher == null) {
            resp.sendRedirect(req.getContextPath() + "/login?msg=teacher_profile_missing");
            return;
        }

        if ("/teacher/attendance/mark".equals(path)) {
            String sessionId = req.getParameter("sessionId");
            String classId = req.getParameter("classId");
            String[] presentIds = req.getParameterValues("present");

            if (sessionId == null || sessionId.isBlank() || classId == null || classId.isBlank()) {
                resp.sendRedirect(req.getContextPath() + "/teacher/classes");
                return;
            }

            EnglishClass clazz = englishClassDAO.findByClassIdWithScheduleSessions(classId);
            if (clazz == null || !teacher.getId().equals(clazz.getTeacherID())) {
                resp.sendRedirect(req.getContextPath() + "/teacher/classes");
                return;
            }

            Session sess = sessionDAO.findByIdWithSchedule(sessionId);
            if (sess == null || !sessionBelongsToClass(clazz, sess)) {
                resp.sendRedirect(req.getContextPath() + "/teacher/attendance?classId=" + classId);
                return;
            }

            Set<String> present = new HashSet<>();
            if (presentIds != null) {
                present.addAll(Arrays.asList(presentIds));
            }

            List<Enrollment> enrollments = enrollmentDAO.findByClassIdWithStudent(classId);
            for (Enrollment enrollment : enrollments) {
                boolean isPresent = present.contains(enrollment.getId().toString());
                attendanceService.markAttendance(enrollment, sess, isPresent, "");
            }

            resp.sendRedirect(req.getContextPath() + "/teacher/attendance/report?classId=" + classId + "&sessionId=" + sessionId);
        }
    }

    private Teacher resolveTeacher(UserAccount user) {
        Long rid = user.getRelatedID();
        if (rid != null) {
            Person person = personDAO.findById(rid, Person.class);
            if (person instanceof Teacher t) {
                return t;
            }
        }
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            return teacherDAO.findByEmail(user.getEmail().trim());
        }
        return null;
    }

    private boolean sessionBelongsToClass(EnglishClass englishClass, Session sess) {
        if (englishClass.getSchedule() == null || sess.getSchedule() == null) {
            return false;
        }
        return englishClass.getSchedule().getScheduleID().equals(sess.getSchedule().getScheduleID());
    }
}
