package vn.iotstar.coolenglish.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.junit.jupiter.api.Test;

import vn.iotstar.coolenglish.entity.Staff;
import vn.iotstar.coolenglish.entity.Student;
import vn.iotstar.coolenglish.entity.Teacher;
import vn.iotstar.coolenglish.enums.StudentStatus;
import vn.iotstar.coolenglish.enums.TeacherStatus;

class UserRegistrationTest {

    @Test
    void studentRegistrationShouldCreateStudent() {
        Student student = (Student) new StudentRegistration().registerUser("Alice", "alice@example.com");
        assertInstanceOf(Student.class, student);
        assertEquals("Alice", student.getFullName());
        assertEquals("alice@example.com", student.getEmail());
        assertEquals(StudentStatus.ACTIVE, student.getStatus());
    }

    @Test
    void teacherRegistrationShouldCreateTeacher() {
        Teacher teacher = (Teacher) new TeacherRegistration().registerUser("Bob", "bob@example.com");
        assertInstanceOf(Teacher.class, teacher);
        assertEquals("Bob", teacher.getFullName());
        assertEquals("bob@example.com", teacher.getEmail());
        assertEquals(TeacherStatus.ACTIVE, teacher.getStatus());
    }

    @Test
    void staffRegistrationShouldCreateStaff() {
        Staff staff = (Staff) new StaffRegistration().registerUser("Carol", "carol@example.com");
        assertInstanceOf(Staff.class, staff);
        assertEquals("Carol", staff.getFullName());
        assertEquals("carol@example.com", staff.getEmail());
    }
}

