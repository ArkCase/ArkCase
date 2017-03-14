package com.armedia.acm.plugins.outlook.service;

import com.armedia.acm.plugins.outlook.web.api.CalendarConfiguration;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Mar 9, 2017
 *
 */
public interface CalendarAdminService
{

    CalendarConfiguration readConfiguration();

    void writeConfiguration(CalendarConfiguration configuration) throws CalendarConfigurationException;

}
