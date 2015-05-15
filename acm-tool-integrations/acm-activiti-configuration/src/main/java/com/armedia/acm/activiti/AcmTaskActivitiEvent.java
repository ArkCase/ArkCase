package com.armedia.acm.activiti;

import com.armedia.acm.event.AcmEvent;
import org.activiti.engine.task.Task;

import java.util.Date;

public class AcmTaskActivitiEvent extends AcmEvent implements AcmTaskEvent {

    private String assignee;
    private String taskName;
    private Date taskCreated;
    private String description;
    private Date dueDate;
    private String taskEvent;
    private String priority;
    private Long parentObjectId;
    private String parentObjectType;
    private String parentObjectName;
    private boolean adhocTask;
    private String owner;
    private String businessProcessName;


    public AcmTaskActivitiEvent(Task source, String taskEvent, Long parentObjectId, String parentObjectType, String parentObjectName)
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
        setParentObjectId(parentObjectId);
        setParentObjectType(parentObjectType);
        setParentObjectName(parentObjectName);
    }

    private String determinePriority(int priority)
    {
        if ( priority < 50 )
        {
            return "Low";
        }
        else if ( priority < 70 )
        {
            return "Medium";
        }
        else if ( priority < 90 )
        {
            return "High";
        }

        return "Expedite";
    }

    @Override
    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    @Override
    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    @Override
    public Date getTaskCreated() {
        return taskCreated;
    }

    public void setTaskCreated(Date taskCreated) {
        this.taskCreated = taskCreated;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    @Override
    public String getTaskEvent() {
        return taskEvent;
    }

    public void setTaskEvent(String taskEvent) {
        this.taskEvent = taskEvent;
    }

    @Override
    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    @Override
    public Long getParentObjectId() {
        return parentObjectId;
    }

    public void setParentObjectId(Long parentObjectId) {
        this.parentObjectId = parentObjectId;
    }

    @Override
    public String getParentObjectType() {
        return parentObjectType;
    }

    public void setParentObjectType(String parentObjectType) {
        this.parentObjectType = parentObjectType;
    }

    @Override
    public String getParentObjectName() {
        return parentObjectName;
    }

    public void setParentObjectName(String parentObjectName) {
        this.parentObjectName = parentObjectName;
    }

    @Override
    public boolean isAdhocTask() {return adhocTask;}

    public void setAdhocTask(boolean adhocTask) {this.adhocTask = adhocTask;}

    @Override
    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    @Override
    public String getBusinessProcessName() {
        return businessProcessName;
    }

    public void setBusinessProcessName(String businessProcessName) {
        this.businessProcessName = businessProcessName;
    }
}
