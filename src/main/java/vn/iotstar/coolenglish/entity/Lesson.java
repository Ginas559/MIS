package vn.iotstar.coolenglish.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "Lessons")
@DiscriminatorValue("LESSON")
public class Lesson extends AcademicContentEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "lesson_type", length = 50)
    private String lessonType;

    @Column(name = "resource_url", length = 500)
    private String resourceUrl;

    public Lesson() {
    }

    public Lesson(String title, String description, String lessonType, String resourceUrl) {
        super(title, description);
        this.lessonType = lessonType;
        this.resourceUrl = resourceUrl;
    }

    public String getLessonType() {
        return lessonType;
    }

    public void setLessonType(String lessonType) {
        this.lessonType = lessonType;
    }

    public String getResourceUrl() {
        return resourceUrl;
    }

    public void setResourceUrl(String resourceUrl) {
        this.resourceUrl = resourceUrl;
    }

    @Override
    public String displayContent() {
        return resourceUrl != null && !resourceUrl.isBlank() ? resourceUrl : getDescription();
    }
}
