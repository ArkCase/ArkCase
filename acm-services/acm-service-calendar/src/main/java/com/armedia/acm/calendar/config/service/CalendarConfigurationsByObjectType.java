package com.armedia.acm.calendar.config.service;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Mar 16, 2017
 *
 */
@JsonInclude(Include.NON_NULL)
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class CalendarConfigurationsByObjectType
{

    private Map<String, CalendarConfiguration> configurationsByType = new HashMap<>();

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
        return configurationsByType.getOrDefault(objectType, new CalendarConfiguration());
    }

}
