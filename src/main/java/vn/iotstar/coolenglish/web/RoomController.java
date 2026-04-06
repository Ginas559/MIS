package vn.iotstar.coolenglish.web;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.iotstar.coolenglish.dao.impl.RoomDAO;
import vn.iotstar.coolenglish.entity.Room;

@WebServlet(urlPatterns = { "/admin/room", "/admin/room/add", "/admin/room/update", "/admin/room/delete" })
public class RoomController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final String STATUS_AVAILABLE = "AVAILABLE";
    private static final String STATUS_OCCUPIED = "OCCUPIED";
    private static final String STATUS_MAINTENANCE = "MAINTENANCE";

    private final RoomDAO roomDAO = new RoomDAO();

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
            deleteRoom(req, resp);
            return;
        }

        if (servletPath.endsWith("/add")) {
            if (isPost) {
                saveRoom(req, resp, false);
            } else {
                showForm(req, resp, new Room());
            }
            return;
        }

        if (servletPath.endsWith("/update")) {
            if (isPost) {
                saveRoom(req, resp, true);
            } else {
                showEditForm(req, resp);
            }
            return;
        }

        showList(req, resp);
    }

    private void showList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("rooms", roomDAO.findAll(Room.class));
        forward(req, resp, "/WEB-INF/views/admin/room-list.jsp");
    }

    private void showForm(HttpServletRequest req, HttpServletResponse resp, Room room)
            throws ServletException, IOException {
        req.setAttribute("room", room);
        forward(req, resp, "/WEB-INF/views/admin/room-form.jsp");
    }

    private void showEditForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String roomID = getRoomID(req);
        if (roomID == null || roomID.isBlank()) {
            redirectToList(req, resp);
            return;
        }

        Room room = roomDAO.findById(roomID, Room.class);
        if (room == null) {
            redirectToList(req, resp);
            return;
        }

        showForm(req, resp, room);
    }

    private void saveRoom(HttpServletRequest req, HttpServletResponse resp, boolean update)
            throws IOException {
        Room room = new Room();
        room.setRoomID(getRoomID(req));
        room.setRoomName(trim(req.getParameter("roomName")));
        room.setCapacity(parseInteger(req.getParameter("capacity")));
        room.setLocation(trim(req.getParameter("location")));
        room.setStatus(normalizeRoomStatus(req.getParameter("status")));

        if (update) {
            roomDAO.update(room);
        } else {
            roomDAO.insert(room);
        }
        redirectToList(req, resp);
    }

    private void deleteRoom(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String roomID = getRoomID(req);
        if (roomID != null && !roomID.isBlank()) {
            roomDAO.delete(roomID, Room.class);
        }
        redirectToList(req, resp);
    }

    private void forward(HttpServletRequest req, HttpServletResponse resp, String view)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = req.getRequestDispatcher(view);
        dispatcher.forward(req, resp);
    }

    private void redirectToList(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.sendRedirect(req.getContextPath() + "/admin/room");
    }

    private String getRoomID(HttpServletRequest req) {
        String roomID = trim(req.getParameter("roomID"));
        return roomID != null ? roomID : trim(req.getParameter("id"));
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }

    private Integer parseInteger(String value) {
        String trimmed = trim(value);
        return trimmed == null || trimmed.isEmpty() ? null : Integer.valueOf(trimmed);
    }

    private String normalizeRoomStatus(String value) {
        String status = trim(value);
        if (status == null || status.isEmpty()) {
            return STATUS_AVAILABLE;
        }

        if (STATUS_AVAILABLE.equals(status) || STATUS_OCCUPIED.equals(status) || STATUS_MAINTENANCE.equals(status)) {
            return status;
        }

        return STATUS_AVAILABLE;
    }
}

