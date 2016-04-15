package com.armedia.acm.plugins.complaint.handler;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.casefile.exceptions.AcmCaseFileNotFound;
import com.armedia.acm.plugins.complaint.dao.ComplaintDao;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.Message;
import javax.mail.MessagingException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ComplaintMailHandler
{

    private final Logger log = LoggerFactory.getLogger(getClass());

    private ComplaintDao complaintDao;
    private String mailDirectory;
    private EcmFileService ecmFileService;
    private AcmFolderService acmFolderService;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;
    private boolean enabled;
    private String complaintNumberRegexPattern;

    @Transactional
    public void handle(Message message) throws MessagingException
    {
        if (!enabled)
            return;
        String complaintNumber = extractComplaintNumberFromSubject(message);
        if (complaintNumber == null)
            throw new AcmCaseFileNotFound("Subject in the mail didn't match correct complaint number. subject: " + message.getSubject());

        String userId = "mail-service";
        auditPropertyEntityAdapter.setUserId(userId);
        String tempDir = System.getProperty("java.io.tmpdir");
        Complaint complaint = complaintDao.findByComplaintNumber(complaintNumber);
        String messageFileName = System.currentTimeMillis() + "_" + complaintNumber + ".eml";
        File messageFile = new File(tempDir + File.separator + messageFileName);
        try (OutputStream os = new FileOutputStream(messageFile))
        {
            message.writeTo(os);
            AcmFolder folder = acmFolderService.addNewFolderByPath(complaint.getObjectType(), complaint.getId(), mailDirectory);
            InputStream is = new FileInputStream(messageFile);
            Authentication auth = new UsernamePasswordAuthenticationToken(userId, "");
            ecmFileService.upload(messageFileName,
                    "mail",
                    "Document",
                    is,
                    "message/rfc822",
                    messageFileName,
                    auth,
                    folder.getCmisFolderId(),
                    complaint.getObjectType(),
                    complaint.getId());
        } catch (Exception e)
        {
            log.error("Error processing complaint with number '{}'. Exception msg: '{}' ", complaint, e.getMessage());
        }

    }

    private String extractComplaintNumberFromSubject(Message message) throws MessagingException
    {
        String subject = message.getSubject();
        if (StringUtils.isEmpty(subject))
        {
            return null;
        }
        Pattern pattern = Pattern.compile(complaintNumberRegexPattern);
        Matcher matcher = pattern.matcher(subject);
        if (matcher.find())
        {
            return subject.substring(matcher.start(), matcher.end());
        }
        return null;
    }

    public void setComplaintDao(ComplaintDao complaintDao)
    {
        this.complaintDao = complaintDao;
    }

    public void setMailDirectory(String mailDirectory)
    {
        this.mailDirectory = mailDirectory;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
    }

    public void setAcmFolderService(AcmFolderService acmFolderService)
    {
        this.acmFolderService = acmFolderService;
    }

    public void setAuditPropertyEntityAdapter(AuditPropertyEntityAdapter auditPropertyEntityAdapter)
    {
        this.auditPropertyEntityAdapter = auditPropertyEntityAdapter;
    }

    public void setComplaintNumberRegexPattern(String complaintNumberRegexPattern)
    {
        this.complaintNumberRegexPattern = complaintNumberRegexPattern;
    }
}
