package vn.iotstar.coolenglish.web;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.coolenglish.entity.UserAccount;
import vn.iotstar.coolenglish.enums.UserRole;
import vn.iotstar.coolenglish.service.AcademicContentService;
import vn.iotstar.coolenglish.service.AcademicContentService.LearningViewModel;

@WebServlet(urlPatterns = { "/learning" })
public class LearningController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final AcademicContentService academicContentService;

    public LearningController() {
        this(new AcademicContentService());
    }

    LearningController(AcademicContentService academicContentService) {
        this.academicContentService = academicContentService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserAccount currentUser = resolveCurrentUser(req);
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login?msg=require_login");
            return;
        }

        Long studentId = currentUser != null ? currentUser.getUserID() : null;
        String studentEmail = currentUser != null ? currentUser.getEmail() : null;
        UserRole currentRole = currentUser != null ? currentUser.getRole() : null;
        String roadmapCode = trim(req.getParameter("roadmapCode"));
        if (roadmapCode == null || roadmapCode.isBlank()) {
            roadmapCode = "TOEIC_RL";
        }
        String currentTitle = trim(req.getParameter("title"));

        LearningViewModel learningView = academicContentService.buildLearningView(
                roadmapCode,
                studentId,
                studentEmail,
                currentTitle,
                currentRole);

        req.setAttribute("currentContent", learningView.getCurrentContent());
        req.setAttribute("renderedContent",
                learningView.getCurrentContent() != null ? learningView.getCurrentContent().displayContent() : "");
        req.setAttribute("roadmap", learningView.getRoadmap());
        req.setAttribute("roadmapCode", learningView.getRoadmapCode());
        req.setAttribute("rootModule", learningView.getRootModule());
        req.setAttribute("currentContentId",
                learningView.getCurrentContent() != null ? learningView.getCurrentContent().getId() : null);
        req.setAttribute("premiumLocked", learningView.isPremiumLocked());

        RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/learning.jsp");
        dispatcher.forward(req, resp);
    }

    private UserAccount resolveCurrentUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            Object currentUser = session.getAttribute("user");
            if (currentUser instanceof UserAccount userAccount) {
                return userAccount;
            }
        }
        return null;
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }
}

