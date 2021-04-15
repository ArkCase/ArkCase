package com.armedia.acm.services.notification.service.provider;

/*-
 * #%L
 * ACM Service: Notification
 * %%
 * Copyright (C) 2014 - 2021 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

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
        String billName = "";
        String paymentMethod = "";
        String last4digitsOfCardNumber = "";
        String sessionId = "";
        if(params.length > 2)
        {
            billName = params[2];
            paymentMethod = params[3];
            last4digitsOfCardNumber = params[4];
            sessionId = params[5];
        }
        Date date = notification.getCreated();
        String message = "";
        if(notification.getTitle().contains(":"))
        {
            String[] objectParams = notification.getTitle().split(":");
            String[] objParams = objectParams[1].split(" ");
            message = objectParams[0] + " with number: " + objParams[1];
        }

        getAuditPropertyEntityAdapter().setUserId(notification.getCreator());
        String token = authenticationTokenService.generateAndSaveAuthenticationToken(Long.valueOf(fileId), notification.getEmailAddresses(),null);

        return new BillingTemplateModel(amount, token, fileId, objectId, notification.getParentType(), billName, paymentMethod, last4digitsOfCardNumber, date.toString(), sessionId, message);
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
