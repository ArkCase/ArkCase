package com.armedia.acm.plugins.casefile.dao;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.Date;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"/spring/spring-library-object-history.xml",
        "/spring/spring-library-case-file.xml",
        "/spring/spring-library-data-source.xml",
        "/spring/test-case-file-context.xml",
        "/spring/spring-library-user-service.xml",
        "/spring/spring-library-context-holder.xml",
        "/spring/spring-library-search.xml",
        "/spring/spring-library-data-access-control.xml",
        "/spring/spring-library-folder-watcher.xml",
        "/spring/spring-library-activiti-configuration.xml",
        "/spring/spring-library-particpants.xml",
        "/spring/spring-library-drools-monitor.xml",
        "/spring/spring-library-ms-outlook-integration.xml",
        "/spring/spring-library-ms-outlook-plugin.xml",
        "/spring/spring-library-ecm-file.xml",
        "/spring/spring-library-property-file-manager.xml",
        "/spring/spring-library-profile.xml",
        "/spring/spring-library-acm-encryption.xml",
        "/spring/spring-library-task.xml",
        "/spring/spring-library-event.xml",
        "/spring/spring-library-note.xml"
})
@TransactionConfiguration(defaultRollback = true)
public class CaseFileDaoIT
{
    @Autowired
    private CaseFileDao caseFileDao;

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
    public void saveCaseFile()
    {
        assertNotNull(caseFileDao);
        assertNotNull(entityManager);

        CaseFile caseFile = new CaseFile();
        caseFile.setCaseNumber("caseNumber");
        caseFile.setCaseType("caseType");
        caseFile.setStatus("status");
        caseFile.setTitle("title");
        caseFile.setRestricted(true);

        AcmContainer container = new AcmContainer();
        AcmFolder folder = new AcmFolder();
        folder.setCmisFolderId("cmisFolderId");
        folder.setName("folderName");
        container.setFolder(folder);
        caseFile.setContainer(container);

        caseFile.setNextCourtDate(new Date());
        caseFile.setCourtroomName("courtroomName");
        caseFile.setResponsibleOrganization("responsibleOrganization");

        CaseFile saved = caseFileDao.save(caseFile);

        entityManager.flush();

        assertNotNull(saved.getId());
    }

}
