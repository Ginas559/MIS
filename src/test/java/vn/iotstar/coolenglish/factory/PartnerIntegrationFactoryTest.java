package vn.iotstar.coolenglish.factory;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

import vn.iotstar.coolenglish.entity.UserAccount;
import vn.iotstar.coolenglish.enums.UserRole;
import vn.iotstar.coolenglish.integration.IPartnerIntegration;
import vn.iotstar.coolenglish.integration.proxy.IntegrationSecurityProxy;

class PartnerIntegrationFactoryTest {

    @Test
    void shouldBeSingleton() {
        assertSame(PartnerIntegrationFactory.getInstance(), PartnerIntegrationFactory.getInstance());
    }

    @Test
    void shouldBuildGatewayChain() {
        IPartnerIntegration gateway = PartnerIntegrationFactory.getInstance()
                .buildExamResultSyncGateway(new UserAccount("staff@test.com", "staff", "x", UserRole.STAFF));

        assertNotNull(gateway);
        assertSame(IntegrationSecurityProxy.class, gateway.getClass());
    }
}

