package vn.iotstar.coolenglish.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import vn.iotstar.coolenglish.dao.impl.EnrollmentDAO;
import vn.iotstar.coolenglish.dao.impl.ExamResultDAO;
import vn.iotstar.coolenglish.dao.impl.PersonDAO;
import vn.iotstar.coolenglish.entity.Enrollment;
import vn.iotstar.coolenglish.entity.ExamResult;
import vn.iotstar.coolenglish.entity.Student;
import vn.iotstar.coolenglish.entity.UserAccount;
import vn.iotstar.coolenglish.enums.UserRole;

class StudentDashboardControllerTest {

    @Test
    void shouldForwardStudentDashboardData() throws Exception {
        EnrollmentDAO enrollmentDAO = new EnrollmentDAO() {
            @Override
            public List<Enrollment> findByStudentEmailWithDetails(String studentEmail) {
                return List.of(new Enrollment());
            }
        };
        ExamResultDAO examResultDAO = new ExamResultDAO() {
            @Override
            public List<ExamResult> findByStudentEmail(String studentEmail) {
                ExamResult result = new ExamResult();
                result.setClassID("CLS01");
                result.setStudentEmail(studentEmail);
                result.setExamCode("CLS01_QUIZ_1");
                result.setScore(8.5d);
                return List.of(result);
            }
        };
        PersonDAO personDAO = new PersonDAO() {
            @Override
            public Student findByEmail(String email) {
                Student student = new Student();
                student.setFullName("Test Student");
                student.setEmail(email);
                return student;
            }
        };

        StudentDashboardController controller = new StudentDashboardController(enrollmentDAO, examResultDAO, personDAO);

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/student/dashboard");
        request.setContextPath("/app");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", new UserAccount("student@coolenglish.vn", "student", "x", UserRole.STUDENT));
        request.setSession(session);
        MockHttpServletResponse response = new MockHttpServletResponse();

        controller.doGet(request, response);

        assertEquals("/WEB-INF/views/student/dashboard.jsp", response.getForwardedUrl());
        assertNotNull(request.getAttribute("enrollments"));
        assertNotNull(request.getAttribute("examResults"));
        assertNotNull(request.getAttribute("resultsByClassID"));
    }

    @Test
    void shouldRedirectWhenSessionHasNoStudent() throws Exception {
        StudentDashboardController controller = new StudentDashboardController();

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/student/dashboard");
        request.setContextPath("/app");
        MockHttpServletResponse response = new MockHttpServletResponse();

        controller.doGet(request, response);

        assertEquals("/app/login?msg=require_login", response.getRedirectedUrl());
    }
}
