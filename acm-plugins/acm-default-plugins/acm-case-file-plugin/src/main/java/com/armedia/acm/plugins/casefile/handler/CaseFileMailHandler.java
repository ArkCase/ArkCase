package com.armedia.acm.plugins.casefile.handler;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.exceptions.AcmCaseFileNotFound;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.model.CaseFileConstants;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by nebojsha on 25.06.2015.
 */
public class CaseFileMailHandler {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private CaseFileDao caseFileDao;
    private String mailDirectory;
    private EcmFileService ecmFileService;
    private AcmFolderService acmFolderService;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;
    private boolean enabled;

    @Transactional
    public void handle(Message message) throws MessagingException {
        if (!enabled)
            return;
        String caseFileNumber = extractCaseNumberFromSubject(message);
        if (caseFileNumber == null)
            throw new AcmCaseFileNotFound("Subject in the mail didn't match correct case number. subject: " + message.getSubject());
        try {
            String userId = "mail-service";
            auditPropertyEntityAdapter.setUserId(userId);
            String tempDir = System.getProperty("java.io.tmpdir");
            CaseFile caseFile = caseFileDao.findByCaseNumber(caseFileNumber);
            String messageFileName = System.currentTimeMillis() + "_" + caseFileNumber + ".eml";
            File messageFile = new File(tempDir + File.separator + messageFileName);
            OutputStream os = new FileOutputStream(messageFile);
            message.writeTo(os);
            try {
                os.flush();
                os.close();
            } catch (IOException e) {
                //it already closed we don't need to have any special handling for this
            }

            AcmFolder folder = acmFolderService.addNewFolderByPath(caseFile.getObjectType(), caseFile.getId(), mailDirectory);

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
                    caseFile.getObjectType(),
                    caseFile.getId());
        } catch (Exception e) {
            log.error("Error processing case file with number " + caseFileNumber, e);
        }
    }

    private String extractCaseNumberFromSubject(Message message) throws MessagingException {
        String subject = message.getSubject();
        if (subject == null || subject.length() < 1)
            return null;
        Pattern pattern = Pattern.compile(CaseFileConstants.CASE_FILE_NUMBER_REGEX_PATTERN);
        Matcher matcher = pattern.matcher(subject);
        if (matcher.find()) {
            return subject.substring(matcher.start(), matcher.end());
        }
        return null;
    }

    public void setCaseFileDao(CaseFileDao caseFileDao) {
        this.caseFileDao = caseFileDao;
    }

    public void setMailDirectory(String mailDirectory) {
        this.mailDirectory = mailDirectory;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setEcmFileService(EcmFileService ecmFileService) {
        this.ecmFileService = ecmFileService;
    }

    public void setAcmFolderService(AcmFolderService acmFolderService) {
        this.acmFolderService = acmFolderService;
    }

    public void setAuditPropertyEntityAdapter(AuditPropertyEntityAdapter auditPropertyEntityAdapter) {
        this.auditPropertyEntityAdapter = auditPropertyEntityAdapter;
    }
}
