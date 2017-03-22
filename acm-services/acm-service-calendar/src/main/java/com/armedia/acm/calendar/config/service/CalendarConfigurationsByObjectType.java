package com.armedia.acm.calendar.config.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.util.Map;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Mar 16, 2017
 *
 */
@JsonInclude(Include.NON_NULL)
public class CalendarConfigurationsByObjectType
{

    private Map<String, CalendarConfiguration> configurationsByType;

    /**
     * @return the configurationsByType
     */
    public Map<String, CalendarConfiguration> getConfigurationsByType()
    {
        return configurationsByType;
    }

    /**
     * @param configurationsByType
     *            the configurationsByType to set
     */
    public void setConfigurationsByType(Map<String, CalendarConfiguration> configurationsByType)
    {
        this.configurationsByType = configurationsByType;
    }

    /**
     * @param objectType
     * @return
     */
    public CalendarConfiguration getConfiguration(String objectType)
    {
        return configurationsByType.get(objectType);
    }

}
