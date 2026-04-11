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
                // Đăng nhập thành công - Lưu thông tin định danh vào Session
                HttpSession session = req.getSession(true);
                session.setAttribute("user", account); // Lưu UserAccount để dùng cho Proxy
                session.setMaxInactiveInterval(30 * 60); // Timeout 30 phút

                // Chuyển hướng đến danh sách khóa học sau khi đăng nhập
                resp.sendRedirect(req.getContextPath() + "/course?msg=login_success");

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

