package com.armedia.acm.service.outlook.service.impl;

import com.armedia.acm.service.outlook.dao.OutlookDao;
import com.armedia.acm.service.outlook.exception.AcmOutlookConnectionFailedException;
import com.armedia.acm.service.outlook.exception.AcmOutlookCreateItemFailedException;
import com.armedia.acm.service.outlook.exception.AcmOutlookException;
import com.armedia.acm.service.outlook.exception.AcmOutlookFindItemsFailedException;
import com.armedia.acm.service.outlook.exception.AcmOutlookItemNotFoundException;
import com.armedia.acm.service.outlook.exception.AcmOutlookListItemsFailedException;
import com.armedia.acm.service.outlook.model.AcmOutlookUser;
import com.armedia.acm.service.outlook.model.OutlookCalendarItem;
import com.armedia.acm.service.outlook.model.OutlookContactItem;
import com.armedia.acm.service.outlook.model.OutlookFolder;
import com.armedia.acm.service.outlook.model.OutlookFolderPermission;
import com.armedia.acm.service.outlook.model.OutlookItem;
import com.armedia.acm.service.outlook.model.OutlookMailItem;
import com.armedia.acm.service.outlook.model.OutlookResults;
import com.armedia.acm.service.outlook.model.OutlookTaskItem;
import com.armedia.acm.service.outlook.service.OutlookFolderService;
import com.armedia.acm.service.outlook.service.OutlookService;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.PropertySet;
import microsoft.exchange.webservices.data.core.service.folder.Folder;
import microsoft.exchange.webservices.data.core.service.item.Appointment;
import microsoft.exchange.webservices.data.core.service.item.Contact;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.core.service.item.Item;
import microsoft.exchange.webservices.data.core.service.item.Task;
import microsoft.exchange.webservices.data.core.service.schema.AppointmentSchema;
import microsoft.exchange.webservices.data.core.service.schema.ContactSchema;
import microsoft.exchange.webservices.data.core.service.schema.EmailMessageSchema;
import microsoft.exchange.webservices.data.core.service.schema.TaskSchema;
import microsoft.exchange.webservices.data.enumeration.DeleteMode;
import microsoft.exchange.webservices.data.enumeration.EmailAddressKey;
import microsoft.exchange.webservices.data.enumeration.PhoneNumberKey;
import microsoft.exchange.webservices.data.enumeration.WellKnownFolderName;
import microsoft.exchange.webservices.data.exception.ServiceLocalException;
import microsoft.exchange.webservices.data.property.complex.FolderPermission;
import microsoft.exchange.webservices.data.search.FindItemsResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by armdev on 4/20/15.
 */
public class OutlookServiceImpl implements OutlookService, OutlookFolderService
{
    private transient final Logger log = LoggerFactory.getLogger(getClass());
    @Value("${outlook.exchange.system_user_email}")
    private String systemUserEmail;
    @Value("${outlook.exchange.system_user_email_password}")
    private String systemUserEmailPassword;
    @Value("${outlook.exchange.system_user_id}")
    private String systemUserId;

    private OutlookDao dao;

    @Override
    public OutlookResults<OutlookMailItem> findMailItems(AcmOutlookUser user, int start, int maxItems, String sortField,
                                                         boolean sortAscending)
            throws AcmOutlookConnectionFailedException, AcmOutlookListItemsFailedException
    {
        ExchangeService service = connect(user);

        PropertySet mailProperties = new PropertySet(
                EmailMessageSchema.From,
                EmailMessageSchema.Sender,
                EmailMessageSchema.IsRead
        );

        FindItemsResults<Item> items = getDao().findItems(service, WellKnownFolderName.Inbox, mailProperties, start,
                maxItems, sortField, sortAscending);

        OutlookResults<OutlookMailItem> results = new OutlookResults<>();
        populateResultHeaderFields(results, start, maxItems, sortField, sortAscending, items.getTotalCount(),
                items.isMoreAvailable(), items.getNextPageOffset() == null ? -1 : items.getNextPageOffset());

        List<OutlookMailItem> messages = items.getItems().stream().map(this::messageFrom).collect(Collectors.toList());
        results.setItems(messages);

        return results;
    }

    @Override
    public OutlookResults<OutlookTaskItem> findTaskItems(AcmOutlookUser user, int start, int maxItems, String sortField,
                                                         boolean sortAscending)
            throws AcmOutlookConnectionFailedException, AcmOutlookListItemsFailedException
    {
        ExchangeService service = connect(user);

        PropertySet taskProperties = new PropertySet(
                TaskSchema.DueDate,
                TaskSchema.StartDate,
                TaskSchema.CompleteDate,
                TaskSchema.IsComplete,
                TaskSchema.PercentComplete
        );

        FindItemsResults<Item> items = getDao().findItems(service, WellKnownFolderName.Tasks, taskProperties, start,
            maxItems, sortField, sortAscending);

        OutlookResults<OutlookTaskItem> results = new OutlookResults<>();
        populateResultHeaderFields(results, start, maxItems, sortField, sortAscending, items.getTotalCount(),
                items.isMoreAvailable(), items.getNextPageOffset() == null ? - 1 : items.getNextPageOffset());

        List<OutlookTaskItem> tasks = items.getItems().stream().map(this::taskFrom).collect(Collectors.toList());
        results.setItems(tasks);

        return results;
    }

    private void populateResultHeaderFields(OutlookResults<? extends OutlookItem> results, int start, int maxItems,
                                            String sortField, boolean sortAscending, int totalCount,
                                            boolean isMoreAvailable, int nextStartRow)
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
            if(contact.getPhoneNumbers().contains(PhoneNumberKey.PrimaryPhone))
                oci.setPrimaryTelephone(contact.getPhoneNumbers().getPhoneNumber(PhoneNumberKey.PrimaryPhone));
            if(contact.getEmailAddresses().contains(EmailAddressKey.EmailAddress1))
                oci.setEmailAddress1(contact.getEmailAddresses().getEmailAddress(EmailAddressKey.EmailAddress1).getAddress());
            if(contact.getEmailAddresses().contains(EmailAddressKey.EmailAddress2))
                oci.setEmailAddress2(contact.getEmailAddresses().getEmailAddress(EmailAddressKey.EmailAddress2).getAddress());

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
    public OutlookResults<OutlookCalendarItem> findCalendarItems(AcmOutlookUser user, int start, int maxItems, String sortField,
                                                                 boolean sortAscending)
            throws AcmOutlookConnectionFailedException, AcmOutlookListItemsFailedException
    {
        ExchangeService service = connect(user);

        PropertySet calendarProperties = new PropertySet(
                AppointmentSchema.IsAllDayEvent,
                AppointmentSchema.IsCancelled,
                AppointmentSchema.IsMeeting,
                AppointmentSchema.IsRecurring,
                AppointmentSchema.Start,
                AppointmentSchema.End
        );

        FindItemsResults<Item> items = getDao().findItems(service, WellKnownFolderName.Calendar, calendarProperties, start,
                maxItems, sortField, sortAscending);

        OutlookResults<OutlookCalendarItem> results = new OutlookResults<>();
        populateResultHeaderFields(results, start, maxItems, sortField, sortAscending, items.getTotalCount(),
                items.isMoreAvailable(), items.getNextPageOffset() == null ? - 1 : items.getNextPageOffset());

        List<OutlookCalendarItem> appts = items.getItems().stream().map(this::calendarFrom).collect(Collectors.toList());
        results.setItems(appts);

        return results;
    }

    @Override
    public OutlookResults<OutlookContactItem> findContactItems(AcmOutlookUser user, int start, int maxItems, String sortField,
                                                               boolean sortAscending)
            throws AcmOutlookConnectionFailedException, AcmOutlookListItemsFailedException
    {
        ExchangeService service = connect(user);

        PropertySet contactProperties = new PropertySet(
                ContactSchema.Surname,
                ContactSchema.DisplayName,
                ContactSchema.CompleteName,
                ContactSchema.CompanyName,
                ContactSchema.PrimaryPhone,
                ContactSchema.EmailAddress1,
                ContactSchema.EmailAddress2
        );

        FindItemsResults<Item> items = getDao().findItems(service, WellKnownFolderName.Contacts, contactProperties, start,
                maxItems, sortField, sortAscending);

        OutlookResults<OutlookContactItem> results = new OutlookResults<>();
        populateResultHeaderFields(results, start, maxItems, sortField, sortAscending, items.getTotalCount(),
                items.isMoreAvailable(), items.getNextPageOffset() == null ? - 1 : items.getNextPageOffset());

        List<OutlookContactItem> contacts = items.getItems().stream().map(this::contactFrom).collect(Collectors.toList());
        results.setItems(contacts);

        return results;
    }

    @Override
    public OutlookCalendarItem createOutlookAppointment(AcmOutlookUser user, WellKnownFolderName folderName, OutlookCalendarItem calendarItem) throws AcmOutlookConnectionFailedException, AcmOutlookCreateItemFailedException {
        ExchangeService service = connect(user);
        Folder folder;
        try {
            folder = Folder.bind(service, folderName);
        } catch (Exception e) {
            throw new AcmOutlookException("Can't bind to folder(" + folderName + ")!", e);
        }
        return getDao().createCalendarAppointment(service, folder, calendarItem);
    }

    @Override
    public OutlookTaskItem createOutlookTaskItem(AcmOutlookUser user, WellKnownFolderName folderName, OutlookTaskItem taskItem) throws AcmOutlookConnectionFailedException, AcmOutlookCreateItemFailedException {
        ExchangeService service = connect(user);
        Folder folder;
        try {
            folder = Folder.bind(service, folderName);
        } catch (Exception e) {
            throw new AcmOutlookException("Can't bind to folder(" + folderName + ")!", e);
        }
        return getDao().createTaskItem(service, folder, taskItem);
    }

    @Override
    public OutlookContactItem createOutlookContactItem(AcmOutlookUser user, WellKnownFolderName folderName, OutlookContactItem contactItem) throws AcmOutlookConnectionFailedException, AcmOutlookCreateItemFailedException {
        ExchangeService service = connect(user);
        Folder folder;
        try {
            folder = Folder.bind(service, folderName);
        } catch (Exception e) {
            throw new AcmOutlookException("Can't bind to folder(" + folderName + ")!", e);
        }
        return getDao().createContactItem(service, folder, contactItem);
    }

    @Override
    public void deleteItem(AcmOutlookUser user, String itemId, DeleteMode deleteMode) throws AcmOutlookException, AcmOutlookItemNotFoundException {
        ExchangeService service = connect(user);
        getDao().deleteItem(service, itemId, deleteMode);
    }

    @Override
    public void deleteAppointmentItem(AcmOutlookUser user, String appointmentId, Boolean recurring, DeleteMode deleteMode) {
        ExchangeService service = connect(user);
        getDao().deleteAppointmentItem(service, appointmentId, recurring, deleteMode);
    }
    @Override
    public OutlookFolder createFolder(AcmOutlookUser user,
                                      WellKnownFolderName parentFolderName,
                                      OutlookFolder newFolder) throws AcmOutlookItemNotFoundException, AcmOutlookCreateItemFailedException {
        ExchangeService service = connect(user);
        return getDao().createFolder(service, user.getEmailAddress(), parentFolderName, newFolder);
    }

    @Override
    public OutlookFolder createFolder(WellKnownFolderName parentFolderName,
                                      OutlookFolder newFolder) throws AcmOutlookItemNotFoundException, AcmOutlookCreateItemFailedException {
        return createFolder(getAcmSystemOutlookUser(), parentFolderName, newFolder);
    }

    @Override
    public OutlookFolder getFolder(AcmOutlookUser user, String folderId) throws AcmOutlookItemNotFoundException, AcmOutlookCreateItemFailedException {
        ExchangeService service = connect(user);
        Folder folder = getDao().getFolder(service, folderId);

        try {
            return mapFolderToOutlookFolder(folder);
        } catch (ServiceLocalException e) {
            throw new AcmOutlookConnectionFailedException("Error retrieving folder properties. ", e);
        }
    }


    @Override
    public void deleteFolder(AcmOutlookUser user,
                             String folderId,
                             DeleteMode deleteMode) throws AcmOutlookItemNotFoundException {
        ExchangeService service = connect(user);
        getDao().deleteFolder(service, folderId, deleteMode);
    }

    @Override
    public void deleteFolder(String folderId,
                             DeleteMode deleteMode) throws AcmOutlookItemNotFoundException {
        deleteFolder(getAcmSystemOutlookUser(), folderId, deleteMode);
    }

    @Override
    public void addFolderPermission(AcmOutlookUser user,
                                    String folderId,
                                    OutlookFolderPermission permission) throws AcmOutlookItemNotFoundException {
        ExchangeService service = connect(user);
        getDao().addFolderPermission(service, folderId, permission);
    }

    @Override
    public void addFolderPermission(String folderId, OutlookFolderPermission permission) throws AcmOutlookItemNotFoundException {
        addFolderPermission(getAcmSystemOutlookUser(), folderId, permission);
    }

    @Override
    public void removeFolderPermission(AcmOutlookUser user,
                                       String folderId,
                                       OutlookFolderPermission permission) throws AcmOutlookItemNotFoundException {
        ExchangeService service = connect(user);
        getDao().removeFolderPermission(service, folderId, permission);
    }

    @Override
    public void removeFolderPermission(String folderId, OutlookFolderPermission permission) throws AcmOutlookItemNotFoundException {
        removeFolderPermission(getAcmSystemOutlookUser(), folderId, permission);
    }

    protected ExchangeService connect(AcmOutlookUser user) throws AcmOutlookConnectionFailedException
    {
        return getDao().connect(user);
    }

    public OutlookDao getDao()
    {
        return dao;
    }

    public void setDao(OutlookDao dao)
    {
        this.dao = dao;
    }

    private AcmOutlookUser getAcmSystemOutlookUser() {
        return new AcmOutlookUser(systemUserId, systemUserEmail, systemUserEmailPassword);
    }

    private OutlookFolder mapFolderToOutlookFolder(Folder folder) throws ServiceLocalException {
        OutlookFolder of = new OutlookFolder();
        of.setDisplayName(folder.getDisplayName());
        of.setId(folder.getId().getUniqueId());
        of.setParentId(folder.getParentFolderId().getUniqueId());

        List<OutlookFolderPermission> permissions = new LinkedList<>();
        for(FolderPermission fp:folder.getPermissions().getItems()){
            permissions.add(mapFolderPermission(fp));
        }
        of.setPermissions(permissions);

        return of;

    }

    private OutlookFolderPermission mapFolderPermission(FolderPermission folderPermission) {
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
}
