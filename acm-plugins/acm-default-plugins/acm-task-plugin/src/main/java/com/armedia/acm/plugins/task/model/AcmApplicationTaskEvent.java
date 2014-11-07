package com.armedia.acm.plugins.task.model;

import com.armedia.acm.activiti.AcmTaskEvent;
import com.armedia.acm.event.AcmEvent;

import java.util.Date;

/**
 * Created by armdev on 6/25/14.
 */
public class AcmApplicationTaskEvent extends AcmEvent implements AcmTaskEvent
{
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




    public AcmApplicationTaskEvent(AcmTask source, String taskEvent, String eventUser, boolean succeeded, String ipAddress)
    {
        super(source);

        setSucceeded(succeeded);
        setIpAddress(ipAddress);
        setObjectType("TASK");
        setEventType("com.armedia.acm.app.task." + taskEvent);
        setObjectId(source.getTaskId());
        setEventDate(new Date());
        setUserId(eventUser);

        setAssignee(source.getAssignee());
        setTaskName(source.getTitle());
        setTaskCreated(new Date());
        setDescription(source.getTitle());
        setDueDate(source.getDueDate());
        setTaskEvent(taskEvent);
        setPriority(source.getPriority());
        setParentObjectId(source.getAttachedToObjectId());
        setParentObjectType(source.getAttachedToObjectType());
        setParentObjectName(source.getAttachedToObjectName());
        setAdhocTask(source.isAdhocTask());

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



}
