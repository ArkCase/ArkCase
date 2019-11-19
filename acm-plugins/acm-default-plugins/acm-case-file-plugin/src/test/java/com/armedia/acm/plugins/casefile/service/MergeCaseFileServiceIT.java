package com.armedia.acm.plugins.casefile.service;

/*-
 * #%L
 * ACM Default Plugin: Case File
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.armedia.acm.auth.AcmGrantedAuthority;
import com.armedia.acm.core.exceptions.AcmAccessControlException;
import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.exceptions.MergeCaseFilesException;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.model.CaseFileConfig;
import com.armedia.acm.plugins.casefile.model.MergeCaseOptions;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociation;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.participants.model.ParticipantTypes;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.web.api.MDCConstants;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mule.api.MuleException;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
import java.util.UUID;
import java.util.stream.Collectors;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(name = "spring", locations = {
        "/spring/spring-library-acm-encryption.xml",
        "/spring/spring-library-activiti-configuration.xml",
        "/spring/spring-library-admin.xml",
        "/spring/spring-library-audit-service.xml",
        "/spring/spring-library-authentication-token.xml",
        "/spring/spring-library-business-process.xml",
        "/spring/spring-library-calendar-config-service.xml",
        "/spring/spring-library-calendar-integration-exchange-service.xml",
        "/spring/spring-library-case-file-dao.xml",
        "/spring/spring-library-case-file-rules.xml",
        "/spring/spring-library-case-file-save.xml",
        "/spring/spring-library-case-file-split-merge.xml",
        "/spring/spring-library-case-file.xml",
        "/spring/spring-library-case-file-events.xml",
        "/spring/spring-library-case-file-queue-service.xml",
        "/spring/spring-library-context-holder.xml",
        "/spring/spring-library-data-access-control.xml",
        "/spring/spring-library-data-source.xml",
        "/spring/spring-library-drools-rule-monitor.xml",
        "/spring/spring-library-ecm-file.xml",
        "/spring/spring-library-ecm-file-lock.xml",
        "/spring/spring-library-ecm-tika.xml",
        "/spring/spring-library-email.xml",
        "/spring/spring-library-email-smtp.xml",
        "/spring/spring-library-event.xml",
        "/spring/spring-library-folder-watcher.xml",
        "/spring/spring-library-form-configurations.xml",
        "/spring/spring-library-forms-configuration.xml",
        "/spring/spring-library-merge-case-test-IT.xml",
        "/spring/spring-library-ms-outlook-integration.xml",
        "/spring/spring-library-ms-outlook-plugin.xml",
        "/spring/spring-library-note.xml",
        "/spring/spring-library-notification.xml",
        "/spring/spring-library-object-converter.xml",
        "/spring/spring-library-object-diff.xml",
        "/spring/spring-library-object-lock.xml",
        "/spring/spring-library-object-association-plugin.xml",
        "/spring/spring-library-object-history.xml",
        "/spring/spring-library-organization-rules.xml",
        "/spring/spring-library-particpants.xml",
        "/spring/spring-library-pdf-utilities.xml",
        "/spring/spring-library-person.xml",
        "/spring/spring-library-person-rules.xml",
        "/spring/spring-library-profile.xml",
        "/spring/spring-library-property-file-manager.xml",
        "/spring/spring-library-search.xml",
        "/spring/spring-library-service-data.xml",
        "/spring/spring-library-task.xml",
        "/spring/spring-library-user-service.xml",
        "/spring/spring-library-core-api.xml",
        "/spring/spring-library-user-login.xml",
        "/spring/spring-library-plugin-manager.xml",
        "/spring/spring-library-audit-service.xml",
        "/spring/spring-library-configuration.xml",
        "/spring/spring-library-acm-email.xml",
        "/spring/spring-library-convert-folder-service.xml",
        "/spring/spring-library-user-tracker.xml"
})
@TransactionConfiguration(defaultRollback = true)
public class MergeCaseFileServiceIT
{
    static
    {
        String userHomePath = System.getProperty("user.home");
        System.setProperty("acm.configurationserver.propertyfile", userHomePath + "/.arkcase/acm/conf.yml");
        System.setProperty("configuration.server.url", "http://localhost:9999");
    }

    private final Logger log = LogManager.getLogger(getClass());

    @Autowired
    private CaseFileDao caseFileDao;
    @Autowired
    private EcmFileService ecmFileService;
    @Autowired
    private AcmFolderService acmFolderService;

    @Autowired
    private CaseFileConfig caseConfig;

    @Autowired
    private EcmFileDao ecmFileDao;

    @Autowired
    private MergeCaseService mergeCaseService;

    @Autowired
    private SaveCaseService saveCaseService;

    @Autowired
    private AuditPropertyEntityAdapter auditAdapter;

    @PersistenceContext
    private EntityManager entityManager;

    private Long sourceId;
    private Long targetId;
    private Authentication auth;
    private String ipAddress;

    @Before
    public void setUp() throws Exception
    {
        MDC.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, "admin");
        MDC.put(MDCConstants.EVENT_MDC_REQUEST_ID_KEY, UUID.randomUUID().toString());
    }

    @Test
    @Transactional
    public void mergeCaseFilesTest() throws MergeCaseFilesException, MuleException, AcmUserActionFailedException,
            AcmCreateObjectFailedException, IOException, AcmObjectNotFoundException, PipelineProcessException, AcmAccessControlException
    {
        auditAdapter.setUserId("auditUser");

        String roleAdd = "ROLE_ADMINISTRATOR";
        AcmGrantedAuthority authority = new AcmGrantedAuthority(roleAdd);

        auth = new UsernamePasswordAuthenticationToken("ann-acm@armedia.com", "ann-acm", Arrays.asList(authority));
        ipAddress = "127.0.0.1";

        Resource dammyDocument = new ClassPathResource("/documents/textDammydocument.txt");
        assertTrue(dammyDocument.exists());

        assertNotNull(caseFileDao);
        assertNotNull(ecmFileService);
        assertNotNull(acmFolderService);
        assertNotNull(mergeCaseService);

        // create source case file
        CaseFile sourceCaseFile = new CaseFile();
        sourceCaseFile.setCaseType("caseType");
        sourceCaseFile.setTitle("title");

        CaseFile sourceSaved = saveCaseService.saveCase(sourceCaseFile, auth, ipAddress);
        sourceId = sourceSaved.getId();

        // create target case file
        CaseFile targetCaseFile = new CaseFile();
        targetCaseFile.setCaseType("caseType");
        targetCaseFile.setTitle("title");

        CaseFile targetSaved = saveCaseService.saveCase(targetCaseFile, auth, ipAddress);

        targetId = targetSaved.getId();

        // verify that case files are saved
        assertNotNull(sourceId);
        assertNotNull(targetId);

        // upload in root folder
        ecmFileService.upload("dammyDocument1.txt", "attachment", "Document", dammyDocument.getInputStream(), "text/plain",
                "dammyDocument1.txt", auth, sourceSaved.getContainer().getFolder().getCmisFolderId(),
                sourceSaved.getContainer().getContainerObjectType(), sourceSaved.getContainer().getContainerObjectId());

        // create folder and add document to this folder
        AcmFolder folderInSourceCase = acmFolderService.addNewFolder(sourceSaved.getContainer().getFolder().getId(), "some_folder");

        ecmFileService.upload("dammyDocument.txt", "attachment", "Document", dammyDocument.getInputStream(), "text/plain",
                "dammyDocument.txt", auth, folderInSourceCase.getCmisFolderId(), sourceSaved.getContainer().getContainerObjectType(),
                sourceSaved.getContainer().getContainerObjectId());
        MergeCaseOptions mergeCaseOptions = new MergeCaseOptions();
        mergeCaseOptions.setSourceCaseFileId(sourceId);
        mergeCaseOptions.setTargetCaseFileId(targetId);

        List<EcmFile> sourceFiles = ecmFileDao.findForContainer(sourceSaved.getContainer().getId());
        // the case file pipeline generates a PDF representation of the case file, so we start with one file,
        // and then we added two in this test.
        assertEquals(3, sourceFiles.size());

        List<Long> sourceIdBeforeMerge = sourceFiles.stream().map(EcmFile::getId).collect(Collectors.toList());

        List<EcmFile> targetFiles = ecmFileDao.findForContainer(targetSaved.getContainer().getId());
        // target case file also got a PDF file, from the pipeline.
        assertEquals(1, targetFiles.size());

        mergeCaseService.mergeCases(auth, ipAddress, mergeCaseOptions);

        sourceFiles = ecmFileDao.findForContainer(sourceSaved.getContainer().getId());
        if (!sourceFiles.isEmpty())
        {
            String excluded = caseConfig.getMergeExcludeDocumentTypes();
            List<String> excludedTypes = Arrays.asList(excluded.trim().replaceAll(",[\\s]*", ",").split(","));

            // any files left in the original case file must have a file type from
            // the exclude file types list
            for (EcmFile sourceFile : sourceFiles)
            {
                String found = excludedTypes.stream().filter(et -> et.equalsIgnoreCase(sourceFile.getFileType())).findFirst().orElse(null);
                assertNotNull("File remaining in source case has type [" + sourceFile.getFileType() + "] " +
                        "which is not in the list of excluded types [" + excluded + "]",
                        found);
            }
        }

        targetFiles = ecmFileDao.findForContainer(targetSaved.getContainer().getId());
        // any files that were kept in the source case should not have been moved to the target.
        // so, the target starts with 1, then it gets all files from the source, except for
        // the ones with excluded file types.
        assertEquals(1 + sourceIdBeforeMerge.size() - sourceFiles.size(), targetFiles.size());

        CaseFile sourceCase = caseFileDao.find(sourceId);
        CaseFile targetCase = caseFileDao.find(targetId);

        ObjectAssociation sourceOa = null;
        for (ObjectAssociation oa : sourceCase.getChildObjects())
        {
            if ("MERGED_TO".equals(oa.getCategory()))
                sourceOa = oa;
        }
        assertNotNull(sourceOa);
        assertNotNull(sourceOa.getTargetId());
        assertEquals(sourceOa.getTargetId().longValue(), targetCase.getId().longValue());

        ObjectAssociation targetOa = null;
        for (ObjectAssociation oa : targetCase.getChildObjects())
        {
            if ("MERGED_FROM".equals(oa.getCategory()))
                targetOa = oa;
        }
        assertNotNull(targetOa);
        assertNotNull(targetOa.getTargetId());
        assertEquals(targetOa.getTargetId().longValue(), sourceCase.getId().longValue());

        assertEquals(sourceCase.getContainer().getFolder().getParentFolder().getId(), targetCase.getContainer().getFolder().getId());

    }

    @Test
    @Transactional
    public void mergeCaseFilesParticipantSameAssigneeTest() throws MergeCaseFilesException, MuleException, AcmUserActionFailedException,
            AcmCreateObjectFailedException, IOException, AcmObjectNotFoundException, PipelineProcessException, AcmAccessControlException
    {
        auditAdapter.setUserId("auditUser");
        String roleAdd = "ROLE_ADMINISTRATOR";
        AcmGrantedAuthority authority = new AcmGrantedAuthority(roleAdd);

        auth = new UsernamePasswordAuthenticationToken("ann-acm@armedia.com", "ann-acm", Arrays.asList(authority));
        ipAddress = "127.0.0.1";

        assertNotNull(caseFileDao);
        assertNotNull(ecmFileService);
        assertNotNull(acmFolderService);
        assertNotNull(mergeCaseService);

        // create source case file
        CaseFile sourceCaseFile = new CaseFile();
        sourceCaseFile.setCaseType("caseType");
        sourceCaseFile.setTitle("title");

        CaseFile sourceSaved = saveCaseService.saveCase(sourceCaseFile, auth, ipAddress);
        sourceId = sourceSaved.getId();

        // create target case file
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

        entityManager.flush();

        targetId = targetSaved.getId();

        // verify that case files are saved
        assertNotNull(sourceId);
        assertNotNull(targetId);

        MergeCaseOptions mergeCaseOptions = new MergeCaseOptions();
        mergeCaseOptions.setSourceCaseFileId(sourceId);
        mergeCaseOptions.setTargetCaseFileId(targetId);

        mergeCaseService.mergeCases(auth, ipAddress, mergeCaseOptions);

        entityManager.flush();

        CaseFile targetCase = caseFileDao.find(targetId);
        AcmParticipant foundAssignee = null;
        for (AcmParticipant ap : targetCase.getParticipants())
        {
            if (ParticipantTypes.ASSIGNEE.equals(ap.getParticipantType()))
            {
                foundAssignee = ap;
                break;
            }
        }

        assertEquals(4, targetCase.getParticipants().size());

        assertNotNull(foundAssignee);
        assertEquals(auth.getName(), foundAssignee.getParticipantLdapId());

    }

    @Test
    @Transactional
    public void mergeCaseFilesParticipantDifferentAssigneeTest() throws MergeCaseFilesException, MuleException,
            AcmUserActionFailedException, AcmCreateObjectFailedException, IOException, AcmObjectNotFoundException, PipelineProcessException,
            AcmAccessControlException
    {
        auditAdapter.setUserId("auditUser");
        String roleAdd = "ROLE_ADMINISTRATOR";
        AcmGrantedAuthority authority = new AcmGrantedAuthority(roleAdd);

        auth = new UsernamePasswordAuthenticationToken("ann-acm@armedia.com", "ann-acm", Arrays.asList(authority));
        ipAddress = "127.0.0.1";

        assertNotNull(caseFileDao);
        assertNotNull(ecmFileService);
        assertNotNull(acmFolderService);
        assertNotNull(mergeCaseService);

        // create source case file
        CaseFile sourceCaseFile = new CaseFile();
        sourceCaseFile.setCaseType("caseType");
        sourceCaseFile.setTitle("title");

        CaseFile sourceSaved = saveCaseService.saveCase(sourceCaseFile, auth, ipAddress);
        sourceId = sourceSaved.getId();

        // create target case file
        CaseFile targetCaseFile = new CaseFile();
        targetCaseFile.setCaseType("caseType");
        targetCaseFile.setTitle("title");

        AcmParticipant assigneeParticipant = new AcmParticipant();
        assigneeParticipant.setParticipantLdapId("ian-acm@armedia.com");
        assigneeParticipant.setParticipantType(ParticipantTypes.ASSIGNEE);
        if (targetCaseFile.getParticipants() == null)
            targetCaseFile.setParticipants(new ArrayList<>());
        targetCaseFile.getParticipants().add(assigneeParticipant);

        CaseFile targetSaved = saveCaseService.saveCase(targetCaseFile, auth, ipAddress);

        entityManager.flush();

        targetId = targetSaved.getId();

        // verify that case files are saved
        assertNotNull(sourceId);
        assertNotNull(targetId);

        assertEquals(4, targetSaved.getParticipants().size());

        AcmParticipant foundAssignee = null;
        for (AcmParticipant ap : targetSaved.getParticipants())
        {
            if (ParticipantTypes.ASSIGNEE.equals(ap.getParticipantType()))
            {
                foundAssignee = ap;
                break;
            }
        }
        assertNotNull(foundAssignee);
        assertEquals("ian-acm@armedia.com", foundAssignee.getParticipantLdapId());

        // merge case files
        MergeCaseOptions mergeCaseOptions = new MergeCaseOptions();
        mergeCaseOptions.setSourceCaseFileId(sourceId);
        mergeCaseOptions.setTargetCaseFileId(targetId);

        mergeCaseService.mergeCases(auth, ipAddress, mergeCaseOptions);

        entityManager.flush();

        CaseFile targetCase = caseFileDao.find(targetId);

        assertEquals(5, targetCase.getParticipants().size());

        foundAssignee = null;
        for (AcmParticipant ap : targetCase.getParticipants())
        {
            if (ParticipantTypes.ASSIGNEE.equals(ap.getParticipantType()))
            {
                foundAssignee = ap;
                break;
            }
        }
        assertNotNull(foundAssignee);
        assertEquals(auth.getName(), foundAssignee.getParticipantLdapId());

        AcmParticipant foundPreviousAssignee = null;
        for (AcmParticipant ap : targetCase.getParticipants())
        {
            if (ParticipantTypes.FOLLOWER.equals(ap.getParticipantType()) && "ian-acm@armedia.com".equals(ap.getParticipantLdapId()))
            {
                foundPreviousAssignee = ap;
                break;
            }
        }
        assertNotNull(foundPreviousAssignee);
    }
}
