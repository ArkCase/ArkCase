package com.armedia.acm.service.outlook.model;

import java.util.Date;

/**
 * Created by armdev on 4/20/15.
 */
public class OutlookTaskItem extends OutlookItem
{
    private Date dueDate;
    private Date startDate;
    private Date completeDate;
    private boolean complete;
    private double percentComplete;

    public Date getDueDate()
    {
        return dueDate;
    }

    public void setDueDate(Date dueDate)
    {
        this.dueDate = dueDate;
    }

    public Date getStartDate()
    {
        return startDate;
    }

    public void setStartDate(Date startDate)
    {
        this.startDate = startDate;
    }

    public Date getCompleteDate()
    {
        return completeDate;
    }

    public void setCompleteDate(Date completeDate)
    {
        this.completeDate = completeDate;
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
                "dueDate=" + dueDate +
                ", startDate=" + startDate +
                ", completeDate=" + completeDate +
                ", complete=" + complete +
                ", percentComplete=" + percentComplete +
                "} " + super.toString();
    }
}
