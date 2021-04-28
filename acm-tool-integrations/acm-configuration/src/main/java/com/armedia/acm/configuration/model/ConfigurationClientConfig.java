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

import com.armedia.acm.configuration.annotations.ListValue;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

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

    @Value("${application.profile.reversed}")
    private String activeProfileReversed;

    @Value("${configuration.server.url}")
    private String configurationUrl;

    @Value("${configuration.client.branding.path}")
    private String brandingPath;

    @Value("${configuration.client.labels.path}")
    private String labelsPath;

    @Value("${configuration.client.ldap.path}")
    private String ldapPath;

    @Value("${configuration.client.lookups.path}")
    private String lookupsPath;

    @Value("${configuration.client.stylesheets.path}")
    private String stylesheetsPath;

    @Value("${configuration.client.rules.path}")
    private String rulesPath;

    @Value("${configuration.client.spring.path}")
    private String springPath;

    private List<String> brandingFiles;

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

    public String getActiveProfileReversed()
    {
        return activeProfileReversed;
    }

    public void setActiveProfileReversed(String activeProfileReversed)
    {
        this.activeProfileReversed = activeProfileReversed;
    }

    public String getBrandingPath()
    {
        return brandingPath;
    }

    public void setBrandingPath(String brandingPath)
    {
        this.brandingPath = brandingPath;
    }

    public String getLabelsPath()
    {
        return labelsPath;
    }

    public void setLabelsPath(String labelsPath)
    {
        this.labelsPath = labelsPath;
    }

    public String getLdapPath()
    {
        return ldapPath;
    }

    public void setLdapPath(String ldapPath)
    {
        this.ldapPath = ldapPath;
    }

    public String getLookupsPath()
    {
        return lookupsPath;
    }

    public void setLookupsPath(String lookupsPath)
    {
        this.lookupsPath = lookupsPath;
    }

    public String getStylesheetsPath()
    {
        return stylesheetsPath;
    }

    public void setStylesheetsPath(String stylesheetsPath)
    {
        this.stylesheetsPath = stylesheetsPath;
    }

    public String getRulesPath()
    {
        return rulesPath;
    }

    public void setRulesPath(String rulesPath)
    {
        this.rulesPath = rulesPath;
    }

    public String getSpringPath()
    {
        return springPath;
    }

    public void setSpringPath(String springPath)
    {
        this.springPath = springPath;
    }

    @ListValue("application.brandingFiles")
    public List<String> getBrandingFiles()
    {
        return brandingFiles;
    }

    public void setBrandingFiles(List<String> brandingFiles)
    {
        this.brandingFiles = brandingFiles;
    }
}
