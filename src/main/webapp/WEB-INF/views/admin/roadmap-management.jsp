<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="vn.iotstar.coolenglish.entity.Roadmap" %>
<%@ page import="vn.iotstar.coolenglish.service.RoadmapManagementService.ContentNodeView" %>
<%
    List<Roadmap> roadmaps = (List<Roadmap>) request.getAttribute("roadmaps");
    List<ContentNodeView> contentNodes = (List<ContentNodeView>) request.getAttribute("contentNodes");
    List<ContentNodeView> moduleNodes = (List<ContentNodeView>) request.getAttribute("moduleNodes");
    Long selectedRoadmapId = (Long) request.getAttribute("selectedRoadmapId");
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Quan ly roadmap</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/vendor/bootstrap/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/app.css">
</head>
<body class="app-body">
<nav class="navbar app-navbar">
    <div class="container py-2">
        <a class="app-brand" href="${pageContext.request.contextPath}/index.jsp">CoolEnglish</a>
        <div class="d-flex gap-2">
            <a class="btn btn-outline-sky" href="${pageContext.request.contextPath}/admin/dashboard">Bảng điều khiển</a>
            <a class="btn btn-outline-sky" href="${pageContext.request.contextPath}/admin/course">Quan ly khoa hoc</a>
            <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/admin/roadmap-grants">Cap quyen roadmap</a>
            <a class="btn btn-outline-dark" href="${pageContext.request.contextPath}/roadmaps">Trang roadmap</a>
            <form method="post" action="${pageContext.request.contextPath}/logout">
                <button type="submit" class="btn btn-outline-dark">Dang xuat</button>
            </form>
        </div>
    </div>
</nav>
<div class="container py-4">
    <div class="app-section p-4 mb-4">
        <h1 class="h4 app-title mb-3">CRUD Roadmap (Composite)</h1>
        <% if (request.getAttribute("error") != null) { %>
            <p class="text-danger"><%= request.getAttribute("error") %></p>
        <% } %>
        <% if (request.getAttribute("success") != null) { %>
            <p class="text-success"><%= request.getAttribute("success") %></p>
        <% } %>
        <form method="post" action="${pageContext.request.contextPath}/admin/roadmap-management" class="row g-2">
            <input type="hidden" name="action" value="createRoadmap">
            <div class="col-md-2"><input class="form-control" name="roadmapCode" placeholder="Code (IELTS)" required></div>
            <div class="col-md-3"><input class="form-control" name="title" placeholder="Ten roadmap" required></div>
            <div class="col-md-4"><input class="form-control" name="description" placeholder="Mo ta roadmap"></div>
            <div class="col-md-3"><button class="btn btn-sky" type="submit">Tao roadmap moi</button></div>
        </form>
    </div>
    <div class="app-section p-4 mb-4">
        <h2 class="h5 app-title mb-3">Chon roadmap de quan ly</h2>
        <form method="get" action="${pageContext.request.contextPath}/admin/roadmap-management" class="row g-2">
            <div class="col-md-6">
                <select class="form-select" name="roadmapId" onchange="this.form.submit()">
                    <option value="">-- Chon roadmap --</option>
                    <% if (roadmaps != null) {
                           for (Roadmap roadmap : roadmaps) {
                               boolean selected = selectedRoadmapId != null && selectedRoadmapId.equals(roadmap.getId());
                    %>
                    <option value="<%= roadmap.getId() %>" <%= selected ? "selected" : "" %>>
                        <%= roadmap.getRoadmapCode() %> - <%= roadmap.getTitle() %>
                    </option>
                    <%     }
                       } %>
                </select>
            </div>
        </form>
        <% if (selectedRoadmapId != null) { %>
        <div class="row g-3 mt-2">
            <div class="col-md-6">
                <h3 class="h6 app-title">Them Module (Chang)</h3>
                <form method="post" action="${pageContext.request.contextPath}/admin/roadmap-management" class="row g-2">
                    <input type="hidden" name="action" value="addModule">
                    <input type="hidden" name="roadmapId" value="<%= selectedRoadmapId %>">
                    <div class="col-12">
                        <select class="form-select" name="parentModuleId" required>
                            <% if (moduleNodes != null) {
                                   for (ContentNodeView node : moduleNodes) {
                            %>
                            <option value="<%= node.getId() %>"><%= node.getId() %> - <%= node.getTitle() %></option>
                            <%     }
                               } %>
                        </select>
                    </div>
                    <div class="col-6"><input class="form-control" name="title" placeholder="Ten module" required></div>
                    <div class="col-6"><input class="form-control" name="description" placeholder="Mo ta"></div>
                    <div class="col-12"><button class="btn btn-outline-sky" type="submit">Them module</button></div>
                </form>
            </div>
            <div class="col-md-6">
                <h3 class="h6 app-title">Them Lesson (La)</h3>
                <form method="post" action="${pageContext.request.contextPath}/admin/roadmap-management" class="row g-2">
                    <input type="hidden" name="action" value="addLesson">
                    <input type="hidden" name="roadmapId" value="<%= selectedRoadmapId %>">
                    <div class="col-12">
                        <select class="form-select" name="parentModuleId" required>
                            <% if (moduleNodes != null) {
                                   for (ContentNodeView node : moduleNodes) {
                            %>
                            <option value="<%= node.getId() %>"><%= node.getId() %> - <%= node.getTitle() %></option>
                            <%     }
                               } %>
                        </select>
                    </div>
                    <div class="col-6"><input class="form-control" name="title" placeholder="Ten lesson" required></div>
                    <div class="col-6"><input class="form-control" name="lessonType" placeholder="VIDEO/READING"></div>
                    <div class="col-12"><input class="form-control" name="description" placeholder="Mo ta lesson"></div>
                    <div class="col-12"><input class="form-control" name="resourceUrl" placeholder="https://..."></div>
                    <div class="col-12"><button class="btn btn-outline-sky" type="submit">Them lesson</button></div>
                </form>
            </div>
        </div>
        <div class="row g-3 mt-2">
            <div class="col-md-6">
                <h3 class="h6 app-title">Cap nhat noi dung</h3>
                <form method="post" action="${pageContext.request.contextPath}/admin/roadmap-management" class="row g-2">
                    <input type="hidden" name="action" value="updateContent">
                    <input type="hidden" name="roadmapId" value="<%= selectedRoadmapId %>">
                    <div class="col-12"><input class="form-control" name="contentId" placeholder="Content ID" required></div>
                    <div class="col-6"><input class="form-control" name="title" placeholder="Ten moi" required></div>
                    <div class="col-6"><input class="form-control" name="description" placeholder="Mo ta moi"></div>
                    <div class="col-6"><input class="form-control" name="lessonType" placeholder="Neu la Lesson"></div>
                    <div class="col-6"><input class="form-control" name="resourceUrl" placeholder="Neu la Lesson"></div>
                    <div class="col-12"><button class="btn btn-outline-dark" type="submit">Cap nhat</button></div>
                </form>
            </div>
            <div class="col-md-6">
                <h3 class="h6 app-title">Xoa noi dung / roadmap</h3>
                <form method="post" action="${pageContext.request.contextPath}/admin/roadmap-management" class="row g-2 mb-3">
                    <input type="hidden" name="action" value="deleteContent">
                    <input type="hidden" name="roadmapId" value="<%= selectedRoadmapId %>">
                    <div class="col-8"><input class="form-control" name="contentId" placeholder="Content ID can xoa" required></div>
                    <div class="col-4"><button class="btn btn-outline-dark w-100" type="submit">Xoa content</button></div>
                </form>
                <form method="post" action="${pageContext.request.contextPath}/admin/roadmap-management" class="row g-2">
                    <input type="hidden" name="action" value="deleteRoadmap">
                    <div class="col-8"><input class="form-control" name="roadmapId" value="<%= selectedRoadmapId %>" readonly></div>
                    <div class="col-4"><button class="btn btn-danger w-100" type="submit">Xoa roadmap</button></div>
                </form>
            </div>
        </div>
        <% } %>
    </div>
    <div class="app-section p-4">
        <h2 class="h5 app-title mb-3">Cau truc cay hoc lieu</h2>
        <div class="table-responsive">
            <table class="table table-bordered align-middle">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Type</th>
                    <th>Title</th>
                    <th>ParentID</th>
                    <th>Depth</th>
                </tr>
                </thead>
                <tbody>
                <% if (contentNodes != null && !contentNodes.isEmpty()) {
                       for (ContentNodeView node : contentNodes) {
                %>
                <tr>
                    <td><%= node.getId() %></td>
                    <td><%= node.getType() %></td>
                    <td><%= node.getTitle() %></td>
                    <td><%= node.getParentId() %></td>
                    <td><%= node.getDepth() %></td>
                </tr>
                <%     }
                   } else { %>
                <tr>
                    <td colspan="5" class="text-center app-subtle">Chua co du lieu trong roadmap duoc chon.</td>
                </tr>
                <% } %>
                </tbody>
            </table>
        </div>
    </div>
</div>
<script src="${pageContext.request.contextPath}/assets/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
</body>
</html>
