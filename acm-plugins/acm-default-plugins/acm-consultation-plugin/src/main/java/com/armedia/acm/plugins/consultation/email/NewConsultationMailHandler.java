package com.armedia.acm.plugins.consultation.email;

/*-
 * #%L
 * ACM Default Plugin: Consultation
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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
import com.armedia.acm.plugins.consultation.model.Consultation;
import com.armedia.acm.plugins.consultation.service.ConsultationService;
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on May, 2020
 */
public class NewConsultationMailHandler extends AcmObjectMailHandler
{
    private final Logger log = LogManager.getLogger(getClass());

    private ConsultationService consultationService;
    private LookupDao lookupDao;
    private ReadWriteLock lock = new ReentrantReadWriteLock();
    private EmailReceiverConfig emailReceiverConfig;

    public NewConsultationMailHandler(AcmNameDao dao)
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

    private String setConsultationType()
    {
        List<StandardLookupEntry> consultationTypeLookup = (List<StandardLookupEntry>) getLookupDao().getLookupByName("consultationTypes")
                .getEntries();
        return consultationTypeLookup
                .stream()
                .filter(standardLookupEntry -> standardLookupEntry.getKey().equals("Generated from Email"))
                .findFirst()
                .map(StandardLookupEntry::getKey)
                .orElse(null);
    }

    @Transactional
    public Consultation createConsultation(Message message) throws MessagingException
    {
        Consultation consultation = new Consultation();

        consultation.setTitle(message.getSubject());
        consultation.setOriginator(createInitiator(message));
        consultation.setConsultationType(setConsultationType());

        AcmAuthentication acmAuthentication = new AcmAuthentication(null, null, null, true, "mail-service");

        try
        {
            log.info("Generating consultation from email with subject [{}]", message.getSubject());
            return consultationService.saveConsultation(consultation, acmAuthentication, null);
        }
        catch (PipelineProcessException e)
        {
            log.error("Consultation could not be generated from email with subject [{}]", message.getSubject(), e);
        }
        return consultation;
    }

    @Override
    @Transactional
    public void handle(Message message) throws MessagingException, IllegalArgumentException, SecurityException, IOException
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
        AcmObject entity = getEntityDao().findByName(entityId);
        Consultation consultation = null;
        if (entityId == null || entity == null)
        {
            consultation = createConsultation(message);
        }

        if (consultation != null)
        {

            String emailSender = extractEmailAddressFromMessage(message);
            String fileAndFolderName = makeFileOrFolderName(message, emailSender);

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

                AcmFolder folder = getAcmFolderService().addNewFolderByPath(consultation.getObjectType(), consultation.getId(),
                        getMailDirectory());
                emailReceivedFolder = getAcmFolderService().addNewFolder(folder.getId(), fileAndFolderName);
                try (InputStream is = new FileInputStream(messageFile))
                {
                    Authentication auth = new UsernamePasswordAuthenticationToken(userId, "");
                    mailFile = getEcmFileService().upload(messageFileName, "mail", "Document", is, "message/rfc822", messageFileName, auth,
                            emailReceivedFolder.getCmisFolderId(), consultation.getObjectType(), consultation.getId());

                }

            }
            catch (Exception e)
            {
                log.error("Failed to upload mail into consultation with number '{}'. Exception msg: '{}' ", consultation.getId(),
                        e.getMessage());
                exception = e;
            }

            if (message.getContent() instanceof Multipart)
            {
                uploadAttachments(message, consultation, userId, emailReceivedFolder);
            }

            SmtpEmailReceivedEvent event = new SmtpEmailReceivedEvent(emailSender, userId, mailFile.getId(), mailFile.getObjectType(),
                    consultation.getId(), consultation.getObjectType(),
                    AuthenticationUtils.getUserIpAddress());
            boolean success = (exception == null);
            event.setSucceeded(success);
            getEventPublisher().publishEvent(event);
        }

    }

    public ConsultationService getConsultationService()
    {
        return consultationService;
    }

    public void setConsultationService(ConsultationService consultationService)
    {
        this.consultationService = consultationService;
    }

    public LookupDao getLookupDao()
    {
        return lookupDao;
    }

    public void setLookupDao(LookupDao lookupDao)
    {
        this.lookupDao = lookupDao;
    }

    @Override
    public EmailReceiverConfig getEmailReceiverConfig()
    {
        return emailReceiverConfig;
    }

    @Override
    public void setEmailReceiverConfig(EmailReceiverConfig emailReceiverConfig)
    {
        this.emailReceiverConfig = emailReceiverConfig;
    }
}
