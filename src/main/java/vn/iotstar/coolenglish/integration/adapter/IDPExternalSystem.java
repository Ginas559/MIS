package vn.iotstar.coolenglish.integration.adapter;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class IDPExternalSystem {

    public String getRawXmlResults(String partnerCode) {
        if (partnerCode == null || partnerCode.isBlank()) {
            throw new IllegalArgumentException("partnerCode is required.");
        }

        if (!"IDP".equalsIgnoreCase(partnerCode.trim())) {
            throw new IllegalArgumentException("Unsupported partner code: " + partnerCode);
        }

        try (InputStream inputStream = IDPExternalSystem.class.getClassLoader().getResourceAsStream("dataset.xml")) {
            if (inputStream == null) {
                throw new IllegalStateException("Missing dataset.xml in classpath.");
            }
            byte[] bytes = inputStream.readAllBytes();
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read dataset.xml from classpath.", e);
        }
    }
}

