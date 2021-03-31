package com.armedia.acm.services.billing.web.api;

import com.armedia.acm.data.service.AcmDataService;
import com.armedia.acm.services.billing.exception.CreateBillingItemException;
import com.armedia.acm.services.billing.model.BillingConstants;
import com.armedia.acm.services.billing.model.BillingItem;
import com.armedia.acm.services.billing.service.BillingService;
import com.armedia.acm.services.billing.service.impl.TouchNetService;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.service.NotificationService;
import com.armedia.acm.services.participants.service.AcmParticipantService;
import com.armedia.acm.services.participants.utils.ParticipantUtils;
import com.touchnet.secureLink.types.AuthorizeAccountResponse;
import com.touchnet.secureLink.types.NameValuePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.xml.rpc.ServiceException;
import java.rmi.RemoteException;

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
@Transactional(rollbackFor = Exception.class)
@Controller
@RequestMapping({ "/api/v1/plugin/billing", "/api/latest/plugin/billing" })
public class TouchNetAPIController
{

    private TouchNetService touchNetService;
    private BillingService billingService;
    private AcmDataService acmDataService;
    private NotificationService notificationService;
    private AcmParticipantService acmParticipantService;

    private Logger log = LogManager.getLogger(getClass());

    @GetMapping(value = "/touchnet", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String redirectToPayment(@RequestParam(value = "amt", required = true) String amt,
                                    @RequestParam(value = "objectId", required = true) String objectId,
                                    @RequestParam(value = "ecmFileId", required = true) String ecmFileId,
                                    @RequestParam(value = "objectType", required = true) String objectType,
                                    @RequestParam(value = "acm_email_ticket", required = false) String acm_ticket)
    {
        String ticket = touchNetService.generateTicketID(amt, objectId, objectType, ecmFileId);
        String ticketName = objectId + objectType;

        return "<form name=\"autoform\" action=\"https://test.secure.touchnet.net:8443/C30002test_upay/web/index.jsp\" method=\"post\">\n" +
                "    <input name=\"UPAY_SITE_ID\" type=\"hidden\" value=\"252\" />\n" +
                "    <input name=\"TICKET\" type=\"hidden\" value=\"" + ticket + "\" />\n" +
                "    <input name=\"TICKET_NAME\" type=\"hidden\" value=\"" + ticketName + "\" />\n" +
                "</form>\n" +
                "<script type=\"text/javascript\">\n" +
                "         document.autoform.submit();\n" +
                "</script>\n";
    }

    @RequestMapping(value = "/confirmPayment", method = RequestMethod.POST, produces = MediaType.TEXT_HTML_VALUE)
    public String confirmPayment(@RequestParam(value = "session_identifier", required = true) String sessionId,
                                 @RequestParam(value = "pmt_amt", required = true) String paymentAmount,
                                 @RequestParam(value = "name_on_acct", required = true) String billName,
                                 @RequestParam(value = "acct_number", required = true) String cardNumber,
                                 @RequestParam(value = "pmt_method", required = true) String paymentMethod) throws ServiceException, RemoteException
    {


        AuthorizeAccountResponse authorizeAccountResponse = touchNetService.authorizeAccount(sessionId);
        String objectType = "";
        String objectId = "";
        NameValuePair[] params = authorizeAccountResponse.getNameValuePairs();
        for (NameValuePair param : params)
        {
            if(param.getName().equals("BILL_PARENT_ID"))
            {
                objectId = param.getValue();
            }
            else if(param.getName().equals("BILL_PARENT_TYPE"))
            {
                objectType = param.getValue();
            }
        }
        generateAndSaveBilling(objectId,objectType,paymentAmount);
        sendPaymentConfirmationEmail(objectType, Long.valueOf(objectId));

        return "<div class=\"jumbotron\">\n" +
                "  <h1>Thanks for your payment.</h1>\n" +
                "  <p>A confirmation email will be sent.</p>\n" +
                "  <p><a class=\"btn btn-primary btn-lg\" href=\"#\" role=\"button”>Continue...</a></p>\n" +
                "</div>";

    }

    private void generateAndSaveBilling(String objectId, String objectType, String itemAmount)
    {
        BillingItem billingItem = new BillingItem();
        billingItem.setCreator("TOUCHNET-PAYMENT");
        billingItem.setModifier("TOUCHNET-PAYMENT");
        billingItem.setItemDescription("TouchNet Payment");
        billingItem.setParentObjectId(Long.valueOf(objectId));
        billingItem.setParentObjectType(objectType);
        billingItem.setItemAmount(-Double.valueOf(itemAmount));
        billingItem.setItemType(BillingConstants.BILLING_ITEM_TYPE_DEFAULT);
        try
        {
            billingService.createBillingItem(billingItem);
        }
        catch (CreateBillingItemException e)
        {
            log.error("Error creating billing item for object with ID: [{}]", objectId);
        }
    }

    private void sendPaymentConfirmationEmail(String objectType, Long objectId)
    {
        String assigneeEmailAddress = ParticipantUtils.getAssigneeIdFromParticipants(getAcmParticipantService().getParticipantsFromParentObject(objectId,objectType));

        Notification notification = notificationService.getNotificationBuilder()
                .newNotification("confirmationPayment", BillingConstants.CONFIRMATION_PAYMENT_TITLE, objectType, objectId, null)
                .withEmailAddresses(assigneeEmailAddress)
                .build();

        notificationService.saveNotification(notification);
    }

    public TouchNetService getTouchNetService()
    {
        return touchNetService;
    }

    public void setTouchNetService(TouchNetService touchNetService)
    {
        this.touchNetService = touchNetService;
    }

    public BillingService getBillingService()
    {
        return billingService;
    }

    public void setBillingService(BillingService billingService)
    {
        this.billingService = billingService;
    }

    public AcmDataService getAcmDataService()
    {
        return acmDataService;
    }

    public void setAcmDataService(AcmDataService acmDataService)
    {
        this.acmDataService = acmDataService;
    }

    public NotificationService getNotificationService()
    {
        return notificationService;
    }

    public void setNotificationService(NotificationService notificationService)
    {
        this.notificationService = notificationService;
    }

    public AcmParticipantService getAcmParticipantService()
    {
        return acmParticipantService;
    }

    public void setAcmParticipantService(AcmParticipantService acmParticipantService)
    {
        this.acmParticipantService = acmParticipantService;
    }
}

