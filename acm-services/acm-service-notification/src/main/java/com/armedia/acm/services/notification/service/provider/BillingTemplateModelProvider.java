package com.armedia.acm.services.notification.service.provider;

import com.armedia.acm.core.provider.TemplateModelProvider;
import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.services.authenticationtoken.service.AuthenticationTokenService;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.service.provider.model.BillingTemplateModel;

import java.util.Date;


public class BillingTemplateModelProvider implements TemplateModelProvider<BillingTemplateModel>

{
    private AuthenticationTokenService authenticationTokenService;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;

    @Override
    public BillingTemplateModel getModel(Object object)
    {
        Notification notification = (Notification) object;

        String[] params = notification.getNote().split("_");
        String fileId = params[0] != null ? params[0] : "";
        String amount = params[1];
        String objectId = notification.getParentId().toString();
        String billName = params[2] != null ? params[2] : "";
        String paymentMethod = params[3] != null ? params[3] : "";
        String last4digitsOfCardNumber = params[4] != null ? params[4] : "";
        Date date = notification.getCreated();

        getAuditPropertyEntityAdapter().setUserId(notification.getCreator());
        String token = authenticationTokenService.generateAndSaveAuthenticationToken(Long.valueOf(fileId), notification.getEmailAddresses(),null);

        return new BillingTemplateModel(amount, token, fileId.toString(), objectId, notification.getParentType(), billName, paymentMethod, last4digitsOfCardNumber, date);
    }

    @Override
    public Class<BillingTemplateModel> getType()
    {
        return BillingTemplateModel.class;
    }


    public AuthenticationTokenService getAuthenticationTokenService()
    {
        return authenticationTokenService;
    }

    public void setAuthenticationTokenService(AuthenticationTokenService authenticationTokenService)
    {
        this.authenticationTokenService = authenticationTokenService;
    }

    public AuditPropertyEntityAdapter getAuditPropertyEntityAdapter()
    {
        return auditPropertyEntityAdapter;
    }

    public void setAuditPropertyEntityAdapter(AuditPropertyEntityAdapter auditPropertyEntityAdapter)
    {
        this.auditPropertyEntityAdapter = auditPropertyEntityAdapter;
    }
}
