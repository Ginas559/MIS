package vn.iotstar.coolenglish.entity;

import java.util.List;

public interface AcademicContent {

    Long getId();

    String getTitle();

    void setTitle(String title);

    String getDescription();

    void setDescription(String description);

    default String displayContent() {
        return getDescription();
    }

    void add(AcademicContent content);

    void remove(AcademicContent content);

    List<AcademicContent> getChildren();
}
