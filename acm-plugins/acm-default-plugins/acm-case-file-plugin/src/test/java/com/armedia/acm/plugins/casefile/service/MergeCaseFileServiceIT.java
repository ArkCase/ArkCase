package com.armedia.acm.plugins.casefile.service;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mule.api.MuleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
        "/spring/spring-library-property-file-manager.xml"
})
@TransactionConfiguration(defaultRollback = true)
public class MergeCaseFileServiceIT extends EasyMock {
    @Autowired
    private CaseFileDao caseFileDao;

    private MergeCaseService mergeCaseService;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private AuditPropertyEntityAdapter auditAdapter;

    private Long sourceId;
    private Long targetId;
    private Authentication auth;
    private String ipAddress;

    @Before
    public void setUp() {

        auditAdapter.setUserId("auditUser");
        auth = createMock(Authentication.class);
        ipAddress = "127.0.0.1";
        CaseFile sourceCaseFile = new CaseFile();
        sourceCaseFile.setCaseNumber("caseNumber");
        sourceCaseFile.setCaseType("caseType");
        sourceCaseFile.setStatus("status");
        sourceCaseFile.setTitle("title");
        sourceCaseFile.setRestricted(true);

        AcmContainer sourceContainer = new AcmContainer();
        AcmFolder sourceFolder = new AcmFolder();
        sourceFolder.setCmisFolderId("cmisFolderId");
        sourceFolder.setName("folderName");
        sourceContainer.setFolder(sourceFolder);
        sourceCaseFile.setContainer(sourceContainer);

        CaseFile sourceSaved = caseFileDao.save(sourceCaseFile);
        entityManager.flush();
        sourceId = sourceSaved.getId();

        CaseFile targetCaseFile = new CaseFile();
        targetCaseFile.setCaseNumber("caseNumber");
        targetCaseFile.setCaseType("caseType");
        targetCaseFile.setStatus("status");
        targetCaseFile.setTitle("title");
        targetCaseFile.setRestricted(true);

        AcmContainer targetContainer = new AcmContainer();
        AcmFolder targetFolder = new AcmFolder();
        targetFolder.setCmisFolderId("cmisFolderId");
        targetFolder.setName("folderName");
        targetContainer.setFolder(targetFolder);
        targetCaseFile.setContainer(targetContainer);

        CaseFile targetSaved = caseFileDao.save(targetCaseFile);
        entityManager.flush();
        targetId = targetSaved.getId();
    }

    @Test
    public void mergeCaseFilesTest() {
        assertNotNull(sourceId);
        assertNotNull(targetId);

        try {
            mergeCaseService.mergeCases(auth, ipAddress, sourceId, targetId);
        } catch (MuleException e) {
            e.printStackTrace();
        }

        CaseFile sourceCase = caseFileDao.find(sourceId);
        assertNotNull(sourceCase.getMergedTo());
        assertEquals(targetId, sourceCase.getMergedTo().getId());

    }

}
