package com.armedia.acm.plugins.casefile.service;

import com.armedia.acm.auth.AcmGrantedAuthority;
import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.exceptions.MergeCaseFilesException;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.model.MergeCaseOptions;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociation;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.participants.model.ParticipantTypes;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        "/spring/spring-library-property-file-manager.xml",
        "/spring/spring-library-profile.xml",
        "/spring/spring-library-acm-encryption.xml",
        "/spring/spring-library-task.xml",
        "/spring/spring-library-event.xml",
        "/spring/spring-library-note.xml"
})
@TransactionConfiguration(defaultRollback = true)
public class MergeCaseFileServiceIT extends EasyMock {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private CaseFileDao caseFileDao;
    @Autowired
    private EcmFileService ecmFileService;
    @Autowired
    private AcmFolderService acmFolderService;

    @Autowired
    private EcmFileDao ecmFileDao;

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
    public void mergeCaseFilesTest() throws MergeCaseFilesException, MuleException, AcmUserActionFailedException, AcmCreateObjectFailedException, IOException, AcmObjectNotFoundException {
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

        List<EcmFile> sourceFiles = ecmFileDao.findForContainer(sourceSaved.getContainer().getId());
        assertEquals(2, sourceFiles.size());

        List<EcmFile> targetFiles = ecmFileDao.findForContainer(targetSaved.getContainer().getId());
        assertEquals(0, targetFiles.size());

        mergeCaseService.mergeCases(auth, ipAddress, mergeCaseOptions);

        sourceFiles = ecmFileDao.findForContainer(sourceSaved.getContainer().getId());
        assertEquals(0, sourceFiles.size());

        targetFiles = ecmFileDao.findForContainer(targetSaved.getContainer().getId());
        assertEquals(2, targetFiles.size());

        CaseFile sourceCase = caseFileDao.find(sourceId);
        CaseFile targetCase = caseFileDao.find(targetId);

        ObjectAssociation sourceOa = null;
        for (ObjectAssociation oa : sourceCase.getChildObjects()) {
            if ("MERGED_TO".equals(oa.getCategory()))
                sourceOa = oa;
        }
        assertNotNull(sourceOa);
        assertNotNull(sourceOa.getTargetId());
        assertEquals(sourceOa.getTargetId().longValue(), targetCase.getId().longValue());

        ObjectAssociation targetOa = null;
        for (ObjectAssociation oa : targetCase.getChildObjects()) {
            if ("MERGED_FROM".equals(oa.getCategory()))
                targetOa = oa;
        }
        assertNotNull(targetOa);
        assertNotNull(targetOa.getTargetId());
        assertEquals(targetOa.getTargetId().longValue(), sourceCase.getId().longValue());

        assertEquals(sourceCase.getContainer().getFolder().getParentFolderId(), targetCase.getContainer().getFolder().getId());

    }

    @Test
    @Transactional
    public void mergeCaseFilesParticipantSameAssigneeTest() throws MergeCaseFilesException, MuleException, AcmUserActionFailedException, AcmCreateObjectFailedException, IOException, AcmObjectNotFoundException {
        auditAdapter.setUserId("auditUser");
        auth = createMock(Authentication.class);
        ipAddress = "127.0.0.1";

        String roleAdd = "ROLE_ADMINISTRATOR";
        AcmGrantedAuthority authority = new AcmGrantedAuthority(roleAdd);

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

        AcmParticipant assigneeParticipant = new AcmParticipant();
        assigneeParticipant.setParticipantLdapId(auth.getName());
        assigneeParticipant.setParticipantType(ParticipantTypes.ASSIGNEE);
        if (targetCaseFile.getParticipants() == null)
            targetCaseFile.setParticipants(new ArrayList<>());
        targetCaseFile.getParticipants().add(assigneeParticipant);

        CaseFile targetSaved = saveCaseService.saveCase(targetCaseFile, auth, ipAddress);

        targetId = targetSaved.getId();


        //verify that case files are saved
        assertNotNull(sourceId);
        assertNotNull(targetId);

        MergeCaseOptions mergeCaseOptions = new MergeCaseOptions();
        mergeCaseOptions.setSourceCaseFileId(sourceId);
        mergeCaseOptions.setTargetCaseFileId(targetId);

        mergeCaseService.mergeCases(auth, ipAddress, mergeCaseOptions);

        CaseFile sourceCase = caseFileDao.find(sourceId);
        CaseFile targetCase = caseFileDao.find(targetId);

        assertEquals(3, targetCase.getParticipants().size());

        AcmParticipant foundAssignee = null;
        for (AcmParticipant ap : targetCase.getParticipants()) {
            if (ParticipantTypes.ASSIGNEE.equals(ap.getParticipantType())) {
                foundAssignee = ap;
                break;
            }
        }
        assertNotNull(foundAssignee);
        assertEquals(auth.getName(), foundAssignee.getParticipantLdapId());
    }

    @Test
    @Transactional
    public void mergeCaseFilesParticipantDifferentAssigneeTest() throws MergeCaseFilesException, MuleException, AcmUserActionFailedException, AcmCreateObjectFailedException, IOException, AcmObjectNotFoundException {
        auditAdapter.setUserId("auditUser");
        auth = createMock(Authentication.class);
        ipAddress = "127.0.0.1";

        String roleAdd = "ROLE_ADMINISTRATOR";
        AcmGrantedAuthority authority = new AcmGrantedAuthority(roleAdd);

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

        AcmParticipant assigneeParticipant = new AcmParticipant();
        assigneeParticipant.setParticipantLdapId("ian-acm");
        assigneeParticipant.setParticipantType(ParticipantTypes.ASSIGNEE);
        if (targetCaseFile.getParticipants() == null)
            targetCaseFile.setParticipants(new ArrayList<>());
        targetCaseFile.getParticipants().add(assigneeParticipant);

        CaseFile targetSaved = saveCaseService.saveCase(targetCaseFile, auth, ipAddress);

        targetId = targetSaved.getId();


        //verify that case files are saved
        assertNotNull(sourceId);
        assertNotNull(targetId);

        assertEquals(3, targetSaved.getParticipants().size());

        AcmParticipant foundAssignee = null;
        for (AcmParticipant ap : targetSaved.getParticipants()) {
            if (ParticipantTypes.ASSIGNEE.equals(ap.getParticipantType())) {
                foundAssignee = ap;
                break;
            }
        }
        assertNotNull(foundAssignee);
        assertEquals("ian-acm", foundAssignee.getParticipantLdapId());

        //merge case files
        MergeCaseOptions mergeCaseOptions = new MergeCaseOptions();
        mergeCaseOptions.setSourceCaseFileId(sourceId);
        mergeCaseOptions.setTargetCaseFileId(targetId);

        mergeCaseService.mergeCases(auth, ipAddress, mergeCaseOptions);

        CaseFile sourceCase = caseFileDao.find(sourceId);
        CaseFile targetCase = caseFileDao.find(targetId);

        assertEquals(4, targetCase.getParticipants().size());

        foundAssignee = null;
        for (AcmParticipant ap : targetCase.getParticipants()) {
            if (ParticipantTypes.ASSIGNEE.equals(ap.getParticipantType())) {
                foundAssignee = ap;
                break;
            }
        }
        assertNotNull(foundAssignee);
        assertEquals(auth.getName(), foundAssignee.getParticipantLdapId());

        AcmParticipant foundPreviousAssignee = null;
        for (AcmParticipant ap : targetCase.getParticipants()) {
            if (ParticipantTypes.FOLLOWER.equals(ap.getParticipantType()) && "ian-acm".equals(ap.getParticipantLdapId())) {
                foundPreviousAssignee = ap;
                break;
            }
        }
        assertNotNull(foundPreviousAssignee);
    }
}
