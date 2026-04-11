package vn.iotstar.coolenglish.invoice;

import java.io.Serializable;

/** Một dòng chi tiết trên hóa đơn (dịch vụ / khoản phí). */
public final class InvoiceLineItem implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String lineCode;
    private final String description;
    private final int quantity;
    private final double unitPrice;
    private final double lineTotal;

    public InvoiceLineItem(String lineCode, String description, int quantity, double unitPrice, double lineTotal) {
        this.lineCode = lineCode;
        this.description = description;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.lineTotal = lineTotal;
    }

    public String getLineCode() {
        return lineCode;
    }

    public String getDescription() {
        return description;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public double getLineTotal() {
        return lineTotal;
    }
}
