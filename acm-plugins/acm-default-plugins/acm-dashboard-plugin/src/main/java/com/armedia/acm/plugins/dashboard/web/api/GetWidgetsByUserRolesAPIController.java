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
import com.armedia.acm.pluginmanager.model.AcmPlugin;
import com.armedia.acm.plugins.dashboard.dao.WidgetDao;
import com.armedia.acm.plugins.dashboard.exception.AcmWidgetException;
import com.armedia.acm.plugins.dashboard.model.widget.Widget;
import com.armedia.acm.plugins.dashboard.service.DashboardPropertyReader;
import com.armedia.acm.plugins.dashboard.service.DashboardService;
import com.armedia.acm.plugins.dashboard.service.WidgetEventPublisher;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.service.AcmUserRoleService;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by marjan.stefanoski on 9/19/2014.
 */

@Controller
@RequestMapping({ "/api/v1/plugin/dashboard/widgets", "/api/latest/plugin/dashboard/widgets" })
public class GetWidgetsByUserRolesAPIController
{

    private UserDao userDao;
    private WidgetDao widgetDao;
    private DashboardPropertyReader dashboardPropertyReader;
    private WidgetEventPublisher eventPublisher;
    private DashboardService dashboardService;
    private AcmUserRoleService userRoleService;
    private Logger log = LogManager.getLogger(getClass());

    @RequestMapping(value = "/get", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public List<Widget> getWidgetsByUserAndRoles(Authentication authentication, HttpSession session)
            throws AcmWidgetException, AcmObjectNotFoundException
    {

        String userId = authentication.getName();

        log.info("Finding widgets for user: [{}]  based on the user roles'", userId);

        Set<String> roles = userRoleService.getUserRoles(userId);
        if (roles.isEmpty())
        {
            throw new AcmObjectNotFoundException("user", null, "Object not found", null);
        }
        List<Widget> retval = null;
        try
        {
            retval = dashboardService.onlyUniqueValues(getWidgetDao().getAllWidgetsByRoles(roles));
            raiseGetEvent(authentication, session, retval, true);
            List<Widget> dashboardWidgetsOnly = dashboardPropertyReader.getDashboardWidgetsOnly();
            return retval.stream()
                    .filter(dashboardWidgetsOnly::contains).collect(Collectors.toList());
        }
        catch (AcmObjectNotFoundException e)
        {
            log.error("Widgets by roles associated to user: [{}] not found! ", userId, e, e.getMessage());
            raiseGetEvent(authentication, session, retval, true);
            return new ArrayList<>();
        }
        catch (Exception e1)
        {
            log.error("Exception occurred while raising an event or while reading widgets values");
            throw new AcmWidgetException("Get widgets by user roles exception", e1);
        }
    }

    protected void raiseGetEvent(Authentication authentication, HttpSession session, List<Widget> foundWidgets, boolean succeeded)
    {
        String ipAddress = (String) session.getAttribute("acm_ip_address");
        getEventPublisher().publishGetWidgetsByUserRoles(foundWidgets, authentication, ipAddress, succeeded);
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public WidgetDao getWidgetDao()
    {
        return widgetDao;
    }

    public void setWidgetDao(WidgetDao widgetDao)
    {
        this.widgetDao = widgetDao;
    }

    public WidgetEventPublisher getEventPublisher()
    {
        return eventPublisher;
    }

    public void setEventPublisher(WidgetEventPublisher eventPublisher)
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

    public void setUserRoleService(AcmUserRoleService userRoleService)
    {
        this.userRoleService = userRoleService;
    }
}
