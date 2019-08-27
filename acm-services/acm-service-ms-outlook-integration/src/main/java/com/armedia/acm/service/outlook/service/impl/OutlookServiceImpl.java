package com.armedia.acm.service.outlook.service.impl;

/*-
 * #%L
 * ACM Service: MS Outlook integration
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

import com.armedia.acm.convertfolder.ConversionException;
import com.armedia.acm.convertfolder.DefaultFolderAndFileConverter;
import com.armedia.acm.core.exceptions.AcmOutlookConnectionFailedException;
import com.armedia.acm.core.exceptions.AcmOutlookCreateItemFailedException;
import com.armedia.acm.core.exceptions.AcmOutlookException;
import com.armedia.acm.core.exceptions.AcmOutlookFindItemsFailedException;
import com.armedia.acm.core.exceptions.AcmOutlookItemNotFoundException;
import com.armedia.acm.core.exceptions.AcmOutlookListItemsFailedException;
import com.armedia.acm.crypto.AcmCryptoUtils;
import com.armedia.acm.crypto.exceptions.AcmEncryptionException;
import com.armedia.acm.plugins.ecm.dao.AcmContainerDao;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.service.outlook.dao.AcmOutlookFolderCreatorDao;
import com.armedia.acm.service.outlook.dao.AcmOutlookFolderCreatorDaoException;
import com.armedia.acm.service.outlook.dao.OutlookDao;
import com.armedia.acm.service.outlook.dao.OutlookPasswordDao;
import com.armedia.acm.service.outlook.model.AcmOutlookFolderCreator;
import com.armedia.acm.service.outlook.model.AcmOutlookUser;
import com.armedia.acm.service.outlook.model.OutlookCalendarItem;
import com.armedia.acm.service.outlook.model.OutlookConfig;
import com.armedia.acm.service.outlook.model.OutlookContactItem;
import com.armedia.acm.service.outlook.model.OutlookDTO;
import com.armedia.acm.service.outlook.model.OutlookFolder;
import com.armedia.acm.service.outlook.model.OutlookFolderPermission;
import com.armedia.acm.service.outlook.model.OutlookItem;
import com.armedia.acm.service.outlook.model.OutlookMailItem;
import com.armedia.acm.service.outlook.model.OutlookPassword;
import com.armedia.acm.service.outlook.model.OutlookResults;
import com.armedia.acm.service.outlook.model.OutlookTaskItem;
import com.armedia.acm.service.outlook.service.OutlookEventPublisher;
import com.armedia.acm.service.outlook.service.OutlookFolderService;
import com.armedia.acm.service.outlook.service.OutlookService;
import com.armedia.acm.services.email.model.EmailBodyBuilder;
import com.armedia.acm.services.email.model.EmailBuilder;
import com.armedia.acm.services.email.model.EmailWithAttachmentsAndLinksDTO;
import com.armedia.acm.services.email.model.EmailWithAttachmentsDTO;
import com.armedia.acm.services.email.model.EmailWithEmbeddedLinksDTO;
import com.armedia.acm.services.email.model.EmailWithEmbeddedLinksResultDTO;
import com.armedia.acm.services.email.sender.service.EmailSenderConfigurationServiceImpl;
import com.armedia.acm.services.email.service.AcmEmailContentGeneratorService;
import com.armedia.acm.services.email.service.TemplatingEngine;
import com.armedia.acm.services.users.model.AcmUser;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mule.util.FileUtils;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.PropertySet;
import microsoft.exchange.webservices.data.core.enumeration.property.BodyType;
import microsoft.exchange.webservices.data.core.enumeration.property.EmailAddressKey;
import microsoft.exchange.webservices.data.core.enumeration.property.PhoneNumberKey;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.enumeration.service.DeleteMode;
import microsoft.exchange.webservices.data.core.exception.service.local.ServiceLocalException;
import microsoft.exchange.webservices.data.core.service.folder.Folder;
import microsoft.exchange.webservices.data.core.service.item.Appointment;
import microsoft.exchange.webservices.data.core.service.item.Contact;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.core.service.item.Item;
import microsoft.exchange.webservices.data.core.service.item.Task;
import microsoft.exchange.webservices.data.core.service.schema.AppointmentSchema;
import microsoft.exchange.webservices.data.core.service.schema.ContactSchema;
import microsoft.exchange.webservices.data.core.service.schema.EmailMessageSchema;
import microsoft.exchange.webservices.data.core.service.schema.ItemSchema;
import microsoft.exchange.webservices.data.core.service.schema.TaskSchema;
import microsoft.exchange.webservices.data.property.complex.FolderId;
import microsoft.exchange.webservices.data.property.complex.FolderPermission;
import microsoft.exchange.webservices.data.property.complex.MessageBody;
import microsoft.exchange.webservices.data.property.definition.ExtendedPropertyDefinition;
import microsoft.exchange.webservices.data.search.FindItemsResults;
import microsoft.exchange.webservices.data.search.filter.SearchFilter;

/**
 * Created by armdev on 4/20/15.
 */
public class OutlookServiceImpl implements OutlookService, OutlookFolderService
{
    private transient final Logger log = LogManager.getLogger(getClass());

    private OutlookDao dao;
    private AcmOutlookFolderCreatorDao folderCreatorDao;
    private EcmFileService ecmFileService;
    private AcmContainerDao acmContainerDao;
    private TemplatingEngine templatingEngine;

    private OutlookEventPublisher outlookEventPublisher;
    private OutlookConfig outlookConfig;

    private AcmEmailContentGeneratorService acmEmailContentGeneratorService;
    private AcmCryptoUtils acmCryptoUtils;
    private OutlookPasswordDao outlookPasswordDao;

    private EmailSenderConfigurationServiceImpl emailSenderConfigurationService;
    private DefaultFolderAndFileConverter defaultFolderAndFileConverter;

    @Override
    public OutlookResults<OutlookMailItem> findMailItems(AcmOutlookUser user, int start, int maxItems, String sortField,
            boolean sortAscending, SearchFilter filter) throws AcmOutlookConnectionFailedException, AcmOutlookListItemsFailedException
    {
        ExchangeService service = connect(user);

        PropertySet mailProperties = new PropertySet(EmailMessageSchema.From, EmailMessageSchema.Sender, EmailMessageSchema.IsRead);

        OutlookResults<OutlookMailItem> results = new OutlookResults<>();

        try
        {
            FindItemsResults<Item> items = getDao().findItems(service, WellKnownFolderName.Inbox, mailProperties, start, maxItems,
                    sortField, sortAscending, filter);

            populateResultHeaderFields(results, start, maxItems, sortField, sortAscending, items.getTotalCount(), items.isMoreAvailable(),
                    items.getNextPageOffset() == null ? -1 : items.getNextPageOffset());

            List<OutlookMailItem> messages = items.getItems().stream().map(this::messageFrom).collect(Collectors.toList());
            results.setItems(messages);
        }
        catch (AcmOutlookException e)
        {
            disconnectAndRetry(user, e);
        }

        return results;
    }

    @Override
    public OutlookResults<OutlookTaskItem> findTaskItems(AcmOutlookUser user, int start, int maxItems, String sortField,
            boolean sortAscending, SearchFilter filter) throws AcmOutlookConnectionFailedException, AcmOutlookListItemsFailedException
    {
        ExchangeService service = connect(user);

        PropertySet taskProperties = new PropertySet(TaskSchema.DueDate, TaskSchema.StartDate, TaskSchema.CompleteDate,
                TaskSchema.IsComplete, TaskSchema.PercentComplete);

        OutlookResults<OutlookTaskItem> results = new OutlookResults<>();

        try
        {
            FindItemsResults<Item> items = getDao().findItems(service, WellKnownFolderName.Tasks, taskProperties, start, maxItems,
                    sortField, sortAscending, filter);

            populateResultHeaderFields(results, start, maxItems, sortField, sortAscending, items.getTotalCount(), items.isMoreAvailable(),
                    items.getNextPageOffset() == null ? -1 : items.getNextPageOffset());

            List<OutlookTaskItem> tasks = items.getItems().stream().map(this::taskFrom).collect(Collectors.toList());
            results.setItems(tasks);
        }
        catch (AcmOutlookException e)
        {
            disconnectAndRetry(user, e);
        }

        return results;

    }

    private void populateResultHeaderFields(OutlookResults<? extends OutlookItem> results, int start, int maxItems, String sortField,
            boolean sortAscending, int totalCount, boolean isMoreAvailable, int nextStartRow)
    {
        results.setTotalItems(totalCount);
        results.setMoreItemsAvailable(isMoreAvailable);
        results.setCurrentStartIndex(start);
        results.setCurrentMaxItems(maxItems);
        results.setNextStartIndex(nextStartRow);
        results.setCurrentSortField(sortField);
        results.setCurrentSortAscending(sortAscending);
    }

    private OutlookMailItem messageFrom(Item item)
    {
        EmailMessage message = (EmailMessage) item;

        OutlookMailItem omi = new OutlookMailItem();

        try
        {
            populateCoreFields(item, omi);

            omi.setFrom(message.getFrom() == null ? null : message.getFrom().getAddress());
            omi.setSender(message.getSender() == null ? null : message.getSender().getAddress());
            omi.setRead(message.getIsRead());

            return omi;
        }
        catch (ServiceLocalException sle)
        {
            throw new AcmOutlookFindItemsFailedException(sle.getMessage(), sle);
        }
    }

    private OutlookTaskItem taskFrom(Item item)
    {
        Task task = (Task) item;

        OutlookTaskItem oti = new OutlookTaskItem();

        try
        {
            populateCoreFields(item, oti);

            oti.setComplete(task.getIsComplete());
            oti.setCompleteDate(task.getCompleteDate());
            oti.setDueDate(task.getDueDate());
            oti.setPercentComplete(task.getPercentComplete());
            oti.setStartDate(task.getStartDate());

            return oti;
        }
        catch (ServiceLocalException sle)
        {
            throw new AcmOutlookFindItemsFailedException(sle.getMessage(), sle);
        }
    }

    private OutlookCalendarItem calendarFrom(Item item)
    {
        Appointment appt = (Appointment) item;

        OutlookCalendarItem oci = new OutlookCalendarItem();

        try
        {
            populateCoreFields(item, oci);

            oci.setAllDayEvent(appt.getIsAllDayEvent());
            oci.setCancelled(appt.getIsCancelled());
            oci.setMeeting(appt.getIsMeeting());
            oci.setRecurring(appt.getIsRecurring());
            oci.setStartDate(appt.getStart());
            oci.setEndDate(appt.getEnd());
            oci.setFolderId(item.getParentFolderId().getUniqueId());

            return oci;
        }
        catch (ServiceLocalException sle)
        {
            throw new AcmOutlookFindItemsFailedException(sle.getMessage(), sle);
        }
    }

    private OutlookContactItem contactFrom(Item item)
    {
        Contact contact = (Contact) item;

        OutlookContactItem oci = new OutlookContactItem();

        try
        {
            populateCoreFields(item, oci);

            oci.setSurname(contact.getSurname());
            oci.setDisplayName(contact.getDisplayName());
            oci.setCompleteName(contact.getCompleteName() == null ? null : contact.getCompleteName().getFullName());
            oci.setCompanyName(contact.getCompanyName());
            if (contact.getPhoneNumbers().contains(PhoneNumberKey.PrimaryPhone))
            {
                oci.setPrimaryTelephone(contact.getPhoneNumbers().getPhoneNumber(PhoneNumberKey.PrimaryPhone));
            }
            if (contact.getEmailAddresses().contains(EmailAddressKey.EmailAddress1))
            {
                oci.setEmailAddress1(contact.getEmailAddresses().getEmailAddress(EmailAddressKey.EmailAddress1).getAddress());
            }
            if (contact.getEmailAddresses().contains(EmailAddressKey.EmailAddress2))
            {
                oci.setEmailAddress2(contact.getEmailAddresses().getEmailAddress(EmailAddressKey.EmailAddress2).getAddress());
            }

            return oci;
        }
        catch (ServiceLocalException sle)
        {
            throw new AcmOutlookFindItemsFailedException(sle.getMessage(), sle);
        }
    }

    private void populateCoreFields(Item fromOutlook, OutlookItem coreItem) throws ServiceLocalException
    {
        coreItem.setSubject(fromOutlook.getSubject());
        coreItem.setSent(fromOutlook.getDateTimeSent());
        coreItem.setModified(fromOutlook.getLastModifiedTime());
        coreItem.setId(fromOutlook.getId().toString());
        coreItem.setBody(fromOutlook.getBody() == null ? null : fromOutlook.getBody().toString());
        coreItem.setCreated(fromOutlook.getDateTimeCreated());
        coreItem.setSize(fromOutlook.getSize());
    }

    @Override
    public OutlookResults<OutlookCalendarItem> findCalendarItems(String folderId, AcmOutlookUser user, int start, int maxItems,
            String sortField, boolean sortAscending, SearchFilter filter)
            throws AcmOutlookConnectionFailedException, AcmOutlookListItemsFailedException
    {
        ExchangeService service = connect(user);

        PropertySet calendarProperties = new PropertySet(AppointmentSchema.IsAllDayEvent, AppointmentSchema.IsCancelled,
                AppointmentSchema.IsMeeting, AppointmentSchema.IsRecurring, AppointmentSchema.Start, AppointmentSchema.End,
                ItemSchema.ParentFolderId);

        OutlookResults<OutlookCalendarItem> results = new OutlookResults<>();

        try
        {
            FindItemsResults<Item> items = folderId != null
                    ? getDao().findItems(service, folderId, calendarProperties, start, maxItems, sortField, sortAscending, filter)
                    : getDao().findItems(service, WellKnownFolderName.Calendar, calendarProperties, start, maxItems, sortField,
                            sortAscending, filter);

            populateResultHeaderFields(results, start, maxItems, sortField, sortAscending, items.getTotalCount(), items.isMoreAvailable(),
                    items.getNextPageOffset() == null ? -1 : items.getNextPageOffset());

            List<OutlookCalendarItem> appts = items.getItems().stream().map(this::calendarFrom).collect(Collectors.toList());
            results.setItems(appts);
        }
        catch (AcmOutlookException e)
        {
            disconnectAndRetry(user, e);
        }

        return results;

    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.services.email.service.AcmEmailSenderService#sendPlainEmail(java.util.stream.Stream,
     * com.armedia.acm.services.email.model.EmailBuilder, com.armedia.acm.services.email.model.EmailBodyBuilder)
     */
    @Override
    public <T> void sendPlainEmail(Stream<T> emailsDataStream, EmailBuilder<T> emailBuilder, EmailBodyBuilder<T> emailBodyBuilder)
            throws Exception
    {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void sendEmailWithAttachments(EmailWithAttachmentsDTO emailWithAttachmentsDTO, Authentication authentication, AcmUser user)
            throws Exception
    {
        AcmOutlookUser outlookUser;
        if (outlookConfig.getSendNotificationFromSystemUser())
        {
            outlookUser = new AcmOutlookUser(outlookConfig.getSystemUserId(), outlookConfig.getSystemUserEmail(),
                    outlookConfig.getSystemUserPassword());
        }
        else
        {
            OutlookDTO outlookDTO = retrieveOutlookPasswordInternal(authentication);
            outlookUser = new AcmOutlookUser(authentication.getName(), user.getMail(), outlookDTO.getOutlookPassword());
        }

        sendEmail(emailWithAttachmentsDTO, authentication, outlookUser);
    }

    @Override
    public void sendEmail(EmailWithAttachmentsDTO emailWithAttachmentsDTO, Authentication authentication, AcmUser user) throws Exception
    {
        OutlookDTO outlookDTO = retrieveOutlookPasswordInternal(authentication);
        AcmOutlookUser outlookUser = new AcmOutlookUser(authentication.getName(), user.getMail(), outlookDTO.getOutlookPassword());

        sendEmail(emailWithAttachmentsDTO, authentication, outlookUser);
    }

    /**
     * @param emailWithAttachmentsDTO
     * @param authentication
     * @param outlookUser
     * @throws Exception
     */
    private void sendEmail(EmailWithAttachmentsDTO emailWithAttachmentsDTO, Authentication authentication, AcmOutlookUser outlookUser)
            throws Exception
    {
        emailWithAttachmentsDTO.setTemplatingEngine(getTemplatingEngine());

        ExchangeService service = connect(outlookUser);
        EmailMessage emailMessage = new EmailMessage(service);
        emailMessage.setSubject(emailWithAttachmentsDTO.getSubject());
        emailMessage.setBody(MessageBody.getMessageBodyFromText(emailWithAttachmentsDTO.getMessageBody()));
        emailMessage.getBody().setBodyType(BodyType.HTML);
        emailMessage.getToRecipients().add(outlookConfig.getSystemUserEmail());

        if (emailWithAttachmentsDTO.getEmailAddresses() != null && !emailWithAttachmentsDTO.getEmailAddresses().isEmpty())
        {
            for (String emailAddress : emailWithAttachmentsDTO.getEmailAddresses())
            {
                emailMessage.getToRecipients().add(emailAddress);
            }
        }

        List<EcmFile> attachedFiles = new ArrayList<>();

        addAttachmentsAndSend(emailWithAttachmentsDTO.getAttachmentIds(), emailMessage, attachedFiles);

        // use the first method if you don't require a copy
        // use the second if a copy of the sent email is needed
        // emailMessage.send();
        // emailMessage.sendAndSaveCopy();

        // Fires an audit event for each successfully emailed file
        for (EcmFile emailedFile : attachedFiles)
        {
            outlookEventPublisher.publishFileEmailedEvent(emailedFile, authentication);
        }
    }

    @Override
    public void sendEmailWithAttachmentsAndLinks(EmailWithAttachmentsAndLinksDTO emailWithAttachmentsAndLinksDTO,
            Authentication authentication, AcmUser user) throws Exception
    {

        AcmOutlookUser outlookUser;

        if (outlookConfig.getSendNotificationFromSystemUser())
        {
            outlookUser = new AcmOutlookUser(outlookConfig.getSystemUserId(), outlookConfig.getSystemUserEmail(),
                    outlookConfig.getSystemUserPassword());
        }
        else
        {
            OutlookDTO outlookDTO = retrieveOutlookPasswordInternal(authentication);
            outlookUser = new AcmOutlookUser(authentication.getName(), user.getMail(), outlookDTO.getOutlookPassword());
        }

        sendEmail(emailWithAttachmentsAndLinksDTO, authentication, outlookUser);
    }

    @Override
    public void sendEmail(EmailWithAttachmentsAndLinksDTO emailWithAttachmentsAndLinksDTO, Authentication authentication, AcmUser user)
            throws Exception
    {
        OutlookDTO outlookDTO = retrieveOutlookPasswordInternal(authentication);
        AcmOutlookUser outlookUser = new AcmOutlookUser(authentication.getName(), user.getMail(), outlookDTO.getOutlookPassword());

        sendEmail(emailWithAttachmentsAndLinksDTO, authentication, outlookUser);
    }

    /**
     * @param emailWithAttachmentsAndLinksDTO
     * @param authentication
     * @param outlookUser
     */
    private void sendEmail(EmailWithAttachmentsAndLinksDTO emailWithAttachmentsAndLinksDTO, Authentication authentication,
            AcmOutlookUser outlookUser)
    {
        ExchangeService service = connect(outlookUser);

        if (emailWithAttachmentsAndLinksDTO.getEmailAddresses() != null && !emailWithAttachmentsAndLinksDTO.getEmailAddresses().isEmpty())
        {
            for (String emailAddress : emailWithAttachmentsAndLinksDTO.getEmailAddresses())
            {

                try
                {
                    EmailMessage emailMessage = new EmailMessage(service);
                    emailMessage.getToRecipients().add(emailAddress);
                    emailMessage.setSubject(emailWithAttachmentsAndLinksDTO.getSubject());
                    emailMessage.setBody(MessageBody
                            .getMessageBodyFromText(generateBody(emailWithAttachmentsAndLinksDTO, emailAddress, authentication)));
                    emailMessage.getBody().setBodyType(BodyType.HTML);

                    List<EcmFile> attachedFiles = new ArrayList<>();
                    addAttachmentsAndSend(emailWithAttachmentsAndLinksDTO.getAttachmentIds(), emailMessage, attachedFiles);

                    /*
                     * // Fires an audit event for each successfully emailed file for (EcmFile emailedFile :
                     * attachedFiles) { outlookEventPublisher.publishFileEmailedEvent(emailedFile, authentication); }
                     */

                }
                catch (Exception e)
                {
                    log.error(String.format("Could not send email to %s from %s.", emailAddress, outlookUser.getEmailAddress()));
                }
            }
        }
    }

    private void addAttachmentsAndSend(List<Long> attachmentIds, EmailMessage emailMessage,
            List<EcmFile> attachedFiles) throws Exception
    {

        List<File> tmpFiles = new ArrayList<>();
        List<InputStream> tmpFilesInputStream = new ArrayList<>();

        if (Objects.nonNull(attachmentIds) && !attachmentIds.isEmpty())
        {
            for (Long attachmentId : attachmentIds)
            {
                EcmFile ecmFile = getEcmFileService().findById(attachmentId);
                InputStream contents = getEcmFileService().downloadAsInputStream(attachmentId, ecmFile.getActiveVersionTag());
                String fileName = ecmFile.getFileName();
                String fileKey = fileName;

                File pdfConvertedFile = null;
                InputStream pdfConvertedFileInputStream = null;

                if (ecmFile.getFileActiveVersionNameExtension() != null)
                {
                    fileName = fileName + ecmFile.getFileActiveVersionNameExtension();
                }

                if (getEmailSenderConfigurationService().readConfiguration().getConvertDocumentsToPdf() &&
                        Objects.nonNull(ecmFile) && !".pdf".equals(ecmFile.getFileActiveVersionNameExtension()))
                {
                    try
                    {
                        pdfConvertedFile = getDefaultFolderAndFileConverter().convertAndReturnConvertedFile(ecmFile);
                        if(pdfConvertedFile != null)
                        {
                            pdfConvertedFileInputStream = new FileInputStream(pdfConvertedFile);
                            contents = pdfConvertedFileInputStream;
                            fileName = fileKey.concat(".pdf");

                            tmpFiles.add(pdfConvertedFile);
                            tmpFilesInputStream.add(pdfConvertedFileInputStream);
                        }
                    }
                    catch (ConversionException | FileNotFoundException e)
                    {
                        log.error("Could not convert attached document [{}] to PDF", fileName);
                    }
                }

                emailMessage.getAttachments().addFileAttachment(fileName, contents);
                attachedFiles.add(ecmFile);
            }
        }
        emailMessage.sendAndSaveCopy();

        // CLEAN TEMP FILES AND INPUT STREAMS

        tmpFilesInputStream.forEach(inputStream -> {
            try
            {
                inputStream.close();
            }
            catch (IOException e)
            {
                log.warn("Could not close input stream", e);
            }
        });

        tmpFiles.forEach(FileUtils::deleteQuietly);

        ///
    }

    @Override
    public List<EmailWithEmbeddedLinksResultDTO> sendEmailWithEmbeddedLinks(EmailWithEmbeddedLinksDTO emailDTO,
            Authentication authentication, AcmUser user) throws Exception
    {

        OutlookDTO outlookDTO = retrieveOutlookPasswordInternal(authentication);
        AcmOutlookUser outlookUser = new AcmOutlookUser(authentication.getName(), user.getMail(), outlookDTO.getOutlookPassword());

        if (outlookConfig.getSendNotificationFromSystemUser())
        {
            outlookUser = new AcmOutlookUser(outlookConfig.getSystemUserId(), outlookConfig.getSystemUserEmail(),
                    outlookConfig.getSystemUserPassword());
        }

        ExchangeService service = connect(outlookUser);

        List<EmailWithEmbeddedLinksResultDTO> results = new ArrayList<>();

        if (emailDTO.getEmailAddresses() != null && !emailDTO.getEmailAddresses().isEmpty())
        {
            for (String emailAddress : emailDTO.getEmailAddresses())
            {

                try
                {
                    EmailMessage emailMessage = new EmailMessage(service);
                    emailMessage.getToRecipients().add(emailAddress);
                    emailMessage.setSubject(emailDTO.getSubject());
                    emailMessage.setBody(MessageBody.getMessageBodyFromText(generateBody(emailDTO, emailAddress, authentication)));
                    emailMessage.getBody().setBodyType(BodyType.HTML);

                    emailMessage.sendAndSaveCopy();

                    results.add(new EmailWithEmbeddedLinksResultDTO(emailAddress, true));

                }
                catch (Exception e)
                {
                    log.error(String.format("Could not send email to %s from %s.", emailAddress, outlookUser.getEmailAddress()));
                    results.add(new EmailWithEmbeddedLinksResultDTO(emailAddress, false));
                }

            }
        }

        return results;

    }

    private String generateBody(EmailWithEmbeddedLinksDTO emailDTO, String emailAddress, Authentication authentication)
    {
        return getAcmEmailContentGeneratorService().generateEmailBody(emailDTO, emailAddress, authentication);
    }

    @Override
    public OutlookResults<OutlookContactItem> findContactItems(AcmOutlookUser user, int start, int maxItems, String sortField,
            boolean sortAscending, SearchFilter filter) throws AcmOutlookConnectionFailedException, AcmOutlookListItemsFailedException
    {
        ExchangeService service = connect(user);

        PropertySet contactProperties = new PropertySet(ContactSchema.Surname, ContactSchema.DisplayName, ContactSchema.CompleteName,
                ContactSchema.CompanyName, ContactSchema.PrimaryPhone, ContactSchema.EmailAddress1, ContactSchema.EmailAddress2);

        OutlookResults<OutlookContactItem> results = new OutlookResults<>();

        try
        {
            FindItemsResults<Item> items = getDao().findItems(service, WellKnownFolderName.Contacts, contactProperties, start, maxItems,
                    sortField, sortAscending, filter);

            populateResultHeaderFields(results, start, maxItems, sortField, sortAscending, items.getTotalCount(), items.isMoreAvailable(),
                    items.getNextPageOffset() == null ? -1 : items.getNextPageOffset());

            List<OutlookContactItem> contacts = items.getItems().stream().map(this::contactFrom).collect(Collectors.toList());
            results.setItems(contacts);
        }
        catch (AcmOutlookException e)
        {
            disconnectAndRetry(user, e);
        }

        return results;

    }

    @Override
    public OutlookCalendarItem createOutlookAppointment(AcmOutlookUser user, OutlookCalendarItem calendarItem)
            throws AcmOutlookConnectionFailedException, AcmOutlookCreateItemFailedException
    {

        ExchangeService service = connect(user);
        Folder folder;

        OutlookCalendarItem retval = null;

        try
        {
            try
            {
                folder = calendarItem.getFolderId() == null ? Folder.bind(service, WellKnownFolderName.Calendar)
                        : Folder.bind(service, new FolderId(calendarItem.getFolderId()));
            }
            catch (Exception e)
            {
                throw new AcmOutlookException("Can't bind to folder id [" + calendarItem.getFolderId() + " ]!", e);
            }

            retval = getDao().createCalendarAppointment(service, folder, calendarItem);

            List<AcmContainer> containers = getAcmContainerDao().findByCalendarFolderId(calendarItem.getFolderId());

            // we expect that exactly one container is returned
            if (containers.size() == 1)
            {
                getOutlookEventPublisher().publishCalendarEventAdded(retval, user.getUserId(), containers.get(0).getContainerObjectId(),
                        containers.get(0).getContainerObjectType());
            }
            else
            {
                log.error(String.format("Unexpected number of containers=%d returned for calendarFolderId=%d", containers.size(),
                        Integer.parseInt(calendarItem.getFolderId())));
            }

        }
        catch (AcmOutlookException e)
        {
            disconnectAndRetry(user, e);

        }

        // we can't return null, since either the method will work and we return an apppoinment, or else the retry
        // handler will stop trying to retry and we'll throw the exception.
        return retval;
    }

    private void disconnectAndRetry(AcmOutlookUser user, AcmOutlookException e)
    {
        // disconnect the user (which evicts from cache), then rethrow; the Spring retry handler should call use
        // again, maybe it will work this time... since connections do go bad from time to time.
        log.info("Something went wrong in Outlook; disconnecting user.");
        getDao().disconnect(user);
        throw e;
    }

    @Override
    public OutlookTaskItem createOutlookTaskItem(AcmOutlookUser user, WellKnownFolderName folderName, OutlookTaskItem taskItem)
            throws AcmOutlookConnectionFailedException, AcmOutlookCreateItemFailedException
    {
        ExchangeService service = connect(user);
        Folder folder;
        OutlookTaskItem retval = null;

        try
        {
            try
            {
                folder = Folder.bind(service, folderName);
            }
            catch (Exception e)
            {
                throw new AcmOutlookException("Can't bind to folder(" + folderName + ")!", e);
            }
            retval = getDao().createTaskItem(service, folder, taskItem);
        }
        catch (AcmOutlookException e)
        {
            disconnectAndRetry(user, e);
        }

        return retval;

    }

    @Override
    public OutlookContactItem createOutlookContactItem(AcmOutlookUser user, WellKnownFolderName folderName, OutlookContactItem contactItem)
            throws AcmOutlookConnectionFailedException, AcmOutlookCreateItemFailedException
    {

        ExchangeService service = connect(user);
        Folder folder;
        OutlookContactItem retval = null;

        try
        {
            try
            {
                folder = Folder.bind(service, folderName);
            }
            catch (Exception e)
            {
                throw new AcmOutlookException("Can't bind to folder(" + folderName + ")!", e);
            }

            retval = getDao().createContactItem(service, folder, contactItem);
        }
        catch (AcmOutlookException e)
        {
            disconnectAndRetry(user, e);
        }

        return retval;

    }

    @Override
    public void deleteItem(AcmOutlookUser user, String itemId, DeleteMode deleteMode) throws AcmOutlookException
    {
        ExchangeService service = connect(user);

        try
        {
            getDao().deleteItem(service, itemId, deleteMode);
        }
        catch (AcmOutlookException e)
        {
            disconnectAndRetry(user, e);
        }

    }

    @Override
    public void deleteAllItemsFoundByExtendedProperty(String folderId, AcmOutlookUser user,
            ExtendedPropertyDefinition extendedPropertyDefinition, Object extendedPropertyValue)
    {
        // disconnectAndRetry is not needed here since this method calls other public methods in this service class,
        // and does not call any DAO methods.
        try
        {
            SearchFilter filter = new SearchFilter.IsEqualTo(extendedPropertyDefinition, extendedPropertyValue);

            OutlookResults<OutlookCalendarItem> results;
            // In case there are more than one, remove all (this cannot be the case, only one will exist, but do this to
            // be 100% sure)
            do
            {
                // This code always will remove the items if found. For that reason "start" and "maxItems" are set to 0
                // and 50.
                // There is no sense to change "start" index in the next iteration because the next iteration some of
                // them will be removed
                results = findCalendarItems(folderId, user, 0, 50, null, false, filter);

                if (results != null && results.getItems() != null)
                {
                    results.getItems().stream().forEach(element -> deleteItem(user, element.getId(), DeleteMode.HardDelete));
                }
            } while (results != null && results.getItems() != null && results.getItems().size() > 0);
        }
        catch (Exception e)
        {
            log.error("Error while removing all items found by extended property.", e);
        }

    }

    @Override
    public void deleteAppointmentItem(AcmOutlookUser user, String appointmentId, Boolean recurring, DeleteMode deleteMode)
    {
        ExchangeService service = connect(user);

        try
        {
            getDao().deleteAppointmentItem(service, appointmentId, recurring, deleteMode);
        }
        catch (AcmOutlookException e)
        {
            disconnectAndRetry(user, e);
        }

    }

    @Override
    public OutlookFolder createFolder(AcmOutlookUser user, Long objectId, String objectType, WellKnownFolderName parentFolderName,
            OutlookFolder newFolder) throws AcmOutlookItemNotFoundException, AcmOutlookCreateItemFailedException
    {
        ExchangeService service = connect(user);

        OutlookFolder retval = null;

        try
        {
            retval = getDao().createFolder(service, user.getEmailAddress(), parentFolderName, newFolder);
        }
        catch (AcmOutlookException e)
        {
            disconnectAndRetry(user, e);
        }

        try
        {
            AcmOutlookFolderCreator folderCreator = folderCreatorDao.getFolderCreator(user.getEmailAddress(), user.getOutlookPassword());
            folderCreatorDao.recordFolderCreator(folderCreator, objectId, objectType);
        }
        catch (AcmOutlookFolderCreatorDaoException e)
        {
            disconnectAndRetry(user, new AcmOutlookException(e));
        }

        return retval;

    }

    @Override
    public OutlookFolder getFolder(AcmOutlookUser user, String folderId)
            throws AcmOutlookItemNotFoundException, AcmOutlookCreateItemFailedException
    {

        ExchangeService service = connect(user);
        OutlookFolder retval = null;

        try
        {
            Folder folder = getDao().getFolder(service, folderId);

            try
            {
                retval = mapFolderToOutlookFolder(folder);
            }
            catch (ServiceLocalException e)
            {
                throw new AcmOutlookConnectionFailedException("Error retrieving folder properties. ", e);
            }
        }
        catch (AcmOutlookException e)
        {
            disconnectAndRetry(user, e);
        }

        return retval;
    }

    @Override
    public void deleteFolder(AcmOutlookUser user, String folderId, DeleteMode deleteMode) throws AcmOutlookItemNotFoundException
    {

        ExchangeService service = connect(user);

        try
        {
            getDao().deleteFolder(service, folderId, deleteMode);
        }
        catch (AcmOutlookException e)
        {
            disconnectAndRetry(user, e);
        }
    }

    @Override
    public void addFolderPermission(AcmOutlookUser user, String folderId, OutlookFolderPermission permission)
            throws AcmOutlookItemNotFoundException
    {
        ExchangeService service = connect(user);

        try
        {
            getDao().addFolderPermission(service, folderId, permission);
        }
        catch (AcmOutlookException e)
        {
            disconnectAndRetry(user, e);
        }
    }

    @Override
    public void removeFolderPermission(AcmOutlookUser user, String folderId, OutlookFolderPermission permission)
            throws AcmOutlookItemNotFoundException
    {
        ExchangeService service = connect(user);

        try
        {
            getDao().removeFolderPermission(service, folderId, permission);
        }
        catch (AcmOutlookException e)
        {
            disconnectAndRetry(user, e);
        }

    }

    @Override
    public void updateFolderPermissions(AcmOutlookUser user, String calendarFolderId, List<OutlookFolderPermission> folderPermissions)
    {

        List<OutlookFolderPermission> existingFolderPermissions = getFolder(user, calendarFolderId).getPermissions();
        List<OutlookFolderPermission> folderPermissionsToBeAdded = new LinkedList<>();
        List<OutlookFolderPermission> folderPermissionsToBeRemoved = new ArrayList<>(existingFolderPermissions);

        for (OutlookFolderPermission outlookFolderPermission : folderPermissions)
        {
            if (!existingFolderPermissions.contains(outlookFolderPermission))
            {
                folderPermissionsToBeAdded.add(outlookFolderPermission);// this is new permissions and needs to be added
            }
            else
            {
                folderPermissionsToBeRemoved.remove(outlookFolderPermission);// this is existing permission and not to
                // be removed
            }
        }

        for (OutlookFolderPermission outlookFolderPermission : folderPermissionsToBeAdded)
        {
            try
            {
                addFolderPermission(user, calendarFolderId, outlookFolderPermission);
            }
            catch (Exception e)
            {
                log.error("Can't add permission for user email: {}, reason: ", outlookFolderPermission.getEmail(), e.getMessage());
            }
        }

        for (OutlookFolderPermission outlookFolderPermission : folderPermissionsToBeRemoved)
        {
            try
            {
                if (!outlookFolderPermission.isFolderOwner())
                {
                    removeFolderPermission(user, calendarFolderId, outlookFolderPermission);
                }
            }
            catch (Exception e)
            {
                log.error("Can't remove permission for user email: {}, reason: ", outlookFolderPermission.getEmail(), e.getMessage());
            }
        }
    }

    protected ExchangeService connect(AcmOutlookUser user) throws AcmOutlookConnectionFailedException
    {
        return getDao().connect(user);
    }

    private OutlookFolder mapFolderToOutlookFolder(Folder folder) throws ServiceLocalException
    {
        OutlookFolder of = new OutlookFolder();
        of.setDisplayName(folder.getDisplayName());
        of.setId(folder.getId().getUniqueId());
        of.setParentId(folder.getParentFolderId().getUniqueId());

        List<OutlookFolderPermission> permissions = folder.getPermissions().getItems().stream().map(this::mapFolderPermission)
                .collect(Collectors.toCollection(() -> new LinkedList<>()));
        of.setPermissions(permissions);

        return of;

    }

    private OutlookFolderPermission mapFolderPermission(FolderPermission folderPermission)
    {
        OutlookFolderPermission outlookFolderPermission = new OutlookFolderPermission();
        outlookFolderPermission.setEmail(folderPermission.getUserId().getPrimarySmtpAddress());
        outlookFolderPermission.setLevel(outlookFolderPermission.getLevel());
        outlookFolderPermission.setCanCreateItems(folderPermission.getCanCreateItems());
        outlookFolderPermission.setCanCreateSubFolders(folderPermission.getCanCreateSubFolders());
        outlookFolderPermission.setFolderOwner(folderPermission.getIsFolderOwner());
        outlookFolderPermission.setFolderVisible(folderPermission.getIsFolderVisible());
        outlookFolderPermission.setFolderContact(folderPermission.getIsFolderContact());
        outlookFolderPermission.setEditItems(folderPermission.getEditItems());
        outlookFolderPermission.setDeleteItems(folderPermission.getDeleteItems());
        outlookFolderPermission.setReadItems(outlookFolderPermission.getReadItems());

        return outlookFolderPermission;
    }

    public OutlookDao getDao()
    {
        return dao;
    }

    @Override
    public void setDao(OutlookDao dao)
    {
        this.dao = dao;
    }

    /**
     * @param folderCreatorDao
     *            the folderCreatorDao to set
     */
    public void setFolderCreatorDao(AcmOutlookFolderCreatorDao folderCreatorDao)
    {
        this.folderCreatorDao = folderCreatorDao;
    }

    public EcmFileService getEcmFileService()
    {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
    }

    public OutlookEventPublisher getOutlookEventPublisher()
    {
        return outlookEventPublisher;
    }

    public void setOutlookEventPublisher(OutlookEventPublisher outlookEventPublisher)
    {
        this.outlookEventPublisher = outlookEventPublisher;
    }

    public AcmContainerDao getAcmContainerDao()
    {
        return acmContainerDao;
    }

    public void setAcmContainerDao(AcmContainerDao acmContainerDao)
    {
        this.acmContainerDao = acmContainerDao;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public OutlookDTO retrieveOutlookPassword(Authentication authentication) throws AcmEncryptionException
    {
        return retrieveOutlookPasswordInternal(authentication);
    }

    private OutlookDTO retrieveOutlookPasswordInternal(Authentication authentication) throws AcmEncryptionException
    {
        OutlookDTO retval = getOutlookPasswordDao().retrieveOutlookPassword(authentication);

        // decrypt password and decode it from BASE64
        String sha256Hex = DigestUtils.sha256Hex(authentication.getCredentials().toString());
        byte[] decryptedPassword = acmCryptoUtils.decryptData(sha256Hex.getBytes(),
                Base64.getDecoder().decode(retval.getOutlookPassword().getBytes()), true);

        retval.setOutlookPassword(new String(decryptedPassword));

        return retval;
    }

    @Override
    public void saveOutlookPassword(Authentication authentication, OutlookDTO in) throws AcmEncryptionException
    {

        // encrypt password and encode it to BASE64
        String sha256Hex = DigestUtils.sha256Hex(authentication.getCredentials().toString());

        byte[] encryptedPassword = acmCryptoUtils.encryptData(sha256Hex.getBytes(), in.getOutlookPassword().getBytes(), true);
        in.setOutlookPassword(Base64.getEncoder().encodeToString(encryptedPassword));
        getOutlookPasswordDao().saveOutlookPassword(authentication, in);

    }

    @Override
    public OutlookPassword getOutlookPasswordForUser(String userId)
    {
        return getOutlookPasswordDao().findByUserId(userId);
    }

    public AcmCryptoUtils getAcmCryptoUtils()
    {
        return acmCryptoUtils;
    }

    public void setAcmCryptoUtils(AcmCryptoUtils acmCryptoUtils)
    {
        this.acmCryptoUtils = acmCryptoUtils;
    }

    public OutlookPasswordDao getOutlookPasswordDao()
    {
        return outlookPasswordDao;
    }

    public void setOutlookPasswordDao(OutlookPasswordDao outlookPasswordDao)
    {
        this.outlookPasswordDao = outlookPasswordDao;
    }

    public AcmEmailContentGeneratorService getAcmEmailContentGeneratorService()
    {
        return acmEmailContentGeneratorService;
    }

    public void setAcmEmailContentGeneratorService(AcmEmailContentGeneratorService acmEmailContentGeneratorService)
    {
        this.acmEmailContentGeneratorService = acmEmailContentGeneratorService;
    }

    public TemplatingEngine getTemplatingEngine()
    {
        return templatingEngine;
    }

    public void setTemplatingEngine(TemplatingEngine templatingEngine)
    {
        this.templatingEngine = templatingEngine;
    }

    public EmailSenderConfigurationServiceImpl getEmailSenderConfigurationService()
    {
        return emailSenderConfigurationService;
    }

    public void setEmailSenderConfigurationService(EmailSenderConfigurationServiceImpl emailSenderConfigurationService)
    {
        this.emailSenderConfigurationService = emailSenderConfigurationService;
    }

    public DefaultFolderAndFileConverter getDefaultFolderAndFileConverter()
    {
        return defaultFolderAndFileConverter;
    }

    public void setDefaultFolderAndFileConverter(DefaultFolderAndFileConverter defaultFolderAndFileConverter)
    {
        this.defaultFolderAndFileConverter = defaultFolderAndFileConverter;
    }

    public OutlookConfig getOutlookConfig()
    {
        return outlookConfig;
    }

    public void setOutlookConfig(OutlookConfig outlookConfig)
    {
        this.outlookConfig = outlookConfig;
    }
}
