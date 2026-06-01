package vn.iotstar.coolenglish.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.coolenglish.dao.impl.RoadmapAccessGrantDAO;
import vn.iotstar.coolenglish.dao.impl.RoadmapDAO;
import vn.iotstar.coolenglish.dao.impl.UserAccountDAO;
import vn.iotstar.coolenglish.entity.Roadmap;
import vn.iotstar.coolenglish.entity.UserAccount;
import vn.iotstar.coolenglish.enums.UserRole;

@WebServlet(urlPatterns = { "/admin/roadmap-grants" })
public class RoadmapGrantController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final UserAccountDAO userAccountDAO = new UserAccountDAO();
    private final RoadmapDAO roadmapDAO = new RoadmapDAO();
    private final RoadmapAccessGrantDAO roadmapAccessGrantDAO = new RoadmapAccessGrantDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserAccount currentUser = resolveCurrentUser(req);
        if (!canManageRoadmapGrant(currentUser)) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp?msg=forbidden");
            return;
        }

        List<UserAccount> learners = userAccountDAO.findLearnersForRoadmapGrant();
        List<Roadmap> roadmaps = roadmapDAO.findAllActive();

        Long userId = parseLong(req.getParameter("userId"));
        if (userId == null && !learners.isEmpty()) {
            userId = learners.get(0).getUserID();
        }

        List<Long> grantedRoadmapIds = roadmapAccessGrantDAO.findActiveRoadmapIdsByUserId(userId);

        req.setAttribute("learners", learners);
        req.setAttribute("roadmaps", roadmaps);
        req.setAttribute("selectedUserId", userId);
        req.setAttribute("grantedRoadmapIds", grantedRoadmapIds);

        RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/admin/roadmap-grants.jsp");
        dispatcher.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        UserAccount currentUser = resolveCurrentUser(req);
        if (!canManageRoadmapGrant(currentUser)) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp?msg=forbidden");
            return;
        }

        Long userId = parseLong(req.getParameter("userId"));
        if (userId == null) {
            resp.sendRedirect(req.getContextPath() + "/admin/roadmap-grants?msg=missing_person");
            return;
        }

        String[] selectedRoadmapIds = req.getParameterValues("roadmapIds");
        List<Long> roadmapIds = new ArrayList<>();
        if (selectedRoadmapIds != null) {
            for (String selectedRoadmapId : selectedRoadmapIds) {
                Long roadmapId = parseLong(selectedRoadmapId);
                if (roadmapId != null) {
                    roadmapIds.add(roadmapId);
                }
            }
        }

        roadmapAccessGrantDAO.replaceActiveGrants(userId, roadmapIds, currentUser.getEmail());
        resp.sendRedirect(req.getContextPath() + "/admin/roadmap-grants?userId=" + userId + "&msg=updated");
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

    private boolean canManageRoadmapGrant(UserAccount currentUser) {
        if (currentUser == null) {
            return false;
        }

        UserRole role = currentUser.getRole();
        return role == UserRole.ADMIN || role == UserRole.STAFF;
    }

    private Long parseLong(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return Long.valueOf(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}

