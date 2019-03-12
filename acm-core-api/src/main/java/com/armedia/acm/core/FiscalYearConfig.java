package com.armedia.acm.core;

/*-
 * #%L
 * ACM Core API
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
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
import org.springframework.beans.factory.annotation.Value;

public class FiscalYearConfig
{
    @JsonProperty("fiscal.year.start.day")
    @Value("${fiscal.year.start.day}")
    private Integer startDay;

    @JsonProperty("fiscal.year.start.month")
    @Value("${fiscal.year.start.month}")
    private Integer startMonth;

    @JsonProperty("fiscal.year.start.year")
    @Value("${fiscal.year.start.year}")
    private Integer startYear;

    public Integer getStartDay()
    {
        return startDay;
    }

    public void setStartDay(Integer startDay)
    {
        this.startDay = startDay;
    }

    public Integer getStartMonth()
    {
        return startMonth;
    }

    public void setStartMonth(Integer startMonth)
    {
        this.startMonth = startMonth;
    }

    public Integer getStartYear()
    {
        return startYear;
    }

    public void setStartYear(Integer startYear)
    {
        this.startYear = startYear;
    }
}
