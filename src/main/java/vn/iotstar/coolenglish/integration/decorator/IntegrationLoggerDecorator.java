package vn.iotstar.coolenglish.integration.decorator;

import java.time.LocalDateTime;

import vn.iotstar.coolenglish.integration.IPartnerIntegration;

public class IntegrationLoggerDecorator implements IPartnerIntegration {

    private final IPartnerIntegration wrappee;

    public IntegrationLoggerDecorator(IPartnerIntegration wrappee) {
        this.wrappee = wrappee;
    }

    @Override
    public void syncExamResults(String partnerCode) {
        System.out.println("LOG: Bat dau dong bo luc " + LocalDateTime.now() + " | partner=" + partnerCode);
        wrappee.syncExamResults(partnerCode);
        System.out.println("LOG: Hoan tat dong bo du lieu.");
    }
}

