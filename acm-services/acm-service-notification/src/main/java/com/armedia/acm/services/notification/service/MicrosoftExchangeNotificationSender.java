package com.armedia.acm.services.notification.service;

import com.armedia.acm.service.outlook.dao.impl.ExchangeWebServicesOutlookDao;
import com.armedia.acm.service.outlook.model.AcmOutlookUser;
import com.armedia.acm.service.outlook.model.EmailWithAttachmentsDTO;
import com.armedia.acm.service.outlook.model.EmailWithEmbeddedLinksDTO;
import com.armedia.acm.service.outlook.model.EmailWithEmbeddedLinksResultDTO;
import com.armedia.acm.service.outlook.model.MessageBodyFactory;
import com.armedia.acm.service.outlook.model.OutlookDTO;
import com.armedia.acm.service.outlook.service.OutlookService;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.model.NotificationConstants;
import com.armedia.acm.services.users.model.AcmUser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class MicrosoftExchangeNotificationSender extends NotificationSender
{

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private OutlookService outlookService;
    private ExchangeWebServicesOutlookDao dao;

    @Override
    public Notification send(Notification notification)
    {
        Exception exception = null;

        if (notification == null)
        {
            return null;
        }

        try
        {
            getAuditPropertyEntityAdapter().setUserId(NotificationConstants.SYSTEM_USER);

            EmailWithAttachmentsDTO in = new EmailWithAttachmentsDTO();
            in.setHeader("");
            in.setFooter("");
            in.setTemplate(notificationTemplate);

            String notificationLink = getNotificationUtils().buildNotificationLink(notification.getParentType(), notification.getParentId(),
                    notification.getRelatedObjectType(), notification.getRelatedObjectId());

            String messageBody = notificationLink != null ? String.format("%s Link: %s", notification.getNote(), notificationLink)
                    : notification.getNote();

            in.setBody(new MessageBodyFactory().buildMessageBodyWithoutHeaderFromTemplate(messageBody, ""));
            in.setSubject(notification.getTitle());
            in.setEmailAddresses(Arrays.asList(notification.getUserEmail()));

            String userId = getPropertyFileManager().load(getEmailSenderPropertyFileLocation(), NotificationConstants.EMAIL_USER_KEY, null);
            String userEmail = getPropertyFileManager().load(getEmailSenderPropertyFileLocation(), NotificationConstants.EMAIL_FROM_KEY,
                    null);
            String userPass = getPropertyFileManager().load(getEmailSenderPropertyFileLocation(), NotificationConstants.EMAIL_PASSWORD_KEY,
                    null);

            AcmOutlookUser outlookUser = new AcmOutlookUser(userId, userEmail, userPass);

            Authentication authentication = SecurityContextHolder.getContext() != null
                    ? SecurityContextHolder.getContext().getAuthentication() : null;

            getOutlookService().sendEmail(in, outlookUser, authentication);
        } catch (Exception e)
        {
            exception = e;
        }
        if (exception == null)
        {
            notification.setState(NotificationConstants.STATE_SENT);
        } else
        {
            LOG.error("Notification message not sent ...", exception);
            notification.setState(NotificationConstants.STATE_NOT_SENT);
        }

        return notification;
    }

    @Override
    public <T> void sendPlainEmail(Stream<T> emailsDataStream, EmailBuilder<T> emailBuilder, EmailBodyBuilder<T> emailBodyBuilder)
            throws Exception
    {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void sendEmailWithAttachments(EmailWithAttachmentsDTO in, Authentication authentication, AcmUser user) throws Exception
    {
        in.setTemplate(notificationTemplate);
        OutlookDTO outlookDTO = getOutlookService().retrieveOutlookPassword(authentication);
        AcmOutlookUser outlookUser = new AcmOutlookUser(authentication.getName(), user.getMail(), outlookDTO.getOutlookPassword());
        getOutlookService().sendEmailWithAttachments(in, outlookUser, authentication);
    }

    @Override
    public List<EmailWithEmbeddedLinksResultDTO> sendEmailWithEmbeddedLinks(EmailWithEmbeddedLinksDTO in, Authentication authentication,
            AcmUser user) throws Exception
    {
        in.setTemplate(notificationTemplate);
        OutlookDTO outlookDTO = getOutlookService().retrieveOutlookPassword(authentication);
        AcmOutlookUser outlookUser = new AcmOutlookUser(authentication.getName(), user.getMail(), outlookDTO.getOutlookPassword());
        return getOutlookService().sendEmailWithEmbeddedLinks(in, outlookUser, authentication);
    }

    public OutlookService getOutlookService()
    {
        return outlookService;
    }

    public void setOutlookService(OutlookService outlookService)
    {
        this.outlookService = outlookService;
    }

    public ExchangeWebServicesOutlookDao getDao()
    {
        return dao;
    }

    public void setDao(ExchangeWebServicesOutlookDao dao)
    {
        this.dao = dao;
    }

}
