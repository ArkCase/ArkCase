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

import java.util.HashMap;
import java.util.Map;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Mar 16, 2017
 *
 */
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
