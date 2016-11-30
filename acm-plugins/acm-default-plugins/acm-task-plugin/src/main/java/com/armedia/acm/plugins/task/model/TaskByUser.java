package com.armedia.acm.plugins.task.model;

/**
 * @author sasko.tanaskoski
 *
 */
public class TaskByUser
{
    private String user;
    private int taskCount;

    public String getUser()
    {
        return user;
    }

    public void setUser(String user)
    {
        this.user = user;
    }

    public int getTaskCount()
    {
        return taskCount;
    }

    public void setTaskCount(int taskCount)
    {
        this.taskCount = taskCount;
    }

}
