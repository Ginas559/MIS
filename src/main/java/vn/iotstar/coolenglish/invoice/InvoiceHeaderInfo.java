package vn.iotstar.coolenglish.invoice;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Thông tin đơn vị phát hành hóa đơn (thành phần header).
 */
public final class InvoiceHeaderInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String issuerName;
    private final String issuerAddress;
    private final String issuerTaxCode;
    private final String issuerHotline;
    private final String documentTitle;
    private final LocalDate issueDate;

    public InvoiceHeaderInfo(String issuerName, String issuerAddress, String issuerTaxCode, String issuerHotline,
            String documentTitle, LocalDate issueDate) {
        this.issuerName = issuerName;
        this.issuerAddress = issuerAddress;
        this.issuerTaxCode = issuerTaxCode;
        this.issuerHotline = issuerHotline;
        this.documentTitle = documentTitle;
        this.issueDate = issueDate;
    }

    public String getIssuerName() {
        return issuerName;
    }

    public String getIssuerAddress() {
        return issuerAddress;
    }

    public String getIssuerTaxCode() {
        return issuerTaxCode;
    }

    public String getIssuerHotline() {
        return issuerHotline;
    }

    public String getDocumentTitle() {
        return documentTitle;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }
}
