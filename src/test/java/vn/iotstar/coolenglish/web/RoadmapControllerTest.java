package vn.iotstar.coolenglish.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import vn.iotstar.coolenglish.dao.impl.RoadmapDAO;
import vn.iotstar.coolenglish.entity.Roadmap;
import vn.iotstar.coolenglish.entity.UserAccount;
import vn.iotstar.coolenglish.enums.UserRole;

class RoadmapControllerTest {

    @Test
    void shouldRedirectToLoginWhenUserIsMissing() throws Exception {
        RoadmapController controller = new RoadmapController();

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/roadmaps");
        request.setContextPath("/app");
        MockHttpServletResponse response = new MockHttpServletResponse();

        controller.doGet(request, response);

        assertEquals("/app/login?msg=require_login", response.getRedirectedUrl());
    }

    @Test
    void shouldForwardToRoadmapPageWhenUserIsLoggedIn() throws Exception {
        RoadmapController controller = new RoadmapController(new StubRoadmapDAO());

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/roadmaps");
        request.setContextPath("/app");
        UserAccount user = new UserAccount("admin@coolenglish.vn", "admin", "x", UserRole.ADMIN);
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", user);
        request.setSession(session);

        MockHttpServletResponse response = new MockHttpServletResponse();

        controller.doGet(request, response);

        assertEquals("/WEB-INF/views/roadmap-list.jsp", response.getForwardedUrl());
        assertNotNull(request.getAttribute("roadmaps"));
    }

    private static final class StubRoadmapDAO extends RoadmapDAO {
        @Override
        public List<Roadmap> findAllActive() {
            return List.of();
        }
    }
}

