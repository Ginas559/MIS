<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="vn.iotstar.coolenglish.entity.ExamResult" %>
<%
    List<ExamResult> results = (List<ExamResult>) request.getAttribute("results");
    String syncStatus = (String) request.getAttribute("syncStatus");
    String syncMessage = (String) request.getAttribute("syncMessage");
    String selectedPartner = (String) request.getAttribute("selectedPartner");
    Integer currentPage = (Integer) request.getAttribute("currentPage");
    Integer totalPages = (Integer) request.getAttribute("totalPages");
    Integer totalItems = (Integer) request.getAttribute("totalItems");
    String paginationPath = (String) request.getAttribute("paginationPath");

    if (selectedPartner == null || selectedPartner.isBlank()) {
        selectedPartner = "IDP";
    }
    if (currentPage == null) currentPage = 1;
    if (totalPages == null) totalPages = 1;
    if (totalItems == null) totalItems = 0;
    if (paginationPath == null || paginationPath.isBlank()) paginationPath = "/admin/integration/exam-results";
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Dong bo ket qua doi tac</title>
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
            <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/admin/room">Quan ly phong hoc</a>
            <a class="btn btn-outline-dark" href="${pageContext.request.contextPath}/admin/class">Quan ly lop hoc</a>
            <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/admin/audit-logs">Audit logs</a>
        </div>
    </div>
</nav>

<div class="container py-4">
    <div class="app-section list-card mb-4">
        <div class="d-flex justify-content-between align-items-center mb-3">
            <h1 class="h4 app-title mb-0">Dong bo ket qua thi doi tac</h1>
            <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/admin/course">Quay lai quan ly</a>
        </div>

        <% if ("success".equals(syncStatus)) { %>
        <div class="alert alert-success" role="alert"><%= syncMessage %></div>
        <% } else if ("error".equals(syncStatus)) { %>
        <div class="alert alert-danger" role="alert"><%= syncMessage %></div>
        <% } %>

        <form method="post" action="${pageContext.request.contextPath}/admin/integration/exam-results/sync" class="row g-2 align-items-end">
            <div class="col-md-4">
                <label class="form-label">Partner</label>
                <select class="form-select" name="partnerCode" required>
                    <option value="IDP" <%= "IDP".equalsIgnoreCase(selectedPartner) ? "selected" : "" %>>IDP</option>
                </select>
            </div>
            <div class="col-md-4">
                <button type="submit" class="btn btn-sky">Dong bo ket qua</button>
            </div>
        </form>
    </div>

    <div class="app-section list-card">
        <h2 class="h5 app-title mb-3">Lich su ket qua da dong bo</h2>
        <div class="table-responsive">
            <table class="table table-bordered align-middle">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Partner</th>
                    <th>Email hoc vien</th>
                    <th>Ma bai thi</th>
                    <th>Diem</th>
                    <th>Ngay thi</th>
                    <th>Thoi diem dong bo</th>
                </tr>
                </thead>
                <tbody>
                <%
                    if (results != null && !results.isEmpty()) {
                        for (ExamResult result : results) {
                %>
                <tr>
                    <td><%= result.getId() %></td>
                    <td><%= result.getPartnerCode() %></td>
                    <td><%= result.getStudentEmail() %></td>
                    <td><%= result.getExamCode() %></td>
                    <td><%= result.getScore() %></td>
                    <td><%= result.getTakenAt() %></td>
                    <td><%= result.getSyncedAt() %></td>
                </tr>
                <%
                        }
                    } else {
                %>
                <tr>
                    <td colspan="7" class="text-center app-subtle">Chua co ket qua nao duoc dong bo.</td>
                </tr>
                <%
                    }
                %>
                </tbody>
            </table>
        </div>

        <% if (totalPages > 1) { %>
        <nav aria-label="Integration pagination" class="mt-3">
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
        <p class="app-subtle mb-0 mt-2">Tong: <%= totalItems %> ban ghi (30 dong/trang).</p>
        <% } %>
    </div>
</div>
<script src="${pageContext.request.contextPath}/assets/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
</body>
</html>
