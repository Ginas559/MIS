package vn.iotstar.coolenglish.service;

import java.util.List;

import vn.iotstar.coolenglish.entity.Course;

public interface ICourseService {

    void updateCourseFee(String courseID, Double newFee);

    List<Course> findAll();
}

