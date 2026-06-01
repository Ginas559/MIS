package vn.iotstar.coolenglish.state.classroom;

import vn.iotstar.coolenglish.entity.EnglishClass;
import vn.iotstar.coolenglish.entity.Student;

public class BlockedClassState implements ClassState {

    private final String reason;

    public BlockedClassState(String reason) {
        this.reason = reason;
    }

    @Override
    public void registerStudent(EnglishClass clazz, Student student) {
        throw new IllegalStateException(reason);
    }
}

