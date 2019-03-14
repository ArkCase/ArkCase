package com.armedia.acm.plugins.dashboard.model;

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

/**
 * Created by marjan.stefanoski on 14.01.2016.
 */
public interface DashboardConstants
{
    String DASHBOARD_MODULE_NAME = "DASHBOARD";

    String OBJECT_TYPE = DASHBOARD_MODULE_NAME;
    String EVENT_TYPE_DASHBOARD_UPDATED = "com.armedia.acm.dashboard.updated";
    String EVENT_TYPE_DASHBOARD_CREATED = "com.armedia.acm.dashboard.created";

    String DEFAULT_MODULE_DASHBOARD = "moduleDefaultDashboard";

    String ROLE = "role";
    String WIDGET_LIST = "widgetList";
    String COMMA_SPLITTER = ",";

    String DASHBOARD_ROWS = "rows";
    String DASHBOARD_COLUMNS = "columns";
    String DASHBOARD_WIDGETS = "widgets";
    String DASHBOARD_WIDGET_TYPE = "type";

}
