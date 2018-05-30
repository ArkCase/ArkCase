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

public class LdapTemplateConfig
{
    private String userPropertiesTemplate;
    private String userPropertiesTemplateName;
    private String userFileTemplate;
    private String userFileTemplateName;

    private String groupPropertiesTemplate;
    private String groupPropertiesTemplateName;
    private String groupFileTemplate;
    private String groupFileTemplateName;

    public String getUserPropertiesTemplate()
    {
        return userPropertiesTemplate;
    }

    public void setUserPropertiesTemplate(String userPropertiesTemplate)
    {
        this.userPropertiesTemplate = userPropertiesTemplate;
    }

    public String getUserPropertiesTemplateName()
    {
        return userPropertiesTemplateName;
    }

    public void setUserPropertiesTemplateName(String userPropertiesTemplateName)
    {
        this.userPropertiesTemplateName = userPropertiesTemplateName;
    }

    public String getUserFileTemplate()
    {
        return userFileTemplate;
    }

    public void setUserFileTemplate(String userFileTemplate)
    {
        this.userFileTemplate = userFileTemplate;
    }

    public String getUserFileTemplateName()
    {
        return userFileTemplateName;
    }

    public void setUserFileTemplateName(String userFileTemplateName)
    {
        this.userFileTemplateName = userFileTemplateName;
    }

    public String getGroupPropertiesTemplate()
    {
        return groupPropertiesTemplate;
    }

    public void setGroupPropertiesTemplate(String groupPropertiesTemplate)
    {
        this.groupPropertiesTemplate = groupPropertiesTemplate;
    }

    public String getGroupPropertiesTemplateName()
    {
        return groupPropertiesTemplateName;
    }

    public void setGroupPropertiesTemplateName(String groupPropertiesTemplateName)
    {
        this.groupPropertiesTemplateName = groupPropertiesTemplateName;
    }

    public String getGroupFileTemplate()
    {
        return groupFileTemplate;
    }

    public void setGroupFileTemplate(String groupFileTemplate)
    {
        this.groupFileTemplate = groupFileTemplate;
    }

    public String getGroupFileTemplateName()
    {
        return groupFileTemplateName;
    }

    public void setGroupFileTemplateName(String groupFileTemplateName)
    {
        this.groupFileTemplateName = groupFileTemplateName;
    }
}
