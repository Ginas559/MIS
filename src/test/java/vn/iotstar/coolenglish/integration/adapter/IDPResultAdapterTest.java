package vn.iotstar.coolenglish.integration.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import vn.iotstar.coolenglish.dao.impl.ExamResultDAO;
import vn.iotstar.coolenglish.entity.ExamResult;

class IDPResultAdapterTest {

    @Test
    void shouldConvertXmlAndPassEntitiesToDao() {
        CapturingExamResultDAO dao = new CapturingExamResultDAO();
        IDPExternalSystem externalSystem = new IDPExternalSystem() {
            @Override
            public String getRawXmlResults(String partnerCode) {
                return """
                        <results>
                            <result>
                                <studentEmail>a@student.vn</studentEmail>
                                <examCode>IELTS</examCode>
                                <score>7.0</score>
                                <takenAt>2026-01-01</takenAt>
                            </result>
                            <result>
                                <studentEmail>b@student.vn</studentEmail>
                                <examCode>IELTS</examCode>
                                <score>6.0</score>
                                <takenAt>2026-01-02</takenAt>
                            </result>
                        </results>
                        """;
            }
        };

        IDPResultAdapter adapter = new IDPResultAdapter(externalSystem, dao);
        adapter.syncExamResults("IDP");

        assertEquals(2, dao.saved.size());
        assertEquals("IDP", dao.saved.get(0).getPartnerCode());
        assertEquals("a@student.vn", dao.saved.get(0).getStudentEmail());
        assertEquals("IELTS", dao.saved.get(0).getExamCode());
    }

    @Test
    void shouldConvertDatasetRecordSchemaAndPassEntitiesToDao() {
        CapturingExamResultDAO dao = new CapturingExamResultDAO();
        IDPExternalSystem externalSystem = new IDPExternalSystem() {
            @Override
            public String getRawXmlResults(String partnerCode) {
                return """
                        <dataset>
                            <record>
                                <candidateId>ABC123</candidateId>
                                <fullName>Mock Student</fullName>
                                <score>8.0</score>
                                <testDate>2026-02-01</testDate>
                            </record>
                        </dataset>
                        """;
            }
        };

        IDPResultAdapter adapter = new IDPResultAdapter(externalSystem, dao);
        adapter.syncExamResults("IDP");

        assertEquals(1, dao.saved.size());
        assertEquals("abc123@idp.partner.local", dao.saved.get(0).getStudentEmail());
        assertEquals("IELTS", dao.saved.get(0).getExamCode());
        assertEquals(8.0, dao.saved.get(0).getScore());
    }

    private static final class CapturingExamResultDAO extends ExamResultDAO {
        private final List<ExamResult> saved = new ArrayList<>();

        @Override
        public void insertBatch(List<ExamResult> examResults) {
            saved.addAll(examResults);
        }
    }
}

