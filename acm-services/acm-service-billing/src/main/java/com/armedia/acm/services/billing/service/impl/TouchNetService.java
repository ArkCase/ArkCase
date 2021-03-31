package com.armedia.acm.services.billing.service.impl;

/*-
 * #%L
 * ACM Service: Billing
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

import com.armedia.acm.core.model.ApplicationConfig;
import com.armedia.acm.services.authenticationtoken.service.AuthenticationTokenService;
import com.touchnet.secureLink.service.TPGSecureLink_BindingStub;
import com.touchnet.secureLink.service.TPGSecureLink_ServiceLocator;
import com.touchnet.secureLink.types.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import javax.xml.rpc.ServiceException;
import java.rmi.RemoteException;


public class TouchNetService
{

    private AuthenticationTokenService authenticationTokenService;
    private ApplicationConfig applicationConfig;

    private Logger log = LogManager.getLogger(getClass());

    @Value("${payment.touchnet.username}")
    private String touchNetUsername;

    @Value("${payment.touchnet.password}")
    private String touchNetPassword;

    @Value("${payment.touchnet.securelinkendpoint}")
    private String secureLinkEndPoint;


    public String generateTicketID(String amt, String objectId, String objectType)
    {
        String ticketName = authenticationTokenService.getTokenForAuthentication(null);

        GenerateSecureLinkTicketRequest req = new GenerateSecureLinkTicketRequest();
        req.setTicketName(objectId + objectType);
        NameValuePair[] pairs = new NameValuePair[5];
        pairs[0] = new NameValuePair();
        pairs[0].setName("AMT");
        pairs[0].setValue(amt);
        pairs[1] = new NameValuePair();
        pairs[1].setName("EXT_TRANS_ID");
        pairs[1].setValue(ticketName);
        pairs[2] = new NameValuePair();
        pairs[2].setName("SUCCESS_LINK");
        pairs[2].setValue(getApplicationConfig().getBaseUrl() + "/api/latest/plugin/billing/confirmPayment");
        pairs[3] = new NameValuePair();
        pairs[3].setName("BILL_PARENT_ID");
        pairs[3].setValue(objectId);
        pairs[4] = new NameValuePair();
        pairs[4].setName("BILL_PARENT_TYPE");
        pairs[4].setValue(objectType);
        req.setNameValuePairs(pairs);

        TPGSecureLink_BindingStub binding = null;
        try
        {
            binding = getSecureLinkBinding();
        }
        catch (ServiceException e)
        {
            e.printStackTrace();
        }
        String ticketId = null;
        try
        {
            log.debug("Secure link end point: " + secureLinkEndPoint);
            log.debug("Touchnet username:  " + touchNetUsername);
            log.debug("Touchnet pass:  " + touchNetPassword);
            GenerateSecureLinkTicketResponse secureLinkTicketResponse = binding.generateSecureLinkTicket(req);
            ticketId = secureLinkTicketResponse.getTicket();
        }
        catch (SecureLinkException e)
        {
            e.printStackTrace();
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
        }

        return ticketId;

    }

    public AuthorizeAccountResponse authorizeAccount(String sessionId) throws ServiceException, RemoteException
    {
        AuthorizeAccountRequest authorizeAccountRequest = new AuthorizeAccountRequest();
        authorizeAccountRequest.setSession(sessionId);
        TPGSecureLink_BindingStub binding = getSecureLinkBinding();
        AuthorizeAccountResponse authorizeAccountResponse = binding.authorizeAccount(authorizeAccountRequest);

        return authorizeAccountResponse;
    }

    private TPGSecureLink_BindingStub getSecureLinkBinding() throws ServiceException
    {
        TPGSecureLink_BindingStub binding;

        TPGSecureLink_ServiceLocator locator = new TPGSecureLink_ServiceLocator();
        locator.setTPGSecureLinkEndpointAddress(getSecureLinkEndPoint());

        binding = (TPGSecureLink_BindingStub) locator.getTPGSecureLink();
        binding.setUsername(getTouchNetUsername());
        binding.setPassword(getTouchNetPassword());

        return binding;
    }


    public AuthenticationTokenService getAuthenticationTokenService()
    {
        return authenticationTokenService;
    }

    public void setAuthenticationTokenService(AuthenticationTokenService authenticationTokenService)
    {
        this.authenticationTokenService = authenticationTokenService;
    }

    public String getTouchNetUsername()
    {
        return touchNetUsername;
    }

    public void setTouchNetUsername(String touchNetUsername)
    {
        this.touchNetUsername = touchNetUsername;
    }

    public String getTouchNetPassword()
    {
        return touchNetPassword;
    }

    public void setTouchNetPassword(String touchNetPassword)
    {
        this.touchNetPassword = touchNetPassword;
    }

    public String getSecureLinkEndPoint()
    {
        return secureLinkEndPoint;
    }

    public void setSecureLinkEndPoint(String secureLinkEndPoint)
    {
        this.secureLinkEndPoint = secureLinkEndPoint;
    }

    public ApplicationConfig getApplicationConfig()
    {
        return applicationConfig;
    }

    public void setApplicationConfig(ApplicationConfig applicationConfig)
    {
        this.applicationConfig = applicationConfig;
    }
}
