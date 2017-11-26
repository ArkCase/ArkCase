package com.armedia.acm.plugins.task.service.impl;

import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.task.exception.AcmTaskException;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.service.AcmTaskService;
import com.armedia.acm.plugins.task.service.TaskDao;
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
import static org.junit.Assert.*;

/**
 * Created by vladimir.radeski on 11/22/2017.
 */
public class AcmTaskServiceImplTest extends EasyMockSupport
{
    private AcmTaskServiceImpl acmTaskService;
    private TaskDao mockTaskDao;
    private Authentication mockAuthentication;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    @Before
    public void setUp() throws Exception
    {
        acmTaskService = new AcmTaskServiceImpl();
        mockTaskDao = createMock(TaskDao.class);
        mockAuthentication = createMock(Authentication.class);
        acmTaskService.setTaskDao(mockTaskDao);
    }

    @Test
    public void startReviewDocumentsWorkflow () throws Exception
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

        expect(mockAuthentication.getName()).andReturn("assignee").atLeastOnce();
        expect(mockTaskDao.startBusinessProcess(pVars, businessProcessName)).andReturn(reviewTask);

        replayAll();
        List<AcmTask> createdAcmTasks = acmTaskService.startReviewDocumentsWorkflow(reviewTask, businessProcessName, mockAuthentication);
        verifyAll();

        assertNotNull(createdAcmTasks);
        assertEquals(createdAcmTasks.get(0).getDocumentsToReview().get(0).getFileId(), documentToReview.getFileId());
    }

    @Test
    public void startReviewDocumentsWorkflow_exception () throws Exception
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
        try{
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
