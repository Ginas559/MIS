package vn.iotstar.coolenglish.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import vn.iotstar.coolenglish.enums.EnrollmentStatus;

@Entity
@Table(name = "Enrollments")
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

    @Column(name = "enrolledAt", nullable = false)
    private LocalDateTime enrolledAt;

    public Enrollment() {
        this.status = EnrollmentStatus.ENROLLED;
        this.enrolledAt = LocalDateTime.now();
    }

    public Enrollment(EnglishClass englishClass, Student student, EnrollmentStatus status, LocalDateTime enrolledAt) {
        this.englishClass = englishClass;
        this.student = student;
        this.status = status;
        this.enrolledAt = enrolledAt;
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
    }
}

