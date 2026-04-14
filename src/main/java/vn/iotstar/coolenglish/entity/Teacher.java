package vn.iotstar.coolenglish.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import vn.iotstar.coolenglish.enums.TeacherStatus;

@Entity
@DiscriminatorValue("TEACHER")
public class Teacher extends Person {

    @Column(name = "teacher_id", length = 50)
    private String teacherID;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TeacherStatus status;

    @Column(name = "specialty", length = 100)
    private String specialty;

    @Column(name = "hire_date")
    private LocalDate hireDate;

    @Column(name = "certificate", length = 255)
    private String certificate;

    public String getTeacherID() {
        return teacherID;
    }

    public void setTeacherID(String teacherID) {
        this.teacherID = teacherID;
    }

    public TeacherStatus getStatus() {
        return status;
    }

    public void setStatus(TeacherStatus status) {
        this.status = status;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public void assignClass(EnglishClass englishClass) {
        if (englishClass != null) {
            englishClass.assignTeacher(this);
        }
    }

    @Override
    public void showSpecificInfo() {
        System.out.println("Role: Teacher - ID: " + teacherID);
    }
}

