package com.armedia.acm.services.notification.service;

import com.armedia.acm.core.exceptions.AcmEncryptionException;
import com.armedia.acm.files.propertymanager.PropertyFileManager;
import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.services.authenticationtoken.dao.AuthenticationTokenDao;
import com.armedia.acm.services.authenticationtoken.model.AuthenticationToken;
import com.armedia.acm.services.authenticationtoken.model.AuthenticationTokenConstants;
import com.armedia.acm.services.authenticationtoken.service.AuthenticationTokenService;
import com.armedia.acm.services.email.model.EmailBodyBuilder;
import com.armedia.acm.services.email.model.EmailBuilder;
import com.armedia.acm.services.email.model.EmailWithAttachmentsAndLinksDTO;
import com.armedia.acm.services.email.model.EmailWithAttachmentsDTO;
import com.armedia.acm.services.email.model.EmailWithEmbeddedLinksDTO;
import com.armedia.acm.services.email.model.EmailWithEmbeddedLinksResultDTO;
import com.armedia.acm.services.email.sender.model.EmailSenderConfigurationConstants;
import com.armedia.acm.services.email.service.AcmEmailSenderService;
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

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jun 21, 2017
 *
 */
public class SmtpService implements AcmEmailSenderService, ApplicationEventPublisherAware
{

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private PropertyFileManager propertyFileManager;

    private String emailSenderPropertyFileLocation;

    private EcmFileService ecmFileService;

    private MuleContextManager muleContextManager;

    private ApplicationEventPublisher eventPublisher;

    protected AuthenticationTokenService authenticationTokenService;

    protected AuthenticationTokenDao authenticationTokenDao;

    private String flow = "vm://sendEmailViaSmtp.in";

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        eventPublisher = applicationEventPublisher;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.armedia.acm.services.email.service.AcmEmailSenderService#sendPlainEmail(java.util.stream.Stream,
     * com.armedia.acm.services.email.model.EmailBuilder, com.armedia.acm.services.email.model.EmailBodyBuilder)
     */
    @Override
    public <T> void sendPlainEmail(Stream<T> emailsDataStream, EmailBuilder<T> emailBuilder, EmailBodyBuilder<T> emailBodyBuilder)
            throws Exception
    {
        Map<String, Object> messageProps = loadSmtpAndOriginatingProperties();

        emailsDataStream.forEach(emailData -> {
            emailBuilder.buildEmail(emailData, messageProps);
            Exception exception = null;
            try
            {
                MuleMessage received = muleContextManager.send(flow, emailBodyBuilder.buildEmailBody(emailData), messageProps);
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

    /*
     * (non-Javadoc)
     *
     * @see
     * com.armedia.acm.services.email.service.AcmEmailSenderService#sendEmailWithAttachments(com.armedia.acm.services.
     * email.model.EmailWithAttachmentsDTO, org.springframework.security.core.Authentication,
     * com.armedia.acm.services.users.model.AcmUser)
     */
    @Override
    public void sendEmailWithAttachments(EmailWithAttachmentsDTO in, Authentication authentication, AcmUser user) throws Exception
    {
        String userId = user.getUserId();
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
                        InputStream contents = ecmFileService.downloadAsInputStream(attachmentId);
                        EcmFile ecmFile = ecmFileService.findById(attachmentId);
                        String fileName = ecmFile.getFileName();
                        if (ecmFile.getFileActiveVersionNameExtension() != null)
                        {
                            fileName = fileName + ecmFile.getFileActiveVersionNameExtension();
                        }
                        attachments.put(fileName, new DataHandler(new InputStreamDataSource(contents, fileName)));

                        if (firstIteration)
                        {
                            sentEvents.add(
                                    new SmtpEventSentEvent(ecmFile, userId, ecmFile.getParentObjectId(), ecmFile.getParentObjectType()));
                            sentEvents.add(new SmtpEventSentEvent(ecmFile, userId, ecmFile.getId(), ecmFile.getObjectType()));
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
                MuleMessage received = muleContextManager.send(flow, in.getMessageBody(), attachments, messageProps);
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

    /*
     * (non-Javadoc)
     *
     * @see com.armedia.acm.services.email.service.AcmEmailSenderService#sendEmail(com.armedia.acm.services.email.model.
     * EmailWithAttachmentsDTO, org.springframework.security.core.Authentication,
     * com.armedia.acm.services.users.model.AcmUser)
     */
    @Override
    public void sendEmail(EmailWithAttachmentsDTO in, Authentication authentication, AcmUser user) throws Exception
    {
        sendEmailWithAttachments(in, authentication, user);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.armedia.acm.services.email.service.AcmEmailSenderService#sendEmailWithAttachmentsAndLinks(com.armedia.acm.
     * services.email.model.EmailWithAttachmentsAndLinksDTO, org.springframework.security.core.Authentication,
     * com.armedia.acm.services.users.model.AcmUser)
     */
    @Override
    public void sendEmailWithAttachmentsAndLinks(EmailWithAttachmentsAndLinksDTO in, Authentication authentication, AcmUser user)
            throws Exception
    {
        Exception exception;

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
                        InputStream contents = ecmFileService.downloadAsInputStream(attachmentId);
                        EcmFile ecmFile = ecmFileService.findById(attachmentId);
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
                MuleMessage received = muleContextManager.send(flow, makeNote(emailAddress, in, authentication), attachments, messageProps);
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

    /*
     * (non-Javadoc)
     *
     * @see com.armedia.acm.services.email.service.AcmEmailSenderService#sendEmail(com.armedia.acm.services.email.model.
     * EmailWithAttachmentsAndLinksDTO, org.springframework.security.core.Authentication,
     * com.armedia.acm.services.users.model.AcmUser)
     */
    @Override
    public void sendEmail(EmailWithAttachmentsAndLinksDTO in, Authentication authentication, AcmUser user) throws Exception
    {
        sendEmailWithAttachmentsAndLinks(in, authentication, user);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.armedia.acm.services.email.service.AcmEmailSenderService#sendEmailWithEmbeddedLinks(com.armedia.acm.services.
     * email.model.EmailWithEmbeddedLinksDTO, org.springframework.security.core.Authentication,
     * com.armedia.acm.services.users.model.AcmUser)
     */
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
                MuleMessage received = muleContextManager.send(flow, makeNote(emailAddress, in, authentication), messageProps);
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

        messageProps.put("host", propertyFileManager.load(emailSenderPropertyFileLocation, EmailSenderConfigurationConstants.HOST, null));
        messageProps.put("port", propertyFileManager.load(emailSenderPropertyFileLocation, EmailSenderConfigurationConstants.PORT, null));
        messageProps.put("user",
                propertyFileManager.load(emailSenderPropertyFileLocation, EmailSenderConfigurationConstants.USERNAME, null));
        messageProps.put("password",
                propertyFileManager.load(emailSenderPropertyFileLocation, EmailSenderConfigurationConstants.PASSWORD, null));
        messageProps.put("from",
                propertyFileManager.load(emailSenderPropertyFileLocation, EmailSenderConfigurationConstants.USER_FROM, null));
        messageProps.put("encryption",
                propertyFileManager.load(emailSenderPropertyFileLocation, EmailSenderConfigurationConstants.ENCRYPTION, null));

        return messageProps;
    }

    private String makeNote(String emailAddress, EmailWithEmbeddedLinksDTO emailWithEmbeddedLinksDTO, Authentication authentication)
            throws AcmEncryptionException
    {
        StringBuilder body = new StringBuilder();
        body.append(emailWithEmbeddedLinksDTO.getBody() != null ? emailWithEmbeddedLinksDTO.getBody() : "").append("<br/>");
        if (emailWithEmbeddedLinksDTO.getFileIds() != null)
        {
            for (Long fileId : emailWithEmbeddedLinksDTO.getFileIds())
            {
                String token = generateAndSaveAuthenticationToken(fileId, emailAddress, authentication);
                body.append(emailWithEmbeddedLinksDTO.getBaseUrl()).append(fileId).append("&acm_email_ticket=").append(token)
                        .append("<br/>");
            }
        }
        return emailWithEmbeddedLinksDTO.buildMessageBodyFromTemplate(body.toString());
    }

    private String generateAndSaveAuthenticationToken(Long fileId, String emailAddress, Authentication authentication)
    {
        String token = authenticationTokenService.getUncachedTokenForAuthentication(authentication);
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
        authenticationTokenDao.save(authenticationToken);
    }

    /**
     * @return the propertyFileManager
     */
    public PropertyFileManager getPropertyFileManager()
    {
        return propertyFileManager;
    }

    /**
     * @param propertyFileManager
     *            the propertyFileManager to set
     */
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

    /**
     * @param ecmFileService
     *            the ecmFileService to set
     */
    public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
    }

    /**
     * @param muleContextManager
     *            the muleContextManager to set
     */
    public void setMuleContextManager(MuleContextManager muleContextManager)
    {
        this.muleContextManager = muleContextManager;
    }

}
