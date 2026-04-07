package vn.iotstar.coolenglish.web;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.coolenglish.dao.impl.ExamResultDAO;
import vn.iotstar.coolenglish.entity.ExamResult;
import vn.iotstar.coolenglish.entity.UserAccount;
import vn.iotstar.coolenglish.factory.PartnerIntegrationFactory;
import vn.iotstar.coolenglish.integration.IPartnerIntegration;

@WebServlet(urlPatterns = {
        "/admin/integration/exam-results",
        "/admin/integration/exam-results/sync"
})
public class PartnerIntegrationController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final ExamResultDAO examResultDAO = new ExamResultDAO();
    private final PartnerIntegrationFactory partnerIntegrationFactory = PartnerIntegrationFactory.getInstance();

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
        String servletPath = req.getServletPath();

        if (servletPath.endsWith("/sync") && isPost) {
            syncExamResults(req, resp);
            return;
        }

        showSyncPage(req, resp);
    }

    private void showSyncPage(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<ExamResult> results = examResultDAO.findAll(ExamResult.class);
        results.sort(Comparator
                .comparing(ExamResult::getSyncedAt, Comparator.nullsLast(Comparator.reverseOrder()))
                .thenComparing(ExamResult::getId, Comparator.nullsLast(Comparator.reverseOrder())));

        PaginationSupport.Page<ExamResult> page = PaginationSupport.paginate(
                results,
                req.getParameter("page"),
                PaginationSupport.DEFAULT_PAGE_SIZE);

        req.setAttribute("results", page.getItems());
        req.setAttribute("currentPage", page.getCurrentPage());
        req.setAttribute("totalPages", page.getTotalPages());
        req.setAttribute("totalItems", page.getTotalItems());
        req.setAttribute("pageSize", page.getPageSize());
        req.setAttribute("paginationPath", req.getServletPath());
        req.setAttribute("syncStatus", pullFlashAttribute(req, "syncStatus"));
        req.setAttribute("syncMessage", pullFlashAttribute(req, "syncMessage"));
        req.setAttribute("selectedPartner", pullFlashAttribute(req, "selectedPartner"));

        RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/admin/integration-sync.jsp");
        dispatcher.forward(req, resp);
    }

    private void syncExamResults(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserAccount currentUser = resolveCurrentUser(req);
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp?msg=require_login");
            return;
        }

        String partnerCode = trim(req.getParameter("partnerCode"));
        if (partnerCode == null) {
            partnerCode = "IDP";
        }

        try {
            IPartnerIntegration gateway = partnerIntegrationFactory.buildExamResultSyncGateway(currentUser);
            gateway.syncExamResults(partnerCode);
            setFlash(req, "syncStatus", "success");
            setFlash(req, "syncMessage", "Dong bo ket qua thi tu doi tac thanh cong.");
        } catch (SecurityException | IllegalArgumentException | IllegalStateException e) {
            setFlash(req, "syncStatus", "error");
            setFlash(req, "syncMessage", e.getMessage());
        }

        setFlash(req, "selectedPartner", partnerCode);
        resp.sendRedirect(req.getContextPath() + "/admin/integration/exam-results");
    }

    private void setFlash(HttpServletRequest req, String key, String value) {
        HttpSession session = req.getSession(true);
        session.setAttribute(key, value);
    }

    private String pullFlashAttribute(HttpServletRequest req, String key) {
        HttpSession session = req.getSession(false);
        if (session == null) {
            return null;
        }

        Object value = session.getAttribute(key);
        if (value != null) {
            session.removeAttribute(key);
            return value.toString();
        }

        return null;
    }

    private UserAccount resolveCurrentUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            Object currentUser = session.getAttribute("user");
            if (currentUser instanceof UserAccount) {
                return (UserAccount) currentUser;
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
}
