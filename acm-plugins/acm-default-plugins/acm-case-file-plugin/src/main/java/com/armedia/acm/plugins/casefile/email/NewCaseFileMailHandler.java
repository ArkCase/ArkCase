package com.armedia.acm.plugins.casefile.email;

/*-
 * #%L
 * ACM Default Plugin: Case File
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

import com.armedia.acm.auth.AcmAuthentication;
import com.armedia.acm.core.AcmObject;
import com.armedia.acm.data.AcmNameDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.service.SaveCaseService;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import com.armedia.acm.services.config.lookups.model.StandardLookupEntry;
import com.armedia.acm.services.config.lookups.service.LookupDao;
import com.armedia.acm.services.email.event.SmtpEmailReceivedEvent;
import com.armedia.acm.services.email.handler.AcmObjectMailHandler;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.web.api.MDCConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class NewCaseFileMailHandler extends AcmObjectMailHandler
{

    private final Logger log = LoggerFactory.getLogger(getClass());
    private SaveCaseService saveCaseService;
    private LookupDao lookupDao;
    private Resource emailReceiverPropertiesResource;
    private ReadWriteLock lock = new ReentrantReadWriteLock();

    public NewCaseFileMailHandler(AcmNameDao dao)
    {
        super(dao);
    }

    private PersonAssociation createInitiator(Message message) throws MessagingException
    {

        Address[] addresses = message.getFrom();

        Person person = new Person();

        if (addresses.length > 0)
        {
            person.setGivenName(addresses[0].toString().split(" ")[0]);
            person.setFamilyName(addresses[0].toString().split(" ")[1]);
        }

        PersonAssociation personAssociation = new PersonAssociation();
        personAssociation.setPersonType("Initiator");
        personAssociation.setPerson(person);

        return personAssociation;
    }

    private String setUserId()
    {

        String userId = null;
        Properties emailReceiverProperties = new Properties();

        Lock readLock = lock.readLock();
        readLock.lock();

        try (InputStream propertyInputStream = emailReceiverPropertiesResource.getInputStream())
        {
            emailReceiverProperties.load(propertyInputStream);
        }
        catch (IOException e)
        {
            log.error("Could not read properties from [{}] file.", emailReceiverPropertiesResource.getFilename());
        }
        finally
        {
            readLock.unlock();
        }
        Set<String> propertyNames = emailReceiverProperties.stringPropertyNames();
        for (String propertyName : propertyNames)
        {
            String propertyValue = emailReceiverProperties.getProperty(propertyName);
            if (propertyName.equals("email.userId"))
            {
                userId = propertyValue;
            }
        }
        return userId;

    }

    private String setCaseFileType()
    {
        List<StandardLookupEntry> caseTypeLookup = (List<StandardLookupEntry>) getLookupDao().getLookupByName("caseFileTypes").getEntries();
        StandardLookupEntry caseFileType = caseTypeLookup
                .stream()
                .filter(standardLookupEntry -> standardLookupEntry.getKey().equals("Generated from Email"))
                .findFirst()
                .orElse(null);
        return caseFileType.getKey();
    }

    @Transactional
    public CaseFile createCaseFile(Message message) throws MessagingException
    {
        CaseFile caseFile = new CaseFile();

        caseFile.setTitle(message.getSubject());
        caseFile.setOriginator(createInitiator(message));
        caseFile.setCaseType(setCaseFileType());

        AcmAuthentication acmAuthentication = new AcmAuthentication(null, null, null, true, "mail-service");

        try
        {
            log.info("Generating case file from email with subject [{}]", message.getSubject());
            return saveCaseService.saveCase(caseFile, acmAuthentication, null);
        }
        catch (PipelineProcessException e)
        {
            log.error("Case file could not be generated from email with subject [{}]", message.getSubject(), e);
        }
        return caseFile;
    }

    @Override
    @Transactional
    public void handle(Message message) throws MessagingException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, NoSuchMethodException, SecurityException, IOException
    {
        if (!isEnabled())
        {
            return;
        }

        String userId = setUserId();
        getAuditPropertyEntityAdapter().setUserId(userId);

        // set the Alfresco user id, so we can attach the incoming message to the parent object.
        MDC.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, "admin");
        MDC.put(MDCConstants.EVENT_MDC_REQUEST_ID_KEY, UUID.randomUUID().toString());

        String entityId = extractIdFromSubject(message);
        AcmObject entity = getEntityDao().findByName(entityId);
        CaseFile caseFile = null;
        if (entityId == null || entity == null)
        {
            caseFile = createCaseFile(message);
        }

        if (caseFile != null)
        {
            String tempDir = System.getProperty("java.io.tmpdir");
            String messageFileName = System.currentTimeMillis() + "_" + caseFile.getId() + ".eml";
            File messageFile = new File(tempDir + File.separator + messageFileName);
            Exception exception = null;

            try
            {
                try (OutputStream os = new FileOutputStream(messageFile))
                {
                    message.writeTo(os);
                }

                AcmFolder folder = getAcmFolderService().addNewFolderByPath(caseFile.getObjectType(), caseFile.getId(), getMailDirectory());
                try (InputStream is = new FileInputStream(messageFile))
                {
                    Authentication auth = new UsernamePasswordAuthenticationToken(userId, "");
                    getEcmFileService().upload(messageFileName, "mail", "Document", is, "message/rfc822", messageFileName, auth,
                            folder.getCmisFolderId(), caseFile.getObjectType(), caseFile.getId());

                }

            }
            catch (Exception e)
            {
                log.error("Failed to upload mail into case with number '{}'. Exception msg: '{}' ", caseFile.getId(), e.getMessage());
                exception = e;
            }

            if (message.getContent() instanceof Multipart)
            {
                uploadAttachments(message, caseFile, userId);
            }

            SmtpEmailReceivedEvent event = new SmtpEmailReceivedEvent(message, userId, caseFile.getId(), caseFile.getObjectType());
            boolean success = (exception == null);
            event.setSucceeded(success);
            getEventPublisher().publishEvent(event);
        }

    }

    public void setSaveCaseService(SaveCaseService saveCaseService)
    {
        this.saveCaseService = saveCaseService;
    }

    public LookupDao getLookupDao()
    {
        return lookupDao;
    }

    public void setLookupDao(LookupDao lookupDao)
    {
        this.lookupDao = lookupDao;
    }

    public Resource getEmailReceiverPropertiesResource()
    {
        return emailReceiverPropertiesResource;
    }

    public void setEmailReceiverPropertiesResource(Resource emailReceiverPropertiesResource)
    {
        this.emailReceiverPropertiesResource = emailReceiverPropertiesResource;
    }
}
