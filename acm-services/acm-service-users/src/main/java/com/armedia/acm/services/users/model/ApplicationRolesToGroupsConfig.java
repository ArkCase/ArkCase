package com.armedia.acm.services.users.model;

/*-
 * #%L
 * ACM Service: Users
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

import org.json.JSONObject;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

public class ApplicationRolesToGroupsConfig implements InitializingBean
{
    @JsonProperty("application.rolesToGroups")
    @Value("${application.rolesToGroups}")
    private String rolesToGroupsString;

    private Map<String, String> rolesToGroups = new HashMap<>();

    private JSONUnmarshaller jsonUnmarshaller;

    @JsonIgnore
    public Map<String, String> getRolesToGroups()
    {
        return rolesToGroups;
    }

    public void setRolesToGroups(Map<String, String> rolesToGroups)
    {
        this.rolesToGroups = rolesToGroups;
    }

    public String getRolesToGroupsString()
    {
        return new JSONObject(rolesToGroups).toString();
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

    @Override
    public void afterPropertiesSet()
    {
        rolesToGroups = jsonUnmarshaller.unmarshall(rolesToGroupsString, Map.class);
    }
}
