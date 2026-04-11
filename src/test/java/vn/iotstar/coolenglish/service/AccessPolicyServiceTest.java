package vn.iotstar.coolenglish.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import vn.iotstar.coolenglish.enums.UserRole;

class AccessPolicyServiceTest {

    private final AccessPolicyService accessPolicyService = new AccessPolicyService();

    @Test
    void shouldGrantAccessForAdminWithoutGrant() {
        boolean canAccess = accessPolicyService.canAccessRoadmap(UserRole.ADMIN, "TOEIC_RL", 100L,
                (roadmapCode, personId) -> false);

        assertTrue(canAccess);
    }

    @Test
    void shouldRequireGrantForStudent() {
        boolean canAccess = accessPolicyService.canAccessRoadmap(UserRole.STUDENT, "TOEIC_RL", 100L,
                (roadmapCode, personId) -> false);

        assertFalse(canAccess);
    }

    @Test
    void shouldAllowTeacherWhenGrantExists() {
        boolean canAccess = accessPolicyService.canAccessRoadmap(UserRole.TEACHER, "IELTS", 100L,
                (roadmapCode, personId) -> true);

        assertTrue(canAccess);
    }
}

