package com.armedia.acm.service.outlook.dao.impl;

import com.armedia.acm.service.outlook.dao.OutlookDao;
import com.armedia.acm.service.outlook.model.AcmOutlookUser;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.PropertySet;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.core.service.item.Item;
import microsoft.exchange.webservices.data.core.service.item.Task;
import microsoft.exchange.webservices.data.core.service.schema.EmailMessageSchema;
import microsoft.exchange.webservices.data.core.service.schema.ItemSchema;
import microsoft.exchange.webservices.data.core.service.schema.TaskSchema;
import microsoft.exchange.webservices.data.enumeration.DateTimePrecision;
import microsoft.exchange.webservices.data.enumeration.WellKnownFolderName;
import microsoft.exchange.webservices.data.exception.ServiceLocalException;
import microsoft.exchange.webservices.data.search.FindItemsResults;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

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

    private String validUser = "ann.acm@armedia.com";
    private String validPassword = "Armedia123";

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
            fail("should have failed to authenticate");
        }
        catch (Exception e)
        {
            // expected
            log.info("Exception: " + e.getMessage(), e);
        }
    }


}
