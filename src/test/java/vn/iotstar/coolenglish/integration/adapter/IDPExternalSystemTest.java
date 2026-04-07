package vn.iotstar.coolenglish.integration.adapter;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class IDPExternalSystemTest {

    @Test
    void shouldLoadDatasetXmlFromClasspath() {
        IDPExternalSystem externalSystem = new IDPExternalSystem();

        String xml = externalSystem.getRawXmlResults("IDP");

        assertTrue(xml.contains("<dataset>"));
        assertTrue(xml.contains("<record>"));
    }
}

