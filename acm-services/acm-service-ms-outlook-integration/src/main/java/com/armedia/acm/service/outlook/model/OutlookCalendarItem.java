package com.armedia.acm.service.outlook.model;

/*-
 * #%L
 * ACM Service: MS Outlook integration
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

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
