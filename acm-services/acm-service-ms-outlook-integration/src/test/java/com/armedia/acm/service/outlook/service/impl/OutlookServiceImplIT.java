package com.armedia.acm.service.outlook.service.impl;

import com.armedia.acm.service.outlook.model.AcmOutlookUser;
import com.armedia.acm.service.outlook.model.OutlookCalendarItem;
import com.armedia.acm.service.outlook.model.OutlookContactItem;
import com.armedia.acm.service.outlook.model.OutlookMailItem;
import com.armedia.acm.service.outlook.model.OutlookResults;
import com.armedia.acm.service.outlook.model.OutlookTaskItem;
import com.armedia.acm.service.outlook.service.OutlookService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-ms-outlook-integration.xml"
})
public class OutlookServiceImplIT
{
    @Autowired
    private OutlookService outlookService;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    private String validUser = "ann.acm@armedia.com";
    private String validPassword = "Armedia123";

    private AcmOutlookUser user = new AcmOutlookUser("ann-acm", validUser, validPassword);

    @Test
    public void tasks()
    {
        OutlookResults<OutlookTaskItem> tasks = outlookService.findTaskItems(user, 0, 5, "subject", true);

        assertNotNull(tasks);

        log.info("Found tasks: " + tasks);
    }

    @Test
    public void messages()
    {
        OutlookResults<OutlookMailItem> messages = outlookService.findMailItems(user, 0, 5, "subject", true);

        assertNotNull(messages);

        log.info("Found messages: " + messages);
    }

    @Test
    public void calendarItems()
    {
        OutlookResults<OutlookCalendarItem> appts = outlookService.findCalendarItems(user, 0, 5, "subject", true);

        assertNotNull(appts);

        log.info("Found appointments: " + appts);
    }

    @Test
    public void contacts()
    {
        OutlookResults<OutlookContactItem> contacts = outlookService.findContactItems(user, 0, 5, "subject", true);

        assertNotNull(contacts);

        log.info("Found contacts: " + contacts);
    }
}
