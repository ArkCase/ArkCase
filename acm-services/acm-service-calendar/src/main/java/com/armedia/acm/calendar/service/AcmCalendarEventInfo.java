package com.armedia.acm.calendar.service;

/*-
 * #%L
 * ACM Service: Calendar Service
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

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Apr 4, 2017
 *
 */
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class AcmCalendarEventInfo
{

    private String eventId;

    private String calendarId;

    private String creatorId;

    private String objectType;

    private String objectId;

    private String subject;

    /**
     * @return the eventId
     */
    public String getEventId()
    {
        return eventId;
    }

    /**
     * @param eventId
     *            the eventId to set
     */
    public void setEventId(String eventId)
    {
        this.eventId = eventId;
    }

    /**
     * @return the calendarId
     */
    public String getCalendarId()
    {
        return calendarId;
    }

    /**
     * @param calendarId
     *            the calendarId to set
     */
    public void setCalendarId(String calendarId)
    {
        this.calendarId = calendarId;
    }

    /**
     * @return the creatorId
     */
    public String getCreatorId()
    {
        return creatorId;
    }

    /**
     * @param creatorId
     *            the creatorId to set
     */
    public void setCreatorId(String creatorId)
    {
        this.creatorId = creatorId;
    }

    /**
     * @return the objectType
     */
    public String getObjectType()
    {
        return objectType;
    }

    /**
     * @param objectType
     *            the objectType to set
     */
    public void setObjectType(String objectType)
    {
        this.objectType = objectType;
    }

    /**
     * @return the objectId
     */
    public String getObjectId()
    {
        return objectId;
    }

    /**
     * @param objectId
     *            the objectId to set
     */
    public void setObjectId(String objectId)
    {
        this.objectId = objectId;
    }

    /**
     * @return the subject
     */
    public String getSubject()
    {
        return subject;
    }

    /**
     * @param subject
     *            the subject to set
     */
    public void setSubject(String subject)
    {
        this.subject = subject;
    }

}
