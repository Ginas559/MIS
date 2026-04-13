package vn.iotstar.coolenglish.service;

import java.time.LocalDateTime;
import java.util.UUID;

import vn.iotstar.coolenglish.dao.impl.NotificationDAO;
import vn.iotstar.coolenglish.entity.ExamResult;
import vn.iotstar.coolenglish.entity.Notification;

public class NotificationObserver implements ResultObserver {

    private final NotificationDAO notificationDAO;

    public NotificationObserver(NotificationDAO notificationDAO) {
        this.notificationDAO = notificationDAO;
    }

    @Override
    public void update(ExamResult result) {
        if (result == null || result.getClassID() == null || result.getStudentEmail() == null
                || result.getStudentEmail().isBlank() || !result.hasRecordedScore()) {
            return;
        }

        Notification notification = new Notification(
                UUID.randomUUID().toString(),
                result.getClassID(),
                buildContent(result),
                LocalDateTime.now());

        notificationDAO.insert(notification);
        notification.sendEmail(result.getStudentEmail());
    }

    private String buildContent(ExamResult result) {
        StringBuilder content = new StringBuilder();
        content.append("Kết quả của bạn đã được cập nhật.\n\n");
        content.append("Mã bài thi: ").append(result.getExamCode()).append("\n");
        content.append("Điểm tổng: ").append(result.getScore() == null ? "(Chưa có điểm tổng)" : result.getScore()).append("\n");

        // Chi tiết kỹ năng
        StringBuilder skills = new StringBuilder();
        if (result.usesListening()) {
            skills.append("Listening: ").append(result.getDisplayListeningScore()).append("\n");
        }
        if (result.usesReading()) {
            skills.append("Reading: ").append(result.getDisplayReadingScore()).append("\n");
        }
        if (result.usesSpeaking()) {
            skills.append("Speaking: ").append(result.getDisplaySpeakingScore()).append("\n");
        }
        if (result.usesWriting()) {
            skills.append("Writing: ").append(result.getDisplayWritingScore()).append("\n");
        }

        if (skills.length() > 0) {
            content.append("Chi tiết kỹ năng:\n").append(skills);
        }

        return content.toString().trim();
    }
}
