package com.armedia.acm.calendar.service;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Apr 6, 2017
 *
 */
public class Attendee
{

    public static enum AttendeeType
    {
        REQUIRED, OPTIONAL, RESOURCE;
    }

    public static enum ResponseStatus
    {
        NONE, DECLINED, TENTATIVE, ACCEPTED, ORGANIZER;
    }

    private String displayName;

    private String email;

    private AttendeeType type;

    private ResponseStatus status;

    /**
     * @return the displayName
     */
    public String getDisplayName()
    {
        return displayName;
    }

    /**
     * @param displayName
     *            the displayName to set
     */
    public void setDisplayName(String displayName)
    {
        this.displayName = displayName;
    }

    /**
     * @return the email
     */
    public String getEmail()
    {
        return email;
    }

    /**
     * @param email
     *            the email to set
     */
    public void setEmail(String email)
    {
        this.email = email;
    }

    /**
     * @return the type
     */
    public AttendeeType getType()
    {
        return type;
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(AttendeeType type)
    {
        this.type = type;
    }

    /**
     * @return the status
     */
    public ResponseStatus getStatus()
    {
        return status;
    }

    /**
     * @param status
     *            the status to set
     */
    public void setStatus(ResponseStatus status)
    {
        this.status = status;
    }

}
