package vn.iotstar.coolenglish.web;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.coolenglish.dao.impl.CourseDAO;
import vn.iotstar.coolenglish.entity.Course;
import vn.iotstar.coolenglish.entity.UserAccount;
import vn.iotstar.coolenglish.enums.UserRole;
import vn.iotstar.coolenglish.factory.SecurityAccessFactory;
import vn.iotstar.coolenglish.service.ICourseService;

@WebServlet(urlPatterns = { "/course", "/admin/course", "/admin/course/add", "/admin/course/update", "/admin/course/delete",
        "/admin/course/update-fee" })
public class CourseController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final CourseDAO courseDAO = new CourseDAO();
    private final SecurityAccessFactory securityAccessFactory = SecurityAccessFactory.getInstance();
    private static final String STATUS_ACTIVE = "ACTIVE";
    private static final String STATUS_INACTIVE = "INACTIVE";

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
        UserAccount currentUser = resolveCurrentUser(req);
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp?msg=require_login");
            return;
        }

        String servletPath = req.getServletPath();

        if ("/course".equals(servletPath)) {
            showList(req, resp, currentUser, false);
            return;
        }

        if (servletPath.endsWith("/delete")) {
            if (!isAdmin(currentUser)) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Chi ADMIN moi co quyen xoa khoa hoc.");
                return;
            }
            deleteCourse(req, resp);
            return;
        }

        if (servletPath.endsWith("/update-fee")) {
            if (!isAdmin(currentUser)) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Chi ADMIN moi co quyen cap nhat hoc phi.");
                return;
            }
            if (isPost) {
                updateCourseFee(req, resp, currentUser);
            } else {
                redirectToList(req, resp);
            }
            return;
        }

        if (servletPath.endsWith("/add")) {
            if (!canManageCourse(currentUser)) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Chi ADMIN hoac STAFF moi co quyen them khoa hoc.");
                return;
            }
            if (isPost) {
                saveCourse(req, resp, false);
            } else {
                showForm(req, resp, new Course());
            }
            return;
        }

        if (servletPath.endsWith("/update")) {
            if (!isAdmin(currentUser)) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Chi ADMIN moi co quyen sua khoa hoc.");
                return;
            }
            if (isPost) {
                saveCourse(req, resp, true);
            } else {
                showEditForm(req, resp);
            }
            return;
        }

        if (!canViewCourseManagement(currentUser)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Ban khong co quyen truy cap quan ly khoa hoc.");
            return;
        }

        showList(req, resp, currentUser, true);
    }

    private void showList(HttpServletRequest req, HttpServletResponse resp, UserAccount currentUser, boolean managementView)
            throws ServletException, IOException {
        ICourseService courseService = securityAccessFactory.getCourseService(currentUser);
        req.setAttribute("courses", courseService.findAll());
        req.setAttribute("managementView", managementView);
        req.setAttribute("canManage", canManageCourse(currentUser));
        forward(req, resp, "/WEB-INF/views/admin/course-list.jsp");
    }

    private void showForm(HttpServletRequest req, HttpServletResponse resp, Course course)
            throws ServletException, IOException {
        req.setAttribute("course", course);
        forward(req, resp, "/WEB-INF/views/admin/course-form.jsp");
    }

    private void showEditForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String courseID = getCourseID(req);
        if (courseID == null || courseID.isBlank()) {
            redirectToList(req, resp);
            return;
        }

        Course course = courseDAO.findById(courseID, Course.class);
        if (course == null) {
            redirectToList(req, resp);
            return;
        }

        showForm(req, resp, course);
    }

    private void saveCourse(HttpServletRequest req, HttpServletResponse resp, boolean update)
            throws IOException {
        Course course = new Course();
        course.setCourseID(getCourseID(req));
        course.setCourseName(trim(req.getParameter("courseName")));
        course.setDescription(trim(req.getParameter("description")));
        course.setLevel(trim(req.getParameter("level")));
        course.setDuration(parseInteger(req.getParameter("duration")));
        course.setFee(parseDouble(req.getParameter("fee")));
        course.setStatus(normalizeCourseStatus(req.getParameter("status")));

        if (update) {
            courseDAO.update(course);
        } else {
            courseDAO.insert(course);
        }
        redirectToList(req, resp);
    }

    private void deleteCourse(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String courseID = getCourseID(req);
        if (courseID != null && !courseID.isBlank()) {
            courseDAO.delete(courseID, Course.class);
        }
        redirectToList(req, resp);
    }

    private void updateCourseFee(HttpServletRequest req, HttpServletResponse resp, UserAccount currentUser)
            throws IOException {
        String courseID = getCourseID(req);
        Double newFee = parseDouble(req.getParameter("newFee"));
        ICourseService courseService = securityAccessFactory.getCourseService(currentUser);

        try {
            courseService.updateCourseFee(courseID, newFee);
            redirectToList(req, resp);
        } catch (SecurityException e) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    private void forward(HttpServletRequest req, HttpServletResponse resp, String view)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = req.getRequestDispatcher(view);
        dispatcher.forward(req, resp);
    }

    private void redirectToList(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.sendRedirect(req.getContextPath() + "/admin/course");
    }

    private String getCourseID(HttpServletRequest req) {
        String courseID = trim(req.getParameter("courseID"));
        return courseID != null ? courseID : trim(req.getParameter("id"));
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }

    private Integer parseInteger(String value) {
        String trimmed = trim(value);
        return trimmed == null || trimmed.isEmpty() ? null : Integer.valueOf(trimmed);
    }

    private Double parseDouble(String value) {
        String trimmed = trim(value);
        return trimmed == null || trimmed.isEmpty() ? null : Double.valueOf(trimmed);
    }

    private String normalizeCourseStatus(String value) {
        String status = trim(value);
        if (status == null || status.isEmpty()) {
            return STATUS_ACTIVE;
        }

        if (STATUS_ACTIVE.equalsIgnoreCase(status)) {
            return STATUS_ACTIVE;
        }

        if (STATUS_INACTIVE.equalsIgnoreCase(status)) {
            return STATUS_INACTIVE;
        }

        return STATUS_ACTIVE;
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

    private boolean isAdmin(UserAccount user) {
        return user != null && user.getRole() == UserRole.ADMIN;
    }

    private boolean canViewCourseManagement(UserAccount user) {
        if (user == null) {
            return false;
        }
        return user.getRole() == UserRole.ADMIN || user.getRole() == UserRole.STAFF;
    }

    private boolean canManageCourse(UserAccount user) {
        return canViewCourseManagement(user);
    }
}

