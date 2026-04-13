package vn.iotstar.coolenglish.web;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.coolenglish.dao.impl.UserAccountDAO;
import vn.iotstar.coolenglish.entity.UserAccount;
import vn.iotstar.coolenglish.enums.UserRole;

public class LoginController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final UserAccountDAO userAccountDAO = new UserAccountDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestDispatcher rd = req.getRequestDispatcher("/login.jsp");
        rd.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        try {
            String email = req.getParameter("email");
            String password = req.getParameter("password");

            if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
                req.setAttribute("error", "Email va password khong duoc de trong");
                RequestDispatcher rd = req.getRequestDispatcher("/login.jsp");
                rd.forward(req, resp);
                return;
            }

            UserAccount account = userAccountDAO.checkLogin(email, password);

            if (account != null) {
                HttpSession session = req.getSession(true);
                session.setAttribute("user", account);
                session.setMaxInactiveInterval(30 * 60);
                resp.sendRedirect(req.getContextPath() + resolveTargetPath(account));
                return;
            }

            req.setAttribute("error", "Email hoac password khong chinh xac");
            RequestDispatcher rd = req.getRequestDispatcher("/login.jsp");
            rd.forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Loi trong qua trinh dang nhap: " + e.getMessage());
            RequestDispatcher rd = req.getRequestDispatcher("/login.jsp");
            rd.forward(req, resp);
        }
    }

    private String resolveTargetPath(UserAccount account) {
        if (account.getRole() == UserRole.ADMIN || account.getRole() == UserRole.STAFF) {
            return "/admin/class?msg=login_success";
        }
        return "/course?msg=login_success";
    }
}
