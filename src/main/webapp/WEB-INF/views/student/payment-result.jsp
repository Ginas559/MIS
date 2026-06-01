<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="vn.iotstar.coolenglish.entity.Payment" %>
<%
    Payment payment = (Payment) request.getAttribute("payment");
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Ket qua thanh toan</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/vendor/bootstrap/css/bootstrap.min.css">
</head>
<body class="container py-4">
    <h1 class="h4 mb-3">Ket qua thanh toan</h1>
    <% if (payment == null) { %>
        <div class="alert alert-warning">Khong co du lieu thanh toan.</div>
    <% } else { %>
        <% if ("FAILED".equals(payment.getStatus().name())) { %>
            <div class="alert alert-danger">
                Ban chua duoc ghi danh, vui long thanh toan lai.
            </div>
        <% } else if ("COMPLETED".equals(payment.getStatus().name())) { %>
            <div class="alert alert-success">
                Thanh toan thanh cong, da ghi danh lop.
            </div>
        <% } %>

        <div class="card">
            <div class="card-body">
                <p><strong>Ma giao dich:</strong> <%= payment.getTransactionRef() %></p>
                <p><strong>Phuong thuc:</strong> <%= payment.getMethod() %></p>
                <p><strong>So tien:</strong> <%= payment.getAmount() %></p>
                <p><strong>Trang thai:</strong> <%= payment.getStatus() %></p>
                <p><strong>Ghi chu:</strong> <%= payment.getNote() == null ? "" : payment.getNote() %></p>
            </div>
        </div>

        <% if ("MB_BANK".equals(payment.getMethod()) && "PENDING".equals(payment.getStatus().name())) { %>
            <div class="mt-3">
                <a class="btn btn-success"
                   href="${pageContext.request.contextPath}/student/payment/mb/simulate?txnRef=<%= payment.getTransactionRef() %>&validTransfer=true">Mo phong da chuyen khoan</a>
                <a class="btn btn-outline-danger"
                   href="${pageContext.request.contextPath}/student/payment/mb/simulate?txnRef=<%= payment.getTransactionRef() %>&validTransfer=false">Mo phong loi chuyen khoan</a>
            </div>
        <% } %>

        <% if ("CASH".equals(payment.getMethod()) && "PENDING".equals(payment.getStatus().name())) { %>
            <div class="alert alert-info mt-3 mb-0">
                Yeu cau thanh toan tien mat dang cho staff xac nhan.
            </div>
        <% } %>

        <% if ("COMPLETED".equals(payment.getStatus().name())) { %>
            <div class="mt-3 d-flex flex-wrap gap-2">
                <a class="btn btn-primary"
                   href="${pageContext.request.contextPath}/student/invoice/detail?txnRef=<%= payment.getTransactionRef() %>">Xem hóa đơn chi tiết</a>
                <a class="btn btn-danger"
                   href="${pageContext.request.contextPath}/student/invoice/pdf?txnRef=<%= payment.getTransactionRef() %>">Xuất hóa đơn PDF</a>
                <a class="btn btn-outline-dark"
                   href="${pageContext.request.contextPath}/student/payment/refund?txnRef=<%= payment.getTransactionRef() %>">Thử nghiệm Refund</a>
            </div>
        <% } %>
    <% } %>

    <div class="mt-3">
        <a href="${pageContext.request.contextPath}/course" class="btn btn-primary">Ve danh sach khoa hoc</a>
    </div>
</body>
</html>
