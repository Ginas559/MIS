<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="vn.iotstar.coolenglish.invoice.InvoiceDocument" %>
<%@ page import="vn.iotstar.coolenglish.invoice.InvoiceLineItem" %>
<%@ page import="vn.iotstar.coolenglish.service.StudentInvoiceService" %>
<%
    InvoiceDocument invoice = (InvoiceDocument) request.getAttribute("invoice");
    String error = (String) request.getAttribute("error");
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Hóa đơn chi tiết</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/vendor/bootstrap/css/bootstrap.min.css">
</head>
<body class="container py-4">
    <h1 class="h4 mb-3">Hóa đơn chi tiết</h1>

    <% if (error != null) { %>
        <div class="alert alert-warning"><%= error %></div>
        <a class="btn btn-primary" href="${pageContext.request.contextPath}/course">Về khóa học</a>
    <% } else if (invoice == null) { %>
        <div class="alert alert-warning">Không có dữ liệu hóa đơn.</div>
    <% } else { %>
        <div class="d-flex flex-wrap gap-2 mb-3">
            <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/course">Về danh sách khóa học</a>
            <a class="btn btn-danger"
               href="${pageContext.request.contextPath}/student/invoice/pdf?txnRef=<%= invoice.getPaymentSection().getTransactionRef() %>">
                Xuất PDF
            </a>
        </div>

        <div class="card mb-3 border-primary">
            <div class="card-header bg-primary text-white">
                <%= invoice.getHeader().getDocumentTitle() %>
                <span class="float-end">Số HĐ: <strong><%= invoice.getInvoiceNumber() %></strong></span>
            </div>
            <div class="card-body small">
                <div class="row">
                    <div class="col-md-6 mb-3">
                        <h6 class="text-uppercase text-muted">Đơn vị phát hành</h6>
                        <p class="mb-1"><strong><%= invoice.getHeader().getIssuerName() %></strong></p>
                        <p class="mb-1"><%= invoice.getHeader().getIssuerAddress() %></p>
                        <p class="mb-1">MST: <%= invoice.getHeader().getIssuerTaxCode() %> &mdash; Hotline: <%= invoice.getHeader().getIssuerHotline() %></p>
                        <p class="mb-0">Ngày lập: <%= StudentInvoiceService.formatDate(invoice.getHeader().getIssueDate()) %></p>
                    </div>
                    <div class="col-md-6 mb-3">
                        <h6 class="text-uppercase text-muted">Người mua / Học viên</h6>
                        <p class="mb-1"><strong><%= invoice.getBuyer().getFullName() %></strong></p>
                        <p class="mb-1">Email: <%= invoice.getBuyer().getEmail() %></p>
                        <p class="mb-1">Điện thoại: <%= invoice.getBuyer().getPhone() %></p>
                        <p class="mb-0">Mã HV: <%= invoice.getBuyer().getStudentCode() %></p>
                    </div>
                </div>
            </div>
        </div>

        <div class="card mb-3">
            <div class="card-header">Khóa học &amp; lớp</div>
            <div class="card-body row small">
                <div class="col-md-6">
                    <p><strong>Lớp:</strong> <%= invoice.getClassSection().getClassName() %> (<%= invoice.getClassSection().getClassId() %>)</p>
                    <p><strong>Khóa:</strong> <%= invoice.getClassSection().getCourseName() %> (<%= invoice.getClassSection().getCourseId() %>)</p>
                    <p><strong>Cấp độ:</strong> <%= invoice.getClassSection().getCourseLevel() %>
                        <% if (invoice.getClassSection().getCourseDurationWeeks() != null) { %>
                            &mdash; <strong>Thời lượng:</strong> <%= invoice.getClassSection().getCourseDurationWeeks() %> tuần
                        <% } %>
                    </p>
                </div>
                <div class="col-md-6">
                    <p><strong>Thời gian lớp:</strong>
                        <%= StudentInvoiceService.formatDate(invoice.getClassSection().getClassStartDate()) %>
                        &rarr;
                        <%= StudentInvoiceService.formatDate(invoice.getClassSection().getClassEndDate()) %>
                    </p>
                    <p><strong>Phòng:</strong> <%= invoice.getClassSection().getRoomLabel() %></p>
                    <p class="mb-0"><strong>Lịch:</strong> <%= invoice.getClassSection().getScheduleSummary() %></p>
                </div>
            </div>
        </div>

        <div class="card mb-3">
            <div class="card-header">Chi tiết khoản phí</div>
            <div class="table-responsive">
                <table class="table table-sm table-striped mb-0">
                    <thead>
                    <tr>
                        <th>Mã</th>
                        <th>Mô tả</th>
                        <th class="text-end">SL</th>
                        <th class="text-end">Đơn giá</th>
                        <th class="text-end">Thành tiền</th>
                    </tr>
                    </thead>
                    <tbody>
                    <% for (InvoiceLineItem line : invoice.getLineItems()) { %>
                        <tr>
                            <td><%= line.getLineCode() %></td>
                            <td><%= line.getDescription() %></td>
                            <td class="text-end"><%= line.getQuantity() %></td>
                            <td class="text-end"><%= StudentInvoiceService.formatMoney(line.getUnitPrice()) %></td>
                            <td class="text-end"><%= StudentInvoiceService.formatMoney(line.getLineTotal()) %></td>
                        </tr>
                    <% } %>
                    </tbody>
                </table>
            </div>
            <div class="card-body">
                <div class="row justify-content-end">
                    <div class="col-md-5">
                        <p class="mb-1 d-flex justify-content-between"><span>Tạm tính (trước VAT)</span><span><%= StudentInvoiceService.formatMoney(invoice.getTotals().getSubtotalExVat()) %></span></p>
                        <p class="mb-1 d-flex justify-content-between"><span>VAT (<%= (int) invoice.getTotals().getVatRatePercent() %>%)</span><span><%= StudentInvoiceService.formatMoney(invoice.getTotals().getVatAmount()) %></span></p>
                        <p class="mb-1 d-flex justify-content-between"><span>Giảm giá</span><span><%= StudentInvoiceService.formatMoney(invoice.getTotals().getDiscountAmount()) %></span></p>
                        <hr>
                        <p class="mb-0 d-flex justify-content-between fw-bold"><span>Tổng thanh toán</span><span class="text-success"><%= StudentInvoiceService.formatMoney(invoice.getTotals().getGrandTotal()) %></span></p>
                    </div>
                </div>
            </div>
        </div>

        <div class="card mb-3">
            <div class="card-header">Thanh toán</div>
            <div class="card-body small">
                <p class="mb-1"><strong>Phương thức:</strong> <%= invoice.getPaymentSection().getPaymentMethodLabel() %></p>
                <p class="mb-1"><strong>Mã giao dịch:</strong> <%= invoice.getPaymentSection().getTransactionRef() %></p>
                <p class="mb-1"><strong>Thời điểm:</strong> <%= invoice.getPaymentSection().getPaymentTimestamp() != null ? invoice.getPaymentSection().getPaymentTimestamp().toString() : "—" %></p>
                <p class="mb-0"><strong>Trạng thái:</strong> <%= invoice.getPaymentSection().getPaymentStatusLabel() %> &mdash; Hóa đơn: <%= invoice.getPaymentSection().getInvoiceStatusLabel() %></p>
            </div>
        </div>

        <% if (!invoice.getFootnotes().isEmpty()) { %>
            <div class="alert alert-light border small mb-0">
                <% for (String note : invoice.getFootnotes()) { %>
                    <p class="mb-1">&bull; <%= note %></p>
                <% } %>
            </div>
        <% } %>
    <% } %>
</body>
</html>
