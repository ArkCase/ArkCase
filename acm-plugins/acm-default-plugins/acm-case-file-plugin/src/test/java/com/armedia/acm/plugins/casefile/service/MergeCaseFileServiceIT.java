package com.armedia.acm.plugins.casefile.service;

import com.armedia.acm.auth.AcmGrantedAuthority;
import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.exceptions.MergeCaseFilesException;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.model.MergeCaseOptions;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mule.api.MuleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-activiti-configuration.xml",
        "/spring/spring-library-activiti-actions.xml",
        "/spring/spring-library-activemq.xml",
        "/spring/spring-library-case-file.xml",
        "/spring/test-case-file-context.xml",
        "/spring/spring-library-data-source.xml",
        "/spring/spring-library-ecm-file.xml",
        "/spring/spring-library-user-service.xml",
        "/spring/spring-library-context-holder.xml",
        "/spring/spring-library-search.xml",
        "/spring/spring-library-data-access-control.xml",
        "/spring/spring-library-folder-watcher.xml",
        "/spring/spring-library-drools-monitor.xml",
        "/spring/spring-library-merge-case-test-IT.xml",
        "/spring/spring-library-ms-outlook-integration.xml",
        "/spring/spring-library-ms-outlook-plugin.xml",
        "/spring/spring-library-mule-context-manager.xml",
        "/spring/spring-library-object-history.xml",
        "/spring/spring-library-particpants.xml",
        "/spring/spring-library-person.xml",
        "/spring/spring-library-property-file-manager.xml"
})
@TransactionConfiguration(defaultRollback = true)
public class MergeCaseFileServiceIT extends EasyMock {
    @Autowired
    private CaseFileDao caseFileDao;
    @Autowired
    private EcmFileService ecmFileService;
    @Autowired
    private AcmFolderService acmFolderService;

    @Autowired
    private MergeCaseService mergeCaseService;

    @Autowired
    private SaveCaseService saveCaseService;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private AuditPropertyEntityAdapter auditAdapter;

    private Long sourceId;
    private Long targetId;
    private Authentication auth;
    private String ipAddress;

    @Test
    @Transactional
    public void mergeCaseFilesTest() throws MergeCaseFilesException, MuleException, AcmUserActionFailedException, AcmCreateObjectFailedException, IOException {
        auditAdapter.setUserId("auditUser");
        auth = createMock(Authentication.class);
        ipAddress = "127.0.0.1";

        String roleAdd = "ROLE_ADMINISTRATOR";
        AcmGrantedAuthority authority = new AcmGrantedAuthority(roleAdd);




        Resource dammyDocument = new ClassPathResource("/documents/textDammydocument.txt");
        assertTrue(dammyDocument.exists());

        assertNotNull(caseFileDao);
        assertNotNull(ecmFileService);
        assertNotNull(acmFolderService);
        assertNotNull(mergeCaseService);

        expect(auth.getName()).andReturn("ann-acm").anyTimes();
        expect((List<AcmGrantedAuthority>) auth.getAuthorities()).andReturn(Arrays.asList(authority)).atLeastOnce();
        replay(auth);
        //create source case file
        CaseFile sourceCaseFile = new CaseFile();
        sourceCaseFile.setCaseType("caseType");
        sourceCaseFile.setTitle("title");

        CaseFile sourceSaved = saveCaseService.saveCase(sourceCaseFile, auth, ipAddress);
        sourceId = sourceSaved.getId();

        //create target case file
        CaseFile targetCaseFile = new CaseFile();
        targetCaseFile.setCaseType("caseType");
        targetCaseFile.setTitle("title");


        CaseFile targetSaved = saveCaseService.saveCase(targetCaseFile, auth, ipAddress);

        targetId = targetSaved.getId();


        //verify that case files are saved
        assertNotNull(sourceId);
        assertNotNull(targetId);


        //upload in root folder
        ecmFileService.upload("dammyDocument1.txt",
                "attachment",
                "Document",
                dammyDocument.getInputStream(),
                "text/plain",
                "dammyDocument1.txt",
                auth,
                sourceSaved.getContainer().getFolder().getCmisFolderId(),
                sourceSaved.getContainer().getContainerObjectType(),
                sourceSaved.getContainer().getContainerObjectId());

        //create folder and add document to this folder
        AcmFolder folderInSourceCase = acmFolderService.addNewFolder(sourceSaved.getContainer().getFolder().getId(), "some_folder");

        ecmFileService.upload("dammyDocument.txt",
                "attachment",
                "Document",
                dammyDocument.getInputStream(),
                "text/plain",
                "dammyDocument.txt",
                auth,
                folderInSourceCase.getCmisFolderId(),
                sourceSaved.getContainer().getContainerObjectType(),
                sourceSaved.getContainer().getContainerObjectId());
        MergeCaseOptions mergeCaseOptions = new MergeCaseOptions();
        mergeCaseOptions.setSourceCaseFileId(sourceId);
        mergeCaseOptions.setTargetCaseFileId(targetId);

        mergeCaseService.mergeCases(auth, ipAddress, mergeCaseOptions);

        CaseFile sourceCase = caseFileDao.find(sourceId);
        assertNotNull(sourceCase.getMergedTo());
        assertEquals(targetId, sourceCase.getMergedTo().getId());

    }

}
