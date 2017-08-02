package com.armedia.acm.calendar.service;

import com.armedia.acm.calendar.config.service.CalendarAdminService;
import com.armedia.acm.calendar.config.service.CalendarConfiguration;
import com.armedia.acm.calendar.config.service.CalendarConfigurationException;
import com.armedia.acm.calendar.config.service.CalendarConfigurationsByObjectType;
import com.armedia.acm.scheduler.AcmSchedulableBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jul 18, 2017
 *
 */
public class AcmScheduledCalendarPurger implements AcmSchedulableBean
{

    private final Logger log = LoggerFactory.getLogger(getClass());

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

    /*
     * (non-Javadoc)
     *
     * @see com.armedia.acm.scheduler.AcmSchedulableBean#executeTask()
     */
    @Override
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
                } catch (CalendarServiceException e)
                {
                    log.error("Could not purge calendars for [{}].", k, e);
                }
            });

        } catch (CalendarConfigurationException e)
        {
            log.error("Could not load configuration for [{}].", getClass().getName(), e);
        }

    }

}
