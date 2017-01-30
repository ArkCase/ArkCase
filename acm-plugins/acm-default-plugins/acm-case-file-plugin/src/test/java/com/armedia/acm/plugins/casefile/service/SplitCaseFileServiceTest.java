package com.armedia.acm.plugins.casefile.service;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
import com.armedia.acm.plugins.task.service.AcmTaskService;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;

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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/spring-library-split-case-test.xml" })
@TransactionConfiguration(defaultRollback = true)
public class SplitCaseFileServiceTest extends EasyMockSupport
{

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private SplitCaseServiceImpl splitCaseService;

    private SaveCaseService saveCaseService;
    private CaseFileDao caseFileDao;
    private AcmFolderService acmFolderService;
    private AcmTaskService acmTaskService;
    private SplitCaseFileBusinessRule mockSplitCaseFileBusinessRule;

    private Authentication auth;
    AcmFolder sourceFolder;
    private String ipAddress;
    private Long sourceId;
    AcmFolder copiedFolder;
    private Map<Long, AcmFolder> folderMap = new HashMap<>();
    private Map<Long, EcmFile> documentMap = new HashMap<>();

    @Before
    public void setUp()
    {
        auth = createMock(Authentication.class);
        ipAddress = "127.0.0.1";

        sourceId = 1L;
        // create mocks
        saveCaseService = createMock(SaveCaseService.class);
        caseFileDao = createMock(CaseFileDao.class);
        acmFolderService = createMock(AcmFolderService.class);
        acmTaskService = createMock(AcmTaskService.class);
        mockSplitCaseFileBusinessRule = createMock(SplitCaseFileBusinessRule.class);

        sourceFolder = new AcmFolder();
        sourceFolder.setId(100l);
        sourceFolder.setName("ROOT");
        addToFolderMap(sourceFolder);

        splitCaseService.setSaveCaseService(saveCaseService);
        splitCaseService.setCaseFileDao(caseFileDao);
        splitCaseService.setAcmFolderService(acmFolderService);
        splitCaseService.setAcmTaskService(acmTaskService);
        splitCaseService.setSplitCaseFileBusinessRule(mockSplitCaseFileBusinessRule);
        createSourceFolderStructure();
    }

    @Test
    public void splitCaseTest() throws MergeCaseFilesException, MuleException, AcmUserActionFailedException, AcmCreateObjectFailedException,
            IOException, SplitCaseFileException, AcmFolderException, AcmObjectNotFoundException, PipelineProcessException
    {
        assertNotNull(splitCaseService);

        CaseFile sourceCaseFile = new CaseFile();
        sourceCaseFile.setId(sourceId);
        sourceCaseFile.setCaseNumber("55435345435_2133");
        AcmContainer sourceContainer = new AcmContainer();
        sourceContainer.setFolder(sourceFolder);
        sourceCaseFile.setContainer(sourceContainer);
        sourceCaseFile.setTitle("Source");
        sourceCaseFile.setDetails("Source Details");

        EasyMock.expect(auth.getName()).andReturn("ann-acm").anyTimes();

        Capture<CaseFile> caseFileCapture = new Capture<>();
        Capture<Map<String, CaseFile>> toSplitCaseRulesCapture = new Capture<>();

        EasyMock.expect(caseFileDao.find(sourceId)).andReturn(sourceCaseFile).anyTimes();
        EasyMock.expect(mockSplitCaseFileBusinessRule.applyRules(capture(toSplitCaseRulesCapture))).andReturn(null);
        EasyMock.expect(saveCaseService.saveCase(capture(caseFileCapture), eq(auth), eq(ipAddress))).andAnswer(new IAnswer<CaseFile>()
        {
            @Override
            public CaseFile answer() throws Throwable
            {
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

        Capture<Long> folderIdCapture = new Capture<>();
        Capture<AcmContainer> tContainerCapture = new Capture<>();
        Capture<AcmFolder> tFolderCapture = new Capture<>();
        acmFolderService.copyFolderStructure(capture(folderIdCapture), capture(tContainerCapture), capture(tFolderCapture));
        expectLastCall().andAnswer(new IAnswer<Object>()
        {
            @Override
            public Object answer() throws Throwable
            {
                log.info("folder Id = {}, tContainer = {}, tFolder = {}", folderIdCapture.getValue().toString(),
                        tContainerCapture.getValue(), tFolderCapture.getValue());
                return null; // required to be null for a void method
            }
        }).anyTimes();

        Capture<Long> documentIdCapture = new Capture<>();
        Capture<AcmContainer> ttContainerCapture = new Capture<>();
        Capture<AcmFolder> ttFolderCapture = new Capture<>();
        acmFolderService.copyDocumentStructure(capture(documentIdCapture), capture(ttContainerCapture), capture(ttFolderCapture));
        expectLastCall().andAnswer(new IAnswer<Object>()
        {
            @Override
            public Object answer() throws Throwable
            {
                log.info("documentId = {}, ttContainer = {}, ttFolder = {}", documentIdCapture.getValue().toString(),
                        ttContainerCapture.getValue(), ttFolderCapture.getValue());
                return null; // required to be null for a void method
            }
        }).anyTimes();

        replayAll();

        SplitCaseOptions splitCaseOptions = new SplitCaseOptions();
        splitCaseOptions.setCaseFileId(sourceId);
        List<SplitCaseOptions.AttachmentDTO> attachments = new ArrayList<>();
        attachments.add(new SplitCaseOptions.AttachmentDTO(1l, "document"));
        attachments.add(new SplitCaseOptions.AttachmentDTO(2l, "document"));
        attachments.add(new SplitCaseOptions.AttachmentDTO(3l, "folder"));
        splitCaseOptions.setAttachments(attachments);
        splitCaseOptions.setPreserveFolderStructure(true);

        // file structure
        /*
         * ROOT |---F2 (folder) | |---F4 (folder) | |---d2 (document) | |---d3 (document) | |---F3 (folder) | |---d4
         * (document) | |---d1 (document)
         */
        splitCaseService.splitCase(auth, ipAddress, splitCaseOptions);

        assertEquals(200l, tFolderCapture.getValue().getId().longValue());
        assertEquals(200l, ttFolderCapture.getValue().getId().longValue());
        verifyAll();

        Map<String, CaseFile> sentToSplitCaseRule = toSplitCaseRulesCapture.getValue();
        assertEquals(sourceCaseFile.getTitle(), sentToSplitCaseRule.get("source").getTitle());
        assertNotNull(sentToSplitCaseRule.get("copy"));

    }

    private void createSourceFolderStructure()
    {
        // Documents and folder structure
        /*
         * ROOT |---F2 (folder) | |---F4 (folder) | |---d2 (document) | |---d3 (document) | |---F3 (folder) | |---d4
         * (document) | |---d1 (document)
         */

        // fill folder map for testing

        AcmFolder f2 = new AcmFolder();
        f2.setId(2l);
        f2.setName("F2");
        f2.setParentFolder(sourceFolder);
        addToFolderMap(f2);

        AcmFolder f3 = new AcmFolder();
        f3.setId(3l);
        f3.setName("F3");
        f3.setParentFolder(sourceFolder);
        addToFolderMap(f3);

        AcmFolder f4 = new AcmFolder();
        f4.setId(4l);
        f4.setName("F4");
        f4.setParentFolder(sourceFolder);
        addToFolderMap(f4);

        // fill document Map
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

    private void addToDocumentMap(EcmFile d)
    {
        documentMap.put(d.getFileId(), d);
    }

    private void addToFolderMap(AcmFolder f)
    {
        folderMap.put(f.getId(), f);
    }
}
