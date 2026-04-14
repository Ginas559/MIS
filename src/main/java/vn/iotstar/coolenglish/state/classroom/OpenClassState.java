package vn.iotstar.coolenglish.state.classroom;

import vn.iotstar.coolenglish.entity.EnglishClass;
import vn.iotstar.coolenglish.entity.Student;
import vn.iotstar.coolenglish.enums.ClassStatus;

public class OpenClassState implements ClassState {

    @Override
    public void registerStudent(EnglishClass clazz, Student student) {
        if (student == null) {
            throw new IllegalArgumentException("Student is required for enrollment.");
        }

        if (!clazz.checkCapacity()) {
            clazz.setStatus(ClassStatus.RUNNING);
            throw new IllegalStateException("Class reached max capacity and moved to RUNNING.");
        }

        clazz.increaseEnrollmentCount();

        Integer maxCapacity = clazz.getMaxCapacity();
        int updated = clazz.getCurrentEnrollment() == null ? 0 : clazz.getCurrentEnrollment();
        if (maxCapacity != null && maxCapacity > 0 && updated >= maxCapacity) {
            clazz.setStatus(ClassStatus.RUNNING);
        }
    }
}

