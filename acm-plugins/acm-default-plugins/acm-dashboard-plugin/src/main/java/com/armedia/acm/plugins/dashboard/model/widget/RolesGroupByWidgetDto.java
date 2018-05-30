package com.armedia.acm.plugins.dashboard.model.widget;

/*-
 * #%L
 * ACM Default Plugin: Dashboard
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

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

import java.util.List;

/**
 * Created by marjan.stefanoski on 9/30/2014.
 */
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class RolesGroupByWidgetDto
{

    private String widgetName;
    private String name;
    private List<WidgetRoleName> widgetAuthorizedRoles;
    private List<WidgetRoleName> widgetNotAuthorizedRoles;

    public String getWidgetName()
    {
        return widgetName;
    }

    public void setWidgetName(String widgetName)
    {
        this.widgetName = widgetName;
    }

    public List<WidgetRoleName> getWidgetAuthorizedRoles()
    {
        return widgetAuthorizedRoles;
    }

    public void setWidgetAuthorizedRoles(List<WidgetRoleName> widgetAuthorizedRoles)
    {
        this.widgetAuthorizedRoles = widgetAuthorizedRoles;
    }

    public List<WidgetRoleName> getWidgetNotAuthorizedRoles()
    {
        return widgetNotAuthorizedRoles;
    }

    public void setWidgetNotAuthorizedRoles(List<WidgetRoleName> widgetNotAuthorizedRoles)
    {
        this.widgetNotAuthorizedRoles = widgetNotAuthorizedRoles;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return "RolesGroupByWidgetDto{" +
                "widgetName='" + widgetName + '\'' +
                ", name='" + name + '\'' +
                ", widgetAuthorizedRoles=" + widgetAuthorizedRoles +
                ", widgetNotAuthorizedRoles=" + widgetNotAuthorizedRoles +
                '}';
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        RolesGroupByWidgetDto that = (RolesGroupByWidgetDto) o;

        if (widgetName != null ? !widgetName.equals(that.widgetName) : that.widgetName != null)
            return false;
        if (name != null ? !name.equals(that.name) : that.name != null)
            return false;
        if (widgetAuthorizedRoles != null ? !widgetAuthorizedRoles.equals(that.widgetAuthorizedRoles) : that.widgetAuthorizedRoles != null)
            return false;
        return widgetNotAuthorizedRoles != null ? widgetNotAuthorizedRoles.equals(that.widgetNotAuthorizedRoles)
                : that.widgetNotAuthorizedRoles == null;

    }

    @Override
    public int hashCode()
    {
        int result = widgetName != null ? widgetName.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (widgetAuthorizedRoles != null ? widgetAuthorizedRoles.hashCode() : 0);
        result = 31 * result + (widgetNotAuthorizedRoles != null ? widgetNotAuthorizedRoles.hashCode() : 0);
        return result;
    }
}
