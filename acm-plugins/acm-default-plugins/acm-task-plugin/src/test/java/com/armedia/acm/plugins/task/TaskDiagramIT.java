package com.armedia.acm.plugins.task;

import com.armedia.acm.plugins.task.exception.AcmTaskException;
import com.armedia.acm.plugins.task.service.impl.AcmTaskServiceImpl;
import com.armedia.acm.plugins.task.service.impl.ActivitiTaskDao;
import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicMatch;
import org.activiti.engine.*;
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

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/spring/spring-library-task-activiti-test.xml"})
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
