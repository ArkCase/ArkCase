package com.armedia.acm.services.notification.service;

import com.armedia.acm.core.exceptions.AcmEncryptionException;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.service.outlook.model.EmailWithAttachmentsDTO;
import com.armedia.acm.service.outlook.model.EmailWithEmbeddedLinksDTO;
import com.armedia.acm.service.outlook.model.EmailWithEmbeddedLinksResultDTO;
import com.armedia.acm.service.outlook.model.MessageBodyFactory;
import com.armedia.acm.services.authenticationtoken.model.AuthenticationToken;
import com.armedia.acm.services.authenticationtoken.model.AuthenticationTokenConstants;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.model.NotificationConstants;
import com.armedia.acm.services.notification.model.SmtpEventSentEvent;
import com.armedia.acm.services.users.model.AcmUser;

import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.core.Authentication;

import javax.activation.DataHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class SmtpNotificationSender extends NotificationSender implements ApplicationEventPublisherAware
{

    private final Logger LOG = LoggerFactory.getLogger(getClass());
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

            String notificationLink = getNotificationUtils().buildNotificationLink(notification.getParentType(), notification.getParentId(),
                    notification.getRelatedObjectType(), notification.getRelatedObjectId());

            String messageBody = notificationLink != null ? String.format("%s Link: %s", notification.getNote(), notificationLink)
                    : notification.getNote();

            messageBody = new MessageBodyFactory(notificationTemplate).buildMessageBodyWithoutHeaderFromTemplate(messageBody, "");
            MuleMessage received = getMuleContextManager().send(flow, messageBody, messageProps);

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
    public <T> void sendPlainEmail(Stream<T> emailsDataStream, EmailBuilder<T> emailBuilder, EmailBodyBuilder<T> emailBodyBuilder)
            throws AcmEncryptionException
    {

        Map<String, Object> messageProps = loadSmtpAndOriginatingProperties();

        emailsDataStream.forEach(emailData -> {
            emailBuilder.buildEmail(emailData, messageProps);
            Exception exception = null;
            try
            {
                MuleMessage received = getMuleContextManager().send(flow, emailBodyBuilder.buildEmailBody(emailData), messageProps);
                exception = received.getInboundProperty("sendEmailException");
            } catch (Exception e)
            {
                exception = e;
            }

            if (exception != null)
            {
                LOG.error("Email message not sent ...", exception);
            }

        });

    }

    @Override
    public void sendEmailWithAttachments(EmailWithAttachmentsDTO in, Authentication authentication, AcmUser user) throws Exception
    {

        in.setTemplate(notificationTemplate);
        Exception exception = null;
        Map<String, Object> messageProps = loadSmtpAndOriginatingProperties();
        messageProps.put("subject", in.getSubject());

        boolean firstIteration = true;
        for (String emailAddress : in.getEmailAddresses())
        {
            List<SmtpEventSentEvent> sentEvents = new ArrayList<>();
            try
            {
                messageProps.put("to", emailAddress);
                Map<String, DataHandler> attachments = new HashMap<>();
                if (in.getAttachmentIds() != null && !in.getAttachmentIds().isEmpty())
                {
                    for (Long attachmentId : in.getAttachmentIds())
                    {
                        InputStream contents = getEcmFileService().downloadAsInputStream(attachmentId);
                        EcmFile ecmFile = getEcmFileService().findById(attachmentId);
                        String fileName = ecmFile.getFileName();
                        if (ecmFile.getFileActiveVersionNameExtension() != null)
                        {
                            fileName = fileName + ecmFile.getFileActiveVersionNameExtension();
                        }
                        attachments.put(fileName, new DataHandler(new InputStreamDataSource(contents, fileName)));

                        if (firstIteration)
                        {
                            sentEvents.add(new SmtpEventSentEvent(ecmFile, user.getUserId(), ecmFile.getParentObjectId(),
                                    ecmFile.getParentObjectType()));
                            sentEvents.add(new SmtpEventSentEvent(ecmFile, user.getUserId(), ecmFile.getId(), ecmFile.getObjectType()));
                        }
                    }
                }
                // Adding non ecmFile(s) as attachments
                if (in.getFilePaths() != null && !in.getFilePaths().isEmpty())
                {
                    for (String filePath : in.getFilePaths())
                    {
                        File file = new File(filePath);
                        FileInputStream contents = new FileInputStream(file);
                        attachments.put(file.getName(), new DataHandler(new InputStreamDataSource(contents, file.getName())));
                    }
                }
                MuleMessage received = getMuleContextManager().send(flow, in.getMessageBody(), attachments, messageProps);
                exception = received.getInboundProperty("sendEmailException");

            } catch (MuleException e)
            {
                exception = e;
            }

            if (exception != null)
            {
                LOG.error("Email message not sent ...", exception);
            }

            if (firstIteration)
            {
                for (SmtpEventSentEvent event : sentEvents)
                {
                    boolean success = (exception == null);
                    event.setSucceeded(success);
                    eventPublisher.publishEvent(event);
                }
                firstIteration = false;
            }
        }

    }

    @Override
    public List<EmailWithEmbeddedLinksResultDTO> sendEmailWithEmbeddedLinks(EmailWithEmbeddedLinksDTO in, Authentication authentication,
            AcmUser user) throws Exception
    {
        in.setTemplate(notificationTemplate);
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

            if (exception != null)
            {
                emailResultList.add(new EmailWithEmbeddedLinksResultDTO(emailAddress, false));
                LOG.error("Email message not sent ...", exception);
            } else
            {
                emailResultList.add(new EmailWithEmbeddedLinksResultDTO(emailAddress, true));
            }

        }

        SmtpEventSentEvent event = new SmtpEventSentEvent(in, user.getUserId());
        boolean success = (exception == null);
        event.setSucceeded(success);
        eventPublisher.publishEvent(event);

        return emailResultList;
    }

    protected Map<String, Object> loadSmtpAndOriginatingProperties() throws AcmEncryptionException
    {
        Map<String, Object> messageProps = new HashMap<>();

        messageProps.put("host",
                getPropertyFileManager().load(getEmailSenderPropertyFileLocation(), NotificationConstants.EMAIL_HOST_KEY, null));
        messageProps.put("port",
                getPropertyFileManager().load(getEmailSenderPropertyFileLocation(), NotificationConstants.EMAIL_PORT_KEY, null));
        messageProps.put("user",
                getPropertyFileManager().load(getEmailSenderPropertyFileLocation(), NotificationConstants.EMAIL_USER_KEY, null));
        messageProps.put("password",
                getPropertyFileManager().load(getEmailSenderPropertyFileLocation(), NotificationConstants.EMAIL_PASSWORD_KEY, null));
        messageProps.put("from",
                getPropertyFileManager().load(getEmailSenderPropertyFileLocation(), NotificationConstants.EMAIL_FROM_KEY, null));

        return messageProps;
    }

    protected String makeNote(String emailAddress, EmailWithEmbeddedLinksDTO emailWithEmbeddedLinksDTO, Authentication authentication)
            throws AcmEncryptionException
    {
        String body = "";
        for (Long fileId : emailWithEmbeddedLinksDTO.getFileIds())
        {
            String token = generateAndSaveAuthenticationToken(fileId, emailAddress, authentication);
            body += " http://" + emailWithEmbeddedLinksDTO.getBaseUrl() + fileId + "&acm_email_ticket=" + token + "\n";
        }
        return emailWithEmbeddedLinksDTO.buildMessageBodyFromTemplate(body);
    }

    protected String generateAndSaveAuthenticationToken(Long fileId, String emailAddress, Authentication authentication)
    {
        String token = getAuthenticationTokenService().getUncachedTokenForAuthentication(authentication);
        saveAuthenticationToken(emailAddress, fileId, token);
        return token;
    }

    protected void saveAuthenticationToken(String email, Long fileId, String token)
    {
        AuthenticationToken authenticationToken = new AuthenticationToken();
        authenticationToken.setKey(token);
        authenticationToken.setStatus(AuthenticationTokenConstants.ACTIVE);
        authenticationToken.setEmail(email);
        authenticationToken.setFileId(fileId);
        getAuthenticationTokenDao().save(authenticationToken);
    }

}