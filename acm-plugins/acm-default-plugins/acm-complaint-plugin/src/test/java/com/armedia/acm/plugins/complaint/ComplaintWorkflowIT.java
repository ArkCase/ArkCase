package com.armedia.acm.plugins.complaint;

/*-
 * #%L
 * ACM Default Plugin: Complaints
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ExecutionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/spring-library-complaint-activiti-test.xml" })
public class ComplaintWorkflowIT
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

    private Logger log = LogManager.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {

        // deploy
        repo.createDeployment()
                .addClasspathResource("activiti/DefaultComplaintWorkflow_v5.bpmn20.xml")
                .deploy();

    }

    @After
    public void shutDown() throws Exception
    {
        pe.close();
    }

    /**
     * Associate a workflow with a business object by setting a process variable. Find workflows for that
     * business object and complete tasks associated with it.
     * 
     * @throws Exception
     */
    @Test
    public void startAndCompleteTask() throws Exception
    {
        Long complaintId = 12345L;
        Long badId = 54321L;
        String user = "Test User";
        String user2 = "Another User";
        String complaintNumber = "20140530_001";
        String complaintTitle = "complaintTitle";

        List<String> approvers = Arrays.asList(user, user2);
        Map<String, Object> processVariables = new HashMap<>();
        processVariables.put("approvers", approvers);
        processVariables.put("complaintNumber", complaintNumber);
        processVariables.put("complaintTitle", complaintTitle);

        assertNotNull(rt);

        ProcessInstance found = createWorkflowProcess(complaintId, badId, processVariables);
        assertFalse(found.isEnded());

        List<Task> approvals = ts.createTaskQuery().processInstanceId(found.getProcessInstanceId()).list();

        log.debug("Found " + approvals.size() + " approval tasks.");

        assertEquals(approvers.size(), approvals.size());

        for (int a = 0; a < approvals.size(); ++a)
        {
            Task task = approvals.get(a);
            ts.complete(task.getId());

            ProcessInstance current = rt.createProcessInstanceQuery().processInstanceId(found.getProcessInstanceId()).singleResult();
            boolean shouldBeComplete = a == approvals.size() - 1;

            // when all approvers have finished the process will be complete and the runtime service query
            // will not find it.
            if (shouldBeComplete)
            {
                assertNull(current);
            }
            else
            {
                assertFalse(current.isEnded());
            }

        }

    }

    private ProcessInstance createWorkflowProcess(Long complaintId, Long badId, Map<String, Object> processVariables)
    {
        // start a process
        ProcessInstance pi = rt.startProcessInstanceByKey("cmComplaintWorkflow", processVariables);
        rt.setVariable(pi.getId(), "cmComplaintId", complaintId);

        ExecutionQuery eq = rt.createExecutionQuery().variableValueEquals("cmComplaintId", complaintId);
        ExecutionQuery eqNotFound = rt.createExecutionQuery().variableValueEquals("cmComplaintId", badId);

        Execution found = eq.singleResult();
        Object notFound = eqNotFound.singleResult();

        assertNotNull(found);
        assertNull(notFound);

        return pi;
    }

}
