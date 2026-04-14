package vn.iotstar.coolenglish.web;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.coolenglish.dao.impl.RoadmapDAO;
import vn.iotstar.coolenglish.entity.Roadmap;
import vn.iotstar.coolenglish.entity.UserAccount;
import vn.iotstar.coolenglish.enums.UserRole;

@WebServlet(urlPatterns = { "/roadmaps" })
public class RoadmapController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final RoadmapDAO roadmapDAO;

    public RoadmapController() {
        this(new RoadmapDAO());
    }

    RoadmapController(RoadmapDAO roadmapDAO) {
        this.roadmapDAO = roadmapDAO;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserAccount currentUser = resolveCurrentUser(req);
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login?msg=require_login");
            return;
        }

        List<Roadmap> roadmaps = roadmapDAO.findAllActive();
        req.setAttribute("roadmaps", roadmaps);
        req.setAttribute("canManageGrants", isAdminOrStaff(currentUser));
        RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/roadmap-list.jsp");
        dispatcher.forward(req, resp);
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

    private boolean isAdminOrStaff(UserAccount currentUser) {
        UserRole role = currentUser.getRole();
        return role == UserRole.ADMIN || role == UserRole.STAFF;
    }
}
