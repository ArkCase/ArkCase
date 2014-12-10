package com.armedia.acm.plugins.personnelsecurity.service;


import com.armedia.acm.plugins.personnelsecurity.casestatus.service.CaseFileStateService;
import com.armedia.acm.service.milestone.service.MilestoneService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
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

    private Object[] mocks;

    @Before
    public void setUp() throws Exception
    {

        // deploy
        repo.createDeployment()
                .addClasspathResource("activiti/personnelSecurityBackgroundInvestigation_v5.bpmn20.xml")
                .deploy();

        mocks = new Object[] { mockMilestoneService, caseFileStateService };

    }

    @After
    public void shutDown() throws Exception
    {
        pe.close();
    }

    @Test
    public void startProcess_processStart_create4TasksSetCaseStatusAndCallMilestoneService() throws Exception
    {
        Long caseId = 12345L;
        String caseNumber = "20140530_001";
        String folderId = "folderId";
        String subjectLastName = "Garcia";
        String processName = "personnelSecurityBackgroundInvestigation";

        int expectedVerifyTasks = 4;

        Map<String, Object> pvars = new HashMap<>();

        pvars.put("OBJECT_TYPE", "CASE_FILE");
        pvars.put("OBJECT_ID", caseId);
        pvars.put("OBJECT_NAME", caseNumber);
        pvars.put("CASE_FILE", caseId);
        pvars.put("REQUEST_TYPE", "BACKGROUND_INVESTIGATION");
        pvars.put("REQUEST_ID", caseId);
        pvars.put("OBJECT_FOLDER_ID", folderId);
        pvars.put("SUBJECT_LAST_NAME", subjectLastName);

        log.debug("starting process: " + processName);

        assertNotNull(mockMilestoneService);
        log.debug("type: " + mockMilestoneService.getClass().getName());

        mockMilestoneService.saveMilestone(caseId, "CASE_FILE", "Initiated");
        caseFileStateService.changeCaseFileState(caseId, "ACTIVE");

        replay(mocks);

        ProcessInstance pi = rt.startProcessInstanceByKey(processName, pvars);

        verify(mocks);

        List<Task> verifyTasks = ts.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).list();

        log.debug("Found " + verifyTasks.size() + " verify tasks.");

        assertEquals(expectedVerifyTasks, verifyTasks.size());
    }



}
