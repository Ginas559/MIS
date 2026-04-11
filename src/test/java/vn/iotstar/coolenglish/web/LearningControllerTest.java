package vn.iotstar.coolenglish.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import vn.iotstar.coolenglish.entity.Lesson;
import vn.iotstar.coolenglish.entity.Module;
import vn.iotstar.coolenglish.entity.Roadmap;
import vn.iotstar.coolenglish.entity.UserAccount;
import vn.iotstar.coolenglish.enums.UserRole;
import vn.iotstar.coolenglish.service.AcademicContentService;

class LearningControllerTest {

    @Test
    void shouldForwardLearningDataToJsp() throws Exception {
        AcademicContentService service = new AcademicContentService() {
            @Override
            public LearningViewModel buildLearningView(String roadmapCode, Long learnerPersonId,
                    String learnerEmail, String currentTitle, UserRole role) {
                Lesson lesson = new Lesson("Test Lesson", "Test content", "READING", "https://learning.local/test");
                Roadmap roadmap = new Roadmap();
                roadmap.setRoadmapCode("TOEIC_RL");
                roadmap.setTitle("TOEIC 2 ky nang RL");
                return new LearningViewModel("TOEIC_RL", roadmap, new Module("Root", "Root"), lesson, null, false);
            }
        };

        LearningController controller = new LearningController(service);

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/learning");
        request.setContextPath("/app");
        request.setParameter("roadmapCode", "TOEIC_RL");
        UserAccount user = new UserAccount("student@coolenglish.vn", "student", "x", UserRole.STUDENT);
        user.setRelatedID(1L);
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", user);
        request.setSession(session);
        MockHttpServletResponse response = new MockHttpServletResponse();

        controller.doGet(request, response);

        assertEquals("/WEB-INF/views/learning.jsp", response.getForwardedUrl());
        assertNotNull(request.getAttribute("currentContent"));
        assertEquals("https://learning.local/test", request.getAttribute("renderedContent"));
    }

    @Test
    void shouldRedirectToLoginWhenSessionHasNoUser() throws Exception {
        LearningController controller = new LearningController(new AcademicContentService());

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/learning");
        request.setContextPath("/app");
        MockHttpServletResponse response = new MockHttpServletResponse();

        controller.doGet(request, response);

        assertEquals("/app/login?msg=require_login", response.getRedirectedUrl());
    }
}

