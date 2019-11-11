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


import static gov.foia.model.FOIARequestUtils.extractRequestorEmailAddress;

import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;
import com.armedia.acm.services.email.service.TemplatingEngine;
import com.armedia.acm.services.notification.dao.NotificationDao;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.service.NotificationSender;
import com.armedia.acm.services.users.dao.UserDao;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.core.io.Resource;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
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
    private Logger log = LogManager.getLogger(getClass());
    private NotificationSender notificationSender;
    private UserDao userDao;
    private EcmFileDao ecmFileDao;
    private FOIARequestDao requestDao;
    private FOIADocumentGeneratorService documentGeneratorService;
    private DocumentGenerator documentGenerator;
    private FoiaConfigurationService foiaConfigurationService;
    private FOIAQueueCorrespondenceService foiaQueueCorrespondenceService;
    private String emailBodyTemplate;
    private TemplatingEngine templatingEngine;
    private NotificationDao notificationDao;

    public void emailAcknowledgement(Long requestId)
    {
        if (!foiaConfigurationService.readConfiguration().getReceivedDateEnabled())
        {
            FOIARequest request = getRequestDao().find(requestId);
            String emailAddress = extractRequestorEmailAddress(request.getOriginator().getPerson());
            if (emailAddress != null && emailAddress.isEmpty())
            {
                FOIADocumentDescriptor documentDescriptor = documentGeneratorService.getDocumentDescriptor(request, FOIAConstants.ACK);
                EcmFile letter = getEcmFileDao().findForContainerAttachmentFolderAndFileType(request.getContainer().getId(), request.getContainer().getAttachmentFolder().getId(), documentDescriptor.getDoctype());

                EcmFileVersion ecmFileVersions = letter.getVersions().get(letter.getVersions().size() - 1);

                Notification notification = new Notification();
                notification.setTemplateModelName("requestDocumentAttached");
                notification.setEmailAddresses(emailAddress);
                notification.setAttachFiles(true);
                notification.setFiles(Arrays.asList(ecmFileVersions));
                notification.setParentId(requestId);
                notification.setParentType(request.getObjectType());
                notification.setTitle(String.format("%s %s", request.getRequestType(), request.getCaseNumber()));
                notification.setUser(request.getCreator());
                notificationDao.save(notification);

                notificationDao.save(notification);


            }
        }
    }

    public void generateAndUpload(String objectType, Long requestId) throws DocumentGeneratorException
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

    public void generateAndUploadACK(Long requestId) throws DocumentGeneratorException

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

    public String getEmailBodyTemplate()
    {
        return emailBodyTemplate;
    }

    public void setEmailBodyTemplate(Resource emailBodyTemplate) throws IOException
    {
        try (DataInputStream resourceStream = new DataInputStream(emailBodyTemplate.getInputStream()))
        {
            byte[] bytes = new byte[resourceStream.available()];
            resourceStream.readFully(bytes);
            this.emailBodyTemplate = new String(bytes, Charset.forName("UTF-8"));
        }
    }

    public TemplatingEngine getTemplatingEngine()
    {
        return templatingEngine;
    }

    public void setTemplatingEngine(TemplatingEngine templatingEngine)
    {
        this.templatingEngine = templatingEngine;
    }

    public NotificationDao getNotificationDao()
    {
        return notificationDao;
    }

    public void setNotificationDao(NotificationDao notificationDao)
    {
        this.notificationDao = notificationDao;
    }
}
