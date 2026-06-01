package vn.iotstar.coolenglish.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import vn.iotstar.coolenglish.entity.iterator.CourseNavigator;

class CourseNavigatorTest {

    @Test
    void shouldFindFirstLessonInsideNestedModules() {
        Module root = new Module("Root", "Root module");
        Module chapter = new Module("Chapter 1", "Nested chapter");
        Lesson lessonA = new Lesson("Lesson A", "A", "READING", "url-a");
        Lesson lessonB = new Lesson("Lesson B", "B", "VIDEO", "url-b");

        chapter.add(lessonA);
        root.add(chapter);
        root.add(lessonB);

        CourseNavigator navigator = new CourseNavigator(root);

        assertEquals("Lesson A", navigator.first().getTitle());
        assertEquals("Lesson B", navigator.findNextAfter(lessonA).getTitle());
        assertNull(navigator.findNextAfter(lessonB));
    }
}

