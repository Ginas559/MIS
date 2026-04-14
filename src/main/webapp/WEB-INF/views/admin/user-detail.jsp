<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="vn.iotstar.coolenglish.entity.Person" %>
<%@ page import="vn.iotstar.coolenglish.entity.Student" %>
<%@ page import="vn.iotstar.coolenglish.entity.Teacher" %>
<%@ page import="vn.iotstar.coolenglish.entity.Staff" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Chi tiet tai khoan</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/vendor/bootstrap/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/app.css">
</head>
<body class="app-body">
<%
    Person rolePerson = (Person) request.getAttribute("person");
    String roleTitle = "";
    String roleField1Label = "";
    String roleField1Value = "";
    String roleField2Label = "";
    String roleField2Value = "";
    String roleField3Label = "";
    String roleField3Value = "";
    String roleField4Label = "";
    String roleField4Value = "";

    if (rolePerson instanceof Student) {
        Student student = (Student) rolePerson;
        roleTitle = "Thong tin hoc vien";
        roleField1Label = "Ma hoc vien";
        roleField1Value = student.getStudentID() != null ? student.getStudentID() : "Chua cap nhat";
        roleField2Label = "Trang thai";
        roleField2Value = student.getStatus() != null ? student.getStatus().name() : "Chua cap nhat";
        roleField3Label = "Ngay sinh";
        roleField3Value = student.getDateOfBirth() != null ? student.getDateOfBirth().toString() : "Chua cap nhat";
        roleField4Label = "Ngay dang ky";
        roleField4Value = student.getRegistrationDate() != null ? student.getRegistrationDate().toString() : "Chua cap nhat";
    } else if (rolePerson instanceof Teacher) {
        Teacher teacher = (Teacher) rolePerson;
        roleTitle = "Thong tin giao vien";
        roleField1Label = "Ma giao vien";
        roleField1Value = teacher.getTeacherID() != null ? teacher.getTeacherID() : "Chua cap nhat";
        roleField2Label = "Chuyen mon";
        roleField2Value = teacher.getSpecialty() != null ? teacher.getSpecialty() : "Chua cap nhat";
        roleField3Label = "Chung chi";
        roleField3Value = teacher.getCertificate() != null ? teacher.getCertificate() : "Chua cap nhat";
        roleField4Label = "Ngay vao lam (Ngay dang ky tai khoan)";
        roleField4Value = teacher.getHireDate() != null ? teacher.getHireDate().toString() : "Chua cap nhat";
    } else if (rolePerson instanceof Staff) {
        Staff staff = (Staff) rolePerson;
        roleTitle = "Thong tin nhan vien";
        roleField1Label = "Ma nhan vien";
        roleField1Value = staff.getStaffID() != null ? staff.getStaffID() : "Chua cap nhat";
        roleField2Label = "";
        roleField2Value = "";
    }
%>
<div class="container py-4">
    <div class="app-section list-card">
        <div class="d-flex justify-content-between align-items-center mb-3">
            <div>
                <h1 class="h4 app-title mb-1">Chi tiet tai khoan</h1>
                <p class="app-subtle mb-0">Thong tin dang nhap va ho so lien ket.</p>
            </div>
            <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/admin/users">Quay lai danh sach</a>
        </div>

        <c:if test="${not empty param.msg and param.msg == 'toggled'}">
            <div class="alert alert-success">Da cap nhat trang thai tai khoan.</div>
        </c:if>

        <div class="row g-3">
            <div class="col-md-6">
                <div class="card h-100">
                    <div class="card-body">
                        <h2 class="h5 mb-3">Thong tin tai khoan</h2>
                        <p class="mb-2"><strong>ID:</strong> ${user.userID}</p>
                        <p class="mb-2"><strong>Email:</strong> ${user.email}</p>
                        <p class="mb-2"><strong>Username:</strong> ${user.username}</p>
                        <p class="mb-2"><strong>Vai tro:</strong> <span class="status-pill">${user.role}</span></p>
                        <p class="mb-0">
                            <strong>Trang thai:</strong>
                            <span class="badge ${user.active ? 'bg-success' : 'bg-secondary'}">
                                ${user.active ? 'Kich hoat' : 'Vo hieu hoa'}
                            </span>
                        </p>
                    </div>
                </div>
            </div>

            <div class="col-md-6">
                <div class="card h-100">
                    <div class="card-body">
                        <h2 class="h5 mb-3">Thong tin ho so</h2>
                        <p class="mb-2"><strong>Ho ten:</strong> ${empty personName ? user.username : personName}</p>
                        <p class="mb-2"><strong>Person ID:</strong> ${empty person ? 'Chua lien ket' : person.id}</p>
                        <p class="mb-2"><strong>Email lien ket:</strong> ${empty person ? '-' : person.email}</p>
                        <p class="mb-2"><strong>Dien thoai:</strong> ${empty person ? '-' : person.phone}</p>
                        <p class="mb-0"><strong>Gioi tinh:</strong> ${empty person ? '-' : person.gender}</p>
                    </div>
                </div>
            </div>
        </div>

        <% if (!roleTitle.isEmpty()) { %>
            <div class="card mt-3">
                <div class="card-body">
                    <h2 class="h5 mb-3"><%= roleTitle %></h2>
                    <p class="mb-2"><strong><%= roleField1Label %>:</strong> <%= roleField1Value %></p>
                    <% if (!roleField2Label.isEmpty()) { %>
                        <p class="mb-2"><strong><%= roleField2Label %>:</strong> <%= roleField2Value %></p>
                    <% } %>
                    <% if (!roleField3Label.isEmpty()) { %>
                        <p class="mb-2"><strong><%= roleField3Label %>:</strong> <%= roleField3Value %></p>
                    <% } %>
                    <% if (!roleField4Label.isEmpty()) { %>
                        <p class="mb-0"><strong><%= roleField4Label %>:</strong> <%= roleField4Value %></p>
                    <% } %>
                </div>
            </div>
        <% } %>

        <div class="mt-4 d-flex gap-2">
            <form method="post" action="${pageContext.request.contextPath}/admin/users/toggle" class="m-0">
                <input type="hidden" name="id" value="${user.userID}" />
                <button type="submit" class="btn ${user.active ? 'btn-outline-warning' : 'btn-outline-success'}"
                        onclick="return confirm('${user.active ? 'Vô hiệu hóa' : 'Kích hoạt'} tài khoản này?');">
                    ${user.active ? 'Vo hieu hoa tai khoan' : 'Kich hoat tai khoan'}
                </button>
            </form>
            <a class="btn btn-outline-info" href="${pageContext.request.contextPath}/admin/users">Xem danh sach</a>
        </div>
    </div>
</div>
<script src="${pageContext.request.contextPath}/assets/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
</body>
</html>

