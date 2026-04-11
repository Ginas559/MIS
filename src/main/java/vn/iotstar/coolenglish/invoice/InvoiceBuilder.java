package vn.iotstar.coolenglish.invoice;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Builder Pattern: lắp ráp hóa đơn chi tiết từ nhiều thành phần (header, người mua, lớp học, dòng phí, tổng tiền, thanh toán).
 */
public class InvoiceBuilder {

    private String invoiceNumber;
    private InvoiceHeaderInfo header;
    private BuyerInfo buyer;
    private ClassEnrollmentSection classSection;
    private final List<InvoiceLineItem> lineItems = new ArrayList<>();
    private InvoiceTotals totals;
    private PaymentTransactionSection paymentSection;
    private final List<String> footnotes = new ArrayList<>();

    public InvoiceBuilder invoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
        return this;
    }

    public InvoiceBuilder header(InvoiceHeaderInfo header) {
        this.header = header;
        return this;
    }

    public InvoiceBuilder buyer(BuyerInfo buyer) {
        this.buyer = buyer;
        return this;
    }

    public InvoiceBuilder classSection(ClassEnrollmentSection classSection) {
        this.classSection = classSection;
        return this;
    }

    public InvoiceBuilder addLineItem(InvoiceLineItem item) {
        Objects.requireNonNull(item, "line item");
        lineItems.add(item);
        return this;
    }

    public InvoiceBuilder totals(InvoiceTotals totals) {
        this.totals = totals;
        return this;
    }

    public InvoiceBuilder paymentSection(PaymentTransactionSection paymentSection) {
        this.paymentSection = paymentSection;
        return this;
    }

    public InvoiceBuilder addFootnote(String note) {
        if (note != null && !note.isBlank()) {
            footnotes.add(note.trim());
        }
        return this;
    }

    public InvoiceDocument build() {
        Objects.requireNonNull(invoiceNumber, "invoiceNumber");
        Objects.requireNonNull(header, "header");
        Objects.requireNonNull(buyer, "buyer");
        Objects.requireNonNull(classSection, "classSection");
        Objects.requireNonNull(totals, "totals");
        Objects.requireNonNull(paymentSection, "paymentSection");
        if (lineItems.isEmpty()) {
            throw new IllegalStateException("Hoa don phai co it nhat mot dong chi tiet.");
        }
        return new InvoiceDocument(invoiceNumber, header, buyer, classSection, new ArrayList<>(lineItems), totals,
                paymentSection, new ArrayList<>(footnotes));
    }
}
