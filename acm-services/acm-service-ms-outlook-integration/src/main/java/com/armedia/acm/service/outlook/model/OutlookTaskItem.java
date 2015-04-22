package com.armedia.acm.service.outlook.model;

import java.util.Date;

/**
 * Created by armdev on 4/20/15.
 */
public class OutlookTaskItem extends OutlookItem
{
    private Date due;
    private Date started;
    private Date completed;
    private boolean complete;
    private double percentComplete;

    public Date getDue()
    {
        return due;
    }

    public void setDue(Date due)
    {
        this.due = due;
    }

    public Date getStarted()
    {
        return started;
    }

    public void setStarted(Date started)
    {
        this.started = started;
    }

    public Date getCompleted()
    {
        return completed;
    }

    public void setCompleted(Date completed)
    {
        this.completed = completed;
    }

    public boolean isComplete()
    {
        return complete;
    }

    public void setComplete(boolean complete)
    {
        this.complete = complete;
    }

    public double getPercentComplete()
    {
        return percentComplete;
    }

    public void setPercentComplete(double percentComplete)
    {
        this.percentComplete = percentComplete;
    }

    @Override
    public String toString()
    {
        return "OutlookTaskItem{" +
                "due=" + due +
                ", started=" + started +
                ", completed=" + completed +
                ", complete=" + complete +
                ", percentComplete=" + percentComplete +
                "} " + super.toString();
    }
}
