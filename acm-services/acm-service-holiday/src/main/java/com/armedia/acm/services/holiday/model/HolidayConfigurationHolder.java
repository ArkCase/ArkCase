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

import com.armedia.acm.objectonverter.json.JSONUnmarshaller;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.json.JSONObject;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

import java.util.Objects;

@JsonSerialize(as = HolidayConfigurationHolder.class)
public class HolidayConfigurationHolder implements InitializingBean
{
    @JsonProperty("holidayJsonConfiguration")
    @Value("${holidayJsonConfiguration}")
    private String holidayJsonConfiguration;

    private JSONUnmarshaller jsonUnmarshaller;

    private HolidayConfiguration holidayConfiguration;

    @Override
    public void afterPropertiesSet()
    {
        holidayConfiguration = jsonUnmarshaller.unmarshall(holidayJsonConfiguration, HolidayConfiguration.class);
    }

    public String getHolidayJsonConfiguration()
    {
        return Objects.nonNull(holidayConfiguration) ? new JSONObject(holidayConfiguration).toString() : "{}";
    }

    public void setHolidayJsonConfiguration(String holidayJsonConfiguration)
    {
        this.holidayJsonConfiguration = holidayJsonConfiguration;
    }

    @JsonIgnore
    public JSONUnmarshaller getJsonUnmarshaller()
    {
        return jsonUnmarshaller;
    }

    public void setJsonUnmarshaller(JSONUnmarshaller jsonUnmarshaller)
    {
        this.jsonUnmarshaller = jsonUnmarshaller;
    }

    @JsonIgnore
    public HolidayConfiguration getHolidayConfiguration()
    {
        return holidayConfiguration;
    }

    public void setHolidayConfiguration(HolidayConfiguration holidayConfiguration)
    {
        this.holidayConfiguration = holidayConfiguration;
    }
}
