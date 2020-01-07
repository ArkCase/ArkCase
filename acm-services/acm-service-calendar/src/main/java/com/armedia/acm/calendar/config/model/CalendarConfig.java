package com.armedia.acm.calendar.config.model;

/*-
 * #%L
 * ACM Service: Calendar Service
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

import com.armedia.acm.calendar.config.service.CalendarConfiguration;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.json.JSONObject;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CalendarConfig implements InitializingBean
{
    @JsonProperty("calendar.configured_object_types")
    @Value("${calendar.configured_object_types:''}")
    private String configuredObjectTypesString;

    @JsonProperty("calendar.configuration")
    @Value("${calendar.configuration:''}")
    private String configurationByObjectTypeString;

    private Map<String, CalendarConfiguration> configurationsByObjectType;

    private ObjectConverter objectConverter;

    public String getConfiguredObjectTypesString()
    {
        return configuredObjectTypesString;
    }

    public void setConfiguredObjectTypesString(String configuredObjectTypesString)
    {
        this.configuredObjectTypesString = configuredObjectTypesString;
    }

    public String getConfigurationByObjectTypeString()
    {
        return objectConverter.getJsonMarshaller().marshal(configurationsByObjectType, Map.class);
    }

    public void setConfigurationByObjectTypeString(String configurationByObjectTypeString)
    {
        this.configurationByObjectTypeString = configurationByObjectTypeString;
    }

    @JsonIgnore
    public ObjectConverter getObjectConverter()
    {
        return objectConverter;
    }

    public void setObjectConverter(ObjectConverter objectConverter)
    {
        this.objectConverter = objectConverter;
    }

    @Override
    public void afterPropertiesSet()
    {
        JSONObject jsonObject = new JSONObject(configurationByObjectTypeString);
        configurationsByObjectType = new HashMap<>();
        jsonObject.keySet().forEach(key -> {
            String keyString = key.toString();
            JSONObject object = jsonObject.getJSONObject(keyString);
            CalendarConfiguration calendarConfiguration = objectConverter.getJsonUnmarshaller().unmarshall(object.toString(), CalendarConfiguration.class);
            configurationsByObjectType.put(keyString, calendarConfiguration);
        });
    }

    @JsonIgnore
    public Map<String, CalendarConfiguration> getConfigurationsByObjectType()
    {
        return configurationsByObjectType;
    }

    public void setConfigurationsByObjectType(Map<String, CalendarConfiguration> configurationsByObjectType)
    {
        this.configurationsByObjectType = configurationsByObjectType;
    }

    @JsonIgnore
    public List<String> getObjectTypes()
    {
        return Arrays.stream(configuredObjectTypesString.split(",")).collect(Collectors.toList());
    }
}
