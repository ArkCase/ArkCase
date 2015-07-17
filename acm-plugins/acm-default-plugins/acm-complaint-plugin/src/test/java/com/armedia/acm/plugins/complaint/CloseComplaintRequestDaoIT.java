package com.armedia.acm.plugins.complaint;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.casefile.model.Disposition;
import com.armedia.acm.plugins.complaint.dao.CloseComplaintRequestDao;
import com.armedia.acm.plugins.complaint.model.CloseComplaintRequest;
import com.armedia.acm.services.participants.model.AcmParticipant;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
        @ContextConfiguration(name = "spring",
                locations = {
                        "/spring/spring-library-object-history.xml",
                        "/spring/spring-library-data-source.xml",
                        "/spring/spring-library-object-association-plugin.xml",
                        "/spring/spring-library-complaint-plugin-test.xml",
                        "/spring/spring-library-complaint.xml",
                        "/spring/spring-library-activiti-actions.xml",
                        "/spring/spring-library-activiti-configuration.xml",
                        "/spring/spring-library-folder-watcher.xml",
                        "/spring/spring-library-drools-monitor.xml",
                        "/spring/spring-library-user-service.xml",
                        "/spring/spring-library-context-holder.xml",
                        "/spring/spring-library-data-access-control.xml",
                        "/spring/spring-library-search.xml",
                        "/spring/spring-library-ecm-file.xml",
                        "/spring/spring-library-particpants.xml",
                        "/spring/spring-library-property-file-manager.xml",
                        "/spring/spring-library-person.xml",
                        "/spring/spring-library-case-file.xml",
                        "/spring/spring-library-ms-outlook-integration.xml",
                        "/spring/spring-library-ms-outlook-plugin.xml",
                        "/spring/spring-library-profile.xml",
                        "/spring/spring-library-acm-encryption.xml",
                        "/spring/spring-library-task.xml",
                        "/spring/spring-library-note.xml",
                        "/spring/spring-library-event.xml",
                        "/spring/spring-library-complaint-plugin-test-mule.xml"
})
@TransactionConfiguration(defaultRollback = true, transactionManager = "transactionManager")
public class CloseComplaintRequestDaoIT
{
    @Autowired
    private CloseComplaintRequestDao requestDao;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private AuditPropertyEntityAdapter auditAdapter;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp()
    {
        auditAdapter.setUserId("auditUser");
    }

    @Test
    @Transactional
    public void saveCloseComplaintRequest()
    {

        CloseComplaintRequest ccr = new CloseComplaintRequest();
        ccr.setComplaintId(500L);
        ccr.setStatus("DRAFT");

        AcmParticipant reviewer = new AcmParticipant();
        reviewer.setParticipantType("approver");
        reviewer.setParticipantLdapId("jgarcia");

        ccr.getParticipants().add(reviewer);

        Disposition d = new Disposition();
        d.setDispositionType("add-to-existing-case");
        d.setExistingCaseNumber("12345678");

        ccr.setDisposition(d);

        CloseComplaintRequest saved = requestDao.save(ccr);

        entityManager.flush();

        log.info("CCR ID: " + saved.getId());

        CloseComplaintRequest found = requestDao.find(saved.getId());

        assertEquals(1, found.getParticipants().size());
    }

}
