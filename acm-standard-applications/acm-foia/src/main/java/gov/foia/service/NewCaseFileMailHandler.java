package gov.foia.service;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
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
import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.data.AcmNameDao;
import com.armedia.acm.email.model.EmailReceiverConfig;
import com.armedia.acm.plugins.addressable.model.ContactMethod;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.person.dao.PersonDao;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import com.armedia.acm.plugins.person.model.PersonConstants;
import com.armedia.acm.service.MimeMessageParser;
import com.armedia.acm.services.config.lookups.model.AcmLookupEntry;
import com.armedia.acm.services.config.lookups.model.StandardLookupEntry;
import com.armedia.acm.services.config.lookups.service.LookupDao;
import com.armedia.acm.services.email.event.SmtpEmailReceivedEvent;
import com.armedia.acm.services.email.handler.AcmObjectMailHandler;
import com.armedia.acm.services.email.service.OriginalEmailExtractor;
import com.armedia.acm.web.api.MDCConstants;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.MDC;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.activation.DataHandler;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.InternetAddress;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import gov.foia.model.FOIAConstants;
import gov.foia.model.FOIAPerson;
import gov.foia.model.FOIARequest;
import gov.foia.model.FOIARequesterAssociation;

/**
 * Created by Aleksandar Acevski <aleksandar.acevski@armedia.com> on October, 2020
 */
public class NewCaseFileMailHandler extends AcmObjectMailHandler
{
    private final Logger log = LogManager.getLogger(getClass());

    private SaveFOIARequestService saveFOIARequestService;
    private EmailReceiverConfig emailReceiverConfig;
    private PersonDao personDao;
    private LookupDao lookupDao;
    private OriginalEmailExtractor originalEmailExtractor;

    public NewCaseFileMailHandler(AcmNameDao dao)
    {
        super(dao);
    }

    @Override
    @Transactional
    public void handle(Message message) throws MessagingException, IllegalArgumentException, SecurityException, IOException
    {
        if (!isEnabled())
        {
            return;
        }

        // Use different strategies to extract the original message on forwarded emails to ensure proper request data
        message = getOriginalEmailExtractor().extractMessage(message);

        String userId = emailReceiverConfig.getEmailUserId();
        getAuditPropertyEntityAdapter().setUserId(userId);

        // set the Alfresco user id, so we can attach the incoming message to the parent object.
        MDC.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, "admin");
        MDC.put(MDCConstants.EVENT_MDC_REQUEST_ID_KEY, UUID.randomUUID().toString());

        FOIARequest request = createRequestFromEmail(message);

        if (request != null)
        {
            addEmailToRequest(message, request, userId);

        }

    }

    private void addEmailToRequest(Message message, FOIARequest request, String userId) throws MessagingException
    {
        String emailSender = extractEmailAddressFromMessage(message);
        String fileAndFolderName = makeFileOrFolderName();

        String tempDir = System.getProperty("java.io.tmpdir");
        String messageFileName = fileAndFolderName + ".eml";
        File messageFile = new File(tempDir + File.separator + messageFileName);

        try (OutputStream os = new FileOutputStream(messageFile); InputStream is = new FileInputStream(messageFile))
        {
            message.writeTo(os);

            AcmFolder folder = getAcmFolderService().addNewFolderByPath(request.getObjectType(), request.getId(), getMailDirectory());

            AcmFolder emailReceivedFolder = getAcmFolderService().addNewFolder(folder.getId(), fileAndFolderName);
            Authentication auth = new UsernamePasswordAuthenticationToken(userId, "");
            EcmFile mailFile = getEcmFileService().upload(messageFileName, "mail", "Document", is, "message/rfc822", messageFileName, auth,
                    emailReceivedFolder.getCmisFolderId(), request.getObjectType(), request.getId());

            SmtpEmailReceivedEvent event = new SmtpEmailReceivedEvent(emailSender, userId, mailFile.getId(), mailFile.getObjectType(),
                    request.getId(), request.getObjectType(),
                    AuthenticationUtils.getUserIpAddress());

            event.setSucceeded(true);
            getEventPublisher().publishEvent(event);
        }
        catch (Exception e)
        {
            log.error("Failed to upload mail into request with number '{}'. Exception msg: '{}' ", request.getId(), e.getMessage());
        }
    }

    @Transactional
    public FOIARequest createRequestFromEmail(Message message) throws MessagingException
    {
        FOIARequest request = new FOIARequest();

        request.setTitle(message.getSubject());
        request.setRequestType(FOIAConstants.NEW_REQUEST_TYPE);
        request.setRequestSubType(FOIAConstants.REQUEST_SUB_TYPE);
        request.setDeliveryMethodOfResponse(FOIAConstants.DEFAULT_DELIVERY_METHOD_OF_RESPONSE);
        request.setReceivedDate(LocalDateTime.now());
        request.setRequestCategory(FOIAConstants.REQUEST_CATEGORY_MAIL_SERVICE);
        request.setFeeWaiverFlag(false);
        request.setExpediteFlag(false);
        request.setExternal(true);
        request.setComponentAgency(getDefaultComponentAgency());
        request.setDetails(MimeMessageParser.getFormattedStringContent(message));
        request.getPersonAssociations().add(createRequesterAssociation(message));

        AcmAuthentication acmAuthentication = new AcmAuthentication(null, null, null, true, "mail-service");
        SecurityContextHolder.getContext().setAuthentication(acmAuthentication);

        Map<String, List<MultipartFile>> filesMap = new HashMap<>();

        if (emailReceiverConfig.getEnableBurstingAttachments())
        {
            List<MultipartFile> fileList = getMultipartFileAttachmentList(message);
            filesMap.put(FOIAConstants.FILES_DESCRIPTION_DOCUMENTS, fileList);
        }

        try
        {
            log.info("Generating request from email with subject [{}]", message.getSubject());
            return (FOIARequest) saveFOIARequestService.saveFOIARequest(request, filesMap, acmAuthentication,
                    MDC.get(MDCConstants.EVENT_MDC_REQUEST_REMOTE_ADDRESS_KEY));
        }
        catch (AcmCreateObjectFailedException e)
        {
            log.error("Request could not be generated from email with subject [{}]", message.getSubject(), e);
        }
        return request;
    }

    private String getDefaultComponentAgency()
    {
        List<StandardLookupEntry> componentAgenciesLookupEntries = getLookupDao()
                .getLookupByName("componentsAgencies")
                .getEntries()
                .stream()
                .map(obj -> (StandardLookupEntry) obj)
                .collect(Collectors.toList());

        StandardLookupEntry defaultComponentAgencyLookup = componentAgenciesLookupEntries.stream()
                .filter(AcmLookupEntry::isPrimary)
                .findAny()
                .orElse(componentAgenciesLookupEntries.get(0));

        return defaultComponentAgencyLookup.getKey();
    }

    private List<MultipartFile> getMultipartFileAttachmentList(Message message)
    {
        List<MultipartFile> fileList = new ArrayList<>();

        try
        {
            Multipart multipart = (Multipart) message.getContent();

            for (int i = 0; i < multipart.getCount(); i++)
            {
                BodyPart bodyPart = multipart.getBodyPart(i);

                if (Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition()) && StringUtils.isNotBlank(bodyPart.getFileName()))
                {
                    DataHandler handler = bodyPart.getDataHandler();

                    File file = new File(handler.getName());
                    FileOutputStream fos = new FileOutputStream(file);
                    handler.writeTo(fos);

                    FileItem fileItem = new DiskFileItem("", handler.getContentType(), false, file.getName(), (int) file.length(),
                            file.getParentFile());

                    try (InputStream input = new FileInputStream(file))
                    {
                        OutputStream os = fileItem.getOutputStream();
                        IOUtils.copy(input, os);
                    }

                    fileList.add(new CommonsMultipartFile(fileItem));

                }
            }
        }
        catch (IOException | MessagingException e)
        {
            log.error("Error processing Multipart attachment. Exception msg: '{}' ", e.getMessage());
        }
        return fileList;

    }

    private PersonAssociation createRequesterAssociation(Message message) throws MessagingException
    {
        Address[] addresses = message.getFrom();
        String email = addresses == null ? null : ((InternetAddress) addresses[0]).getAddress();

        FOIARequesterAssociation requesterAssociation = new FOIARequesterAssociation();
        Person requester = getPersonDao().findByEmail(email).orElseGet(() -> createPlaceholderPerson(addresses));
        requesterAssociation.setPersonType("Requester");
        requesterAssociation.setPerson(requester);

        return requesterAssociation;
    }

    private FOIAPerson createPlaceholderPerson(Address[] addresses)
    {
        FOIAPerson person = new FOIAPerson();

        person.setGivenName(PersonConstants.GIVEN_NAME_PLACEHOLDER);
        person.setFamilyName(PersonConstants.FAMILY_NAME_PLACEHOLDER);

        if (addresses != null)
        {
            InternetAddress internetAddress = (InternetAddress) addresses[0];
            String emailAddress = internetAddress.getAddress();
            String[] splitPersonal = internetAddress.getPersonal().split(" ");

            if (splitPersonal.length == 2)
            {
                person.setGivenName(splitPersonal[0]);
                person.setFamilyName(splitPersonal[1]);
            }

            List<ContactMethod> contactMethods = new ArrayList<>();

            ContactMethod contactMethodEmail = new ContactMethod();
            contactMethodEmail.setType("email");
            contactMethodEmail.setSubType("Business");
            contactMethodEmail.setValue(emailAddress);
            contactMethods.add(contactMethodEmail);

            person.setContactMethods(contactMethods);
            person.setDefaultEmail(contactMethodEmail);
        }

        return person;
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

    public SaveFOIARequestService getSaveFOIARequestService()
    {
        return saveFOIARequestService;
    }

    public void setSaveFOIARequestService(SaveFOIARequestService saveFOIARequestService)
    {
        this.saveFOIARequestService = saveFOIARequestService;
    }

    public PersonDao getPersonDao()
    {
        return personDao;
    }

    public void setPersonDao(PersonDao personDao)
    {
        this.personDao = personDao;
    }

    public LookupDao getLookupDao()
    {
        return lookupDao;
    }

    public void setLookupDao(LookupDao lookupDao)
    {
        this.lookupDao = lookupDao;
    }

    public OriginalEmailExtractor getOriginalEmailExtractor()
    {
        return originalEmailExtractor;
    }

    public void setOriginalEmailExtractor(OriginalEmailExtractor originalEmailExtractor)
    {
        this.originalEmailExtractor = originalEmailExtractor;
    }
}
