package com.armedia.acm.plugins.complaint.service;

import com.armedia.acm.plugins.addressable.model.PostalAddress;
import com.armedia.acm.plugins.complaint.dao.ComplaintDao;
import com.armedia.acm.plugins.complaint.model.complaint.Complaint;
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

import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations =
        {
                "/spring/spring-library-data-source.xml",
                "/spring/spring-library-complaint.xml",
                "/spring/spring-library-activiti-actions.xml",
                "/spring/spring-library-mule-context-manager.xml",
                "/spring/spring-library-activemq.xml",
                "/spring/spring-library-person.xml",
                "/spring/spring-library-activiti-configuration.xml",
                "/spring/spring-library-ecm-file.xml",
                "/spring/spring-library-folder-watcher.xml",
                "/spring/spring-library-cmis-configuration.xml",
                "/spring/spring-library-drools-monitor.xml"
        }
)
public class ComplaintServiceIT
{
    private ComplaintService service;

    @Autowired
    private ComplaintDao dao;

    @Autowired
    private SaveComplaintTransaction saveComplaintTransaction;

    @Before
    public void setUp() throws Exception
    {
        service = new ComplaintService();
        service.setSaveComplaintTransaction(saveComplaintTransaction);

        Authentication auth = new UsernamePasswordAuthenticationToken("anotherUser", "password");
        service.setAuthentication(auth);
    }

    @Test
    public void save() throws Exception
    {
        assertNotNull(service);

        Date now = new Date();

        Complaint frevvoComplaint = new Complaint();
        frevvoComplaint.setComplaintDescription("<strong>description</strong>");
        frevvoComplaint.setDate(now);
        frevvoComplaint.setPriority("High");

        Contact initiator = new Contact();
        frevvoComplaint.setInitiator(initiator);

        MainInformation initMainInfo = new MainInformation();
        initiator.setMainInformation(initMainInfo);

        PersonAlias alias = new PersonAlias();
        initiator.setAlias(alias);

        PostalAddress location = new PostalAddress();
        initiator.setLocation(Arrays.asList(location));

        Organization organization = new Organization();
        initiator.setOrganization(Arrays.asList(organization));

        initMainInfo.setAnonimuos("true");
        initMainInfo.setDescription("initDesc");
        initMainInfo.setFirstName("init first");
        initMainInfo.setLastName("init last");
        initMainInfo.setTitle("mr.");
        initMainInfo.setType("Complaintant");

        alias.setAliasType("Nick Name");
        alias.setAliasValue("init alias");

        location.setType("type");
        location.setCity("city");
        location.setCountry("country");
        location.setState("state");
        location.setStreetAddress("street address");
        location.setStreetAddress2("street address 2");
        location.setZip("zip");

        organization.setOrganizationType("org type");
        organization.setOrganizationValue("org value");

        Complaint savedFrevvoComplaint = service.saveComplaint(frevvoComplaint);

        assertNotNull(savedFrevvoComplaint.getComplaintId());

        com.armedia.acm.plugins.complaint.model.Complaint acmComplaint = dao.find(savedFrevvoComplaint.getComplaintId());

        assertNotNull(acmComplaint);
        assertEquals(frevvoComplaint.getComplaintDescription(), acmComplaint.getDetails());
        assertEquals(frevvoComplaint.getDate().toString(), acmComplaint.getIncidentDate().toString());
        assertEquals(frevvoComplaint.getPriority(), acmComplaint.getPriority());

        assertNotNull(acmComplaint.getOriginator());
        verifyContactMainInfo(initMainInfo, acmComplaint.getOriginator());

        assertNotNull(acmComplaint.getOriginator().getPerson().getPersonAliases());
        verifyContactAlias(alias, acmComplaint.getOriginator());

        assertNotNull(acmComplaint.getOriginator().getPerson().getAddresses());
        verifyAddresses(location, acmComplaint.getOriginator());

        assertNotNull(acmComplaint.getOriginator().getPerson().getOrganizations());
        verifyOrganizations(organization, acmComplaint.getOriginator());

    }

    private void verifyOrganizations(Organization organization, PersonAssociation pa)
    {
        assertEquals(1, pa.getPerson().getOrganizations().size());
        Organization org = pa.getPerson().getOrganizations().get(0);

        assertEquals(organization.getOrganizationValue(), org.getOrganizationValue());
        assertEquals(organization.getOrganizationType(), org.getOrganizationType());
    }

    private void verifyAddresses(PostalAddress location, PersonAssociation pa)
    {
        assertEquals(1, pa.getPerson().getAddresses().size());
        PostalAddress addr = pa.getPerson().getAddresses().get(0);

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
        assertEquals(alias.getAliasType(), acmPa.getAliasType());
        assertEquals(alias.getAliasValue(), acmPa.getAliasValue());
    }

    private void verifyContactMainInfo(MainInformation initMainInfo, PersonAssociation pa)
    {
        assertEquals(initMainInfo.getFirstName(), pa.getPerson().getGivenName());
        assertEquals(initMainInfo.getLastName(), pa.getPerson().getFamilyName());
        assertEquals(initMainInfo.getTitle(), pa.getPerson().getTitle());
        assertEquals(initMainInfo.getType(), pa.getPersonType());
        assertEquals(initMainInfo.getDescription(), pa.getPersonDescription());
    }

}
