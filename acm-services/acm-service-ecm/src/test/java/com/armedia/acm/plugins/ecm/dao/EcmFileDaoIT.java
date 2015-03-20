package com.armedia.acm.plugins.ecm.dao;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;
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
        "/spring/spring-library-context-holder.xml"
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
        file.setVersionSeriesId("cmisFileId");
        file.setFileName("testFileName");

        EcmFileVersion version = new EcmFileVersion();
        version.setCmisObjectId("cmisObjectId");
        version.setVersionTag("versionTag");
        file.getVersions().add(version);

        file.setActiveVersionTag(version.getVersionTag());

        AcmFolder folder = new AcmFolder();
        folder.setCmisFolderId("cmisFolderId");
        folder.setName("folderName");

        AcmContainer container = new AcmContainer();
        container.setFolder(folder);
        container.setContainerObjectId(500L);
        container.setContainerObjectType("containerObjectType");

        file.setFolder(folder);
        file.setContainer(container);

        file = ecmFileDao.save(file);

        // flush() causes JPA to issue the SQL, so we will see any database exceptions from null constraints, etc.
        // but the transaction will still rollback, so we aren't popping test data into our db.
        entityManager.flush();

        log.info("File ID: " + file.getFileId());

        assertNotNull(file.getFileId());

    }
}
