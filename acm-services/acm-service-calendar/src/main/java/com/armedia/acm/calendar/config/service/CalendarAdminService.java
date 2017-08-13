package com.armedia.acm.calendar.config.service;

import com.armedia.acm.calendar.service.CalendarService;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Mar 9, 2017
 *
 */
public interface CalendarAdminService
{

    CalendarConfigurationsByObjectType readConfiguration(boolean includePassword) throws CalendarConfigurationException;

    /**
     * Upon successful storage of the configuration, the implementation is required to raise
     * <code>CalendarConfigurationEvent</code> to inform the implementations of <code>CalendarService</code> that the
     * configuration has been updated.
     *
     * @param configuration
     * @throws CalendarConfigurationException
     *
     * @see {@link CalendarService}
     * @see CalendarConfigurationEvent
     */
    void writeConfiguration(CalendarConfigurationsByObjectType configuration) throws CalendarConfigurationException;

    <CCE extends CalendarConfigurationException> CalendarConfigurationExceptionMapper<CCE> getExceptionMapper(CCE e);

}
