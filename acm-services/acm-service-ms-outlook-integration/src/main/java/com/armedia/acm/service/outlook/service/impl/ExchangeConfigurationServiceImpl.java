package com.armedia.acm.service.outlook.service.impl;

/*-
 * #%L
 * ACM Service: MS Outlook integration
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

import com.armedia.acm.configuration.service.ConfigurationPropertyService;
import com.armedia.acm.service.outlook.model.ExchangeConfiguration;
import com.armedia.acm.service.outlook.model.OutlookConfig;
import com.armedia.acm.service.outlook.service.ExchangeConfigurationService;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.locks.Lock;

/**
 * @author sasko.tanaskoski
 */
public class ExchangeConfigurationServiceImpl implements ExchangeConfigurationService
{

    private Logger log = LogManager.getLogger(getClass());

    private OutlookConfig outlookConfig;

    private ConfigurationPropertyService configurationPropertyService;

    @Override
    public void writeConfiguration(ExchangeConfiguration configuration, Authentication auth)
    {
        outlookConfig.setIntegrationEnabled(configuration.isIntegrationEnabled());
        outlookConfig.setServerVersion(configuration.getServerVersion());
        outlookConfig.setEnableAutoDiscovery(configuration.isEnableAutodiscovery());
        outlookConfig.setClientAccessServer(configuration.getClientAccessServer());
        outlookConfig.setDefaultAccess(configuration.getDefaultAccess());
        outlookConfig.setSystemUserEmail(configuration.getSystemUserEmail());
        outlookConfig.setSystemUserPassword(configuration.getSystemUserEmailPassword());
        outlookConfig.setSystemUserId(configuration.getSystemUserId());
        configurationPropertyService.updateProperties(outlookConfig);
    }

    @Override
    public ExchangeConfiguration readConfiguration()
    {
        ExchangeConfiguration exchangeConfiguration = new ExchangeConfiguration();

        exchangeConfiguration.setIntegrationEnabled(outlookConfig.getIntegrationEnabled());
        exchangeConfiguration.setServerVersion(outlookConfig.getServerVersion());
        exchangeConfiguration.setEnableAutodiscovery(outlookConfig.getEnableAutoDiscovery());
        exchangeConfiguration.setClientAccessServer(outlookConfig.getClientAccessServer());
        exchangeConfiguration.setDefaultAccess(outlookConfig.getDefaultAccess());
        exchangeConfiguration.setSystemUserEmail(outlookConfig.getSystemUserEmail());
        exchangeConfiguration.setSystemUserEmailPassword(outlookConfig.getSystemUserPassword());
        exchangeConfiguration.setSystemUserId(outlookConfig.getSystemUserId());
        return exchangeConfiguration;
    }

    public OutlookConfig getOutlookConfig()
    {
        return outlookConfig;
    }

    public void setOutlookConfig(OutlookConfig outlookConfig)
    {
        this.outlookConfig = outlookConfig;
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
