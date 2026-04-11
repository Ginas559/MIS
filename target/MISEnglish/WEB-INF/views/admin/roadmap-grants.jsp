<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="vn.iotstar.coolenglish.entity.Roadmap" %>
<%@ page import="vn.iotstar.coolenglish.entity.UserAccount" %>
<%
    List<UserAccount> learners = (List<UserAccount>) request.getAttribute("learners");
    List<Roadmap> roadmaps = (List<Roadmap>) request.getAttribute("roadmaps");
    List<Long> grantedRoadmapIds = (List<Long>) request.getAttribute("grantedRoadmapIds");
    Long selectedPersonId = (Long) request.getAttribute("selectedPersonId");
    if (grantedRoadmapIds == null) {
        grantedRoadmapIds = java.util.Collections.emptyList();
    }
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Cap quyen roadmap</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/vendor/bootstrap/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/app.css">
</head>
<body class="app-body">
<nav class="navbar app-navbar">
    <div class="container py-2">
        <a class="app-brand" href="${pageContext.request.contextPath}/index.jsp">CoolEnglish</a>
        <div class="d-flex gap-2">
            <a class="btn btn-outline-sky" href="${pageContext.request.contextPath}/admin/course">Quan ly khoa hoc</a>
            <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/admin/room">Quan ly phong hoc</a>
            <a class="btn btn-outline-dark" href="${pageContext.request.contextPath}/admin/class">Quan ly lop hoc</a>
            <form method="post" action="${pageContext.request.contextPath}/logout">
                <button type="submit" class="btn btn-outline-dark">Dang xuat</button>
            </form>
        </div>
    </div>
</nav>

<div class="container py-4">
    <div class="app-section p-4">
        <h1 class="h4 app-title mb-2">Cap quyen roadmap cho Student/Teacher</h1>
        <p class="app-subtle mb-3">ADMIN va STAFF duoc xem tat ca roadmap. O day ban cap quyen thu cong cho hoc vien va giao vien.</p>

        <form method="get" action="${pageContext.request.contextPath}/admin/roadmap-grants" class="row g-2 mb-3">
            <div class="col-md-8">
                <select class="form-select" name="personId" onchange="this.form.submit()" required>
                    <option value="">-- Chon tai khoan --</option>
                    <% if (learners != null) {
                           for (UserAccount learner : learners) {
                               Long learnerPersonId = learner.getRelatedID();
                               boolean selected = selectedPersonId != null && selectedPersonId.equals(learnerPersonId);
                    %>
                        <option value="<%= learnerPersonId %>" <%= selected ? "selected" : "" %>>
                            <%= learner.getRole() %> - <%= learner.getUsername() %> (<%= learner.getEmail() %>)
                        </option>
                    <%     }
                       } %>
                </select>
            </div>
        </form>

        <% if (selectedPersonId != null) { %>
        <form method="post" action="${pageContext.request.contextPath}/admin/roadmap-grants">
            <input type="hidden" name="personId" value="<%= selectedPersonId %>">
            <div class="mb-3">
                <% if (roadmaps != null && !roadmaps.isEmpty()) {
                       for (Roadmap roadmap : roadmaps) {
                           boolean checked = grantedRoadmapIds.contains(roadmap.getId());
                %>
                    <div class="form-check mb-2">
                        <input class="form-check-input" type="checkbox" name="roadmapIds" value="<%= roadmap.getId() %>" id="roadmap-<%= roadmap.getId() %>" <%= checked ? "checked" : "" %>>
                        <label class="form-check-label" for="roadmap-<%= roadmap.getId() %>">
                            <strong><%= roadmap.getTitle() %></strong> - <code><%= roadmap.getRoadmapCode() %></code>
                        </label>
                    </div>
                <%     }
                   } else { %>
                    <p class="app-subtle">Chua co roadmap nao trong he thong.</p>
                <% } %>
            </div>

            <button type="submit" class="btn btn-sky">Luu quyen truy cap</button>
            <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/index.jsp">Ve trang chu</a>
        </form>
        <% } %>
    </div>
</div>

<script src="${pageContext.request.contextPath}/assets/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
</body>
</html>

