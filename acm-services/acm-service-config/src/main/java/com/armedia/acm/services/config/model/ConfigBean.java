package com.armedia.acm.services.config.model;

/*-
 * #%L
 * ACM Service: Config
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

import com.armedia.acm.objectonverter.json.JSONMarshaller;

import org.springframework.aop.support.AopUtils;

/**
 * Class to represent all configuration beans in application which
 * properties needs to be exposed to the UI.
 *
 * @see com.armedia.acm.plugins.casefile.model.CaseFileConfig
 * @see com.armedia.acm.plugins.complaint.model.ComplaintConfig
 */
public class ConfigBean implements AcmConfig
{
    private String configName;

    private Object config;

    private String description;

    private JSONMarshaller jsonMarshaller;

    @Override
    public String getConfigName()
    {
        return configName;
    }

    @Override
    public String getConfigAsJson()
    {
        return jsonMarshaller.marshal(config, AopUtils.getTargetClass(config));
    }

    @Override
    public String getConfigDescription()
    {
        return description;
    }

    public void setConfigName(String configName)
    {
        this.configName = configName;
    }

    public void setConfig(Object config)
    {
        this.config = config;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public JSONMarshaller getJsonMarshaller()
    {
        return jsonMarshaller;
    }

    public void setJsonMarshaller(JSONMarshaller jsonMarshaller)
    {
        this.jsonMarshaller = jsonMarshaller;
    }
}
