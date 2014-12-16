package com.armedia.acm.plugins.personnelsecurity.service;


import com.armedia.acm.correspondence.service.CorrespondenceService;
import com.armedia.acm.plugins.personnelsecurity.casestatus.service.CaseFileStateService;
import com.armedia.acm.plugins.personnelsecurity.cvs.service.ClearanceVerificationSystemExportService;
import com.armedia.acm.service.milestone.service.MilestoneService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/spring-library-personnel-security-activiti-test.xml" } )
public class BackgroundInvestigationBusinessProcessIT
{
    @Autowired
    private ProcessEngine pe;

    @Autowired
    private RepositoryService repo;

    @Autowired
    private RuntimeService rt;

    @Autowired
    private TaskService ts;

    @Autowired
    private HistoryService hs;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    @Qualifier(value = "milestoneService")
    private MilestoneService mockMilestoneService;

    @Autowired
    @Qualifier(value = "caseFileStateService")
    private CaseFileStateService caseFileStateService;

    @Autowired
    @Qualifier(value = "clearanceVerificationSystemExportService")
    private ClearanceVerificationSystemExportService clearanceVerificationSystemExportService;

    @Autowired
    @Qualifier(value = "correspondenceService")
    private CorrespondenceService correspondenceService;

    private Object[] mocks;

    private Long caseId = 12345L;
    private String caseNumber = "20140530_001";
    private String folderId = "folderId";
    private String subjectLastName = "Garcia";
    private String processName = "personnelSecurityBackgroundInvestigation";
    private String defaultAdjudicator = "ann-acm";
    Map<String, Object> pvars = new HashMap<>();

    @Before
    public void setUp() throws Exception
    {

        // deploy
        repo.createDeployment()
                .addClasspathResource("activiti/personnelSecurityBackgroundInvestigation_v9.bpmn20.xml")
                .deploy();

        mocks = new Object[] { mockMilestoneService, caseFileStateService, clearanceVerificationSystemExportService };

        pvars.put("OBJECT_TYPE", "CASE_FILE");
        pvars.put("OBJECT_ID", caseId);
        pvars.put("OBJECT_NAME", caseNumber);
        pvars.put("CASE_FILE", caseId);
        pvars.put("REQUEST_TYPE", "BACKGROUND_INVESTIGATION");
        pvars.put("REQUEST_ID", caseId);
        pvars.put("OBJECT_FOLDER_ID", folderId);
        pvars.put("SUBJECT_LAST_NAME", subjectLastName);

        EasyMock.reset(mockMilestoneService, caseFileStateService, clearanceVerificationSystemExportService, correspondenceService);
    }

    @After
    public void tearDown() throws Exception
    {
        pe.close();
    }

    @Test
    public void startProcess_processStart_happyPath() throws Exception
    {
        // these calls should happen when process is started
        mockMilestoneService.saveMilestone(caseId, "CASE_FILE", "Initiated");
        caseFileStateService.changeCaseFileState(caseId, "ACTIVE");

        // should happen when adjudication task is completed, no matter what the outcome
        mockMilestoneService.saveMilestone(caseId, "CASE_FILE", "Adjudication");
        clearanceVerificationSystemExportService.exportDeterminationRecord(
                defaultAdjudicator,
                caseId,
                caseNumber,
                folderId,
                subjectLastName,
                "GRANT_CLEARANCE");
        expect(correspondenceService.generate("ClearanceGranted.docx", "CASE_FILE", caseId, caseNumber, folderId)).andReturn(null);

        // should happen after clearance is issued
        mockMilestoneService.saveMilestone(caseId, "CASE_FILE", "Issued");

        // always happens at end of process
        mockMilestoneService.saveMilestone(caseId, "CASE_FILE", "Closed");
        caseFileStateService.changeCaseFileState(caseId, "CLOSED");

        replay(mocks);

        ProcessInstance pi = rt.startProcessInstanceByKey(processName, pvars);

        happyPath_verifyInitialUserTasks(pi);

        happyPath_completeInitialUserTasks(pi);

        happyPath_verifyAdjudicationTask(pi);

        happyPath_completeAdjudicationTask(pi, "GRANT_CLEARANCE");

        happyPath_verifyIssueClearanceTask(pi);

        happyPath_completeIssueClearanceTask(pi);

        verify(mocks);


    }

    @Test
    public void startProcess_processStart_denyClearance() throws Exception
    {
        // these calls should happen when process is started
        mockMilestoneService.saveMilestone(caseId, "CASE_FILE", "Initiated");
        caseFileStateService.changeCaseFileState(caseId, "ACTIVE");

        // should happen when adjudication task is completed, no matter what the outcome
        mockMilestoneService.saveMilestone(caseId, "CASE_FILE", "Adjudication");
        clearanceVerificationSystemExportService.exportDeterminationRecord(
                defaultAdjudicator,
                caseId,
                caseNumber,
                folderId,
                subjectLastName,
                "DENY_CLEARANCE");
        expect(correspondenceService.generate("ClearanceDenied.docx", "CASE_FILE", caseId, caseNumber, folderId)).andReturn(null);

        // always happens at end of process
        mockMilestoneService.saveMilestone(caseId, "CASE_FILE", "Closed");
        caseFileStateService.changeCaseFileState(caseId, "CLOSED");

        replay(mocks);

        ProcessInstance pi = rt.startProcessInstanceByKey(processName, pvars);

        happyPath_verifyInitialUserTasks(pi);

        happyPath_completeInitialUserTasks(pi);

        happyPath_verifyAdjudicationTask(pi);

        happyPath_completeAdjudicationTask(pi, "DENY_CLEARANCE");

        verify(mocks);


    }

    private void happyPath_completeIssueClearanceTask(ProcessInstance pi)
    {
        String caseNumberInQuotes = "'" + caseNumber + "'";
        completeTask("Issue Clearance " + caseNumberInQuotes, "issueClearanceOutcome", "COMPLETE");
    }

    private void happyPath_verifyIssueClearanceTask(ProcessInstance pi)
    {
        List<Task> userTasks = ts.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).list();

        int expectedTasks = 1;

        assertEquals(expectedTasks, userTasks.size());

        String caseNumberInQuotes = "'" + caseNumber + "'";

        String expectedName = "Issue Clearance " + caseNumberInQuotes;

        assertEquals(expectedName, userTasks.get(0).getName());
    }

    private void happyPath_completeAdjudicationTask(ProcessInstance pi, String outcomeValue)
    {
        String caseNumberInQuotes = "'" + caseNumber + "'";
        completeTask("Adjudicate Clearance Request " + caseNumberInQuotes, "adjudicationOutcome", outcomeValue);
    }

    /** should now have one user task to adjudicate the case
     *
     * @param pi
     */
    private void happyPath_verifyAdjudicationTask(ProcessInstance pi)
    {
        List<Task> userTasks = ts.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).list();

        int expectedTasks = 1;

        assertEquals(expectedTasks, userTasks.size());

        String caseNumberInQuotes = "'" + caseNumber + "'";

        String expectedName = "Adjudicate Clearance Request " + caseNumberInQuotes;

        assertEquals(expectedName, userTasks.get(0).getName());
    }

    private void happyPath_completeInitialUserTasks(ProcessInstance pi)
    {
        String caseNumberInQuotes = "'" + caseNumber + "'";
        completeTask("eVerify " + caseNumberInQuotes, "eVerifyOutcome", "COMPLETE");
        completeTask("Verify Address History " + caseNumberInQuotes, "verifyAddressOutcome", "COMPLETE");
        completeTask("Verify Employment History " + caseNumberInQuotes, "verifyEmploymentOutcome", "COMPLETE");
        completeTask("Verify SF86 Packet " + caseNumberInQuotes, "sf86Outcome", "COMPLETE");
    }

    private void completeTask(String taskName, String outcomeName, String outcomeValue)
    {
        Task task = ts.createTaskQuery().taskName(taskName).singleResult();
        assertNotNull(task);
        ts.setVariable(task.getId(), outcomeName, outcomeValue);
        ts.complete(task.getId());
    }

    private void happyPath_verifyInitialUserTasks(ProcessInstance pi)
    {
        List<Task> verifyTasks = ts.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).orderByTaskName().asc().list();

        int expectedVerifyTasks = 4;

        log.debug("Found " + verifyTasks.size() + " verify tasks.");

        String caseNumberInQuotes = "'" + caseNumber + "'";

        // note, activiti sorts task names in a case-sensitive way, so all caps come before all lowercase apparently.
        List<String> expectedNames = Arrays.asList(
                "Verify Address History " + caseNumberInQuotes,
                "Verify Employment History " + caseNumberInQuotes,
                "Verify SF86 Packet " + caseNumberInQuotes,
                "eVerify " + caseNumberInQuotes
        );

        assertEquals(expectedVerifyTasks, verifyTasks.size());

        List<String> foundTaskNames = new ArrayList<>();
        for ( Task task : verifyTasks )
        {
            foundTaskNames.add(task.getName());
        }

        assertEquals(expectedNames, foundTaskNames);
    }


}
