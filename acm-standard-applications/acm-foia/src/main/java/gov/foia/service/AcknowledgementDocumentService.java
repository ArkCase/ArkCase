package gov.foia.service;

import static gov.foia.model.FOIAConstants.EMAIL_BODY_ATTACHMENT;
import static gov.foia.model.FOIAConstants.EMAIL_FOOTER_ATTACHMENT;
import static gov.foia.model.FOIAConstants.EMAIL_HEADER_ATTACHMENT;
import static gov.foia.model.FOIARequestUtils.extractRequestorEmailAddress;

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

    public void emailAcknowledgement(Long requestId)
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

    public void generateAndUpload(String objectType, Long requestId) throws DocumentGeneratorException
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

}
