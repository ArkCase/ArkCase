package com.armedia.acm.service.outlook.model;

import java.util.Date;
import java.util.TimeZone;

import microsoft.exchange.webservices.data.property.definition.ExtendedPropertyDefinition;

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
    private String folderId;
    private ExtendedPropertyDefinition extendedPropertyDefinition;
    private Object extendedPropertyValue;

    public Boolean getAllDayEvent()
    {
        return allDayEvent;
    }

    public void setAllDayEvent(Boolean allDayEvent)
    {
        this.allDayEvent = allDayEvent;
    }

    public Boolean getCancelled()
    {
        return cancelled;
    }

    public void setCancelled(Boolean cancelled)
    {
        this.cancelled = cancelled;
    }

    public Boolean getMeeting()
    {
        return meeting;
    }

    public void setMeeting(Boolean meeting)
    {
        this.meeting = meeting;
    }

    public Boolean getRecurring()
    {
        return recurring;
    }

    public void setRecurring(Boolean recurring)
    {
        this.recurring = recurring;
    }

    public Date getStartDate()
    {
        return startDate;
    }

    public void setStartDate(Date startDate)
    {
        this.startDate = startDate;
    }

    public Date getEndDate()
    {
        return endDate;
    }

    public void setEndDate(Date endDate)
    {
        this.endDate = endDate;
    }

    public Date getRecurringEndDate()
    {
        return recurringEndDate;
    }

    public void setRecurringEndDate(Date recurringEndDate)
    {
        this.recurringEndDate = recurringEndDate;
    }

    public TimeZone getTimeZone()
    {
        return timeZone;
    }

    public void setTimeZone(TimeZone timeZone)
    {
        this.timeZone = timeZone;
    }

    public int getRecurringInterval()
    {
        return recurringInterval;
    }

    public void setRecurringInterval(int recurringInterval)
    {
        this.recurringInterval = recurringInterval;
    }

    public String getFolderId()
    {
        return folderId;
    }

    public void setFolderId(String folderId)
    {
        this.folderId = folderId;
    }

    public ExtendedPropertyDefinition getExtendedPropertyDefinition()
    {
        return extendedPropertyDefinition;
    }

    public void setExtendedPropertyDefinition(
            ExtendedPropertyDefinition extendedPropertyDefinition)
    {
        this.extendedPropertyDefinition = extendedPropertyDefinition;
    }

    public Object getExtendedPropertyValue()
    {
        return extendedPropertyValue;
    }

    public void setExtendedPropertyValue(Object extendedPropertyValue)
    {
        this.extendedPropertyValue = extendedPropertyValue;
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
                ", folderId=" + folderId +
                "} " + super.toString();
    }
}
