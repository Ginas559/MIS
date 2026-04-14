<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="vn.iotstar.coolenglish.entity.EnglishClass" %>
<%@ page import="vn.iotstar.coolenglish.entity.Room" %>
<%@ page import="vn.iotstar.coolenglish.entity.Teacher" %>
<%@ page import="vn.iotstar.coolenglish.entity.Schedule" %>
<%@ page import="vn.iotstar.coolenglish.entity.Session" %>
<%@ page import="vn.iotstar.coolenglish.entity.UserAccount" %>
<%@ page import="vn.iotstar.coolenglish.enums.UserRole" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%
    UserAccount currentUser = (UserAccount) session.getAttribute("user");
    boolean canManage = currentUser != null
            && (currentUser.getRole() == UserRole.ADMIN || currentUser.getRole() == UserRole.STAFF);
    
    Schedule schedule = (Schedule) request.getAttribute("schedule");
    boolean isEditMode = schedule != null;
    List<EnglishClass> assignableClasses = (List<EnglishClass>) request.getAttribute("assignableClasses");
    String selectedClassID = null;
    if (isEditMode && schedule.getEnglishClass() != null) {
        selectedClassID = schedule.getEnglishClass().getClassID();
    }
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thiet lap lich hoc</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/vendor/bootstrap/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/app.css">
    <style>
        .session-row {
            background-color: #f8f9fa;
            border-left: 4px solid #0d6efd;
            padding: 20px;
            margin-bottom: 15px;
            border-radius: 5px;
            position: relative;
        }

        .session-row h6 {
            color: #0d6efd;
            margin-bottom: 15px;
            font-weight: 600;
        }

        .btn-remove-session {
            position: absolute;
            top: 10px;
            right: 10px;
            padding: 5px 10px;
            font-size: 12px;
        }

        .info-box-custom {
            background-color: #d1ecf1;
            border-left: 4px solid #0c5460;
            padding: 15px;
            margin-bottom: 20px;
            border-radius: 5px;
            color: #0c5460;
            font-size: 14px;
        }

        .form-label {
            font-weight: 500;
            margin-bottom: 8px;
            color: #333;
        }

        .required {
            color: #dc3545;
        }
    </style>
</head>
<body class="app-body">
<nav class="navbar app-navbar">
    <div class="container py-2">
        <a class="app-brand" href="${pageContext.request.contextPath}/index.jsp">CoolEnglish</a>
        <div class="d-flex gap-2">
            <% if (canManage) { %>
            <a class="btn btn-outline-sky" href="${pageContext.request.contextPath}/admin/dashboard">Bảng điều khiển</a>
            <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/admin/course">Quan ly khoa hoc</a>
            <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/admin/room">Quan ly phong hoc</a>
            <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/admin/class">Quan ly lop hoc</a>
            <a class="btn btn-outline-sky" href="${pageContext.request.contextPath}/admin/schedule">Quan ly lich hoc</a>
            <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/admin/integration/exam-results">Dong bo ket qua</a>
            <% } %>
            <form method="post" action="${pageContext.request.contextPath}/logout">
                <button type="submit" class="btn btn-outline-dark">Dang xuat</button>
            </form>
        </div>
    </div>
</nav>

<div class="container py-4">
    <div class="app-section">
        <!-- Header -->
        <div class="d-flex justify-content-between align-items-center mb-4">
            <div>
                <h1 class="h4 app-title mb-1"><%= isEditMode ? "Sua lich hoc" : "Tao lich hoc moi" %></h1>
                <p class="app-subtle mb-0"><%= isEditMode ? "Cap nhat thong tin lich hoc" : "Nhap thong tin lich cac buoi hoc de kiem tra trung lich phong/giao vien" %></p>
            </div>
            <div class="d-flex gap-2">
                <a class="btn btn-outline-secondary btn-sm" href="${pageContext.request.contextPath}/admin/dashboard">Bảng điều khiển</a>
                <a class="btn btn-outline-secondary btn-sm" href="${pageContext.request.contextPath}/admin/schedule">Quay ve</a>
            </div>
        </div>

        <!-- Messages -->
        <% String error = (String) request.getAttribute("error"); %>
        <% String success = (String) request.getAttribute("success"); %>

        <% if (error != null && !error.isEmpty()) { %>
        <div class="alert alert-danger alert-dismissible fade show">
            <strong>⚠️ Loi:</strong> <%= error %>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
        <% } %>

        <% if (success != null && !success.isEmpty()) { %>
        <div class="alert alert-success alert-dismissible fade show">
            <strong>✓ Thanh cong:</strong> <%= success %>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
        <% } %>

        <form method="POST" action="${pageContext.request.contextPath}/<%= isEditMode ? "admin/schedule/edit" : "admin/schedule/add" %>" id="scheduleForm" class="bg-white p-4 rounded">
            <!-- Basic Schedule Info -->
            <h5 class="mb-4">Thong tin co ban</h5>

            <div class="info-box-custom mb-4">
                <strong>✓ Luu y:</strong> He thong se tu dong kiem tra xung dot phong hoc va giao vien khi luu.<br>
                Neu phat hien xung dot (cung phong/giao vien tren cung ngay), yeu cau se bi tu choi.
            </div>

            <div class="mb-3">
                <label for="scheduleID" class="form-label">Ma lich hoc <span class="required">*</span></label>
                <input type="text" class="form-control" id="scheduleID" name="scheduleID" placeholder="vi du: SCH-2026-04-09" required
                       value="<%= isEditMode ? schedule.getScheduleID() : "" %>" <%= isEditMode ? "disabled" : "" %>>
                <% if (isEditMode) { %>
                <input type="hidden" name="scheduleID" value="<%= schedule.getScheduleID() %>">
                <% } %>
            </div>

            <div class="mb-3">
                <label for="createDate" class="form-label">Ngay tao lich</label>
                <input type="date" class="form-control" id="createDate" name="createDate"
                       value="<% if (isEditMode && schedule.getCreateDate() != null) { %>
                              <%= new SimpleDateFormat("yyyy-MM-dd").format(schedule.getCreateDate()) %>
                              <% } %>">
            </div>

            <div class="mb-4">
                <label for="description" class="form-label">Mo ta lich hoc</label>
                <textarea class="form-control" id="description" name="description" placeholder="vi du: Lich hoc tieng Anh lop A101"><%= isEditMode && schedule.getDescription() != null ? schedule.getDescription() : "" %></textarea>
            </div>

            <div class="mb-4">
                <label for="classID" class="form-label">Lop hoc <span class="required">*</span></label>
                <select class="form-select" id="classID" name="classID" required>
                    <option value="">-- Chon lop (ma lop) --</option>
                    <% if (assignableClasses != null) { %>
                    <% for (EnglishClass ec : assignableClasses) { %>
                    <option value="<%= ec.getClassID() %>"
                        <%= selectedClassID != null && selectedClassID.equals(ec.getClassID()) ? "selected" : "" %>>
                        <%= ec.getClassID() %> — <%= ec.getClassName() != null ? ec.getClassName() : "" %>
                    </option>
                    <% } %>
                    <% } %>
                </select>
                <div class="form-text">Moi lich hoc gan voi dung mot lop (quan he 1-1). Lop da co lich khac se khong hien trong danh sach khi tao moi.</div>
            </div>

            <!-- Sessions -->
            <h5 class="mb-3">Cac buoi hoc</h5>

            <div id="sessionsContainer"></div>

            <button type="button" class="btn btn-outline-success btn-sm mb-4" onclick="addSession()">+ Them buoi hoc</button>

            <!-- Form Actions -->
            <div class="d-flex gap-2 mt-4">
                <button type="submit" class="btn btn-sky flex-grow-1"><%= isEditMode ? "💾 Cap nhat lich hoc" : "💾 Luu lich hoc" %></button>
                <button type="reset" class="btn btn-outline-secondary flex-grow-1" onclick="resetForm(); return false;">🔄 Lam moi</button>
            </div>
        </form>
    </div>
</div>

<!-- Hidden Template for Session Row (OUTSIDE FORM) -->
<div id="sessionTemplate" style="display: none;">
    <div class="session-row" data-index="-1">
        <div class="d-flex justify-content-between align-items-center mb-3">
            <h6 class="mb-0">Buoi hoc #<span class="session-index">1</span></h6>
            <button type="button" class="btn btn-danger btn-sm btn-remove-session" onclick="removeSession(this); return false;">Xoa</button>
        </div>
        <div class="row">
            <div class="col-md-6 mb-3">
                <label class="form-label">Ma buoi hoc <span class="required">*</span></label>
                <input type="text" class="form-control" name="sessionID[]" placeholder="vi du: SESS-001">
            </div>
            <div class="col-md-6 mb-3">
                <label class="form-label">Ten buoi hoc <span class="required">*</span></label>
                <input type="text" class="form-control" name="sessionName[]" placeholder="vi du: Buoi 1 - Speaking">
            </div>
            <div class="col-md-6 mb-3">
                <label class="form-label">Ngay hoc <span class="required">*</span></label>
                <input type="date" class="form-control" name="sessionDate[]">
            </div>
            <div class="col-md-6 mb-3">
                <label class="form-label">Gio bat dau <span class="required">*</span></label>
                <input type="time" class="form-control" name="startTime[]">
            </div>
            <div class="col-md-6 mb-3">
                <label class="form-label">Gio ket thuc <span class="required">*</span></label>
                <input type="time" class="form-control" name="endTime[]">
            </div>
            <div class="col-md-6 mb-3">
                <label class="form-label">Phong hoc <span class="required">*</span></label>
                <select class="form-select" name="roomID[]">
                    <option value="">-- Chon phong --</option>
                    <% List<Room> rooms = (List<Room>) request.getAttribute("rooms"); %>
                    <% if (rooms != null) { %>
                    <% for (Room room : rooms) { %>
                    <option value="<%= room.getRoomID() %>"><%= room.getRoomName() %> (suc chua: <%= room.getCapacity() %>)</option>
                    <% } %>
                    <% } %>
                </select>
            </div>
            <div class="col-md-6 mb-3">
                <label class="form-label">Giao vien <span class="required">*</span></label>
                <select class="form-select" name="teacherID[]">
                    <option value="">-- Chon giao vien --</option>
                    <% List<Teacher> teachers = (List<Teacher>) request.getAttribute("teachers"); %>
                    <% if (teachers != null) { %>
                    <% for (Teacher teacher : teachers) { %>
                    <option value="<%= teacher.getId() %>"><%= teacher.getFullName() %></option>
                    <% } %>
                    <% } %>
                </select>
            </div>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/assets/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
<script>
    let sessionCount = 0;
    const form = document.getElementById('scheduleForm');

    function addSession(sessionData = null) {
        const template = document.getElementById('sessionTemplate');
        const container = document.getElementById('sessionsContainer');
        const clone = template.cloneNode(true);

        clone.removeAttribute('id');
        clone.style.display = 'block';
        clone.setAttribute('data-index', sessionCount);

        const sessionIndex = clone.querySelector('.session-index');
        sessionIndex.textContent = sessionCount + 1;

        // Add required attribute to cloned inputs
        const inputs = clone.querySelectorAll('input[name], select[name]');
        inputs.forEach(input => {
            input.setAttribute('required', 'required');
        });

        // Populate with data if provided (for edit mode)
        if (sessionData) {
            clone.querySelector('input[name="sessionID[]"]').value = sessionData.sessionID || '';
            clone.querySelector('input[name="sessionName[]"]').value = sessionData.sessionName || '';
            if (sessionData.sessionDate) {
                clone.querySelector('input[name="sessionDate[]"]').value = sessionData.sessionDate;
            }
            if (sessionData.startTime) {
                clone.querySelector('input[name="startTime[]"]').value = sessionData.startTime;
            }
            if (sessionData.endTime) {
                clone.querySelector('input[name="endTime[]"]').value = sessionData.endTime;
            }
            if (sessionData.roomID) {
                clone.querySelector('select[name="roomID[]"]').value = sessionData.roomID;
            }
            if (sessionData.teacherID) {
                clone.querySelector('select[name="teacherID[]"]').value = sessionData.teacherID;
            }
        }

        container.appendChild(clone);
        sessionCount++;
    }

    function removeSession(button) {
        const row = button.closest('.session-row');
        if (row) {
            row.remove();
        }
    }

    function resetForm() {
        form.reset();
        document.getElementById('sessionsContainer').innerHTML = '';
        sessionCount = 0;
        addSession();
    }

    // Initialize with existing sessions if in edit mode, otherwise add one empty session
    window.addEventListener('load', function () {
        <% if (isEditMode && schedule.getSessions() != null && !schedule.getSessions().isEmpty()) { %>
            <% SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm"); %>
            <% SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); %>
            <% for (Session s : schedule.getSessions()) { %>
            addSession({
                sessionID: '<%= s.getSessionID() %>',
                sessionName: '<%= s.getSessionName() != null ? s.getSessionName() : "" %>',
                sessionDate: '<%= s.getSessionDate() != null ? dateFormat.format(s.getSessionDate()) : "" %>',
                startTime: '<%= s.getStartTime() != null ? s.getStartTime() : "" %>',
                endTime: '<%= s.getEndTime() != null ? s.getEndTime() : "" %>',
                roomID: '<%= s.getRoom() != null ? s.getRoom().getRoomID() : "" %>',
                teacherID: '<%= s.getTeacher() != null ? s.getTeacher().getId() : "" %>'
            });
            <% } %>
        <% } else { %>
            addSession();
        <% } %>
    });
</script>
</body>
</html>
