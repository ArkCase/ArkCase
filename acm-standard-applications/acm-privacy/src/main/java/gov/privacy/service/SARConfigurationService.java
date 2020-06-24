package gov.privacy.service;

/*-
 * #%L
 * ACM Privacy: Subject Access Request
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
import com.armedia.acm.plugins.casefile.service.SystemConfigurationService;

import gov.privacy.model.SARConfig;
import gov.privacy.model.SARConfiguration;

/**
 * @author Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on Jun, 2020
 *
 */
public class SARConfigurationService extends SystemConfigurationService
{
    private SARConfig SARConfig;
    private ConfigurationPropertyService configurationPropertyService;

    public void writeConfiguration(SARConfiguration SARConfiguration)
    {
        SARConfig.setMaxDaysInHoldQueue(SARConfiguration.getMaxDaysInHoldQueue());
        SARConfig.setDashboardBannerEnabled(SARConfiguration.getDashboardBannerEnabled());
        SARConfig.setNotificationGroupsEnabled(SARConfiguration.getNotificationGroupsEnabled());
        SARConfig.setPurgeRequestWhenInHoldEnabled(SARConfiguration.getPurgeRequestWhenInHoldEnabled());
        SARConfig.setLimitedDeliveryToSpecificPageCountEnabled(SARConfiguration.getLimitedDeliveryToSpecificPageCountEnabled());
        SARConfig.setLimitedDeliveryToSpecificPageCount(SARConfiguration.getLimitedDeliveryToSpecificPageCount());
        SARConfig.setProvideReasonToHoldRequestEnabled(SARConfiguration.getProvideReasonToHoldRequestEnabled());

        configurationPropertyService.updateProperties(SARConfig);
    }

    @Override
    public SARConfiguration readConfiguration()
    {
        SARConfiguration SARConfiguration = new SARConfiguration();
        SARConfiguration.setMaxDaysInHoldQueue(SARConfig.getMaxDaysInHoldQueue());
        SARConfiguration.setDashboardBannerEnabled(SARConfig.getDashboardBannerEnabled());
        SARConfiguration.setNotificationGroupsEnabled(SARConfig.getNotificationGroupsEnabled());
        SARConfiguration.setPurgeRequestWhenInHoldEnabled(SARConfig.getPurgeRequestWhenInHoldEnabled());
        SARConfiguration.setLimitedDeliveryToSpecificPageCountEnabled(SARConfig.getLimitedDeliveryToSpecificPageCountEnabled());
        SARConfiguration.setLimitedDeliveryToSpecificPageCount(SARConfig.getLimitedDeliveryToSpecificPageCount());
        SARConfiguration.setProvideReasonToHoldRequestEnabled(SARConfig.getProvideReasonToHoldRequestEnabled());

        return SARConfiguration;
    }

    public SARConfig getSARConfig()
    {
        return SARConfig;
    }

    public void setSARConfig(SARConfig SARConfig)
    {
        this.SARConfig = SARConfig;
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
