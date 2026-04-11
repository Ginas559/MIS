package vn.iotstar.coolenglish.entity.decorator;

import vn.iotstar.coolenglish.entity.AcademicContent;

public class WatermarkDecorator extends BaseContentDecorator {

    private final String learnerEmail;

    public WatermarkDecorator(AcademicContent wrappedContent, String learnerEmail) {
        super(wrappedContent);
        this.learnerEmail = learnerEmail == null || learnerEmail.isBlank() ? "anonymous@coolenglish.local" : learnerEmail;
    }

    @Override
    public String displayContent() {
        String baseContent = wrappedContent.displayContent();
        return baseContent + "\n\n[Watermark] Hoc vien: " + learnerEmail;
    }
}

