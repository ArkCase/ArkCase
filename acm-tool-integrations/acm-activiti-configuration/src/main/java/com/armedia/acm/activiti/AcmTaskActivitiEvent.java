package com.armedia.acm.activiti;

import com.armedia.acm.core.model.AcmEvent;

import org.activiti.engine.task.Task;

import java.util.Date;
import java.util.Map;

public class AcmTaskActivitiEvent extends AcmEvent implements AcmTaskEvent
{

    private String assignee;
    private String taskName;
    private Date taskCreated;
    private String description;
    private Date dueDate;
    private String taskEvent;
    private String priority;
    private boolean adhocTask;
    private String owner;
    private String businessProcessName;
    private Map<String, Object> processVariables;
    private Map<String, Object> localVariables;

    public AcmTaskActivitiEvent(Task source, String taskEvent, Map<String, Object> processVariables, Map<String, Object> localVariables)
    {
        super(source);
        setSucceeded(true);
        setObjectType("TASK");
        setEventType("com.armedia.acm.activiti.task." + taskEvent);
        setObjectId(Long.valueOf(source.getId()));
        setEventDate(new Date());
        setUserId("ACTIVITI_SYSTEM");

        setAssignee(source.getAssignee());
        setTaskName(source.getName());
        setTaskCreated(source.getCreateTime());
        setOwner(source.getOwner());
        setDescription(source.getDescription());
        setDueDate(source.getDueDate());
        setTaskEvent(taskEvent);
        setPriority(determinePriority(source.getPriority()));
        setParentObjectId((Long) processVariables.get("OBJECT_ID"));
        setParentObjectType((String) processVariables.get("OBJECT_TYPE"));
        setParentObjectName((String) processVariables.get("OBJECT_NAME"));
        setProcessVariables(processVariables);
        setLocalVariables(localVariables);
    }

    private String determinePriority(int priority)
    {
        if (priority < 50)
        {
            return "Low";
        } else if (priority < 70)
        {
            return "Medium";
        } else if (priority < 90)
        {
            return "High";
        }

        return "Expedite";
    }

    @Override
    public String getAssignee()
    {
        return assignee;
    }

    public void setAssignee(String assignee)
    {
        this.assignee = assignee;
    }

    @Override
    public String getTaskName()
    {
        return taskName;
    }

    public void setTaskName(String taskName)
    {
        this.taskName = taskName;
    }

    @Override
    public Date getTaskCreated()
    {
        return taskCreated;
    }

    public void setTaskCreated(Date taskCreated)
    {
        this.taskCreated = taskCreated;
    }

    @Override
    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    @Override
    public Date getDueDate()
    {
        return dueDate;
    }

    public void setDueDate(Date dueDate)
    {
        this.dueDate = dueDate;
    }

    @Override
    public String getTaskEvent()
    {
        return taskEvent;
    }

    public void setTaskEvent(String taskEvent)
    {
        this.taskEvent = taskEvent;
    }

    @Override
    public String getPriority()
    {
        return priority;
    }

    public void setPriority(String priority)
    {
        this.priority = priority;
    }

    @Override
    public boolean isAdhocTask()
    {
        return adhocTask;
    }

    public void setAdhocTask(boolean adhocTask)
    {
        this.adhocTask = adhocTask;
    }

    @Override
    public String getOwner()
    {
        return owner;
    }

    public void setOwner(String owner)
    {
        this.owner = owner;
    }

    @Override
    public String getBusinessProcessName()
    {
        return businessProcessName;
    }

    public void setBusinessProcessName(String businessProcessName)
    {
        this.businessProcessName = businessProcessName;
    }

    public Map<String, Object> getProcessVariables()
    {
        return processVariables;
    }

    public void setProcessVariables(Map<String, Object> processVariables)
    {
        this.processVariables = processVariables;
    }

    public Map<String, Object> getLocalVariables()
    {
        return localVariables;
    }

    public void setLocalVariables(Map<String, Object> localVariables)
    {
        this.localVariables = localVariables;
    }

}
