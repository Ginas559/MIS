package vn.iotstar.coolenglish.dao.impl;

import vn.iotstar.coolenglish.entity.Teacher;

public class TeacherDAO extends AbstractDAO<Teacher> {

    @Override
    protected void validateEntity(Teacher entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Teacher is required.");
        }
    }

    public Teacher findByTeacherID(String teacherID) {
        return findById(teacherID, Teacher.class);
    }
}
