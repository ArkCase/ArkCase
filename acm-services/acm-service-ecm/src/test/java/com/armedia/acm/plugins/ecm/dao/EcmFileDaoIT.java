package com.armedia.acm.plugins.ecm.dao;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.ecm.model.EcmFile;
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

import static org.junit.Assert.*;

/**
 * Created by armdev on 4/22/14.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring/spring-library-data-source.xml",
        "/spring/spring-library-ecm-file.xml",
        "/spring/spring-library-mule-context-manager.xml",
        "/spring/spring-library-cmis-configuration.xml",
        "/spring/spring-library-activemq.xml",
        "/spring/spring-library-search.xml",
        "/spring/spring-library-context-holder.xml",
        "/spring/spring-library-activiti-actions.xml",
        "/spring/spring-library-activiti-configuration.xml",
        "/spring/spring-library-event.xml"
})
@TransactionConfiguration(defaultRollback = true, transactionManager = "transactionManager")
public class EcmFileDaoIT
{
    @Autowired
    private EcmFileDao ecmFileDao;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private AuditPropertyEntityAdapter auditAdapter;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        auditAdapter.setUserId("auditUser");
    }

    @Test
    @Transactional
    public void saveEcmFile()
    {

        EcmFile file = new EcmFile();

        file.setFileMimeType("text/plain");
        file.setEcmFileId("cmisFileId");
        file.setFileName("testFileName");

        ObjectAssociation parent = new ObjectAssociation();
        parent.setParentId(12345L);
        parent.setParentType("COMPLAINT");
        parent.setParentName("Test Name");
        parent.setCreator("tester");
        parent.setModifier("testModifier");
        file.addParentObject(parent);

        file = ecmFileDao.save(file);

        // flush() causes JPA to issue the SQL, so we will see any database exceptions from null constraints, etc.
        // but the transaction will still rollback, so we aren't popping test data into our db.
        entityManager.flush();

        log.info("File ID: " + file.getFileId());

        assertNotNull(file.getFileId());

    }
}
