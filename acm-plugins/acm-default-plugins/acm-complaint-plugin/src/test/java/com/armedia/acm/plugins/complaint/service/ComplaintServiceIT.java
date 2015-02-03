package com.armedia.acm.plugins.complaint.service;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.addressable.model.ContactMethod;
import com.armedia.acm.plugins.addressable.model.PostalAddress;
import com.armedia.acm.plugins.complaint.dao.ComplaintDao;
import com.armedia.acm.plugins.complaint.model.complaint.ComplaintForm;
import com.armedia.acm.plugins.complaint.model.complaint.Contact;
import com.armedia.acm.plugins.complaint.model.complaint.MainInformation;
import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.plugins.person.model.PersonAlias;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations =
        {
				"/spring/spring-library-object-history.xml",
                "/spring/spring-library-data-source.xml",
                "/spring/spring-library-object-association-plugin.xml",
                "/spring/spring-library-complaint-plugin-test.xml",
                "/spring/spring-library-complaint.xml",
                "/spring/spring-library-complaint-plugin-test-mule.xml",
                "/spring/spring-library-activiti-actions.xml",
                "/spring/spring-library-activiti-configuration.xml",
                "/spring/spring-library-folder-watcher.xml",
                "/spring/spring-library-drools-monitor.xml",
                "/spring/spring-library-user-service.xml",
                "/spring/spring-library-context-holder.xml",
                "/spring/spring-library-data-access-control.xml",
                "/spring/spring-library-search.xml",
                "/spring/spring-library-ecm-file.xml",
                "/spring/spring-library-activemq.xml"
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

    @Before
    public void setUp() throws Exception
    {
        auditAdapter.setUserId("auditUser");

        service = new ComplaintService();
        service.setSaveComplaintTransaction(saveComplaintTransaction);

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
        
        frevvoComplaint.setLocation(location);

        Contact initiator = new Contact();
        frevvoComplaint.setInitiator(initiator);

        populateContact(initiator, "Complaintant");

        Contact witness = new Contact();
        frevvoComplaint.setPeople(Arrays.asList(witness));
        populateContact(witness, "Witness");

        assertEquals("Witness first", frevvoComplaint.getPeople().get(0).getMainInformation().getFirstName());
        assertEquals("Complaintant first", frevvoComplaint.getInitiator().getMainInformation().getFirstName());

        ComplaintForm savedFrevvoComplaint = service.saveComplaint(frevvoComplaint);

        entityManager.flush();

        assertNotNull(savedFrevvoComplaint.getComplaintId());
        assertNotNull(savedFrevvoComplaint.getComplaintNumber());

        com.armedia.acm.plugins.complaint.model.Complaint acmComplaint = dao.find(savedFrevvoComplaint.getComplaintId());

        assertNotNull(acmComplaint);
        verifyComplaint(frevvoComplaint, acmComplaint);

        verifyContact(frevvoComplaint.getInitiator(), acmComplaint.getOriginator());

        assertEquals(2, acmComplaint.getPersonAssociations().size());


        boolean witnessFound = false;
        for ( PersonAssociation pa : acmComplaint.getPersonAssociations() )
        {
            if ( "Witness".equals(pa.getPersonType()) )
            {
                witnessFound = true;
                verifyContact(frevvoComplaint.getPeople().get(0), pa);
            }
        }

        assertTrue(witnessFound);



    }

    private void verifyContact(Contact contact, PersonAssociation personAssociation)
    {
        assertNotNull(personAssociation);
        verifyContactMainInfo(contact.getMainInformation(), personAssociation);

        assertNotNull(personAssociation.getPerson().getPersonAliases());
        verifyContactAlias(contact.getAlias(), personAssociation);

        assertNotNull(personAssociation.getPerson().getAddresses());
        verifyAddresses(contact.getLocation().get(0), personAssociation);

        assertNotNull(personAssociation.getPerson().getOrganizations());
        verifyOrganizations(contact.getOrganization().get(0), personAssociation);

        assertNotNull(personAssociation.getPerson().getContactMethods());
        verifyContactMethods(contact.getCommunicationDevice().get(0), personAssociation);

        assertEquals(1, personAssociation.getTags().size());
        assertEquals("Anonymous", personAssociation.getTags().get(0));
    }

    private void populateContact(Contact in, String personType)
    {
        MainInformation initMainInfo = new MainInformation();
        in.setMainInformation(initMainInfo);

        PersonAlias alias = new PersonAlias();
        in.setAlias(alias);

        PostalAddress location = new PostalAddress();
        in.setLocation(Arrays.asList(location));

        Organization organization = new Organization();
        in.setOrganization(Arrays.asList(organization));

        ContactMethod contactMethod = new ContactMethod();
        in.setCommunicationDevice(Arrays.asList(contactMethod));

        initMainInfo.setAnonymous("true");
        initMainInfo.setDescription(personType + " Desc");
        initMainInfo.setFirstName(personType + " first");
        initMainInfo.setLastName(personType + " last");
        initMainInfo.setTitle("mr.");
        initMainInfo.setType(personType);

        alias.setAliasType("Nick Name");
        alias.setAliasValue(personType + " alias");

        location.setType("type");
        location.setCity("city");
        location.setCountry("country");
        location.setState("state");
        location.setStreetAddress("street address");
        location.setStreetAddress2("street address 2");
        location.setZip("zip");

        organization.setOrganizationType("org type");
        organization.setOrganizationValue("org value");

        contactMethod.setType("contact type");
        contactMethod.setValue("contact value");
    }

    private void verifyContactMethods(ContactMethod contactMethod, PersonAssociation pa)
    {
        assertEquals(1, pa.getPerson().getContactMethods().size());
        ContactMethod cm = pa.getPerson().getContactMethods().get(0);

        assertNotNull(cm.getValue());
        assertNotNull(cm.getType());

        assertEquals(contactMethod.getValue(), cm.getValue());
        assertEquals(contactMethod.getType(), cm.getType());
    }

    private void verifyComplaint(ComplaintForm frevvoComplaint, com.armedia.acm.plugins.complaint.model.Complaint acmComplaint)
    {
        assertNotNull(acmComplaint.getDetails());
        assertNotNull(acmComplaint.getIncidentDate());
        assertNotNull(acmComplaint.getPriority());
        assertNotNull(acmComplaint.getComplaintTitle());

        assertEquals(frevvoComplaint.getComplaintDescription(), acmComplaint.getDetails());
        assertEquals(frevvoComplaint.getDate().toString(), acmComplaint.getIncidentDate().toString());
        assertEquals(frevvoComplaint.getPriority(), acmComplaint.getPriority());
        assertEquals(frevvoComplaint.getComplaintTitle(), acmComplaint.getComplaintTitle());
    }

    private void verifyOrganizations(Organization organization, PersonAssociation pa)
    {
        assertEquals(1, pa.getPerson().getOrganizations().size());
        Organization org = pa.getPerson().getOrganizations().get(0);

        assertNotNull(org.getOrganizationValue());
        assertNotNull(org.getOrganizationType());

        assertEquals(organization.getOrganizationValue(), org.getOrganizationValue());
        assertEquals(organization.getOrganizationType(), org.getOrganizationType());
    }

    private void verifyAddresses(PostalAddress location, PersonAssociation pa)
    {
        assertEquals(1, pa.getPerson().getAddresses().size());
        PostalAddress addr = pa.getPerson().getAddresses().get(0);

        assertNotNull(addr.getCity());
        assertNotNull(addr.getZip());
        assertNotNull(addr.getStreetAddress());
        assertNotNull(addr.getStreetAddress2());
        assertNotNull(addr.getState());
        assertNotNull(addr.getCountry());

        assertEquals(location.getCity(), addr.getCity());
        assertEquals(location.getCountry(), addr.getCountry());
        assertEquals(location.getState(), addr.getState());
        assertEquals(location.getStreetAddress(), addr.getStreetAddress());
        assertEquals(location.getStreetAddress2(), addr.getStreetAddress2());
        assertEquals(location.getZip(), addr.getZip());

    }

    private void verifyContactAlias(PersonAlias alias, PersonAssociation pa)
    {
        assertEquals(1, pa.getPerson().getPersonAliases().size());
        PersonAlias acmPa = pa.getPerson().getPersonAliases().get(0);

        assertNotNull(acmPa.getAliasType());
        assertNotNull(acmPa.getAliasValue());

        assertEquals(alias.getAliasType(), acmPa.getAliasType());
        assertEquals(alias.getAliasValue(), acmPa.getAliasValue());
    }

    private void verifyContactMainInfo(MainInformation initMainInfo, PersonAssociation pa)
    {
        assertNotNull(pa.getPerson().getGivenName());
        assertNotNull(pa.getPerson().getFamilyName());
        assertNotNull(pa.getPerson().getTitle());
        assertNotNull(pa.getPersonType());
        assertNotNull(pa.getPersonDescription());

        assertEquals(initMainInfo.getFirstName(), pa.getPerson().getGivenName());
        assertEquals(initMainInfo.getLastName(), pa.getPerson().getFamilyName());
        assertEquals(initMainInfo.getTitle(), pa.getPerson().getTitle());
        assertEquals(initMainInfo.getType(), pa.getPersonType());
        assertEquals(initMainInfo.getDescription(), pa.getPersonDescription());
    }

}
