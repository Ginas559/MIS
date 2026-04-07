package vn.iotstar.coolenglish.integration.proxy;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import vn.iotstar.coolenglish.entity.UserAccount;
import vn.iotstar.coolenglish.enums.UserRole;
import vn.iotstar.coolenglish.integration.IPartnerIntegration;

class IntegrationSecurityProxyTest {

    @Test
    void shouldAllowAdminAndStaff() {
        IPartnerIntegration target = partnerCode -> {
        };

        IntegrationSecurityProxy adminProxy = new IntegrationSecurityProxy(target,
                new UserAccount("admin@test.com", "admin", "x", UserRole.ADMIN));
        IntegrationSecurityProxy staffProxy = new IntegrationSecurityProxy(target,
                new UserAccount("staff@test.com", "staff", "x", UserRole.STAFF));

        assertDoesNotThrow(() -> adminProxy.syncExamResults("IDP"));
        assertDoesNotThrow(() -> staffProxy.syncExamResults("IDP"));
    }

    @Test
    void shouldBlockNonPrivilegedRoles() {
        IPartnerIntegration target = partnerCode -> {
        };

        IntegrationSecurityProxy studentProxy = new IntegrationSecurityProxy(target,
                new UserAccount("student@test.com", "student", "x", UserRole.STUDENT));

        assertThrows(SecurityException.class, () -> studentProxy.syncExamResults("IDP"));
    }
}

