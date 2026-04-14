<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page isErrorPage="true" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Co loi xay ra!</title>
</head>
<body>
    <%
        Integer statusCode = (Integer) request.getAttribute("jakarta.servlet.error.status_code");
        String requestUri = (String) request.getAttribute("jakarta.servlet.error.request_uri");
        String errorMessage = (String) request.getAttribute("jakarta.servlet.error.message");
        Throwable root = exception != null ? exception : (Throwable) request.getAttribute("jakarta.servlet.error.exception");
    %>
    <h2>Rat tiec, he thong gap su co!</h2>
    <p>
        Thong bao loi:
        <%= root != null && root.getMessage() != null ? root.getMessage() : (errorMessage == null ? "Khong xac dinh" : errorMessage) %>
    </p>
    <p>Ma loi: <%= statusCode == null ? "N/A" : statusCode %></p>
    <p>Trang: <%= requestUri == null ? "N/A" : requestUri %></p>

    <div style="display:none">
        <%
            if (root != null) {
                root.printStackTrace(new java.io.PrintWriter(out));
            }
        %>
    </div>

    <a href="${pageContext.request.contextPath}/index.jsp">Quay lai trang chu</a>
</body>
</html>


