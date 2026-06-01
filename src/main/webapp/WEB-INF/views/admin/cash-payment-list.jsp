<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="vn.iotstar.coolenglish.entity.Payment" %>
<%
    List<Payment> payments = (List<Payment>) request.getAttribute("pendingCashPayments");
    String msg = request.getParameter("msg");
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Duyet thanh toan tien mat</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/vendor/bootstrap/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/app.css">
</head>
<body class="app-body">
<div class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h1 class="h4 app-title mb-0">Xac nhan thanh toan tien mat</h1>
        <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/admin/course">Ve quan ly khoa hoc</a>
    </div>

    <% if ("confirm_success".equals(msg)) { %>
        <div class="alert alert-success">Da xac nhan thanh toan tien mat.</div>
    <% } else if ("mark_failed_success".equals(msg)) { %>
        <div class="alert alert-warning">Da danh dau thanh toan that bai.</div>
    <% } else if ("error".equals(msg)) { %>
        <div class="alert alert-danger">Khong the cap nhat thanh toan. Vui long thu lai.</div>
    <% } %>

    <div class="table-responsive app-section">
        <table class="table table-bordered align-middle mb-0">
            <thead>
            <tr>
                <th>Payment ID</th>
                <th>Txn Ref</th>
                <th>Hoc vien</th>
                <th>Lop hoc</th>
                <th>So tien</th>
                <th>Ngay tao</th>
                <th>Thao tac</th>
            </tr>
            </thead>
            <tbody>
            <%
                if (payments != null && !payments.isEmpty()) {
                    for (Payment payment : payments) {
            %>
            <tr>
                <td><%= payment.getPaymentID() %></td>
                <td><%= payment.getTransactionRef() %></td>
                <td><%= payment.getStudentEmail() %></td>
                <td><%= payment.getClassID() %></td>
                <td><%= payment.getAmount() %></td>
                <td><%= payment.getPaymentDate() %></td>
                <td class="d-flex gap-2">
                    <form method="post" action="${pageContext.request.contextPath}/admin/payment/cash/update">
                        <input type="hidden" name="txnRef" value="<%= payment.getTransactionRef() %>">
                        <input type="hidden" name="action" value="confirm">
                        <button type="submit" class="btn btn-success btn-sm">Xac nhan da thanh toan</button>
                    </form>
                    <form method="post" action="${pageContext.request.contextPath}/admin/payment/cash/update">
                        <input type="hidden" name="txnRef" value="<%= payment.getTransactionRef() %>">
                        <input type="hidden" name="action" value="fail">
                        <button type="submit" class="btn btn-outline-danger btn-sm">Danh dau that bai</button>
                    </form>
                </td>
            </tr>
            <%
                    }
                } else {
            %>
            <tr>
                <td colspan="7" class="text-center app-subtle">Khong co giao dich tien mat cho duyet.</td>
            </tr>
            <%
                }
            %>
            </tbody>
        </table>
    </div>
</div>
</body>
</html>
