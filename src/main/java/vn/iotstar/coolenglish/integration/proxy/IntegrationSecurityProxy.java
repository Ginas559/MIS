package vn.iotstar.coolenglish.integration.proxy;

import vn.iotstar.coolenglish.entity.UserAccount;
import vn.iotstar.coolenglish.enums.UserRole;
import vn.iotstar.coolenglish.integration.IPartnerIntegration;

public class IntegrationSecurityProxy implements IPartnerIntegration {

    private final IPartnerIntegration realService;
    private final UserAccount currentUser;

    public IntegrationSecurityProxy(IPartnerIntegration realService, UserAccount currentUser) {
        this.realService = realService;
        this.currentUser = currentUser;
    }

    @Override
    public void syncExamResults(String partnerCode) {
        if (currentUser != null
                && (currentUser.getRole() == UserRole.STAFF || currentUser.getRole() == UserRole.ADMIN)) {
            realService.syncExamResults(partnerCode);
            return;
        }

        throw new SecurityException("Ban khong co quyen kich hoat cong tich hop nay!");
    }
}

