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
import vn.iotstar.coolenglish.entity.Payment;
import vn.iotstar.coolenglish.entity.UserAccount;
import vn.iotstar.coolenglish.enums.UserRole;
import vn.iotstar.coolenglish.service.PaymentService;

@WebServlet(urlPatterns = {
        "/admin/payment/cash",
        "/admin/payment/cash/update"
})
public class StaffCashPaymentController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final PaymentService paymentService = new PaymentService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserAccount user = resolveCurrentUser(req);
        if (!canManageCashPayment(user)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Ban khong co quyen truy cap.");
            return;
        }

        List<Payment> pendingCashPayments = paymentService.getPendingCashPayments();
        req.setAttribute("pendingCashPayments", pendingCashPayments);
        RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/admin/cash-payment-list.jsp");
        dispatcher.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserAccount user = resolveCurrentUser(req);
        if (!canManageCashPayment(user)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Ban khong co quyen truy cap.");
            return;
        }

        String txnRef = trim(req.getParameter("txnRef"));
        String action = trim(req.getParameter("action"));
        if (txnRef == null || action == null) {
            resp.sendRedirect(req.getContextPath() + "/admin/payment/cash?msg=invalid_request");
            return;
        }

        try {
            boolean paid = "confirm".equalsIgnoreCase(action);
            paymentService.staffUpdateCashPayment(txnRef, paid);
            String msg = paid ? "confirm_success" : "mark_failed_success";
            resp.sendRedirect(req.getContextPath() + "/admin/payment/cash?msg=" + msg);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            resp.sendRedirect(req.getContextPath() + "/admin/payment/cash?msg=error");
        }
    }

    private UserAccount resolveCurrentUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) {
            return null;
        }
        Object currentUser = session.getAttribute("user");
        return currentUser instanceof UserAccount user ? user : null;
    }

    private boolean canManageCashPayment(UserAccount user) {
        if (user == null) {
            return false;
        }
        return user.getRole() == UserRole.STAFF || user.getRole() == UserRole.ADMIN;
    }

    private String trim(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
