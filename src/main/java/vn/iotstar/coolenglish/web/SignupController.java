package vn.iotstar.coolenglish.web;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.coolenglish.dao.impl.UserAccountDAO;
import vn.iotstar.coolenglish.enums.UserRole;
import vn.iotstar.coolenglish.util.EmailUtils;

/**
 * SignupController - Xử lý chức năng đăng ký người dùng
 * 
 * Sử dụng Template Method + Factory Method pattern:
 * - Template Method (UserRegistration.registerUser()): Định nghĩa khung xương của quy trình đăng ký
 * - Factory Method (StudentRegistration/TeacherRegistration): Tạo đối tượng cụ thể dựa trên vai trò
 * 
 * @author CoolEnglish Team
 */
@WebServlet("/signup")
public class SignupController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final long OTP_TTL_MILLIS = 5 * 60 * 1000;
    static final String NON_STUDENT_SECRET_CODE = "1k3t";
    private final UserAccountDAO userAccountDAO = new UserAccountDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Hiển thị form đăng ký
        RequestDispatcher rd = req.getRequestDispatcher("/register.jsp");
        rd.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        
        try {
            String name = req.getParameter("name");
            String email = req.getParameter("email");
            String password = req.getParameter("password");
            String confirmPassword = req.getParameter("confirmPassword");
            String roleParam = req.getParameter("role");
            String secretCode = req.getParameter("secretCode");

            // Kiểm tra thông tin hợp lệ
            String validationError = validateInputError(name, email, password, confirmPassword, roleParam, secretCode);
            if (validationError != null) {
                req.setAttribute("error", validationError);
                RequestDispatcher rd = req.getRequestDispatcher("/register.jsp");
                rd.forward(req, resp);
                return;
            }

            // Kiểm tra email đã tồn tại chưa
            if (userAccountDAO.isEmailExists(email)) {
                req.setAttribute("error", "Email đã tồn tại trong hệ thống");
                RequestDispatcher rd = req.getRequestDispatcher("/register.jsp");
                rd.forward(req, resp);
                return;
            }

            if (!EmailUtils.isSmtpConfigured()) {
                req.setAttribute("error", EmailUtils.buildMissingSmtpConfigMessage());
                RequestDispatcher rd = req.getRequestDispatcher("/register.jsp");
                rd.forward(req, resp);
                return;
            }

            String otpCode = generateOtpCode();
            try {
                EmailUtils.sendVerificationCode(email, otpCode);
            } catch (RuntimeException emailError) {
                req.setAttribute("error", "Khong gui duoc OTP qua Gmail. " + emailError.getMessage());
                RequestDispatcher rd = req.getRequestDispatcher("/register.jsp");
                rd.forward(req, resp);
                return;
            }

            HttpSession session = req.getSession(true);
            PendingSignupData pendingSignupData = new PendingSignupData(name, email, password, roleParam);
            session.setAttribute(SignupOtpSessionKeys.PENDING_SIGNUP, pendingSignupData);
            session.setAttribute(SignupOtpSessionKeys.OTP_CODE, otpCode);
            session.setAttribute(SignupOtpSessionKeys.OTP_EXPIRES_AT,
                    Long.valueOf(System.currentTimeMillis() + OTP_TTL_MILLIS));

            // Chuyển sang bước xác thực OTP trước khi lưu account vào DB.
            resp.sendRedirect(req.getContextPath() + "/verify-otp");

        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Lỗi trong quá trình đăng ký: " + e.getMessage());
            RequestDispatcher rd = req.getRequestDispatcher("/register.jsp");
            rd.forward(req, resp);
        }
    }

    static String generateOtpCode() {
        int value = ThreadLocalRandom.current().nextInt(100000, 1000000);
        return String.valueOf(value);
    }

    /**
     * Kiểm tra tính hợp lệ của thông tin nhập vào
     */
    private String validateInputError(String name, String email, String password, String confirmPassword,
            String role, String secretCode) {
        // Kiểm tra xem có field nào rỗng không
        if (name == null || name.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            password == null || password.trim().isEmpty() ||
            confirmPassword == null || confirmPassword.trim().isEmpty() ||
            role == null) {
            return "Vui lòng điền đầy đủ thông tin bắt buộc";
        }

        // Kiểm tra password khớp không
        if (!password.equals(confirmPassword)) {
            return "Mật khẩu xác nhận không khớp";
        }

        if (password.length() < 6) {
            return "Mật khẩu phải dài ít nhất 6 ký tự";
        }

        // Kiểm tra email hợp lệ (cơ bản)
        if (!email.contains("@")) {
            return "Email không hợp lệ";
        }

        UserRole requestedRole = parseRole(role);
        if (requestedRole == null) {
            return "Vai trò đăng ký không hợp lệ";
        }

        if (requestedRole == UserRole.ADMIN) {
            return "Tài khoản ADMIN không thể tự đăng ký";
        }

        if (requestedRole != UserRole.STUDENT &&
            (secretCode == null || !NON_STUDENT_SECRET_CODE.equals(secretCode.trim()))) {
            return "Mã bí mật không đúng cho vai trò đã chọn";
        }

        return null;
    }

    private UserRole parseRole(String roleParam) {
        try {
            return UserRole.valueOf(roleParam);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}


