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

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.exception.AcmFolderException;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.services.config.lookups.model.StandardLookupEntry;
import com.armedia.acm.services.config.lookups.service.LookupDao;
import com.armedia.acm.services.email.model.EmailWithAttachmentsDTO;
import com.armedia.acm.services.templateconfiguration.model.Template;
import com.armedia.acm.services.templateconfiguration.service.CorrespondenceTemplateManager;
import com.armedia.acm.services.templateconfiguration.service.TemplatingEngine;
import com.armedia.acm.services.labels.service.TranslationService;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.service.NotificationSender;
import com.armedia.acm.services.notification.service.NotificationService;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;

import com.sun.xml.fastinfoset.stax.events.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import freemarker.template.TemplateException;
import gov.foia.dao.FOIARequestDao;
import gov.foia.model.FOIAConstants;
import gov.foia.model.FOIADocumentDescriptor;
import gov.foia.model.FOIAFile;
import gov.foia.model.FOIARequest;

/**
 * @author sasko.tanaskoski
 *
 */
public class FOIAQueueCorrespondenceService
{
    private Logger log = LogManager.getLogger(getClass());

    private ResponseFolderService responseFolderService;
    private NotificationSender notificationSender;
    private UserDao userDao;
    private EcmFileService ecmFileService;
    private DocumentGenerator documentGenerator;
    private FOIADocumentGeneratorService documentGeneratorService;
    private FOIARequestDao requestDao;
    private FoiaConfigurationService foiaConfigurationService;
    private String emailBodyTemplate;
    private TemplatingEngine templatingEngine;
    private NotificationService notificationService;
    private TranslationService translationService;
    private LookupDao lookupDao;
    private CorrespondenceTemplateManager templateManager;

    public void handleApproveCorrespondence(Long requestId)
    {

        FOIARequest request = requestDao.find(requestId);

        if (request.getDeniedFlag())
        {

            try
            {
                generateCorrespondenceLetter(request, documentGeneratorService.getDocumentDescriptor(request, FOIAConstants.DENIAL));
            }
            catch (Exception e)
            {
                log.error("Unable to generate Denial Letter for {} [{}]", request.getRequestType(), request.getId(),
                        e);
            }
        }

    }

    public void handleReleaseCorrespondence(Long requestId)
    {

        FOIARequest request = requestDao.find(requestId);

        if (request.getDeniedFlag())
        {

            try
            {
                FOIAFile letter = (FOIAFile) generateCorrespondenceLetter(request,
                        documentGeneratorService.getDocumentDescriptor(request, FOIAConstants.DENIAL));

                FOIAFile letterCopy = new FOIAFile();
                BeanUtils.copyProperties(letter, letterCopy);
                letterCopy.setPublicFlag(true);

                ecmFileService.updateFile(letterCopy);
                emailCorrespondenceLetter(request, letter);
            }
            catch (Exception e)
            {
                log.error("Unable to generate and email Correspondence Letter for {} [{}]", request.getRequestType(), request.getId(),
                        e);
            }
        }

    }

    public void handleDeleteCorrespondenceLetter(Long requestId)
    {

        FOIARequest request = requestDao.find(requestId);
        {

            try
            {
                FOIAFile deleteCorrespondenceLetter = (FOIAFile) generateCorrespondenceLetter(request,
                        documentGeneratorService.getDocumentDescriptor(request, FOIAConstants.REQ_DELETE));

                FOIAFile deleteCorrespondenceLetterCopy = new FOIAFile();
                BeanUtils.copyProperties(deleteCorrespondenceLetter, deleteCorrespondenceLetterCopy);
                deleteCorrespondenceLetterCopy.setPublicFlag(true);

                ecmFileService.updateFile(deleteCorrespondenceLetterCopy);

            }
            catch (Exception e)
            {
                log.error("Unable to generate Delete Correspondence Letter for Request {} [{}]", request.getRequestType(), request.getId(),
                        e);
            }
        }

    }

    public void handleRequestReceivedAcknowledgementLetter(Long requestId)
    {
        FOIARequest request = requestDao.find(requestId);
        try
        {
            FOIADocumentDescriptor documentDescriptor;
            documentDescriptor = documentGeneratorService.getDocumentDescriptor(request, FOIAConstants.RECEIVE_ACK);

            String arkcaseFilename = String.format(documentDescriptor.getFilenameFormat(), request.getId());
            String targetFolderId = request.getContainer().getAttachmentFolder() == null
                    ? request.getContainer().getFolder().getCmisFolderId()
                    : request.getContainer().getAttachmentFolder().getCmisFolderId();

            FOIAFile letter = (FOIAFile) documentGenerator.generateAndUpload(documentDescriptor, request,
                    targetFolderId, arkcaseFilename, documentGeneratorService.getReportSubstitutions(request));

            EcmFileVersion ecmFileVersions = letter.getVersions().get(letter.getVersions().size() - 1);

            AcmUser user = request.getAssigneeLdapId() != null ? userDao.findByUserId(request.getAssigneeLdapId()) : null;

            String emailAddress = extractRequestorEmailAddress(request.getOriginator().getPerson());

            if(!Util.isEmptyString(emailAddress))
            {
                String emailSubject = "";
                Template template = templateManager.findTemplate("requestDocumentAttached.html");
                if(template != null)
                {
                    emailSubject = template.getEmailSubject();
                }

                Notification notification = notificationService.getNotificationBuilder()
                        .newNotification("requestDocumentAttached", String.format("%s %s", request.getRequestType(), request.getCaseNumber()),
                                request.getObjectType(), requestId, user != null ? user.getUserId() : null)
                        .withFiles(Arrays.asList(ecmFileVersions))
                        .withEmailAddresses(emailAddress)
                        .forObjectWithNumber(request.getCaseNumber())
                        .forObjectWithTitle(request.getTitle())
                        .withSubject(emailSubject)
                        .build();

                notificationService.saveNotification(notification);
            }


        }
        catch (Exception e)
        {
            log.error("Unable to generate and email Correspondence Letter for {} [{}]", request.getRequestType(), request.getId(), e);
        }
    }

    public EcmFile generateCorrespondenceLetter(FOIARequest request, FOIADocumentDescriptor documentDescriptor)
            throws AcmObjectNotFoundException, DocumentGeneratorException, AcmFolderException, AcmUserActionFailedException
    {
        String arkcaseFilename = String.format(documentDescriptor.getFilenameFormat(), request.getId());
        List<StandardLookupEntry> lookupEntries = (List<StandardLookupEntry>) getLookupDao().getLookupByName("requestDispositionSubType").getEntries();
        if (request.getDisposition() != null)
        {
            String dispositionValue = lookupEntries.stream()
                    .filter(entry -> request.getDisposition().equalsIgnoreCase(entry.getKey()))
                    .findFirst()
                    .map(tempDispValue -> getTranslationService().translate(tempDispValue.getValue())).orElse("");
            request.setDispositionValue(dispositionValue);
        }

        return documentGenerator.generateAndUpload(documentDescriptor, request,
                getResponseFolderService().getResponseFolder(request).getCmisFolderId(), arkcaseFilename,
                documentGeneratorService.getReportSubstitutions(request));
    }

    public void emailCorrespondenceLetter(FOIARequest request, FOIAFile letter) throws Exception
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String emailAddress = extractRequestorEmailAddress(request.getOriginator().getPerson());
        if (emailAddress != null || emailAddress != "")
        {
            if (emailAddress != null)
            {
                EmailWithAttachmentsDTO emailData = new EmailWithAttachmentsDTO();
                emailData.setEmailAddresses(Arrays.asList(emailAddress));

                emailData.setSubject(String.format("%s %s", request.getRequestType(), request.getCaseNumber()));
                emailData.setHeader(EMAIL_HEADER_ATTACHMENT);
                try
                {
                    String body = getTemplatingEngine().process(emailBodyTemplate, "request", request);
                    emailData.setBody(body);
                }
                catch (TemplateException | IOException e)
                {
                    // failing to send an email should not break the flow
                    log.error("Unable to generate email for {} about {} with ID [{}]", emailAddress, request.getObjectType(),
                            request.getId(),
                            e);
                    emailData.setBody(EMAIL_BODY_ATTACHMENT);
                }

                emailData.setFooter(EMAIL_FOOTER_ATTACHMENT);
                emailData.setAttachmentIds(Arrays.asList(letter.getFileId()));

                String userIdOrName = request.getCreator();
                getNotificationSender().sendEmailWithAttachments(emailData, auth, getUserDao().findByUserId(userIdOrName));

            }
        }

    }

    /**
     * @return the responseFolderService
     */
    public ResponseFolderService getResponseFolderService()
    {
        return responseFolderService;
    }

    /**
     * @param responseFolderService
     *            the responseFolderService to set
     */
    public void setResponseFolderService(ResponseFolderService responseFolderService)
    {
        this.responseFolderService = responseFolderService;
    }

    /**
     * @return the notificationSender
     */
    public NotificationSender getNotificationSender()
    {
        return notificationSender;
    }

    /**
     * @param notificationSender
     *            the notificationSender to set
     */
    public void setNotificationSender(NotificationSender notificationSender)
    {
        this.notificationSender = notificationSender;
    }

    /**
     * @return the userDao
     */
    public UserDao getUserDao()
    {
        return userDao;
    }

    /**
     * @param userDao
     *            the userDao to set
     */
    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    /**
     * @return the ecmFileService
     */
    public EcmFileService getEcmFileService()
    {
        return ecmFileService;
    }

    /**
     * @param ecmFileService
     *            the ecmFileService to set
     */
    public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
    }

    public DocumentGenerator getDocumentGenerator()
    {
        return documentGenerator;
    }

    public void setDocumentGenerator(DocumentGenerator documentGenerator)
    {
        this.documentGenerator = documentGenerator;
    }

    public FOIADocumentGeneratorService getDocumentGeneratorService()
    {
        return documentGeneratorService;
    }

    public void setDocumentGeneratorService(FOIADocumentGeneratorService documentDescriptorService)
    {
        this.documentGeneratorService = documentDescriptorService;
    }

    public FOIARequestDao getRequestDao()
    {
        return requestDao;
    }

    public void setRequestDao(FOIARequestDao requestDao)
    {
        this.requestDao = requestDao;
    }

    public FoiaConfigurationService getFoiaConfigurationService()
    {
        return foiaConfigurationService;
    }

    public void setFoiaConfigurationService(FoiaConfigurationService foiaConfigurationService)
    {
        this.foiaConfigurationService = foiaConfigurationService;
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

    public NotificationService getNotificationService()
    {
        return notificationService;
    }

    public void setNotificationService(NotificationService notificationService)
    {
        this.notificationService = notificationService;
    }

    public TranslationService getTranslationService()
    {
        return translationService;
    }

    public void setTranslationService(TranslationService translationService)
    {
        this.translationService = translationService;
    }

    public LookupDao getLookupDao()
    {
        return lookupDao;
    }

    public void setLookupDao(LookupDao lookupDao)
    {
        this.lookupDao = lookupDao;
    }

    public CorrespondenceTemplateManager getTemplateManager()
    {
        return templateManager;
    }

    public void setTemplateManager(CorrespondenceTemplateManager templateManager)
    {
        this.templateManager = templateManager;
    }
}
