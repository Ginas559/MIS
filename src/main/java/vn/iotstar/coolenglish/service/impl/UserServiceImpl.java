package vn.iotstar.coolenglish.service.impl;

import java.util.List;

import vn.iotstar.coolenglish.dao.impl.UserAccountDAO;
import vn.iotstar.coolenglish.entity.UserAccount;
import vn.iotstar.coolenglish.service.IUserService;

public class UserServiceImpl implements IUserService {

    private final UserAccountDAO userAccountDAO = new UserAccountDAO();

    @Override
    public List<UserAccount> getAllUsers() {
        return userAccountDAO.findAllWithProfiles();
    }

    @Override
    public UserAccount getUserById(Long userID) {
        return userAccountDAO.findByUserID(userID);
    }

    @Override
    public void toggleUserActive(Long userID) {
        UserAccount user = userAccountDAO.findByUserID(userID);
        if (user == null) {
            throw new IllegalArgumentException("User not found: " + userID);
        }

        user.setActive(!user.isActive());
        userAccountDAO.update(user);
    }
}


