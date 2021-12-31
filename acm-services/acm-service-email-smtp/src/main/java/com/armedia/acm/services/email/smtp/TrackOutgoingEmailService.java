package com.armedia.acm.services.email.smtp;

/*-
 * #%L
 * ACM Service: Email SMTP
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.email.model.EmailReceiverConfig;
import com.armedia.acm.email.model.EmailSenderConfig;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.EcmFileService;

import com.armedia.acm.service.EMLToPDFConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.UUID;

public class TrackOutgoingEmailService implements ApplicationEventPublisherAware
{
    private final Logger log = LogManager.getLogger(getClass());
    private AcmFolderService acmFolderService;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;
    private EmailReceiverConfig emailReceiverConfig;
    private EcmFileService ecmFileService;
    private EcmFileDao ecmFileDao;
    private ApplicationEventPublisher eventPublisher;
    private EmailSenderConfig emailSenderConfig;
    private EMLToPDFConverter emlToPDFConverter;

    @Value("${convertEmailsToPDF.outgoingEmailToPdf:false}")
    private Boolean convertFileToPdf;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        eventPublisher = applicationEventPublisher;
    }

    public void trackEmail(MimeMessage message, String emailAddress, String group, String subject, String objectType, String objectId,
            List<InputStreamDataSource> attachments, String ccEmailAddress, String bccEmailAddress)
    {
        if (objectType != null && objectType.equals("USER"))
        {
            log.warn("Outgoing emails for objectType:USER are not tracked.");
            return;
        }

        String tempDir = System.getProperty("java.io.tmpdir");
        ZonedDateTime date = ZonedDateTime.now(ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String currentDate = formatter.format(date);
        String messageFileName = currentDate + "-" + UUID.randomUUID() + ".eml";
        // email address might be multiple emails concatenated with a comma. Se we cut the email address not to exceed
        // filename limit of 255 characters
        String email = group != null ? group : emailAddress + "," + ccEmailAddress + "," + bccEmailAddress;
        email = email.replaceAll(",,", "");
        File messageFile = new File(tempDir + File.separator + messageFileName);

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

            AcmFolder folder = getAcmFolderService().addNewFolderByPath(objectType, Long.parseLong(objectId),
                    emailSenderConfig.getOutgoingEmailFolderName());
            try (InputStream is = new FileInputStream(messageFile))
            {
                messageFileName = checkDuplicateFileName(messageFileName, folder.getId());
                mailFile = getEcmFileService().upload(messageFileName, "mail", "Document", is, "message/rfc822", messageFileName, auth,
                        folder.getCmisFolderId(), objectType, Long.parseLong(objectId));
                if(convertFileToPdf)
                {
                    try
                    {
                        log.debug("Converting file [{}] to PDF started!", messageFileName);
                        File pdfConvertedFile = emlToPDFConverter.convert(new FileInputStream(messageFile), messageFileName);
                        if (pdfConvertedFile != null && pdfConvertedFile.exists() && pdfConvertedFile.length() > 0)
                        {
                            try (InputStream pdfConvertedFileIs = new FileInputStream(pdfConvertedFile))
                            {
                                mailFile.setFileActiveVersionNameExtension(".pdf");
                                mailFile.setFileActiveVersionMimeType("application/pdf");
                                ecmFileService.update(mailFile, pdfConvertedFileIs, auth);
                                mailFile.getVersions().get(1).setFile(mailFile);
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        log.debug("Converting file [{}] to PDF failed!", messageFileName, e);
                    }
                }

            }

            SmtpEventMailSent event = new SmtpEventMailSent(email, userId, mailFile.getId(), mailFile.getObjectType(),
                    Long.parseLong(objectId),
                    objectType, null);
            event.setSucceeded(true);
            eventPublisher.publishEvent(event);

        }
        // File upload falling should not break the flow
        catch (Exception e)
        {
            log.error("Failed to upload mail into object [{}] with ID [{}]. Exception msg: '{}' ", objectType, objectId, e.getMessage());
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
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssZ");
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

    public EmailSenderConfig getEmailSenderConfig()
    {
        return emailSenderConfig;
    }

    public void setEmailSenderConfig(EmailSenderConfig emailSenderConfig)
    {
        this.emailSenderConfig = emailSenderConfig;
    }

    public EMLToPDFConverter getEmlToPDFConverter()
    {
        return emlToPDFConverter;
    }

    public void setEmlToPDFConverter(EMLToPDFConverter emlToPDFConverter)
    {
        this.emlToPDFConverter = emlToPDFConverter;
    }
}
