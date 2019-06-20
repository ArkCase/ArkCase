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
import com.armedia.acm.plugins.dashboard.dao.WidgetDao;
import com.armedia.acm.plugins.dashboard.exception.AcmWidgetException;
import com.armedia.acm.plugins.dashboard.model.widget.RolesGroupByWidgetDto;
import com.armedia.acm.plugins.dashboard.model.widget.WidgetRoleName;
import com.armedia.acm.plugins.dashboard.service.DashboardService;
import com.armedia.acm.plugins.dashboard.service.WidgetEventPublisher;
import com.armedia.acm.services.users.dao.UserDao;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by marjan.stefanoski on 9/30/2014.
 */
@Controller
@RequestMapping({ "/api/v1/plugin/dashboard/widgets", "/api/latest/plugin/dashboard/widgets" })
public class GetRolesByWidgetsAPIController
{

    private UserDao userDao;
    private WidgetDao widgetDao;
    private WidgetEventPublisher eventPublisher;
    private DashboardService dashboardService;
    private Logger log = LogManager.getLogger(getClass());

    @RequestMapping(value = "/rolesByWidget/all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public List<RolesGroupByWidgetDto> getRolesGroupedByWidget(Authentication authentication, HttpSession session)
            throws AcmWidgetException
    {
        log.info("List of all, authorized and not authorized roles grouped by widget'");

        List<RolesGroupByWidgetDto> result = null;
        try
        {
            result = dashboardService.addNotAuthorizedRolesPerWidget(getWidgetDao().getRolesGroupByWidget());
            dashboardService.raiseGetEvent(authentication, session, result, true);
            return result;
        }
        catch (AcmObjectNotFoundException e)
        {
            throw new AcmWidgetException("No Roles per Widgets found", e);
        }
    }

    @RequestMapping(value = "/{widgetName:.+}/roles", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE
    })
    @ResponseBody
    public List<WidgetRoleName> findRolesByWidgetPaged(
            @PathVariable(value = "widgetName") String widgetName,
            @RequestParam(value = "authorized") Boolean authorized,
            @RequestParam(value = "sortBy", required = false, defaultValue = "widgetName") String sortBy,
            @RequestParam(value = "dir", required = false, defaultValue = "ASC") String sortDirection,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "1000") int maxRows, Authentication authentication,
            HttpSession session)
            throws IOException
    {
        List<WidgetRoleName> result = new ArrayList<>();
        try
        {
            result = dashboardService.getRolesByWidgetPaged(widgetName, sortBy, sortDirection, startRow, maxRows, authorized,
                    getWidgetDao().getRolesGroupByWidget());
            dashboardService.raiseGetEvent(authentication, session, getWidgetDao().getRolesGroupByWidget(), true);
        }
        catch (Exception e)
        {
            log.warn("Can't retrieve privileges", e);
        }
        return result;
    }

    @RequestMapping(value = "/{widgetName:.+}/roles", params = { "fn" }, method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE
    })
    @ResponseBody
    public List<WidgetRoleName> findRolesByWidgetPaged(
            @PathVariable(value = "widgetName") String widgetName,
            @RequestParam(value = "authorized") Boolean authorized,
            @RequestParam(value = "fn") String filterName,
            @RequestParam(value = "sortBy", required = false, defaultValue = "widgetName") String sortBy,
            @RequestParam(value = "dir", required = false, defaultValue = "ASC") String sortDirection,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "1000") int maxRows, Authentication authentication,
            HttpSession session)
    {
        List<WidgetRoleName> result = new ArrayList<>();
        try
        {
            result = dashboardService.getRolesByWidget(widgetName, sortBy, sortDirection, startRow, maxRows, filterName, authorized,
                    getWidgetDao().getRolesGroupByWidget());
            dashboardService.raiseGetEvent(authentication, session, getWidgetDao().getRolesGroupByWidget(), true);
        }
        catch (Exception e)
        {
            log.warn("Can't retrieve privileges {}", e);
        }
        return result;
    }

    public WidgetDao getWidgetDao()
    {
        return widgetDao;
    }

    public void setWidgetDao(WidgetDao widgetDao)
    {
        this.widgetDao = widgetDao;
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public DashboardService getDashboardService()
    {
        return dashboardService;
    }

    public void setDashboardService(DashboardService dashboardService)
    {
        this.dashboardService = dashboardService;
    }

    public WidgetEventPublisher getEventPublisher()
    {
        return eventPublisher;
    }

    public void setEventPublisher(WidgetEventPublisher eventPublisher)
    {
        this.eventPublisher = eventPublisher;
    }
}
