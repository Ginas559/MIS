package vn.iotstar.coolenglish.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ExamResults")
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

    @Column(name = "score", nullable = false)
    private Double score;

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

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
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

