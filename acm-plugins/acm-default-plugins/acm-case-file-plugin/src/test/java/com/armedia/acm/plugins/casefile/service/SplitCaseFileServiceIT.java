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
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociation;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.participants.model.ParticipantTypes;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)

@ContextConfiguration(name = "spring",
        locations = {
                "/spring/spring-library-activiti-configuration.xml",
                "/spring/spring-library-activiti-actions.xml",
                "/spring/spring-mule-activemq.xml",
                "/spring/spring-library-case-file.xml",
                "/spring/spring-library-data-source.xml",
                "/spring/spring-library-ecm-file.xml",
                "/spring/spring-library-user-service.xml",
                "/spring/spring-library-context-holder.xml",
                "/spring/spring-library-search.xml",
                "/spring/spring-library-data-access-control.xml",
                "/spring/spring-library-folder-watcher.xml",
                "/spring/spring-library-merge-case-test-IT.xml",
                "/spring/spring-library-ms-outlook-integration.xml",
                "/spring/spring-library-ms-outlook-plugin.xml",
                "/spring/spring-library-object-history.xml",
                "/spring/spring-library-particpants.xml",
                "/spring/spring-library-person.xml",
                "/spring/spring-library-property-file-manager.xml",
                "/spring/spring-library-profile.xml",
                "/spring/spring-library-acm-encryption.xml",
                "/spring/spring-library-task.xml",
                "/spring/spring-library-note.xml",
                "/spring/spring-library-event.xml",
                "/spring/test-case-file-context.xml",
                "/spring/spring-library-authentication-token.xml",
                "/spring/spring-library-business-process.xml"
        }
)

@TransactionConfiguration(defaultRollback = true)
public class SplitCaseFileServiceIT extends EasyMock
{

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

    @Autowired
    private AuditPropertyEntityAdapter auditAdapter;

    private Authentication auth;
    private String ipAddress;
    private Long sourceId;

    @Test
    @Transactional
    public void splitCaseTest() throws MergeCaseFilesException, MuleException, AcmUserActionFailedException, AcmCreateObjectFailedException, IOException, SplitCaseFileException, AcmFolderException, AcmObjectNotFoundException, PipelineProcessException
    {
        auditAdapter.setUserId("auditUser");
        String roleAdd = "ROLE_ADMINISTRATOR";
        AcmGrantedAuthority authority = new AcmGrantedAuthority(roleAdd);

        auth = new UsernamePasswordAuthenticationToken("ann-acm", "AcMd3v$", Arrays.asList(authority));

        ipAddress = "127.0.0.1";


        Resource dammyDocument = new ClassPathResource("/documents/textDammydocument.txt");
        Resource dammyDocument1 = new ClassPathResource("/documents/textDammydocument1.txt");
        Resource dammyDocument2 = new ClassPathResource("/documents/textDammydocument2.txt");
        assertTrue(dammyDocument.exists());
        assertTrue(dammyDocument1.exists());
        assertTrue(dammyDocument2.exists());

        assertNotNull(caseFileDao);
        assertNotNull(ecmFileService);
        assertNotNull(acmFolderService);
        assertNotNull(splitCaseService);

        //create source case file
        CaseFile sourceCaseFile = new CaseFile();
        sourceCaseFile.setCaseType("caseType");
        sourceCaseFile.setTitle("title");

        CaseFile sourceSaved = saveCaseService.saveCase(sourceCaseFile, auth, ipAddress);
        sourceId = sourceSaved.getId();


        //verify that case files are saved
        assertNotNull(sourceId);


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

        //create folder and add folder to this folder
        AcmFolder folderInSourceCase = acmFolderService.addNewFolder(sourceSaved.getContainer().getFolder(), "Folder");

        //create folder and add document to this folder
        AcmFolder folderInSourceCase1 = acmFolderService.addNewFolder(folderInSourceCase, "Folder1");

        EcmFile file = ecmFileService.upload("dammyDocument.txt",
                "attachment",
                "Document",
                dammyDocument.getInputStream(),
                "text/plain",
                "dammyDocument.txt",
                auth,
                folderInSourceCase1.getCmisFolderId(),
                sourceSaved.getContainer().getContainerObjectType(),
                sourceSaved.getContainer().getContainerObjectId());

        EcmFile file1 = ecmFileService.upload("dammyDocument1.txt",
                "attachment",
                "Document",
                dammyDocument1.getInputStream(),
                "text/plain",
                "dammyDocument1.txt",
                auth,
                folderInSourceCase1.getCmisFolderId(),
                sourceSaved.getContainer().getContainerObjectType(),
                sourceSaved.getContainer().getContainerObjectId());


        //create folder and add document to this folder
        AcmFolder folderInSourceCase2 = acmFolderService.addNewFolder(sourceSaved.getContainer().getFolder(), "Folder2");

        EcmFile file2 = ecmFileService.upload("dammyDocument2.txt",
                "attachment",
                "Document",
                dammyDocument2.getInputStream(),
                "text/plain",
                "dammyDocument1.txt",
                auth,
                folderInSourceCase2.getCmisFolderId(),
                sourceSaved.getContainer().getContainerObjectType(),
                sourceSaved.getContainer().getContainerObjectId());


        //set split options
        SplitCaseOptions splitCaseOptions = new SplitCaseOptions();
        splitCaseOptions.setCaseFileId(sourceId);
        List<SplitCaseOptions.AttachmentDTO> attachments = new ArrayList<>();
        attachments.add(new SplitCaseOptions.AttachmentDTO(file1.getId(), "document"));
        attachments.add(new SplitCaseOptions.AttachmentDTO(folderInSourceCase2.getId(), "folder"));
        splitCaseOptions.setAttachments(attachments);
        splitCaseOptions.setPreserveFolderStructure(true);

        CaseFile copyCaseFile = splitCaseService.splitCase(auth, ipAddress, splitCaseOptions);

        CaseFile originalCase = caseFileDao.find(sourceId);

        ObjectAssociation sourceOa = null;
        for (ObjectAssociation oa : originalCase.getChildObjects())
        {
            if ("COPY_TO".equals(oa.getCategory()))
                sourceOa = oa;
        }
        assertNotNull(sourceOa);
        assertNotNull(sourceOa.getTargetId());
        assertEquals(sourceOa.getTargetId().longValue(), copyCaseFile.getId().longValue());

        ObjectAssociation copyOa = null;
        for (ObjectAssociation oa : copyCaseFile.getChildObjects())
        {
            if ("COPY_FROM".equals(oa.getCategory()))
                copyOa = oa;
        }
        assertNotNull(copyOa);
        assertNotNull(copyOa.getTargetId());
        assertEquals(copyOa.getTargetId().longValue(), originalCase.getId().longValue());
        assertTrue(copyCaseFile.getParticipants().size() >= 3);
        AcmParticipant assignee = null;
        for (AcmParticipant ap : copyCaseFile.getParticipants())
        {
            if (ParticipantTypes.ASSIGNEE.equals(ap.getParticipantType()))
            {
                assignee = ap;
                break;
            }
        }
        assertNotNull(assignee);
        assertEquals(auth.getName(), assignee.getParticipantLdapId());
        assertEquals(copyCaseFile.getObjectType(), assignee.getObjectType());
        assertEquals(copyCaseFile.getId(), assignee.getObjectId());

    }

}
