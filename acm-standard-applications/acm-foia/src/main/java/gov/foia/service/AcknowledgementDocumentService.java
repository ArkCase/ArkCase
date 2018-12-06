package gov.foia.service;

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

import com.armedia.acm.core.exceptions.CorrespondenceMergeFieldVersionException;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.services.email.model.EmailWithAttachmentsDTO;
import com.armedia.acm.services.notification.service.NotificationSender;
import com.armedia.acm.services.users.dao.UserDao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;

import gov.foia.dao.FOIARequestDao;
import gov.foia.model.FOIAConstants;
import gov.foia.model.FOIADocumentDescriptor;
import gov.foia.model.FOIARequest;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Aug 17, 2016
 */
public class AcknowledgementDocumentService
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private NotificationSender notificationSender;
    private UserDao userDao;
    private EcmFileDao ecmFileDao;
    private FOIARequestDao requestDao;
    private FOIADocumentGeneratorService documentGeneratorService;
    private DocumentGenerator documentGenerator;
    private FoiaConfigurationService foiaConfigurationService;
    private FOIAQueueCorrespondenceService foiaQueueCorrespondenceService;

    public void emailAcknowledgement(Long requestId)
    {
        if (!foiaConfigurationService.readConfiguration().getReceivedDateEnabled())
        {
            FOIARequest request = getRequestDao().find(requestId);
            String emailAddress = extractRequestorEmailAddress(request.getOriginator().getPerson());
            if (emailAddress != null)
            {
                EmailWithAttachmentsDTO emailData = new EmailWithAttachmentsDTO();
                emailData.setEmailAddresses(Arrays.asList(emailAddress));

                emailData.setSubject(String.format("%s %s", request.getRequestType(), request.getCaseNumber()));
                emailData.setHeader(EMAIL_HEADER_ATTACHMENT);
                emailData.setBody(EMAIL_BODY_ATTACHMENT);
                emailData.setFooter(EMAIL_FOOTER_ATTACHMENT);

                FOIADocumentDescriptor documentDescriptor = documentGeneratorService.getDocumentDescriptor(request, FOIAConstants.ACK);

                EcmFile ackFile = getEcmFileDao().findForContainerAttachmentFolderAndFileType(request.getContainer().getId(),
                        request.getContainer().getAttachmentFolder().getId(), documentDescriptor.getDoctype());
                emailData.setAttachmentIds(Arrays.asList(ackFile.getFileId()));
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
                    log.error("Unable to email {} for {} [{}]", request.getRequestType(), documentDescriptor.getDoctype(), requestId, e);
                }
            }
        }
    }

    public void generateAndUpload(String objectType, Long requestId)
            throws DocumentGeneratorException, CorrespondenceMergeFieldVersionException
    {
        if (foiaConfigurationService.readConfiguration().getReceivedDateEnabled())
        {
            foiaQueueCorrespondenceService.handleRequestReceivedAcknowledgementLetter(requestId);
        }
        else
        {
            generateAndUploadACK(requestId);
        }
    }

    private void generateAndUploadACK(Long requestId) throws DocumentGeneratorException, CorrespondenceMergeFieldVersionException

    {
        FOIARequest request = requestDao.find(requestId);
        FOIADocumentDescriptor documentDescriptor = documentGeneratorService.getDocumentDescriptor(request, FOIAConstants.ACK);
        String arkcaseFilename = String.format(documentDescriptor.getFilenameFormat(), request.getId());
        String targetFolderId = request.getContainer().getAttachmentFolder() == null
                ? request.getContainer().getFolder().getCmisFolderId()
                : request.getContainer().getAttachmentFolder().getCmisFolderId();
        documentGenerator.generateAndUpload(documentDescriptor, request, targetFolderId, arkcaseFilename,
                documentGeneratorService.getReportSubstitutions(request));
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

    public EcmFileDao getEcmFileDao()
    {
        return ecmFileDao;
    }

    public void setEcmFileDao(EcmFileDao ecmFileDao)
    {
        this.ecmFileDao = ecmFileDao;
    }

    public FOIARequestDao getRequestDao()
    {
        return requestDao;
    }

    public void setRequestDao(FOIARequestDao requestDao)
    {
        this.requestDao = requestDao;
    }

    public FOIADocumentGeneratorService getDocumentGeneratorService()
    {
        return documentGeneratorService;
    }

    public void setDocumentGeneratorService(FOIADocumentGeneratorService documentDescriptorService)
    {
        this.documentGeneratorService = documentDescriptorService;
    }

    public DocumentGenerator getDocumentGenerator()
    {
        return documentGenerator;
    }

    public void setDocumentGenerator(DocumentGenerator documentGenerator)
    {
        this.documentGenerator = documentGenerator;
    }

    public FoiaConfigurationService getFoiaConfigurationService()
    {
        return foiaConfigurationService;
    }

    public void setFoiaConfigurationService(FoiaConfigurationService foiaConfigurationService)
    {
        this.foiaConfigurationService = foiaConfigurationService;
    }

    public FOIAQueueCorrespondenceService getFoiaQueueCorrespondenceService()
    {
        return foiaQueueCorrespondenceService;
    }

    public void setFoiaQueueCorrespondenceService(FOIAQueueCorrespondenceService foiaQueueCorrespondenceService)
    {
        this.foiaQueueCorrespondenceService = foiaQueueCorrespondenceService;
    }
}
