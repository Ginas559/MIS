package vn.iotstar.coolenglish.payment;

import java.util.Map;

import vn.iotstar.coolenglish.entity.Payment;
import vn.iotstar.coolenglish.enums.PaymentStatus;

public class MBBankPaymentStrategy implements PaymentStrategy {

    @Override
    public void pay(Payment payment) {
        payment.setStatus(PaymentStatus.PENDING);
        payment.setNote("Chuyen khoan MB Bank. Noi dung: PAYMENT_" + payment.getPaymentID());
    }

    @Override
    public void handleCallback(Payment payment, Map<String, String> response) {
        String validTransfer = response == null ? null : response.get("validTransfer");
        if ("true".equalsIgnoreCase(validTransfer)) {
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setNote("MB Bank transfer confirmed.");
            return;
        }
        payment.setStatus(PaymentStatus.FAILED);
        payment.setNote("MB Bank transfer invalid or timeout.");
    }
}
