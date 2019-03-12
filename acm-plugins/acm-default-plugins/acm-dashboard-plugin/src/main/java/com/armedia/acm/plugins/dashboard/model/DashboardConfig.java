package com.armedia.acm.plugins.dashboard.model;

/*-
 * #%L
 * ACM Default Plugin: Dashboard
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

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;

public class DashboardConfig
{
    @JsonProperty("acm.defaultDashboard")
    @Value("${dashboard.plugin.defaultDashboard}")
    private String defaultDashboard;

    @JsonProperty("acm.moduleDefaultDashboard")
    @Value("${dashboard.plugin.moduleDefaultDashboard}")
    private String moduleDefaultDashboard;

    @JsonProperty("acm.add.widget")
    @Value("${dashboard.plugin.add.widget}")
    private Boolean addWidget;

    @JsonProperty("acm.new.widgets")
    @Value("${dashboard.plugin.new.widgets}")
    private String newWidgets;

    @JsonProperty("acm.role.widgets")
    @Value("${dashboard.plugin.role.widgets}")
    private String roleWidgets;

    @JsonProperty("acm.modules")
    @Value("${dashboard.plugin.modules}")
    private String modules;

    @JsonProperty("acm.modules.dashboard.widgets")
    @Value("${dashboard.plugin.modules.dashboard.widgets}")
    private String moduleDashboardWidgets;

    public String getDefaultDashboard()
    {
        return defaultDashboard;
    }

    public void setDefaultDashboard(String defaultDashboard)
    {
        this.defaultDashboard = defaultDashboard;
    }

    public String getModuleDefaultDashboard()
    {
        return moduleDefaultDashboard;
    }

    public void setModuleDefaultDashboard(String moduleDefaultDashboard)
    {
        this.moduleDefaultDashboard = moduleDefaultDashboard;
    }

    public Boolean getAddWidget()
    {
        return addWidget;
    }

    public void setAddWidget(Boolean addWidget)
    {
        this.addWidget = addWidget;
    }

    public String getNewWidgets()
    {
        return newWidgets;
    }

    public void setNewWidgets(String newWidgets)
    {
        this.newWidgets = newWidgets;
    }

    public String getRoleWidgets()
    {
        return roleWidgets;
    }

    public void setRoleWidgets(String roleWidgets)
    {
        this.roleWidgets = roleWidgets;
    }

    public String getModules()
    {
        return modules;
    }

    public void setModules(String modules)
    {
        this.modules = modules;
    }

    public String getModuleDashboardWidgets()
    {
        return moduleDashboardWidgets;
    }

    public void setModuleDashboardWidgets(String moduleDashboardWidgets)
    {
        this.moduleDashboardWidgets = moduleDashboardWidgets;
    }
}
