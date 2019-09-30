package com.armedia.acm.configuration.core.propertysource;

/*-
 * #%L
 * configuration-core
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

import com.armedia.acm.configuration.core.LabelsConfiguration;

import org.springframework.core.env.PropertySource;

public class LabelsConfigServerPropertyResource extends PropertySource<LabelsConfiguration>
{
    private final static String CONFIGURATION_SERVER_SOURCE_NAME = "labels-configuration-server";

    public LabelsConfigServerPropertyResource(String name, LabelsConfiguration source)
    {
        super(name, source);
    }

    public LabelsConfigServerPropertyResource(String name)
    {
        super(name);
    }

    public LabelsConfigServerPropertyResource(LabelsConfiguration source)
    {
        super(CONFIGURATION_SERVER_SOURCE_NAME, source);
    }

    @Override
    public Object getProperty(String name)
    {
        return source.getProperty(name);
    }
}
