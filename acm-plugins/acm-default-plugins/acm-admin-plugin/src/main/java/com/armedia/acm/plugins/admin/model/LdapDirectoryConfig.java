package com.armedia.acm.plugins.admin.model;

/*-
 * #%L
 * ACM Default Plugin: admin
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

public class LdapDirectoryConfig
{
    private String ldapConfigurationLocation;
    private String ldapFile;
    private String ldapPropertiesFile;
    private String ldapConfigurationTemplatesLocation;
    private String ldapTemplateFile;
    private String ldapTemplatePropertiesFile;
    private String ldapPropertiesFileRegex;

    private String ldapUserPropertiesFileRegex;
    private String ldapUserFileRegex;

    private String ldapGroupPropertiesFileRegex;
    private String ldapGroupFileRegex;

    public String getLdapConfigurationLocation()
    {
        return ldapConfigurationLocation;
    }

    public void setLdapConfigurationLocation(String ldapConfigurationLocation)
    {
        this.ldapConfigurationLocation = ldapConfigurationLocation;
    }

    public String getLdapFile()
    {
        return ldapFile;
    }

    public void setLdapFile(String ldapFile)
    {
        this.ldapFile = ldapFile;
    }

    public String getLdapPropertiesFile()
    {
        return ldapPropertiesFile;
    }

    public void setLdapPropertiesFile(String ldapPropertiesFile)
    {
        this.ldapPropertiesFile = ldapPropertiesFile;
    }

    public String getLdapConfigurationTemplatesLocation()
    {
        return ldapConfigurationTemplatesLocation;
    }

    public void setLdapConfigurationTemplatesLocation(String ldapConfigurationTemplatesLocation)
    {
        this.ldapConfigurationTemplatesLocation = ldapConfigurationTemplatesLocation;
    }

    public String getLdapTemplateFile()
    {
        return ldapTemplateFile;
    }

    public void setLdapTemplateFile(String ldapTemplateFile)
    {
        this.ldapTemplateFile = ldapTemplateFile;
    }

    public String getLdapTemplatePropertiesFile()
    {
        return ldapTemplatePropertiesFile;
    }

    public void setLdapTemplatePropertiesFile(String ldapTemplatePropertiesFile)
    {
        this.ldapTemplatePropertiesFile = ldapTemplatePropertiesFile;
    }

    public String getLdapPropertiesFileRegex()
    {
        return ldapPropertiesFileRegex;
    }

    public void setLdapPropertiesFileRegex(String ldapPropertiesFileRegex)
    {
        this.ldapPropertiesFileRegex = ldapPropertiesFileRegex;
    }

    public String getLdapUserPropertiesFileRegex()
    {
        return ldapUserPropertiesFileRegex;
    }

    public void setLdapUserPropertiesFileRegex(String ldapUserPropertiesFileRegex)
    {
        this.ldapUserPropertiesFileRegex = ldapUserPropertiesFileRegex;
    }

    public String getLdapUserFileRegex()
    {
        return ldapUserFileRegex;
    }

    public void setLdapUserFileRegex(String ldapUserFileRegex)
    {
        this.ldapUserFileRegex = ldapUserFileRegex;
    }

    public String getLdapGroupPropertiesFileRegex()
    {
        return ldapGroupPropertiesFileRegex;
    }

    public void setLdapGroupPropertiesFileRegex(String ldapGroupPropertiesFileRegex)
    {
        this.ldapGroupPropertiesFileRegex = ldapGroupPropertiesFileRegex;
    }

    public String getLdapGroupFileRegex()
    {
        return ldapGroupFileRegex;
    }

    public void setLdapGroupFileRegex(String ldapGroupFileRegex)
    {
        this.ldapGroupFileRegex = ldapGroupFileRegex;
    }
}
