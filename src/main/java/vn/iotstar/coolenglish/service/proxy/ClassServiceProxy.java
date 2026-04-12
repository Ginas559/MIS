package vn.iotstar.coolenglish.service.proxy;

import vn.iotstar.coolenglish.entity.UserAccount;
import vn.iotstar.coolenglish.enums.UserRole;
import vn.iotstar.coolenglish.service.IClassService;

public class ClassServiceProxy implements IClassService {

    private final IClassService realService;
    private final UserAccount currentUser;

    public ClassServiceProxy(IClassService realService, UserAccount currentUser) {
        this.realService = realService;
        this.currentUser = currentUser;
    }

    @Override
    public void assignTeacher(String classID, Long teacherID) {
        if (currentUser != null && (currentUser.getRole() == UserRole.ADMIN || currentUser.getRole() == UserRole.STAFF)) {
            realService.assignTeacher(classID, teacherID);
            return;
        }
        throw new SecurityException("Ban khong co quyen gan giao vien cho lop hoc.");
    }
}

