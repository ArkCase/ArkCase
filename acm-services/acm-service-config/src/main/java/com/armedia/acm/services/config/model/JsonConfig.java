package com.armedia.acm.services.config.model;

/*-
 * #%L
 * ACM Service: Config
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

import java.io.Serializable;

/**
 * This class implement AcmConfig interface.
 * It is used for reading json files under .acm/lookups folder
 *
 * Created by Bojan Mickoski on 26-Nov-15.
 */
public class JsonConfig implements AcmConfig, Serializable
{
    private String configName;
    private String configDescription;
    private String json;

    @Override
    public String getConfigAsJson()
    {
        return getJson();
    }

    @Override
    public String getConfigName()
    {
        return configName;
    }

    public void setConfigName(String configName)
    {
        this.configName = configName;
    }

    @Override
    public String getConfigDescription()
    {
        return configDescription;
    }

    public void setConfigDescription(String configDescription)
    {
        this.configDescription = configDescription;
    }

    public String getJson()
    {
        return json;
    }

    public void setJson(String json)
    {
        this.json = json;
    }
}
