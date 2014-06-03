package com.armedia.acm.plugins.task.service.impl;

import com.armedia.acm.plugins.task.model.AcmTask;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

/**
 * Created by armdev on 6/2/14.
 */
public class ActivitiTaskDaoTest extends EasyMockSupport
{
    private TaskService mockTaskService;
    private RepositoryService mockRepositoryService;
    private Task mockTask;
    private TaskQuery mockTaskQuery;
    private ProcessDefinitionQuery mockProcessDefinitionQuery;
    private ProcessDefinition mockProcessDefinition;

    private ActivitiTaskDao unit;

    @Before
    public void setUp() throws Exception
    {
        mockTaskService = createMock(TaskService.class);
        mockTask = createMock(Task.class);
        mockTaskQuery = createMock(TaskQuery.class);
        mockRepositoryService = createMock(RepositoryService.class);
        mockProcessDefinitionQuery = createMock(ProcessDefinitionQuery.class);
        mockProcessDefinition = createMock(ProcessDefinition.class);

        unit = new ActivitiTaskDao();

        unit.setActivitiTaskService(mockTaskService);
        unit.setActivitiRepositoryService(mockRepositoryService);
    }

    @Test
    public void tasksForUser() throws Exception
    {
        String user = "user";

        Long taskId = 500L;
        Date dueDate = new Date();
        int priority = 22;
        String title = "task Title";
        String processId = "processId";
        String processName = "processName";

        Long objectId = 250L;
        String objectType = "objectType";

        Map<String, Object> pvars = new HashMap<>();
        pvars.put("OBJECT_ID", objectId);
        pvars.put("OBJECT_TYPE", objectType);

        expect(mockTaskService.createTaskQuery()).andReturn(mockTaskQuery);
        expect(mockTaskQuery.taskAssignee(user)).andReturn(mockTaskQuery);
        expect(mockTaskQuery.includeProcessVariables()).andReturn(mockTaskQuery);
        expect(mockTaskQuery.orderByDueDate()).andReturn(mockTaskQuery);
        expect(mockTaskQuery.desc()).andReturn(mockTaskQuery);
        expect(mockTaskQuery.list()).andReturn(Arrays.asList(mockTask));

        expect(mockTask.getId()).andReturn(taskId.toString());
        expect(mockTask.getDueDate()).andReturn(dueDate);
        expect(mockTask.getPriority()).andReturn(priority);
        expect(mockTask.getName()).andReturn(title);
        expect(mockTask.getProcessVariables()).andReturn(pvars).atLeastOnce();
        expect(mockTask.getAssignee()).andReturn(user);
        expect(mockTask.getProcessDefinitionId()).andReturn(processId);

        expect(mockRepositoryService.createProcessDefinitionQuery()).andReturn(mockProcessDefinitionQuery);
        expect(mockProcessDefinitionQuery.processDefinitionId(processId)).andReturn(mockProcessDefinitionQuery);
        expect(mockProcessDefinitionQuery.singleResult()).andReturn(mockProcessDefinition);

        expect(mockProcessDefinition.getName()).andReturn(processName);

        replayAll();

        List<AcmTask> tasks = unit.tasksForUser(user);

        verifyAll();

        assertEquals(1, tasks.size());

        AcmTask found = tasks.get(0);

        assertEquals(taskId, found.getTaskId());
        assertEquals(dueDate, found.getDueDate());
        assertEquals(priority, found.getPriority());
        assertEquals(title, found.getTitle());
        assertEquals(objectId, found.getAttachedToObjectId());
        assertEquals(objectType, found.getAttachedToObjectType());
        assertEquals(user, found.getAssignee());
        assertEquals(processName, found.getBusinessProcessName());
        assertFalse(found.isAdhocTask());

    }
}
