package vn.iotstar.coolenglish.web;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.coolenglish.entity.Roadmap;
import vn.iotstar.coolenglish.entity.UserAccount;
import vn.iotstar.coolenglish.enums.UserRole;
import vn.iotstar.coolenglish.service.RoadmapManagementService;
import vn.iotstar.coolenglish.service.RoadmapManagementService.ContentNodeView;

@WebServlet(urlPatterns = { "/admin/roadmap-management" })
public class RoadmapManagementController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final RoadmapManagementService roadmapManagementService;

    public RoadmapManagementController() {
        this(new RoadmapManagementService());
    }

    RoadmapManagementController(RoadmapManagementService roadmapManagementService) {
        this.roadmapManagementService = roadmapManagementService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserAccount currentUser = resolveCurrentUser(req);
        if (!canManage(currentUser)) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp?msg=forbidden");
            return;
        }

        loadPage(req);
        RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/admin/roadmap-management.jsp");
        dispatcher.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        UserAccount currentUser = resolveCurrentUser(req);
        if (!canManage(currentUser)) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp?msg=forbidden");
            return;
        }

        String action = trim(req.getParameter("action"));
        try {
            handleAction(action, req);
            req.setAttribute("success", "Thao tac thanh cong.");
        } catch (IllegalArgumentException e) {
            req.setAttribute("error", e.getMessage());
        }

        loadPage(req);
        RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/admin/roadmap-management.jsp");
        dispatcher.forward(req, resp);
    }

    private void handleAction(String action, HttpServletRequest req) {
        if ("createRoadmap".equals(action)) {
            roadmapManagementService.createRoadmap(
                    req.getParameter("roadmapCode"),
                    req.getParameter("title"),
                    req.getParameter("description"));
            return;
        }

        if ("updateRoadmap".equals(action)) {
            roadmapManagementService.updateRoadmap(
                    parseLong(req.getParameter("roadmapId")),
                    req.getParameter("title"),
                    req.getParameter("description"),
                    Boolean.parseBoolean(req.getParameter("active")));
            return;
        }

        if ("deleteRoadmap".equals(action)) {
            roadmapManagementService.deleteRoadmap(parseLong(req.getParameter("roadmapId")));
            return;
        }

        if ("addModule".equals(action)) {
            roadmapManagementService.addModule(
                    parseLong(req.getParameter("parentModuleId")),
                    req.getParameter("title"),
                    req.getParameter("description"));
            return;
        }

        if ("addLesson".equals(action)) {
            roadmapManagementService.addLesson(
                    parseLong(req.getParameter("parentModuleId")),
                    req.getParameter("title"),
                    req.getParameter("description"),
                    req.getParameter("lessonType"),
                    req.getParameter("resourceUrl"));
            return;
        }

        if ("updateContent".equals(action)) {
            roadmapManagementService.updateContent(
                    parseLong(req.getParameter("contentId")),
                    req.getParameter("title"),
                    req.getParameter("description"),
                    req.getParameter("lessonType"),
                    req.getParameter("resourceUrl"));
            return;
        }

        if ("deleteContent".equals(action)) {
            roadmapManagementService.deleteContent(parseLong(req.getParameter("contentId")));
            return;
        }

        throw new IllegalArgumentException("Action khong hop le.");
    }

    private void loadPage(HttpServletRequest req) {
        List<Roadmap> roadmaps = roadmapManagementService.findAllRoadmaps();
        Long selectedRoadmapId = parseLong(req.getParameter("roadmapId"));
        if (selectedRoadmapId == null && !roadmaps.isEmpty()) {
            selectedRoadmapId = roadmaps.get(0).getId();
        }

        List<ContentNodeView> nodes = roadmapManagementService.findRoadmapContentNodes(selectedRoadmapId);
        List<ContentNodeView> moduleNodes = nodes.stream()
                .filter(ContentNodeView::isModule)
                .collect(Collectors.toList());

        req.setAttribute("roadmaps", roadmaps);
        req.setAttribute("selectedRoadmapId", selectedRoadmapId);
        req.setAttribute("contentNodes", nodes);
        req.setAttribute("moduleNodes", moduleNodes);
    }

    private UserAccount resolveCurrentUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            Object currentUser = session.getAttribute("user");
            if (currentUser instanceof UserAccount userAccount) {
                return userAccount;
            }
        }
        return null;
    }

    private boolean canManage(UserAccount currentUser) {
        if (currentUser == null) {
            return false;
        }
        UserRole role = currentUser.getRole();
        return role == UserRole.ADMIN || role == UserRole.STAFF;
    }

    private Long parseLong(String raw) {
        String value = trim(raw);
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return Long.valueOf(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("ID khong hop le.");
        }
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }
}


