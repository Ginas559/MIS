package vn.iotstar.coolenglish.web;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.coolenglish.audit.command.AuditCommandInvoker;
import vn.iotstar.coolenglish.dao.impl.AuditLogDAO;
import vn.iotstar.coolenglish.entity.AuditLog;
import vn.iotstar.coolenglish.entity.UserAccount;
import vn.iotstar.coolenglish.enums.UserRole;

@WebServlet(urlPatterns = { "/admin/audit-logs", "/admin/audit-logs/undo" })
public class AuditLogController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter DISPLAY_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final AuditLogDAO auditLogDAO = new AuditLogDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        showAuditLogs(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getServletPath().endsWith("/undo")) {
            undoLastAudit(req, resp);
            return;
        }
        showAuditLogs(req, resp);
    }

    private void showAuditLogs(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<AuditLog> logs = auditLogDAO.findAll(AuditLog.class);
        logs.sort(Comparator
                .comparing(AuditLog::getChangedAt, Comparator.nullsLast(Comparator.reverseOrder()))
                .thenComparing(AuditLog::getAuditId, Comparator.nullsLast(Comparator.reverseOrder())));

        List<AuditLogRow> rows = logs.stream()
                .map(this::toRow)
                .toList();

        PaginationSupport.Page<AuditLogRow> page = PaginationSupport.paginate(
                rows,
                req.getParameter("page"),
                PaginationSupport.DEFAULT_PAGE_SIZE);

        req.setAttribute("auditRows", page.getItems());
        req.setAttribute("currentPage", page.getCurrentPage());
        req.setAttribute("totalPages", page.getTotalPages());
        req.setAttribute("totalItems", page.getTotalItems());
        req.setAttribute("pageSize", page.getPageSize());
        req.setAttribute("paginationPath", "/admin/audit-logs");
        req.setAttribute("undoStatus", req.getParameter("undo"));
        req.setAttribute("isAdmin", isAdmin(resolveCurrentUser(req)));

        RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/admin/audit-log-list.jsp");
        dispatcher.forward(req, resp);
    }

    private void undoLastAudit(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserAccount currentUser = resolveCurrentUser(req);
        if (!isAdmin(currentUser)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Chi ADMIN moi co quyen undo audit log.");
            return;
        }

        try {
            boolean undone = AuditCommandInvoker.getInstance().undoLast();
            if (undone) {
                redirectWithUndoStatus(req, resp, "success");
            } else {
                redirectWithUndoStatus(req, resp, "empty");
            }
        } catch (SecurityException ex) {
            redirectWithUndoStatus(req, resp, "denied");
        }
    }

    private void redirectWithUndoStatus(HttpServletRequest req, HttpServletResponse resp, String status) throws IOException {
        resp.sendRedirect(req.getContextPath() + "/admin/audit-logs?undo=" + status);
    }

    private AuditLogRow toRow(AuditLog log) {
        String actor = compact(log.getActorUsername());
        if (actor == null) {
            actor = "SYSTEM";
        }
        if (log.getActorRole() != null && !log.getActorRole().isBlank()) {
            actor = actor + " (" + log.getActorRole() + ")";
        }

        String changedAt = log.getChangedAt() != null ? DISPLAY_TIME.format(log.getChangedAt()) : "";
        String detailSummary = summarizeDetails(log.getDetails());

        return new AuditLogRow(
                log.getAuditId(),
                compact(log.getAction()),
                compact(log.getEntityName()),
                compact(log.getEntityId()),
                actor,
                changedAt,
                detailSummary);
    }

    private String summarizeDetails(String details) {
        if (details == null || details.isBlank()) {
            return "";
        }

        try {
            JsonNode root = OBJECT_MAPPER.readTree(details);
            JsonNode state = root.get("state");
            if (state == null || !state.isObject()) {
                return details.length() > 180 ? details.substring(0, 180) + "..." : details;
            }

            StringBuilder sb = new StringBuilder();
            state.fields().forEachRemaining(entry -> {
                if (sb.length() >= 160) {
                    return;
                }
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append(entry.getKey()).append("=");
                JsonNode value = entry.getValue();
                if (value == null || value.isNull()) {
                    sb.append("null");
                } else if (value.isTextual()) {
                    sb.append(value.asText());
                } else {
                    sb.append(value.toString());
                }
            });
            String result = sb.toString();
            return result.length() > 180 ? result.substring(0, 180) + "..." : result;
        } catch (Exception ignored) {
            String legacySummary = summarizeLegacyMap(details);
            return legacySummary.length() > 180 ? legacySummary.substring(0, 180) + "..." : legacySummary;
        }
    }

    private String summarizeLegacyMap(String details) {
        int stateIndex = details.indexOf("state={");
        if (stateIndex < 0) {
            return details;
        }

        int openBrace = details.indexOf('{', stateIndex);
        if (openBrace < 0) {
            return details;
        }

        int depth = 0;
        int closeBrace = -1;
        for (int i = openBrace; i < details.length(); i++) {
            char ch = details.charAt(i);
            if (ch == '{') {
                depth++;
            } else if (ch == '}') {
                depth--;
                if (depth == 0) {
                    closeBrace = i;
                    break;
                }
            }
        }

        if (closeBrace <= openBrace) {
            return details;
        }

        String stateBody = details.substring(openBrace + 1, closeBrace).trim();
        if (stateBody.isEmpty()) {
            return "";
        }
        return stateBody;
    }

    private String compact(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
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

    private boolean isAdmin(UserAccount user) {
        return user != null && user.getRole() == UserRole.ADMIN;
    }

    public static final class AuditLogRow {
        private final Long auditId;
        private final String action;
        private final String entityName;
        private final String entityId;
        private final String actor;
        private final String changedAt;
        private final String details;

        public AuditLogRow(Long auditId, String action, String entityName, String entityId, String actor, String changedAt,
                String details) {
            this.auditId = auditId;
            this.action = action;
            this.entityName = entityName;
            this.entityId = entityId;
            this.actor = actor;
            this.changedAt = changedAt;
            this.details = details;
        }

        public Long getAuditId() {
            return auditId;
        }

        public String getAction() {
            return action;
        }

        public String getEntityName() {
            return entityName;
        }

        public String getEntityId() {
            return entityId;
        }

        public String getActor() {
            return actor;
        }

        public String getChangedAt() {
            return changedAt;
        }

        public String getDetails() {
            return details;
        }
    }
}
