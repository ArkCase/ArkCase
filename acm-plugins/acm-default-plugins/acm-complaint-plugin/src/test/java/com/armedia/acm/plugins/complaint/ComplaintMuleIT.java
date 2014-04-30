package com.armedia.acm.plugins.complaint;

import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.service.SaveComplaintTransaction;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
        "/spring/spring-library-person.xml",
        "/spring/spring-library-activiti-actions.xml",
        "/spring/spring-library-activemq.xml",
        "/spring/spring-library-activiti-configuration.xml",
        "/spring/spring-library-folder-watcher.xml",
        "/spring/spring-library-cmis-configuration.xml",
        "/spring/spring-library-drools-monitor.xml",
        "/spring/spring-library-ecm-file.xml"})
@TransactionConfiguration(defaultRollback = false, transactionManager = "transactionManager")
public class ComplaintMuleIT
{

    @Autowired
    private SaveComplaintTransaction saveComplaintTransaction;

    private ComplaintFactory complaintFactory = new ComplaintFactory();

    private Logger log = LoggerFactory.getLogger(getClass());

    @Test
    @Transactional
    public void saveComplaintFlow() throws Exception
    {
        Complaint complaint = complaintFactory.complaint();

        // complaint number should be set by the flow
        complaint.setComplaintNumber(null);

        Authentication auth = new UsernamePasswordAuthenticationToken("testUser", "testUser");

        Complaint saved = saveComplaintTransaction.saveComplaint(complaint, auth);

        assertNotNull(saved.getComplaintId());
        assertNotNull(saved.getComplaintNumber());

        log.info("New complaint id: " + saved.getComplaintId());
        log.info("New complaint number: " + saved.getComplaintNumber());
    }
}
