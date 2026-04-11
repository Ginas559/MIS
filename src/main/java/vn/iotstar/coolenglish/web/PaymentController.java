package vn.iotstar.coolenglish.web;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.coolenglish.dao.impl.CourseDAO;
import vn.iotstar.coolenglish.dao.impl.EnglishClassDAO;
import vn.iotstar.coolenglish.dao.impl.EnrollmentDAO;
import vn.iotstar.coolenglish.entity.Course;
import vn.iotstar.coolenglish.entity.EnglishClass;
import vn.iotstar.coolenglish.entity.Payment;
import vn.iotstar.coolenglish.entity.UserAccount;
import vn.iotstar.coolenglish.enums.PaymentMethod;
import vn.iotstar.coolenglish.enums.UserRole;
import vn.iotstar.coolenglish.service.PaymentService;

@WebServlet(urlPatterns = {
        "/student/payment/detail",
        "/student/payment/method",
        "/student/payment/start",
        "/student/payment/vnpay/mock",
        "/student/payment/vnpay/callback",
        "/student/payment/mb/simulate",
        "/student/payment/refund"
})
public class PaymentController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final CourseDAO courseDAO = new CourseDAO();
    private final EnglishClassDAO classDAO = new EnglishClassDAO();
    private final EnrollmentDAO enrollmentDAO = new EnrollmentDAO();
    private final PaymentService paymentService = new PaymentService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleRequest(req, resp, false);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        handleRequest(req, resp, true);
    }

    private void handleRequest(HttpServletRequest req, HttpServletResponse resp, boolean isPost)
            throws ServletException, IOException {
        UserAccount user = resolveCurrentUser(req);
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp?msg=require_login");
            return;
        }
        if (user.getRole() != UserRole.STUDENT) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Chuc nang chi danh cho STUDENT.");
            return;
        }

        String path = req.getServletPath();
        if ("/student/payment/detail".equals(path)) {
            showCourseDetail(req, resp);
            return;
        }
        if ("/student/payment/method".equals(path)) {
            showMethodSelection(req, resp);
            return;
        }
        if ("/student/payment/start".equals(path) && isPost) {
            startPayment(req, resp, user);
            return;
        }
        if ("/student/payment/vnpay/mock".equals(path)) {
            showVNPayMock(req, resp);
            return;
        }
        if ("/student/payment/vnpay/callback".equals(path)) {
            handleVNPayCallback(req, resp);
            return;
        }
        if ("/student/payment/mb/simulate".equals(path)) {
            handleMBBankSimulate(req, resp);
            return;
        }
        if ("/student/payment/refund".equals(path)) {
            handleRefund(req, resp);
            return;
        }

        resp.sendRedirect(req.getContextPath() + "/course");
    }

    private void showCourseDetail(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String courseID = trim(req.getParameter("courseID"));
        if (courseID == null) {
            resp.sendRedirect(req.getContextPath() + "/course");
            return;
        }
        UserAccount user = resolveCurrentUser(req);
        if (user != null && isAlreadyEnrolled(courseID, user.getEmail())) {
            resp.sendRedirect(req.getContextPath() + "/course?msg=already_enrolled");
            return;
        }
        Course course = courseDAO.findById(courseID, Course.class);
        EnglishClass clazz = classDAO.findFirstOpenClassByCourseID(courseID);
        req.setAttribute("course", course);
        req.setAttribute("classroom", clazz);
        forward(req, resp, "/WEB-INF/views/student/payment-detail.jsp");
    }

    private void showMethodSelection(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String courseID = trim(req.getParameter("courseID"));
        if (courseID == null) {
            resp.sendRedirect(req.getContextPath() + "/course");
            return;
        }
        UserAccount user = resolveCurrentUser(req);
        if (user != null && isAlreadyEnrolled(courseID, user.getEmail())) {
            resp.sendRedirect(req.getContextPath() + "/course?msg=already_enrolled");
            return;
        }
        Course course = courseDAO.findById(courseID, Course.class);
        EnglishClass clazz = classDAO.findFirstOpenClassByCourseID(courseID);
        req.setAttribute("course", course);
        req.setAttribute("classroom", clazz);
        forward(req, resp, "/WEB-INF/views/student/payment-method.jsp");
    }

    private void startPayment(HttpServletRequest req, HttpServletResponse resp, UserAccount user)
            throws ServletException, IOException {
        String courseID = trim(req.getParameter("courseID"));
        String methodValue = trim(req.getParameter("method"));
        if (courseID == null || methodValue == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "courseID va method la bat buoc.");
            return;
        }
        EnglishClass clazz = classDAO.findFirstOpenClassByCourseID(courseID);
        if (clazz == null) {
            resp.sendError(HttpServletResponse.SC_CONFLICT, "Khong co lop OPEN cho khoa hoc nay.");
            return;
        }

        try {
            PaymentMethod method = PaymentMethod.valueOf(methodValue);
            Course course = courseDAO.findById(courseID, Course.class);
            if (isAlreadyEnrolled(courseID, user.getEmail())) {
                throw new IllegalStateException("Ban da ghi danh khoa hoc nay.");
            }
            if (course == null || course.getFee() == null || course.getFee() <= 0) {
                throw new IllegalStateException("Khoa hoc khong hop le de thanh toan.");
            }
            Payment payment = paymentService.createAndPay(course.getFee(), method, clazz.getClassID(), user.getEmail());

            req.setAttribute("course", course);
            req.setAttribute("classroom", clazz);
            req.setAttribute("payment", payment);

            if (method == PaymentMethod.VNPAY && payment.getRedirectUrl() != null) {
                resp.sendRedirect(req.getContextPath() + payment.getRedirectUrl());
                return;
            }
            forward(req, resp, "/WEB-INF/views/student/payment-result.jsp");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            req.setAttribute("error", ex.getMessage());
            showMethodSelection(req, resp);
        }
    }

    private void showVNPayMock(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("txnRef", trim(req.getParameter("txnRef")));
        req.setAttribute("amount", trim(req.getParameter("amount")));
        forward(req, resp, "/WEB-INF/views/student/vnpay-mock.jsp");
    }

    private void handleVNPayCallback(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String txnRef = trim(req.getParameter("txnRef"));
        String responseCode = trim(req.getParameter("responseCode"));
        Map<String, String> callback = new HashMap<>();
        callback.put("responseCode", responseCode);
        Payment payment = paymentService.handleCallback(txnRef, callback);
        req.setAttribute("payment", payment);
        forward(req, resp, "/WEB-INF/views/student/payment-result.jsp");
    }

    private void handleMBBankSimulate(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String txnRef = trim(req.getParameter("txnRef"));
        String validTransfer = trim(req.getParameter("validTransfer"));
        Map<String, String> callback = new HashMap<>();
        callback.put("validTransfer", validTransfer);
        Payment payment = paymentService.handleCallback(txnRef, callback);
        req.setAttribute("payment", payment);
        forward(req, resp, "/WEB-INF/views/student/payment-result.jsp");
    }

    private void handleRefund(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String txnRef = trim(req.getParameter("txnRef"));
        Payment payment = paymentService.refund(txnRef);
        req.setAttribute("payment", payment);
        forward(req, resp, "/WEB-INF/views/student/payment-result.jsp");
    }

    private UserAccount resolveCurrentUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            Object currentUser = session.getAttribute("user");
            if (currentUser instanceof UserAccount user) {
                return user;
            }
        }
        return null;
    }

    private String trim(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private void forward(HttpServletRequest req, HttpServletResponse resp, String view)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = req.getRequestDispatcher(view);
        dispatcher.forward(req, resp);
    }

    private boolean isAlreadyEnrolled(String courseID, String studentEmail) {
        return enrollmentDAO.existsByCourseAndStudentEmail(courseID, studentEmail);
    }
}
