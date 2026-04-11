package vn.iotstar.coolenglish.invoice;

import java.io.Serializable;
import java.time.LocalDate;

/** Khối thông tin lớp học & khóa (thành phần phức tạp trên hóa đơn). */
public final class ClassEnrollmentSection implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String classId;
    private final String className;
    private final String courseId;
    private final String courseName;
    private final String courseLevel;
    private final Integer courseDurationWeeks;
    private final LocalDate classStartDate;
    private final LocalDate classEndDate;
    private final String roomLabel;
    private final String scheduleSummary;

    public ClassEnrollmentSection(String classId, String className, String courseId, String courseName,
            String courseLevel, Integer courseDurationWeeks, LocalDate classStartDate, LocalDate classEndDate,
            String roomLabel, String scheduleSummary) {
        this.classId = classId;
        this.className = className;
        this.courseId = courseId;
        this.courseName = courseName;
        this.courseLevel = courseLevel;
        this.courseDurationWeeks = courseDurationWeeks;
        this.classStartDate = classStartDate;
        this.classEndDate = classEndDate;
        this.roomLabel = roomLabel;
        this.scheduleSummary = scheduleSummary;
    }

    public String getClassId() {
        return classId;
    }

    public String getClassName() {
        return className;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getCourseLevel() {
        return courseLevel;
    }

    public Integer getCourseDurationWeeks() {
        return courseDurationWeeks;
    }

    public LocalDate getClassStartDate() {
        return classStartDate;
    }

    public LocalDate getClassEndDate() {
        return classEndDate;
    }

    public String getRoomLabel() {
        return roomLabel;
    }

    public String getScheduleSummary() {
        return scheduleSummary;
    }
}
