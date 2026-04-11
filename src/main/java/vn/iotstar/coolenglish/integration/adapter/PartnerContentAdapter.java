package vn.iotstar.coolenglish.integration.adapter;

import java.io.StringReader;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import vn.iotstar.coolenglish.entity.AcademicContent;

public class PartnerContentAdapter implements AcademicContent {

    private final IDPExternalSystem externalSystem;
    private final String partnerCode;
    private final String onlineExamLink;
    private String title;
    private String description;

    public PartnerContentAdapter(IDPExternalSystem externalSystem, String partnerCode, String onlineExamLink) {
        if (externalSystem == null) {
            throw new IllegalArgumentException("externalSystem is required.");
        }
        this.externalSystem = externalSystem;
        this.partnerCode = partnerCode;
        this.onlineExamLink = onlineExamLink;
        hydrateFromPartnerPayload();
    }

    @Override
    public Long getId() {
        return null;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String displayContent() {
        return onlineExamLink;
    }

    @Override
    public void add(AcademicContent content) {
        throw new UnsupportedOperationException("Partner content is a leaf node.");
    }

    @Override
    public void remove(AcademicContent content) {
        throw new UnsupportedOperationException("Partner content is a leaf node.");
    }

    @Override
    public List<AcademicContent> getChildren() {
        return Collections.emptyList();
    }

    private void hydrateFromPartnerPayload() {
        String xmlData = externalSystem.getRawXmlResults(partnerCode);
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
            dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            dbf.setXIncludeAware(false);
            dbf.setExpandEntityReferences(false);

            Document document = dbf.newDocumentBuilder().parse(new InputSource(new StringReader(xmlData)));
            NodeList results = document.getElementsByTagName("result");
            if (results.getLength() > 0) {
                Element first = (Element) results.item(0);
                this.title = readTag(first, "examCode");
                this.description = "Noi dung duoc dong bo tu doi tac " + partnerCode + ".";
                return;
            }

            NodeList records = document.getElementsByTagName("record");
            if (records.getLength() > 0) {
                this.title = "IDP IELTS";
                this.description = "Bai thi doi tac IDP duoc chuyen doi thanh hoc lieu noi bo.";
                return;
            }

            throw new IllegalArgumentException("Unsupported partner payload.");
        } catch (Exception e) {
            throw new IllegalStateException("Cannot adapt partner payload to academic content.", e);
        }
    }

    private String readTag(Element parent, String tagName) {
        NodeList nodeList = parent.getElementsByTagName(tagName);
        if (nodeList.getLength() == 0) {
            throw new IllegalArgumentException("Missing XML tag: " + tagName);
        }
        String value = nodeList.item(0).getTextContent();
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("XML tag is blank: " + tagName);
        }
        return value.trim();
    }
}

