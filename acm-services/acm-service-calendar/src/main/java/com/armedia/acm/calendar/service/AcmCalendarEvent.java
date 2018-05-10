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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Mar 28, 2017
 *
 */
@JsonInclude(Include.NON_NULL)
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class AcmCalendarEvent
{

    static final String ZONED_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ssXXX";
    private String eventId;
    private String calendarId;
    private String objectType;
    private String objectId;
    private String subject;
    private String location;
    @JsonFormat(shape = Shape.STRING, pattern = ZONED_DATE_TIME_FORMAT)
    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    @JsonDeserialize(using = ZonedDateTimeDeserializer.class)
    private ZonedDateTime start;
    @JsonFormat(shape = Shape.STRING, pattern = ZONED_DATE_TIME_FORMAT)
    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    @JsonDeserialize(using = ZonedDateTimeDeserializer.class)
    private ZonedDateTime end;
    private boolean allDayEvent;
    private RecurrenceDetails recurrenceDetails;
    private String details;
    private int remindIn = -1;
    private Sensitivity sensitivity = Sensitivity.NORMAL;
    private Priority priority = Priority.LOW;
    private boolean sendEmails;
    private List<Attendee> attendees;
    private List<AcmCalendarEventAttachment> files;

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

    /**
     * @return the location
     */
    public String getLocation()
    {
        return location;
    }

    /**
     * @param location
     *            the location to set
     */
    public void setLocation(String location)
    {
        this.location = location;
    }

    /**
     * @return the startDate
     */
    public ZonedDateTime getStart()
    {
        return start;
    }

    /**
     * @param startDate
     *            the startDate to set
     */
    public void setStart(ZonedDateTime start)
    {
        this.start = start;
    }

    /**
     * @return the endTime
     */
    public ZonedDateTime getEnd()
    {
        return end;
    }

    /**
     * @param endTime
     *            the endTime to set
     */
    public void setEnd(ZonedDateTime end)
    {
        this.end = end;
    }

    /**
     * @return the allDayEvent
     */
    public boolean isAllDayEvent()
    {
        return allDayEvent;
    }

    /**
     * @param allDayEvent
     *            the allDayEvent to set
     */
    public void setAllDayEvent(boolean allDayEvent)
    {
        this.allDayEvent = allDayEvent;
    }

    /**
     * @return the recurrenceDetails
     */
    public RecurrenceDetails getRecurrenceDetails()
    {
        return recurrenceDetails;
    }

    /**
     * @param recurrenceDetails
     *            the recurrenceDetails to set
     */
    public void setRecurrenceDetails(RecurrenceDetails recurrenceDetails)
    {
        this.recurrenceDetails = recurrenceDetails;
    }

    /**
     * @return the details
     */
    public String getDetails()
    {
        return details;
    }

    /**
     * @param details
     *            the details to set
     */
    public void setDetails(String details)
    {
        this.details = details;
    }

    /**
     * @return the remindIn
     */
    public int getRemindIn()
    {
        return remindIn;
    }

    /**
     * @param remindIn
     *            the remindIn to set
     */
    public void setRemindIn(int remindIn)
    {
        this.remindIn = remindIn;
    }

    /**
     * @return the privateEvent
     */
    public Sensitivity getSensitivity()
    {
        return sensitivity;
    }

    /**
     * @param sensitivity
     *            the sensitivity to set
     */
    public void setSensitivity(Sensitivity sensitivity)
    {
        this.sensitivity = sensitivity;
    }

    /**
     * @return the importance
     */
    public Priority getPriority()
    {
        return priority;
    }

    /**
     * @param priority
     *            the priority to set
     */
    public void setPriority(Priority priority)
    {
        this.priority = priority;
    }

    /**
     * @return the sendEmails
     */
    public boolean isSendEmails()
    {
        return sendEmails;
    }

    /**
     * @param sendEmails
     *            the sendEmails to set
     */
    public void setSendEmails(boolean sendEmails)
    {
        this.sendEmails = sendEmails;
    }

    /**
     * @return the invitees
     */
    public List<Attendee> getAttendees()
    {
        return attendees;
    }

    /**
     * @param attendees
     *            the attendees to set
     */
    public void setAttendees(List<Attendee> attendees)
    {
        this.attendees = attendees;
    }

    /**
     * @return the files
     */
    public List<AcmCalendarEventAttachment> getFiles()
    {
        return files;
    }

    /**
     * @param files
     *            the files to set
     */
    public void setFiles(List<AcmCalendarEventAttachment> files)
    {
        this.files = files;
    }

    public static enum Priority
    {
        LOW, NORMAL, HIGH
    }

    public static enum Sensitivity
    {
        CONFIDENTIAL, PRIVATE, PERSONAL, NORMAL
    }

}
