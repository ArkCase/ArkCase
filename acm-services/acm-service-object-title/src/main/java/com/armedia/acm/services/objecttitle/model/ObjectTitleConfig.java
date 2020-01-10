package com.armedia.acm.services.objecttitle.model;

/*-
 * #%L
 * ACM Default Plugin: admin
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
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.json.JSONObject;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

@JsonSerialize(as = ObjectTitleConfig.class)
public class ObjectTitleConfig implements InitializingBean
{
    @JsonProperty("objectTitleConfig")
    @Value("${objectTitleConfig}")
    private String objectTitleTypesString;

    private Map<String, TitleConfiguration> objectTitleTypes = new HashMap<>();

    private JSONUnmarshaller jsonUnmarshaller;

    public String getObjectTitleTypesString()
    {
        return new JSONObject(objectTitleTypes).toString();
    }

    public void setObjectTitleTypesString(String objectTitleTypesString)
    {
        this.objectTitleTypesString = objectTitleTypesString;
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
        JSONObject jsonObject = new JSONObject(objectTitleTypesString);
        jsonObject.keySet().forEach(key -> {
            String keyString = key.toString();
            JSONObject object = jsonObject.getJSONObject(keyString);
            TitleConfiguration titleConfiguration = jsonUnmarshaller.unmarshall(object.toString(), TitleConfiguration.class);
            objectTitleTypes.put(keyString, titleConfiguration);
        });
    }

    @JsonIgnore
    public Map<String, TitleConfiguration> getObjectTitleTypes()
    {
        return objectTitleTypes;
    }

    public void setObjectTitleTypes(Map<String, TitleConfiguration> objectTitleTypes)
    {
        this.objectTitleTypes = objectTitleTypes;
    }

}
