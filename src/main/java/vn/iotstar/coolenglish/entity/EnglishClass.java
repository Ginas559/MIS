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

    @OneToOne(mappedBy = "englishClass", fetch = FetchType.LAZY)
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", referencedColumnName = "id", nullable = false)
    private Teacher teacher;

    public EnglishClass() {
        this.status = ClassStatus.PLANNED;
        this.currentEnrollment = 0;
        this.enrollments = new ArrayList<>();
        updateInternalState();
    }

    /**
     * Constructor bắt buộc: EnglishClass không thể tồn tại nếu không có giáo viên.
     * Đảm bảo dữ liệu nhất quán ngay từ bộ nhớ trước khi chạm xuống Database.
     */
    public EnglishClass(String classID, String className, Teacher teacher) {
        this(classID, className, teacher, null, null, 1);
    }

    public EnglishClass(String classID, String className, Teacher teacher, LocalDate startDate, Integer maxStudent) {
        this(classID, className, teacher, startDate, null, maxStudent);
    }

    public EnglishClass(String classID, String className, Teacher teacher,
            LocalDate startDate, LocalDate endDate, Integer maxStudent) {
        if (classID == null || classID.isBlank()) {
            throw new IllegalArgumentException("Class ID is required.");
        }
        if (className == null || className.isBlank()) {
            throw new IllegalArgumentException("Class name is required.");
        }
        if (teacher == null) {
            throw new IllegalArgumentException("Lớp học bắt buộc phải có giáo viên!");
        }
        if (startDate == null) {
            throw new IllegalArgumentException("Ngày bắt đầu là bắt buộc.");
        }
        if (endDate == null) {
            throw new IllegalArgumentException("Ngày kết thúc là bắt buộc.");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("Ngày kết thúc phải sau hoặc bằng ngày bắt đầu.");
        }
        if (maxStudent == null || maxStudent <= 0) {
            throw new IllegalArgumentException("Sức chứa tối đa phải lớn hơn 0.");
        }
        this.classID = classID;
        this.className = className;
        this.teacher = teacher;
        this.startDate = startDate;
        this.endDate = endDate;
        this.maxStudent = maxStudent;
        this.status = ClassStatus.PLANNED;
        this.currentEnrollment = 0;
        this.enrollments = new ArrayList<>();
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
        if (enrollments == null) {
            enrollments = new ArrayList<>();
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


    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule newSchedule) {
        if (newSchedule == null) {
            if (this.schedule != null) {
                this.schedule.setEnglishClass(null);
            }
            this.schedule = null;
            return;
        }
        if (this.schedule != null && this.schedule != newSchedule) {
            this.schedule.setEnglishClass(null);
        }
        newSchedule.setEnglishClass(this);
        this.schedule = newSchedule;
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
        this.enrollments = enrollments == null ? new ArrayList<>() : enrollments;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public String getTeacherName() {
        return teacher == null ? null : teacher.getFullName();
    }

    /**
     * Backward-compatible accessor for legacy JSP/controller code.
     * Uses teacher relation as single source of truth.
     */
    @Deprecated
    public Long getTeacherID() {
        return teacher == null ? null : teacher.getId();
    }

    public void setTeacher(Teacher teacher) {
        assignTeacher(teacher);
    }

    /**
     * Teacher Replacement: Cập nhật tham chiếu teacher sang một đối tượng Teacher mới.
     * Nhờ State Pattern, trạng thái vận hành của lớp (RUNNING, OPEN, etc.) vẫn được giữ nguyên.
     * Các bản ghi liên quan như Enrollment hay Schedule vẫn trỏ về EnglishClass.
     */
    public void assignTeacher(Teacher newTeacher) {
        if (newTeacher == null) {
            throw new IllegalArgumentException("Lớp học bắt buộc phải có giáo viên!");
        }
        this.teacher = newTeacher;
    }
}
