package com.armedia.acm.plugins.complaint;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.service.SaveComplaintTransaction;
import org.junit.Before;
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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"/spring/spring-library-object-history.xml",
		"/spring/spring-library-data-source.xml",
        "/spring/spring-library-complaint-plugin-test.xml",
        "/spring/spring-library-complaint-plugin-test-mule.xml",
        "/spring/spring-library-complaint-plugin-test-ecm.xml",
        "/spring/spring-library-object-association-plugin.xml",
        "/spring/spring-library-complaint.xml",
        "/spring/spring-library-activiti-actions.xml",
        "/spring/spring-library-activiti-configuration.xml",
        "/spring/spring-library-folder-watcher.xml",
        "/spring/spring-library-drools-monitor.xml",
        "/spring/spring-library-user-service.xml",
        "/spring/spring-library-context-holder.xml",
        "/spring/spring-library-particpants.xml",
        "/spring/spring-library-data-access-control.xml",
        "/spring/spring-library-search.xml"})
@TransactionConfiguration(defaultRollback = false, transactionManager = "transactionManager")
public class ComplaintMuleIT
{

    @Autowired
    private SaveComplaintTransaction saveComplaintTransaction;

    private ComplaintFactory complaintFactory = new ComplaintFactory();

    private Logger log = LoggerFactory.getLogger(getClass());

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private AuditPropertyEntityAdapter auditAdapter;

    @Before
    public void setUp()
    {
        auditAdapter.setUserId("auditUser");
    }


    @Test
    @Transactional
    public void saveComplaintFlow() throws Exception
    {
        Complaint complaint = complaintFactory.complaint();
        complaint.setRestricted(true);

        // complaint number should be set by the flow
        complaint.setComplaintNumber(null);

        Authentication auth = new UsernamePasswordAuthenticationToken("testUser", "testUser");

        Complaint saved = saveComplaintTransaction.saveComplaint(complaint, auth);

        entityManager.flush();

        assertNotNull(saved.getComplaintId());
        assertNotNull(saved.getComplaintNumber());

        log.info("New complaint id: " + saved.getComplaintId());
        log.info("New complaint number: " + saved.getComplaintNumber());
    }
}
