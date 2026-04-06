package vn.iotstar.coolenglish.service.impl;

import java.util.List;

import vn.iotstar.coolenglish.dao.impl.CourseDAO;
import vn.iotstar.coolenglish.entity.Course;
import vn.iotstar.coolenglish.service.ICourseService;

public class CourseServiceImpl implements ICourseService {

    private final CourseDAO dao = new CourseDAO();

    @Override
    public void updateCourseFee(String courseID, Double newFee) {
        Course course = dao.findById(courseID, Course.class);
        if (course == null) {
            throw new IllegalArgumentException("Course not found: " + courseID);
        }
        course.setFee(newFee);
        dao.update(course);
    }

    @Override
    public List<Course> findAll() {
        return dao.findAll(Course.class);
    }
}

