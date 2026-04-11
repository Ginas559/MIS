package vn.iotstar.coolenglish.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class AcademicContentCompositeTest {

    @Test
    void moduleShouldManageChildrenAndSetParent() {
        Module module = new Module("Module 1", "Intro module");
        Lesson lesson = new Lesson("Lesson 1", "Read and practice", "READING", "https://example.com/lesson-1");

        module.add(lesson);

        assertEquals(1, module.getChildren().size());
        assertSame(module, lesson.getParentModule());

        module.remove(lesson);

        assertEquals(0, module.getChildren().size());
        assertNull(lesson.getParentModule());
    }

    @Test
    void lessonShouldRejectChildOperations() {
        Lesson leaf = new Lesson();
        Module childModule = new Module("Child", "Nested module");

        assertThrows(UnsupportedOperationException.class, () -> leaf.add(childModule));
        assertThrows(UnsupportedOperationException.class, () -> leaf.remove(childModule));
    }
}

