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

    public static enum CalendarPropertyKeys
    {
        INTEGRATION_ENABLED, SYSTEM_EMAIL, PASSWORD, PURGE_OPTION, DAYS_CLOSED;
    }

    public static enum PurgeOptions
    {
        RETAIN_INDEFINITELY, CLOSED, CLOSED_X_DAYS;
    }

    private boolean integrationEnabled;

    private String systemEmail;

    private String password;

    private PurgeOptions purgeOptions = PurgeOptions.RETAIN_INDEFINITELY;

    private Integer daysClosed;

    /**
     * @return the integrationEnabled
     */
    public boolean isIntegrationEnabled()
    {
        return integrationEnabled;
    }

    /**
     * @param integrationEnabled
     *            the integrationEnabled to set
     */
    public void setIntegrationEnabled(boolean integrationEnabled)
    {
        this.integrationEnabled = integrationEnabled;
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

    /**
     * @return the purgeOptions
     */
    public PurgeOptions getPurgeOptions()
    {
        return purgeOptions;
    }

    /**
     * @param purgeOptions
     *            the purgeOptions to set
     */
    public void setPurgeOptions(PurgeOptions purgeOptions)
    {
        this.purgeOptions = purgeOptions;
    }

    /**
     * @return the daysClosed
     */
    public Integer getDaysClosed()
    {
        return daysClosed;
    }

    /**
     * @param daysClosed
     *            the daysClosed to set
     */
    public void setDaysClosed(Integer daysClosed)
    {
        this.daysClosed = daysClosed;
    }

}
