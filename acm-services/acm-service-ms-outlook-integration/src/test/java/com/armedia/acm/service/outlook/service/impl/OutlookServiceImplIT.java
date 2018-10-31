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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.armedia.acm.service.outlook.model.AcmOutlookUser;
import com.armedia.acm.service.outlook.model.OutlookCalendarItem;
import com.armedia.acm.service.outlook.model.OutlookContactItem;
import com.armedia.acm.service.outlook.model.OutlookItem;
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

import java.util.Date;

import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.enumeration.service.DeleteMode;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-ms-outlook-integration.xml",
        "/spring/spring-library-property-file-manager.xml",
        "/spring/spring-library-ecm-file.xml",
        "/spring/spring-test-ms-outlook-integration.xml",
        "/spring/spring-library-data-source.xml",
        "/spring/spring-library-search.xml",
        "/spring/spring-library-data-access-control.xml",
        "/spring/spring-library-particpants.xml",
        "/spring/spring-library-context-holder.xml",
        "/spring/spring-library-acm-encryption.xml",
        "/spring/spring-library-user-service.xml",
        "/spring/spring-library-authentication-token.xml",
        "/spring/spring-library-activiti-configuration.xml",
        "/spring/spring-library-drools-rule-monitor.xml",
        "/spring/spring-library-object-lock.xml",
        "/spring/spring-library-ecm-tika.xml",
        "/spring/spring-library-email.xml",
        "/spring/spring-library-calendar-config-service.xml",
        "/spring/spring-library-email-smtp.xml",
        "/spring/spring-library-object-converter.xml",
        "/spring/spring-library-ecm-file-lock.xml",
        "/spring/spring-library-service-data.xml",
        "/spring/spring-library-core-api.xml"
})
public class OutlookServiceImplIT
{
    private transient final Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    private OutlookService outlookService;
    private String validUser = "***REMOVED***";
    private String validPassword = "AcMd3v$";

    private AcmOutlookUser user = new AcmOutlookUser("ann-acm", validUser, validPassword);

    @Test
    public void tasks()
    {
        OutlookResults<OutlookTaskItem> tasks = outlookService.findTaskItems(user, 0, 5, "subject", true, null);

        assertNotNull(tasks);

        log.info("Found tasks: " + tasks);
    }

    @Test
    public void messages()
    {
        OutlookResults<OutlookMailItem> messages = outlookService.findMailItems(user, 0, 5, "subject", true, null);

        assertNotNull(messages);

        log.info("Found messages: " + messages);
    }

    @Test
    public void calendarItems()
    {
        OutlookResults<OutlookCalendarItem> appts = outlookService.findCalendarItems(null, user, 0, 5, "subject", true, null);

        assertNotNull(appts);

        log.info("Found appointments: " + appts);
    }

    @Test
    public void contacts()
    {
        OutlookResults<OutlookContactItem> contacts = outlookService.findContactItems(user, 0, 5, "subject", true, null);

        assertNotNull(contacts);

        log.info("Found contacts: " + contacts);
    }

    @Test
    public void createTaskItem() throws Exception
    {

        OutlookTaskItem taskItem = new OutlookTaskItem();
        taskItem.setSubject("Task 1");
        taskItem.setBody("");
        long tomorrow = System.currentTimeMillis() + 1000 * 60 * 60 * 24;// due to tomorrow
        taskItem.setDueDate(new Date(tomorrow));
        taskItem.setPercentComplete(20);
        taskItem.setComplete(false);
        taskItem.setStartDate(new Date(System.currentTimeMillis() + 1000 * 60));// start next minute
        assertNull(taskItem.getId());

        taskItem = outlookService.createOutlookTaskItem(user, WellKnownFolderName.Tasks, taskItem);
        verifyFilledItemDetails(taskItem);

        outlookService.deleteItem(user, taskItem.getId(), DeleteMode.HardDelete);
    }

    @Test
    public void createContactItem() throws Exception
    {

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
        outlookService.createOutlookContactItem(user, WellKnownFolderName.Contacts, contactItem);
        verifyFilledItemDetails(contactItem);
        outlookService.deleteItem(user, contactItem.getId(), DeleteMode.HardDelete);
    }

    @Test
    public void createRecurringAppointmentItem() throws Exception
    {
        OutlookCalendarItem appointmentItem = new OutlookCalendarItem();
        appointmentItem.setBody("Body");
        appointmentItem.setSubject("Subject");
        appointmentItem.setAllDayEvent(false);

        long tomorrow = System.currentTimeMillis() + 1000 * 60 * 60 * 24;// start tomorrow
        appointmentItem.setStartDate(new Date(tomorrow));
        appointmentItem.setEndDate(new Date(tomorrow + 1000 * 60 * 60));// start + 1 hour
        appointmentItem.setMeeting(false);
        appointmentItem.setRecurring(true);
        appointmentItem.setRecurringInterval(1);
        appointmentItem.setRecurringEndDate(new Date(tomorrow + 1000 * 60 * 60 * 48));// ends after 2 days

        assertNull(appointmentItem.getId());
        appointmentItem = outlookService.createOutlookAppointment(user, appointmentItem);
        verifyFilledItemDetails(appointmentItem);

        outlookService.deleteAppointmentItem(user, appointmentItem.getId(), appointmentItem.getRecurring(), DeleteMode.HardDelete);
    }

    @Test
    public void createNonRecurringAppointmentItem() throws Exception
    {
        OutlookCalendarItem appointmentItem = new OutlookCalendarItem();
        appointmentItem.setBody("Body");
        appointmentItem.setSubject("Subject");
        appointmentItem.setAllDayEvent(false);

        long tomorrow = System.currentTimeMillis() + 1000 * 60 * 60 * 24;// start tomorrow
        appointmentItem.setStartDate(new Date(tomorrow));
        appointmentItem.setEndDate(new Date(tomorrow + 1000 * 60 * 60));// start + 1 hour
        appointmentItem.setMeeting(false);

        assertNull(appointmentItem.getId());
        appointmentItem = outlookService.createOutlookAppointment(user, appointmentItem);
        verifyFilledItemDetails(appointmentItem);

        outlookService.deleteAppointmentItem(user, appointmentItem.getId(), false, DeleteMode.HardDelete);
    }

    private void verifyFilledItemDetails(OutlookItem outlookItem)
    {
        assertNotNull(outlookItem.getId());
    }

}
