package com.armedia.acm.services.notification.service;

import com.armedia.acm.core.exceptions.AcmEncryptionException;
import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.files.propertymanager.PropertyFileManager;
import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.service.outlook.model.EmailWithAttachmentsDTO;
import com.armedia.acm.service.outlook.model.EmailWithEmbeddedLinksDTO;
import com.armedia.acm.service.outlook.model.EmailWithEmbeddedLinksResultDTO;
import com.armedia.acm.services.authenticationtoken.dao.AuthenticationTokenDao;
import com.armedia.acm.services.authenticationtoken.model.AuthenticationToken;
import com.armedia.acm.services.authenticationtoken.model.AuthenticationTokenConstants;
import com.armedia.acm.services.authenticationtoken.service.AuthenticationTokenService;
import com.armedia.acm.services.notification.model.EmailNotificationDto;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.model.NotificationConstants;
import com.armedia.acm.services.users.model.AcmUser;

import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SmtpNotificationSender implements NotificationSender
{

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;
    private PropertyFileManager propertyFileManager;
    private String notificationPropertyFileLocation;
    private MuleContextManager muleContextManager;
    private AuthenticationTokenService authenticationTokenService;
    private AuthenticationTokenDao authenticationTokenDao;

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

            String flow = "vm://sendEmail.in";

            Map<String, Object> messageProps = new HashMap<>();

            messageProps.put("host",
                    getPropertyFileManager().load(getNotificationPropertyFileLocation(), NotificationConstants.EMAIL_HOST_KEY, null));
            messageProps.put("port",
                    getPropertyFileManager().load(getNotificationPropertyFileLocation(), NotificationConstants.EMAIL_PORT_KEY, null));
            messageProps.put("user",
                    getPropertyFileManager().load(getNotificationPropertyFileLocation(), NotificationConstants.EMAIL_USER_KEY, null));
            messageProps.put("password",
                    getPropertyFileManager().load(getNotificationPropertyFileLocation(), NotificationConstants.EMAIL_PASSWORD_KEY, null));
            messageProps.put("from",
                    getPropertyFileManager().load(getNotificationPropertyFileLocation(), NotificationConstants.EMAIL_FROM_KEY, null));
            // messageProps.put("to", notification.getUserEmail());
            messageProps.put("to", "sasko.tanaskoski@armedia.com");
            messageProps.put("subject", notification.getTitle());

            MuleMessage received = getMuleContextManager().send(flow, notification.getNote(), messageProps);

            exception = received.getInboundProperty("sendEmailException");
        } catch (MuleException e)
        {
            exception = e;
        } catch (AcmEncryptionException e)
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
    public List<Notification> sendEmailNotificationWithLinks(List<EmailNotificationDto> emailNotificationDtoList,
            Authentication authentication)
    {
        List<Notification> notificationList = new ArrayList<>();
        for (EmailNotificationDto emailNotificationDto : emailNotificationDtoList)
        {
            Notification notification = new Notification();
            notification.setTitle(emailNotificationDto.getTitle());
            for (String emailAddress : emailNotificationDto.getEmailAddresses())
            {
                notification.setNote(makeNote(emailAddress, emailNotificationDto, authentication));
                notification.setUserEmail(emailAddress);
                notification.setStatus(NotificationConstants.STATUS_NEW);
                notificationList.add(send(notification));
            }
        }
        return notificationList;
    }

    public String makeNote(String emailAddress, EmailNotificationDto emailNotificationDto, Authentication authentication)
    {
        String note = "";
        note += emailNotificationDto.getHeader();
        for (Long fileId : emailNotificationDto.getFileIds())
        {
            String token = generateAndSaveAuthenticationToken(fileId, emailAddress, emailNotificationDto, authentication);
            note += emailNotificationDto.getBaseUrl() + fileId + "&acm_email_ticket=" + token + "\n";
        }
        note += emailNotificationDto.getFooter();
        return note;
    }

    public String generateAndSaveAuthenticationToken(Long fileId, String emailAddress, EmailNotificationDto emailNotificationDto,
            Authentication authentication)
    {
        String token = getAuthenticationTokenService().getUncachedTokenForAuthentication(authentication);
        saveAuthenticationToken(emailAddress, fileId, token);
        return token;
    }

    public void saveAuthenticationToken(String email, Long fileId, String token)
    {
        AuthenticationToken authenticationToken = new AuthenticationToken();
        authenticationToken.setKey(token);
        authenticationToken.setStatus(AuthenticationTokenConstants.ACTIVE);
        authenticationToken.setEmail(email);
        authenticationToken.setFileId(fileId);
        getAuthenticationTokenDao().save(authenticationToken);
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

    @Override
    public void sendEmailWithAttachments(EmailWithAttachmentsDTO in, Authentication authentication, AcmUser user)
    {

    }

    @Override
    public void sendEmailNotificationWithLinks(EmailNotificationDto in, Authentication authentication)
    {

    }

    @Override
    public List<EmailWithEmbeddedLinksResultDTO> sendEmailWithEmbeddedLinks(EmailWithEmbeddedLinksDTO in, Authentication authentication,
            AcmUser user)
    {
        return null;
    }

}
