package vn.iotstar.coolenglish.service.proxy;

import java.util.List;

import vn.iotstar.coolenglish.entity.UserAccount;
import vn.iotstar.coolenglish.enums.UserRole;
import vn.iotstar.coolenglish.service.IUserService;

public class UserServiceProxy implements IUserService {

    private final IUserService realService;
    private final UserAccount currentUser;

    public UserServiceProxy(IUserService realService, UserAccount currentUser) {
        this.realService = realService;
        this.currentUser = currentUser;
    }

    @Override
    public List<UserAccount> getAllUsers() {
        if (currentUser != null && currentUser.getRole() == UserRole.ADMIN) {
            return realService.getAllUsers();
        }
        throw new SecurityException("Ban khong co quyen xem danh sach nguoi dung.");
    }

    @Override
    public UserAccount getUserById(Long userID) {
        if (currentUser != null && currentUser.getRole() == UserRole.ADMIN) {
            return realService.getUserById(userID);
        }
        throw new SecurityException("Ban khong co quyen xem chi tiet nguoi dung.");
    }

    @Override
    public void toggleUserActive(Long userID) {
        if (currentUser != null && currentUser.getRole() == UserRole.ADMIN) {
            realService.toggleUserActive(userID);
            return;
        }
        throw new SecurityException("Ban khong co quyen thay doi trang thai tai khoan.");
    }
}


