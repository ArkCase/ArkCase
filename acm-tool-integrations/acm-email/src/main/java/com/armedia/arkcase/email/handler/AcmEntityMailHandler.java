package com.armedia.arkcase.email.handler;

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.data.AuditPropertyEntityAdapter;
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
 * 
 * @author dame.gjorgjievski
 * 
 *         Generic entity mail handler, handles incoming entity related emails and imports the email files in entity
 *         contained email folder
 *
 */
public class AcmEntityMailHandler<E extends AcmObject, DAO extends AcmAbstractDao<E>>
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final AcmAbstractDao<E> entityDao;
    private final String entityDaoMethod;
    private final Class<E> entityClass;

    private EcmFileService ecmFileService;
    private AcmFolderService acmFolderService;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;

    private String entityIdRegexPattern;
    private String mailDirectory;
    private boolean enabled;

    public AcmEntityMailHandler(DAO dao, String daoMethod, Class<E> entityClass)
    {
        this.entityDao = dao;
        this.entityDaoMethod = daoMethod;
        this.entityClass = entityClass;
    }

    @Transactional
    public void handle(Message message) throws MessagingException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, NoSuchMethodException, SecurityException
    {
        if (!enabled)
        {
            return;
        }
        String entityId = extractIdFromSubject(message);
        if (entityId == null)
        {
            throw new EntityNotFoundException("Subject in the mail didn't match correct entity number. subject: " + message.getSubject());
        }

        String userId = "mail-service";
        auditPropertyEntityAdapter.setUserId(userId);
        String tempDir = System.getProperty("java.io.tmpdir");
        Object result = entityDao.getClass().getMethod(entityDaoMethod, String.class).invoke(entityId);
        E entity = entityClass.cast(result);

        String messageFileName = System.currentTimeMillis() + "_" + entityId + ".eml";
        File messageFile = new File(tempDir + File.separator + messageFileName);
        try (OutputStream os = new FileOutputStream(messageFile))
        {
            message.writeTo(os);
            AcmFolder folder = acmFolderService.addNewFolderByPath(entity.getObjectType(), entity.getId(), mailDirectory);
            InputStream is = new FileInputStream(messageFile);
            Authentication auth = new UsernamePasswordAuthenticationToken(userId, "");
            ecmFileService.upload(messageFileName, "mail", "Document", is, "message/rfc822", messageFileName, auth,
                    folder.getCmisFolderId(), entity.getObjectType(), entity.getId());
        } catch (Exception e)
        {
            log.error("Error processing complaint with number '{}'. Exception msg: '{}' ", entityId, e.getMessage());
        }

    }

    /**
     * Extract id from message subject
     * 
     * @param message
     * @return
     * @throws MessagingException
     */
    private String extractIdFromSubject(Message message) throws MessagingException
    {
        String result = null;
        String subject = message.getSubject();
        if (!StringUtils.isEmpty(subject))
        {

            Pattern pattern = Pattern.compile(entityIdRegexPattern);
            Matcher matcher = pattern.matcher(subject);
            if (matcher.find())
            {
                result = subject.substring(matcher.start(), matcher.end());
            }
        }
        return result;
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

    public void setEntityIdRegexPattern(String entityIdRegexPattern)
    {
        this.entityIdRegexPattern = entityIdRegexPattern;
    }

    public void setMailDirectory(String mailDirectory)
    {
        this.mailDirectory = mailDirectory;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

}
