package vn.iotstar.coolenglish.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import vn.iotstar.coolenglish.enums.StudentStatus;

@Entity
@DiscriminatorValue("STUDENT")
public class Student extends Person {

    @Column(name = "student_id", length = 50)
    private String studentID;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private StudentStatus status;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "registration_date")
    private LocalDate registrationDate;

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public StudentStatus getStatus() {
        return status;
    }

    public void setStatus(StudentStatus status) {
        this.status = status;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public void registerCourse(String classID) {
        if (classID == null || classID.isBlank()) {
            throw new IllegalArgumentException("classID is required.");
        }
    }

    public String viewResult() {
        return "Result is not available yet.";
    }

    @Override
    public void showSpecificInfo() {
        System.out.println("Role: Student - ID: " + studentID);
    }
}

