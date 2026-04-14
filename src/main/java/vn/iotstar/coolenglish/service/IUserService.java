package vn.iotstar.coolenglish.service;

import java.util.List;

import vn.iotstar.coolenglish.entity.UserAccount;

public interface IUserService {

    List<UserAccount> getAllUsers();

    UserAccount getUserById(Long userID);

    void toggleUserActive(Long userID);
}


