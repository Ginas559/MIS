<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="vn.iotstar.coolenglish.web.AuditLogController.AuditLogRow" %>
<%
    List<AuditLogRow> auditRows = (List<AuditLogRow>) request.getAttribute("auditRows");
    Integer currentPage = (Integer) request.getAttribute("currentPage");
    Integer totalPages = (Integer) request.getAttribute("totalPages");
    Integer totalItems = (Integer) request.getAttribute("totalItems");
    String paginationPath = (String) request.getAttribute("paginationPath");
    String undoStatus = (String) request.getAttribute("undoStatus");
    boolean isAdmin = Boolean.TRUE.equals(request.getAttribute("isAdmin"));

    if (currentPage == null) currentPage = 1;
    if (totalPages == null) totalPages = 1;
    if (totalItems == null) totalItems = 0;
    if (paginationPath == null || paginationPath.isBlank()) paginationPath = "/admin/audit-logs";
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Lich su Audit Log</title>
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
            <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/admin/integration/exam-results">Dong bo ket qua</a>
            <form method="post" action="${pageContext.request.contextPath}/logout">
                <button type="submit" class="btn btn-outline-dark">Dang xuat</button>
            </form>
        </div>
    </div>
</nav>

<div class="container py-4">
    <div class="app-section list-card">
        <div class="d-flex justify-content-between align-items-center mb-3">
            <div>
                <h1 class="h4 app-title mb-1">Lich su Audit Log</h1>
                <p class="app-subtle mb-0">Theo doi ai thay doi du lieu gi, vao thoi diem nao.</p>
            </div>
            <% if (isAdmin) { %>
            <form method="post" action="${pageContext.request.contextPath}/admin/audit-logs/undo">
                <button type="submit" class="btn btn-outline-dark">Undo audit gan nhat</button>
            </form>
            <% } %>
        </div>

        <% if ("success".equals(undoStatus)) { %>
            <div class="alert alert-success">Undo audit gan nhat thanh cong.</div>
        <% } else if ("empty".equals(undoStatus)) { %>
            <div class="alert alert-warning">Khong co audit nao trong history runtime de undo.</div>
        <% } else if ("denied".equals(undoStatus)) { %>
            <div class="alert alert-danger">Ban khong co quyen undo audit.</div>
        <% } %>

        <div class="table-responsive">
            <table class="table table-bordered align-middle">
                <thead>
                <tr>
                    <th>#</th>
                    <th>Thao tac</th>
                    <th>Doi tuong</th>
                    <th>Ma ban ghi</th>
                    <th>Nguoi thuc hien</th>
                    <th>Thoi gian</th>
                    <th>Tom tat thay doi</th>
                </tr>
                </thead>
                <tbody>
                <%
                    if (auditRows != null && !auditRows.isEmpty()) {
                        for (AuditLogRow row : auditRows) {
                %>
                <tr>
                    <td><%= row.getAuditId() %></td>
                    <td><span class="status-pill"><%= row.getAction() %></span></td>
                    <td><%= row.getEntityName() %></td>
                    <td><%= row.getEntityId() == null ? "" : row.getEntityId() %></td>
                    <td><%= row.getActor() %></td>
                    <td><%= row.getChangedAt() %></td>
                    <td><%= row.getDetails() %></td>
                </tr>
                <%
                        }
                    } else {
                %>
                <tr>
                    <td colspan="7" class="text-center app-subtle">Chua co du lieu audit log.</td>
                </tr>
                <%
                    }
                %>
                </tbody>
            </table>
        </div>

        <% if (totalPages > 1) { %>
        <nav aria-label="Audit pagination" class="mt-3">
            <ul class="pagination mb-0">
                <li class="page-item <%= currentPage <= 1 ? "disabled" : "" %>">
                    <a class="page-link" href="${pageContext.request.contextPath}<%= paginationPath %>?page=<%= currentPage - 1 %>">Truoc</a>
                </li>
                <% for (int pageNumber = 1; pageNumber <= totalPages; pageNumber++) { %>
                <li class="page-item <%= pageNumber == currentPage ? "active" : "" %>">
                    <a class="page-link" href="${pageContext.request.contextPath}<%= paginationPath %>?page=<%= pageNumber %>"><%= pageNumber %></a>
                </li>
                <% } %>
                <li class="page-item <%= currentPage >= totalPages ? "disabled" : "" %>">
                    <a class="page-link" href="${pageContext.request.contextPath}<%= paginationPath %>?page=<%= currentPage + 1 %>">Sau</a>
                </li>
            </ul>
        </nav>
        <% } %>
        <p class="app-subtle mb-0 mt-2">Tong: <%= totalItems %> ban ghi.</p>
    </div>
</div>
<script src="${pageContext.request.contextPath}/assets/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
</body>
</html>
