package com.armedia.acm.plugins.task.service.impl;


import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.service.TaskDao;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

class ActivitiTaskDao implements TaskDao
{
    private TaskService activitiTaskService;
    private RepositoryService activitiRepositoryService;
    private Logger log = LoggerFactory.getLogger(getClass());

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
}
