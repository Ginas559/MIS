package vn.iotstar.coolenglish.web;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.iotstar.coolenglish.dao.impl.EnglishClassDAO;
import vn.iotstar.coolenglish.entity.EnglishClass;
import vn.iotstar.coolenglish.enums.ClassStatus;
import vn.iotstar.coolenglish.facade.EnrollmentFacade;

@WebServlet(urlPatterns = {
        "/admin/class",
        "/admin/class/add",
        "/admin/class/update",
        "/admin/class/delete",
        "/admin/class/register"
})
public class ClassController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final EnglishClassDAO englishClassDAO = new EnglishClassDAO();

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

        if (servletPath.endsWith("/delete")) {
            deleteClass(req, resp);
            return;
        }

        if (servletPath.endsWith("/add")) {
            if (isPost) {
                saveClass(req, resp, false);
            } else {
                showForm(req, resp, new EnglishClass());
            }
            return;
        }

        if (servletPath.endsWith("/update")) {
            if (isPost) {
                saveClass(req, resp, true);
            } else {
                showEditForm(req, resp);
            }
            return;
        }

        if (servletPath.endsWith("/register") && isPost) {
            registerStudent(req, resp);
            return;
        }

        showList(req, resp);
    }

    private void showList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("classes", englishClassDAO.findAll(EnglishClass.class));
        forward(req, resp, "/WEB-INF/views/admin/class-list.jsp");
    }

    private void showForm(HttpServletRequest req, HttpServletResponse resp, EnglishClass clazz)
            throws ServletException, IOException {
        req.setAttribute("classroom", clazz);
        forward(req, resp, "/WEB-INF/views/admin/class-form.jsp");
    }

    private void showEditForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String classID = getClassID(req);
        if (classID == null) {
            redirectToList(req, resp);
            return;
        }

        EnglishClass clazz = englishClassDAO.findByClassID(classID);
        if (clazz == null) {
            redirectToList(req, resp);
            return;
        }

        showForm(req, resp, clazz);
    }

    private void saveClass(HttpServletRequest req, HttpServletResponse resp, boolean update) throws IOException {
        EnglishClass clazz = new EnglishClass();
        clazz.setClassID(getClassID(req));
        clazz.setClassName(trim(req.getParameter("className")));
        clazz.setCourseID(trim(req.getParameter("courseID")));
        clazz.setRoomID(trim(req.getParameter("roomID")));
        clazz.setMaxCapacity(parseInteger(req.getParameter("maxCapacity")));
        clazz.setCurrentEnrollment(parseInteger(req.getParameter("currentEnrollment")));
        clazz.setStatus(parseStatus(req.getParameter("status")));
        clazz.updateInternalState();

        if (update) {
            englishClassDAO.update(clazz);
        } else {
            englishClassDAO.insert(clazz);
        }

        redirectToList(req, resp);
    }

    private void deleteClass(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String classID = getClassID(req);
        if (classID != null) {
            englishClassDAO.delete(classID, EnglishClass.class);
        }
        redirectToList(req, resp);
    }

    private void registerStudent(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String classID = getClassID(req);
        String studentEmail = trim(req.getParameter("studentEmail"));

        try {
            EnrollmentFacade.getInstance().enrollStudent(classID, studentEmail);
            redirectToList(req, resp);
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (IllegalStateException e) {
            resp.sendError(HttpServletResponse.SC_CONFLICT, e.getMessage());
        }
    }

    private void forward(HttpServletRequest req, HttpServletResponse resp, String view)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = req.getRequestDispatcher(view);
        dispatcher.forward(req, resp);
    }

    private void redirectToList(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.sendRedirect(req.getContextPath() + "/admin/class");
    }

    private String getClassID(HttpServletRequest req) {
        String classID = trim(req.getParameter("classID"));
        return classID != null ? classID : trim(req.getParameter("id"));
    }

    private Integer parseInteger(String value) {
        String trimmed = trim(value);
        return trimmed == null ? null : Integer.valueOf(trimmed);
    }

    private ClassStatus parseStatus(String value) {
        String normalized = trim(value);
        if (normalized == null) {
            return ClassStatus.OPEN;
        }

        try {
            return ClassStatus.valueOf(normalized.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return ClassStatus.OPEN;
        }
    }

    private String trim(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}

