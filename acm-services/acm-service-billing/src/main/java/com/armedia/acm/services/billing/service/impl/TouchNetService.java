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
import com.armedia.acm.services.billing.dao.BillingItemDao;
import com.touchnet.secureLink.service.TPGSecureLink_BindingStub;
import com.touchnet.secureLink.service.TPGSecureLink_ServiceLocator;
import com.touchnet.secureLink.types.AuthorizeAccountRequest;
import com.touchnet.secureLink.types.AuthorizeAccountResponse;
import com.touchnet.secureLink.types.GenerateSecureLinkTicketRequest;
import com.touchnet.secureLink.types.GenerateSecureLinkTicketResponse;
import com.touchnet.secureLink.types.NameValuePair;
import com.touchnet.secureLink.types.SecureLinkException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.util.HtmlUtils;

import javax.xml.rpc.ServiceException;

import java.rmi.RemoteException;

public class TouchNetService
{

    private ApplicationConfig applicationConfig;
    private BillingItemDao billingItemDao;

    private Logger log = LogManager.getLogger(getClass());

    @Value("${payment.touchnet.username}")
    private String touchNetUsername;

    @Value("${payment.touchnet.password}")
    private String touchNetPassword;

    @Value("${payment.touchnet.securelinkendpoint}")
    private String secureLinkEndPoint;

    @Value("${payment.touchnet.securepaylinkendpoint}")
    private String securePayLinkEndPoint;

    @Value("${payment.touchnet.upaysiteid}")
    private String uPaySiteId;

    public String generateTicketID(String amt, String objectId, String objectType, String ecmFileId, String acm_ticket, String objectNumber)
    {

        GenerateSecureLinkTicketRequest req = new GenerateSecureLinkTicketRequest();
        req.setTicketName(objectId + objectType);
        NameValuePair[] pairs = new NameValuePair[7];
        pairs[0] = new NameValuePair();
        pairs[0].setName("AMT");
        pairs[0].setValue(amt);
        pairs[1] = new NameValuePair();
        pairs[1].setName("EXT_TRANS_ID");
        pairs[1].setValue(acm_ticket);
        pairs[2] = new NameValuePair();
        pairs[2].setName("SUCCESS_LINK");
        pairs[2].setValue(getApplicationConfig().getBaseUrl() + "/api/latest/plugin/billing/confirmPayment");
        pairs[3] = new NameValuePair();
        pairs[3].setName("BILL_PARENT_ID");
        pairs[3].setValue(objectId);
        pairs[4] = new NameValuePair();
        pairs[4].setName("BILL_PARENT_TYPE");
        pairs[4].setValue(objectType);
        pairs[5] = new NameValuePair();
        pairs[5].setName("BILL_INVOICE_ID");
        pairs[5].setValue(ecmFileId);
        pairs[6] = new NameValuePair();
        pairs[6].setName("BILL_PARENT_NUMBER");
        pairs[6].setValue(objectNumber);
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

    public String validateLinkAndRedirectToPaymentForm(String amount, String objectId, String objectType, String objectNumber,
            String ecmFileId, String acm_ticket)
    {
        if (!billingItemDao.checkIfPaymentIsAlreadyDone(acm_ticket))
        {
            return redirectToPaymentForm(amount, objectId, objectType, ecmFileId, acm_ticket, objectNumber);
        }
        else
        {
            return redirectToAlreadyPaidPage();
        }
    }

    public String redirectToConfirmationPage()
    {
        String imgSrc = getApplicationConfig().getBaseUrl() + "/branding/emaillogo.png";

        return HtmlUtils.htmlEscape("<html>\n" +
                "<header>\n" +
                "<link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css\">\n" +
                "<style>\n" +
                ".icon{\n" +
                "  font-size:90px;\n" +
                "  color:#3E8F3E;\n" +
                "}\n" +
                ".htext{\n" +
                "  font-family: Arial, Helvetica, sans-serif;\n" +
                "  font-size:36;\n" +
                "  color:#788288;\n" +
                "}\n" +
                ".ptext {\n" +
                "  font-family: Arial, Helvetica, sans-serif;\n" +
                "  font-size:24;\n" +
                "  color:#788288\n" +
                "}\n" +
                "</style>\n" +
                "</header>\n" +
                "<body style=\"background-color:#F2F4F8;text-align: center;\">\n" +
                "<br><br><br><br><br><br><br><br><br>\n" +
                "<i class=\"fa fa-check-circle icon\"/>\n" +
                "<h1 class=\"htext\">Thank You For Your Payment</h1>\n" +
                "<p class=\"ptext\">A confirmation email will be sent.</p></br>\n" +
                "<br><br><br>\n" +
                "<img src=\"" + imgSrc + "\" width=\"186\" height=\"38.79\">\n" +
                "</body>\n" +
                "</html>\n");
    }

    private String redirectToPaymentForm(String amount, String objectId, String objectType, String ecmFileId, String acm_ticket,
            String objectNumber)
    {
        String ticket = generateTicketID(amount, objectId, objectType, ecmFileId, acm_ticket, objectNumber);
        String ticketName = objectId + objectType;

        String form = "<form name=\"autoform\" action=\"" + securePayLinkEndPoint + "\" method=\"post\">\n" +
                "    <input name=\"UPAY_SITE_ID\" type=\"hidden\" value=\"" + uPaySiteId + "\" />\n" +
                "    <input name=\"TICKET\" type=\"hidden\" value=\"" + ticket + "\" />\n" +
                "    <input name=\"TICKET_NAME\" type=\"hidden\" value=\"" + ticketName + "\" />\n" +
                "</form>\n" +
                "<script type=\"text/javascript\">\n" +
                "         document.autoform.submit();\n" +
                "</script>\n";

        log.debug(form);

        return HtmlUtils.htmlEscape(form);
    }

    private String redirectToAlreadyPaidPage()
    {
        String imgSrc = getApplicationConfig().getBaseUrl() + "/branding/emaillogo.png";
        return HtmlUtils.htmlEscape("<html>\n" +
                "<header>\n" +
                "<link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css\">\n" +
                "<style>\n" +
                ".icon{\n" +
                "  font-size:90px;\n" +
                "  color:#3393b7;\n" +
                "}\n" +
                ".htext{\n" +
                "  font-family: Arial, Helvetica, sans-serif;\n" +
                "  font-size:36;\n" +
                "  color:#788288;\n" +
                "}\n" +
                "</style>\n" +
                "</header>\n" +
                "<body style=\"background-color:#F2F4F8;text-align: center;\">\n" +
                "<br><br><br><br><br><br><br><br><br><br><br>\n" +
                "<i class=\"fa fa-exclamation-circle icon\"/>\n" +
                "<h1 class=\"htext\">This invoice has already been paid</h1>\n" +
                "<br>\n" +
                "<img src=\"" + imgSrc + "\" width=\"186\" height=\"38.79\">\n" +
                "</body>\n" +
                "</html>\n");
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

    public String getuPaySiteId()
    {
        return uPaySiteId;
    }

    public void setuPaySiteId(String uPaySiteId)
    {
        this.uPaySiteId = uPaySiteId;
    }

    public BillingItemDao getBillingItemDao()
    {
        return billingItemDao;
    }

    public void setBillingItemDao(BillingItemDao billingItemDao)
    {
        this.billingItemDao = billingItemDao;
    }

    public String getSecurePayLinkEndPoint()
    {
        return securePayLinkEndPoint;
    }

    public void setSecurePayLinkEndPoint(String securePayLinkEndPoint)
    {
        this.securePayLinkEndPoint = securePayLinkEndPoint;
    }
}
