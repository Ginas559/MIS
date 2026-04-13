package vn.iotstar.coolenglish.web;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.coolenglish.entity.UserAccount;
import vn.iotstar.coolenglish.enums.UserRole;
import vn.iotstar.coolenglish.facade.DashboardFacade;

@WebServlet("/admin/dashboard")
public class DashboardController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final DashboardFacade dashboardFacade = new DashboardFacade();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserAccount currentUser = resolveCurrentUser(req);
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login?msg=require_login");
            return;
        }
        if (!isAdminOrStaff(currentUser)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Chi ADMIN/STAFF moi duoc truy cap dashboard nay.");
            return;
        }

        if ("login_success".equals(req.getParameter("msg"))) {
            req.setAttribute("message", "Đăng nhập thành công. Đây là bảng điều khiển vận hành cho Admin/Staff.");
        }
        req.setAttribute("currentUser", currentUser);
        req.setAttribute("dashboard", dashboardFacade.buildDashboard(currentUser));
        RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp");
        dispatcher.forward(req, resp);
    }

    private UserAccount resolveCurrentUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) {
            return null;
        }
        Object user = session.getAttribute("user");
        if (user instanceof UserAccount account) {
            return account;
        }
        return null;
    }

    private boolean isAdminOrStaff(UserAccount user) {
        return user != null && (user.getRole() == UserRole.ADMIN || user.getRole() == UserRole.STAFF);
    }
}
