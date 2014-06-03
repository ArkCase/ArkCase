package com.armedia.acm.plugins.task.service.impl;


import com.armedia.acm.plugins.task.exception.AcmTaskException;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.service.TaskDao;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

class ActivitiTaskDao implements TaskDao
{
    private TaskService activitiTaskService;
    private RepositoryService activitiRepositoryService;
    private Logger log = LoggerFactory.getLogger(getClass());
    private HistoryService activitiHistoryService;

    @Override
    @Transactional
    public AcmTask createAdHocTask(AcmTask in) throws AcmTaskException
    {
        Task activitiTask = getActivitiTaskService().newTask();
        activitiTask.setAssignee(in.getAssignee());
        activitiTask.setPriority(in.getPriority());
        activitiTask.setDueDate(in.getDueDate());
        activitiTask.setName(in.getTitle());

        try
        {
            getActivitiTaskService().saveTask(activitiTask);
            in.setTaskId(Long.valueOf(activitiTask.getId()));
            return in;
        }
        catch (ActivitiException e)
        {
            throw new AcmTaskException(e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public AcmTask completeTask(Principal userThatCompletedTheTask, Long taskId) throws AcmTaskException
    {
        verifyCompleteTaskArgs(userThatCompletedTheTask, taskId);

        String user = userThatCompletedTheTask.getName();

        if ( log.isInfoEnabled() )
        {
            log.info("Completing task '" + taskId + "' for user '" + user + "'");
        }

        String strTaskId = String.valueOf(taskId);

        Task existingTask = getActivitiTaskService().createTaskQuery().taskId(strTaskId).singleResult();

        verifyTaskExists(taskId, existingTask);

        verifyUserIsTheAssignee(taskId, user, existingTask);

        AcmTask retval = acmTaskFromActivitiTask(existingTask);

        try
        {
            getActivitiTaskService().complete(strTaskId);

            HistoricTaskInstance hti =
                    getActivitiHistoryService().createHistoricTaskInstanceQuery().taskId(strTaskId).singleResult();

            retval.setTaskStartDate(hti.getStartTime());
            retval.setTaskFinishedDate(hti.getEndTime());
            retval.setTaskDurationInMillis(hti.getDurationInMillis());
            retval.setCompleted(true);

            return retval;
        }
        catch (ActivitiException e)
        {
            log.error("Could not close task '" + taskId + "' for user '" + user + "': " + e.getMessage(), e);
            throw new AcmTaskException(e);
        }
    }

    protected void verifyCompleteTaskArgs(Principal userThatCompletedTheTask, Long taskId)
    {
        if ( userThatCompletedTheTask == null || taskId == null )
        {
            throw new IllegalArgumentException("userThatCompletedTheTask and taskId must be specified");
        }
    }

    protected void verifyUserIsTheAssignee(Long taskId, String user, Task existingTask) throws AcmTaskException
    {
        if ( existingTask.getAssignee() == null || !existingTask.getAssignee().equals(user))
        {
            throw new AcmTaskException("Task '" + taskId + "' can only be closed by the assignee.");
        }
    }

    protected void verifyTaskExists(Long taskId, Task existingTask) throws AcmTaskException
    {
        if ( existingTask == null )
        {
            throw new AcmTaskException("Task '" + taskId + "' does not exist or has already been completed.");
        }
    }

    @Override
    public List<AcmTask> tasksForUser(String user)
    {
        if ( log.isInfoEnabled() )
        {
            log.info("Finding all tasks for user '" + user + "'");
        }

        List<Task> activitiTasks =
                getActivitiTaskService().createTaskQuery().taskAssignee(user).includeProcessVariables().
                        orderByDueDate().desc().list();

        if ( log.isDebugEnabled() )
        {
            log.debug("Found '" + activitiTasks.size() + "' tasks for user '" + user + "'");
        }

        List<AcmTask> retval = new ArrayList<>(activitiTasks.size());

        for ( Task activitiTask : activitiTasks )
        {
            AcmTask acmTask = acmTaskFromActivitiTask(activitiTask);

            retval.add(acmTask);
        }

        return retval;
    }

    protected AcmTask acmTaskFromActivitiTask(Task activitiTask)
    {
        AcmTask acmTask = new AcmTask();

        acmTask.setTaskId(Long.valueOf(activitiTask.getId()));
        acmTask.setDueDate(activitiTask.getDueDate());
        acmTask.setPriority(activitiTask.getPriority());
        acmTask.setTitle(activitiTask.getName());
        acmTask.setAssignee(activitiTask.getAssignee());

        extractProcessVariables(activitiTask, acmTask);

        findBusinessProcessName(activitiTask, acmTask);

        if ( log.isTraceEnabled() )
        {
            log.trace("Activiti task id '" + acmTask.getTaskId() + "' for object type '" +
                    acmTask.getAttachedToObjectType() + "'" +
                    ", object id '" + acmTask.getAttachedToObjectId() + "' found for user '" + acmTask.getAssignee()
                    + "'");
        }

        return acmTask;
    }

    protected void findBusinessProcessName(Task activitiTask, AcmTask acmTask)
    {
        String pid = activitiTask.getProcessDefinitionId();
        if ( pid != null )
        {
            ProcessDefinition pd =
                    getActivitiRepositoryService().createProcessDefinitionQuery().processDefinitionId(pid).singleResult();
            acmTask.setBusinessProcessName(pd.getName());
            acmTask.setAdhocTask(false);
        }
        else
        {
            acmTask.setAdhocTask(true);
        }
    }

    protected void extractProcessVariables(Task activitiTask, AcmTask acmTask)
    {
        if ( activitiTask.getProcessVariables() != null )
        {
            acmTask.setAttachedToObjectId((Long) activitiTask.getProcessVariables().get("OBJECT_ID"));
            acmTask.setAttachedToObjectType((String) activitiTask.getProcessVariables().get("OBJECT_TYPE"));
        }
    }

    public TaskService getActivitiTaskService()
    {
        return activitiTaskService;
    }

    public void setActivitiTaskService(TaskService activitiTaskService)
    {
        this.activitiTaskService = activitiTaskService;
    }

    public RepositoryService getActivitiRepositoryService()
    {
        return activitiRepositoryService;
    }

    public void setActivitiRepositoryService(RepositoryService activitiRepositoryService)
    {
        this.activitiRepositoryService = activitiRepositoryService;
    }

    public void setActivitiHistoryService(HistoryService activitiHistoryService)
    {
        this.activitiHistoryService = activitiHistoryService;
    }

    public HistoryService getActivitiHistoryService()
    {
        return activitiHistoryService;
    }
}
