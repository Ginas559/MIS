package vn.iotstar.coolenglish.payment;

import java.util.EnumMap;
import java.util.Map;

import vn.iotstar.coolenglish.enums.PaymentMethod;

public class PaymentProcessor {

    private final Map<PaymentMethod, PaymentStrategy> strategyMap = new EnumMap<>(PaymentMethod.class);

    public PaymentProcessor() {
        strategyMap.put(PaymentMethod.VNPAY, new VNPayPaymentStrategy());
        strategyMap.put(PaymentMethod.MB_BANK, new MBBankPaymentStrategy());
        strategyMap.put(PaymentMethod.CASH, new CashPaymentStrategy());
    }

    public PaymentStrategy getStrategy(PaymentMethod method) {
        PaymentStrategy strategy = strategyMap.get(method);
        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported payment method: " + method);
        }
        return strategy;
    }
}
