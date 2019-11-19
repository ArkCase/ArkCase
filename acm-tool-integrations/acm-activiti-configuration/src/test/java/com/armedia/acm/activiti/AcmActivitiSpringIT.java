package com.armedia.acm.activiti;

/*-
 * #%L
 * Tool Integrations: Activiti Configuration
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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-configuration.xml",
        "/spring/spring-library-object-converter.xml",
        "/spring/spring-library-activiti-configuration.xml",
        "/spring/spring-library-data-source.xml",
        "/spring/spring-library-test-activiti-configuration.xml",
        "/spring/spring-library-context-holder.xml",
        "/spring/spring-library-property-file-manager.xml",
        "/spring/spring-library-acm-encryption.xml"
})
public class AcmActivitiSpringIT
{
    static
    {
        String userHomePath = System.getProperty("user.home");
        System.setProperty("acm.configurationserver.propertyfile", userHomePath + "/.arkcase/acm/conf.yml");
        System.setProperty("configuration.server.url", "http://localhost:9999");
    }

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

        ProcessInstance pi = runtimeService.startProcessInstanceByKey("TestActivitiSpringProcessUnitTest", processVariables);
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
