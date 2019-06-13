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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import com.armedia.acm.plugins.task.model.TaskConstants;

import org.activiti.engine.ActivitiTaskAlreadyClaimedException;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/spring-library-task-activiti-test.xml" })
public class CandidateGroupIT
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
                .addClasspathResource("activiti/Task_Buckets_Sample.bpmn20.xml")
                .deploy();

    }

    @Test
    public void identifyCandidateGroup() throws Exception
    {
        Map<String, Object> processVariables = new HashMap<>();
        String player = "Jerry Garcia";
        processVariables.put("player", player);

        ProcessInstance pi = rt.startProcessInstanceByKey("TaskBucketsSample", processVariables);

        List<Task> userTasks = ts.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).includeProcessVariables().list();

        assertEquals(1, userTasks.size());

        Task task = userTasks.get(0);

        List<IdentityLink> identityLinks = ts.getIdentityLinksForTask(task.getId());

        assertEquals(1, identityLinks.size());

        IdentityLink link = identityLinks.get(0);
        assertEquals(TaskConstants.IDENTITY_LINK_TYPE_CANDIDATE, link.getType());

        assertEquals("gratefulDead " + player, link.getGroupId());
    }

    @Test
    public void onlyOneUserCanClaim()
    {
        // this test is just to illustrate how Activiti works, versus ArkCase logic.

        Map<String, Object> processVariables = new HashMap<>();
        String player = "Jerry Garcia";
        processVariables.put("player", player);

        ProcessInstance pi = rt.startProcessInstanceByKey("TaskBucketsSample", processVariables);

        List<Task> userTasks = ts.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).includeProcessVariables().list();

        assertEquals(1, userTasks.size());

        Task task = userTasks.get(0);

        ts.claim(task.getId(), "jgarcia");

        // same user can claim again
        ts.claim(task.getId(), "jgarcia");

        Task claimed = ts.createTaskQuery().taskId(task.getId()).singleResult();
        assertEquals("jgarcia", claimed.getAssignee());

        try
        {
            ts.claim(task.getId(), "bweir");
            fail("should have exception here");
        }
        catch (ActivitiTaskAlreadyClaimedException e)
        {
            // good, this is what we wanted.
        }

        ts.unclaim(task.getId());

        Task unclaimed = ts.createTaskQuery().taskId(claimed.getId()).singleResult();
        assertNull(unclaimed.getAssignee());

    }

}
