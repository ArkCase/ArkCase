package com.armedia.acm.services.email.smtp;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.core.model.AcmEvent;
import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.email.model.EmailReceiverConfig;
import com.armedia.acm.files.capture.CaptureConfig;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.EcmFileService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import javax.mail.internet.MimeMessage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class TrackOutgoingEmailService implements ApplicationEventPublisherAware
{
    private final Logger log = LogManager.getLogger(getClass());
    private AcmFolderService acmFolderService;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;
    private EmailReceiverConfig emailReceiverConfig;
    private EcmFileService ecmFileService;
    private EcmFileDao ecmFileDao;
    private ApplicationEventPublisher eventPublisher;
    private CaptureConfig captureConfig;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        eventPublisher = applicationEventPublisher;
    }

    public void trackEmail(MimeMessage message, String emailAddress, String subject, String objectType, String objectId,
            List<InputStreamDataSource> attachments)
    {
        String tempDir = System.getProperty("java.io.tmpdir");
        ZonedDateTime date = ZonedDateTime.now(ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String currentDate = formatter.format(date);
        String messageFileName = emailAddress + "-" + currentDate + "-" + subject.replaceAll(":", "_") + ".eml";
        File messageFile = new File(tempDir + File.separator + messageFileName);
        Exception exception = null;

        String userId = emailReceiverConfig.getEmailUserId();
        auditPropertyEntityAdapter.setUserId(userId);

        Authentication auth = new UsernamePasswordAuthenticationToken(userId, "");
        EcmFile mailFile = null;

        try
        {
            try (OutputStream os = new FileOutputStream(messageFile))
            {
                message.writeTo(os);
            }

            AcmFolder folder = getAcmFolderService().addNewFolderByPath(objectType, Long.parseLong(objectId), captureConfig.getOutgoingEmailFolderName());
            try (InputStream is = new FileInputStream(messageFile))
            {
                messageFileName = checkDuplicateFileName(messageFileName, folder.getId());
                mailFile = getEcmFileService().upload(messageFileName, "mail", "Document", is, "message/rfc822", messageFileName, auth,
                        folder.getCmisFolderId(), objectType, Long.parseLong(objectId));

            }
            if (attachments != null)
            {
                attachments.forEach(attachment -> {
                    try (InputStream is = attachment.getInputStream())
                    {
                        String attachmentFileName = checkDuplicateFileName(attachment.getName(), folder.getId());
                        getEcmFileService().upload(attachmentFileName, "attachment", "Document", is, attachment.getContentType(),
                                attachmentFileName, auth,
                                folder.getCmisFolderId(), objectType, Long.parseLong(objectId));
                    }
                    catch (IOException | AcmUserActionFailedException | AcmCreateObjectFailedException e)
                    {
                        log.error("Failed to upload attachments to Outgoing Email folder for object with ID '{}' ", objectId,
                                e.getMessage());
                    }
                });
            }

            SmtpEventMailSent event = new SmtpEventMailSent(emailAddress, userId, mailFile.getId(), mailFile.getObjectType(), Long.parseLong(objectId),
                    objectType, null);
            event.setSucceeded(true);
            eventPublisher.publishEvent(event);
            
        }
        catch (Exception e)
        {
            log.error("Failed to upload mail into object with ID '{}'. Exception msg: '{}' ", objectId, e.getMessage());
            exception = e;
        }

    }

    private String checkDuplicateFileName(String fileName, Long folderId)
    {
        int endIndex = fileName.lastIndexOf(".");
        String newFileName = fileName.substring(0, endIndex);
        Optional<EcmFile> sameFilesName = getEcmFileDao().findByFolderId(folderId).stream()
                .filter(obj -> obj.getFileName().equals(fileName.substring(0, endIndex)))
                .findAny();
        if (sameFilesName.isPresent())
        {
            ZonedDateTime date = ZonedDateTime.now(ZoneOffset.UTC);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            String timestampName = formatter.format(date);
            newFileName = newFileName + "-" + timestampName;
        }
        return newFileName;
    }

    public AcmFolderService getAcmFolderService()
    {
        return acmFolderService;
    }

    public void setAcmFolderService(AcmFolderService acmFolderService)
    {
        this.acmFolderService = acmFolderService;
    }

    public AuditPropertyEntityAdapter getAuditPropertyEntityAdapter()
    {
        return auditPropertyEntityAdapter;
    }

    public void setAuditPropertyEntityAdapter(AuditPropertyEntityAdapter auditPropertyEntityAdapter)
    {
        this.auditPropertyEntityAdapter = auditPropertyEntityAdapter;
    }

    public EmailReceiverConfig getEmailReceiverConfig()
    {
        return emailReceiverConfig;
    }

    public void setEmailReceiverConfig(EmailReceiverConfig emailReceiverConfig)
    {
        this.emailReceiverConfig = emailReceiverConfig;
    }

    public EcmFileService getEcmFileService()
    {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
    }

    public EcmFileDao getEcmFileDao()
    {
        return ecmFileDao;
    }

    public void setEcmFileDao(EcmFileDao ecmFileDao)
    {
        this.ecmFileDao = ecmFileDao;
    }

    public CaptureConfig getCaptureConfig() 
    {
        return captureConfig;
    }

    public void setCaptureConfig(CaptureConfig captureConfig) 
    {
        this.captureConfig = captureConfig;
    }
}
