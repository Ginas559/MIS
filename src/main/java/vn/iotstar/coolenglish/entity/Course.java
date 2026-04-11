package vn.iotstar.coolenglish.entity;

import java.io.Serializable;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import vn.iotstar.coolenglish.audit.listener.AuditEntityListener;

@Entity
@Table(name = "Courses")
@EntityListeners(AuditEntityListener.class)
public class Course implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "courseID", length = 50)
    private String courseID;
    private String courseName;
    private String description;
    private String level;
    private Integer duration;
    private Double fee;
    private String status;

    public Course() {
    }

    public Course(String courseID, String courseName, String description, String level, Integer duration, Double fee,
            String status) {
        this.courseID = courseID;
        this.courseName = courseName;
        this.description = description;
        this.level = level;
        this.duration = duration;
        this.fee = fee;
        this.status = status;
    }

    public String getCourseID() {
        return courseID;
    }

    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Double getFee() {
        return fee;
    }

    public void setFee(Double fee) {
        this.fee = fee;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

