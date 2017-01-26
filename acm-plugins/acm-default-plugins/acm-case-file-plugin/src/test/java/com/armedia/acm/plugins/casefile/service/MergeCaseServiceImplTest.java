package com.armedia.acm.plugins.casefile.service;

import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.model.MergeCaseOptions;
import com.armedia.acm.plugins.ecm.dao.AcmFolderDao;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.AcmCmisObjectList;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.participants.service.AcmParticipantService;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-library-merge-case-test.xml"})
public class MergeCaseServiceImplTest extends EasyMockSupport {

    private SaveCaseService saveCaseService;
    private AcmFolderDao acmFolderDao;
    private EcmFileService ecmFileService;
    private AcmFolderService acmFolderService;
    private EcmFileDao ecmFileDao;

    @Autowired
    private MergeCaseServiceImpl mergeCaseService;
    private Authentication auth;
    private String ipAddress;
    private CaseFileDao caseFileDao;
    private Long sourceId;
    private Long targetId;
    private AcmParticipantService acmParticipantService;

    @Before
    public void setUp() {
        auth = createMock(Authentication.class);
        ipAddress = "127.0.0.1";
        saveCaseService = createMock(SaveCaseService.class);
        caseFileDao = createMock(CaseFileDao.class);
        acmFolderDao = createMock(AcmFolderDao.class);
        ecmFileService = createMock(EcmFileService.class);
        acmFolderService = createMock(AcmFolderService.class);
        ecmFileDao = createMock(EcmFileDao.class);
        acmParticipantService = createMock(AcmParticipantService.class);


        mergeCaseService.setSaveCaseService(saveCaseService);
        mergeCaseService.setCaseFileDao(caseFileDao);
        mergeCaseService.setEcmFileService(ecmFileService);
        mergeCaseService.setAcmFolderDao(acmFolderDao);
        mergeCaseService.setAcmFolderService(acmFolderService);
        mergeCaseService.setEcmFileDao(ecmFileDao);
        mergeCaseService.setAcmParticipantService(acmParticipantService);


        EasyMock.expect(auth.getName()).andReturn("ann-acm").anyTimes();

        sourceId = 1L;
        targetId = 2L;
    }

    @Test
    public void testMergeCases() throws Exception {
        assertNotNull(mergeCaseService);

        CaseFile sourceCaseFile = new CaseFile();
        CaseFile targetCaseFile = new CaseFile();
        fillSourceDammyData(sourceCaseFile);
        fillTargetDammyData(targetCaseFile);

        EasyMock.expect(saveCaseService.saveCase(sourceCaseFile, auth, ipAddress)).andReturn(sourceCaseFile);
        EasyMock.expect(saveCaseService.saveCase(targetCaseFile, auth, ipAddress)).andReturn(targetCaseFile);
        EasyMock.expect(acmParticipantService.saveParticipant("ann-acm", "assignee", 2l, "CASE_FILE")).andReturn(new AcmParticipant());

        Capture<AcmFolder> folderCapture = new Capture<>();

        EasyMock.expect(caseFileDao.find(sourceId)).andReturn(sourceCaseFile);
        EasyMock.expect(caseFileDao.find(targetId)).andReturn(targetCaseFile);

        //EasyMock.expect(acmFolderDao.save(EasyMock.capture(folderCapture))).andReturn(folderCapture.getValue());

        EasyMock.expect(acmFolderService.moveRootFolder(EasyMock.anyObject(AcmFolder.class)
                , EasyMock.anyObject(AcmFolder.class))).andReturn(null);

        AcmFolder someFolder = new AcmFolder();
        EasyMock.expect(acmFolderService.addNewFolder(1l, String.format("%s(%s)", "Source", "55435345435_2133"))).andReturn(someFolder);

        EasyMock.expect(ecmFileDao.changeContainer(EasyMock.anyObject(AcmContainer.class), EasyMock.anyObject(AcmContainer.class), EasyMock.anyObject(List.class))).andReturn(1);

        AcmCmisObjectList cmisObjlectList = new AcmCmisObjectList();
        EasyMock.expect(ecmFileService.listFolderContents(EasyMock.anyObject(Authentication.class),
                EasyMock.anyObject(AcmContainer.class),
                EasyMock.eq(null),
                EasyMock.eq("name"),
                EasyMock.eq("ASC"),
                EasyMock.eq(0),
                EasyMock.eq(10000))).andReturn(cmisObjlectList);


        replayAll();

        MergeCaseOptions mergeCaseOptions = new MergeCaseOptions();
        mergeCaseOptions.setSourceCaseFileId(sourceId);
        mergeCaseOptions.setTargetCaseFileId(targetId);

        mergeCaseService.mergeCases(auth, ipAddress, mergeCaseOptions);

        //assertEquals("Target Details" + String.format(MergeCaseService.MERGE_TEXT_SEPPARATOR, "Source", "55435345435_2133") + "Source Details", targetCaseFile.getDetails());
    }

    private void fillTargetDammyData(CaseFile caseFile) {
        caseFile.setId(targetId);
        caseFile.setCaseNumber("123123123213_123213");

        AcmContainer targetContainer = new AcmContainer();
        AcmFolder targetFolder = new AcmFolder();
        targetFolder.setId(1l);
        targetFolder.setName("ROOT");
        targetContainer.setFolder(targetFolder);
        caseFile.setContainer(targetContainer);

        caseFile.setTitle("Target");
        caseFile.setDetails("Target Details");

    }

    private void fillSourceDammyData(CaseFile caseFile) {
        caseFile.setId(sourceId);
        caseFile.setCaseNumber("55435345435_2133");
        AcmContainer targetContainer = new AcmContainer();
        AcmFolder targetFolder = new AcmFolder();
        targetFolder.setId(1l);
        targetFolder.setName("ROOT");
        targetContainer.setFolder(targetFolder);
        caseFile.setContainer(targetContainer);


        caseFile.setTitle("Source");
        caseFile.setDetails("Source Details");
    }

    public void setMergeCaseService(MergeCaseServiceImpl mergeCaseService) {
        this.mergeCaseService = mergeCaseService;
    }
}