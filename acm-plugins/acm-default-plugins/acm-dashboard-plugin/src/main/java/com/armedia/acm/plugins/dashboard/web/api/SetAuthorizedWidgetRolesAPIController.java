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
import com.armedia.acm.plugins.dashboard.dao.WidgetDao;
import com.armedia.acm.plugins.dashboard.exception.AcmWidgetException;
import com.armedia.acm.plugins.dashboard.model.widget.RolesGroupByWidgetDto;
import com.armedia.acm.plugins.dashboard.model.widget.Widget;
import com.armedia.acm.plugins.dashboard.model.widget.WidgetRole;
import com.armedia.acm.plugins.dashboard.model.widget.WidgetRoleName;
import com.armedia.acm.plugins.dashboard.service.WidgetEventPublisher;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmRole;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by marjan.stefanoski on 10/2/2014.
 */

@Controller
@RequestMapping({ "/api/v1/plugin/dashboard/widgets", "/api/latest/plugin/dashboard/widgets" })
public class SetAuthorizedWidgetRolesAPIController
{

    private UserDao userDao;
    private WidgetDao widgetDao;
    private WidgetEventPublisher eventPublisher;
    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/set", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public RolesGroupByWidgetDto setAuthorizedWidgetRoles(
            @RequestBody RolesGroupByWidgetDto updateAuthorizedWidgetRoles,
            Authentication authentication,
            HttpSession session) throws AcmObjectNotFoundException, AcmUserActionFailedException
    {
        String userId = (String) authentication.getName();

        log.info("Updating authorized roles for dashboard widget: [{}]", updateAuthorizedWidgetRoles.getWidgetName());

        RolesGroupByWidgetDto result = null;
        try
        {
            result = updateWidgetRolesAuthorization(updateAuthorizedWidgetRoles);
            raiseSetEvent(authentication, session, result, true);
            return result;
        }
        catch (AcmUserActionFailedException e)
        {
            throw e;
        }
    }

    protected void raiseSetEvent(Authentication authentication, HttpSession session, RolesGroupByWidgetDto rolesPerWidget,
            boolean succeeded)
    {
        String ipAddress = (String) session.getAttribute("acm_ip_address");
        getEventPublisher().publishSetAuthorizedWidgetRolesEvent(rolesPerWidget, authentication, ipAddress, succeeded);
    }

    protected RolesGroupByWidgetDto updateWidgetRolesAuthorization(RolesGroupByWidgetDto rolesGroupByWidgetDto)
            throws AcmUserActionFailedException
    {
        int i = getWidgetDao().deleteAllWidgetRolesByWidgetName(rolesGroupByWidgetDto.getWidgetName());

        log.info("Deleted " + i + " WidgetRoles");

        List<AcmRole> allRoles = getUserDao().findAllRoles();
        List<AcmRole> rolesForUpdate = new ArrayList<>();

        for (WidgetRoleName roleName : rolesGroupByWidgetDto.getWidgetAuthorizedRoles())
        {
            for (AcmRole role : allRoles)
            {
                if (role.getRoleName().equals(roleName.getName()))
                {
                    rolesForUpdate.add(role);
                    break;
                }
            }
        }
        try
        {
            addRolesToAWidgetByWidgetNameAndRoles(rolesGroupByWidgetDto.getWidgetName(), rolesForUpdate);
            return rolesGroupByWidgetDto;
        }
        catch (AcmWidgetException e)
        {
            log.error("Updating Authorized Roles for widget: [{}] ", rolesGroupByWidgetDto.getWidgetName(), e);
            throw new AcmUserActionFailedException("Update Authorized Roles for a Widget", "Dashboard", null, e.getMessage(), e);
        }
    }

    protected void addRolesToAWidgetByWidgetNameAndRoles(String widgetName, List<AcmRole> roles) throws AcmWidgetException
    {
        WidgetRole widgetRole = new WidgetRole();
        Widget widget = null;
        try
        {
            widget = getWidgetDao().getWidgetByWidgetName(widgetName);
        }
        catch (AcmObjectNotFoundException e)
        {
            throw new AcmWidgetException("Widget " + widgetName + " is not found", e);
        }
        for (AcmRole role : roles)
        {
            widgetRole.setRoleName(role.getRoleName());
            widgetRole.setWidgetId(widget.getWidgetId());
            getWidgetDao().saveWidgetRole(widgetRole);
            widgetRole = new WidgetRole();
        }
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
}
