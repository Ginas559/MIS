package vn.iotstar.coolenglish.factory;

import vn.iotstar.coolenglish.entity.UserAccount;
import vn.iotstar.coolenglish.integration.IPartnerIntegration;
import vn.iotstar.coolenglish.integration.adapter.IDPResultAdapter;
import vn.iotstar.coolenglish.integration.decorator.IntegrationLoggerDecorator;
import vn.iotstar.coolenglish.integration.proxy.IntegrationSecurityProxy;

public final class PartnerIntegrationFactory {

    private static volatile PartnerIntegrationFactory instance;

    private PartnerIntegrationFactory() {
    }

    public static PartnerIntegrationFactory getInstance() {
        if (instance == null) {
            synchronized (PartnerIntegrationFactory.class) {
                if (instance == null) {
                    instance = new PartnerIntegrationFactory();
                }
            }
        }
        return instance;
    }

    public IPartnerIntegration buildExamResultSyncGateway(UserAccount currentUser) {
        IPartnerIntegration adapter = new IDPResultAdapter();
        IPartnerIntegration loggerDecorator = new IntegrationLoggerDecorator(adapter);
        return new IntegrationSecurityProxy(loggerDecorator, currentUser);
    }
}

