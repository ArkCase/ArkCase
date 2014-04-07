package com.armedia.acm.plugins.complaint;

import com.armedia.acm.plugins.complaint.dao.ComplaintDao;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.person.model.Person;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring/spring-library-data-source.xml",
        "/spring/spring-library-complaint-plugin-test.xml",
        "/spring/spring-library-complaint.xml"})
@TransactionConfiguration(defaultRollback = true, transactionManager = "transactionManager")
public class ComplaintIT
{
    @Autowired
    private ComplaintDao complaintDao;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Test
    @Transactional
    public void saveComplaint()
    {
        Complaint complaint = new Complaint();
        complaint.setComplaintNumber(UUID.randomUUID().toString());
        complaint.setComplaintTitle("testTitle");
        complaint.setComplaintType("TEST");
        complaint.setCreated(new Date());
        complaint.setCreator("tester");
        complaint.setDetails("details");
        complaint.setPriority("testPriority");
        complaint.setModified(new Date());
        complaint.setModifier("testModifier");

        Person p = new Person();
        p.setModifier("testModifier");
        p.setCreator("testCreator");
        p.setCreated(new Date());
        p.setModified(new Date());
        p.setFamilyName("Person");
        p.setGivenName("ACM");
        p.setStatus("testStatus");

        complaint.setOriginator(p);

        complaint = complaintDao.save(complaint);

        log.info("Complaint ID: " + complaint.getId());
        log.info("Compaint originator object ID: " + complaint.getOriginator().getId());

        assertNotNull(complaint.getId());
        assertNotNull(complaint.getOriginator().getId());
    }

}
