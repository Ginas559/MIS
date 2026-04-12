package vn.iotstar.coolenglish.service.impl;

import vn.iotstar.coolenglish.dao.impl.EnglishClassDAO;
import vn.iotstar.coolenglish.dao.impl.TeacherDAO;
import vn.iotstar.coolenglish.entity.EnglishClass;
import vn.iotstar.coolenglish.entity.Teacher;
import vn.iotstar.coolenglish.service.IClassService;

public class ClassServiceImpl implements IClassService {

    private final EnglishClassDAO classDAO = new EnglishClassDAO();
    private final TeacherDAO teacherDAO = new TeacherDAO();

    @Override
    public void assignTeacher(String classID, Long teacherID) {
        if (classID == null || classID.isBlank()) {
            throw new IllegalArgumentException("Class ID is required.");
        }

        EnglishClass clazz = classDAO.findByClassID(classID);
        if (clazz == null) {
            throw new IllegalArgumentException("Class not found: " + classID);
        }

        Teacher teacher = null;
        if (teacherID != null) {
            teacher = teacherDAO.findById(teacherID, Teacher.class);
            if (teacher == null) {
                throw new IllegalArgumentException("Teacher not found: " + teacherID);
            }
        }

        clazz.assignTeacher(teacher);
        classDAO.update(clazz);
    }
}

