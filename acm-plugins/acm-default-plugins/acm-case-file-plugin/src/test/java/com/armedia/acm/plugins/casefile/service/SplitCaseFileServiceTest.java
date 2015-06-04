package com.armedia.acm.plugins.casefile.service;

import com.armedia.acm.auth.AcmGrantedAuthority;
import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.exceptions.MergeCaseFilesException;
import com.armedia.acm.plugins.casefile.exceptions.SplitCaseFileException;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.model.SplitCaseOptions;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.exception.AcmFolderException;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
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
public class SplitCaseFileServiceTest extends EasyMock {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private CaseFileDao caseFileDao;

    @Autowired
    private SplitCaseService splitCaseService;

    @Autowired
    private SaveCaseService saveCaseService;

    @Autowired
    AcmFolderService acmFolderService;

    @Autowired
    EcmFileService ecmFileService;

    @Autowired
    EcmFileDao ecmFileDao;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private AuditPropertyEntityAdapter auditAdapter;

    private Long savedCaseFileId;
    private Authentication auth;
    private String ipAddress;

    @Test
    @Transactional
    public void splitCaseTest() throws MergeCaseFilesException, MuleException, AcmUserActionFailedException, AcmCreateObjectFailedException, IOException, SplitCaseFileException, AcmFolderException, AcmObjectNotFoundException {
        auditAdapter.setUserId("auditUser");
        auth = createMock(Authentication.class);
        ipAddress = "127.0.0.1";

        String roleAdd = "ROLE_ADMINISTRATOR";
        AcmGrantedAuthority authority = new AcmGrantedAuthority(roleAdd);


        Resource dammyDocument = new ClassPathResource("/documents/textDammydocument.txt");
        assertTrue(dammyDocument.exists());

        assertNotNull(caseFileDao);
        assertNotNull(splitCaseService);
        assertNotNull(ecmFileService);
        assertNotNull(acmFolderService);

        expect(auth.getName()).andReturn("ann-acm").anyTimes();
        expect((List<AcmGrantedAuthority>) auth.getAuthorities()).andReturn(Arrays.asList(authority)).atLeastOnce();
        replay(auth);

        //create source case file
        CaseFile caseFile = new CaseFile();
        caseFile.setCaseType("caseType");
        caseFile.setTitle("title");

        CaseFile caseFileSaved = saveCaseService.saveCase(caseFile, auth, ipAddress);
        savedCaseFileId = caseFileSaved.getId();


        //verify that case files are saved
        assertNotNull(savedCaseFileId);


        //upload in root folder
        ecmFileService.upload("dammyDocument1.txt",
                "attachment",
                "Document",
                dammyDocument.getInputStream(),
                "text/plain",
                "dammyDocument1.txt",
                auth,
                caseFileSaved.getContainer().getFolder().getCmisFolderId(),
                caseFileSaved.getContainer().getContainerObjectType(),
                caseFileSaved.getContainer().getContainerObjectId());

        //create folder and add document to this folder
        AcmFolder folderInCaseFile = acmFolderService.addNewFolder(caseFileSaved.getContainer().getFolder().getId(), "some_folder");

        ecmFileService.upload("dammyDocument.txt",
                "attachment",
                "Document",
                dammyDocument.getInputStream(),
                "text/plain",
                "dammyDocument.txt",
                auth,
                folderInCaseFile.getCmisFolderId(),
                caseFileSaved.getContainer().getContainerObjectType(),
                caseFileSaved.getContainer().getContainerObjectId());

        SplitCaseOptions splitCaseOptions = new SplitCaseOptions();
        splitCaseOptions.setCaseFileId(savedCaseFileId);


        List<EcmFile> caseFileDocuments = ecmFileDao.findForContainer(caseFileSaved.getContainer().getId());
        assertEquals(2, caseFileDocuments.size());


        CaseFile splitted = splitCaseService.splitCase(auth, ipAddress, splitCaseOptions);

        caseFileSaved = caseFileDao.find(savedCaseFileId);

        List<EcmFile> splittedCaseFileDocuments = ecmFileDao.findForContainer(splitted.getContainer().getId());
        assertEquals(0, splittedCaseFileDocuments.size());

        assertNotEquals(splitted.getId().longValue(), caseFileSaved.getId().longValue());
        assertNotEquals(splitted.getContainer().getId().longValue(), caseFileSaved.getContainer().getId().longValue());
        assertNotEquals(splitted.getContainer().getFolder().getId().longValue(), caseFileSaved.getContainer().getFolder().getId().longValue());
        assertTrue(splitted.getCreated().after(caseFileSaved.getCreated()));
        assertTrue(splitted.getModified().after(caseFileSaved.getModified()));

    }

}
