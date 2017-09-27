package com.armedia.acm.services.notification.service;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.files.propertymanager.PropertyFileManager;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.services.authenticationtoken.dao.AuthenticationTokenDao;
import com.armedia.acm.services.authenticationtoken.service.AuthenticationTokenService;
import com.armedia.acm.services.email.model.EmailBodyBuilder;
import com.armedia.acm.services.email.model.EmailBuilder;
import com.armedia.acm.services.email.model.EmailWithAttachmentsAndLinksDTO;
import com.armedia.acm.services.email.model.EmailWithAttachmentsDTO;
import com.armedia.acm.services.email.model.EmailWithEmbeddedLinksDTO;
import com.armedia.acm.services.email.model.EmailWithEmbeddedLinksResultDTO;
import com.armedia.acm.services.email.model.MessageBodyFactory;
import com.armedia.acm.services.email.sender.model.EmailSenderConfigurationConstants;
import com.armedia.acm.services.email.service.AcmEmailSenderService;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.model.NotificationConstants;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author riste.tutureski
 */
public abstract class NotificationSender
{

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    protected AuditPropertyEntityAdapter auditPropertyEntityAdapter;
    protected PropertyFileManager propertyFileManager;
    protected String emailSenderPropertyFileLocation;
    protected AuthenticationTokenService authenticationTokenService;
    protected AuthenticationTokenDao authenticationTokenDao;
    protected EcmFileService ecmFileService;
    protected NotificationUtils notificationUtils;
    protected String notificationTemplate;
    private AcmEmailSenderService emailSenderService;
    private UserDao userDao;

    /**
     * Sends the notification to user's email. If successful, sets the notification state to
     * {@link NotificationConstants#STATE_SENT}, otherwise it sets it to {@link NotificationConstants#STATE_NOT_SENT}
     *
     * @param notification
     *            the notification to send
     * @return the notification with state set
     */
    public Notification send(Notification notification)
    {
        Exception exception = null;

        if (notification == null)
        {
            return null;
        }

        try
        {
            // Notifications are always send as system user
            getAuditPropertyEntityAdapter().setUserId(NotificationConstants.SYSTEM_USER);

            EmailWithAttachmentsDTO in = new EmailWithAttachmentsDTO();
            in.setHeader("");
            in.setFooter("");
            in.setTemplate(notificationTemplate);

            String notificationLink = notificationUtils.buildNotificationLink(notification.getParentType(), notification.getParentId(),
                    notification.getRelatedObjectType(), notification.getRelatedObjectId());

            String messageBody = notificationLink != null ? String.format("%s Link: %s", notification.getNote(), notificationLink)
                    : notification.getNote();

            in.setBody(new MessageBodyFactory(notificationTemplate).buildMessageBodyFromTemplate(messageBody, "", ""));
            in.setSubject(notification.getTitle());
            in.setEmailAddresses(Arrays.asList(notification.getUserEmail()));

            Authentication authentication = SecurityContextHolder.getContext() != null
                    ? SecurityContextHolder.getContext().getAuthentication() : null;

            String userId = propertyFileManager.load(emailSenderPropertyFileLocation, EmailSenderConfigurationConstants.USERNAME, null);

            AcmUser acmUser = userDao.findByUserId(userId);

            getEmailSenderService().sendEmail(in, authentication, acmUser);

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

    public abstract <T> void sendPlainEmail(Stream<T> emailsDataStream, EmailBuilder<T> emailBuilder, EmailBodyBuilder<T> emailBodyBuilder)
            throws Exception;

    public abstract void sendEmailWithAttachments(EmailWithAttachmentsDTO in, Authentication authentication, String userId)
            throws Exception;

    public abstract void sendEmailWithAttachments(EmailWithAttachmentsDTO in, Authentication authentication, AcmUser user) throws Exception;

    public abstract void sendEmailWithAttachmentsAndLinks(EmailWithAttachmentsAndLinksDTO in, Authentication authentication, AcmUser user)
            throws Exception;

    public abstract List<EmailWithEmbeddedLinksResultDTO> sendEmailWithEmbeddedLinks(EmailWithEmbeddedLinksDTO in,
            Authentication authentication, AcmUser user) throws Exception;

    public void setNotificationUtils(NotificationUtils notificationUtils)
    {
        this.notificationUtils = notificationUtils;
    }

    public AuditPropertyEntityAdapter getAuditPropertyEntityAdapter()
    {
        return auditPropertyEntityAdapter;
    }

    public void setAuditPropertyEntityAdapter(AuditPropertyEntityAdapter auditPropertyEntityAdapter)
    {
        this.auditPropertyEntityAdapter = auditPropertyEntityAdapter;
    }

    public void setPropertyFileManager(PropertyFileManager propertyFileManager)
    {
        this.propertyFileManager = propertyFileManager;
    }

    /**
     * @param emailSenderPropertyFileLocation
     *            the emailSenderPropertyFileLocation to set
     */
    public void setEmailSenderPropertyFileLocation(String emailSenderPropertyFileLocation)
    {
        this.emailSenderPropertyFileLocation = emailSenderPropertyFileLocation;
    }

    public AuthenticationTokenService getAuthenticationTokenService()
    {
        return authenticationTokenService;
    }

    public void setAuthenticationTokenService(AuthenticationTokenService authenticationTokenService)
    {
        this.authenticationTokenService = authenticationTokenService;
    }

    public AuthenticationTokenDao getAuthenticationTokenDao()
    {
        return authenticationTokenDao;
    }

    public void setAuthenticationTokenDao(AuthenticationTokenDao authenticationTokenDao)
    {
        this.authenticationTokenDao = authenticationTokenDao;
    }

    public EcmFileService getEcmFileService()
    {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
    }

    public void setNotificationTemplate(Resource notificationTemplate) throws IOException
    {
        DataInputStream resourceStream = new DataInputStream(notificationTemplate.getInputStream());
        byte[] bytes = new byte[resourceStream.available()];
        resourceStream.readFully(bytes);
        this.notificationTemplate = new String(bytes, Charset.forName("UTF-8"));
    }

    public AcmEmailSenderService getEmailSenderService()
    {
        return emailSenderService;
    }

    public void setEmailSenderService(AcmEmailSenderService emailSenderService)
    {
        this.emailSenderService = emailSenderService;
    }

    /**
     * @param userDao
     *            the userDao to set
     */
    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    /**
     * @return the userDao
     */
    protected UserDao getUserDao()
    {
        return userDao;
    }

}
