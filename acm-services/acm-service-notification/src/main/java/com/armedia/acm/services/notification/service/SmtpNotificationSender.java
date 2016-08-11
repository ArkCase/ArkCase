package com.armedia.acm.services.notification.service;

import com.armedia.acm.core.exceptions.AcmEncryptionException;
import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.files.propertymanager.PropertyFileManager;
import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.service.outlook.model.EmailWithAttachmentsDTO;
import com.armedia.acm.service.outlook.model.EmailWithEmbeddedLinksDTO;
import com.armedia.acm.service.outlook.model.EmailWithEmbeddedLinksResultDTO;
import com.armedia.acm.service.outlook.model.OutlookEventSentEvent;
import com.armedia.acm.services.authenticationtoken.dao.AuthenticationTokenDao;
import com.armedia.acm.services.authenticationtoken.model.AuthenticationToken;
import com.armedia.acm.services.authenticationtoken.model.AuthenticationTokenConstants;
import com.armedia.acm.services.authenticationtoken.service.AuthenticationTokenService;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.model.NotificationConstants;
import com.armedia.acm.services.users.model.AcmUser;

import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.core.Authentication;

import javax.activation.DataHandler;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SmtpNotificationSender implements NotificationSender, ApplicationEventPublisherAware
{

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;
    private PropertyFileManager propertyFileManager;
    private String notificationPropertyFileLocation;
    private MuleContextManager muleContextManager;
    private AuthenticationTokenService authenticationTokenService;
    private AuthenticationTokenDao authenticationTokenDao;
    private EcmFileService ecmFileService;
    String flow = "vm://sendEmailViaSmtp.in";
    private ApplicationEventPublisher eventPublisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        eventPublisher = applicationEventPublisher;
    }

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
            // Notifications are always send as system user
            getAuditPropertyEntityAdapter().setUserId(NotificationConstants.SYSTEM_USER);
            Map<String, Object> messageProps = loadSmtpAndOriginatingProperties();
            messageProps.put("to", notification.getUserEmail());
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
    public void sendEmailWithAttachments(EmailWithAttachmentsDTO in, Authentication authentication, AcmUser user) throws Exception
    {
        Exception exception = null;
        Map<String, Object> messageProps = loadSmtpAndOriginatingProperties();
        messageProps.put("subject", in.getSubject());

        for (String emailAddress : in.getEmailAddresses())
        {
            try
            {
                messageProps.put("to", emailAddress);
                Map<String, DataHandler> attachments = new HashMap<String, DataHandler>();
                for (Long attachmentId : in.getAttachmentIds())
                {
                    InputStream contents = getEcmFileService().downloadAsInputStream(attachmentId);
                    EcmFile ecmFile = getEcmFileService().findById(attachmentId);
                    attachments.put(ecmFile.getFileName(), new DataHandler(new InputStreamDataSource(contents, ecmFile.getFileName())));
                }
                MuleMessage received = getMuleContextManager().send(flow, makeNote(emailAddress, in, authentication), attachments,
                        messageProps);
                exception = received.getInboundProperty("sendEmailException");

            } catch (MuleException e)
            {
                exception = e;
            }

            OutlookEventSentEvent event = new OutlookEventSentEvent(in, user.getUserId(), 0L, "SENT_EMAIL");

            if (exception == null)
            {
                event.setSucceeded(true);
            }
            else
            {
                event.setSucceeded(false);
                LOG.error("Email message not sent ...", exception);
            }

            eventPublisher.publishEvent(event);
        }
    }

    @Override
    public List<EmailWithEmbeddedLinksResultDTO> sendEmailWithEmbeddedLinks(EmailWithEmbeddedLinksDTO in, Authentication authentication,
            AcmUser user) throws Exception
    {
        List<EmailWithEmbeddedLinksResultDTO> emailResultList = new ArrayList<>();
        Exception exception = null;

        Map<String, Object> messageProps = loadSmtpAndOriginatingProperties();
        messageProps.put("subject", in.getSubject());
        for (String emailAddress : in.getEmailAddresses())
        {
            try
            {
                messageProps.put("to", emailAddress);
                MuleMessage received = getMuleContextManager().send(flow, makeNote(emailAddress, in, authentication), messageProps);
                exception = received.getInboundProperty("sendEmailException");
            } catch (MuleException e)
            {
                exception = e;
            }

            OutlookEventSentEvent event = new OutlookEventSentEvent(in, user.getUserId(), 0L, "SENT_EMAIL");

            if (exception != null)
            {
                emailResultList.add(new EmailWithEmbeddedLinksResultDTO(emailAddress, false));
                event.setSucceeded(false);
                LOG.error("Email message not sent ...", exception);
            } else
            {
                emailResultList.add(new EmailWithEmbeddedLinksResultDTO(emailAddress, true));
                event.setSucceeded(true);
            }

            eventPublisher.publishEvent(event);
        }

        return emailResultList;
    }

    private Map<String, Object> loadSmtpAndOriginatingProperties() throws AcmEncryptionException
    {
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
        return messageProps;
    }

    private String makeNote(String emailAddress, EmailWithAttachmentsDTO emailWithAttachmentsDTO, Authentication authentication)
    {
        String note = "";
        note += emailWithAttachmentsDTO.getHeader();
        note += "\r\r";
        note += emailWithAttachmentsDTO.getBody();
        note += "\r\r\r";
        note += emailWithAttachmentsDTO.getFooter();
        return note;
    }

    private String makeNote(String emailAddress, EmailWithEmbeddedLinksDTO emailWithEmbeddedLinksDTO, Authentication authentication)
    {
        String note = "";
        note += emailWithEmbeddedLinksDTO.getHeader();
        for (Long fileId : emailWithEmbeddedLinksDTO.getFileIds())
        {
            String token = generateAndSaveAuthenticationToken(fileId, emailAddress, authentication);
            note += " http://" + emailWithEmbeddedLinksDTO.getBaseUrl() + fileId + "&acm_email_ticket=" + token + "\n";
        }
        note += emailWithEmbeddedLinksDTO.getFooter();
        return note;
    }

    private String generateAndSaveAuthenticationToken(Long fileId, String emailAddress, Authentication authentication)
    {
        String token = getAuthenticationTokenService().getUncachedTokenForAuthentication(authentication);
        saveAuthenticationToken(emailAddress, fileId, token);
        return token;
    }

    private void saveAuthenticationToken(String email, Long fileId, String token)
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

    public EcmFileService getEcmFileService()
    {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
    }

}
