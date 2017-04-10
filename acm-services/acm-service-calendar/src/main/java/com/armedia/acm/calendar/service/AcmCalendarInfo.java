package com.armedia.acm.calendar.service;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import java.io.Serializable;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Mar 29, 2017
 *
 */
@Entity
@Table(name = "acm_calendar")
public class AcmCalendarInfo implements Serializable
{

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "cm_calendar_id")
    private String calendarId;

    @Column(name = "cm_object_type")
    private String objectType;

    @Column(name = "cm_object_id")
    private String objectId;

    @Column(name = "cm_name")
    private String name;

    @Column(name = "cm_description")
    private String description;

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
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * @return the description
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * @param description
     *            the description to set
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

}
