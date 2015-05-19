package com.armedia.acm.service.outlook.dao.impl;

import com.armedia.acm.service.outlook.dao.OutlookDao;
import com.armedia.acm.service.outlook.model.AcmOutlookUser;
import com.armedia.acm.service.outlook.model.OutlookCalendarItem;
import com.armedia.acm.service.outlook.model.OutlookContactItem;
import com.armedia.acm.service.outlook.model.OutlookItem;
import com.armedia.acm.service.outlook.model.OutlookTaskItem;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.PropertySet;
import microsoft.exchange.webservices.data.core.service.folder.Folder;
import microsoft.exchange.webservices.data.core.service.item.Appointment;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.core.service.item.Item;
import microsoft.exchange.webservices.data.core.service.item.Task;
import microsoft.exchange.webservices.data.core.service.schema.AppointmentSchema;
import microsoft.exchange.webservices.data.core.service.schema.EmailMessageSchema;
import microsoft.exchange.webservices.data.core.service.schema.ItemSchema;
import microsoft.exchange.webservices.data.core.service.schema.TaskSchema;
import microsoft.exchange.webservices.data.enumeration.DateTimePrecision;
import microsoft.exchange.webservices.data.enumeration.DeleteMode;
import microsoft.exchange.webservices.data.enumeration.WellKnownFolderName;
import microsoft.exchange.webservices.data.search.FindItemsResults;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

/**
 * Created by armdev on 4/20/15.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-ms-outlook-integration.xml",
        "/spring/spring-library-property-file-manager.xml"
})
public class ExchangeWebServicesOutlookDaoIT
{
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    private String validUser = "***REMOVED***";
    private String validPassword = "AcMd3v$";

    private AcmOutlookUser user = new AcmOutlookUser("ann-acm", validUser, validPassword);

    @Autowired
    @Qualifier("exchangeWebServicesOutlookDao")
    private OutlookDao dao;

    @Test
    public void mailItems() throws Exception
    {
        ExchangeService service = dao.connect(user);

        FindItemsResults<Item> mailItems = dao.findItems(
                service, WellKnownFolderName.Inbox, new PropertySet(ItemSchema.Body, EmailMessageSchema.From), 0, 5,
                "subject", true);

        assertNotNull(mailItems);

        log.info("Total items: " + mailItems.getTotalCount() + "; more? " + mailItems.isMoreAvailable());

        for ( Item item : mailItems.getItems() )
        {
            log.info("Date: " + item.getDateTimeReceived() + "; subject: " + item.getSubject() + "; from: " + ((EmailMessage) item).getFrom());
        }

        //service.loadPropertiesForItems(mailItems.getItems(), new PropertySet(ItemSchema.Body));

        log.info("Body of first message: " + ( mailItems.getItems().get(0).getBody()));

        mailItems = dao.findItems(
                service, WellKnownFolderName.Inbox, new PropertySet(ItemSchema.Body, EmailMessageSchema.From), 0, 5, "subject", false);
        log.info("--- descending: ");

        for ( Item item : mailItems.getItems() )
        {
            log.info("Date: " + item.getDateTimeReceived() + "; subject: " + item.getSubject() + "; from: " + ((EmailMessage) item).getFrom());
        }

    }

    @Test
    public void appointmentItems() throws Exception
    {
        ExchangeService service = dao.connect(user);

        FindItemsResults<Item> items = dao.findItems(
                service, WellKnownFolderName.Calendar, new PropertySet(ItemSchema.Subject, AppointmentSchema.Start, AppointmentSchema.AppointmentType, AppointmentSchema.Recurrence), 0, 15,
                "subject", true);

        assertNotNull(items);

        log.info("Total items: " + items.getTotalCount() + "; more? " + items.isMoreAvailable());

        for ( Item item : items.getItems() )
        {
            System.out.println(((Appointment)item).getRecurrence());
            log.info("Date: " + item.getDateTimeReceived() + "; subject: " + item.getSubject() + "; type: " + ((Appointment) item).getAppointmentType());
        }

        //service.loadPropertiesForItems(mailItems.getItems(), new PropertySet(ItemSchema.Body));

        log.info("Body of first message: " + (items.getItems().get(0).getBody()));

        items = dao.findItems(
                service, WellKnownFolderName.Calendar, new PropertySet(ItemSchema.Subject, AppointmentSchema.Start, AppointmentSchema.AppointmentType), 0, 15,
                "subject", false);
        log.info("--- descending: ");

        for ( Item item : items.getItems() )
        {
            log.info("Date: " + item.getDateTimeReceived() + "; subject: " + item.getSubject() + "; type: " + ((Appointment) item).getAppointmentType());
        }

    }

    @Test
    public void taskItems() throws Exception
    {
        ExchangeService service = dao.connect(user);

        FindItemsResults<Item> taskItems = dao.findItems(
                service, WellKnownFolderName.Tasks, new PropertySet(ItemSchema.Body, TaskSchema.DueDate), 0, 5,
                "subject", true);

        assertNotNull(taskItems);

        log.info("Total items: " + taskItems.getTotalCount() + "; more? " + taskItems.isMoreAvailable());

        for ( Item item : taskItems.getItems() )
        {
            Task outlookTask = (Task) item;
            log.info("Date: " + outlookTask.getDateTimeReceived() + "; subject: " + outlookTask.getSubject() +
                    "; due: " + outlookTask.getDueDate());
        }
    }

    @Test
    public void connect()
    {
        ExchangeService service = null;

        service = dao.connect(user);

        DateTimePrecision precision = service.getDateTimePrecision();

        assertNotNull(precision);

        log.info("Date time precision: " + precision);

        log.debug("---------------- starting again");

        service = dao.connect(user);

        log.info("Exchange 2007 compatibility mode? " + service.getExchange2007CompatibilityMode());

        log.debug("---------------- another user");

        AcmOutlookUser invalidUser = new AcmOutlookUser("invalidUser", "invalidUser@armedia.com", "AcMd3v$");

        try
        {
            dao.connect(invalidUser);
            log.info("hmmm... with Office 365 even an invalid user gets here");
        }
        catch (Exception e)
        {
            // expected
            log.info("Exception: " + e.getMessage(), e);
        }
    }

    @Test
    public void createTaskItem() throws Exception
    {
        ExchangeService service = dao.connect(user);
        Folder folder = Folder.bind(service, WellKnownFolderName.Tasks);
        OutlookTaskItem taskItem = new OutlookTaskItem();
        taskItem.setSubject("Task 1");
        taskItem.setBody("");
        long tomorrow = System.currentTimeMillis() + 1000 * 60 * 60 * 24;//due to tomorrow
        taskItem.setDueDate(new Date(tomorrow));
        taskItem.setPercentComplete(20);
        taskItem.setComplete(false);
        taskItem.setStartDate(new Date(System.currentTimeMillis() + 1000 * 60));//start next minute
        assertNull(taskItem.getId());
        taskItem = dao.createTaskItem(service, folder, taskItem);
        verifyFilledItemDetails(taskItem);

        dao.deleteItem(service, taskItem.getId(), DeleteMode.HardDelete);
    }

    @Test
    public void createContactItem() throws Exception
    {
        ExchangeService service = dao.connect(user);
        Folder folder = Folder.bind(service, WellKnownFolderName.Contacts);


        OutlookContactItem contactItem = new OutlookContactItem();
        contactItem.setDisplayName("John Doe");
        contactItem.setBody("Body");
        contactItem.setSubject("Subject");
        contactItem.setCompanyName("Armedia");
        contactItem.setEmailAddress1("john.doe@armedia.com");
        contactItem.setPrimaryTelephone("+55555656456");
        contactItem.setSurname("Doe");
        contactItem.setCompleteName("John Doe");

        assertNull(contactItem.getId());
        contactItem = dao.createContactItem(service, folder, contactItem);
        verifyFilledItemDetails(contactItem);
        dao.deleteItem(service, contactItem.getId(), DeleteMode.HardDelete);
    }

    @Test
    public void createRecurringAppointmentItem() throws Exception
    {
        ExchangeService service = dao.connect(user);
        Folder folder = Folder.bind(service, WellKnownFolderName.Calendar);


        OutlookCalendarItem appointmentItem = new OutlookCalendarItem();
        appointmentItem.setBody("Body");
        appointmentItem.setSubject("Subject");
        appointmentItem.setAllDayEvent(false);

        long tomorrow = System.currentTimeMillis() + 1000 * 60 * 60 * 24;//start tomorrow
        appointmentItem.setStartDate(new Date(tomorrow));
        appointmentItem.setEndDate(new Date(tomorrow + 1000 * 60 * 60));//start + 1 hour
        appointmentItem.setMeeting(false);
        appointmentItem.setRecurring(true);
        appointmentItem.setRecurringInterval(1);
        appointmentItem.setRecurringEndDate(new Date(tomorrow + 1000 * 60 * 60 * 48));//ends after 2 days

        assertNull(appointmentItem.getId());
        appointmentItem = dao.createCalendarAppointment(service, folder, appointmentItem);
        verifyFilledItemDetails(appointmentItem);

        dao.deleteAppointmentItem(service, appointmentItem.getId(), appointmentItem.getRecurring(), DeleteMode.HardDelete);
    }

    @Test
    public void createNonRecurringAppointmentItem() throws Exception
    {
        ExchangeService service = dao.connect(user);
        Folder folder = Folder.bind(service, WellKnownFolderName.Calendar);

        OutlookCalendarItem appointmentItem = new OutlookCalendarItem();
        appointmentItem.setBody("Body");
        appointmentItem.setSubject("Subject");
        appointmentItem.setAllDayEvent(false);

        long tomorrow = System.currentTimeMillis() + 1000 * 60 * 60 * 24;//start tomorrow
        appointmentItem.setStartDate(new Date(tomorrow));
        appointmentItem.setEndDate(new Date(tomorrow + 1000 * 60 * 60));//start + 1 hour
        appointmentItem.setMeeting(false);

        assertNull(appointmentItem.getId());
        appointmentItem = dao.createCalendarAppointment(service, folder, appointmentItem);
        verifyFilledItemDetails(appointmentItem);

        dao.deleteAppointmentItem(service, appointmentItem.getId(), false, DeleteMode.HardDelete);
    }

    private void verifyFilledItemDetails(OutlookItem outlookItem) {
        assertNotNull(outlookItem.getId());
    }

}
