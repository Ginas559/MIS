package vn.iotstar.coolenglish.state;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import vn.iotstar.coolenglish.entity.EnglishClass;
import vn.iotstar.coolenglish.entity.Student;
import vn.iotstar.coolenglish.enums.ClassStatus;

class ClassStateTest {

    @Test
    void openClassShouldAllowEnrollmentAndTransitionToRunningWhenFull() {
        EnglishClass clazz = new EnglishClass();
        clazz.setClassID("CLS_TEST_01");
        clazz.setClassName("Test Open Class");
        clazz.setStatus(ClassStatus.OPEN);
        clazz.setMaxCapacity(1);
        clazz.setCurrentEnrollment(0);
        clazz.updateInternalState();

        Student student = new Student();
        student.setFullName("Student One");

        clazz.register(student);

        assertEquals(1, clazz.getCurrentEnrollment());
        assertEquals(ClassStatus.RUNNING, clazz.getStatus());
    }

    @Test
    void closedClassShouldBlockEnrollment() {
        EnglishClass clazz = new EnglishClass();
        clazz.setClassID("CLS_TEST_02");
        clazz.setClassName("Test Closed Class");
        clazz.setStatus(ClassStatus.CLOSED);
        clazz.setMaxCapacity(20);
        clazz.setCurrentEnrollment(5);
        clazz.updateInternalState();

        Student student = new Student();
        student.setFullName("Student Two");

        assertThrows(IllegalStateException.class, () -> clazz.register(student));
    }

    @Test
    void runningClassShouldBlockEnrollment() {
        EnglishClass clazz = new EnglishClass();
        clazz.setClassID("CLS_TEST_03");
        clazz.setClassName("Test Running Class");
        clazz.setStatus(ClassStatus.RUNNING);
        clazz.setMaxCapacity(20);
        clazz.setCurrentEnrollment(20);
        clazz.updateInternalState();

        Student student = new Student();
        student.setFullName("Student Three");

        assertThrows(IllegalStateException.class, () -> clazz.register(student));
    }
}

