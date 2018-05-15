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
import static org.junit.Assert.assertTrue;

import com.armedia.acm.plugins.task.exception.AcmTaskException;
import com.armedia.acm.plugins.task.service.impl.AcmTaskServiceImpl;
import com.armedia.acm.plugins.task.service.impl.ActivitiTaskDao;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicMatch;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/spring-library-task-activiti-test.xml" })
public class TaskDiagramIT
{
    @Autowired
    private RepositoryService repo;

    @Autowired
    private RuntimeService rt;

    @Autowired
    private TaskService ts;

    private AcmTaskServiceImpl acmTaskService;
    private ActivitiTaskDao dao;

    @Before
    public void setUp() throws Exception
    {

        // deploy
        repo.createDeployment().addClasspathResource("activiti/Task_Buckets_Sample.bpmn20.xml").deploy();

        dao = new ActivitiTaskDao();
        dao.setActivitiRepositoryService(repo);
        dao.setActivitiRuntimeService(rt);
        dao.setActivitiTaskService(ts);

        acmTaskService = new AcmTaskServiceImpl();
        acmTaskService.setTaskDao(dao);

    }

    @Test
    public void getDiagramTest() throws Exception
    {
        Map<String, Object> processVariables = new HashMap<>();
        String player = "Jerry Garcia";
        processVariables.put("player", player);

        ProcessInstance pi = rt.startProcessInstanceByKey("TaskBucketsSample", processVariables);

        List<Task> tasks = ts.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).includeProcessVariables().list();

        assertNotNull(tasks);
        assertEquals(1, tasks.size());

        byte[] diagram = acmTaskService.getDiagram(Long.parseLong(tasks.get(0).getId()));
        assertNotNull(diagram);

        MagicMatch match = Magic.getMagicMatch(diagram);
        String mimeType = match.getMimeType();

        assertEquals("image/png", mimeType);
    }

    @Test
    public void diagramNotFoundTest() throws Exception
    {
        Long idThatNotExist = 111L;
        String expectedMessage = "Diagram for task id = [" + idThatNotExist + "] cannot be retrieved";

        Map<String, Object> processVariables = new HashMap<>();
        String player = "Jerry Garcia";
        processVariables.put("player", player);

        ProcessInstance pi = rt.startProcessInstanceByKey("TaskBucketsSample", processVariables);

        List<Task> tasks = ts.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).includeProcessVariables().list();

        assertNotNull(tasks);
        assertEquals(1, tasks.size());

        Exception exception = null;

        try
        {
            acmTaskService.getDiagram(idThatNotExist);
        }
        catch (Exception e)
        {
            exception = e;
        }

        assertNotNull(exception);
        assertTrue(exception instanceof AcmTaskException);
        assertEquals(expectedMessage, exception.getMessage());
    }

}
