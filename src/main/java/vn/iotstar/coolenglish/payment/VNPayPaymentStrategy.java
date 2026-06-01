package vn.iotstar.coolenglish.payment;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import vn.iotstar.coolenglish.entity.Payment;
import vn.iotstar.coolenglish.enums.PaymentStatus;

public class VNPayPaymentStrategy implements PaymentStrategy {

    @Override
    public void pay(Payment payment) {
        payment.setStatus(PaymentStatus.PENDING);
        String transactionRef = payment.getTransactionRef();
        String amountText = String.valueOf(payment.getAmount());
        String redirectUrl = "/student/payment/vnpay/mock?txnRef="
                + encode(transactionRef)
                + "&amount=" + encode(amountText);
        payment.setRedirectUrl(redirectUrl);
    }

    @Override
    public void handleCallback(Payment payment, Map<String, String> response) {
        String responseCode = response == null ? null : response.get("responseCode");
        if ("00".equals(responseCode)) {
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setNote("VNPay callback success.");
            return;
        }
        payment.setStatus(PaymentStatus.FAILED);
        payment.setNote("VNPay callback failed, responseCode=" + responseCode);
    }

    private String encode(String value) {
        return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8);
    }
}
