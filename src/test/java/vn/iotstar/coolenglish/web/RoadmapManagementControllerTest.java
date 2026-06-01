package vn.iotstar.coolenglish.web;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import vn.iotstar.coolenglish.entity.UserAccount;
import vn.iotstar.coolenglish.enums.UserRole;
import vn.iotstar.coolenglish.service.RoadmapManagementService;

class RoadmapManagementControllerTest {

    @Test
    void shouldRedirectToLoginWhenUserIsMissing() throws Exception {
        RoadmapManagementController controller = new RoadmapManagementController(new StubRoadmapManagementService());

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/admin/roadmap-management");
        request.setContextPath("/app");
        MockHttpServletResponse response = new MockHttpServletResponse();

        controller.doGet(request, response);

        assertEquals("/app/login.jsp?msg=forbidden", response.getRedirectedUrl());
    }

    @Test
    void shouldForwardWhenStaffIsLoggedIn() throws Exception {
        RoadmapManagementController controller = new RoadmapManagementController(new StubRoadmapManagementService());

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/admin/roadmap-management");
        request.setContextPath("/app");

        UserAccount user = new UserAccount("staff@coolenglish.vn", "staff", "x", UserRole.STAFF);
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", user);
        request.setSession(session);

        MockHttpServletResponse response = new MockHttpServletResponse();

        controller.doGet(request, response);

        assertEquals("/WEB-INF/views/admin/roadmap-management.jsp", response.getForwardedUrl());
    }

    private static class StubRoadmapManagementService extends RoadmapManagementService {
        @Override
        public List<vn.iotstar.coolenglish.entity.Roadmap> findAllRoadmaps() {
            return List.of();
        }

        @Override
        public List<ContentNodeView> findRoadmapContentNodes(Long roadmapId) {
            return List.of();
        }
    }
}

