package vn.iotstar.coolenglish.web;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.coolenglish.dao.impl.TeacherDAO;
import vn.iotstar.coolenglish.dao.impl.UserAccountDAO;
import vn.iotstar.coolenglish.entity.Teacher;
import vn.iotstar.coolenglish.entity.UserAccount;
import vn.iotstar.coolenglish.enums.UserRole;

public class LoginController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final UserAccountDAO userAccountDAO = new UserAccountDAO();
    private final TeacherDAO teacherDAO = new TeacherDAO();

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
                req.getRequestDispatcher("/login.jsp").forward(req, resp);
                return;
            }

            UserAccount account = userAccountDAO.checkLogin(email, password);

            if (account != null) {
                // Logic từ LCMS-28: Tự động liên kết UserAccount với Teacher nếu chưa có mapping
                if (account.getRole() == UserRole.TEACHER && account.getRelatedID() == null 
                        && account.getEmail() != null) {
                    Teacher teacher = teacherDAO.findByEmail(account.getEmail().trim());
                    if (teacher != null) {
                        account.setRelatedID(teacher.getId());
                        userAccountDAO.update(account);
                    }
                }

                // Thiết lập Session
                HttpSession session = req.getSession(true);
                session.setAttribute("user", account);
                session.setMaxInactiveInterval(30 * 60);

                // Điều hướng dựa trên role (Logic từ develop)
                resp.sendRedirect(req.getContextPath() + resolveTargetPath(account));
                return;
            }

            // Đăng nhập thất bại
            req.setAttribute("error", "Email hoặc password không chính xác");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);

        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Loi trong qua trinh dang nhap: " + e.getMessage());
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
        }
    }

    private String resolveTargetPath(UserAccount account) {
        if (account.getRole() == UserRole.ADMIN || account.getRole() == UserRole.STAFF) {
            return "/admin/dashboard?msg=login_success";
        }
        return "/course?msg=login_success";
    }
}
