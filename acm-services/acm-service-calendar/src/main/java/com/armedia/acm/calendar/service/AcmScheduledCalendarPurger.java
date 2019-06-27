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

import com.armedia.acm.calendar.config.service.CalendarAdminService;
import com.armedia.acm.calendar.config.service.CalendarConfiguration;
import com.armedia.acm.calendar.config.service.CalendarConfigurationException;
import com.armedia.acm.calendar.config.service.CalendarConfigurationsByObjectType;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.Map;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jul 18, 2017
 *
 */
public class AcmScheduledCalendarPurger
{

    private final Logger log = LogManager.getLogger(getClass());

    private CalendarAdminService calendarAdminService;

    private CalendarService calendarService;

    /**
     * @param calendarAdminService
     *            the calendarAdminService to set
     */
    public void setCalendarAdminService(CalendarAdminService calendarAdminService)
    {
        this.calendarAdminService = calendarAdminService;
    }

    /**
     * @param calendarService
     *            the calendarService to set
     */
    public void setCalendarService(CalendarService calendarService)
    {
        this.calendarService = calendarService;
    }

    public void executeTask()
    {
        try
        {
            CalendarConfigurationsByObjectType configurations = calendarAdminService.readConfiguration(false);
            Map<String, CalendarConfiguration> configurationsByType = configurations.getConfigurationsByType();
            configurationsByType.forEach((k, v) -> {
                log.debug("Purging calendar events for [{}].", k);
                try
                {
                    calendarService.purgeEvents(k, v);
                }
                catch (CalendarServiceException e)
                {
                    log.error("Could not purge calendars for [{}].", k, e);
                }
            });

        }
        catch (CalendarConfigurationException e)
        {
            log.error("Could not load configuration for [{}].", getClass().getName(), e);
        }

    }

}
