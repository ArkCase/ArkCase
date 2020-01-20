package com.armedia.acm.configuration.api.environment;

/*-
 * #%L
 * configuration-api
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

import java.util.Map;

/**
 * Configuration properties for specific {@link Environment}
 */
public class PropertySource
{
    private String name;

    private Map<String, Object> source;

    public PropertySource()
    {
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Map<String, Object> getSource()
    {
        return source;
    }

    public void setSource(Map<String, Object> source)
    {
        this.source = source;
    }

    /**
     * @return extract the name of the application source from url.
     *
     *         ex. file:C://Users//mario.gjurcheski/.arkcase/acm/acm-config-server-repo/arkcase.yaml -> arkase
     */
    public String extractApplicationNameFromSourceName()
    {
        return name.substring(name.lastIndexOf('/') + 1, name.length() - 5);
    }

    @Override
    public String toString()
    {
        return "PropertySource [name=" + name + "]";
    }
}
