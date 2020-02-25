package gov.foia.pipeline.postsave;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
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

import static gov.foia.model.FOIAConstants.EMAIL_BODY_ATTACHMENT;
import static gov.foia.model.FOIAConstants.EMAIL_FOOTER_ATTACHMENT;
import static gov.foia.model.FOIAConstants.EMAIL_HEADER_ATTACHMENT;
import static gov.foia.model.FOIARequestUtils.extractRequestorEmailAddress;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.casefile.pipeline.CaseFilePipelineContext;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.services.email.model.EmailWithAttachmentsDTO;
import com.armedia.acm.services.labels.service.TranslationService;
import com.armedia.acm.services.notification.dao.NotificationDao;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.model.NotificationConstants;
import com.armedia.acm.services.notification.service.NotificationSender;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;

import gov.foia.model.FOIAConstants;
import gov.foia.model.FOIADocumentDescriptor;
import gov.foia.model.FOIARequest;
import gov.foia.service.DocumentGenerator;
import gov.foia.service.DocumentGeneratorException;
import gov.foia.service.FOIADocumentGeneratorService;

public class FOIAExtensionEmailHandler implements PipelineHandler<FOIARequest, CaseFilePipelineContext>
{
    private transient final Logger log = LogManager.getLogger(getClass());

    private static final String NEW_EXTENSION_FILE = "NEW_EXTENSION_FILE";
    private static final String EXTENSION_FILE_ID = "EXTENSION_FILE_ID";

    private DocumentGenerator documentGenerator;
    private FOIADocumentGeneratorService documentGeneratorService;
    private EcmFileService ecmFileService;
    private NotificationSender notificationSender;
    private UserDao userDao;
    private NotificationDao notificationDao;
    private TranslationService translationService;

    @Override
    public void execute(FOIARequest entity, CaseFilePipelineContext pipelineContext)
            throws PipelineProcessException
    {
        log.debug("FOIARequest extension post save handler called for RequestId={}", entity.getId());

        if (pipelineContext.getPropertyValue(FOIAConstants.FOIA_PIPELINE_EXTENSION_PROPERTY_KEY) != null)
        {
            log.debug("Generating extension document");
            EcmFile file = null;
            EcmFileVersion ecmFileVersion;
            try
            {
                file = generateExtensionDocument(entity, pipelineContext);
                ecmFileVersion = file.getVersions().get(file.getVersions().size() - 1);
            }
            catch (Exception e)
            {
                throw new PipelineProcessException(e);
            }

            log.debug("Emailing extension document");

            AcmUser user = userDao.findByUserId(entity.getAssigneeLdapId());
            String emailAddress = extractRequestorEmailAddress(entity.getOriginator().getPerson());

            Notification notification = new Notification();
            notification.setTemplateModelName("requestExtension");
            notification.setParentType(entity.getObjectType());
            notification.setParentId(entity.getId());
            notification.setTitle(String.format(translationService.translate(NotificationConstants.NOTIFICATION_FOIA_EXTENSION), entity.getCaseNumber()));
            notification.setAttachFiles(true);
            notification.setEmailAddresses(emailAddress);
            notification.setFiles(Arrays.asList(ecmFileVersion));
            notification.setUser(user.getUserId());
            notificationDao.save(notification);

        }

        log.debug("FOIARequest extension post save handler ended for RequestId={}", entity.getId());
    }

    @Override
    public void rollback(FOIARequest entity, CaseFilePipelineContext pipelineContext) throws PipelineProcessException
    {
        if (pipelineContext.getPropertyValue(NEW_EXTENSION_FILE) != null)
        {
            // delete created and uploaded file from Alfresco
            if (pipelineContext.hasProperty(EXTENSION_FILE_ID))
            {
                Long fileId = (Long) pipelineContext.getPropertyValue(EXTENSION_FILE_ID);
                try
                {
                    getEcmFileService().deleteFile(fileId);
                }
                catch (AcmUserActionFailedException | AcmObjectNotFoundException e)
                {
                    log.warn("Unable to delete ecm file with id [{}]", fileId);
                    throw new PipelineProcessException(e);
                }
            }
        }
    }

    private EcmFile generateExtensionDocument(FOIARequest entity, CaseFilePipelineContext pipelineContext)
            throws PipelineProcessException
    {
        FOIADocumentDescriptor documentDescriptor = getDocumentGeneratorService().getDocumentDescriptor(entity,
                FOIAConstants.REQ_EXTENSION);
        String arkcaseFilename = String.format(documentDescriptor.getFilenameFormat(), entity.getId());
        String targetFolderId = entity.getContainer().getAttachmentFolder() == null
                ? entity.getContainer().getFolder().getCmisFolderId()
                : entity.getContainer().getAttachmentFolder().getCmisFolderId();

        EcmFile ecmFile = null;
        try
        {
            ecmFile = getDocumentGenerator().generateAndUpload(documentDescriptor, entity, targetFolderId, arkcaseFilename,
                    getDocumentGeneratorService().getReportSubstitutions(entity));

            if (pipelineContext != null)
            {
                pipelineContext.addProperty(NEW_EXTENSION_FILE, true);
                pipelineContext.addProperty(EXTENSION_FILE_ID, ecmFile.getId());
            }
        }
        catch (DocumentGeneratorException e)
        {
            throw new PipelineProcessException(e);
        }

        return ecmFile;
    }

    private void emailRequestExtension(FOIARequest request, EcmFile file)
    {
        String emailAddress = extractRequestorEmailAddress(request.getOriginator().getPerson());
        if (emailAddress != null)
        {
            EmailWithAttachmentsDTO emailData = new EmailWithAttachmentsDTO();
            emailData.setEmailAddresses(Arrays.asList(emailAddress));

            emailData.setSubject(String.format("%s %s", FOIAConstants.EMAIL_HEADER_SUBJECT, request.getCaseNumber()));
            emailData.setHeader(EMAIL_HEADER_ATTACHMENT);
            emailData.setBody(EMAIL_BODY_ATTACHMENT);
            emailData.setFooter(EMAIL_FOOTER_ATTACHMENT);
            emailData.setAttachmentIds(Arrays.asList(file.getFileId()));
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            String userIdOrName = request.getCreator();
            try
            {
                if (request.isExternal()) // request from external portal
                {
                    getNotificationSender().sendEmailWithAttachments(emailData, auth, userIdOrName);
                }
                else // request from foia app
                {
                    getNotificationSender().sendEmailWithAttachments(emailData, auth, getUserDao().findByUserId(userIdOrName));
                }

            }
            catch (Exception e)
            {
                log.error("Unable to email extension letter for RequestId={}", request.getId(), e);
            }
        }
    }

    public FOIADocumentGeneratorService getDocumentGeneratorService()
    {
        return documentGeneratorService;
    }

    public void setDocumentGeneratorService(FOIADocumentGeneratorService documentGeneratorService)
    {
        this.documentGeneratorService = documentGeneratorService;
    }

    public DocumentGenerator getDocumentGenerator()
    {
        return documentGenerator;
    }

    public void setDocumentGenerator(DocumentGenerator documentGenerator)
    {
        this.documentGenerator = documentGenerator;
    }

    public EcmFileService getEcmFileService()
    {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
    }

    public NotificationSender getNotificationSender()
    {
        return notificationSender;
    }

    public void setNotificationSender(NotificationSender notificationSender)
    {
        this.notificationSender = notificationSender;
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public NotificationDao getNotificationDao()
    {
        return notificationDao;
    }

    public void setNotificationDao(NotificationDao notificationDao)
    {
        this.notificationDao = notificationDao;
    }

    public TranslationService getTranslationService()
    {
        return translationService;
    }

    public void setTranslationService(TranslationService translationService)
    {
        this.translationService = translationService;
    }
}
