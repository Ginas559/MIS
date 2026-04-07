package vn.iotstar.coolenglish.integration.adapter;

import java.io.StringReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import vn.iotstar.coolenglish.dao.impl.ExamResultDAO;
import vn.iotstar.coolenglish.entity.ExamResult;
import vn.iotstar.coolenglish.integration.IPartnerIntegration;

public class IDPResultAdapter implements IPartnerIntegration {

    private final IDPExternalSystem externalSystem;
    private final ExamResultDAO examResultDAO;

    public IDPResultAdapter() {
        this(new IDPExternalSystem(), new ExamResultDAO());
    }

    public IDPResultAdapter(IDPExternalSystem externalSystem, ExamResultDAO examResultDAO) {
        this.externalSystem = externalSystem;
        this.examResultDAO = examResultDAO;
    }

    @Override
    public void syncExamResults(String partnerCode) {
        String xmlData = externalSystem.getRawXmlResults(partnerCode);
        List<ExamResult> examResults = convertXmlToEntities(xmlData, partnerCode);
        examResultDAO.insertBatch(examResults);
    }

    List<ExamResult> convertXmlToEntities(String xmlData, String partnerCode) {
        if (xmlData == null || xmlData.isBlank()) {
            throw new IllegalArgumentException("XML data is empty.");
        }

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
            dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            dbf.setXIncludeAware(false);
            dbf.setExpandEntityReferences(false);

            Document document = dbf.newDocumentBuilder().parse(new InputSource(new StringReader(xmlData)));
            NodeList rows = document.getElementsByTagName("result");
            boolean legacySchema = rows.getLength() > 0;
            if (!legacySchema) {
                rows = document.getElementsByTagName("record");
            }

            if (rows.getLength() == 0) {
                throw new IllegalArgumentException("Unsupported XML structure. Expected <result> or <record> entries.");
            }

            List<ExamResult> results = new ArrayList<>();
            LocalDateTime syncedAt = LocalDateTime.now();
            for (int i = 0; i < rows.getLength(); i++) {
                Element element = (Element) rows.item(i);
                String studentEmail;
                String examCode;
                Double score;
                LocalDate takenAt;

                if (legacySchema) {
                    studentEmail = readTag(element, "studentEmail");
                    examCode = readTag(element, "examCode");
                    score = Double.parseDouble(readTag(element, "score"));
                    takenAt = LocalDate.parse(readTag(element, "takenAt"));
                } else {
                    String candidateId = readTag(element, "candidateId");
                    studentEmail = candidateId.toLowerCase() + "@idp.partner.local";
                    examCode = "IELTS";
                    score = Double.parseDouble(readTag(element, "score"));
                    takenAt = LocalDate.parse(readTag(element, "testDate"));
                }

                results.add(new ExamResult(partnerCode, studentEmail, examCode, score, takenAt, syncedAt));
            }
            return results;
        } catch (Exception e) {
            throw new IllegalStateException("Cannot convert partner XML payload.", e);
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

