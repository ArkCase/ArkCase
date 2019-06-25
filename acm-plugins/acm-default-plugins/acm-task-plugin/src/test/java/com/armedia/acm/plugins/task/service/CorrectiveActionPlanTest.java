package com.armedia.acm.plugins.task.service;

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

import com.armedia.acm.plugins.task.model.TaskConstants;

import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/spring-library-task-activiti-test.xml" })
public class CorrectiveActionPlanTest
{
    private transient final Logger log = LogManager.getLogger(getClass());
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

    @Before
    public void setUp() throws Exception
    {
        // deploy
        repo.createDeployment()
                .addClasspathResource("activiti/Corrective Action Plan_v3.bpmn20.xml")
                .deploy();
    }

    @Test
    public void correctiveActionPlan()
    {
        String assignee = "jgarcia@dead.net";
        String documentAuthor = "rhunter@dead.net";
        String objectName = "Dark Star.flac";

        Map<String, Object> pvars = new HashMap<>();
        pvars.put("assignee", assignee);
        pvars.put("documentAuthor", documentAuthor);
        pvars.put("OBJECT_NAME", objectName);
        pvars.put("candidateGroups", "Grateful Dead");
        pvars.put("dueDate", new Date());

        ProcessInstance pi = rt.startProcessInstanceByKey("CorrectiveActionPlan", pvars);

        Task assigneeTask = verifyTask(assignee, objectName, pi, "Review Document ", (Date) pvars.get("dueDate"));

        ts.complete(assigneeTask.getId());

        Task finalizeTask = verifyTask(documentAuthor, objectName, pi, "Finalize Document ", (Date) pvars.get("dueDate"));

        ts.complete(finalizeTask.getId());

        // should have a history process
        List<HistoricProcessInstance> hpiList = hs.createHistoricProcessInstanceQuery().processInstanceId(pi.getId()).list();
        assertEquals(1, hpiList.size());

        // should not be a current process any more
        List<ProcessInstance> pis = rt.createProcessInstanceQuery().processInstanceId(pi.getId()).list();
        assertEquals(0, pis.size());
    }

    protected Task verifyTask(String assignee, String objectName, ProcessInstance pi, String tasknamePrefix, Date dueDate)
    {
        Task foundTask = ts.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
        assertNotNull(foundTask);
        assertEquals(tasknamePrefix + "\"" + objectName + "\"", foundTask.getName());
        assertEquals(assignee, foundTask.getAssignee());
        assertEquals(dueDate, foundTask.getDueDate());

        List<IdentityLink> identityLinks = ts.getIdentityLinksForTask(foundTask.getId());

        // one for the assignee, one for the candidate group
        assertEquals(2, identityLinks.size());

        identityLinks.removeIf(il -> "assignee".equals(il.getType()));

        // now we should have one
        assertEquals(1, identityLinks.size());
        IdentityLink link = identityLinks.get(0);
        assertEquals(TaskConstants.IDENTITY_LINK_TYPE_CANDIDATE, link.getType());

        assertEquals("Grateful Dead", link.getGroupId());

        return foundTask;
    }

}
