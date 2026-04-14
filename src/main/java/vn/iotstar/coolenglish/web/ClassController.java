package vn.iotstar.coolenglish.web;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.coolenglish.dao.impl.CourseDAO;
import vn.iotstar.coolenglish.dao.impl.EnglishClassDAO;
import vn.iotstar.coolenglish.dao.impl.TeacherDAO;
import vn.iotstar.coolenglish.entity.Course;
import vn.iotstar.coolenglish.entity.EnglishClass;
import vn.iotstar.coolenglish.entity.Teacher;
import vn.iotstar.coolenglish.entity.UserAccount;
import vn.iotstar.coolenglish.enums.ClassStatus;
import vn.iotstar.coolenglish.facade.EnrollmentFacade;
import vn.iotstar.coolenglish.factory.SecurityAccessFactory;

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
    private final CourseDAO courseDAO = new CourseDAO();
    private final TeacherDAO teacherDAO = new TeacherDAO();
    private final SecurityAccessFactory securityAccessFactory = SecurityAccessFactory.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleRequest(req, resp, false);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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
                try {
                    saveClass(req, resp, false);
                } catch (IllegalArgumentException ex) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
                }
            } else {
                showForm(req, resp, new EnglishClass());
            }
            return;
        }

        if (servletPath.endsWith("/update")) {
            if (isPost) {
                try {
                    saveClass(req, resp, true);
                } catch (IllegalArgumentException ex) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
                }
            } else {
                showEditForm(req, resp);
            }
            return;
        }

        if (servletPath.endsWith("/register")) {
            if (isPost) {
                registerStudent(req, resp);
            } else {
                showRegisterForm(req, resp, null, null, null);
            }
            return;
        }

        showList(req, resp);
    }

    private void showList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PaginationSupport.Page<EnglishClass> page = PaginationSupport.paginate(
                englishClassDAO.findAllWithDetails(),
                req.getParameter("page"),
                PaginationSupport.DEFAULT_PAGE_SIZE);

        req.setAttribute("classes", page.getItems());
        req.setAttribute("currentPage", page.getCurrentPage());
        req.setAttribute("totalPages", page.getTotalPages());
        req.setAttribute("totalItems", page.getTotalItems());
        req.setAttribute("pageSize", page.getPageSize());
        req.setAttribute("paginationPath", req.getServletPath());
        forward(req, resp, "/WEB-INF/views/admin/class-list.jsp");
    }

    private void showForm(HttpServletRequest req, HttpServletResponse resp, EnglishClass clazz)
            throws ServletException, IOException {
        List<Course> courses = courseDAO.findAll(Course.class);
        List<Teacher> teachers = teacherDAO.findAll(Teacher.class);
        req.setAttribute("classroom", clazz);
        req.setAttribute("courses", courses);
        req.setAttribute("teachers", teachers);
        forward(req, resp, "/WEB-INF/views/admin/class-form.jsp");
    }

    private void showEditForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String classID = getClassID(req);
        if (classID == null) {
            redirectToList(req, resp);
            return;
        }

        EnglishClass clazz = englishClassDAO.findByClassIDWithDetails(classID);
        if (clazz == null) {
            redirectToList(req, resp);
            return;
        }

        showForm(req, resp, clazz);
    }

    private void saveClass(HttpServletRequest req, HttpServletResponse resp, boolean update)
            throws IOException {
        UserAccount currentUser = resolveCurrentUser(req);
        if (currentUser == null) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Login is required.");
            return;
        }

        String classID = getClassID(req);
        String className = trim(req.getParameter("className"));
        String courseID = trim(req.getParameter("courseID"));
        String teacherIDRaw = trim(req.getParameter("teacherID"));
        LocalDate startDate = parseDate(req.getParameter("startDate"));
        LocalDate endDate = parseDate(req.getParameter("endDate"));
        Integer maxCapacity = parseInteger(req.getParameter("maxCapacity"));

        if (classID == null || className == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Class ID and class name are required.");
            return;
        }

        if (courseID != null && courseDAO.findById(courseID, Course.class) == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Course does not exist.");
            return;
        }

        if (startDate == null || endDate == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Start date and end date are required.");
            return;
        }

        if (maxCapacity == null || maxCapacity <= 0) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Max capacity must be greater than 0.");
            return;
        }

        if (teacherIDRaw == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Teacher is required.");
            return;
        }

        Teacher teacher;
        try {
            Long teacherId = Long.valueOf(teacherIDRaw);
            teacher = teacherDAO.findById(teacherId, Teacher.class);
            if (teacher == null) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Teacher not found.");
                return;
            }
        } catch (NumberFormatException ex) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Teacher ID is invalid.");
            return;
        }

        EnglishClass clazz = update ? englishClassDAO.findByClassIDWithDetails(classID) : null;
        if (update && clazz == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Class not found.");
            return;
        }

        if (clazz == null) {
            clazz = new EnglishClass(classID, className, teacher, startDate, endDate, maxCapacity);
        } else {
            clazz.setClassID(classID);
            clazz.setClassName(className);
            clazz.setStartDate(startDate);
            clazz.setEndDate(endDate);
            clazz.setMaxCapacity(maxCapacity);
        }
        clazz.setCourseID(courseID);
        clazz.setCurrentEnrollment(parseInteger(req.getParameter("currentEnrollment")));
        clazz.setStatus(parseStatus(req.getParameter("status")));
        clazz.updateInternalState();

        if (update) {
            englishClassDAO.update(clazz);
        } else {
            englishClassDAO.insert(clazz);
        }

        securityAccessFactory.getClassService(currentUser).assignTeacher(clazz.getClassID(), teacher.getId());


        redirectToList(req, resp);
    }

    private void deleteClass(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String classID = getClassID(req);
        if (classID != null) {
            englishClassDAO.delete(classID, EnglishClass.class);
        }
        redirectToList(req, resp);
    }

    private void showRegisterForm(HttpServletRequest req, HttpServletResponse resp,
            String studentEmail, String registerError, String registerSuccess)
            throws ServletException, IOException {
        String classID = getClassID(req);
        if (classID == null) {
            redirectToList(req, resp);
            return;
        }

        EnglishClass clazz = englishClassDAO.findByClassIDWithDetails(classID);
        req.setAttribute("classroom", clazz);
        req.setAttribute("studentEmail", studentEmail);
        req.setAttribute("registerError", registerError);
        req.setAttribute("registerSuccess", registerSuccess);
        forward(req, resp, "/WEB-INF/views/admin/class-register.jsp");
    }

    private void registerStudent(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String classID = getClassID(req);
        String studentEmail = trim(req.getParameter("studentEmail"));

        if (classID == null) {
            redirectToList(req, resp);
            return;
        }

        try {
            EnrollmentFacade.getInstance().enrollStudent(classID, studentEmail);
            showRegisterForm(req, resp, null, null, "Ghi danh thanh cong.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            showRegisterForm(req, resp, studentEmail, e.getMessage(), null);
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

    private LocalDate parseDate(String value) {
        String trimmed = trim(value);
        if (trimmed == null) {
            return null;
        }
        try {
            return LocalDate.parse(trimmed);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Date value is invalid: " + trimmed, ex);
        }
    }

    private ClassStatus parseStatus(String value) {
        String normalized = trim(value);
        if (normalized == null) {
            return ClassStatus.PLANNED;
        }

        try {
            return ClassStatus.valueOf(normalized.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return ClassStatus.PLANNED;
        }
    }

    private String trim(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private UserAccount resolveCurrentUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) {
            return null;
        }

        Object user = session.getAttribute("user");
        return user instanceof UserAccount account ? account : null;
    }

}

