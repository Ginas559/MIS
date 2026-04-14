package vn.iotstar.coolenglish.web;

import java.io.IOException;
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
import vn.iotstar.coolenglish.dao.impl.EnrollmentDAO;
import vn.iotstar.coolenglish.dao.impl.ExamResultDAO;
import vn.iotstar.coolenglish.dao.impl.PersonDAO;
import vn.iotstar.coolenglish.entity.Enrollment;
import vn.iotstar.coolenglish.entity.ExamResult;
import vn.iotstar.coolenglish.entity.Person;
import vn.iotstar.coolenglish.entity.Student;
import vn.iotstar.coolenglish.entity.UserAccount;
import vn.iotstar.coolenglish.enums.UserRole;

@WebServlet(urlPatterns = {
        "/student/dashboard",
        "/student/schedule",
        "/student/results"
})
public class StudentDashboardController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final EnrollmentDAO enrollmentDAO;
    private final ExamResultDAO examResultDAO;
    private final PersonDAO personDAO;

    public StudentDashboardController() {
        this(new EnrollmentDAO(), new ExamResultDAO(), new PersonDAO());
    }

    StudentDashboardController(EnrollmentDAO enrollmentDAO, ExamResultDAO examResultDAO, PersonDAO personDAO) {
        this.enrollmentDAO = enrollmentDAO;
        this.examResultDAO = examResultDAO;
        this.personDAO = personDAO;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserAccount studentAccount = resolveStudent(req, resp);
        if (studentAccount == null) {
            return;
        }

        List<Enrollment> enrollments = enrollmentDAO.findByStudentEmailWithDetails(studentAccount.getEmail());
        List<ExamResult> examResults = examResultDAO.findByStudentEmail(studentAccount.getEmail());

        req.setAttribute("student", resolveStudentProfile(studentAccount));
        req.setAttribute("enrollments", enrollments);
        req.setAttribute("examResults", examResults);
        req.setAttribute("resultsByClassID", buildResultsByClassID(examResults));
        req.setAttribute("message", resolveMessage(req.getParameter("msg")));
        req.setAttribute("totalClasses", enrollments.size());
        req.setAttribute("gradedTests", countRecordedScores(examResults));

        RequestDispatcher dispatcher = req.getRequestDispatcher(resolveView(req.getServletPath()));
        dispatcher.forward(req, resp);
    }

    private String resolveView(String servletPath) {
        if (servletPath == null || servletPath.isBlank() || servletPath.endsWith("/dashboard")) {
            return "/WEB-INF/views/student/dashboard.jsp";
        }
        if (servletPath.endsWith("/schedule")) {
            return "/WEB-INF/views/student/schedule.jsp";
        }
        if (servletPath.endsWith("/results")) {
            return "/WEB-INF/views/student/results.jsp";
        }
        return "/WEB-INF/views/student/dashboard.jsp";
    }

    private UserAccount resolveStudent(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.sendRedirect(req.getContextPath() + "/login?msg=require_login");
            return null;
        }

        Object currentUser = session.getAttribute("user");
        if (!(currentUser instanceof UserAccount user) || user.getRole() != UserRole.STUDENT) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp?msg=forbidden");
            return null;
        }
        return user;
    }

    private Student resolveStudentProfile(UserAccount studentAccount) {
        if (studentAccount.getRelatedID() != null) {
            Person person = personDAO.findById(studentAccount.getRelatedID(), Person.class);
            if (person instanceof Student student) {
                return student;
            }
        }

        Person person = personDAO.findByEmail(studentAccount.getEmail());
        if (person instanceof Student student) {
            return student;
        }
        return null;
    }

    private Map<String, List<ExamResult>> buildResultsByClassID(List<ExamResult> examResults) {
        Map<String, List<ExamResult>> grouped = new LinkedHashMap<>();
        for (ExamResult examResult : examResults) {
            String classID = examResult.getClassID() == null ? "__NO_CLASS__" : examResult.getClassID();
            grouped.computeIfAbsent(classID, key -> new ArrayList<>()).add(examResult);
        }
        return grouped;
    }

    private int countRecordedScores(List<ExamResult> examResults) {
        int count = 0;
        for (ExamResult examResult : examResults) {
            if (examResult.hasRecordedScore()) {
                count++;
            }
        }
        return count;
    }

    private String resolveMessage(String msg) {
        if ("login_success".equals(msg)) {
            return "Dang nhap thanh cong. Day la lich hoc va ket qua ca nhan cua ban.";
        }
        return null;
    }
}
