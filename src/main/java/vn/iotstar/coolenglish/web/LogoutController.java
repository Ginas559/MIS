package vn.iotstar.coolenglish.web;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * LogoutController - Xử lý chức năng đăng xuất người dùng
 * 
 * Sử dụng session.invalidate() để xóa bỏ toàn bộ dữ liệu phiên và quyền truy cập
 * 
 * @author CoolEnglish Team
 */
@WebServlet("/logout")
public class LogoutController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            // Lấy session hiện tại
            HttpSession session = req.getSession(false);

            // Nếu có session, hủy nó
            if (session != null) {
                session.invalidate();
            }

            // Chuyển hướng về trang đăng nhập
            resp.sendRedirect(req.getContextPath() + "/login.jsp?msg=logout_success");

        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
        }
    }
}

