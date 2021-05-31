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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.springframework.beans.factory.annotation.Value;

/**
 * Created by Aleksandar Acevski <aleksandar.acevski@armedia.com> on December, 2020
 */
@JsonSerialize(as = BusinessHoursConfig.class)
public class BusinessHoursConfig
{

    @JsonProperty("businessHours.endOfBusinessDayEnabled")
    @Value("${businessHours.endOfBusinessDayEnabled}")
    private Boolean endOfBusinessDayEnabled;

    @JsonProperty("businessHours.endOfBusinessDayTime")
    @Value("${businessHours.endOfBusinessDayTime}")
    private String endOfBusinessDayTime;

    @JsonProperty("businessHours.defaultDueDateGap")
    @Value("${businessHours.defaultDueDateGap}")
    private int defaultDueDateGap;

    public Boolean getEndOfBusinessDayEnabled()
    {
        return endOfBusinessDayEnabled;
    }

    public void setEndOfBusinessDayEnabled(Boolean endOfBusinessDayEnabled)
    {
        this.endOfBusinessDayEnabled = endOfBusinessDayEnabled;
    }

    public String getEndOfBusinessDayTime()
    {
        return endOfBusinessDayTime;
    }

    public void setEndOfBusinessDayTime(String endOfBusinessDayTime)
    {
        this.endOfBusinessDayTime = endOfBusinessDayTime;
    }

    public int getDefaultDueDateGap()
    {
        return defaultDueDateGap;
    }

    public void setDefaultDueDateGap(int defaultDueDateGap)
    {
        this.defaultDueDateGap = defaultDueDateGap;
    }
}
