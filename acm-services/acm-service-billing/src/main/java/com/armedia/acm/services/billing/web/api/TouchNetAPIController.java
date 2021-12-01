package com.armedia.acm.services.billing.web.api;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.data.service.AcmDataService;
import com.armedia.acm.plugins.addressable.model.ContactMethod;
import com.armedia.acm.plugins.person.dao.PersonAssociationDao;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.services.billing.dao.BillingInvoiceDao;
import com.armedia.acm.services.billing.exception.CreateBillingItemException;
import com.armedia.acm.services.billing.model.BillingConstants;
import com.armedia.acm.services.billing.model.BillingEventPublisher;
import com.armedia.acm.services.billing.model.BillingItem;
import com.armedia.acm.services.billing.service.BillingService;
import com.armedia.acm.services.billing.service.impl.TouchNetService;
import com.armedia.acm.services.notification.helper.UserInfoHelper;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.service.NotificationService;
import com.armedia.acm.services.participants.service.AcmParticipantService;
import com.armedia.acm.services.participants.utils.ParticipantUtils;
import com.armedia.acm.services.templateconfiguration.model.Template;
import com.armedia.acm.services.templateconfiguration.service.CorrespondenceTemplateManager;
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
import java.util.ArrayList;
import java.util.List;

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
    private PersonAssociationDao personAssociationDao;
    private BillingEventPublisher billingEventPublisher;
    private BillingInvoiceDao billingInvoiceDao;
    private UserInfoHelper userInfoHelper;
    private CorrespondenceTemplateManager templateManager;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;

    private Logger log = LogManager.getLogger(getClass());

    @GetMapping(value = "/touchnet", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String redirectToPayment(@RequestParam(value = "amt", required = true) String amt,
                                    @RequestParam(value = "objectId", required = true) String objectId,
                                    @RequestParam(value = "ecmFileId", required = true) String ecmFileId,
                                    @RequestParam(value = "objectType", required = true) String objectType,
                                    @RequestParam(value = "objectNumber", required = true) String objectNumber,
                                    @RequestParam(value = "acm_email_ticket", required = false) String acm_ticket)
    {
        return getTouchNetService().validateLinkAndRedirectToPaymentForm(amt,objectId,objectType,objectNumber,ecmFileId, acm_ticket);
    }

    @RequestMapping(value = "/confirmPayment", method = RequestMethod.POST, produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String confirmPayment(@RequestParam(value = "session_identifier", required = true) String sessionId,
                                 @RequestParam(value = "pmt_amt", required = true) String paymentAmount,
                                 @RequestParam(value = "name_on_acct", required = true) String billName,
                                 @RequestParam(value = "acct_number", required = true) String cardNumber,
                                 @RequestParam(value = "pmt_method", required = true) String paymentMethod) throws ServiceException, RemoteException
    {
        AuthorizeAccountResponse authorizeAccountResponse = touchNetService.authorizeAccount(sessionId);
        String objectType = "";
        String objectId = "";
        String invoiceId = "";
        String acmTicket = "";
        String objectNumber = "";
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
            else if(param.getName().equals("BILL_INVOICE_ID"))
            {
                invoiceId = param.getValue();
            }
            else if(param.getName().equals("EXT_TRANS_ID"))
            {
                acmTicket = param.getValue();
            }
            else if(param.getName().equals("BILL_PARENT_NUMBER"))
            {
                objectNumber = param.getValue().replace("_", "-");
            }
        }
        getAuditPropertyEntityAdapter().setUserId("TOUCHNET-PAYMENT");
        generateAndSaveBilling(objectId, objectType, paymentAmount, sessionId, invoiceId, acmTicket);
        sendPaymentConfirmationEmail(objectType, Long.valueOf(objectId), billName, paymentAmount, cardNumber, paymentMethod, sessionId,
                objectNumber);

        return getTouchNetService().redirectToConfirmationPage();

    }

    private void generateAndSaveBilling(String objectId, String objectType, String itemAmount, String sessionId, String invoiceId, String acmTicket)
    {
        BillingItem billingItem = new BillingItem();
        billingItem.setCreator("TOUCHNET-PAYMENT");
        billingItem.setModifier("TOUCHNET-PAYMENT");
        billingItem.setItemDescription("TouchNet Payment with Transaction ID: " + sessionId);
        billingItem.setParentObjectId(Long.valueOf(objectId));
        billingItem.setParentObjectType(objectType);
        billingItem.setItemAmount(-Double.valueOf(itemAmount));
        billingItem.setItemType(BillingConstants.BILLING_ITEM_TYPE_DEFAULT);
        billingItem.setBillingNote(acmTicket);
        try
        {
            billingService.createBillingItem(billingItem);
            getBillingEventPublisher().publishTouchnetPaymentCreatedEvent(getBillingInvoiceDao().getBillingInvoiceByEcmFileId(Long.valueOf(invoiceId)));
        }
        catch (CreateBillingItemException e)
        {
            log.error("Error creating billing item for object with ID: [{}]", objectId);
        }
    }

    private void sendPaymentConfirmationEmail(String objectType, Long objectId, String billName, String amount, String cardNumber, String paymentMethod, String sessionId, String objectNumber)
    {
        List<Person> requestors;
        requestors = getPersonAssociationDao().findPersonByParentIdAndParentTypeAndPersonType(objectType,objectId, "Requester");
        if(requestors.size() < 1 ||  requestors == null)
        {
            requestors = getPersonAssociationDao().findPersonByParentIdAndParentTypeAndPersonType(objectType, objectId, "Initiator");
        }
        String requestorEmailAddress = extractRequestorEmailAddress(requestors.get(0));
        String assigneeLdapId = ParticipantUtils.getAssigneeIdFromParticipants(getAcmParticipantService().getParticipantsFromParentObject(objectId,objectType));
        String assigneeEmailAddress = getUserInfoHelper().getUserEmail(assigneeLdapId);

        String note = objectId.toString() + "_" + amount + "_" + billName + "_" + paymentMethod + "_" + cardNumber.substring(cardNumber.length() - 4) + "_" + sessionId + "_" + objectNumber;
        String emailSubject = "";
        Template template = templateManager.findTemplate("paymentConfirmation.html");
        if (template != null)
        {
            emailSubject = template.getEmailSubject();
        }
        Notification requestorNotification  = notificationService.getNotificationBuilder()
                .newNotification("paymentConfirmation", BillingConstants.CONFIRMATION_PAYMENT_TITLE, objectType, objectId, null)
                .withEmailAddresses(requestorEmailAddress)
                .withNote(note)
                .withSubject(emailSubject)
                .build();

        notificationService.saveNotification(requestorNotification);

        template = templateManager.findTemplate("assigneeConfirmationPayment.html");
        if (template != null)
        {
            emailSubject = template.getEmailSubject();
        }
        Notification assigneeNotification = notificationService.getNotificationBuilder()
                .newNotification("assigneeConfirmationPayment", BillingConstants.CONFIRMATION_PAYMENT_TITLE, objectType, objectId, null)
                .withEmailAddresses(assigneeEmailAddress != null ? assigneeEmailAddress : "")
                .withNote(note)
                .withSubject(emailSubject)
                .build();
        notificationService.saveNotification(assigneeNotification);
    }

    private String extractRequestorEmailAddress(Person person)
    {
        List<ContactMethod> contactMethods = person.getContactMethods();

        if (contactMethods != null && !contactMethods.isEmpty())
        {

            for (ContactMethod contactMethod : contactMethods)
            {

                // Is `email` the correct type? Is there a constant somewhere for the email contact method type?
                if (contactMethod.getType().equalsIgnoreCase("email"))
                {
                    return contactMethod.getValue();
                }

            }

        }
        return "";
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

    public PersonAssociationDao getPersonAssociationDao()
    {
        return personAssociationDao;
    }

    public void setPersonAssociationDao(PersonAssociationDao personAssociationDao)
    {
        this.personAssociationDao = personAssociationDao;
    }

    public BillingEventPublisher getBillingEventPublisher()
    {
        return billingEventPublisher;
    }

    public void setBillingEventPublisher(BillingEventPublisher billingEventPublisher)
    {
        this.billingEventPublisher = billingEventPublisher;
    }

    public BillingInvoiceDao getBillingInvoiceDao()
    {
        return billingInvoiceDao;
    }

    public void setBillingInvoiceDao(BillingInvoiceDao billingInvoiceDao)
    {
        this.billingInvoiceDao = billingInvoiceDao;
    }

    public UserInfoHelper getUserInfoHelper()
    {
        return userInfoHelper;
    }

    public void setUserInfoHelper(UserInfoHelper userInfoHelper)
    {
        this.userInfoHelper = userInfoHelper;
    }

    public CorrespondenceTemplateManager getTemplateManager()
    {
        return templateManager;
    }

    public void setTemplateManager(CorrespondenceTemplateManager templateManager)
    {
        this.templateManager = templateManager;
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

