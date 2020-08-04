package com.armedia.acm.plugins.consultation.service;

/*-
 * #%L
 * ACM Default Plugin: Consultations
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.addressable.model.ContactMethod;
import com.armedia.acm.plugins.consultation.model.Consultation;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociation;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.web.api.MDCConstants;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(name = "spring", locations = {
        "/spring/spring-library-configuration.xml",
        "/spring/spring-library-audit-service.xml",
        "/spring/spring-library-admin.xml",
        "/spring/spring-library-business-process.xml",
        "/spring/spring-library-consultation.xml",
        "/spring/spring-library-consultation-dao.xml",
        "/spring/spring-library-consultation-events.xml",
        "/spring/spring-library-consultation-rules.xml",
        "/spring/spring-library-consultation-save.xml",
        "/spring/spring-library-consultation-plugin-test.xml",
        "/spring/spring-library-consultation-dao-test.xml",
        "/spring/spring-library-data-source.xml",
        "/spring/spring-library-user-service.xml",
        "/spring/spring-library-core-api.xml",
        "/spring/spring-library-context-holder.xml",
        "/spring/spring-library-property-file-manager.xml",
        "/spring/spring-library-ms-outlook-integration.xml",
        "/spring/spring-library-ms-outlook-plugin.xml",
        "/spring/spring-library-acm-encryption.xml",
        "/spring/spring-library-object-association-plugin.xml",
        "/spring/spring-library-object-diff.xml",
        "/spring/spring-library-object-history.xml",
        "/spring/spring-library-object-lock.xml",
        "/spring/spring-library-search.xml",
        "/spring/spring-library-note.xml",
        "/spring/spring-library-notification.xml",
        "/spring/spring-library-object-converter.xml",
        "/spring/spring-library-ecm-file-lock.xml",
        "/spring/spring-library-ecm-file.xml",
        "/spring/spring-library-convert-folder-service.xml",
        "/spring/spring-library-convert-file-service.xml",
        "/spring/spring-library-drools-rule-monitor.xml",
        "/spring/spring-library-profile.xml",
        "/spring/spring-library-particpants.xml",
        "/spring/spring-library-person.xml",
        "/spring/spring-library-person-rules.xml",
        "/spring/spring-library-functional-access-control.xml",
        "/spring/spring-library-organization-rules.xml",
        "/spring/spring-library-ecm-tika.xml",
        "/spring/spring-library-email.xml",
        "/spring/spring-library-email-smtp.xml",
        "/spring/spring-library-data-access-control.xml",
        "/spring/spring-library-activiti-configuration.xml",
        "/spring/spring-library-service-data.xml",
        "/spring/spring-test-quartz-scheduler.xml",
        "/spring/spring-library-activemq.xml",
        "/spring/spring-library-websockets.xml",
        "/spring/spring-library-user-login.xml",
        "/spring/spring-library-camel-context.xml",
        "/spring/spring-library-authentication-token.xml",
        "/spring/spring-library-plugin-manager.xml",
        "/spring/spring-integration-consultation-test.xml",
        "/spring/spring-library-folder-watcher.xml",
        "/spring/spring-library-pdf-utilities.xml",
        "/spring/spring-library-task.xml",
        "/spring/spring-library-form-configurations.xml",
        "/spring/spring-library-service-timesheet.xml",
        "/spring/spring-library-timesheet-save.xml",
        "/spring/spring-library-forms-configuration.xml",
        "/spring/spring-library-service-costsheet.xml",
        "/spring/spring-library-timesheet-rules.xml",
        "/spring/spring-library-costsheet-rules.xml",
        "/spring/spring-library-costsheet-save.xml",
        "/spring/spring-library-labels-service.xml",
        "/spring/spring-library-object-title.xml",
        "/spring/spring-library-calendar-config-service.xml",
        "/spring/spring-library-ldap-directory-config.xml",
        "/spring/spring-library-calendar-integration-exchange-service.xml"
})
@Rollback(true)
public class ConsultationPipelineIT
{
    static
    {
        String userHomePath = System.getProperty("user.home");
        System.setProperty("acm.configurationserver.propertyfile", userHomePath + "/.arkcase/acm/conf.yml");
        System.setProperty("configuration.server.url", "http://localhost:9999");
        System.setProperty("javax.net.ssl.trustStore", userHomePath + "/.arkcase/acm/private/arkcase.ts");
        System.setProperty("javax.net.ssl.trustStorePassword", "password");
    }

    @Autowired
    private ConsultationService consultationService;

    public transient final Logger log = LogManager.getLogger(getClass());

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private AuditPropertyEntityAdapter auditAdapter;

    @Before
    public void setUp()
    {
        auditAdapter.setUserId("auditUser");

        MDC.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, "admin");
        MDC.put(MDCConstants.EVENT_MDC_REQUEST_ID_KEY, UUID.randomUUID().toString());
    }

    private Consultation createTestConsultation()
    {
        Consultation consultation = new Consultation();
        consultation.setConsultationNumber(UUID.randomUUID().toString());
        consultation.setConsultationType("consultationType");
        consultation.setStatus("status");
        consultation.setTitle("title");
        consultation.setComponentAgency("componentAgency");
        consultation.setRestricted(true);

        consultation.setCreator("creator");

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, 3);

        Date dueDate = cal.getTime();
        consultation.setDueDate(dueDate);

        Person p = new Person();
        p.setFamilyName("Person");
        p.setGivenName("ACM");
        p.setStatus("testStatus");

        ContactMethod cm = new ContactMethod();
        cm.setType("Phone Number");
        cm.setValue("703-555-1212");

        List<ContactMethod> cms = new ArrayList<>();
        cms.add(cm);
        p.setContactMethods(cms);

        PersonAssociation pa = new PersonAssociation();

        pa.setPerson(p);
        pa.setPersonDescription("Simple Description");
        pa.setPersonType("Initiator");

        consultation.setOriginator(pa);

        ObjectAssociation oa = new ObjectAssociation();
        oa.setTargetId(12345L);
        oa.setTargetType("DOCUMENT");
        oa.setTargetName("Test Name");

        consultation.addChildObject(oa);

        AcmParticipant assignee = new AcmParticipant();
        assignee.setParticipantType("assignee");
        assignee.setParticipantLdapId("ann-acm");

        consultation.getParticipants().add(assignee);

        consultation.setRestricted(true);

        return consultation;
    }

    @Test
    @Transactional
    public void saveConsultationFlow() throws Exception
    {
        Consultation consultation = createTestConsultation();

        consultation.setCreator("auditUser");

        // consultation number should be set by the flow
        consultation.setConsultationNumber(null);

        Authentication auth = new UsernamePasswordAuthenticationToken("testUser", "testUser");

        Consultation saved = consultationService.saveConsultation(consultation, auth, "ipAddress");

        entityManager.flush();

        assertNotNull(saved.getId());
        assertNotNull(saved.getConsultationNumber());

        log.info("New consultation id: " + saved.getId());
        log.info("New consultation number: " + saved.getConsultationNumber());
    }

    @Test
    @Transactional
    public void checkConsultationSetCreatorHandler() throws Exception
    {
        Consultation consultation = createTestConsultation();

        consultation.setCreator(null);

        Authentication auth = new UsernamePasswordAuthenticationToken("testUser", "testUser");

        Consultation saved = consultationService.saveConsultation(consultation, auth, "ipAddress");

        entityManager.flush();

        assertNotNull(saved.getId());
        assertNotNull(saved.getCreator());
        assertEquals(auth.getName(), saved.getCreator());

        log.info("New consultation id: " + saved.getId());
        log.info("New consultation creator: " + saved.getCreator());
    }

    @Test
    @Transactional
    public void checkConsultationContainerHandler() throws Exception
    {
        Consultation consultation = createTestConsultation();

        consultation.setCreator(null);

        Authentication auth = new UsernamePasswordAuthenticationToken("testUser", "testUser");

        Consultation saved = consultationService.saveConsultation(consultation, auth, "ipAddress");

        entityManager.flush();

        assertNotNull(saved.getId());

        assertNotNull(saved.getContainer());
        assertNotNull(saved.getContainer().getContainerObjectType());
        assertNotNull(saved.getContainer().getContainerObjectTitle());

        assertNotNull(saved.getContainer().getFolder());
        assertEquals("ROOT", saved.getContainer().getFolder().getName());

        log.info("New consultation id: " + saved.getId());
        log.info("New consultation container: " + saved.getContainer());
        log.info("New consultation folder: " + saved.getContainer().getFolder());
    }
}
