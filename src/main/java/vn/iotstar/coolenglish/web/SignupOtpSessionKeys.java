package vn.iotstar.coolenglish.web;

/**
 * Session keys shared by signup and OTP verification controllers.
 */
public final class SignupOtpSessionKeys {

    public static final String OTP_CODE = "otpVerify";
    public static final String OTP_EXPIRES_AT = "otpExpiresAt";
    public static final String PENDING_SIGNUP = "pendingSignup";

    private SignupOtpSessionKeys() {
    }
}

