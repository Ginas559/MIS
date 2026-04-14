package vn.iotstar.coolenglish.service;

import java.time.LocalDateTime;

import vn.iotstar.coolenglish.dao.impl.EnglishClassDAO;
import vn.iotstar.coolenglish.dao.impl.EnrollmentDAO;
import vn.iotstar.coolenglish.dao.impl.StudentDAO;
import vn.iotstar.coolenglish.entity.EnglishClass;
import vn.iotstar.coolenglish.entity.Enrollment;
import vn.iotstar.coolenglish.entity.Student;
import vn.iotstar.coolenglish.enums.EnrollmentStatus;

public class ClassEnrollmentService {

    private final EnglishClassDAO englishClassDAO;
    private final EnrollmentDAO enrollmentDAO;
    private final StudentDAO studentDAO;

    public ClassEnrollmentService() {
        this(new EnglishClassDAO(), new EnrollmentDAO(), new StudentDAO());
    }

    public ClassEnrollmentService(EnglishClassDAO englishClassDAO, EnrollmentDAO enrollmentDAO, StudentDAO studentDAO) {
        this.englishClassDAO = englishClassDAO;
        this.enrollmentDAO = enrollmentDAO;
        this.studentDAO = studentDAO;
    }

    public void registerStudent(String classID, String studentEmail) {
        String normalizedClassID = normalize(classID);
        String normalizedEmail = normalize(studentEmail);

        if (normalizedClassID == null || normalizedEmail == null) {
            throw new IllegalArgumentException("classID and studentEmail are required.");
        }

        EnglishClass clazz = englishClassDAO.findByClassID(normalizedClassID);
        if (clazz == null) {
            throw new IllegalArgumentException("Class not found: " + normalizedClassID);
        }

        Student student = studentDAO.findByEmail(normalizedEmail);
        if (student == null) {
            throw new IllegalArgumentException("Student not found by email: " + normalizedEmail);
        }

        if (enrollmentDAO.existsByClassAndStudent(normalizedClassID, student.getId())) {
            throw new IllegalArgumentException("Student already enrolled in this class.");
        }

        clazz.updateInternalState();

        if (!clazz.checkCapacity()) {
            throw new IllegalStateException("Class is full.");
        }

        clazz.register(student);

        // Persist status transition / enrollment count after state transition logic.
        englishClassDAO.update(clazz);

        Enrollment enrollment = new Enrollment(clazz, student, EnrollmentStatus.ENROLLED, LocalDateTime.now());
        enrollmentDAO.insert(enrollment);
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}

