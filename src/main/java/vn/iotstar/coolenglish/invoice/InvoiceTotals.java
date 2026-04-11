package vn.iotstar.coolenglish.invoice;

import java.io.Serializable;

/** Tổng hợp tiền: tạm tính, thuế, giảm giá, thanh toán. */
public final class InvoiceTotals implements Serializable {

    private static final long serialVersionUID = 1L;

    private final double subtotalExVat;
    private final double vatRatePercent;
    private final double vatAmount;
    private final double discountAmount;
    private final double grandTotal;

    public InvoiceTotals(double subtotalExVat, double vatRatePercent, double vatAmount, double discountAmount,
            double grandTotal) {
        this.subtotalExVat = subtotalExVat;
        this.vatRatePercent = vatRatePercent;
        this.vatAmount = vatAmount;
        this.discountAmount = discountAmount;
        this.grandTotal = grandTotal;
    }

    public double getSubtotalExVat() {
        return subtotalExVat;
    }

    public double getVatRatePercent() {
        return vatRatePercent;
    }

    public double getVatAmount() {
        return vatAmount;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public double getGrandTotal() {
        return grandTotal;
    }
}
