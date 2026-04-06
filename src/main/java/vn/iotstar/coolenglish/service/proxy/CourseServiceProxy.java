package vn.iotstar.coolenglish.service.proxy;

import java.util.List;

import vn.iotstar.coolenglish.entity.Course;
import vn.iotstar.coolenglish.entity.UserAccount;
import vn.iotstar.coolenglish.enums.UserRole;
import vn.iotstar.coolenglish.service.ICourseService;

public class CourseServiceProxy implements ICourseService {

    private final ICourseService realService;
    private final UserAccount currentUser;

    public CourseServiceProxy(ICourseService realService, UserAccount currentUser) {
        this.realService = realService;
        this.currentUser = currentUser;
    }

    @Override
    public void updateCourseFee(String courseID, Double newFee) {
        if (currentUser != null && currentUser.getRole() == UserRole.ADMIN) {
            realService.updateCourseFee(courseID, newFee);
            return;
        }
        throw new SecurityException("Bạn không có quyền thực hiện hành động này!");
    }

    @Override
    public List<Course> findAll() {
        return realService.findAll();
    }
}

