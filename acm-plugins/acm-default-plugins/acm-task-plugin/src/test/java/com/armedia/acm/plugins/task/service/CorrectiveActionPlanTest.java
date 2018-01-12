package com.armedia.acm.plugins.task.service;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        // deploy
        repo.createDeployment()
                .addClasspathResource("activiti/Corrective Action Plan_v2.bpmn20.xml")
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
