package vn.iotstar.coolenglish.entity.decorator;

import java.util.List;

import vn.iotstar.coolenglish.entity.AcademicContent;

public abstract class BaseContentDecorator implements AcademicContent {

    protected final AcademicContent wrappedContent;

    protected BaseContentDecorator(AcademicContent wrappedContent) {
        if (wrappedContent == null) {
            throw new IllegalArgumentException("wrappedContent is required.");
        }
        this.wrappedContent = wrappedContent;
    }

    @Override
    public Long getId() {
        return wrappedContent.getId();
    }

    @Override
    public String getTitle() {
        return wrappedContent.getTitle();
    }

    @Override
    public void setTitle(String title) {
        wrappedContent.setTitle(title);
    }

    @Override
    public String getDescription() {
        return wrappedContent.getDescription();
    }

    @Override
    public void setDescription(String description) {
        wrappedContent.setDescription(description);
    }

    @Override
    public String displayContent() {
        return wrappedContent.displayContent();
    }

    @Override
    public void add(AcademicContent content) {
        wrappedContent.add(content);
    }

    @Override
    public void remove(AcademicContent content) {
        wrappedContent.remove(content);
    }

    @Override
    public List<AcademicContent> getChildren() {
        return wrappedContent.getChildren();
    }
}

