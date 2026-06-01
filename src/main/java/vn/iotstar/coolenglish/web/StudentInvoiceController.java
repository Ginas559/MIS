package vn.iotstar.coolenglish.web;

import java.io.ByteArrayOutputStream;
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
import vn.iotstar.coolenglish.invoice.InvoiceDocument;
import vn.iotstar.coolenglish.invoice.InvoicePdfRenderer;
import vn.iotstar.coolenglish.service.StudentInvoiceService;

@WebServlet(urlPatterns = { "/student/invoice/detail", "/student/invoice/pdf" })
public class StudentInvoiceController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final StudentInvoiceService invoiceService = new StudentInvoiceService();
    private final InvoicePdfRenderer pdfRenderer = new InvoicePdfRenderer();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserAccount user = resolveCurrentUser(req);
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp?msg=require_login");
            return;
        }
        if (user.getRole() != UserRole.STUDENT) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Chức năng chỉ dành cho học viên.");
            return;
        }

        String txnRef = trim(req.getParameter("txnRef"));
        if (txnRef == null) {
            req.setAttribute("error", "Thiếu mã giao dịch (txnRef).");
            forward(req, resp, "/WEB-INF/views/student/invoice-detail.jsp");
            return;
        }

        String path = req.getServletPath();
        try {
            InvoiceDocument doc = invoiceService.buildForStudent(txnRef, user.getEmail());
            if ("/student/invoice/pdf".equals(path)) {
                String safeName = doc.getInvoiceNumber().replaceAll("[^a-zA-Z0-9._-]", "_");
                resp.resetBuffer();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try {
                    pdfRenderer.render(doc, baos);
                } catch (IOException | RuntimeException ex) {
                    resp.resetBuffer();
                    resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                            "Khong tao duoc PDF. Kiem tra font NotoSans trong WAR hoac xem log server: " + ex.getMessage());
                    return;
                }
                byte[] pdfBytes = baos.toByteArray();
                resp.setContentType("application/pdf");
                resp.setHeader("Content-Disposition", "attachment; filename=\"hoa-don-" + safeName + ".pdf\"");
                resp.setContentLength(pdfBytes.length);
                resp.getOutputStream().write(pdfBytes);
                resp.getOutputStream().flush();
                return;
            }
            req.setAttribute("invoice", doc);
            forward(req, resp, "/WEB-INF/views/student/invoice-detail.jsp");
        } catch (SecurityException ex) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, ex.getMessage());
        } catch (IllegalArgumentException | IllegalStateException ex) {
            req.setAttribute("error", ex.getMessage());
            forward(req, resp, "/WEB-INF/views/student/invoice-detail.jsp");
        }
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
        String t = value.trim();
        return t.isEmpty() ? null : t;
    }

    private void forward(HttpServletRequest req, HttpServletResponse resp, String view)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = req.getRequestDispatcher(view);
        dispatcher.forward(req, resp);
    }
}
