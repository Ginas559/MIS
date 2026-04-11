package vn.iotstar.coolenglish.invoice;

import java.io.Serializable;
import java.time.LocalDateTime;

/** Khối thông tin giao dịch thanh toán gắn với hóa đơn. */
public final class PaymentTransactionSection implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String paymentMethodLabel;
    private final String transactionRef;
    private final LocalDateTime paymentTimestamp;
    private final String paymentStatusLabel;
    private final String invoiceStatusLabel;

    public PaymentTransactionSection(String paymentMethodLabel, String transactionRef, LocalDateTime paymentTimestamp,
            String paymentStatusLabel, String invoiceStatusLabel) {
        this.paymentMethodLabel = paymentMethodLabel;
        this.transactionRef = transactionRef;
        this.paymentTimestamp = paymentTimestamp;
        this.paymentStatusLabel = paymentStatusLabel;
        this.invoiceStatusLabel = invoiceStatusLabel;
    }

    public String getPaymentMethodLabel() {
        return paymentMethodLabel;
    }

    public String getTransactionRef() {
        return transactionRef;
    }

    public LocalDateTime getPaymentTimestamp() {
        return paymentTimestamp;
    }

    public String getPaymentStatusLabel() {
        return paymentStatusLabel;
    }

    public String getInvoiceStatusLabel() {
        return invoiceStatusLabel;
    }
}
