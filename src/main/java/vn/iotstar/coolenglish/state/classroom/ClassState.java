package vn.iotstar.coolenglish.state.classroom;

import vn.iotstar.coolenglish.entity.EnglishClass;
import vn.iotstar.coolenglish.entity.Student;

public interface ClassState {

    void registerStudent(EnglishClass clazz, Student student);
}

