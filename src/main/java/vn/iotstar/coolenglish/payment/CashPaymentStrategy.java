package vn.iotstar.coolenglish.payment;

import vn.iotstar.coolenglish.entity.Payment;
import vn.iotstar.coolenglish.enums.PaymentStatus;

public class CashPaymentStrategy implements PaymentStrategy {

    @Override
    public void pay(Payment payment) {
        payment.setStatus(PaymentStatus.PENDING);
        payment.setNote("Cho staff xac nhan thanh toan tien mat.");
    }

    @Override
    public void confirm(Payment payment) {
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setNote("Cash payment confirmed by staff.");
    }
}
