package com.armedia.acm.services.holiday.model;

/*-
 * #%L
 * ACM Service: Holiday
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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

import com.armedia.acm.configuration.annotations.MapValue;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

public class HolidayConfigurationProps
{
    @JsonProperty("holidayConfiguration.includeWeekends")
    @Value("${holidayConfiguration.includeWeekends}")
    private Boolean includeWeekends;

    public Boolean getIncludeWeekends()
    {
        return includeWeekends;
    }

    public void setIncludeWeekends(Boolean includeWeekends)
    {
        this.includeWeekends = includeWeekends;
    }

    @JsonProperty("holidayConfiguration.holidays")
    private Map<String, String> holidays = new HashMap<>();

    @MapValue(value = "holidayConfiguration.holidays")
    public Map<String, String> getHolidays()
    {
        return holidays;
    }

    public void setHolidays(Map<String, String> holidays)
    {
        this.holidays = holidays;
    }
}
