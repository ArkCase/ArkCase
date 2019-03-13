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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The configuration data in the Config Server are stored in EnvironmentRepository serving Environment objects.
 * The Environment resources are parametrized by three variables:
 * {application}, which maps to spring.application.name on the client side.
 * {profile}, which maps to spring.profiles.active on the client (comma-separated list).
 * {label}, which is a server side feature labelling a "versioned" set of config files.
 *
 */
public class Environment
{
    private String name;

    private String[] profiles;

    private String label;

    private List<PropertySource> propertySources;

    private String version;

    private String state;

    public Environment()
    {
        propertySources = new ArrayList<>();
    }

    public List<PropertySource> getPropertySources()
    {
        return propertySources;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public String[] getProfiles()
    {
        return profiles;
    }

    public void setProfiles(String[] profiles)
    {
        this.profiles = profiles;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public String getState()
    {
        return state;
    }

    public void setState(String state)
    {
        this.state = state;
    }

    @Override
    public String toString()
    {
        return "Environment [name=" + name + ", profiles=" + Arrays.asList(profiles)
                + ", label=" + label + ", propertySources=" + propertySources
                + ", version=" + version
                + ", state=" + state + "]";
    }
}
