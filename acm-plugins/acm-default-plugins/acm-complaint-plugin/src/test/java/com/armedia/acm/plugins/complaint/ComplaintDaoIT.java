package com.armedia.acm.plugins.complaint;

import com.armedia.acm.plugins.complaint.dao.ComplaintDao;
import com.armedia.acm.plugins.complaint.model.Complaint;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring/spring-library-data-source.xml",
        "/spring/spring-library-complaint-plugin-test.xml",
        "/spring/spring-library-complaint.xml",
        "/spring/spring-library-mule-context-manager.xml",
        "/spring/spring-library-activiti-actions.xml",
        "/spring/spring-library-activemq.xml",
        "/spring/spring-library-activiti-configuration.xml"
        })
@TransactionConfiguration(defaultRollback = true, transactionManager = "transactionManager")
public class ComplaintDaoIT
{
    @Autowired
    private ComplaintDao complaintDao;

    private Logger log = LoggerFactory.getLogger(getClass());

    private ComplaintFactory complaintFactory = new ComplaintFactory();

    @Test
    @Transactional
    public void saveComplaint()
    {

        Complaint complaint = complaintFactory.complaint();

        complaint = complaintDao.save(complaint);

        log.info("Complaint ID: " + complaint.getComplaintId());
        log.info("Complaint originator object ID: " + complaint.getOriginator().getId());

        assertNotNull(complaint.getComplaintId());
        assertNotNull(complaint.getOriginator().getId());
    }

}
