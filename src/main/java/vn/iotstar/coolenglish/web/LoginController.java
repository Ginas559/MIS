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

/**
 * LoginController - Xử lý chức năng đăng nhập người dùng
 * 
 * Kết nối với cơ chế bảo mật Proxy (Task G):
 * - Khi đăng nhập thành công, UserAccount được lưu vào HttpSession
 * - SecurityAccessFactory sử dụng UserAccount từ Session để quyết định trả về Real Service hay Proxy
 * 
 * @author CoolEnglish Team
 */
public class LoginController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final UserAccountDAO userAccountDAO = new UserAccountDAO();
    private final TeacherDAO teacherDAO = new TeacherDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Hiển thị form đăng nhập
        RequestDispatcher rd = req.getRequestDispatcher("/login.jsp");
        rd.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        
        try {
            String email = req.getParameter("email");
            String password = req.getParameter("password");

            // Kiểm tra thông tin nhập vào
            if (email == null || email.trim().isEmpty() || 
                password == null || password.trim().isEmpty()) {
                req.setAttribute("error", "Email và password không được để trống");
                RequestDispatcher rd = req.getRequestDispatcher("/login.jsp");
                rd.forward(req, resp);
                return;
            }

            // Kiểm tra đăng nhập
            UserAccount account = userAccountDAO.checkLogin(email, password);

            if (account != null) {
                if (account.getRole() == UserRole.TEACHER && account.getRelatedID() == null
                        && account.getEmail() != null) {
                    Teacher teacher = teacherDAO.findByEmail(account.getEmail().trim());
                    if (teacher != null) {
                        account.setRelatedID(teacher.getId());
                        userAccountDAO.update(account);
                    }
                }

                HttpSession session = req.getSession(true);
                session.setAttribute("user", account);
                session.setMaxInactiveInterval(30 * 60);

                if (account.getRole() == UserRole.TEACHER) {
                    resp.sendRedirect(req.getContextPath() + "/course?msg=login_success");
                } else {
                    resp.sendRedirect(req.getContextPath() + "/course?msg=login_success");
                }

            } else {
                // Đăng nhập thất bại
                req.setAttribute("error", "Email hoặc password không chính xác");
                RequestDispatcher rd = req.getRequestDispatcher("/login.jsp");
                rd.forward(req, resp);
            }

        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Lỗi trong quá trình đăng nhập: " + e.getMessage());
            RequestDispatcher rd = req.getRequestDispatcher("/login.jsp");
            rd.forward(req, resp);
        }
    }
}

