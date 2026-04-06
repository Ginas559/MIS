package vn.iotstar.coolenglish.dao.impl;

import vn.iotstar.coolenglish.entity.Course;

public class CourseDAO extends AbstractDAO<Course> {

	@Override
	protected void validateEntity(Course entity) {
		if (entity == null) {
			throw new IllegalArgumentException("Course entity is required.");
		}
	}
}

