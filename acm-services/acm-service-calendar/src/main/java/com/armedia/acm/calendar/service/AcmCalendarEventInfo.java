package com.armedia.acm.calendar.service;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Apr 4, 2017
 *
 */
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
