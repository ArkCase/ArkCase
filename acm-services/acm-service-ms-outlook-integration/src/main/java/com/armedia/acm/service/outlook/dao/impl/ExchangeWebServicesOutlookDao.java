package com.armedia.acm.service.outlook.dao.impl;

import com.armedia.acm.service.outlook.dao.OutlookDao;
import com.armedia.acm.service.outlook.exception.AcmOutlookConnectionFailedException;
import com.armedia.acm.service.outlook.exception.AcmOutlookCreateItemFailedException;
import com.armedia.acm.service.outlook.exception.AcmOutlookException;
import com.armedia.acm.service.outlook.exception.AcmOutlookFindItemsFailedException;
import com.armedia.acm.service.outlook.model.AcmOutlookUser;
import com.armedia.acm.service.outlook.model.OutlookCalendarItem;
import com.armedia.acm.service.outlook.model.OutlookContactItem;
import com.armedia.acm.service.outlook.model.OutlookItem;
import com.armedia.acm.service.outlook.model.OutlookTaskItem;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.PropertySet;
import microsoft.exchange.webservices.data.core.service.folder.Folder;
import microsoft.exchange.webservices.data.core.service.item.Appointment;
import microsoft.exchange.webservices.data.core.service.item.Contact;
import microsoft.exchange.webservices.data.core.service.item.Item;
import microsoft.exchange.webservices.data.core.service.item.Task;
import microsoft.exchange.webservices.data.core.service.schema.ItemSchema;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.enumeration.*;
import microsoft.exchange.webservices.data.exception.ServiceLocalException;
import microsoft.exchange.webservices.data.property.complex.AppointmentOccurrenceId;
import microsoft.exchange.webservices.data.property.complex.EmailAddress;
import microsoft.exchange.webservices.data.property.complex.ItemId;
import microsoft.exchange.webservices.data.property.complex.MessageBody;
import microsoft.exchange.webservices.data.property.complex.RecurringAppointmentMasterId;
import microsoft.exchange.webservices.data.property.complex.recurrence.pattern.Recurrence;
import microsoft.exchange.webservices.data.property.complex.time.OlsonTimeZoneDefinition;
import microsoft.exchange.webservices.data.property.definition.PropertyDefinition;
import microsoft.exchange.webservices.data.search.FindItemsResults;
import microsoft.exchange.webservices.data.search.ItemView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.net.URI;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

/**
 * Created by armdev on 4/20/15.
 */
public class ExchangeWebServicesOutlookDao implements OutlookDao {
    private transient final Logger log = LoggerFactory.getLogger(getClass());
    private ExchangeVersion exchangeVersion = ExchangeVersion.Exchange2007_SP1;
    private Map<String, PropertyDefinition> sortFields;
    private final PropertySet standardProperties = new PropertySet(
            BasePropertySet.IdOnly,
            ItemSchema.Subject,
            ItemSchema.DateTimeSent,
            ItemSchema.DateTimeCreated,
            ItemSchema.DateTimeReceived,
            ItemSchema.LastModifiedTime,
            ItemSchema.Body,
            ItemSchema.Size);
    private boolean autodiscoveryEnabled;
    private URI clientAccessServer;

    @Override
    @Cacheable(value = "outlook-connection-cache", key = "#user.emailAddress")
    public ExchangeService connect(AcmOutlookUser user) throws AcmOutlookConnectionFailedException {
        Objects.requireNonNull(user, "User cannot be null");
        Objects.requireNonNull(user.getOutlookPassword(), "Password cannot be null");
        Objects.requireNonNull(user.getEmailAddress(), "E-mail address cannot be null");

        ExchangeService service = new ExchangeService(getExchangeVersion());
        ExchangeCredentials credentials = new WebCredentials(user.getEmailAddress(), user.getOutlookPassword());
        service.setCredentials(credentials);

        try
        {
            if ( isAutodiscoveryEnabled() )
            {
                service.autodiscoverUrl(user.getEmailAddress(), redirectionUrl -> true);
            }
            else
            {
                service.setUrl(getClientAccessServer());
            }

            return service;
        } catch (Exception e) {
            log.error("Could not connect to Exchange: " + e.getMessage(), e);
            throw new AcmOutlookConnectionFailedException(e.getMessage(), e);
        }
    }

    @Override
    @CacheEvict(value = "outlook-connection-cache", key = "#user.emailAddress")
    public void disconnect(AcmOutlookUser user) {
        // EWS apparently has no concept of "logging out" so the whole point of this method is to
        // remove the connection from the connection cache.
        log.info("Exchange session has been removed from session cache");
    }

    @Override
    public FindItemsResults<Item> findItems(
            ExchangeService service,
            WellKnownFolderName wellKnownFolderName,
            PropertySet extraFieldsToRetrieve,
            int start,
            int maxItems,
            String sortProperty,
            boolean sortAscending)
            throws AcmOutlookFindItemsFailedException {
        try {
            log.debug("finding items");

            Objects.requireNonNull(service, "Service cannot be null");

            Folder folder = Folder.bind(service, wellKnownFolderName);

            ItemView view = new ItemView(maxItems, start);

            PropertyDefinition orderBy =
                    sortProperty == null || sortProperty.trim().isEmpty() || !getSortFields().containsKey(sortProperty) ?
                            ItemSchema.DateTimeReceived : getSortFields().get(sortProperty);

            SortDirection sortDirection = sortAscending ? SortDirection.Ascending : SortDirection.Descending;

            view.getOrderBy().add(orderBy, sortDirection);

            FindItemsResults<Item> findResults = service.findItems(folder.getId(), view);

            PropertySet allProperties = new PropertySet();

            allProperties.addRange(standardProperties);

            allProperties.addRange(extraFieldsToRetrieve);

            if (!findResults.getItems().isEmpty()) {
                service.loadPropertiesForItems(findResults.getItems(), allProperties);
            }

            return findResults;
        } catch (Exception e) {
            log.error("Could not list items: " + e.getMessage(), e);
            throw new AcmOutlookFindItemsFailedException(e.getMessage(), e);
        }

    }

    @Override
    public OutlookCalendarItem createCalendarAppointment(ExchangeService service, Folder folder, OutlookCalendarItem calendarItem) throws AcmOutlookCreateItemFailedException {
        try {

            Appointment appointment = new Appointment(service);
            appointment.setSubject(calendarItem.getSubject());
            appointment.setBody(MessageBody.getMessageBodyFromText(calendarItem.getBody()));

            OlsonTimeZoneDefinition timeZoneDefinition = new OlsonTimeZoneDefinition(TimeZone.getTimeZone("UTC"));

            appointment.setStartTimeZone(timeZoneDefinition);
            appointment.setStart(calendarItem.getStartDate());
            appointment.setEnd(calendarItem.getEndDate());



            if (calendarItem.getAllDayEvent() != null && calendarItem.getAllDayEvent())
                appointment.setIsAllDayEvent(calendarItem.getAllDayEvent());

            if (calendarItem.getRecurring() != null && calendarItem.getRecurring()) {
                appointment.setRecurrence(new Recurrence.DailyPattern(calendarItem.getStartDate(), calendarItem.getRecurringInterval()));
                appointment.getRecurrence().setStartDate(calendarItem.getStartDate());
                appointment.getRecurrence().setEndDate(calendarItem.getRecurringEndDate());
            }

            appointment.save(folder.getId());
            fillCreatedItemDetails(calendarItem, appointment);
        } catch (Exception e) {
            throw new AcmOutlookCreateItemFailedException("Error creating appointment!", e);
        }
        return calendarItem;
    }

    @Override
    public OutlookTaskItem createTaskItem(ExchangeService service, Folder folder, OutlookTaskItem taskItem) throws AcmOutlookCreateItemFailedException {
        try {
            Task task = new Task(service);

            task.setSubject(taskItem.getSubject());
            task.setBody(MessageBody.getMessageBodyFromText(taskItem.getBody()));

            task.setPercentComplete(taskItem.getPercentComplete());

            task.setDueDate(taskItem.getDueDate());
            task.setStartDate(taskItem.getStartDate());
            task.save(folder.getId());

            fillCreatedItemDetails(taskItem, task);
        } catch (Exception e) {
            throw new AcmOutlookCreateItemFailedException("Error creating task!", e);
        }
        return taskItem;
    }

    @Override
    public OutlookContactItem createContactItem(ExchangeService service, Folder folder, OutlookContactItem contactItem) throws AcmOutlookCreateItemFailedException {
        try {
            Contact contact = new Contact(service);
            contact.setSurname(contactItem.getSurname());
            contact.setSubject(contactItem.getSubject());
            contact.setCompanyName(contactItem.getCompanyName());
            contact.setDisplayName(contactItem.getDisplayName());

            if (contactItem.getPrimaryTelephone() != null)
                contact.getPhoneNumbers().setPhoneNumber(PhoneNumberKey.PrimaryPhone, contactItem.getPrimaryTelephone());

            if (contactItem.getEmailAddress1() != null)
                contact.getEmailAddresses().setEmailAddress(EmailAddressKey.EmailAddress1, new EmailAddress(contactItem.getEmailAddress1()));
            if (contactItem.getEmailAddress2() != null)
                contact.getEmailAddresses().setEmailAddress(EmailAddressKey.EmailAddress2, new EmailAddress(contactItem.getEmailAddress2()));

            contact.save(folder.getId());

            fillCreatedItemDetails(contactItem, contact);
        } catch (Exception e) {
            throw new AcmOutlookCreateItemFailedException("Error creating contact!", e);
        }
        return contactItem;
    }

    private void fillCreatedItemDetails(OutlookItem outlookItem, Item item) throws ServiceLocalException {
        outlookItem.setId(item.getId().getUniqueId());
    }

    @Override
    public void deleteItem(ExchangeService service, String itemId, DeleteMode deleteMode) {
        try {
            Item item = Item.bind(service, new ItemId(itemId));
            item.delete(deleteMode);
        } catch (Exception e) {
            throw new AcmOutlookException("Error deleting item with id = " + itemId, e);
        }
    }

    @Override
    public void deleteAppointmentItem(ExchangeService service, String itemId, boolean recurring, DeleteMode deleteMode) {
        try {
            Appointment item = null;

            if (recurring) {
                service.deleteItem(new ItemId(itemId), deleteMode, SendCancellationsMode.SendOnlyToAll, AffectedTaskOccurrence.AllOccurrences);
                //item = Appointment.bindToRecurringMaster(service, new RecurringAppointmentMasterId(itemId));
            } else {
                item = Appointment.bind(service, new ItemId(itemId));
                item.delete(deleteMode);
            }
            // item.delete(deleteMode);
        } catch (Exception e) {
            throw new AcmOutlookException("Error deleting item with id = " + itemId, e);
        }
    }

    public Map<String, PropertyDefinition> getSortFields() {
        return sortFields;
    }

    public void setSortFields(Map<String, PropertyDefinition> sortFields) {
        this.sortFields = sortFields;
    }

    public ExchangeVersion getExchangeVersion() {
        return exchangeVersion;
    }

    public void setExchangeVersion(ExchangeVersion exchangeVersion) {
        this.exchangeVersion = exchangeVersion;
    }

    public boolean isAutodiscoveryEnabled()
    {
        return autodiscoveryEnabled;
    }

    public void setAutodiscoveryEnabled(boolean autodiscoveryEnabled)
    {
        this.autodiscoveryEnabled = autodiscoveryEnabled;
    }

    public URI getClientAccessServer()
    {
        return clientAccessServer;
    }

    public void setClientAccessServer(URI clientAccessServer)
    {
        this.clientAccessServer = clientAccessServer;
    }
}
