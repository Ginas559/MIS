package vn.iotstar.coolenglish.factory;

import vn.iotstar.coolenglish.entity.UserAccount;
import vn.iotstar.coolenglish.enums.UserRole;
import vn.iotstar.coolenglish.service.IClassService;
import vn.iotstar.coolenglish.service.ICourseService;
import vn.iotstar.coolenglish.service.IUserService;
import vn.iotstar.coolenglish.service.impl.ClassServiceImpl;
import vn.iotstar.coolenglish.service.impl.CourseServiceImpl;
import vn.iotstar.coolenglish.service.impl.UserServiceImpl;
import vn.iotstar.coolenglish.service.proxy.ClassServiceProxy;
import vn.iotstar.coolenglish.service.proxy.CourseServiceProxy;
import vn.iotstar.coolenglish.service.proxy.UserServiceProxy;

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

    public IClassService getClassService(UserAccount user) {
        IClassService realService = new ClassServiceImpl();
        return new ClassServiceProxy(realService, user);
    }

    public IUserService getUserService(UserAccount currentUser) {
        IUserService realService = new UserServiceImpl();
        if (currentUser != null && currentUser.getRole() == UserRole.ADMIN) {
            return realService;
        }
        return new UserServiceProxy(realService, currentUser);
    }
}
