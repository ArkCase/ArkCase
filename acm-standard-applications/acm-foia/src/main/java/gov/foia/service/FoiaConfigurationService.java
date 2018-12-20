package gov.foia.service;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
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

import com.armedia.acm.files.ConfigurationFileChangedEvent;
import com.armedia.acm.files.propertymanager.PropertyFileManager;
import com.armedia.acm.plugins.casefile.service.SystemConfigurationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import gov.foia.model.FoiaConfiguration;
import gov.foia.model.FoiaConfigurationConstants;

public class FoiaConfigurationService extends SystemConfigurationService implements ApplicationListener<ConfigurationFileChangedEvent>
{
    private PropertyFileManager propertyFileManager;
    private String propertiesFile;
    private Logger log = LoggerFactory.getLogger(getClass());
    private Map<String, String> foiaProperties = new HashMap<>();

    public void initBean()
    {
        try
        {
            foiaProperties = getPropertyFileManager().readFromFileAsMap((new File(getPropertiesFile())));
        }
        catch (IOException e)
        {
            log.error("Could not read properties file [{}]", getPropertiesFile());
        }
    }

    public void writeConfiguration(FoiaConfiguration foiaConfiguration)
    {
        Map<String, String> properties = new HashMap<>();
        properties.put(FoiaConfigurationConstants.MAX_DAYS_IN_BILLING_QUEUE, foiaConfiguration.getMaxDaysInBillingQueue().toString());
        properties.put(FoiaConfigurationConstants.PURGE_REQUEST_ENABLED, foiaConfiguration.getPurgeRequestWhenInHoldEnabled().toString());
        properties.put(FoiaConfigurationConstants.MAX_DAYS_IN_HOLD_QUEUE, foiaConfiguration.getMaxDaysInHoldQueue().toString());
        properties.put(FoiaConfigurationConstants.HOLDED_AND_APPEALED_REQUESTS,
                foiaConfiguration.getHoldedAndAppealedRequestsDueDateUpdateEnabled().toString());
        properties.put(FoiaConfigurationConstants.REQUEST_EXTENSION_WORKING_DAYS_ENABLED,
                foiaConfiguration.getRequestExtensionWorkingDaysEnabled().toString());
        properties.put(FoiaConfigurationConstants.EXTENSTION_WORKING_DAYS, foiaConfiguration.getRequestExtensionWorkingDays().toString());
        properties.put(FoiaConfigurationConstants.DASHBOARD_BANNER_ENABLED, foiaConfiguration.getDashboardBannerEnabled().toString());
        properties.put(FoiaConfigurationConstants.RECEIVED_DATE_ENABLED, foiaConfiguration.getReceivedDateEnabled().toString());
        properties.put(FoiaConfigurationConstants.NOTIFICATION_GROUPS_ENABLED, foiaConfiguration.getNotificationGroupsEnabled().toString());

        getPropertyFileManager().storeMultiple(properties, getPropertiesFile(), true);
        foiaProperties = properties;
    }

    @Override
    public FoiaConfiguration readConfiguration()
    {
        FoiaConfiguration foiaConfiguration = new FoiaConfiguration();
        for (String property : foiaProperties.keySet())
        {
            switch (property)
            {
            case FoiaConfigurationConstants.MAX_DAYS_IN_BILLING_QUEUE:
                foiaConfiguration.setMaxDaysInBillingQueue(Integer.valueOf(foiaProperties.get(property)));
                break;
            case FoiaConfigurationConstants.MAX_DAYS_IN_HOLD_QUEUE:
                foiaConfiguration.setMaxDaysInHoldQueue(Integer.valueOf(foiaProperties.get(property)));
                break;
            case FoiaConfigurationConstants.HOLDED_AND_APPEALED_REQUESTS:
                foiaConfiguration.setHoldedAndAppealedRequestsDueDateUpdateEnabled(Boolean.valueOf(foiaProperties.get(property)));
                break;
            case FoiaConfigurationConstants.EXTENSTION_WORKING_DAYS:
                foiaConfiguration.setRequestExtensionWorkingDays(Integer.valueOf(foiaProperties.get(property)));
                break;
            case FoiaConfigurationConstants.DASHBOARD_BANNER_ENABLED:
                foiaConfiguration.setDashboardBannerEnabled(Boolean.valueOf(foiaProperties.get(property)));
                break;
            case FoiaConfigurationConstants.RECEIVED_DATE_ENABLED:
                foiaConfiguration.setReceivedDateEnabled(Boolean.valueOf(foiaProperties.get(property)));
                break;
            case FoiaConfigurationConstants.NOTIFICATION_GROUPS_ENABLED:
                foiaConfiguration.setNotificationGroupsEnabled(Boolean.valueOf(foiaProperties.get(property)));
                break;
            case FoiaConfigurationConstants.REQUEST_EXTENSION_WORKING_DAYS_ENABLED:
                foiaConfiguration.setRequestExtensionWorkingDaysEnabled(Boolean.valueOf(foiaProperties.get(property)));
                break;
            case FoiaConfigurationConstants.PURGE_REQUEST_ENABLED:
                foiaConfiguration.setPurgeRequestWhenInHoldEnabled(Boolean.valueOf(foiaProperties.get(property)));
            }
        }

        return foiaConfiguration;
    }

    public PropertyFileManager getPropertyFileManager()
    {
        return propertyFileManager;
    }

    public void setPropertyFileManager(PropertyFileManager propertyFileManager)
    {
        this.propertyFileManager = propertyFileManager;
    }

    public String getPropertiesFile()
    {
        return propertiesFile;
    }

    public void setPropertiesFile(String propertiesFile)
    {
        this.propertiesFile = propertiesFile;
    }

    @Override
    public void onApplicationEvent(ConfigurationFileChangedEvent event)
    {
        if (event.getConfigFile().getName().equals(getPropertiesFile().substring(getPropertiesFile().lastIndexOf("/") + 1)))
        {
            initBean();
        }

    }
}
