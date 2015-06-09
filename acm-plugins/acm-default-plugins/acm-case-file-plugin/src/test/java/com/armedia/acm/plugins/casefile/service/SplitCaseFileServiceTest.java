package com.armedia.acm.plugins.casefile.service;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.exceptions.MergeCaseFilesException;
import com.armedia.acm.plugins.casefile.exceptions.SplitCaseFileException;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.model.SplitCaseOptions;
import com.armedia.acm.plugins.ecm.exception.AcmFolderException;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.easymock.IAnswer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.eq;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-split-case-test.xml"
})
@TransactionConfiguration(defaultRollback = true)
public class SplitCaseFileServiceTest extends EasyMockSupport {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private SplitCaseServiceImpl splitCaseService;

    private SaveCaseService saveCaseService;
    private CaseFileDao caseFileDao;
    private EcmFileService ecmFileService;
    private AcmFolderService acmFolderService;


    private Long savedCaseFileId;
    private Authentication auth;
    AcmFolder sourceFolder;
    private String ipAddress;
    private Long sourceId;
    AcmFolder copiedFolder;
    private Map<Long, AcmFolder> folderMap = new HashMap<>();
    private Map<Long, EcmFile> documentMap = new HashMap<>();
    private long generated;


    @Before
    public void setUp() {
        auth = createMock(Authentication.class);
        ipAddress = "127.0.0.1";

        sourceId = 1L;
        generated = 10l;
        //create mocks
        saveCaseService = createMock(SaveCaseService.class);
        caseFileDao = createMock(CaseFileDao.class);
        ecmFileService = createMock(EcmFileService.class);
        acmFolderService = createMock(AcmFolderService.class);

        sourceFolder = new AcmFolder();
        sourceFolder.setId(100l);
        sourceFolder.setName("ROOT");
        addToFolderMap(sourceFolder);

        AcmFolder copiedFolder = null;

        splitCaseService.setSaveCaseService(saveCaseService);
        splitCaseService.setCaseFileDao(caseFileDao);
        splitCaseService.setAcmFolderService(acmFolderService);
        splitCaseService.setEcmFileService(ecmFileService);
        createSourceFolderStructure();
    }


    @Test
    public void splitCaseTest() throws MergeCaseFilesException, MuleException, AcmUserActionFailedException, AcmCreateObjectFailedException, IOException, SplitCaseFileException, AcmFolderException, AcmObjectNotFoundException {
        assertNotNull(splitCaseService);

        CaseFile sourceCaseFile = new CaseFile();
        sourceCaseFile.setId(sourceId);
        sourceCaseFile.setCaseNumber("55435345435_2133");
        AcmContainer sourceContainer = new AcmContainer();
        sourceContainer.setFolder(sourceFolder);
        sourceCaseFile.setContainer(sourceContainer);
        sourceCaseFile.setTitle("Source");
        sourceCaseFile.setDetails("Source Details");


        Capture<CaseFile> caseFileCapture = new Capture<>();
        EasyMock.expect(caseFileDao.find(sourceId)).andReturn(sourceCaseFile).anyTimes();
        EasyMock.expect(saveCaseService.saveCase(capture(caseFileCapture), eq(auth), eq(ipAddress))).andAnswer(new IAnswer<CaseFile>() {
            public CaseFile answer() throws Throwable {
                CaseFile copiedCaseFile = caseFileCapture.getValue();
                copiedCaseFile.setId(2l);

                AcmContainer copiedContainer = new AcmContainer();
                copiedFolder = new AcmFolder();
                copiedFolder.setId(200l);
                copiedFolder.setName("ROOT");
                copiedContainer.setContainerObjectId(200l);
                copiedContainer.setContainerObjectType("CASE_FILE");
                copiedContainer.setFolder(copiedFolder);
                copiedCaseFile.setContainer(copiedContainer);
                addToFolderMap(copiedFolder);

                return copiedCaseFile;
            }
        });

        EasyMock.expect(saveCaseService.saveCase(eq(sourceCaseFile), eq(auth), eq(ipAddress))).andReturn(sourceCaseFile);


        Capture<Long> fileIdCapture = new Capture<>();
        Capture<Long> targetObjectIdCapture = new Capture<>();
        Capture<String> targetObjectTypeCapture = new Capture<>();
        Capture<AcmFolder> dstFolderCapture = new Capture<>();
        EasyMock.expect(ecmFileService.moveFile(capture(fileIdCapture),
                capture(targetObjectIdCapture),
                capture(targetObjectTypeCapture),
                capture(dstFolderCapture))).andAnswer(new IAnswer<EcmFile>() {
            public EcmFile answer() throws Throwable {
                EcmFile movedFile = new EcmFile();
                //we will keep same id's
                movedFile.setFileId(fileIdCapture.getValue());
                movedFile.setFolder(dstFolderCapture.getValue());
                documentMap.put(movedFile.getId(), movedFile);
                return movedFile;
            }
        }).anyTimes();

        EasyMock.expect(acmFolderService.folderPathExists(EasyMock.anyObject(), EasyMock.anyObject())).andReturn(false).anyTimes();


        Capture<Long> findFileIdCapture = new Capture<>();
        EasyMock.expect(ecmFileService.findById(capture(findFileIdCapture))).andAnswer(new IAnswer<EcmFile>() {
            public EcmFile answer() throws Throwable {
                return documentMap.get(findFileIdCapture.getValue());
            }
        }).anyTimes();


        Capture<AcmFolder> sFolderCapture = new Capture<>();
        Capture<AcmFolder> tFolderCapture = new Capture<>();
        EasyMock.expect(acmFolderService.moveFolder(capture(sFolderCapture)
                , capture(tFolderCapture))).andAnswer(new IAnswer<AcmFolder>() {
            public AcmFolder answer() throws Throwable {
                sFolderCapture.getValue().setParentFolderId(tFolderCapture.getValue().getId());
                return sFolderCapture.getValue();
            }
        }).anyTimes();

        Capture<Long> objectIdCapture = new Capture<>();
        Capture<String> objectTypeCapture = new Capture<>();
        Capture<String> folderPathCapture = new Capture<>();

        EasyMock.expect(acmFolderService.addNewFolderByPath(capture(objectTypeCapture)
                , capture(objectIdCapture), capture(folderPathCapture))).andAnswer(new IAnswer<AcmFolder>() {
            public AcmFolder answer() throws Throwable {
                AcmFolder folder = new AcmFolder();
                switch (folderPathCapture.getValue()) {
                    case "":
                        folder = folderMap.get(200l);
                        break;
                    case "/F2/F4":
                        folder.setId(44l);
                        folder.setName("F4");
                        folder.setParentFolderId(22l);
                        //create parent as well

                        AcmFolder parentF2 = new AcmFolder();
                        parentF2.setId(22l);
                        parentF2.setName("F2");
                        parentF2.setParentFolderId(200l);
                        addToFolderMap(parentF2);
                        break;
                    case "/F2":
                        folder.setId(22l);
                        folder.setName("F2");
                        folder.setParentFolderId(200l);
                        break;
                    case "/F3":
                        folder.setId(33l);
                        folder.setName("F3");
                        folder.setParentFolderId(200l);
                        break;
                }
                addToFolderMap(folder);
                return folder;
            }
        }).anyTimes();

        Capture<AcmFolder> folderGetPathCapture = new Capture<>();


        EasyMock.expect(acmFolderService.getFolderPath(capture(folderGetPathCapture)
        )).andAnswer(new IAnswer<String>() {
            public String answer() throws Throwable {
                switch (folderGetPathCapture.getValue().getId().intValue()) {
                    case 2:
                        return "/F2";
                    case 3:
                        return "/F3";
                    case 4:
                        return "/F2/F4";
                    default:
                        return "";
                }

            }
        }).anyTimes();


        Capture<Long> findFolderByIdCapture = new Capture<>();
        EasyMock.expect(acmFolderService.findById(capture(findFolderByIdCapture)
        )).andAnswer(new IAnswer<AcmFolder>() {
            public AcmFolder answer() throws Throwable {
                return folderMap.get(findFolderByIdCapture.getValue());
            }
        }).anyTimes();


        AcmFolder someFolder = new AcmFolder();
        EasyMock.expect(acmFolderService.addNewFolder(1l, String.format("%s(%s)", "Source", "55435345435_2133"))).andReturn(someFolder);


        replayAll();

        SplitCaseOptions splitCaseOptions = new SplitCaseOptions();
        splitCaseOptions.setCaseFileId(sourceId);
        List<SplitCaseOptions.AttachmentDTO> attachments = new ArrayList<>();
        attachments.add(new SplitCaseOptions.AttachmentDTO(1l, "document"));
        attachments.add(new SplitCaseOptions.AttachmentDTO(2l, "document"));
        attachments.add(new SplitCaseOptions.AttachmentDTO(3l, "folder"));
        splitCaseOptions.setAttachments(attachments);
        splitCaseOptions.setPreserveFolderStructure(true);


        //before split
        /*
            ROOT
            |---F2 (folder)
            |   |---F4 (folder)
            |       |---d2 (document)
            |       |---d3 (document)
            |
            |---F3 (folder)
            |   |---d4 (document)
            |
            |---d1 (document)
         */
        splitCaseService.splitCase(auth, ipAddress, splitCaseOptions);
        //after split should look like this
        /*
            SOURCE
            ROOT
            |---F2 (folder)
            |   |---F4 (folder)
            |       |---d3 (document)
            |

            COPIED
            ROOT
            |---F2(copy - new id) (folder)
            |   |---F4(copy - new id) (folder)
            |       |---d2 (document)
            |
            |---F3 (folder)
            |   |---d4 (document)
            |
            |---d1 (document)
         */

        verifyDocuments();
        verifyFolders();
        if (splitCaseOptions.isPreserveFolderStructure())
            verifyFolderAndDocumentStructureIsPreserved();
    }

    private Long getNextGeneratedFolderId() {
        return generated += 10l;
    }

    private void verifyDocuments() {
        //verify that documents are located under correct ROOT folder

        //d1, d2, d4 should be moved to the copied case file
        EcmFile d1 = documentMap.get(1l);
        AcmFolder folder = d1.getFolder();
        while (folder.getParentFolderId() != null) {
            folder = folderMap.get(folder.getParentFolderId());
        }
        assertEquals("d1 should have ROOT " + copiedFolder.getId(), copiedFolder.getId(), folder.getId());

        //check if ROOT folder is same
        EcmFile d2 = documentMap.get(2l);
        folder = d2.getFolder();
        while (folder.getParentFolderId() != null) {
            log.debug("folder info {}", folder);
            folder = folderMap.get(folder.getParentFolderId());
        }
        assertEquals("d2 should have ROOT " + copiedFolder.getId(), copiedFolder.getId(), folder.getId());

        //check if ROOT folder is same
        EcmFile d4 = documentMap.get(4l);
        log.debug("checking file d4  {}", d4);
        folder = d4.getFolder();
        while (folder.getParentFolderId() != null) {
            log.debug("folder info {}", folder);
            folder = folderMap.get(folder.getParentFolderId());
        }
        assertEquals("d4 should have ROOT " + copiedFolder.getId(), copiedFolder.getId(), folder.getId());


        //d3 should stay at source case file
        EcmFile d3 = documentMap.get(3l);
        folder = d3.getFolder();
        while (folder.getParentFolderId() != null) {
            folder = folderMap.get(folder.getParentFolderId());
        }
        assertEquals("d3 should have ROOT " + sourceFolder.getId(), sourceFolder.getId(), folder.getId());

    }

    private void verifyFolders() {
        //verify that folders are located under correct ROOT folder

        //F2 and F4 should stay at source case file ROOT folder
        AcmFolder folder = folderMap.get(2l);
        while (folder.getParentFolderId() != null) {
            folder = folderMap.get(folder.getParentFolderId());
        }
        assertEquals("Folder F2 should have ROOT " + sourceFolder.getId(), sourceFolder.getId(), folder.getId());

        folder = folderMap.get(4l);
        while (folder.getParentFolderId() != null) {
            folder = folderMap.get(folder.getParentFolderId());
        }
        assertEquals("Folder F4 should have ROOT " + sourceFolder.getId(), sourceFolder.getId(), folder.getId());


        //F3 should be moved to copied case file ROOT folder
        folder = folderMap.get(3l);
        while (folder.getParentFolderId() != null) {
            folder = folderMap.get(folder.getParentFolderId());
        }
        assertEquals("Folder F3 should have ROOT " + copiedFolder.getId(), copiedFolder.getId(), folder.getId());
    }

    private void verifyFolderAndDocumentStructureIsPreserved() {
        //verify source case file folder and document structure
        EcmFile d3 = documentMap.get(3l);
        assertEquals(folderMap.get(4l).getId(), d3.getFolder().getId());
        AcmFolder f4 = folderMap.get(4l);
        AcmFolder f2 = folderMap.get(2l);
        assertEquals(f4.getParentFolderId(), f2.getId());
        assertEquals(f2.getParentFolderId(), sourceFolder.getId());

        //verify copied case file folder and document structure
        EcmFile d1 = documentMap.get(1l);
        assertEquals(d1.getFolder().getId(), copiedFolder.getId());

        EcmFile d4 = documentMap.get(4l);
        AcmFolder f3 = folderMap.get(3l);
        assertEquals(d4.getFolder().getId(), f3.getId());
        assertEquals(f3.getParentFolderId(), copiedFolder.getId());

        //verify created new folders to match like on source
        EcmFile d2 = documentMap.get(2l);
        AcmFolder f4Copy = folderMap.get(d2.getFolder().getId());
        assertEquals("F4", f4Copy.getName());
        assertNotEquals(f4Copy.getId(), f4.getId());
        AcmFolder f2Copy = folderMap.get(f4Copy.getParentFolderId());
        assertEquals("F2", f2Copy.getName());
        assertNotEquals(f2Copy.getId(), f2.getId());
        assertEquals(f2Copy.getParentFolderId(), copiedFolder.getId());

    }

    private void createSourceFolderStructure() {
        //Documents and folder structure
        /*
            ROOT
            |---F2 (folder)
            |   |---F4 (folder)
            |       |---d2 (document)
            |       |---d3 (document)
            |
            |---F3 (folder)
            |   |---d4 (document)
            |
            |---d1 (document)
         */

        //fill folder map for testing

        AcmFolder f2 = new AcmFolder();
        f2.setId(2l);
        f2.setName("F2");
        f2.setParentFolderId(sourceFolder.getId());
        addToFolderMap(f2);

        AcmFolder f3 = new AcmFolder();
        f3.setId(3l);
        f3.setName("F3");
        f3.setParentFolderId(sourceFolder.getId());
        addToFolderMap(f3);

        AcmFolder f4 = new AcmFolder();
        f4.setId(4l);
        f4.setName("F4");
        f4.setParentFolderId(2l);
        addToFolderMap(f4);

        //fill document Map
        EcmFile d1 = new EcmFile();
        d1.setFileId(1l);
        d1.setFileName("d1");
        d1.setFolder(sourceFolder);
        addToDocumentMap(d1);

        EcmFile d2 = new EcmFile();
        d2.setFileId(2l);
        d2.setFileName("d2");
        d2.setFolder(f4);
        addToDocumentMap(d2);

        EcmFile d3 = new EcmFile();
        d3.setFileId(3l);
        d3.setFileName("d3");
        d3.setFolder(f4);
        addToDocumentMap(d3);

        EcmFile d4 = new EcmFile();
        d4.setFileId(4l);
        d4.setFileName("d4");
        d4.setFolder(f3);
        addToDocumentMap(d4);
    }

    private void addToDocumentMap(EcmFile d) {
        documentMap.put(d.getFileId(), d);
    }

    private void addToFolderMap(AcmFolder f) {
        folderMap.put(f.getId(), f);
    }
}
