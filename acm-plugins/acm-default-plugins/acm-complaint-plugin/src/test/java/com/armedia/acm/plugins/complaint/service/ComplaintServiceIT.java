package com.armedia.acm.plugins.complaint.service;

import com.armedia.acm.plugins.complaint.dao.ComplaintDao;
import com.armedia.acm.plugins.complaint.model.complaint.Complaint;
import com.armedia.acm.plugins.complaint.model.complaint.Contact;
import com.armedia.acm.plugins.complaint.model.complaint.MainInformation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
        MainInformation initMainInfo = new MainInformation();
        initiator.setMainInformation(initMainInfo);
        frevvoComplaint.setInitiator(initiator);

        initMainInfo.setAnonimuos("true");
        initMainInfo.setDescription("initDesc");
        initMainInfo.setFirstName("init first");
        initMainInfo.setLastName("init last");
        initMainInfo.setTitle("mr.");
        initMainInfo.setType("Complainant");

        Complaint savedFrevvoComplaint = service.saveComplaint(frevvoComplaint);

        assertNotNull(savedFrevvoComplaint.getComplaintId());

        com.armedia.acm.plugins.complaint.model.Complaint acmComplaint = dao.find(savedFrevvoComplaint.getComplaintId());

        assertNotNull(acmComplaint);
        assertEquals(frevvoComplaint.getComplaintDescription(), acmComplaint.getDetails());
        assertEquals(frevvoComplaint.getDate().toString(), acmComplaint.getIncidentDate().toString());
        assertEquals(frevvoComplaint.getPriority(), acmComplaint.getPriority());

        assertNotNull(acmComplaint.getOriginator());
        assertEquals(initMainInfo.getFirstName(), acmComplaint.getOriginator().getPerson().getGivenName());
        assertEquals(initMainInfo.getLastName(), acmComplaint.getOriginator().getPerson().getFamilyName());
        assertEquals(initMainInfo.getTitle(), acmComplaint.getOriginator().getPerson().getTitle());
        assertEquals(initMainInfo.getType(), acmComplaint.getOriginator().getPersonType());
        assertEquals(initMainInfo.getDescription(), acmComplaint.getOriginator().getPersonDescription());



    }

}
