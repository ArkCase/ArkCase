/**
 * 
 */
package com.armedia.acm.services.notification.service;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.files.propertymanager.PropertyFileManager;
import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.service.outlook.model.EmailWithAttachmentsDTO;
import com.armedia.acm.service.outlook.model.EmailWithEmbeddedLinksDTO;
import com.armedia.acm.service.outlook.model.EmailWithEmbeddedLinksResultDTO;
import com.armedia.acm.services.authenticationtoken.dao.AuthenticationTokenDao;
import com.armedia.acm.services.authenticationtoken.service.AuthenticationTokenService;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.model.NotificationConstants;
import com.armedia.acm.services.users.model.AcmUser;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author riste.tutureski
 *
 */
public abstract class NotificationSender
{

    protected AuditPropertyEntityAdapter auditPropertyEntityAdapter;
    protected PropertyFileManager propertyFileManager;
    protected String notificationPropertyFileLocation;
    protected MuleContextManager muleContextManager;
    protected AuthenticationTokenService authenticationTokenService;
    protected AuthenticationTokenDao authenticationTokenDao;
    protected EcmFileService ecmFileService;
    protected NotificationUtils notificationUtils;
    protected String notificationTemplate;

    /**
     * Sends the notification to user's email. If successful, sets the notification state to
     * {@link NotificationConstants#STATE_SENT}, otherwise it sets it to {@link NotificationConstants#STATE_NOT_SENT}
     * 
     * @param notification
     *            the notification to send
     * @return the notification with state set
     */
    public abstract Notification send(Notification notification);

    public abstract <T> void sendPlainEmail(Stream<T> emailsDataStream, EmailBuilder<T> emailBuilder, EmailBodyBuilder<T> emailBodyBuilder)
            throws Exception;

    public void sendEmailWithAttachments(EmailWithAttachmentsDTO in, Authentication authentication) throws Exception
    {
    }

    public abstract void sendEmailWithAttachments(EmailWithAttachmentsDTO in, Authentication authentication, AcmUser user) throws Exception;

    public abstract List<EmailWithEmbeddedLinksResultDTO> sendEmailWithEmbeddedLinks(EmailWithEmbeddedLinksDTO in,
            Authentication authentication, AcmUser user) throws Exception;

    public NotificationUtils getNotificationUtils()
    {
        return notificationUtils;
    }

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

    public PropertyFileManager getPropertyFileManager()
    {
        return propertyFileManager;
    }

    public void setPropertyFileManager(PropertyFileManager propertyFileManager)
    {
        this.propertyFileManager = propertyFileManager;
    }

    public String getNotificationPropertyFileLocation()
    {
        return notificationPropertyFileLocation;
    }

    public void setNotificationPropertyFileLocation(String notificationPropertyFileLocation)
    {
        this.notificationPropertyFileLocation = notificationPropertyFileLocation;
    }

    public MuleContextManager getMuleContextManager()
    {
        return muleContextManager;
    }

    public void setMuleContextManager(MuleContextManager muleContextManager)
    {
        this.muleContextManager = muleContextManager;
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

    public String getNotificationTemplate()
    {
        return notificationTemplate;
    }

    public void setNotificationTemplate(Resource notificationTemplate) throws IOException
    {
        DataInputStream resourceStream = new DataInputStream(notificationTemplate.getInputStream());
        byte[] bytes = new byte[resourceStream.available()];
        resourceStream.readFully(bytes);
        this.notificationTemplate = new String(bytes, Charset.forName("UTF-8"));
    }

}
