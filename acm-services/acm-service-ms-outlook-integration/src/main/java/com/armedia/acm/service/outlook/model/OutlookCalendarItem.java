package com.armedia.acm.service.outlook.model;

import java.util.Date;
import java.util.TimeZone;

/**
 * Created by armdev on 4/20/15.
 */
public class OutlookCalendarItem extends OutlookItem
{
    private Boolean allDayEvent;
    private Boolean cancelled;
    private Boolean meeting;
    private Boolean recurring;
    private int recurringInterval;
    private Date startDate;
    private TimeZone timeZone;
    private Date endDate;
    private Date recurringEndDate;

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

    public Date getRecurringEndDate() {
        return recurringEndDate;
    }

    public void setRecurringEndDate(Date recurringEndDate) {
        this.recurringEndDate = recurringEndDate;
    }

    public TimeZone getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    public int getRecurringInterval() {
        return recurringInterval;
    }

    public void setRecurringInterval(int recurringInterval) {
        this.recurringInterval = recurringInterval;
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
                ", recurringEndDate=" + recurringEndDate +
                "} " + super.toString();
    }
}
