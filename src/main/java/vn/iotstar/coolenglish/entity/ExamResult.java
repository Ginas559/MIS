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

    @Column(name = "grade", length = 30)
    private String grade;

    @Column(name = "score")
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
        grade = calculateGrade();
    }

    public String calculateGrade() {
        if (score == null || score < 0d) {
            return null;
        }
        if (score >= 8.0d) {
            return "Gioi";
        }
        if (score >= 6.5d) {
            return "Kha";
        }
        if (score >= 5.0d) {
            return "Trung binh";
        }
        return "Yeu";
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
        return score != null && score >= 0d;
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

