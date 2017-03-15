package com.armedia.acm.calendar.config.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Mar 9, 2017
 *
 */
@JsonInclude(Include.NON_NULL)
public class CalendarConfiguration
{

    public static enum CalendarType
    {
        USER_BASED, SYSTEM_BASED;
    }

    public static enum CalendarPropertyKeys
    {
        CALENDAR_TYPE, SYSTEM_EMAIL, PASSWORD;
    }

    private CalendarType calendarType;

    private String systemEmail;

    private String password;

    /**
     * @return the calendarType
     */
    public CalendarType getCalendarType()
    {
        return calendarType;
    }

    /**
     * @param calendarType
     *            the calendarType to set
     */
    public void setCalendarType(CalendarType calendarType)
    {
        this.calendarType = calendarType;
    }

    /**
     * @return the systemEmail
     */
    public String getSystemEmail()
    {
        return systemEmail;
    }

    /**
     * @param systemEmail
     *            the systemEmail to set
     */
    public void setSystemEmail(String systemEmail)
    {
        this.systemEmail = systemEmail;
    }

    /**
     * @return the password
     */
    public String getPassword()
    {
        return password;
    }

    /**
     * @param password
     *            the password to set
     */
    public void setPassword(String password)
    {
        this.password = password;
    }

}
