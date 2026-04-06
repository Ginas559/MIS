package vn.iotstar.coolenglish.web;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.coolenglish.dao.impl.UserAccountDAO;
import vn.iotstar.coolenglish.entity.UserAccount;
import vn.iotstar.coolenglish.enums.UserRole;
import vn.iotstar.coolenglish.factory.StaffRegistration;
import vn.iotstar.coolenglish.factory.StudentRegistration;
import vn.iotstar.coolenglish.factory.TeacherRegistration;
import vn.iotstar.coolenglish.factory.UserRegistration;

@WebServlet("/verify-otp")
public class VerifyOtpController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final UserAccountDAO userAccountDAO = new UserAccountDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        PendingSignupData pendingSignup = getPendingSignup(session);
        if (pendingSignup == null) {
            resp.sendRedirect(req.getContextPath() + "/signup?msg=otp_session_expired");
            return;
        }

        req.setAttribute("email", pendingSignup.getEmail());
        RequestDispatcher rd = req.getRequestDispatcher("/verify-otp.jsp");
        rd.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String inputOtp = req.getParameter("otp");
        HttpSession session = req.getSession(false);
        PendingSignupData pendingSignup = getPendingSignup(session);

        if (session == null || pendingSignup == null) {
            resp.sendRedirect(req.getContextPath() + "/signup?msg=otp_session_expired");
            return;
        }

        String expectedOtp = (String) session.getAttribute(SignupOtpSessionKeys.OTP_CODE);
        Long expiresAt = (Long) session.getAttribute(SignupOtpSessionKeys.OTP_EXPIRES_AT);

        if (expectedOtp == null || expiresAt == null || System.currentTimeMillis() > expiresAt.longValue()) {
            clearSignupSession(session);
            resp.sendRedirect(req.getContextPath() + "/signup?msg=otp_expired");
            return;
        }

        if (inputOtp == null || !expectedOtp.equals(inputOtp.trim())) {
            req.setAttribute("email", pendingSignup.getEmail());
            req.setAttribute("error", "Ma OTP khong chinh xac. Vui long thu lai.");
            RequestDispatcher rd = req.getRequestDispatcher("/verify-otp.jsp");
            rd.forward(req, resp);
            return;
        }

        if (userAccountDAO.isEmailExists(pendingSignup.getEmail())) {
            clearSignupSession(session);
            resp.sendRedirect(req.getContextPath() + "/signup?msg=email_exists");
            return;
        }

        UserRole role = parseRole(pendingSignup.getRole());
        if (role == null || role == UserRole.ADMIN) {
            clearSignupSession(session);
            resp.sendRedirect(req.getContextPath() + "/signup?msg=invalid_role");
            return;
        }

        UserRegistration factory = buildRegistrationFactory(role);

        factory.registerUser(pendingSignup.getName(), pendingSignup.getEmail());

        UserAccount account = new UserAccount(
                pendingSignup.getEmail(),
                pendingSignup.getName(),
                pendingSignup.getPassword(),
                role);
        userAccountDAO.insert(account);

        clearSignupSession(session);
        resp.sendRedirect(req.getContextPath() + "/login.jsp?msg=register_success");
    }

    private UserRole parseRole(String roleParam) {
        try {
            return UserRole.valueOf(roleParam);
        } catch (Exception ex) {
            return null;
        }
    }

    private UserRegistration buildRegistrationFactory(UserRole role) {
        if (role == UserRole.TEACHER) {
            return new TeacherRegistration();
        }
        if (role == UserRole.STAFF) {
            return new StaffRegistration();
        }
        return new StudentRegistration();
    }

    private PendingSignupData getPendingSignup(HttpSession session) {
        if (session == null) {
            return null;
        }

        Object value = session.getAttribute(SignupOtpSessionKeys.PENDING_SIGNUP);
        if (value instanceof PendingSignupData) {
            return (PendingSignupData) value;
        }
        return null;
    }

    private void clearSignupSession(HttpSession session) {
        session.removeAttribute(SignupOtpSessionKeys.PENDING_SIGNUP);
        session.removeAttribute(SignupOtpSessionKeys.OTP_CODE);
        session.removeAttribute(SignupOtpSessionKeys.OTP_EXPIRES_AT);
    }
}

