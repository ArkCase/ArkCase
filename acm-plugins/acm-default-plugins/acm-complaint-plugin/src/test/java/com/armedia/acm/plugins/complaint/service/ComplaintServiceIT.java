package com.armedia.acm.plugins.complaint.service;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.form.config.xml.PersonItem;
import com.armedia.acm.plugins.addressable.model.ContactMethod;
import com.armedia.acm.plugins.addressable.model.PostalAddress;
import com.armedia.acm.plugins.complaint.dao.ComplaintDao;
import com.armedia.acm.plugins.complaint.model.complaint.ComplaintForm;
import com.armedia.acm.plugins.complaint.model.complaint.Contact;
import com.armedia.acm.plugins.complaint.model.complaint.MainInformation;
import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonAlias;
import com.armedia.acm.plugins.person.model.PersonAssociation;
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
import java.util.*;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(name = "spring",
        locations = {
                "/spring/spring-library-acm-encryption.xml",
                "/spring/spring-library-activiti-actions.xml",
                "/spring/spring-library-activiti-configuration.xml",
                "/spring/spring-library-authentication-token.xml",
                "/spring/spring-library-business-process.xml",
                "/spring/spring-library-case-file-dao.xml",
                "/spring/spring-library-case-file-events.xml",
                "/spring/spring-library-case-file-rules.xml",
                "/spring/spring-library-case-file-save.xml",
                "/spring/spring-library-complaint.xml",
                "/spring/spring-library-complaint-plugin-test.xml",
                "/spring/spring-library-complaint-plugin-test-mule.xml",
                "/spring/spring-library-context-holder.xml",
                "/spring/spring-library-data-access-control.xml",
                "/spring/spring-library-data-source.xml",
                "/spring/spring-library-ecm-file.xml",
                "/spring/spring-library-event.xml",
                "/spring/spring-library-folder-watcher.xml",
                "/spring/spring-library-forms-configuration.xml",
                "/spring/spring-library-functional-access-control.xml",
                "/spring/spring-library-ms-outlook-integration.xml",
                "/spring/spring-library-ms-outlook-plugin.xml",
                "/spring/spring-library-note.xml",
                "/spring/spring-library-object-association-plugin.xml",
                "/spring/spring-library-object-history.xml",
                "/spring/spring-library-particpants.xml",
                "/spring/spring-library-person.xml",
                "/spring/spring-library-plugin-manager.xml",
                "/spring/spring-library-profile.xml",
                "/spring/spring-library-property-file-manager.xml",
                "/spring/spring-library-search.xml",
                "/spring/spring-library-task.xml",
                "/spring/spring-library-user-service.xml",
                "/spring/spring-library-user-login.xml",
                "/spring/spring-library-notification.xml",
                "/spring/spring-library-service-data.xml",
                "/spring/spring-library-drools-rule-monitor.xml",
                "/spring/spring-library-object-lock.xml",
                "/spring/spring-library-email.xml",
                "/spring/spring-library-email-smtp.xml"
        }
)
@TransactionConfiguration(defaultRollback = true, transactionManager = "transactionManager")
public class ComplaintServiceIT
{
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

        frevvoComplaint.setLocation(location);

        Person initiator = new Person();
        initiator.setId(123L);
        initiator.setGivenName("Tom");
        initiator.setFamilyName("Initiator");

        frevvoComplaint.setInitiatorId(initiator.getId());
        frevvoComplaint.setPeople(populatePeople());


        assertEquals("Jack Witness", frevvoComplaint.getPeople().get(0).getValue());
        assertEquals(Long.valueOf(123), frevvoComplaint.getInitiatorId());

        ComplaintForm savedFrevvoComplaint = service.saveComplaint(frevvoComplaint);

        entityManager.flush();

        assertNotNull(savedFrevvoComplaint.getComplaintId());
        assertNotNull(savedFrevvoComplaint.getComplaintNumber());

        com.armedia.acm.plugins.complaint.model.Complaint acmComplaint = dao.find(savedFrevvoComplaint.getComplaintId());

        assertNotNull(acmComplaint);
        verifyComplaint(frevvoComplaint, acmComplaint);

        assertEquals(frevvoComplaint.getInitiatorId(), acmComplaint.getOriginator().getPerson().getId());
        assertEquals(2, acmComplaint.getPersonAssociations().size());


        boolean witnessFound = false;
        for ( PersonAssociation pa : acmComplaint.getPersonAssociations() )
        {
            if ( "Witness".equals(pa.getPersonType()) )
            {
                witnessFound = true;
            }
        }

        assertTrue(witnessFound);
    }

    private List<PersonItem> populatePeople()
    {
        List<PersonItem> people = new ArrayList<>();

        PersonItem p1 = new PersonItem();
        p1.setPersonType("Witness");
        p1.setId(234L);
        p1.setValue("Jack Witness");
        people.add(p1);

        return people;
    }



    private void verifyComplaint(ComplaintForm frevvoComplaint, com.armedia.acm.plugins.complaint.model.Complaint acmComplaint)
    {
        assertNotNull(acmComplaint.getDetails());
        assertNotNull(acmComplaint.getIncidentDate());
        assertNotNull(acmComplaint.getPriority());
        assertNotNull(acmComplaint.getComplaintTitle());

        assertEquals(frevvoComplaint.getComplaintDescription(), acmComplaint.getDetails());
        //assertEquals(frevvoComplaint.getDate().toString(), acmComplaint.getIncidentDate().toString());
        assertEquals(frevvoComplaint.getPriority(), acmComplaint.getPriority());
        assertEquals(frevvoComplaint.getComplaintTitle(), acmComplaint.getComplaintTitle());
    }
}
