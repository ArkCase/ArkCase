package com.armedia.acm.plugins.task.service.impl;


import com.armedia.acm.plugins.task.exception.AcmTaskException;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.model.NumberOfDays;
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
import java.util.*;

class ActivitiTaskDao implements TaskDao
{
    private TaskService activitiTaskService;
    private RepositoryService activitiRepositoryService;
    private Logger log = LoggerFactory.getLogger(getClass());
    private HistoryService activitiHistoryService;
    private Map<String, Integer> priorityLevelToNumberMap;



    @Override
    @Transactional
    public AcmTask createAdHocTask(AcmTask in) throws AcmTaskException
    {
        Task activitiTask = getActivitiTaskService().newTask();

        return updateExistingActivitiTask(in, activitiTask);
    }

    @Override
    @Transactional
    public AcmTask save(AcmTask in) throws AcmTaskException
    {
        Task activitiTask = getActivitiTaskService().createTaskQuery().taskId(in.getTaskId().toString()).singleResult();
        if ( activitiTask != null )
        {
            return updateExistingActivitiTask(in, activitiTask);
        }

        // task must have been completed.  Try finding the historic task; but historical tasks can't be updated, so
        // even if we find it we have to throw an exception
        {
            HistoricTaskInstance hti = getActivitiHistoryService().
                    createHistoricTaskInstanceQuery().
                    taskId(in.getTaskId().toString()).
                    singleResult();

            if ( hti == null )
            {
                // no such task!
                throw new AcmTaskException("No such task with id '" + in.getTaskId() + "'");
            }
            else
            {
                throw new AcmTaskException("Task with id '" + in.getTaskId() + "' has already been completed and so " +
                        "it cannot be updated.");
            }
        }


    }

    private AcmTask updateExistingActivitiTask(AcmTask in, Task activitiTask) throws AcmTaskException
    {
        activitiTask.setAssignee(in.getAssignee());
        activitiTask.setOwner(in.getOwner());
        Integer activitiPriority = activitiPriorityFromAcmPriority(in.getPriority());
        activitiTask.setPriority(activitiPriority);
        activitiTask.setDueDate(in.getDueDate());
        activitiTask.setName(in.getTitle());

        try
        {
            getActivitiTaskService().saveTask(activitiTask);
            getActivitiTaskService().setVariableLocal(activitiTask.getId(), "OBJECT_TYPE", in.getAttachedToObjectType());
            getActivitiTaskService().setVariableLocal(activitiTask.getId(), "OBJECT_ID", in.getAttachedToObjectId());
            getActivitiTaskService().setVariableLocal(activitiTask.getId(), in.getAttachedToObjectType(), in.getAttachedToObjectId());
            getActivitiTaskService().setVariableLocal(activitiTask.getId(), "OBJECT_NAME", in.getAttachedToObjectName());
            getActivitiTaskService().setVariableLocal(activitiTask.getId(), "START_DATE", in.getTaskStartDate());
            String status = in.getStatus() == null ? "ASSIGNED" : in.getStatus();
            getActivitiTaskService().setVariableLocal(activitiTask.getId(), "TASK_STATUS", status);
            getActivitiTaskService().setVariableLocal(activitiTask.getId(), "PERCENT_COMPLETE", in.getPercentComplete());
            getActivitiTaskService().setVariableLocal(activitiTask.getId(), "DETAILS", in.getDetails());

            in.setTaskId(Long.valueOf(activitiTask.getId()));
            in.setCreateDate(activitiTask.getCreateTime());
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

        Task existingTask = getActivitiTaskService().
                createTaskQuery().
                includeProcessVariables().
                includeTaskLocalVariables().
                taskId(strTaskId).
                singleResult();

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

        List<Task> activitiTasks = getActivitiTaskService().
                createTaskQuery().
                taskAssignee(user).
                includeProcessVariables().
                includeTaskLocalVariables().
                orderByDueDate().desc().
                list();

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
    @Override
    public List<AcmTask> allTasks() {
        if ( log.isInfoEnabled() )
        {
            log.info("Finding all tasks for all users '");
        }
        List<Task> activitiTasks = getActivitiTaskService().
                createTaskQuery().
                includeProcessVariables().
                includeTaskLocalVariables().
                orderByDueDate().desc().
                list();

        if ( log.isDebugEnabled() )
        {
            log.debug("Found '" + activitiTasks.size() + "' tasks for all users");
        }

        List<AcmTask> retval = new ArrayList<>(activitiTasks.size());

        for ( Task activitiTask : activitiTasks )
        {
            AcmTask acmTask = acmTaskFromActivitiTask(activitiTask);

            retval.add(acmTask);
        }
        return retval;
    }

    @Override
    public List<AcmTask> pastDueTasks() {
        if ( log.isInfoEnabled() )
        {
            log.info("Finding all tasks for all users that due date was before today");
        }
        List<Task> activitiTasks = getActivitiTaskService().
                createTaskQuery().
                includeProcessVariables().
                includeTaskLocalVariables().
                dueBefore(new Date()).
                list();

        if ( log.isDebugEnabled() )
        {
            log.debug("Found '" + activitiTasks.size() + "' tasks for all users with past due date");
        }

        List<AcmTask> retval = new ArrayList<>(activitiTasks.size());

        for ( Task activitiTask : activitiTasks )
        {
            AcmTask acmTask = acmTaskFromActivitiTask(activitiTask);

            retval.add(acmTask);
        }
        return retval;
    }

    @Override
    public List<AcmTask> dueSpecificDateTasks(NumberOfDays numberOfDaysFromToday) {
        if (log.isInfoEnabled())
        {
            log.info(String.format("Finding all tasks for all users which due date is until %s from today", numberOfDaysFromToday.getnDays()));
        }

        List<Task> activitiTasks = getActivitiTaskService().
                createTaskQuery().
                includeProcessVariables().
                includeTaskLocalVariables().
                dueAfter(new Date()).
                dueBefore(shiftDateFromToday(numberOfDaysFromToday.getNumOfDays())).
                list();

        if (log.isDebugEnabled())
        {
            log.debug("Found '" + activitiTasks.size() + "' tasks for all users which due date is between today and " + numberOfDaysFromToday.getnDays() + " from today");
        }

        List<AcmTask> retval = new ArrayList<>(activitiTasks.size());

        for (Task activitiTask : activitiTasks)
        {
            AcmTask acmTask = acmTaskFromActivitiTask(activitiTask);
            retval.add(acmTask);
        }
        return retval;
    }

    @Override
    public AcmTask findById(Long taskId) throws AcmTaskException
    {
        if ( log.isInfoEnabled() )
        {
            log.info("Finding task with ID '" + taskId + "'");
        }
        AcmTask retval;

        Task activitiTask = getActivitiTaskService().
                createTaskQuery().
                taskId(String.valueOf(taskId)).
                includeProcessVariables().
                includeTaskLocalVariables().
                singleResult();
        if ( activitiTask != null )
        {
            retval = acmTaskFromActivitiTask(activitiTask);
            return retval;
        }
        else
        {
            HistoricTaskInstance hti = getActivitiHistoryService().
                    createHistoricTaskInstanceQuery().
                    taskId(String.valueOf(taskId)).
                    includeProcessVariables().
                    includeTaskLocalVariables().
                    singleResult();

            if ( hti != null )
            {
                retval = acmTaskFromHistoricActivitiTask(hti);

                return retval;
            }
        }

        throw new AcmTaskException("Task with ID '" + taskId + "' does not exist.");

    }

    protected AcmTask acmTaskFromHistoricActivitiTask(HistoricTaskInstance hti)
    {
        AcmTask retval;
        retval = new AcmTask();
        retval.setTaskStartDate(hti.getStartTime());
        retval.setTaskFinishedDate(hti.getEndTime());
        retval.setTaskDurationInMillis(hti.getDurationInMillis());
        retval.setCompleted(true);

        retval.setTaskId(Long.valueOf(hti.getId()));
        retval.setDueDate(hti.getDueDate());
        String taskPriority = acmPriorityFromActivitiPriority(hti.getPriority());
        retval.setPriority(taskPriority);
        retval.setTitle(hti.getName());
        retval.setAssignee(hti.getAssignee());

        if ( hti.getProcessVariables() != null )
        {
            retval.setAttachedToObjectId((Long) hti.getProcessVariables().get("OBJECT_ID"));
            retval.setAttachedToObjectType((String) hti.getProcessVariables().get("OBJECT_TYPE"));
            retval.setAttachedToObjectName((String) hti.getProcessVariables().get("OBJECT_NAME"));
            retval.setWorkflowRequestId((Long) hti.getProcessVariables().get("REQUEST_ID"));
            retval.setWorkflowRequestType((String) hti.getProcessVariables().get("REQUEST_TYPE"));
            retval.setReviewDocumentPdfRenditionId((Long) hti.getProcessVariables().get("pdfRenditionId"));
            retval.setReviewDocumentFormXmlId((Long) hti.getProcessVariables().get("formXmlId"));

        }

        if ( hti.getTaskLocalVariables() != null )
        {
            Map<String, Object> taskLocal = hti.getTaskLocalVariables();

            extractTaskLocalVariables(retval, taskLocal);
        }

        String pid = hti.getProcessDefinitionId();
        if ( pid != null )
        {
            ProcessDefinition pd =
                    getActivitiRepositoryService().createProcessDefinitionQuery().processDefinitionId(pid).singleResult();
            retval.setBusinessProcessName(pd.getName());
            retval.setAdhocTask(false);
            retval.setBusinessProcessId(
                    hti.getProcessInstanceId() == null ? null : Long.valueOf(hti.getProcessInstanceId()));
        }
        else
        {
            retval.setAdhocTask(true);
        }

        if ( log.isTraceEnabled() )
        {
            log.trace("Activiti task id '" + retval.getTaskId() + "' for object type '" +
                    retval.getAttachedToObjectType() + "'" +
                    ", object id '" + retval.getAttachedToObjectId() + "' found for user '" + retval.getAssignee()
                    + "'");
        }

        return retval;
    }

    private void extractTaskLocalVariables(AcmTask acmTask, Map<String, Object> taskLocal)
    {
        if ( acmTask.getAttachedToObjectId() == null )
        {
            Long objectId = (Long) taskLocal.get("OBJECT_ID");
            acmTask.setAttachedToObjectId(objectId);
        }
        if ( acmTask.getAttachedToObjectType() == null )
        {
            String objectType = (String) taskLocal.get("OBJECT_TYPE");
            acmTask.setAttachedToObjectType(objectType);
        }
        if ( acmTask.getAttachedToObjectName() == null )
        {
            String objectName = (String) taskLocal.get("OBJECT_NAME");
            acmTask.setAttachedToObjectName(objectName);
        }
        Date startDate = (Date) taskLocal.get("START_DATE");
        acmTask.setTaskStartDate(startDate);

        String status = (String) taskLocal.get("TASK_STATUS");
        acmTask.setStatus(status);

        Integer percentComplete = (Integer) taskLocal.get("PERCENT_COMPLETE");
        acmTask.setPercentComplete(percentComplete);

        String details = (String) taskLocal.get("DETAILS");
        acmTask.setDetails(details);
    }

    private String acmPriorityFromActivitiPriority(int priority)
    {
        String defaultPriority = "Medium";

        for ( Map.Entry<String, Integer> acmToActiviti : getPriorityLevelToNumberMap().entrySet() )
        {
            if ( acmToActiviti.getValue().equals(priority) )
            {
                return acmToActiviti.getKey();
            }
        }

        return defaultPriority;
    }

    private Integer activitiPriorityFromAcmPriority(String acmPriority)
    {
        Integer defaultPriority = 50;

        for ( Map.Entry<String, Integer> acmToActiviti : getPriorityLevelToNumberMap().entrySet() )
        {
            if ( acmToActiviti.getKey().equals(acmPriority) )
            {
                return acmToActiviti.getValue();
            }
        }

        return defaultPriority;
    }

    protected AcmTask acmTaskFromActivitiTask(Task activitiTask)
    {
        AcmTask acmTask = new AcmTask();

        acmTask.setTaskId(Long.valueOf(activitiTask.getId()));
        acmTask.setDueDate(activitiTask.getDueDate());
        String taskPriority = acmPriorityFromActivitiPriority(activitiTask.getPriority());
        acmTask.setPriority(taskPriority);
        acmTask.setTitle(activitiTask.getName());
        acmTask.setAssignee(activitiTask.getAssignee());
        acmTask.setCreateDate(activitiTask.getCreateTime());
        acmTask.setOwner(activitiTask.getOwner());

        extractProcessVariables(activitiTask, acmTask);

        if ( activitiTask.getTaskLocalVariables() != null )
        {
            extractTaskLocalVariables(acmTask, activitiTask.getTaskLocalVariables());
        }

        findBusinessProcessName(activitiTask, acmTask);

        if ( log.isTraceEnabled() )
        {
            log.trace("Activiti task id '" + acmTask.getTaskId() + "' for object type '" +
                    acmTask.getAttachedToObjectType() + "'" +
                    ", object id '" + acmTask.getAttachedToObjectId() + ", object number '" + acmTask.getAttachedToObjectName() +"' found for user '" + acmTask.getAssignee()
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
            acmTask.setBusinessProcessId(
                    activitiTask.getProcessInstanceId() == null ? null : Long.valueOf(activitiTask.getProcessInstanceId()));
        }
        else
        {
            acmTask.setAdhocTask(true);
        }
    }

    private Date shiftDateFromToday(int daysFromToday){
        Date nextDate;
        Date today = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        cal.add(Calendar.DATE,daysFromToday);
        nextDate = cal.getTime();
        return nextDate;
    }

    protected void extractProcessVariables(Task activitiTask, AcmTask acmTask)
    {
        if ( activitiTask.getProcessVariables() != null )
        {
            acmTask.setAttachedToObjectId((Long) activitiTask.getProcessVariables().get("OBJECT_ID"));
            acmTask.setAttachedToObjectType((String) activitiTask.getProcessVariables().get("OBJECT_TYPE"));
            acmTask.setAttachedToObjectName((String) activitiTask.getProcessVariables().get("OBJECT_NAME"));
            acmTask.setWorkflowRequestId((Long) activitiTask.getProcessVariables().get("REQUEST_ID"));
            acmTask.setWorkflowRequestType((String) activitiTask.getProcessVariables().get("REQUEST_TYPE"));
            acmTask.setReviewDocumentPdfRenditionId((Long) activitiTask.getProcessVariables().get("pdfRenditionId"));
            acmTask.setReviewDocumentFormXmlId((Long) activitiTask.getProcessVariables().get("formXmlId"));
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

    public Map<String, Integer> getPriorityLevelToNumberMap()
    {
        return priorityLevelToNumberMap;
    }

    public void setPriorityLevelToNumberMap(Map<String, Integer> priorityLevelToNumberMap)
    {
        this.priorityLevelToNumberMap = priorityLevelToNumberMap;
    }
}
