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

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

//import validateJSON.JSONException;
//import validateJSON.JSONObject;

import org.json.*;

/**
 * Created by marst on 8/6/14.
 */
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class DashboardDto
{
    String userId;
    String dashboardConfig;
    String module;
    boolean collapsed = false;
    boolean updated = false;
    boolean inserted = false;

    public boolean isInserted()
    {
        return inserted;
    }

    public void setInserted(boolean inserted)
    {
        this.inserted = inserted;
    }

    public boolean isUpdated()
    {
        return updated;
    }

    public void setUpdated(boolean updated)
    {
        this.updated = updated;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public String getDashboardConfig()
    {
        return dashboardConfig;
    }

    public void setDashboardConfig(String dashboardConfig)
    {
        try
        {
            new JSONObject(dashboardConfig);
            this.dashboardConfig = dashboardConfig;
        }
        catch (JSONException e)
        {
            throw new JSONException("Invalid dashboard configuration.");
        }
        
    }

    public String getModule()
    {
        return module;
    }

    public void setModule(String module)
    {
        this.module = module;
    }

    public boolean isCollapsed()
    {
        return collapsed;
    }

    public void setCollapsed(boolean collapsed)
    {
        this.collapsed = collapsed;
    }
}
