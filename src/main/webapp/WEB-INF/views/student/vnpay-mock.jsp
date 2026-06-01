<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    String txnRef = (String) request.getAttribute("txnRef");
    String amount = (String) request.getAttribute("amount");
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>VNPay Mock</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/vendor/bootstrap/css/bootstrap.min.css">
</head>
<body class="container py-4">
    <h1 class="h4 mb-3">Cong thanh toan VNPay (Mock)</h1>
    <p><strong>TxnRef:</strong> <%= txnRef %></p>
    <p><strong>Amount:</strong> <%= amount %></p>
    <a class="btn btn-success"
       href="${pageContext.request.contextPath}/student/payment/vnpay/callback?txnRef=<%= txnRef %>&responseCode=00">Thanh toan thanh cong</a>
    <a class="btn btn-outline-danger"
       href="${pageContext.request.contextPath}/student/payment/vnpay/callback?txnRef=<%= txnRef %>&responseCode=24">Thanh toan that bai</a>
</body>
</html>
