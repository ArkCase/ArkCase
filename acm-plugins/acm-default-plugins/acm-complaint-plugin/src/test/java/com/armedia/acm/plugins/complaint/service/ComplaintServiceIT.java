package com.armedia.acm.plugins.complaint.service;

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

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.addressable.model.PostalAddress;
import com.armedia.acm.plugins.complaint.dao.ComplaintDao;
import com.armedia.acm.plugins.complaint.model.complaint.ComplaintForm;
import com.armedia.acm.plugins.person.dao.PersonDao;
import com.armedia.acm.web.api.MDCConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(name = "spring", locations = {
        "/spring/spring-library-configuration.xml",
        "/spring/spring-library-acm-encryption.xml",
        "/spring/spring-library-activiti-configuration.xml",
        "/spring/spring-library-ldap-directory-config.xml",
        "/spring/spring-library-admin.xml",
        "/spring/spring-library-authentication-token.xml",
        "/spring/spring-library-business-process.xml",
        "/spring/spring-library-calendar-config-service.xml",
        "/spring/spring-library-calendar-integration-exchange-service.xml",
        "/spring/spring-library-case-file-dao.xml",
        "/spring/spring-library-case-file-events.xml",
        "/spring/spring-library-case-file-rules.xml",
        "/spring/spring-library-case-file-save.xml",
        "/spring/spring-library-billing.xml",
        "/spring/spring-library-service-timesheet.xml",
        "/spring/spring-library-timesheet-save.xml",
        "/spring/spring-library-timesheet-rules.xml",
        "/spring/spring-library-service-costsheet.xml",
        "/spring/spring-library-costsheet-save.xml",
        "/spring/spring-library-costsheet-rules.xml",
        "/spring/spring-library-complaint.xml",
        "/spring/spring-library-complaint-plugin-test.xml",
        "/spring/spring-library-context-holder.xml",
        "/spring/spring-library-data-access-control.xml",
        "/spring/spring-library-data-source.xml",
        "/spring/spring-library-drools-rule-monitor.xml",
        "/spring/spring-library-ecm-file.xml",
        "/spring/spring-library-ecm-tika.xml",
        "/spring/spring-library-email.xml",
        "/spring/spring-library-email-smtp.xml",
        "/spring/spring-library-folder-watcher.xml",
        "/spring/spring-library-form-configurations.xml",
        "/spring/spring-library-forms-configuration.xml",
        "/spring/spring-library-functional-access-control.xml",
        "/spring/spring-library-ms-outlook-integration.xml",
        "/spring/spring-library-ms-outlook-plugin.xml",
        "/spring/spring-library-note.xml",
        "/spring/spring-library-notification.xml",
        "/spring/spring-library-object-association-plugin.xml",
        "/spring/spring-library-object-diff.xml",
        "/spring/spring-library-object-history.xml",
        "/spring/spring-library-object-lock.xml",
        "/spring/spring-library-organization-rules.xml",
        "/spring/spring-library-particpants.xml",
        "/spring/spring-library-pdf-utilities.xml",
        "/spring/spring-library-person.xml",
        "/spring/spring-library-person-rules.xml",
        "/spring/spring-library-plugin-manager.xml",
        "/spring/spring-library-profile.xml",
        "/spring/spring-library-property-file-manager.xml",
        "/spring/spring-library-search.xml",
        "/spring/spring-library-service-data.xml",
        "/spring/spring-library-task.xml",
        "/spring/spring-library-user-login.xml",
        "/spring/spring-library-user-service.xml",
        "/spring/spring-library-object-converter.xml",
        "/spring/spring-library-ecm-file-lock.xml",
        "/spring/spring-library-audit-service.xml",
        "/spring/spring-library-core-api.xml",
        "/spring/spring-library-activemq.xml",
        "/spring/spring-test-quartz-scheduler.xml",
        "/spring/spring-library-addressable-plugin.xml",
        "/spring/spring-library-camel-context.xml",
        "/spring/spring-library-websockets.xml",
        "/spring/spring-library-object-title.xml",
        "/spring/spring-library-convert-file-service.xml",
        "/spring/spring-library-labels-service.xml",
        "/spring/spring-library-convert-folder-service.xml"
})
@TransactionConfiguration(defaultRollback = true, transactionManager = "transactionManager")
public class ComplaintServiceIT
{

    static
    {
        String userHomePath = System.getProperty("user.home");
        System.setProperty("acm.configurationserver.propertyfile", userHomePath + "/.arkcase/acm/conf.yml");
        System.setProperty("configuration.server.url", "http://localhost:9999");
        System.setProperty("javax.net.ssl.trustStore", userHomePath + "/.arkcase/acm/private/arkcase.ts");
        System.setProperty("javax.net.ssl.trustStorePassword", "password");
    }

    private ComplaintService service;

    @Autowired
    private ComplaintDao dao;

    @Autowired
    private SaveComplaintTransaction saveComplaintTransaction;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private AuditPropertyEntityAdapter auditAdapter;

    @Autowired
    private ComplaintEventPublisher complaintEventPublisher;

    @Autowired
    private ComplaintFactory complaintFactory;

    @Autowired
    private PersonDao personDao;

    @Before
    public void setUp() throws Exception
    {
        auditAdapter.setUserId("auditUser");

        MDC.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, "admin");
        MDC.put(MDCConstants.EVENT_MDC_REQUEST_ID_KEY, UUID.randomUUID().toString());

        service = new ComplaintService();
        service.setSaveComplaintTransaction(saveComplaintTransaction);
        service.setComplaintEventPublisher(complaintEventPublisher);
        service.setComplaintFactory(complaintFactory);
        service.setPersonDao(personDao);

        Authentication auth = new UsernamePasswordAuthenticationToken("anotherUser", "password");
        service.setAuthentication(auth);
    }

    @Test
    @Transactional
    public void save() throws Exception
    {
        assertNotNull(service);

        Date now = new Date();

        ComplaintForm frevvoComplaint = new ComplaintForm();
        frevvoComplaint.setComplaintDescription("<strong>description</strong>");
        frevvoComplaint.setDate(now);
        frevvoComplaint.setPriority("High");
        frevvoComplaint.setComplaintTitle("complaint title");
        frevvoComplaint.setCategory("Agricultural");
        frevvoComplaint.setComplaintTag("No Tag");
        frevvoComplaint.setFrequency("Ongoing");

        PostalAddress location = new PostalAddress();
        location.setStreetAddress("testAddress");
        location.setCity("testCity");
        location.setState("testState");
        location.setZip("12345");
        location.setType("home");
        location.setCountry("country");

        PostalAddress pa1 = new PostalAddress();
        pa1.setCity("Amaurot");
        pa1.setCountry("Utopia");
        pa1.setType("type");

        PostalAddress pa2 = new PostalAddress();
        pa2.setCity("Atlanta");
        pa2.setCountry("Georgia");
        pa2.setType("type");

        ComplaintForm savedFrevvoComplaint = service.saveComplaint(frevvoComplaint);

        entityManager.flush();

        assertNotNull(savedFrevvoComplaint.getComplaintId());
        assertNotNull(savedFrevvoComplaint.getComplaintNumber());

        com.armedia.acm.plugins.complaint.model.Complaint acmComplaint = dao.find(savedFrevvoComplaint.getComplaintId());

        assertNotNull(acmComplaint);
        verifyComplaint(frevvoComplaint, acmComplaint);

        assertEquals(0, acmComplaint.getPersonAssociations().size());
    }

    private void verifyComplaint(ComplaintForm frevvoComplaint, com.armedia.acm.plugins.complaint.model.Complaint acmComplaint)
    {
        assertNotNull(acmComplaint.getDetails());
        assertNotNull(acmComplaint.getIncidentDate());
        assertNotNull(acmComplaint.getPriority());
        assertNotNull(acmComplaint.getComplaintTitle());

        assertEquals(frevvoComplaint.getComplaintDescription(), acmComplaint.getDetails());
        // assertEquals(frevvoComplaint.getDate().toString(), acmComplaint.getIncidentDate().toString());
        assertEquals(frevvoComplaint.getPriority(), acmComplaint.getPriority());
        assertEquals(frevvoComplaint.getComplaintTitle(), acmComplaint.getComplaintTitle());
    }
}
