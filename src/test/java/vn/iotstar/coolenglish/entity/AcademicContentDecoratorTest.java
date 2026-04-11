package vn.iotstar.coolenglish.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import vn.iotstar.coolenglish.entity.decorator.PremiumContentDecorator;
import vn.iotstar.coolenglish.entity.decorator.WatermarkDecorator;
import vn.iotstar.coolenglish.enums.EnrollmentStatus;

class AcademicContentDecoratorTest {

    @Test
    void premiumDecoratorShouldLockWhenStudentHasNotPaid() {
        Lesson lesson = new Lesson("Lesson 1", "Mo ta", "READING", "https://content.local/1");

        PremiumContentDecorator decorator = new PremiumContentDecorator(lesson, EnrollmentStatus.ENROLLED);

        assertTrue(decorator.displayContent().contains("Premium"));
    }

    @Test
    void watermarkDecoratorShouldAppendLearnerEmail() {
        Lesson lesson = new Lesson("Lesson 2", "Noi dung", "VIDEO", "https://content.local/2");
        PremiumContentDecorator premium = new PremiumContentDecorator(lesson, EnrollmentStatus.STUDYING);

        WatermarkDecorator decorator = new WatermarkDecorator(premium, "student@example.com");

        assertEquals("https://content.local/2\n\n[Watermark] Hoc vien: student@example.com", decorator.displayContent());
    }
}

