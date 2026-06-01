package vn.iotstar.coolenglish.state.classroom;

import vn.iotstar.coolenglish.enums.ClassStatus;

public final class ClassStateFactory {

    private ClassStateFactory() {
    }

    public static ClassState fromStatus(ClassStatus status) {
        if (status == null || status == ClassStatus.OPEN) {
            return new OpenClassState();
        }

        if (status == ClassStatus.CLOSED) {
            return new ClosedClassState();
        }

        if (status == ClassStatus.RUNNING) {
            return new BlockedClassState("Class is RUNNING. New enrollment is not allowed.");
        }

        if (status == ClassStatus.CANCELLED) {
            return new BlockedClassState("Class is CANCELLED. Enrollment is not allowed.");
        }

        return new BlockedClassState("Class is not OPEN. Enrollment is not allowed.");
    }
}

