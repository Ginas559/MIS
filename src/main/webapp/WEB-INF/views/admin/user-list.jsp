<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Quan ly nguoi dung</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/vendor/bootstrap/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/app.css">
</head>
<body class="app-body">
<div class="container py-4">
    <div class="app-section list-card">
        <div class="d-flex justify-content-between align-items-center mb-3">
            <div>
                <h1 class="h4 app-title mb-1">Danh sach tai khoan</h1>
                <p class="app-subtle mb-0">Chi ADMIN duoc phep truy cap trang nay.</p>
            </div>
            <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/admin/course">Quay lai quan ly</a>
        </div>

        <div class="table-responsive">
            <table class="table table-bordered align-middle">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Email (Account)</th>
                    <th>Ho ten (Person)</th>
                    <th>Username</th>
                    <th>Vai tro</th>
                    <th>Trang thai</th>
                    <th>Hanh dong</th>
                </tr>
                </thead>
                <tbody>
                <c:choose>
                    <c:when test="${not empty userList}">
                        <c:forEach var="ua" items="${userList}">
                            <c:set var="personName" value="${personNameByRelatedId[ua.relatedID]}" />
                            <tr>
                                <td>${ua.userID}</td>
                                <td>${ua.email}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${not empty personName}">${personName}</c:when>
                                        <c:otherwise>${ua.username}</c:otherwise>
                                    </c:choose>
                                </td>
                                <td>${ua.username}</td>
                                <td><span class="status-pill">${ua.role}</span></td>
                                <td>
                                    <span class="badge ${ua.active ? 'bg-success' : 'bg-secondary'}">
                                        ${ua.active ? 'Kich hoat' : 'Vo hieu hoa'}
                                    </span>
                                </td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/admin/users/detail?id=${ua.userID}"
                                       class="btn btn-outline-info btn-sm">Xem ho so</a>
                                    <form method="post"
                                          action="${pageContext.request.contextPath}/admin/users/toggle"
                                          class="d-inline">
                                        <input type="hidden" name="id" value="${ua.userID}" />
                                        <button type="submit"
                                                class="btn btn-outline-warning btn-sm"
                                                onclick="return confirm('${ua.active ? 'Vô hiệu hóa' : 'Kích hoạt'} tài khoản này?');">
                                            ${ua.active ? 'Tat' : 'Bat'}
                                        </button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <tr>
                            <td colspan="7" class="text-center app-subtle">Chua co nguoi dung nao.</td>
                        </tr>
                    </c:otherwise>
                </c:choose>
                </tbody>
            </table>
        </div>

        <c:if test="${totalPages > 1}">
            <nav aria-label="User pagination" class="mt-3">
                <ul class="pagination mb-0">
                    <li class="page-item ${currentPage <= 1 ? 'disabled' : ''}">
                        <a class="page-link"
                           href="${pageContext.request.contextPath}${paginationPath}?page=${currentPage - 1}">Truoc</a>
                    </li>
                    <c:forEach var="pageNumber" begin="1" end="${totalPages}">
                        <li class="page-item ${pageNumber == currentPage ? 'active' : ''}">
                            <a class="page-link"
                               href="${pageContext.request.contextPath}${paginationPath}?page=${pageNumber}">${pageNumber}</a>
                        </li>
                    </c:forEach>
                    <li class="page-item ${currentPage >= totalPages ? 'disabled' : ''}">
                        <a class="page-link"
                           href="${pageContext.request.contextPath}${paginationPath}?page=${currentPage + 1}">Sau</a>
                    </li>
                </ul>
            </nav>
            <p class="app-subtle mb-0 mt-2">Tong: ${totalItems} ban ghi (30 dong/trang).</p>
        </c:if>
    </div>
</div>
<script src="${pageContext.request.contextPath}/assets/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
</body>
</html>


