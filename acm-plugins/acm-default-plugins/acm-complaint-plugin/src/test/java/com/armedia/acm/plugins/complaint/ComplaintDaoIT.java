package com.armedia.acm.plugins.complaint;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.complaint.dao.ComplaintDao;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociation;
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

import static org.junit.Assert.assertNotNull;

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
                "/spring/spring-library-complaint-plugin-test-mule.xml",
                "/spring/spring-library-forms-configuration.xml",
                "/spring/spring-library-authentication-token.xml",
                "/spring/spring-library-plugin-manager.xml",
                "/spring/spring-library-functional-access-control.xml",
                "/spring/spring-library-user-login.xml"
        }
)
@TransactionConfiguration(defaultRollback = true, transactionManager = "transactionManager")
public class ComplaintDaoIT
{
    @Autowired
    private ComplaintDao complaintDao;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private AuditPropertyEntityAdapter auditAdapter;

    private Logger log = LoggerFactory.getLogger(getClass());

    private ComplaintFactory complaintFactory = new ComplaintFactory();

    @Before
    public void setUp() throws Exception
    {
        auditAdapter.setUserId("auditUser");

        // stupid Drools throws a null pointer exception if we don't wait long enough here.  What bad software.
        Thread.sleep(1000);
    }

    @Test
    @Transactional
    public void saveComplaint() throws Exception
    {

        Complaint complaint = complaintFactory.complaint();
        complaint.setRestricted(true);

        AcmContainer acf = new AcmContainer();
        AcmFolder af = new AcmFolder();
        af.setCmisFolderId("cmisFolderId");
        af.setName("folderName");
        acf.setFolder(af);

        complaint.setContainer(acf);

        complaint = complaintDao.save(complaint);

        assertNotNull(complaint.getComplaintId());
        assertNotNull(complaint.getOriginator());
        assertNotNull(complaint.getOriginator().getId());

        log.info("Complaint ID: " + complaint.getComplaintId());
        log.info("Complaint originator object ID: " + complaint.getOriginator().getId());

        if (complaint.getChildObjects() != null && !complaint.getChildObjects().isEmpty())
        {
            for (ObjectAssociation oa : complaint.getChildObjects())
            {
                assertNotNull(oa.getAssociationId());
            }
        }

        entityManager.flush();


    }

}
