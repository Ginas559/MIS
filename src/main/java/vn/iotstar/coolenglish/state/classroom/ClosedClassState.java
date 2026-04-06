package vn.iotstar.coolenglish.state.classroom;

import vn.iotstar.coolenglish.entity.EnglishClass;
import vn.iotstar.coolenglish.entity.Student;

public class ClosedClassState implements ClassState {

    @Override
    public void registerStudent(EnglishClass clazz, Student student) {
        throw new IllegalStateException("Class is CLOSED. Enrollment is blocked.");
    }
}

