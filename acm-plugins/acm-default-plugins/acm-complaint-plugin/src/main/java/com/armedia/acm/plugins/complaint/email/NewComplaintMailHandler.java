package com.armedia.acm.plugins.complaint.email;

/*-
 * #%L
 * ACM Default Plugin: Complaints
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
import com.armedia.acm.auth.AuthenticationUtils;
import com.armedia.acm.core.AcmObject;
import com.armedia.acm.data.AcmNameDao;
import com.armedia.acm.email.model.EmailReceiverConfig;
import com.armedia.acm.plugins.complaint.dao.ComplaintDao;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.service.SaveComplaintTransaction;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import com.armedia.acm.services.config.lookups.model.StandardLookupEntry;
import com.armedia.acm.services.config.lookups.service.LookupDao;
import com.armedia.acm.services.email.event.SmtpEmailReceivedEvent;
import com.armedia.acm.services.email.handler.AcmObjectMailHandler;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.web.api.MDCConstants;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.slf4j.MDC;
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
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

public class NewComplaintMailHandler extends AcmObjectMailHandler
{
    private final Logger log = LogManager.getLogger(getClass());

    private SaveComplaintTransaction saveComplaintTransaction;
    private LookupDao lookupDao;
    private EmailReceiverConfig emailReceiverConfig;
    private ComplaintDao complaintDao;

    public NewComplaintMailHandler(AcmNameDao dao)
    {
        super(dao);
    }

    private PersonAssociation createInitiator(Message message) throws MessagingException
    {

        Address[] addresses = message.getFrom();

        Person person = new Person();

        if (addresses.length > 0)
        {
            String[] splitedAddress = addresses[0].toString().split(" ");
            if (splitedAddress.length == 1)
            {
                person.setGivenName(splitedAddress[0]);
                person.setFamilyName(splitedAddress[0]);
            }
            else
            {
                person.setGivenName(splitedAddress[0]);
                person.setFamilyName(splitedAddress[1]);
            }
        }

        PersonAssociation personAssociation = new PersonAssociation();
        personAssociation.setPersonType("Initiator");
        personAssociation.setPerson(person);

        return personAssociation;
    }

    private String setComplaintType()
    {
        List<StandardLookupEntry> caseTypeLookup = (List<StandardLookupEntry>) getLookupDao().getLookupByName("complaintTypes")
                .getEntries();
        return caseTypeLookup
                .stream()
                .filter(standardLookupEntry -> standardLookupEntry.getKey().equals("Generated from Email"))
                .findFirst()
                .map(StandardLookupEntry::getKey)
                .orElse(null);
    }

    @Transactional
    public Complaint createComplaint(Message message) throws MessagingException
    {

        Complaint complaint = new Complaint();
        complaint.setComplaintTitle(message.getSubject());
        complaint.setOriginator(createInitiator(message));
        complaint.setComplaintType(setComplaintType());

        AcmAuthentication acmAuthentication = new AcmAuthentication(null, null, null,
                true, emailReceiverConfig.getEmailUserId());

        try
        {
            log.info("Generating complaint from email with subject [{}]", message.getSubject());
            return saveComplaintTransaction.saveComplaint(complaint, acmAuthentication);
        }
        catch (PipelineProcessException e)
        {
            log.error("Complaint could not be generated from email with subject [{}]", message.getSubject(), e);
        }

        return complaint;

    }

    @Override
    @Transactional
    public void handle(Message message) throws MessagingException, IllegalArgumentException,
            SecurityException, IOException
    {

        if (!isEnabled())
        {
            return;
        }

        String userId = emailReceiverConfig.getEmailUserId();
        getAuditPropertyEntityAdapter().setUserId(userId);

        // set the Alfresco user id, so we can attach the incoming message to the parent object.
        MDC.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, "admin");
        MDC.put(MDCConstants.EVENT_MDC_REQUEST_ID_KEY, UUID.randomUUID().toString());

        String entityId = extractIdFromSubject(message);
        AcmObject entity = complaintDao.quietFindByComplaintNumber(entityId);
        Complaint complaint = null;

        if (entityId == null || entity == null)
        {
            complaint = createComplaint(message);
        }

        if (complaint != null)
        {

            String emailSender = extractEmailAddressFromMessage(message);
            String fileAndFolderName = makeFileOrFolderName();
            
            String tempDir = System.getProperty("java.io.tmpdir");
            String messageFileName = fileAndFolderName + ".eml";
            File messageFile = new File(tempDir + File.separator + messageFileName);
            
            AcmFolder emailReceivedFolder = null;
            Exception exception = null;
            EcmFile mailFile = null;

            try
            {
                try (OutputStream os = new FileOutputStream(messageFile))
                {
                    message.writeTo(os);
                }

                AcmFolder folder = getAcmFolderService().addNewFolderByPath(complaint.getObjectType(), complaint.getId(),
                        getMailDirectory());
                emailReceivedFolder = getAcmFolderService().addNewFolder(folder.getId(), fileAndFolderName);
                try (InputStream is = new FileInputStream(messageFile))
                {
                    Authentication auth = new UsernamePasswordAuthenticationToken(userId, "");
                    mailFile = getEcmFileService().upload(messageFileName, "mail", "Document", is, "message/rfc822", messageFileName, auth,
                            emailReceivedFolder.getCmisFolderId(), complaint.getObjectType(), complaint.getId());

                }

            }
            catch (Exception e)
            {
                log.error("Failed to upload mail into complaint with number '{}'. Exception msg: '{}' ", complaint.getId(), e.getMessage());
                exception = e;
            }

            if (message.getContent() instanceof Multipart)
            {
                uploadAttachments(message, complaint, userId, emailReceivedFolder);
            }

            SmtpEmailReceivedEvent event = new SmtpEmailReceivedEvent(emailSender, userId, mailFile.getId(), mailFile.getObjectType(), complaint.getId(), complaint.getObjectType(), 
                    AuthenticationUtils.getUserIpAddress());
            boolean success = (exception == null);
            event.setSucceeded(success);
            getEventPublisher().publishEvent(event);
        }

    }

    public void setSaveComplaintTransaction(SaveComplaintTransaction saveComplaintTransaction)
    {
        this.saveComplaintTransaction = saveComplaintTransaction;
    }

    public LookupDao getLookupDao()
    {
        return lookupDao;
    }

    public void setLookupDao(LookupDao lookupDao)
    {
        this.lookupDao = lookupDao;
    }

    public ComplaintDao getComplaintDao()
    {
        return complaintDao;
    }

    public void setComplaintDao(ComplaintDao complaintDao)
    {
        this.complaintDao = complaintDao;
    }

    public SaveComplaintTransaction getSaveComplaintTransaction()
    {
        return saveComplaintTransaction;
    }

    public EmailReceiverConfig getEmailReceiverConfig()
    {
        return emailReceiverConfig;
    }

    public void setEmailReceiverConfig(EmailReceiverConfig emailReceiverConfig)
    {
        this.emailReceiverConfig = emailReceiverConfig;
    }
}
