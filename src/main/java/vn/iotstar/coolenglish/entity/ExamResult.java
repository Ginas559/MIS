package vn.iotstar.coolenglish.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import vn.iotstar.coolenglish.audit.listener.AuditEntityListener;

@Entity
@Table(name = "ExamResults")
@EntityListeners(AuditEntityListener.class)
public class ExamResult implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "partnerCode", length = 50, nullable = false)
    private String partnerCode;

    @Column(name = "studentEmail", length = 100, nullable = false)
    private String studentEmail;

    @Column(name = "examCode", length = 50, nullable = false)
    private String examCode;

    @Column(name = "classID", length = 50)
    private String classID;

    @Column(name = "teacher_id")
    private Long teacherID;

    @Column(name = "testType", length = 100)
    private String testType;

    @Column(name = "examFormat", length = 30)
    private String examFormat;

    @Column(name = "grade", length = 30)
    private String grade;

    @Column(name = "score")
    private Double score;

    @Column(name = "hasListening")
    private Boolean hasListening = Boolean.FALSE;

    @Column(name = "hasReading")
    private Boolean hasReading = Boolean.FALSE;

    @Column(name = "hasSpeaking")
    private Boolean hasSpeaking = Boolean.FALSE;

    @Column(name = "hasWriting")
    private Boolean hasWriting = Boolean.FALSE;

    @Column(name = "listeningScore")
    private Double listeningScore;

    @Column(name = "readingScore")
    private Double readingScore;

    @Column(name = "speakingScore")
    private Double speakingScore;

    @Column(name = "writingScore")
    private Double writingScore;

    @Column(name = "listeningRaw")
    private Integer listeningRaw;

    @Column(name = "readingRaw")
    private Integer readingRaw;

    @Column(name = "speakingRaw")
    private Integer speakingRaw;

    @Column(name = "writingRaw")
    private Integer writingRaw;

    @Column(name = "takenAt", nullable = false)
    private LocalDate takenAt;

    @Column(name = "syncedAt", nullable = false)
    private LocalDateTime syncedAt;

    public ExamResult() {
    }

    public ExamResult(String partnerCode, String studentEmail, String examCode, Double score, LocalDate takenAt,
            LocalDateTime syncedAt) {
        this.partnerCode = partnerCode;
        this.studentEmail = studentEmail;
        this.examCode = examCode;
        this.score = score;
        this.takenAt = takenAt;
        this.syncedAt = syncedAt;
        this.grade = calculateGrade();
    }

    @PrePersist
    @PreUpdate
    public void preparePersist() {
        if (partnerCode == null || partnerCode.isBlank()) {
            partnerCode = "INTERNAL";
        }
        if (takenAt == null) {
            takenAt = LocalDate.now();
        }
        syncedAt = LocalDateTime.now();
        normalizeSkills();
        syncCalculatedFields();
    }

    public String calculateGrade() {
        Double overall = calculateOverallScore();
        if (overall == null) {
            return null;
        }

        if (isIeltsFormat()) {
            if (overall >= 8.0) {
                return "A";
            }
            if (overall >= 6.5) {
                return "B";
            }
            if (overall >= 5.0) {
                return "C";
            }
            return "D";
        }

        if (overall >= 900) {
            return "A";
        }
        if (overall >= 700) {
            return "B";
        }
        if (overall >= 500) {
            return "C";
        }
        return "D";
    }

    public Double calculateOverallScore() {
        if (!hasAnyEnabledSkill()) {
            return score;
        }

        if (isIeltsFormat()) {
            return calculateIeltsOverall();
        }
        return calculateToeicOverall();
    }

    private Double calculateToeicOverall() {
        Double total = 0d;

        if (usesListening()) {
            total += getConvertedListeningScore();
        }
        if (usesReading()) {
            total += getConvertedReadingScore();
        }
        if (usesSpeaking()) {
            total += getConvertedSpeakingScore();
        }
        if (usesWriting()) {
            total += getConvertedWritingScore();
        }

        return total;
    }

    private Double calculateIeltsOverall() {
        double total = 0d;
        int skillCount = 0;

        if (usesListening()) {
            total += listeningScore == null ? 0d : listeningScore;
            skillCount++;
        }
        if (usesReading()) {
            total += readingScore == null ? 0d : readingScore;
            skillCount++;
        }
        if (usesSpeaking()) {
            total += speakingScore == null ? 0d : speakingScore;
            skillCount++;
        }
        if (usesWriting()) {
            total += writingScore == null ? 0d : writingScore;
            skillCount++;
        }

        if (skillCount == 0) {
            return null;
        }

        return roundToNearestHalf(total / skillCount);
    }

    public double getMaxPossibleScore() {
        double maxScore = 0d;
        if (isIeltsFormat()) {
            if (usesListening()) {
                maxScore += 9d;
            }
            if (usesReading()) {
                maxScore += 9d;
            }
            if (usesSpeaking()) {
                maxScore += 9d;
            }
            if (usesWriting()) {
                maxScore += 9d;
            }
            return usesListening() || usesReading() || usesSpeaking() || usesWriting() ? 9d : 0d;
        }

        if (usesListening()) {
            maxScore += 495d;
        }
        if (usesReading()) {
            maxScore += 495d;
        }
        if (usesSpeaking()) {
            maxScore += 200d;
        }
        if (usesWriting()) {
            maxScore += 200d;
        }
        return maxScore;
    }

    public String getSkillSummary() {
        StringBuilder builder = new StringBuilder();
        appendSkill(builder, usesListening(), "Listening");
        appendSkill(builder, usesReading(), "Reading");
        appendSkill(builder, usesSpeaking(), "Speaking");
        appendSkill(builder, usesWriting(), "Writing");
        return builder.length() == 0 ? "--" : builder.toString();
    }

    public String getDetailedScoreSummary() {
        StringBuilder builder = new StringBuilder();
        appendScore(builder, true, "L", getDisplayListeningScore());
        appendScore(builder, true, "R", getDisplayReadingScore());
        appendScore(builder, true, "S", getDisplaySpeakingScore());
        appendScore(builder, true, "W", getDisplayWritingScore());
        return builder.length() == 0 ? "--" : builder.toString();
    }

    private void appendSkill(StringBuilder builder, boolean condition, String label) {
        if (!condition) {
            return;
        }
        if (!builder.isEmpty()) {
            builder.append(", ");
        }
        builder.append(label);
    }

    private void appendScore(StringBuilder builder, boolean condition, String label, Double value) {
        if (!condition) {
            return;
        }
        if (!builder.isEmpty()) {
            builder.append(" | ");
        }
        builder.append(label).append(": ").append(value == null ? "--" : value);
    }

    private void normalizeSkills() {
        hasListening = Boolean.TRUE.equals(hasListening);
        hasReading = Boolean.TRUE.equals(hasReading);
        hasSpeaking = Boolean.TRUE.equals(hasSpeaking);
        hasWriting = Boolean.TRUE.equals(hasWriting);
    }

    private void syncCalculatedFields() {
        Double calculatedScore = calculateOverallScore();
        if (calculatedScore != null || hasAnyEnabledSkill()) {
            score = calculatedScore;
        }
        grade = calculateGrade();
    }

    private boolean hasAnyEnabledSkill() {
        return usesListening() || usesReading() || usesSpeaking() || usesWriting();
    }

    private boolean isIeltsFormat() {
        return examFormat != null && "IELTS".equalsIgnoreCase(examFormat.trim());
    }

    private Double roundToNearestHalf(double value) {
        return Math.round(value * 2d) / 2d;
    }

    private Double roundToNearestFive(double value) {
        return Math.round(value / 5d) * 5d;
    }

    private Double convertToeicListeningReading(Integer raw) {
        if (raw == null || raw <= 0) {
            return 0d;
        }
        double scaled = 5d + ((double) raw / 100d) * 490d;
        return roundToNearestFive(Math.min(495d, scaled));
    }

    private Double convertToeicSpeakingWriting(Integer raw, int maxRaw) {
        if (raw == null || raw <= 0) {
            return 0d;
        }
        double scaled = ((double) raw / (double) maxRaw) * 200d;
        return roundToNearestFive(Math.min(200d, scaled));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPartnerCode() {
        return partnerCode;
    }

    public void setPartnerCode(String partnerCode) {
        this.partnerCode = partnerCode;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public String getExamCode() {
        return examCode;
    }

    public void setExamCode(String examCode) {
        this.examCode = examCode;
    }

    public String getClassID() {
        return classID;
    }

    public void setClassID(String classID) {
        this.classID = classID;
    }

    public Long getTeacherID() {
        return teacherID;
    }

    public void setTeacherID(Long teacherID) {
        this.teacherID = teacherID;
    }

    public String getTestType() {
        return testType;
    }

    public void setTestType(String testType) {
        this.testType = testType;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
        this.grade = calculateGrade();
    }

    public boolean hasRecordedScore() {
        if (!hasAnyEnabledSkill()) {
            return score != null;
        }
        if (isIeltsFormat()) {
            return (usesListening() && listeningScore != null)
                    || (usesReading() && readingScore != null)
                    || (usesSpeaking() && speakingScore != null)
                    || (usesWriting() && writingScore != null);
        }
        return (usesListening() && listeningRaw != null)
                || (usesReading() && readingRaw != null)
                || (usesSpeaking() && speakingRaw != null)
                || (usesWriting() && writingRaw != null);
    }

    public Double getDisplayListeningScore() {
        return usesListening() ? getConvertedListeningScore() : 0d;
    }

    public Double getDisplayReadingScore() {
        return usesReading() ? getConvertedReadingScore() : 0d;
    }

    public Double getDisplaySpeakingScore() {
        return usesSpeaking() ? getConvertedSpeakingScore() : 0d;
    }

    public Double getDisplayWritingScore() {
        return usesWriting() ? getConvertedWritingScore() : 0d;
    }

    public Double getConvertedListeningScore() {
        if (!usesListening()) {
            return 0d;
        }
        if (isIeltsFormat()) {
            return listeningScore == null ? 0d : listeningScore;
        }
        return convertToeicListeningReading(listeningRaw);
    }

    public Double getConvertedReadingScore() {
        if (!usesReading()) {
            return 0d;
        }
        if (isIeltsFormat()) {
            return readingScore == null ? 0d : readingScore;
        }
        return convertToeicListeningReading(readingRaw);
    }

    public Double getConvertedSpeakingScore() {
        if (!usesSpeaking()) {
            return 0d;
        }
        if (isIeltsFormat()) {
            return speakingScore == null ? 0d : speakingScore;
        }
        return convertToeicSpeakingWriting(speakingRaw, 11);
    }

    public Double getConvertedWritingScore() {
        if (!usesWriting()) {
            return 0d;
        }
        if (isIeltsFormat()) {
            return writingScore == null ? 0d : writingScore;
        }
        return convertToeicSpeakingWriting(writingRaw, 8);
    }

    public Integer getListeningInputValue() {
        return isIeltsFormat() ? null : listeningRaw;
    }

    public Integer getReadingInputValue() {
        return isIeltsFormat() ? null : readingRaw;
    }

    public Integer getSpeakingInputValue() {
        return isIeltsFormat() ? null : speakingRaw;
    }

    public Integer getWritingInputValue() {
        return isIeltsFormat() ? null : writingRaw;
    }

    public String getExamFormat() {
        return examFormat;
    }

    public void setExamFormat(String examFormat) {
        this.examFormat = examFormat;
        syncCalculatedFields();
    }

    public Boolean getHasListening() {
        return hasListening;
    }

    public void setHasListening(Boolean hasListening) {
        this.hasListening = hasListening;
        syncCalculatedFields();
    }

    public Boolean getHasReading() {
        return hasReading;
    }

    public void setHasReading(Boolean hasReading) {
        this.hasReading = hasReading;
        syncCalculatedFields();
    }

    public Boolean getHasSpeaking() {
        return hasSpeaking;
    }

    public void setHasSpeaking(Boolean hasSpeaking) {
        this.hasSpeaking = hasSpeaking;
        syncCalculatedFields();
    }

    public Boolean getHasWriting() {
        return hasWriting;
    }

    public void setHasWriting(Boolean hasWriting) {
        this.hasWriting = hasWriting;
        syncCalculatedFields();
    }

    public Double getListeningScore() {
        return listeningScore;
    }

    public void setListeningScore(Double listeningScore) {
        this.listeningScore = listeningScore;
        syncCalculatedFields();
    }

    public Double getReadingScore() {
        return readingScore;
    }

    public void setReadingScore(Double readingScore) {
        this.readingScore = readingScore;
        syncCalculatedFields();
    }

    public Double getSpeakingScore() {
        return speakingScore;
    }

    public void setSpeakingScore(Double speakingScore) {
        this.speakingScore = speakingScore;
        syncCalculatedFields();
    }

    public Double getWritingScore() {
        return writingScore;
    }

    public void setWritingScore(Double writingScore) {
        this.writingScore = writingScore;
        syncCalculatedFields();
    }

    public Integer getListeningRaw() {
        return listeningRaw;
    }

    public void setListeningRaw(Integer listeningRaw) {
        this.listeningRaw = listeningRaw;
        syncCalculatedFields();
    }

    public Integer getReadingRaw() {
        return readingRaw;
    }

    public void setReadingRaw(Integer readingRaw) {
        this.readingRaw = readingRaw;
        syncCalculatedFields();
    }

    public Integer getSpeakingRaw() {
        return speakingRaw;
    }

    public void setSpeakingRaw(Integer speakingRaw) {
        this.speakingRaw = speakingRaw;
        syncCalculatedFields();
    }

    public Integer getWritingRaw() {
        return writingRaw;
    }

    public void setWritingRaw(Integer writingRaw) {
        this.writingRaw = writingRaw;
        syncCalculatedFields();
    }

    public boolean usesListening() {
        return Boolean.TRUE.equals(hasListening);
    }

    public boolean usesReading() {
        return Boolean.TRUE.equals(hasReading);
    }

    public boolean usesSpeaking() {
        return Boolean.TRUE.equals(hasSpeaking);
    }

    public boolean usesWriting() {
        return Boolean.TRUE.equals(hasWriting);
    }

    public LocalDate getTakenAt() {
        return takenAt;
    }

    public void setTakenAt(LocalDate takenAt) {
        this.takenAt = takenAt;
    }

    public LocalDateTime getSyncedAt() {
        return syncedAt;
    }

    public void setSyncedAt(LocalDateTime syncedAt) {
        this.syncedAt = syncedAt;
    }
}

