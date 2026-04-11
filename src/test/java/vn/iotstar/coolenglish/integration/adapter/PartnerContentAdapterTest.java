package vn.iotstar.coolenglish.integration.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import vn.iotstar.coolenglish.entity.Module;

class PartnerContentAdapterTest {

    @Test
    void shouldMapIdpPayloadToAcademicContentNode() {
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
                        </results>
                        """;
            }
        };

        PartnerContentAdapter adapter = new PartnerContentAdapter(externalSystem, "IDP", "https://idp.example.com/exam/ielts");

        assertEquals("IELTS", adapter.getTitle());
        assertEquals("https://idp.example.com/exam/ielts", adapter.displayContent());
    }

    @Test
    void shouldBeAttachableIntoModuleTreeAsLeaf() {
        PartnerContentAdapter adapter = new PartnerContentAdapter(new IDPExternalSystem(), "IDP",
                "https://idp.example.com/exam/online");
        Module module = new Module("Partner Module", "External exams");

        module.add(adapter);

        assertEquals(1, module.getChildren().size());
        assertTrue(module.getChildren().contains(adapter));
        assertThrows(UnsupportedOperationException.class, () -> adapter.add(module));
    }
}

