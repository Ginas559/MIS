package vn.iotstar.coolenglish.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import vn.iotstar.coolenglish.util.EmailUtils;

@Entity
@Table(name = "Notifications")
public class Notification implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "notificationID", length = 50, nullable = false)
    private String notificationID;

    @Column(name = "relatedID", length = 50, nullable = false)
    private String relatedID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "relatedID", referencedColumnName = "classID", insertable = false, updatable = false)
    private EnglishClass englishClass;

    @Column(name = "content", length = 1000, nullable = false)
    private String content;

    @Column(name = "sendDate")
    private LocalDateTime sendDate;

    @Transient
    private String recipientEmail;

    public Notification() {
    }

    public Notification(String notificationID, String relatedID, String content, LocalDateTime sendDate) {
        this.notificationID = notificationID;
        this.relatedID = relatedID;
        this.content = content;
        this.sendDate = sendDate;
    }

    public String getNotificationID() {
        return notificationID;
    }

    public void setNotificationID(String notificationID) {
        this.notificationID = notificationID;
    }

    public String getRelatedID() {
        return relatedID;
    }

    public void setRelatedID(String relatedID) {
        this.relatedID = relatedID;
    }

    public EnglishClass getEnglishClass() {
        return englishClass;
    }

    public void setEnglishClass(EnglishClass englishClass) {
        this.englishClass = englishClass;
        this.relatedID = englishClass != null ? englishClass.getClassID() : null;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getSendDate() {
        return sendDate;
    }

    public void setSendDate(LocalDateTime sendDate) {
        this.sendDate = sendDate;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    public boolean sendEmail(String toEmail) {
        if (toEmail == null || toEmail.isBlank() || content == null || content.isBlank()) {
            return false;
        }
        try {
            EmailUtils.sendNotificationEmail(toEmail, "CoolEnglish - Thông báo kết quả học tập", content);
            this.recipientEmail = toEmail;
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
