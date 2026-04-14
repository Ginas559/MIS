package vn.iotstar.coolenglish.factory;

import java.time.LocalDate;

import vn.iotstar.coolenglish.entity.Person;
import vn.iotstar.coolenglish.entity.Teacher;
import vn.iotstar.coolenglish.enums.TeacherStatus;

public class TeacherRegistration extends UserRegistration {

    @Override
    protected Person createPerson() {
        return new Teacher();
    }

    @Override
    protected void initializeSpecificInfo(Person person) {
        Teacher teacher = (Teacher) person;
        teacher.setTeacherID("TCH-" + System.currentTimeMillis());
        teacher.setStatus(TeacherStatus.ACTIVE);
        teacher.setHireDate(LocalDate.now());
    }
}

