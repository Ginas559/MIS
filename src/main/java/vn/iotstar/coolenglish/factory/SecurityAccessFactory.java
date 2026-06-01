package vn.iotstar.coolenglish.factory;

import vn.iotstar.coolenglish.entity.UserAccount;
import vn.iotstar.coolenglish.enums.UserRole;
import vn.iotstar.coolenglish.service.ICourseService;
import vn.iotstar.coolenglish.service.impl.CourseServiceImpl;
import vn.iotstar.coolenglish.service.proxy.CourseServiceProxy;

public final class SecurityAccessFactory {

    private static volatile SecurityAccessFactory instance;

    private SecurityAccessFactory() {
    }

    public static SecurityAccessFactory getInstance() {
        if (instance == null) {
            synchronized (SecurityAccessFactory.class) {
                if (instance == null) {
                    instance = new SecurityAccessFactory();
                }
            }
        }
        return instance;
    }

    public ICourseService getCourseService(UserAccount user) {
        ICourseService realService = new CourseServiceImpl();
        if (user != null && user.getRole() == UserRole.ADMIN) {
            return realService;
        }
        return new CourseServiceProxy(realService, user);
    }
}

