package com.armedia.acm.report.config;

/*-
 * #%L
 * Tool Integrations: report Configuration
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

import com.armedia.acm.configuration.annotations.MapValue;

import java.util.List;
import java.util.Map;

public class ReportsToRolesConfig
{

    public static final String REPORTS_TO_ROLES_PROP_KEY = "report.config.reportsToRoles";

    private Map<String, List<String>> reportsToRolesMap;

    @MapValue(value = "report.config.reportsToRoles")
    public Map<String, List<String>> getReportsToRolesMap()
    {
        return reportsToRolesMap;
    }

    public void setReportsToRolesMap(Map<String, List<String>> reportsToRolesMap)
    {
        this.reportsToRolesMap = reportsToRolesMap;
    }
}
