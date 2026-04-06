package vn.iotstar.coolenglish.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SignupControllerOtpTest {

    @Test
    void generateOtpCodeShouldReturnSixDigits() {
        String otp = SignupController.generateOtpCode();
        assertEquals(6, otp.length());
        assertTrue(otp.matches("\\d{6}"));
    }
}

