package com.armedia.acm.calendar.config.service;

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

import com.armedia.acm.calendar.config.model.PurgeOptions;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Mar 9, 2017
 *
 */
public class CalendarConfiguration
{
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
