package vn.iotstar.coolenglish.web;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.coolenglish.audit.context.AuditActorContext;
import vn.iotstar.coolenglish.entity.UserAccount;
import vn.iotstar.coolenglish.enums.UserRole;

/**
 * AuthenticationFilter - Bảo vệ các trang admin yêu cầu đăng nhập
 * 
 * Kiểm tra xem user có trong session không trước khi cho phép truy cập
 * những trang quản lý (admin/*)
 * 
 * @author CoolEnglish Team
 */
@WebFilter(urlPatterns = { "/admin/*", "/teacher/*" })
public class AuthenticationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Lấy session hiện tại
        HttpSession session = httpRequest.getSession(false);

        // Kiểm tra xem user có trong session không
        UserAccount user = null;
        if (session != null) {
            user = (UserAccount) session.getAttribute("user");
        }

        // Nếu không có user, chuyển hướng đến trang đăng nhập
        if (user == null) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.jsp?msg=require_login");
            return;
        }

        String servletPath = httpRequest.getServletPath();
        UserRole role = user.getRole();
        boolean teacherArea = servletPath != null && servletPath.startsWith("/teacher/");
        if (!teacherArea) {
            if (role != UserRole.ADMIN && role != UserRole.STAFF) {
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.jsp?msg=forbidden");
                return;
            }
        } else {
            if (role != UserRole.TEACHER) {
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.jsp?msg=forbidden");
                return;
            }
        }

        AuditActorContext.setCurrentUser(user);
        try {
            // Nếu có user, cho phép tiếp tục
            chain.doFilter(request, response);
        } finally {
            AuditActorContext.clear();
        }
    }
}

