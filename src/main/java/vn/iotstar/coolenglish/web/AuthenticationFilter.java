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
 * AuthenticationFilter - Bảo vệ các trang admin/teacher yêu cầu đăng nhập
 * * Kiểm tra xem user có trong session không trước khi cho phép truy cập
 * những trang quản lý.
 */
@WebFilter(urlPatterns = { "/admin/*", "/teacher/*" })
public class AuthenticationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 1. Lấy session hiện tại
        HttpSession session = httpRequest.getSession(false);

        // 2. Kiểm tra xem user có trong session không
        UserAccount user = null;
        if (session != null) {
            user = (UserAccount) session.getAttribute("user");
        }

        // Nếu không có user, chuyển hướng đến trang đăng nhập
        if (user == null) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.jsp?msg=require_login");
            return;
        }

        // 3. Kiểm tra phân quyền (Authorization)
        String servletPath = httpRequest.getServletPath();
        UserRole role = user.getRole();
        
        boolean isTeacherArea = servletPath != null && servletPath.startsWith("/teacher/");
        boolean isScheduleAdminArea = servletPath != null && servletPath.startsWith("/admin/schedule");

        if (isTeacherArea) {
            // Khu vực /teacher/*: Chỉ dành cho TEACHER
            if (role != UserRole.TEACHER) {
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.jsp?msg=forbidden");
                return;
            }
        } else {
            // Khu vực /admin/*
            // Ngoại lệ: TEACHER được vào admin/schedule để điểm danh
            if (role == UserRole.TEACHER && isScheduleAdminArea) {
                // Cho phép đi tiếp
            } else if (role != UserRole.ADMIN && role != UserRole.STAFF) {
                // Các trường hợp khác trong /admin/ không phải ADMIN/STAFF thì chặn
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.jsp?msg=forbidden");
                return;
            }
        }

        // 4. Thiết lập context audit và thực thi request
        AuditActorContext.setCurrentUser(user);
        try {
            chain.doFilter(request, response);
        } finally {
            AuditActorContext.clear();
        }
    }
}