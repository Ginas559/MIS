package vn.iotstar.coolenglish.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import vn.iotstar.coolenglish.audit.listener.AuditEntityListener;
import vn.iotstar.coolenglish.enums.ClassStatus;
import vn.iotstar.coolenglish.state.classroom.ClassState;
import vn.iotstar.coolenglish.state.classroom.ClassStateFactory;

@Entity
@Table(name = "Classes")
@EntityListeners(AuditEntityListener.class)
public class EnglishClass implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "classID", length = 50)
    private String classID;

    @Column(name = "className", length = 150, nullable = false)
    private String className;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "courseID", length = 50)
    private String courseID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "courseID", referencedColumnName = "courseID", insertable = false, updatable = false)
    private Course course;

    @Column(name = "roomID", length = 50)
    private String roomID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roomID", referencedColumnName = "roomID", insertable = false, updatable = false)
    private Room room;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scheduleID", referencedColumnName = "scheduleID")
    private Schedule schedule;

    @Column(name = "maxCapacity")
    private Integer maxStudent;

    @Column(name = "currentEnrollment")
    private Integer currentEnrollment;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private ClassStatus status;

    @Transient
    private ClassState state;

    @OneToMany(mappedBy = "englishClass")
    private List<Enrollment> enrollments = new ArrayList<>();

    public EnglishClass() {
        this.status = ClassStatus.OPEN;
        this.currentEnrollment = 0;
        updateInternalState();
    }

    @PostLoad
    @PrePersist
    @PreUpdate
    public void updateInternalState() {
        if (status == null) {
            status = ClassStatus.OPEN;
        }
        if (currentEnrollment == null) {
            currentEnrollment = 0;
        }
        state = ClassStateFactory.fromStatus(status);
    }

    public void register(Student student) {
        if (state == null) {
            updateInternalState();
        }
        state.registerStudent(this, student);
        updateInternalState();
    }

    public Boolean checkCapacity() {
        int current = currentEnrollment == null ? 0 : currentEnrollment;
        int max = maxStudent == null ? Integer.MAX_VALUE : maxStudent;
        return current < max;
    }

    public void increaseEnrollmentCount() {
        int value = currentEnrollment == null ? 0 : currentEnrollment;
        currentEnrollment = value + 1;
    }

    public String getClassID() {
        return classID;
    }

    public void setClassID(String classID) {
        this.classID = classID;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getCourseID() {
        return courseID;
    }

    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
        this.courseID = course != null ? course.getCourseID() : null;
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
        this.roomID = room != null ? room.getRoomID() : null;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public Integer getMaxStudent() {
        return maxStudent;
    }

    public void setMaxStudent(Integer maxStudent) {
        this.maxStudent = maxStudent;
    }

    // Backward-compatible aliases for existing service/controller code.
    public Integer getMaxCapacity() {
        return maxStudent;
    }

    public void setMaxCapacity(Integer maxCapacity) {
        this.maxStudent = maxCapacity;
    }

    public Integer getCurrentEnrollment() {
        return currentEnrollment;
    }

    public void setCurrentEnrollment(Integer currentEnrollment) {
        this.currentEnrollment = currentEnrollment;
    }

    public ClassStatus getStatus() {
        return status;
    }

    public void setStatus(ClassStatus status) {
        this.status = status;
    }

    public List<Enrollment> getEnrollments() {
        return enrollments;
    }

    public void setEnrollments(List<Enrollment> enrollments) {
        this.enrollments = enrollments;
    }
}

