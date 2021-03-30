package com.armedia.acm.services.notification.service.provider;

import com.armedia.acm.core.provider.TemplateModelProvider;
import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.services.authenticationtoken.service.AuthenticationTokenService;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.service.provider.model.BillingTemplateModel;


public class BillingTemplateModelProvider implements TemplateModelProvider<BillingTemplateModel>

{
    private AuthenticationTokenService authenticationTokenService;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;

    @Override
    public BillingTemplateModel getModel(Object object)
    {
        Notification notification = (Notification) object;

        String[] params = notification.getNote().split("_");
        String amount = params[0];
        Long fileId = Long.valueOf(params[1]);
        String objectId = notification.getParentId().toString();

        getAuditPropertyEntityAdapter().setUserId(notification.getCreator());
        String token = authenticationTokenService.generateAndSaveAuthenticationToken(fileId, notification.getEmailAddresses(),null);

        return new BillingTemplateModel(amount, token, fileId.toString(), objectId, notification.getParentType());
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
