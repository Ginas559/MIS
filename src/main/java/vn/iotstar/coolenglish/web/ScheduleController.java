package vn.iotstar.coolenglish.web;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.iotstar.coolenglish.dao.impl.RoomDAO;
import vn.iotstar.coolenglish.dao.impl.TeacherDAO;
import vn.iotstar.coolenglish.entity.Room;
import vn.iotstar.coolenglish.entity.Schedule;
import vn.iotstar.coolenglish.entity.Session;
import vn.iotstar.coolenglish.entity.Teacher;
import vn.iotstar.coolenglish.enums.SessionStatus;
import vn.iotstar.coolenglish.service.ScheduleService;

@WebServlet(urlPatterns = {
        "/admin/schedule",
        "/admin/schedule/add",
        "/admin/schedule/view",
        "/admin/schedule/edit",
        "/admin/schedule/delete"
})
public class ScheduleController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final ScheduleService scheduleService = new ScheduleService();
    private final RoomDAO roomDAO = new RoomDAO();
    private final TeacherDAO teacherDAO = new TeacherDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String servletPath = req.getServletPath();

        if (servletPath.endsWith("/add")) {
            showAddForm(req, resp);
            return;
        }

        if (servletPath.endsWith("/view")) {
            viewSchedule(req, resp);
            return;
        }

        if (servletPath.endsWith("/edit")) {
            showEditForm(req, resp);
            return;
        }

        if (servletPath.endsWith("/delete")) {
            deleteSchedule(req, resp);
            return;
        }

        listSchedules(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String servletPath = req.getServletPath();

        if (servletPath.endsWith("/add")) {
            saveSchedule(req, resp);
            return;
        }

        if (servletPath.endsWith("/edit")) {
            updateSchedule(req, resp);
            return;
        }

        listSchedules(req, resp);
    }

    private void listSchedules(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Schedule> allSchedules = scheduleService.findAllSchedules();
        PaginationSupport.Page<Schedule> page = PaginationSupport.paginate(
                allSchedules,
                req.getParameter("page"),
                PaginationSupport.DEFAULT_PAGE_SIZE);

        req.setAttribute("schedules", page.getItems());
        req.setAttribute("currentPage", page.getCurrentPage());
        req.setAttribute("totalPages", page.getTotalPages());
        req.setAttribute("totalItems", page.getTotalItems());
        req.setAttribute("pageSize", page.getPageSize());
        req.setAttribute("paginationPath", req.getServletPath());
        forward(req, resp, "/WEB-INF/views/admin/schedule-list.jsp");
    }

    private void showAddForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Room> rooms = roomDAO.findAll(Room.class);
        List<Teacher> teachers = teacherDAO.findAll(Teacher.class);

        req.setAttribute("rooms", rooms);
        req.setAttribute("teachers", teachers);
        forward(req, resp, "/WEB-INF/views/admin/schedule-form.jsp");
    }

    private void viewSchedule(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String scheduleID = req.getParameter("scheduleID");
        if (scheduleID == null || scheduleID.trim().isEmpty()) {
            listSchedules(req, resp);
            return;
        }

        Schedule schedule = scheduleService.findSchedule(scheduleID);
        if (schedule == null) {
            req.setAttribute("error", "Schedule not found: " + scheduleID);
            listSchedules(req, resp);
            return;
        }

        req.setAttribute("schedule", schedule);
        forward(req, resp, "/WEB-INF/views/admin/schedule-detail.jsp");
    }

    private void saveSchedule(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String scheduleID = req.getParameter("scheduleID");
            String description = req.getParameter("description");
            String createDateStr = req.getParameter("createDate");

            if (scheduleID == null || scheduleID.trim().isEmpty()) {
                req.setAttribute("error", "Schedule ID is required");
                showAddForm(req, resp);
                return;
            }

            Date createDate = new Date();
            if (createDateStr != null && !createDateStr.trim().isEmpty()) {
                try {
                    createDate = new SimpleDateFormat("yyyy-MM-dd").parse(createDateStr);
                } catch (ParseException e) {
                    createDate = new Date();
                }
            }

            // Parse sessions from request
            List<Session> sessions = parseSessions(req);
            if (sessions.isEmpty()) {
                req.setAttribute("error", "At least one session is required");
                showAddForm(req, resp);
                return;
            }

            // Build and save schedule using service
            Schedule schedule = scheduleService.createAndSaveSchedule(
                    scheduleID,
                    createDate,
                    description,
                    sessions);

            req.setAttribute("success", "Schedule created successfully: " + scheduleID);
            listSchedules(req, resp);

        } catch (IllegalArgumentException | IllegalStateException e) {
            req.setAttribute("error", "Failed to create schedule: " + e.getMessage());
            showAddForm(req, resp);
        } catch (Exception e) {
            req.setAttribute("error", "An error occurred: " + e.getMessage());
            showAddForm(req, resp);
        }
    }

    private List<Session> parseSessions(HttpServletRequest req) {
        List<Session> sessions = new ArrayList<>();

        String[] sessionIDs = req.getParameterValues("sessionID[]");
        String[] sessionNames = req.getParameterValues("sessionName[]");
        String[] sessionDates = req.getParameterValues("sessionDate[]");
        String[] startTimes = req.getParameterValues("startTime[]");
        String[] endTimes = req.getParameterValues("endTime[]");
        String[] roomIDs = req.getParameterValues("roomID[]");
        String[] teacherIDs = req.getParameterValues("teacherID[]");

        if (sessionIDs == null || sessionIDs.length == 0) {
            return sessions;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (int i = 0; i < sessionIDs.length; i++) {
            String sessionID = sessionIDs[i];
            if (sessionID == null || sessionID.trim().isEmpty()) {
                continue;
            }

            try {
                Session session = new Session();
                session.setSessionID(sessionID.trim());
                session.setStatus(SessionStatus.SCHEDULED);

                // Parse session name
                if (sessionNames != null && i < sessionNames.length && sessionNames[i] != null
                        && !sessionNames[i].isEmpty()) {
                    session.setSessionName(sessionNames[i].trim());
                }

                // Parse date
                if (sessionDates != null && i < sessionDates.length && sessionDates[i] != null
                        && !sessionDates[i].isEmpty()) {
                    session.setSessionDate(dateFormat.parse(sessionDates[i]));
                }

                // Parse start time
                if (startTimes != null && i < startTimes.length && startTimes[i] != null
                        && !startTimes[i].isEmpty()) {
                    String[] startParts = startTimes[i].split(":");
                    if (startParts.length >= 2) {
                        session.setStartTime(LocalTime.of(Integer.parseInt(startParts[0]),
                                Integer.parseInt(startParts[1])));
                    }
                }

                // Parse end time
                if (endTimes != null && i < endTimes.length && endTimes[i] != null
                        && !endTimes[i].isEmpty()) {
                    String[] endParts = endTimes[i].split(":");
                    if (endParts.length >= 2) {
                        session.setEndTime(LocalTime.of(Integer.parseInt(endParts[0]),
                                Integer.parseInt(endParts[1])));
                    }
                }

                // Set room
                if (roomIDs != null && i < roomIDs.length && roomIDs[i] != null
                        && !roomIDs[i].isEmpty()) {
                    Room room = roomDAO.findById(roomIDs[i], Room.class);
                    session.setRoom(room);
                }

                // Set teacher
                if (teacherIDs != null && i < teacherIDs.length && teacherIDs[i] != null
                        && !teacherIDs[i].isEmpty()) {
                    Teacher teacher = teacherDAO.findById(teacherIDs[i], Teacher.class);
                    session.setTeacher(teacher);
                }

                sessions.add(session);

            } catch (ParseException e) {
                // Skip invalid session
                continue;
            }
        }

        return sessions;
    }

    private void showEditForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String scheduleID = req.getParameter("scheduleID");
        if (scheduleID == null || scheduleID.trim().isEmpty()) {
            listSchedules(req, resp);
            return;
        }

        Schedule schedule = scheduleService.findSchedule(scheduleID);
        if (schedule == null) {
            req.setAttribute("error", "Schedule not found: " + scheduleID);
            listSchedules(req, resp);
            return;
        }

        List<Room> rooms = roomDAO.findAll(Room.class);
        List<Teacher> teachers = teacherDAO.findAll(Teacher.class);

        req.setAttribute("schedule", schedule);
        req.setAttribute("rooms", rooms);
        req.setAttribute("teachers", teachers);
        forward(req, resp, "/WEB-INF/views/admin/schedule-form.jsp");
    }

    private void updateSchedule(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String scheduleID = req.getParameter("scheduleID");
            String description = req.getParameter("description");
            String createDateStr = req.getParameter("createDate");

            if (scheduleID == null || scheduleID.trim().isEmpty()) {
                req.setAttribute("error", "Schedule ID is required");
                showEditForm(req, resp);
                return;
            }

            // Delete old schedule and create new one with updated data
            scheduleService.deleteSchedule(scheduleID);

            Date createDate = new Date();
            if (createDateStr != null && !createDateStr.trim().isEmpty()) {
                try {
                    createDate = new SimpleDateFormat("yyyy-MM-dd").parse(createDateStr);
                } catch (ParseException e) {
                    createDate = new Date();
                }
            }

            // Parse sessions from request
            List<Session> sessions = parseSessions(req);
            if (sessions.isEmpty()) {
                req.setAttribute("error", "At least one session is required");
                showAddForm(req, resp);
                return;
            }

            // Build and save updated schedule
            Schedule schedule = scheduleService.createAndSaveSchedule(
                    scheduleID,
                    createDate,
                    description,
                    sessions);

            req.setAttribute("success", "Schedule updated successfully: " + scheduleID);
            listSchedules(req, resp);

        } catch (Exception e) {
            req.setAttribute("error", "Failed to update schedule: " + e.getMessage());
            showEditForm(req, resp);
        }
    }

    private void deleteSchedule(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String scheduleID = req.getParameter("scheduleID");
            if (scheduleID == null || scheduleID.trim().isEmpty()) {
                req.setAttribute("error", "Schedule ID is required");
                listSchedules(req, resp);
                return;
            }

            scheduleService.deleteSchedule(scheduleID);
            req.setAttribute("success", "Schedule deleted successfully: " + scheduleID);
            listSchedules(req, resp);

        } catch (Exception e) {
            req.setAttribute("error", "Failed to delete schedule: " + e.getMessage());
            listSchedules(req, resp);
        }
    }

    private void forward(HttpServletRequest req, HttpServletResponse resp, String path)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = req.getRequestDispatcher(path);
        dispatcher.forward(req, resp);
    }
}
