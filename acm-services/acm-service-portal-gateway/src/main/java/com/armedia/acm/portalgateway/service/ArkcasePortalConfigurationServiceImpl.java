package com.armedia.acm.portalgateway.service;

/*-
 * #%L
 * ACM Service: Portal Gateway Service
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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

import com.armedia.acm.configuration.service.ConfigurationPropertyService;
import com.armedia.acm.portalgateway.model.ArkcasePortalConfig;

/**
 * Created by ana.serafimoska
 */
public class ArkcasePortalConfigurationServiceImpl implements ArkcasePortalConfigurationService
{
    private ArkcasePortalConfig arkcasePortalConfig;

    private ConfigurationPropertyService configurationPropertyService;

    @Override
    public void writeConfiguration(ArkcasePortalConfig properties)
    {
        getConfigurationPropertyService().updateProperties(properties);
    }

    @Override
    public ArkcasePortalConfig getPortalConfiguration()
    {
        return arkcasePortalConfig;
    }

    public ArkcasePortalConfig getArkcasePortalConfig()
    {
        return arkcasePortalConfig;
    }

    public void setArkcasePortalConfig(ArkcasePortalConfig arkcasePortalConfig)
    {
        this.arkcasePortalConfig = arkcasePortalConfig;
    }

    public ConfigurationPropertyService getConfigurationPropertyService()
    {
        return configurationPropertyService;
    }

    public void setConfigurationPropertyService(ConfigurationPropertyService configurationPropertyService)
    {
        this.configurationPropertyService = configurationPropertyService;
    }

}
