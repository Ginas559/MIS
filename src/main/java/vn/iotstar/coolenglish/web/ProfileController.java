package vn.iotstar.coolenglish.web;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import vn.iotstar.coolenglish.dao.impl.PersonDAO;
import vn.iotstar.coolenglish.entity.Person;
import vn.iotstar.coolenglish.entity.UserAccount;
import vn.iotstar.coolenglish.enums.Gender;
import vn.iotstar.coolenglish.util.UploadUtils;

@MultipartConfig(maxFileSize = 5242880, maxRequestSize = 5242880)
@WebServlet("/profile")
public class ProfileController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final PersonDAO personDAO = new PersonDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html; charset=UTF-8");
        UserAccount currentUser = resolveCurrentUser(req);
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login?msg=require_login");
            return;
        }

        Person person = resolvePerson(currentUser);
        if (person == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp?msg=require_login");
            return;
        }

        req.setAttribute("person", person);
        req.setAttribute("email", currentUser.getEmail());
        RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/profile.jsp");
        dispatcher.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");

        UserAccount currentUser = resolveCurrentUser(req);
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login?msg=require_login");
            return;
        }

        Person person = resolvePerson(currentUser);
        if (person == null) {
            resp.sendRedirect(req.getContextPath() + "/login?msg=require_login");
            return;
        }

        String fullName = normalize(req.getParameter("fullName"));
        String phone = normalize(req.getParameter("phone"));
        Gender gender = parseGender(req.getParameter("gender"));

        if (fullName == null) {
            req.setAttribute("person", person);
            req.setAttribute("email", currentUser.getEmail());
            req.setAttribute("error", "Họ và tên không được để trống.");
            RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/profile.jsp");
            dispatcher.forward(req, resp);
            return;
        }

        String avatarUrl = null;
        try {
            Part avatarPart = req.getPart("avatar");
            if (avatarPart != null && avatarPart.getSize() > 0) {
                avatarUrl = UploadUtils.uploadAvatar(avatarPart, person.getId());
            }
        } catch (IllegalArgumentException e) {
            req.setAttribute("person", person);
            req.setAttribute("email", currentUser.getEmail());
            req.setAttribute("error", e.getMessage());
            RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/profile.jsp");
            dispatcher.forward(req, resp);
            return;
        } catch (IOException e) {
            req.setAttribute("person", person);
            req.setAttribute("email", currentUser.getEmail());
            req.setAttribute("error", "Lỗi upload ảnh: " + e.getMessage());
            RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/profile.jsp");
            dispatcher.forward(req, resp);
            return;
        }

        if (avatarUrl != null) {
            person.updateProfile(fullName, gender, phone, currentUser.getEmail(), avatarUrl);
        } else {
            person.updateProfile(fullName, gender, phone, currentUser.getEmail());
        }
        
        personDAO.update(person);

        currentUser.setUsername(fullName);
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.setAttribute("user", currentUser);
        }

        resp.sendRedirect(req.getContextPath() + "/profile?msg=updated");
    }

    private UserAccount resolveCurrentUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) {
            return null;
        }

        Object value = session.getAttribute("user");
        if (value instanceof UserAccount userAccount) {
            return userAccount;
        }
        return null;
    }

    private Person resolvePerson(UserAccount currentUser) {
        Long relatedID = currentUser.getRelatedID();
        if (relatedID != null) {
            Person person = personDAO.findById(relatedID, Person.class);
            if (person != null) {
                return person;
            }
        }
        return personDAO.findByEmail(currentUser.getEmail());
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private Gender parseGender(String genderParam) {
        String normalized = normalize(genderParam);
        if (normalized == null) {
            return null;
        }

        try {
            return Gender.valueOf(normalized.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}

