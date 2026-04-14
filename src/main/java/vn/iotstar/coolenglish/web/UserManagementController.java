package vn.iotstar.coolenglish.web;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.iotstar.coolenglish.dao.impl.PersonDAO;
import vn.iotstar.coolenglish.entity.Person;
import vn.iotstar.coolenglish.entity.UserAccount;
import vn.iotstar.coolenglish.factory.SecurityAccessFactory;
import vn.iotstar.coolenglish.service.IUserService;

@WebServlet(urlPatterns = { "/admin/users", "/admin/users/detail", "/admin/users/toggle" })
public class UserManagementController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final SecurityAccessFactory factory = SecurityAccessFactory.getInstance();
    private final PersonDAO personDAO = new PersonDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String servletPath = req.getServletPath();
        if ("/admin/users/detail".equals(servletPath)) {
            showDetail(req, resp);
            return;
        }

        UserAccount currentUser = (UserAccount) req.getSession().getAttribute("user");
        IUserService userService = factory.getUserService(currentUser);

        try {
            List<UserAccount> users = userService.getAllUsers();
            PaginationSupport.Page<UserAccount> page = PaginationSupport.paginate(
                    users,
                    req.getParameter("page"),
                    PaginationSupport.DEFAULT_PAGE_SIZE);

            req.setAttribute("userList", page.getItems());
            req.setAttribute("personNameByRelatedId", resolvePersonNames(page.getItems()));
            req.setAttribute("currentPage", page.getCurrentPage());
            req.setAttribute("totalPages", page.getTotalPages());
            req.setAttribute("totalItems", page.getTotalItems());
            req.setAttribute("pageSize", page.getPageSize());
            req.setAttribute("paginationPath", req.getServletPath());
            req.getRequestDispatcher("/WEB-INF/views/admin/user-list.jsp").forward(req, resp);
        } catch (SecurityException e) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp?msg=forbidden");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if ("/admin/users/toggle".equals(req.getServletPath())) {
            toggleUser(req, resp);
            return;
        }
        doGet(req, resp);
    }

    private void showDetail(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserAccount currentUser = (UserAccount) req.getSession().getAttribute("user");
        IUserService userService = factory.getUserService(currentUser);

        try {
            Long userID = parseUserId(req.getParameter("id"));
            UserAccount user = userService.getUserById(userID);
            if (user == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
                return;
            }

            req.setAttribute("user", user);
            req.setAttribute("person", resolvePerson(user.getRelatedID()));
            req.setAttribute("personName", resolvePersonName(user.getRelatedID()));
            req.getRequestDispatcher("/WEB-INF/views/admin/user-detail.jsp").forward(req, resp);
        } catch (NumberFormatException ex) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid user id");
        } catch (SecurityException ex) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp?msg=forbidden");
        }
    }

    private void toggleUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserAccount currentUser = (UserAccount) req.getSession().getAttribute("user");
        IUserService userService = factory.getUserService(currentUser);

        try {
            Long userID = parseUserId(req.getParameter("id"));
            userService.toggleUserActive(userID);
            resp.sendRedirect(req.getContextPath() + "/admin/users/detail?id=" + userID + "&msg=toggled");
        } catch (NumberFormatException ex) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid user id");
        } catch (IllegalArgumentException ex) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, ex.getMessage());
        } catch (SecurityException ex) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp?msg=forbidden");
        }
    }

    private Map<Long, String> resolvePersonNames(List<UserAccount> users) {
        Map<Long, String> personNameByRelatedId = new HashMap<>();
        for (UserAccount user : users) {
            Long relatedId = user.getRelatedID();
            if (relatedId == null || personNameByRelatedId.containsKey(relatedId)) {
                continue;
            }
            Person person = personDAO.findById(relatedId, Person.class);
            if (person != null && person.getFullName() != null && !person.getFullName().isBlank()) {
                personNameByRelatedId.put(relatedId, person.getFullName());
            }
        }
        return personNameByRelatedId;
    }

    private Person resolvePerson(Long relatedID) {
        if (relatedID == null) {
            return null;
        }
        return personDAO.findById(relatedID, Person.class);
    }

    private String resolvePersonName(Long relatedID) {
        Person person = resolvePerson(relatedID);
        return person != null ? person.getFullName() : null;
    }

    private Long parseUserId(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("User id is required");
        }
        return Long.valueOf(raw.trim());
    }
}



