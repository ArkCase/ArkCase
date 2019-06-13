package com.armedia.acm.plugins.dashboard.web.api;

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

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.dashboard.exception.AcmDashboardException;
import com.armedia.acm.plugins.dashboard.model.Dashboard;
import com.armedia.acm.plugins.dashboard.model.DashboardConstants;
import com.armedia.acm.plugins.dashboard.model.DashboardDto;
import com.armedia.acm.plugins.dashboard.service.DashboardEventPublisher;
import com.armedia.acm.plugins.dashboard.service.DashboardPropertyReader;
import com.armedia.acm.plugins.dashboard.service.DashboardService;
import com.armedia.acm.services.users.model.AcmUser;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by marst on 7/30/14.
 */

@Controller
@RequestMapping({ "/api/v1/plugin/dashboard", "/api/latest/plugin/dashboard" })
public class SetDashboardConfigAPIController
{
    private DashboardService dashboardService;
    private DashboardPropertyReader dashboardPropertyReader;
    private DashboardEventPublisher eventPublisher;
    private Logger log = LogManager.getLogger(getClass());

    @RequestMapping(value = "/set", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public DashboardDto setDashboardConfig(
            @RequestBody DashboardDto updateDashboardDto,
            Authentication authentication) throws AcmUserActionFailedException, AcmDashboardException
    {
        String userId = authentication.getName();
        AcmUser user = dashboardService.getUserByUserId(userId);
        List<String> modules = dashboardPropertyReader.getModuleNameList();
        String moduleName;
        if (updateDashboardDto.getModule() != null)
        {
            moduleName = updateDashboardDto.getModule().trim();
        }
        else
        {
            moduleName = DashboardConstants.DASHBOARD_MODULE_NAME;
        }
        if (!modules.contains(moduleName))
        {
            throw new AcmDashboardException("Module name:" + moduleName + "does  not exist");
        }

        log.info("Updating dashboard configuration for user: [{}] '", userId);

        Dashboard d = null;
        try
        {
            // retval is the number of entities (dashboards) updated or deleted
            d = dashboardService.getDashboardConfigForUserAndModuleName(user, moduleName);
            int retval = dashboardService.setDashboardConfigForUserAndModule(user, updateDashboardDto, moduleName);
            if (retval != 1)
            {
                log.error("Unable to update dashboard config because dashboard for user: [{}] is not found", userId);
                throw new AcmObjectNotFoundException("dashboard", null, "Object not found", null);
            }
            else
            {
                getEventPublisher().publishDashboardEvent(d, authentication, false, true);
                updateDashboardDto.setUpdated(true);
                return updateDashboardDto;
            }
        }
        catch (Exception e)
        {
            if (d != null)
            {
                getEventPublisher().publishDashboardEvent(d, authentication, false, false);
            }
            throw new AcmUserActionFailedException("update", "dashboard", null, e.getMessage(), e);
        }
    }

    public DashboardEventPublisher getEventPublisher()
    {
        return eventPublisher;
    }

    public void setEventPublisher(DashboardEventPublisher eventPublisher)
    {
        this.eventPublisher = eventPublisher;
    }

    public DashboardPropertyReader getDashboardPropertyReader()
    {
        return dashboardPropertyReader;
    }

    public void setDashboardPropertyReader(DashboardPropertyReader dashboardPropertyReader)
    {
        this.dashboardPropertyReader = dashboardPropertyReader;
    }

    public DashboardService getDashboardService()
    {
        return dashboardService;
    }

    public void setDashboardService(DashboardService dashboardService)
    {
        this.dashboardService = dashboardService;
    }
}
