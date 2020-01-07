package com.armedia.acm.plugins.task;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/spring-library-task-activiti-test.xml" })
public class TaskActivitiIT
{
    public static final int ACTIVITI_DEFAULT_PRIORITY = 50;
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
                .addClasspathResource("activiti/TestTaskProcess.bpmn20.xml")
                .deploy();

    }

    @Test
    public void taskNotFound() throws Exception
    {
        Task found = ts.createTaskQuery().taskId("NoSuchTask").singleResult();

        assertNull(found);
    }

    @Test
    public void adhocTask() throws Exception
    {
        String user = "user";

        Task task = ts.newTask();
        task.setDueDate(new Date());
        task.setAssignee(user);

        ts.saveTask(task);

        verifyUserTask(user);

        ts.complete(task.getId());

        Task afterComplete = ts.createTaskQuery().taskId(task.getId()).singleResult();
        assertNull(afterComplete);

        HistoricTaskInstance hti = hs.createHistoricTaskInstanceQuery().taskId(task.getId()).singleResult();
        assertNotNull(hti);

        assertEquals(task.getId(), hti.getId());
    }

    @Test
    public void findTasks() throws Exception
    {
        String user = "Test User";
        String user2 = "Another User";

        List<String> approvers = Arrays.asList(user, user2);
        Map<String, Object> processVariables = new HashMap<>();
        processVariables.put("approvers", approvers);

        rt.startProcessInstanceByKey("testTaskProcess", processVariables);

        verifyUserTask(user);
        verifyUserTask(user2);

    }

    protected void verifyUserTask(String user)
    {
        List<Task> userTasks = ts.createTaskQuery().taskAssignee(user).includeProcessVariables().orderByDueDate().desc().list();

        assertEquals(1, userTasks.size());

        Task first = userTasks.get(0);
        assertNotNull(first.getDueDate());
        assertEquals(ACTIVITI_DEFAULT_PRIORITY, first.getPriority());

        String pid = first.getProcessDefinitionId();
        if (pid != null)
        {
            ProcessDefinition pd = repo.createProcessDefinitionQuery().processDefinitionId(pid).singleResult();
            log.info("process name: " + pd.getName());
            log.info("process id: " + pd.getId());
        }

        log.info("Task due date: " + first.getDueDate());
        log.info("Priority: " + first.getPriority());

    }
}
