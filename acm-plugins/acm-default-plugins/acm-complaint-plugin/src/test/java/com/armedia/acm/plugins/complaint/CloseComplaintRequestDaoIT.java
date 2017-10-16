package com.armedia.acm.plugins.complaint;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.casefile.model.Disposition;
import com.armedia.acm.plugins.complaint.dao.CloseComplaintRequestDao;
import com.armedia.acm.plugins.complaint.dao.ComplaintDao;
import com.armedia.acm.plugins.complaint.model.CloseComplaintRequest;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
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

import java.util.UUID;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(name = "spring",
        locations = {
                "/spring/spring-library-data-source.xml",
                "/spring/spring-library-complaint-dao-test.xml",
                "/spring/spring-library-context-holder.xml",
                "/spring/spring-library-property-file-manager.xml",
                "/spring/spring-library-acm-encryption.xml"
        })
@TransactionConfiguration(defaultRollback = true, transactionManager = "transactionManager")
public class CloseComplaintRequestDaoIT
{
    @Autowired
    private CloseComplaintRequestDao requestDao;

    @Autowired
    private ComplaintDao complaintDao;

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

        // MySQL needs the complaint to exist
        Complaint complaint = new Complaint();
        complaint.setComplaintTitle("Grateful Dead");
        complaint.setComplaintNumber("Grateful Dead");

        AcmFolder folder = new AcmFolder();
        folder.setCmisFolderId("cmisFolderId" + UUID.randomUUID().toString());
        folder.setName("The Band");
        complaint.getContainer().setFolder(folder);

        Complaint persisted = complaintDao.save(complaint);

        complaintDao.getEm().flush();

        CloseComplaintRequest ccr = new CloseComplaintRequest();
        ccr.setComplaintId(persisted.getComplaintId());
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
