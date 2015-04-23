package com.armedia.acm.activiti;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-activiti-configuration.xml",
        "/spring/spring-library-data-source.xml",
        "/spring/spring-library-test-activiti-configuration.xml",
        "/spring/spring-library-context-holder.xml",
        "/spring/spring-library-property-file-manager.xml"
})
public class AcmActivitiSpringIT
{


    @Autowired
    private TaskService taskService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private RepositoryService repo;

    @Autowired
    private AcmActivitiTaskEventListener acmTaskEventListener;

    private Deployment testDeployment;

    @Before
    public void setUp() throws Exception
    {
        // deploy
        testDeployment = repo.createDeployment()
                .addClasspathResource("activiti/TestActivitiSpringProcess.bpmn20.xml")
                .deploy();

        acmTaskEventListener.reset();
    }

    @After
    public void shutDown() throws Exception
    {
        repo.deleteDeployment(testDeployment.getId(), true);
    }

    @Test
    public void userTask()
    {
        assertEquals(0, acmTaskEventListener.getTimesCalled());

        Map<String, Object> processVariables = new HashMap<>();
        processVariables.put("key", "Grateful Dead");

        ProcessInstance pi = runtimeService.startProcessInstanceByKey("TestActivitiSpringProcess", processVariables);
        assertNotNull(pi);

        Task userTask = taskService.createTaskQuery().processInstanceId(pi.getId()).singleResult();

        assertNotNull(userTask);

        taskService.claim(userTask.getId(), "test assignee");

        Task found = taskService.createTaskQuery().taskId(userTask.getId()).singleResult();
        assertNotNull(found);
        assertEquals("test assignee", found.getAssignee());

        taskService.complete(found.getId());

        // events: one for assignment, one for create, one for complete, one for delete
        int expectedEventCount = 4;

        assertEquals(expectedEventCount, acmTaskEventListener.getTimesCalled());
    }
}
