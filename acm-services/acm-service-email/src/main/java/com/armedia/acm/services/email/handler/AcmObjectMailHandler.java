package com.armedia.acm.services.email.handler;

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.data.AcmNameDao;
import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.EcmFileService;

import com.armedia.acm.services.email.event.SmtpEmailReceivedEvent;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.persistence.EntityNotFoundException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Acm Object mail handler, handles incoming entity related emails and imports the email files in entity contained email
 * folder
 *
 * @author dame.gjorgjievski
 */
public class AcmObjectMailHandler implements ApplicationEventPublisherAware {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final AcmNameDao entityDao;

    private EcmFileService ecmFileService;
    private AcmFolderService acmFolderService;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;
    private ApplicationEventPublisher eventPublisher;

    private String objectIdRegexPattern;
    private String mailDirectory;
    private boolean enabled;

    public AcmObjectMailHandler(AcmNameDao dao) {
        this.entityDao = dao;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        eventPublisher = applicationEventPublisher;
    }

    @Transactional
    public void handle(Message message) throws MessagingException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, NoSuchMethodException, SecurityException {
        if (!enabled) {
            return;
        }

        String entityId = extractIdFromSubject(message);
        if (entityId == null) {
            throw new EntityNotFoundException("Subject in the mail didn't match correct entity number. subject: " + message.getSubject());
        }

        AcmObject entity = entityDao.findByName(entityId);
        if (entity == null) {
            throw new EntityNotFoundException("No entity was found with given number: " + entityId);
        }

        String userId = "mail-service";
        auditPropertyEntityAdapter.setUserId(userId);
        String tempDir = System.getProperty("java.io.tmpdir");
        String messageFileName = System.currentTimeMillis() + "_" + entityId + ".eml";
        File messageFile = new File(tempDir + File.separator + messageFileName);
        Exception exception = null;

        try {
            try (OutputStream os = new FileOutputStream(messageFile)) {
                message.writeTo(os);
            }

            AcmFolder folder = acmFolderService.addNewFolderByPath(entity.getObjectType(), entity.getId(), mailDirectory);
            try (InputStream is = new FileInputStream(messageFile)) {
                Authentication auth = new UsernamePasswordAuthenticationToken(userId, "");
                ecmFileService.upload(messageFileName, "mail", "Document", is, "message/rfc822", messageFileName, auth,
                        folder.getCmisFolderId(), entity.getObjectType(), entity.getId());


            }

        } catch (Exception e) {
            log.error("Error processing complaint with number '{}'. Exception msg: '{}' ", entityId, e.getMessage());
            exception = e;
        }

        SmtpEmailReceivedEvent event = new SmtpEmailReceivedEvent(message, userId, entity.getId(), entity.getObjectType());
        boolean success = (exception == null);
        event.setSucceeded(success);
        eventPublisher.publishEvent(event);

    }

    /**
     * Extract id from message subject
     *
     * @param message
     * @return
     * @throws MessagingException
     */
    private String extractIdFromSubject(Message message) throws MessagingException {
        String result = null;
        String subject = message.getSubject();
        if (!StringUtils.isEmpty(subject)) {

            Pattern pattern = Pattern.compile(objectIdRegexPattern);
            Matcher matcher = pattern.matcher(subject);
            if (matcher.find()) {
                result = subject.substring(matcher.start(), matcher.end());
            }
        }
        return result;
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

    public void setObjectIdRegexPattern(String objectIdRegexPattern) {
        this.objectIdRegexPattern = objectIdRegexPattern;
    }

    public void setMailDirectory(String mailDirectory) {
        this.mailDirectory = mailDirectory;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}
