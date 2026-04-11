package vn.iotstar.coolenglish.invoice;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * Hóa đơn chi tiết (read-only), được tạo thông qua {@link InvoiceBuilder}.
 */
public final class InvoiceDocument implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String invoiceNumber;
    private final InvoiceHeaderInfo header;
    private final BuyerInfo buyer;
    private final ClassEnrollmentSection classSection;
    private final List<InvoiceLineItem> lineItems;
    private final InvoiceTotals totals;
    private final PaymentTransactionSection paymentSection;
    private final List<String> footnotes;

    InvoiceDocument(String invoiceNumber, InvoiceHeaderInfo header, BuyerInfo buyer, ClassEnrollmentSection classSection,
            List<InvoiceLineItem> lineItems, InvoiceTotals totals, PaymentTransactionSection paymentSection,
            List<String> footnotes) {
        this.invoiceNumber = invoiceNumber;
        this.header = header;
        this.buyer = buyer;
        this.classSection = classSection;
        this.lineItems = List.copyOf(lineItems);
        this.totals = totals;
        this.paymentSection = paymentSection;
        this.footnotes = List.copyOf(footnotes);
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public InvoiceHeaderInfo getHeader() {
        return header;
    }

    public BuyerInfo getBuyer() {
        return buyer;
    }

    public ClassEnrollmentSection getClassSection() {
        return classSection;
    }

    public List<InvoiceLineItem> getLineItems() {
        return Collections.unmodifiableList(lineItems);
    }

    public InvoiceTotals getTotals() {
        return totals;
    }

    public PaymentTransactionSection getPaymentSection() {
        return paymentSection;
    }

    public List<String> getFootnotes() {
        return Collections.unmodifiableList(footnotes);
    }
}
