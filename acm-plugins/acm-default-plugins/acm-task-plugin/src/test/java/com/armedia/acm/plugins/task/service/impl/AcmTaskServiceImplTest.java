package com.armedia.acm.plugins.task.service.impl;

/*-
 * #%L
 * ACM Default Plugin: Tasks
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

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.data.service.AcmDataService;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmContainerEntity;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.ecm.service.impl.EcmFileParticipantService;
import com.armedia.acm.plugins.task.exception.AcmTaskException;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.service.TaskDao;
import com.armedia.acm.services.participants.model.AcmParticipant;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by vladimir.radeski on 11/22/2017.
 */
public class AcmTaskServiceImplTest extends EasyMockSupport
{
    private AcmTaskServiceImpl acmTaskService;
    private TaskDao mockTaskDao;
    private Authentication mockAuthentication;
    private EcmFileService mockEcmFileService;
    private AcmContainer mockAcmContainer;
    private AcmDataService mockAcmDataService;
    private AcmAbstractDao<AcmObject> mockAcmAbstractDao;
    private AcmFolder mockAcmParentFolder;
    private AcmFolder mockAcmFolder;
    private AcmFolderService mockAcmFolderService;
    private AcmFolder mockAcmDocUnderReviewFolder;
    private EcmFileParticipantService mockEcmFileParticipantService;


    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    @Before
    public void setUp() throws Exception
    {
        acmTaskService = new AcmTaskServiceImpl();
        mockTaskDao = createMock(TaskDao.class);
        mockAuthentication = createMock(Authentication.class);
        mockEcmFileService = createMock(EcmFileService.class);
        mockAcmDataService = createMock(AcmDataService.class);
        mockAcmContainer = createMock(AcmContainer.class);
        mockAcmAbstractDao = createMock(AcmAbstractDao.class);
        mockAcmParentFolder = createMock(AcmFolder.class);
        mockAcmFolder = createMock(AcmFolder.class);
        mockAcmFolderService = createMock(AcmFolderService.class);
        mockAcmDocUnderReviewFolder = createMock(AcmFolder.class);
        mockEcmFileParticipantService = createMock(EcmFileParticipantService.class);

        acmTaskService.setTaskDao(mockTaskDao);
        acmTaskService.setEcmFileService(mockEcmFileService);
        acmTaskService.setAcmDataService(mockAcmDataService);
        acmTaskService.setAcmFolderService(mockAcmFolderService);
        acmTaskService.setFileParticipantService(mockEcmFileParticipantService);

    }

    @Test
    public void startReviewDocumentsWorkflow() throws Exception
    {
        String businessProcessName = "acmDocumentWorkflow";

        String attachedToObjectType = "COMPLAINT";
        String attachedToObjectName = "20140827_202";
        Long attachedToObjectId = 500l;

        ArrayList<EcmFile> documentsToReview = new ArrayList<>();
        EcmFile documentToReview = new EcmFile();
        documentToReview.setFileId(500l);
        documentToReview.setFileName("Test File");
        documentsToReview.add(documentToReview);

        EcmFile documentToReviewLink = new EcmFile();
        documentToReviewLink.setFileId(500l);
        documentToReviewLink.setFileName("Test File");
        documentToReviewLink.setLink(true);

        AcmTask reviewTask = new AcmTask();
        reviewTask.setAssignee("assignee");
        reviewTask.setAttachedToObjectType(attachedToObjectType);
        reviewTask.setTaskStartDate(new Date());
        reviewTask.setTitle("title");
        reviewTask.setAttachedToObjectName(attachedToObjectName);
        reviewTask.setAttachedToObjectId(attachedToObjectId);
        reviewTask.setDocumentsToReview(documentsToReview);
        reviewTask.setDueDate(new Date());
        reviewTask.setPercentComplete(0);
        reviewTask.setTaskId(501L);
        List<String> candidateGroup = new ArrayList<>();
        candidateGroup.add("Test Group");
        reviewTask.setCandidateGroups(candidateGroup);
        reviewTask.setDetails("Details");
        reviewTask.setParentObjectType("COMPLAINT");
        reviewTask.setParentObjectId(500L);
        reviewTask.setRestricted(false);
        String taskFolderName = "Task-" + reviewTask.getTitle() + "-" + reviewTask.getId();

        Long parentFolderId = 100L;
        Long destinationFolderId = 1500L;
        Long docUnderReviewFolderId = 2000L;

        AcmParticipant acmParticipant = new AcmParticipant();
        acmParticipant.setId(222L);
        acmParticipant.setObjectType("objectType");
        acmParticipant.setObjectId(223L);
        acmParticipant.setParticipantType("participantType");
        acmParticipant.setParticipantLdapId("ldapType");

        List<AcmParticipant> acmParticipants = new ArrayList<>();
        acmParticipants.add(acmParticipant);
        reviewTask.setParticipants(acmParticipants);

        AcmFolder originalFolder = new AcmFolder();
        originalFolder.setId(1000L);
        originalFolder.setObjectType("FOLDER");
        originalFolder.setName("Attachments");

        AcmContainer taskContainer = new AcmContainer();
        taskContainer.setFolder(originalFolder);

        reviewTask.setContainer(taskContainer);

        Map<String, Object> pVars = new HashMap<>();
        List<String> reviewers = new ArrayList<>();
        reviewers.add(reviewTask.getAssignee());
        pVars.put("reviewers", reviewers);
        pVars.put("assignee", reviewTask.getAssignee());
        pVars.put("taskName", "title");
        pVars.put("documentAuthor", "assignee");
        pVars.put("pdfRenditionId", 500l);
        pVars.put("formXmlId", null);
        pVars.put("dueDate", reviewTask.getDueDate());
        pVars.put("taskStartDate", reviewTask.getTaskStartDate());
        pVars.put("candidateGroups", "Test Group");
        pVars.put("OBJECT_TYPE", "FILE");
        pVars.put("OBJECT_ID", 500l);
        pVars.put("OBJECT_NAME", "Test File");
        pVars.put("PARENT_OBJECT_TYPE", attachedToObjectType);
        pVars.put("PARENT_OBJECT_ID", attachedToObjectId);
        pVars.put("REQUEST_TYPE", "DOCUMENT_REVIEW");
        pVars.put("DETAILS", "Details");
        pVars.put("PARENT_OBJECT_NAME", null);
        pVars.put("PARENT_OBJECT_TITLE", null);


        String documentsUnderReviewFolderName = "Documents Under Review";

        expect(mockAuthentication.getName()).andReturn("assignee").atLeastOnce();
        expect(mockTaskDao.startBusinessProcess(pVars, businessProcessName)).andReturn(reviewTask);

        expect(mockAcmContainer.getFolder()).andReturn(mockAcmParentFolder);
        expect(mockAcmParentFolder.getId()).andReturn(parentFolderId);

        expect(mockAcmDataService.getDaoByObjectType(reviewTask.getParentObjectType())).andReturn(mockAcmAbstractDao);
        expect(mockAcmAbstractDao.find(reviewTask.getParentObjectId())).andReturn(new ContainerEntity());
        expect(mockAcmFolderService.addNewFolder(parentFolderId, taskFolderName, reviewTask.getParentObjectId(),
                reviewTask.getParentObjectType())).andReturn(mockAcmFolder);

        expect(mockAcmFolderService.copyFolderAsLink(originalFolder, mockAcmFolder, originalFolder.getId(), originalFolder.getObjectType(), originalFolder.getName())).andReturn(originalFolder);
        expect(mockAcmFolder.getId()).andReturn(destinationFolderId);
        expect(mockAcmFolderService.addNewFolder(destinationFolderId, documentsUnderReviewFolderName, reviewTask.getParentObjectId(), reviewTask.getParentObjectType())).andReturn(mockAcmDocUnderReviewFolder);
        expect(mockAcmDocUnderReviewFolder.getId()).andReturn(docUnderReviewFolderId);

        expect(mockEcmFileService.copyFileAsLink(documentToReview.getFileId(), reviewTask.getParentObjectId(), reviewTask.getParentObjectType(), docUnderReviewFolderId)).andReturn(documentToReviewLink);

        replayAll();

        List<AcmTask> createdAcmTasks = acmTaskService.startReviewDocumentsWorkflow(reviewTask, businessProcessName, mockAuthentication);

        verifyAll();

        assertNotNull(createdAcmTasks);
        assertEquals(createdAcmTasks.get(0).getDocumentsToReview().get(0).getFileId(), documentToReview.getFileId());
    }

    private class ContainerEntity implements AcmContainerEntity, AcmObject {

        AcmContainer acmContainer;

        @Override
        public AcmContainer getContainer() {
            return mockAcmContainer;
        }

        @Override
        public void setContainer(AcmContainer container) {
            this.acmContainer = mockAcmContainer;
        }

        @Override
        public String getObjectType() {
            return "COMPLAINT";
        }

        @Override
        public Long getId() {
            return 500L;
        }
    }

    @Test
    public void startReviewDocumentsWorkflow_exception() throws Exception
    {
        String businessProcessName = "acmDocumentWorkflow";

        String attachedToObjectType = "COMPLAINT";
        String attachedToObjectName = "20140827_202";
        Long attachedToObjectId = 500l;

        AcmTask reviewTask = new AcmTask();
        reviewTask.setAssignee("assignee");
        reviewTask.setAttachedToObjectType(attachedToObjectType);
        reviewTask.setTaskStartDate(new Date());
        reviewTask.setTitle("title");
        reviewTask.setAttachedToObjectName(attachedToObjectName);
        reviewTask.setAttachedToObjectId(attachedToObjectId);
        reviewTask.setDueDate(new Date());
        reviewTask.setPercentComplete(0);
        List<String> candidateGroup = new ArrayList<>();
        candidateGroup.add("Test Group");
        reviewTask.setCandidateGroups(candidateGroup);

        Map<String, Object> pVars = new HashMap<>();
        List<String> reviewers = new ArrayList<>();
        reviewers.add(reviewTask.getAssignee());
        pVars.put("reviewers", reviewers);
        pVars.put("taskName", "title");
        pVars.put("documentAuthor", "assignee");
        pVars.put("pdfRenditionId", 500l);
        pVars.put("formXmlId", null);
        pVars.put("candidateGroups", "Test Group");
        pVars.put("OBJECT_TYPE", "FILE");
        pVars.put("OBJECT_ID", 500l);
        pVars.put("OBJECT_NAME", "Test File");
        pVars.put("PARENT_OBJECT_TYPE", "COMPLAINT");
        pVars.put("PARENT_OBJECT_ID", 500l);

        replayAll();
        Exception exception = null;
        try
        {
            acmTaskService.startReviewDocumentsWorkflow(reviewTask, businessProcessName, mockAuthentication);
        }
        catch (AcmTaskException e)
        {
            exception = e;
        }

        verifyAll();

        assertNotNull(exception);
        assertEquals(exception.getMessage(), "You must select at least one document to be reviewed.");
    }
}
