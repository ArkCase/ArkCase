package com.armedia.acm.service.outlook.model;

import java.util.Date;

/**
 * Created by armdev on 4/20/15.
 */
public class OutlookCalendarItem extends OutlookItem
{
    private Boolean allDayEvent;
    private Boolean cancelled;
    private Boolean meeting;
    private Boolean recurring;
    private Date startDate;
    private Date endDate;

    public void setAllDayEvent(Boolean allDayEvent)
    {
        this.allDayEvent = allDayEvent;
    }

    public Boolean getAllDayEvent()
    {
        return allDayEvent;
    }

    public void setCancelled(Boolean cancelled)
    {
        this.cancelled = cancelled;
    }

    public Boolean getCancelled()
    {
        return cancelled;
    }

    public void setMeeting(Boolean meeting)
    {
        this.meeting = meeting;
    }

    public Boolean getMeeting()
    {
        return meeting;
    }

    public void setRecurring(Boolean recurring)
    {
        this.recurring = recurring;
    }

    public Boolean getRecurring()
    {
        return recurring;
    }

    public void setStartDate(Date startDate)
    {
        this.startDate = startDate;
    }

    public Date getStartDate()
    {
        return startDate;
    }

    public void setEndDate(Date endDate)
    {
        this.endDate = endDate;
    }

    public Date getEndDate()
    {
        return endDate;
    }

    @Override
    public String toString()
    {
        return "OutlookCalendarItem{" +
                "allDayEvent=" + allDayEvent +
                ", cancelled=" + cancelled +
                ", meeting=" + meeting +
                ", recurring=" + recurring +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                "} " + super.toString();
    }
}
