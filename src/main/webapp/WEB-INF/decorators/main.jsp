<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ page import="org.sitemesh.content.Content" %>
<%@ page import="org.sitemesh.content.ContentProperty" %>
<%@ page import="vn.iotstar.coolenglish.entity.UserAccount" %>
<%@ page import="vn.iotstar.coolenglish.enums.UserRole" %>
<%
    Content sitemeshContent = (Content) request.getAttribute(Content.class.getName());
    String pageTitle = "";
    String pageHead = "";
    String pageBody = "";
    UserAccount currentUser = (UserAccount) session.getAttribute("user");

    if (sitemeshContent != null) {
        ContentProperty properties = sitemeshContent.getExtractedProperties();
        if (properties != null) {
            if (properties.hasChild("title")) {
                pageTitle = properties.getChild("title").getNonNullValue();
            }
            if (properties.hasChild("head")) {
                pageHead = properties.getChild("head").getNonNullValue();
            }
            if (properties.hasChild("body")) {
                pageBody = properties.getChild("body").getNonNullValue();
            }
        }
    }
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= pageTitle %></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/vendor/bootstrap/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/app.css">
    <%= pageHead %>
</head>
<body class="app-body">
<nav class="navbar app-navbar">
    <div class="container py-2">
        <a class="app-brand" href="${pageContext.request.contextPath}/index.jsp">CoolEnglish</a>
        <div class="d-flex gap-2">
            <c:choose>
                <c:when test="${not empty sessionScope.user}">
                    <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/course">Danh sach khoa hoc</a>
                    <% if (currentUser != null && currentUser.getRole() == UserRole.STUDENT) { %>
                    <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/student/schedule">Lich hoc</a>
                    <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/student/results">Ket qua hoc tap</a>
                    <% } %>
                    <% if (currentUser != null && currentUser.getRole() == UserRole.TEACHER) { %>
                    <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/teacher/results">Bang diem</a>
                    <% } %>
                    <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/profile">Ho so ca nhan</a>
                    <form method="post" action="${pageContext.request.contextPath}/logout">
                        <button type="submit" class="btn btn-outline-dark">Dang xuat</button>
                    </form>
                </c:when>
                <c:otherwise>
                    <a class="btn btn-outline-sky" href="${pageContext.request.contextPath}/login">Dang nhap</a>
                    <a class="btn btn-sky" href="${pageContext.request.contextPath}/signup">Dang ky</a>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</nav>

<%= pageBody %>

<footer class="container pb-4">
    <p class="app-subtle mb-0 text-center">CoolEnglish - Learning Management</p>
</footer>

<script src="${pageContext.request.contextPath}/assets/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
</body>
</html>
