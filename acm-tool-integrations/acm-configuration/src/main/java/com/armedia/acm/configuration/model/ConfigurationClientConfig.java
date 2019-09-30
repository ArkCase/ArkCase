package com.armedia.acm.configuration.model;

/*-
 * #%L
 * ACM Tool Integrations: Configuration Library
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

import org.springframework.beans.factory.annotation.Value;

public class ConfigurationClientConfig
{

    @Value("${application.name.default}")
    private String defaultApplicationName;

    @Value("${application.name.active}")
    private String activeApplicationName;

    @Value("${configuration.server.update.file.path}")
    private String updateFilePath;

    @Value("${application.profile}")
    private String activeProfile;

    @Value("${configuration.server.url}")
    private String configurationUrl;

    private String updatePropertiesEndpoint;

    public String getDefaultApplicationName()
    {
        return defaultApplicationName;
    }

    public void setDefaultApplicationName(String defaultApplicationName)
    {
        this.defaultApplicationName = defaultApplicationName;
    }

    public String getActiveApplicationName()
    {
        return activeApplicationName;
    }

    public void setActiveApplicationName(String activeApplicationName)
    {
        this.activeApplicationName = activeApplicationName;
    }

    public String getUpdateFilePath()
    {
        return updateFilePath;
    }

    public void setUpdateFilePath(String updateFilePath)
    {
        this.updateFilePath = updateFilePath;
    }

    public String getActiveProfile()
    {
        return activeProfile;
    }

    public void setActiveProfile(String activeProfile)
    {
        this.activeProfile = activeProfile;
    }

    public String getConfigurationUrl()
    {
        return configurationUrl;
    }

    public void setConfigurationUrl(String configurationUrl)
    {
        this.configurationUrl = configurationUrl;
    }

    public String getUpdateFilePropertiesEndpoint()
    {
        return String.format("%s%s", this.configurationUrl, this.updateFilePath);
    }
}
