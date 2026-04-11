package vn.iotstar.coolenglish.service;

import java.util.function.BiPredicate;

import vn.iotstar.coolenglish.enums.UserRole;

public class AccessPolicyService {

    public boolean canAccessRoadmap(UserRole role, String roadmapCode, Long userId,
            BiPredicate<String, Long> grantChecker) {
        if (isPrivilegedRole(role)) {
            return true;
        }

        if (role != UserRole.STUDENT && role != UserRole.TEACHER) {
            return false;
        }

        return grantChecker.test(roadmapCode, userId);
    }

    private boolean isPrivilegedRole(UserRole role) {
        return role == UserRole.ADMIN || role == UserRole.STAFF;
    }
}
