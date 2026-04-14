package vn.iotstar.coolenglish.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.coolenglish.entity.UserAccount;
import vn.iotstar.coolenglish.enums.UserRole;

class AuthenticationFilterTest {

    private final AuthenticationFilter filter = new AuthenticationFilter();

    @Test
    void shouldRedirectToLoginWhenNoUserInSession() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/admin/course");
        request.setContextPath("/app");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, noopChain());

        assertEquals("/app/login.jsp?msg=require_login", response.getRedirectedUrl());
    }

    @Test
    void shouldRedirectToForbiddenWhenStudentAccessesAdminArea() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/admin/course");
        request.setContextPath("/app");
        HttpSession session = request.getSession(true);
        session.setAttribute("user",
                new UserAccount("student@test.com", "student", "student123", UserRole.STUDENT));
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, noopChain());

        assertEquals("/app/login.jsp?msg=forbidden", response.getRedirectedUrl());
    }

    @Test
    void shouldAllowAdminToPassFilter() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/admin/course");
        request.setContextPath("/app");
        HttpSession session = request.getSession(true);
        session.setAttribute("user",
                new UserAccount("admin@test.com", "admin", "admin123", UserRole.ADMIN));
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicBoolean chainCalled = new AtomicBoolean(false);

        filter.doFilter(request, response, (ServletRequest req, ServletResponse res) -> chainCalled.set(true));

        assertTrue(chainCalled.get());
        assertNull(response.getRedirectedUrl());
    }

    @Test
    void shouldAllowStaffToAccessAdminUsersArea() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/admin/users");
        request.setContextPath("/app");
        HttpSession session = request.getSession(true);
        session.setAttribute("user", new UserAccount("staff@test.com", "staff", "staff123", UserRole.STAFF));
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicBoolean chainCalled = new AtomicBoolean(false);

        filter.doFilter(request, response, (ServletRequest req, ServletResponse res) -> chainCalled.set(true));

        assertTrue(chainCalled.get());
        assertNull(response.getRedirectedUrl());
    }

    @Test
    void shouldDenyTeacherInAdminUsersArea() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/admin/users");
        request.setContextPath("/app");
        HttpSession session = request.getSession(true);
        session.setAttribute("user", new UserAccount("teacher@test.com", "teacher", "teacher123", UserRole.TEACHER));
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, noopChain());

        assertEquals("/app/login.jsp?msg=forbidden", response.getRedirectedUrl());
    }

    private FilterChain noopChain() {
        return (request, response) -> {
            // no-op
        };
    }
}
