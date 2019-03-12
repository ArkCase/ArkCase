package com.armedia.acm.audit.model;

/*-
 * #%L
 * ACM Service: Audit Library
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

import com.armedia.acm.objectonverter.json.JSONUnmarshaller;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

public class AuditEventConfig implements InitializingBean
{
    @JsonProperty("auditEvent.eventTypes")
    @Value("${auditEvent.eventTypes}")
    private String eventTypesMappingString;

    @JsonProperty("auditEvent.eventName.insert")
    @Value("${auditEvent.eventName.insert:false}")
    private Boolean eventNameInsert;

    private JSONUnmarshaller jsonUnmarshaller;

    private Map<String, String> eventTypes = new HashMap<>();

    public String getEventTypesMappingString()
    {
        return eventTypesMappingString;
    }

    public void setEventTypesMappingString(String eventTypesMappingString)
    {
        this.eventTypesMappingString = eventTypesMappingString;
    }

    public Boolean getEventNameInsert()
    {
        return eventNameInsert;
    }

    public void setEventNameInsert(Boolean eventNameInsert)
    {
        this.eventNameInsert = eventNameInsert;
    }

    @JsonIgnore
    public Map<String, String> getEventTypes()
    {
        return eventTypes;
    }

    @Override
    public void afterPropertiesSet()
    {
        eventTypes = jsonUnmarshaller.unmarshall(eventTypesMappingString, Map.class);
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
}
