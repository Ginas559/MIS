package vn.iotstar.coolenglish.util;

import java.util.Properties;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public final class EmailUtils {

    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_SSL_PORT = "465";

    // Hardcoded SMTP credentials per local project requirement.
    private static final String FROM_EMAIL = "levanphong511@gmail.com";
    private static final String APP_PASSWORD = "qapc khkp csdb wubd".replace(" ", "");

    private EmailUtils() {
    }

    public static boolean isSmtpConfigured() {
        return FROM_EMAIL != null && !FROM_EMAIL.isBlank()
                && APP_PASSWORD != null && !APP_PASSWORD.isBlank();
    }

    public static String buildMissingSmtpConfigMessage() {
        return "OTP email is not available because hardcoded SMTP credentials are empty.";
    }

    public static void sendVerificationCode(String toEmail, String code) {
        sendEmail(toEmail, "CoolEnglish - Ma xac nhan dang ky",
                "Chao ban, ma xac nhan cua ban la: " + code + ". Vui long khong chia se ma nay.");
    }

    public static void sendNotificationEmail(String toEmail, String subject, String body) {
        sendEmail(toEmail, subject, body);
    }

    public static void sendEmail(String toEmail, String subject, String body) {
        if (!isSmtpConfigured()) {
            throw new IllegalStateException(buildMissingSmtpConfigMessage());
        }

        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.socketFactory.port", SMTP_SSL_PORT);
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", SMTP_SSL_PORT);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, APP_PASSWORD);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Cannot send email via Gmail SMTP: " + e.getMessage(), e);
        }
    }
}
