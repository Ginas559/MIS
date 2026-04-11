package vn.iotstar.coolenglish.payment;

import java.util.Map;

import vn.iotstar.coolenglish.entity.Payment;
import vn.iotstar.coolenglish.enums.PaymentStatus;

public interface PaymentStrategy {

    void pay(Payment payment);

    default void handleCallback(Payment payment, Map<String, String> response) {
        throw new UnsupportedOperationException("Callback is not supported for this payment method.");
    }

    default void confirm(Payment payment) {
        throw new UnsupportedOperationException("Manual confirmation is not supported for this payment method.");
    }

    default void refund(Payment payment) {
        if (payment.getStatus() == PaymentStatus.COMPLETED) {
            payment.setStatus(PaymentStatus.REFUNDED);
            return;
        }
        throw new IllegalStateException("Chi hoan tien cho thanh toan COMPLETED.");
    }
}
