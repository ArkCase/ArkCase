package com.armedia.acm.calendar.config.service;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Mar 9, 2017
 *
 */
public interface CalendarAdminService
{

    CalendarConfigurationsByObjectType readConfiguration(boolean includePassword) throws CalendarConfigurationException;

    void writeConfiguration(CalendarConfigurationsByObjectType configuration) throws CalendarConfigurationException;

}
