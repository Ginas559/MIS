package vn.iotstar.coolenglish.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import vn.iotstar.coolenglish.audit.listener.AuditEntityListener;
import vn.iotstar.coolenglish.enums.EnrollmentStatus;

@Entity
@Table(name = "Enrollments")
@EntityListeners(AuditEntityListener.class)
public class Enrollment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", referencedColumnName = "classID", nullable = false)
    private EnglishClass englishClass;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_person_id", referencedColumnName = "id", nullable = false)
    private Student student;

    @OneToOne(mappedBy = "enrollment", fetch = FetchType.LAZY)
    private Invoice invoice;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private EnrollmentStatus status;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "enrollmentDate")
    private Date enrollmentDate;

    @Column(name = "enrolledAt", nullable = false)
    private LocalDateTime enrolledAt;

    @Column(name = "note", length = 500)
    private String note;

    public Enrollment() {
        this.status = EnrollmentStatus.ENROLLED;
        this.enrolledAt = LocalDateTime.now();
        this.enrollmentDate = new Date();
    }

    public Enrollment(EnglishClass englishClass, Student student, EnrollmentStatus status, LocalDateTime enrolledAt) {
        this.englishClass = englishClass;
        this.student = student;
        this.status = status;
        this.enrolledAt = enrolledAt;
        this.enrollmentDate = enrolledAt == null ? null : java.sql.Timestamp.valueOf(enrolledAt);
    }

    @PrePersist
    @PreUpdate
    public void syncEnrollmentFields() {
        if (enrollmentDate == null && enrolledAt != null) {
            enrollmentDate = java.sql.Timestamp.valueOf(enrolledAt);
        }
        if (enrolledAt == null && enrollmentDate != null) {
            enrolledAt = LocalDateTime.ofInstant(enrollmentDate.toInstant(), java.time.ZoneId.systemDefault());
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EnglishClass getEnglishClass() {
        return englishClass;
    }

    public void setEnglishClass(EnglishClass englishClass) {
        this.englishClass = englishClass;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public EnrollmentStatus getStatus() {
        return status;
    }

    public void setStatus(EnrollmentStatus status) {
        this.status = status;
    }

    public LocalDateTime getEnrolledAt() {
        return enrolledAt;
    }

    public void setEnrolledAt(LocalDateTime enrolledAt) {
        this.enrolledAt = enrolledAt;
        this.enrollmentDate = enrolledAt == null ? null : java.sql.Timestamp.valueOf(enrolledAt);
    }

    public Date getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(Date enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
        if (enrollmentDate == null) {
            this.enrolledAt = null;
            return;
        }
        this.enrolledAt = LocalDateTime.ofInstant(enrollmentDate.toInstant(), java.time.ZoneId.systemDefault());
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}

