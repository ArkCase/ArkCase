package com.armedia.acm.services.email.smtp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

/*-
 * #%L
 * ACM Service: Email SMTP
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
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

import com.armedia.acm.auth.AuthenticationUtils;
import com.armedia.acm.convertfolder.ConversionException;
import com.armedia.acm.convertfolder.DefaultFolderAndFileConverter;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.core.model.AcmEvent;
import com.armedia.acm.email.model.EmailSenderConfig;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.services.email.model.AttachmentsProcessableDTO;
import com.armedia.acm.services.email.model.EmailBodyBuilder;
import com.armedia.acm.services.email.model.EmailBuilder;
import com.armedia.acm.services.email.model.EmailWithAttachmentsAndLinksDTO;
import com.armedia.acm.services.email.model.EmailWithAttachmentsDTO;
import com.armedia.acm.services.email.model.EmailWithEmbeddedLinksDTO;
import com.armedia.acm.services.email.model.EmailWithEmbeddedLinksResultDTO;
import com.armedia.acm.services.email.service.AcmEmailContentGeneratorService;
import com.armedia.acm.services.email.service.AcmEmailSenderService;
import com.armedia.acm.services.email.service.TemplatingEngine;
import com.armedia.acm.services.users.model.AcmUser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mule.util.FileUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.core.Authentication;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jun 21, 2017
 */
public class SmtpService implements AcmEmailSenderService, ApplicationEventPublisherAware
{

    private final Logger log = LogManager.getLogger(getClass());

    private EcmFileService ecmFileService;

    private ApplicationEventPublisher eventPublisher;

    private AcmEmailContentGeneratorService acmEmailContentGeneratorService;

    private EmailSenderConfig emailSenderConfig;

    private TemplatingEngine templatingEngine;

    private DefaultFolderAndFileConverter defaultFolderAndFileConverter;

    private AcmMailSender acmMailSender;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        eventPublisher = applicationEventPublisher;
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.services.email.service.AcmEmailSenderService#sendEmail(java.util.stream.Stream,
     * com.armedia.acm.services.email.model.EmailBuilder, com.armedia.acm.services.email.model.EmailBodyBuilder)
     */
    @Override
    public <T> void sendPlainEmail(Stream<T> emailsDataStream, EmailBuilder<T> emailBuilder, EmailBodyBuilder<T> emailBodyBuilder)
    {
        emailsDataStream.forEach(emailData -> {
            Map<String, Object> messageProps = new HashMap<>();
            emailBuilder.buildEmail(emailData, messageProps);
            String recipient = (String) messageProps.get("to");
            String subject = (String) messageProps.get("subject");
            try
            {
                acmMailSender.sendEmail(recipient, subject, emailBodyBuilder.buildEmailBody(emailData));
            }
            catch (Exception e)
            {
                log.error("Failed to send mail to [{}].", recipient, e);
            }
        });
    }

    /*
     * (non-Javadoc)
     * @see
     * com.armedia.acm.services.email.service.AcmEmailSenderService#sendEmailWithAttachments(com.armedia.acm.services.
     * email.model.EmailWithAttachmentsDTO, org.springframework.security.core.Authentication,
     * com.armedia.acm.services.users.model.AcmUser)
     */
    @Override
    public void sendEmailWithAttachments(EmailWithAttachmentsDTO in, Authentication authentication, AcmUser user)
            throws Exception
    {
        in.setTemplatingEngine(getTemplatingEngine());

        Exception exception = null;

        List<AcmEvent> sentEvents = new ArrayList<>();
        Map<String, InputStreamDataSource> attachments = processAttachments(in, user, sentEvents);
        if(attachments.size() == 0)
        {
            sentEvents.add(new SmtpEventMailSent(in,user.getUserId(),in.getObjectId(),in.getObjectType(),AuthenticationUtils.getUserIpAddress()));
        }
        for (String emailAddress : in.getEmailAddresses())
        {
            try
            {
                acmMailSender.sendMultipartEmail(emailAddress, in.getSubject(), in.getMessageBody(),
                        new ArrayList<>(attachments.values()));
            }
            catch (Exception e)
            {
                exception = e;
                log.error("Failed to send email to [{}].", emailAddress, exception);
            }
            in.setMailSent(exception == null);
        }
        for (AcmEvent event : sentEvents)
        {
            boolean success = (exception == null);
            event.setSucceeded(success);
            eventPublisher.publishEvent(event);
        }
    }

    /*
     * (non-Javadoc)
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
     * @see
     * com.armedia.acm.services.email.service.AcmEmailSenderService#sendEmailWithAttachmentsAndLinks(com.armedia.acm.
     * services.email.model.EmailWithAttachmentsAndLinksDTO, org.springframework.security.core.Authentication,
     * com.armedia.acm.services.users.model.AcmUser)
     */
    @Override
    public void sendEmailWithAttachmentsAndLinks(EmailWithAttachmentsAndLinksDTO in, Authentication authentication, AcmUser user)
            throws Exception
    {
        Exception exception = null;

        setFilenames(in);

        List<AcmEvent> sentEvents = new ArrayList<>();
        Map<String, InputStreamDataSource> attachments = processAttachments(in, user, sentEvents);

        for (String emailAddress : in.getEmailAddresses())
        {
            try
            {
                acmMailSender.sendMultipartEmail(emailAddress, in.getSubject(),
                        makeNote(emailAddress, in, authentication), new ArrayList<>(attachments.values()));
            }
            catch (Exception e)
            {
                exception = e;
                log.error("Failed to send email to [{}].", emailAddress, exception);
            }
        }
        in.setMailSent(exception == null);
        for (AcmEvent event : sentEvents)
        {
            boolean success = (exception == null);
            event.setSucceeded(success);
            eventPublisher.publishEvent(event);
        }
    }

    private Long setFilenames(EmailWithEmbeddedLinksDTO in)
    {
        EcmFile ecmFile = null;
        List<String> fileNames = new ArrayList<>();

        if (Objects.nonNull(in.getFileIds()) && in.getFileIds().size() > 0)
        {
            for (int i = 0; i < in.getFileIds().size(); i++)
            {
                ecmFile = ecmFileService.findById(in.getFileIds().get(i));
                fileNames.add(ecmFile.getFileName() + ecmFile.getFileActiveVersionNameExtension());
            }
            in.setFileNames(fileNames);
            return ecmFile.getParentObjectId();
        }

        return null;
    }

    /**
     * @param in
     * @param user
     *            *
     * @param sentEvents
     * @return
     * @throws AcmUserActionFailedException
     * @throws FileNotFoundException
     */
    private Map<String, InputStreamDataSource> processAttachments(AttachmentsProcessableDTO in, AcmUser user,
            List<AcmEvent> sentEvents)
            throws AcmUserActionFailedException, FileNotFoundException
    {
        Map<String, InputStreamDataSource> attachments = new HashMap<>();
        if (in.getAttachmentIds() != null && !in.getAttachmentIds().isEmpty())
        {
            // add index to make sure the fileKey is unique for AFDP- 5713
            int idx = 1;
            for (Long attachmentId : in.getAttachmentIds())
            {
                EcmFile ecmFile = ecmFileService.findById(attachmentId);
                InputStream contents = ecmFileService.downloadAsInputStream(attachmentId, ecmFile.getActiveVersionTag());
                String fileName = ecmFile.getFileName();
                File pdfConvertedFile = null;

                // add fileKey for AFDP- 5713
                String fileKey = fileName;
                if (attachments.containsKey(fileKey))
                {
                    fileKey = fileKey + "(" + idx + ")";
                    idx++;
                }

                if (ecmFile.getFileActiveVersionNameExtension() != null)
                {

                    fileName = fileName + ecmFile.getFileActiveVersionNameExtension();
                }

                // Convert the Attachment to PDF if set in Admin section
                if (emailSenderConfig.getConvertDocumentsToPdf() && !".pdf"
                        .equals(ecmFile.getFileActiveVersionNameExtension()))
                {
                    try
                    {
                        pdfConvertedFile = getDefaultFolderAndFileConverter().convertAndReturnConvertedFile(ecmFile);
                    }
                    catch (ConversionException e)
                    {
                        log.error("Could not convert file [{}] to PDF", fileName, e);
                    }

                    if (pdfConvertedFile != null)
                    {

                        try (InputStream pdfConvertedFileInputStream = new FileInputStream(pdfConvertedFile))
                        {
                            contents = pdfConvertedFileInputStream;
                            fileName = fileKey.concat(".pdf");
                            attachments.put(fileKey, new InputStreamDataSource(contents, fileName));
                        }
                        catch (IOException e)
                        {
                            log.error("Could not open input stream of file [{}]", fileName, e);
                        }
                        finally
                        {
                            FileUtils.deleteQuietly(pdfConvertedFile);
                        }
                    }
                    else
                    {
                        attachments.put(fileKey, new InputStreamDataSource(contents, fileName));
                    }
                }
                else
                {
                    attachments.put(fileKey, new InputStreamDataSource(contents, fileName));
                }

                String ipAddress = AuthenticationUtils.getUserIpAddress();
                for (String mailAddress : in.getEmailAddresses())
                {
                    ecmFile.setMailAddress(mailAddress);
                }
                sentEvents
                        .add(new SmtpEventSentEvent(ecmFile, user.getUserId(), ecmFile.getParentObjectId(), ecmFile.getParentObjectType(),
                                ipAddress));
                sentEvents.add(new SmtpEventSentEvent(ecmFile, user.getUserId(), ecmFile.getId(), ecmFile.getObjectType(), ipAddress));
            }
        }

        // Adding non ecmFile(s) as attachments
        if (in.getFilePaths() != null && !in.getFilePaths().isEmpty())
        {
            for (String filePath : in.getFilePaths())
            {
                File file = new File(filePath);
                FileInputStream contents = new FileInputStream(file);
                attachments.put(file.getName(), new InputStreamDataSource(contents, file.getName()));
            }
        }
        return attachments;
    }

    /*
     * (non-Javadoc)
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
     * @see
     * com.armedia.acm.services.email.service.AcmEmailSenderService#sendEmailWithEmbeddedLinks(com.armedia.acm.services.
     * email.model.EmailWithEmbeddedLinksDTO, org.springframework.security.core.Authentication,
     * com.armedia.acm.services.users.model.AcmUser)
     */
    @Override
    public List<EmailWithEmbeddedLinksResultDTO> sendEmailWithEmbeddedLinks(EmailWithEmbeddedLinksDTO in, Authentication authentication,
            AcmUser user)
    {
        List<EmailWithEmbeddedLinksResultDTO> emailResultList = new ArrayList<>();
        Exception exception = null;

        Long parentId = setFilenames(in);

        for (String emailAddress : in.getEmailAddresses())
        {
            try
            {
                acmMailSender.sendEmail(emailAddress, in.getSubject(), makeNote(emailAddress, in, authentication));
            }
            catch (Exception e)
            {
                exception = e;
            }

            if (exception != null)
            {
                emailResultList.add(new EmailWithEmbeddedLinksResultDTO(emailAddress, false));
                log.error("Failed to send email to [{}].", emailAddress, exception);
            }
            else
            {
                emailResultList.add(new EmailWithEmbeddedLinksResultDTO(emailAddress, true));
            }
            AcmEvent event = null;
            if (in.getModelReferenceName().equals("plainEmail"))
            {

                event = new SmtpEventMailSent(in, user.getUserId(),Long.parseLong(in.getParentNumber()),
                        in.getParentType(), null);
            }
            else
            {
                event = new SmtpSentEventHyperlink(in, user.getUserId(), parentId, in.getParentType());
            }
            boolean success = (exception == null);
            event.setSucceeded(success);
            eventPublisher.publishEvent(event);
        }
        return emailResultList;
    }

    private String makeNote(String emailAddress, EmailWithEmbeddedLinksDTO emailWithEmbeddedLinksDTO, Authentication authentication)
    {
        return getAcmEmailContentGeneratorService().generateEmailBody(emailWithEmbeddedLinksDTO, emailAddress, authentication);
    }

    /**
     * @param ecmFileService
     *            the ecmFileService to set
     */
    public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
    }

    public AcmEmailContentGeneratorService getAcmEmailContentGeneratorService()
    {
        return acmEmailContentGeneratorService;
    }

    public void setAcmEmailContentGeneratorService(AcmEmailContentGeneratorService acmEmailContentGeneratorService)
    {
        this.acmEmailContentGeneratorService = acmEmailContentGeneratorService;
    }

    public EmailSenderConfig getEmailSenderConfig()
    {
        return emailSenderConfig;
    }

    public void setEmailSenderConfig(EmailSenderConfig emailSenderConfig)
    {
        this.emailSenderConfig = emailSenderConfig;
    }

    public TemplatingEngine getTemplatingEngine()
    {
        return templatingEngine;
    }

    public void setTemplatingEngine(TemplatingEngine templatingEngine)
    {
        this.templatingEngine = templatingEngine;
    }

    public DefaultFolderAndFileConverter getDefaultFolderAndFileConverter()
    {
        return defaultFolderAndFileConverter;
    }

    public void setDefaultFolderAndFileConverter(DefaultFolderAndFileConverter defaultFolderAndFileConverter)
    {
        this.defaultFolderAndFileConverter = defaultFolderAndFileConverter;
    }

    public AcmMailSender getAcmMailSender()
    {
        return acmMailSender;
    }

    public void setAcmMailSender(AcmMailSender acmMailSender)
    {
        this.acmMailSender = acmMailSender;
    }
}
