package com.armedia.acm.configuration.core.jmx;

import com.armedia.acm.configuration.api.ConfigurationFacade;

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

import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;

@ManagedResource(objectName = "configuration:name=configuration-service,type=com.armedia.acm.configuration.ConfigurationService,artifactId=configuration-service")
public class ConfigurationFacadeJmx implements ConfigurationFacade
{
    private ConfigurationFacade configurationFacade;

    public ConfigurationFacadeJmx(ConfigurationFacade configurationFacade)
    {
        this.configurationFacade = configurationFacade;
    }

    @Override
    @ManagedOperation(description = "delivers the configuration parameters")
    @ManagedOperationParameters({
            @ManagedOperationParameter(name = "name", description = "Name of the configuration parameter")
    })
    public Object getProperty(String name)
    {
        return this.configurationFacade.getProperty(name);
    }

    @Override
    @ManagedOperation(description = "delivers the configuration parameters")
    public void refresh()
    {
        this.configurationFacade.refresh();
    }

    @Override
    @ManagedOperation(description = "delivers the configuration parameters")
    @ManagedOperationParameters({
            @ManagedOperationParameter(name = "channel", description = "Channel for configuration values, like, PK, FIL, etc"),
            @ManagedOperationParameter(name = "stage", description = "Stage of the configuration values, like, DEV, ToC, etc"),
            @ManagedOperationParameter(name = "instance", description = "instance for the configuration values"),
            @ManagedOperationParameter(name = "name", description = "Name of the configuration parameter")
    })
    public Object getProperty(String channel, String stage, String instance, String name)
    {
        return this.configurationFacade.getProperty(channel, stage, instance, name);
    }
}
