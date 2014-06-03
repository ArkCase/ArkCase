package com.armedia.acm.plugins.task;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/spring-library-task-activiti-test.xml" } )
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

    private Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {

        // deploy
        repo.createDeployment()
                    .addClasspathResource("activiti/TestTaskProcess.bpmn20.xml")
                .deploy();

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
        List<Task> userTasks = ts.
                createTaskQuery().
                taskAssignee(user).
                includeProcessVariables().
                orderByDueDate().
                desc().
                list();

        assertEquals(1, userTasks.size());

        Task first = userTasks.get(0);
        assertNotNull(first.getDueDate());
        assertEquals(ACTIVITI_DEFAULT_PRIORITY, first.getPriority());

        String pid = first.getProcessDefinitionId();
        if ( pid != null )
        {
            ProcessDefinition pd = repo.createProcessDefinitionQuery().processDefinitionId(pid).singleResult();
            log.info("process name: " + pd.getName());
            log.info("process id: " + pd.getId());
        }


        log.info("Task due date: " + first.getDueDate());
        log.info("Priority: " + first.getPriority());


    }
}
