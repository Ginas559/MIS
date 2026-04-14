package vn.iotstar.coolenglish.factory;

import java.time.LocalDate;

import vn.iotstar.coolenglish.entity.Person;
import vn.iotstar.coolenglish.entity.Student;
import vn.iotstar.coolenglish.enums.StudentStatus;

public class StudentRegistration extends UserRegistration {

    @Override
    protected Person createPerson() {
        return new Student();
    }

    @Override
    protected void initializeSpecificInfo(Person person) {
        Student student = (Student) person;
        student.setStudentID("STU-" + System.currentTimeMillis());
        student.setStatus(StudentStatus.ACTIVE);
        student.setRegistrationDate(LocalDate.now());
    }
}

