<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="vn.iotstar.coolenglish.entity.Room" %>
<%
    Room room = (Room) request.getAttribute("room");
    boolean editing = room != null && room.getRoomID() != null && !room.getRoomID().isBlank();
    String actionUrl = editing ? request.getContextPath() + "/admin/room/update" : request.getContextPath() + "/admin/room/add";
    String selectedStatus = room == null || room.getStatus() == null || room.getStatus().isBlank() ? "AVAILABLE" : room.getStatus();
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title><%= editing ? "Sửa Room" : "Thêm Room" %></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/vendor/bootstrap/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/app.css">
</head>
<body class="app-body">
<div class="container py-4">
    <div class="app-section app-card">
        <h1 class="h4 app-title mb-3"><%= editing ? "Sửa Room" : "Thêm Room" %></h1>
        <form method="post" action="<%= actionUrl %>" class="vstack gap-3">
            <div>
                <label class="form-label">Mã Room</label>
                <input type="text" class="form-control" name="roomID"
                       value="<%= room == null || room.getRoomID() == null ? "" : room.getRoomID() %>" <%= editing ? "readonly" : "" %> required>
            </div>

            <div>
                <label class="form-label">Tên Room</label>
                <input type="text" class="form-control" name="roomName"
                       value="<%= room == null || room.getRoomName() == null ? "" : room.getRoomName() %>" required>
            </div>

            <div>
                <label class="form-label">Sức chứa</label>
                <input type="number" class="form-control" name="capacity"
                       value="<%= room == null || room.getCapacity() == null ? "" : room.getCapacity() %>">
            </div>

            <div>
                <label class="form-label">Vị trí</label>
                <input type="text" class="form-control" name="location"
                       value="<%= room == null || room.getLocation() == null ? "" : room.getLocation() %>">
            </div>

            <div>
                <label class="form-label">Trạng thái</label>
                <select name="status" class="form-select" required>
                    <option value="AVAILABLE" <%= "AVAILABLE".equals(selectedStatus) ? "selected" : "" %>>AVAILABLE</option>
                    <option value="OCCUPIED" <%= "OCCUPIED".equals(selectedStatus) ? "selected" : "" %>>OCCUPIED</option>
                    <option value="MAINTENANCE" <%= "MAINTENANCE".equals(selectedStatus) ? "selected" : "" %>>MAINTENANCE</option>
                </select>
            </div>

            <div class="d-flex gap-2">
                <button type="submit" class="btn btn-sky"><%= editing ? "Cập nhật" : "Thêm mới" %></button>
                <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/admin/room">Quay lại</a>
            </div>
        </form>
    </div>
</div>
<script src="${pageContext.request.contextPath}/assets/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
</body>
</html>
