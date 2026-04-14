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
import vn.iotstar.coolenglish.dao.impl.CourseDAO;
import vn.iotstar.coolenglish.dao.impl.EnglishClassDAO;
import vn.iotstar.coolenglish.dao.impl.RoomDAO;
import vn.iotstar.coolenglish.dao.impl.TeacherDAO;
import vn.iotstar.coolenglish.entity.Course;
import vn.iotstar.coolenglish.entity.EnglishClass;
import vn.iotstar.coolenglish.entity.Room;
import vn.iotstar.coolenglish.entity.Teacher;
import vn.iotstar.coolenglish.entity.UserAccount;
import vn.iotstar.coolenglish.enums.ClassStatus;
import vn.iotstar.coolenglish.facade.EnrollmentFacade;
import vn.iotstar.coolenglish.factory.SecurityAccessFactory;
import vn.iotstar.coolenglish.service.IClassService;

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
    private final RoomDAO roomDAO = new RoomDAO();
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
        UserAccount currentUser = resolveCurrentUser(req);
        String servletPath = req.getServletPath();

        if (servletPath.endsWith("/delete")) {
            deleteClass(req, resp);
            return;
        }

        if (servletPath.endsWith("/add")) {
            if (isPost) {
                saveClass(req, resp, false, currentUser);
            } else {
                showForm(req, resp, new EnglishClass());
            }
            return;
        }

        if (servletPath.endsWith("/update")) {
            if (isPost) {
                saveClass(req, resp, true, currentUser);
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
                englishClassDAO.findAll(EnglishClass.class),
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
        List<Room> rooms = roomDAO.findAll(Room.class);
        List<Teacher> teachers = teacherDAO.findAll(Teacher.class);
        req.setAttribute("classroom", clazz);
        req.setAttribute("courses", courses);
        req.setAttribute("rooms", rooms);
        req.setAttribute("teachers", teachers);
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

    private void saveClass(HttpServletRequest req, HttpServletResponse resp, boolean update, UserAccount currentUser)
            throws IOException {
        String courseID = trim(req.getParameter("courseID"));
        String roomID = trim(req.getParameter("roomID"));

        if (courseID != null && courseDAO.findById(courseID, Course.class) == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Course does not exist.");
            return;
        }

        if (roomID != null && roomDAO.findById(roomID, Room.class) == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Room does not exist.");
            return;
        }

        EnglishClass clazz = new EnglishClass();
        clazz.setClassID(getClassID(req));
        clazz.setClassName(trim(req.getParameter("className")));
        clazz.setCourseID(courseID);
        clazz.setRoomID(roomID);
        clazz.setMaxCapacity(parseInteger(req.getParameter("maxCapacity")));
        clazz.setCurrentEnrollment(parseInteger(req.getParameter("currentEnrollment")));
        clazz.setStatus(parseStatus(req.getParameter("status")));
        clazz.updateInternalState();

        if (update) {
            englishClassDAO.update(clazz);
        } else {
            englishClassDAO.insert(clazz);
        }

        String teacherIDRaw = trim(req.getParameter("teacherID"));
        if (teacherIDRaw != null) {
            try {
                IClassService classService = securityAccessFactory.getClassService(currentUser);
                classService.assignTeacher(clazz.getClassID(), Long.valueOf(teacherIDRaw));
            } catch (NumberFormatException ex) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Teacher ID is invalid.");
                return;
            } catch (SecurityException ex) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, ex.getMessage());
                return;
            } catch (IllegalArgumentException ex) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
                return;
            }
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

    private void showRegisterForm(HttpServletRequest req, HttpServletResponse resp,
            String studentEmail, String registerError, String registerSuccess)
            throws ServletException, IOException {
        String classID = getClassID(req);
        if (classID == null) {
            redirectToList(req, resp);
            return;
        }

        EnglishClass clazz = englishClassDAO.findByClassID(classID);
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
        if (session != null) {
            Object currentUser = session.getAttribute("user");
            if (currentUser instanceof UserAccount user) {
                return user;
            }
        }
        return null;
    }
}

