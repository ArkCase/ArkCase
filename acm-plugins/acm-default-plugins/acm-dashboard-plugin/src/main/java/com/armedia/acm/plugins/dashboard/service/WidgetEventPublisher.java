package com.armedia.acm.plugins.dashboard.service;

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

import com.armedia.acm.auth.AcmAuthenticationDetails;
import com.armedia.acm.auth.AuthenticationUtils;
import com.armedia.acm.plugins.dashboard.model.widget.GetRolesByWidgetsEvent;
import com.armedia.acm.plugins.dashboard.model.widget.GetWidgetsByUserRolesEvent;
import com.armedia.acm.plugins.dashboard.model.widget.RolesGroupByWidgetDto;
import com.armedia.acm.plugins.dashboard.model.widget.SetAuthorizedWidgetRolesEvent;
import com.armedia.acm.plugins.dashboard.model.widget.Widget;
import com.armedia.acm.plugins.dashboard.model.widget.WidgetCreatedEvent;
import com.armedia.acm.plugins.dashboard.model.widget.WidgetPersistenceEvent;
import com.armedia.acm.plugins.dashboard.model.widget.WidgetRole;
import com.armedia.acm.plugins.dashboard.model.widget.WidgetRoleCreatedEvent;
import com.armedia.acm.plugins.dashboard.model.widget.WidgetRolePersistenceEvent;
import com.armedia.acm.plugins.dashboard.model.widget.WidgetRoleUpdatedEvent;
import com.armedia.acm.plugins.dashboard.model.widget.WidgetUpdatedEvent;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.core.Authentication;

import java.util.List;

/**
 * Created by marjan.stefanoski on 9/20/2014.
 */
public class WidgetEventPublisher implements ApplicationEventPublisherAware
{

    private transient final Logger log = LogManager.getLogger(getClass());
    private ApplicationEventPublisher eventPublisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        eventPublisher = applicationEventPublisher;
    }

    public void publishWidgetEvent(Widget source, Authentication authentication, boolean newWidget, boolean succeeded)
    {

        log.debug("Publishing a widget event.");
        WidgetPersistenceEvent widgetPersistenceEvent = newWidget ? new WidgetCreatedEvent(source, AuthenticationUtils.getUserIpAddress())
                : new WidgetUpdatedEvent(source);
        widgetPersistenceEvent.setSucceeded(succeeded);
        if (authentication.getDetails() != null && authentication.getDetails() instanceof AcmAuthenticationDetails)
        {
            widgetPersistenceEvent.setIpAddress(((AcmAuthenticationDetails) authentication.getDetails()).getRemoteAddress());
        }
        eventPublisher.publishEvent(widgetPersistenceEvent);
    }

    public void publishWidgetRoleEvent(WidgetRole source, Authentication authentication, boolean newWidgetRole, boolean succeeded)
    {

        log.debug("Publishing a widget event.");
        WidgetRolePersistenceEvent widgetRolePersistenceEvent = newWidgetRole ? new WidgetRoleCreatedEvent(source)
                : new WidgetRoleUpdatedEvent(source);
        widgetRolePersistenceEvent.setSucceeded(succeeded);
        if (authentication.getDetails() != null && authentication.getDetails() instanceof AcmAuthenticationDetails)
        {
            widgetRolePersistenceEvent.setIpAddress(((AcmAuthenticationDetails) authentication.getDetails()).getRemoteAddress());
        }
        eventPublisher.publishEvent(widgetRolePersistenceEvent);
    }

    public void publishGetWidgetsByUserRoles(List<Widget> source, Authentication authentication, String ipAddress, boolean succeeded)
    {

        log.debug("Publishing a widget event. Get Widgets by User Roles Event");

        GetWidgetsByUserRolesEvent getWidgetsByUserRoles = new GetWidgetsByUserRolesEvent(source);
        String user = authentication.getName();
        getWidgetsByUserRoles.setUserId(user);
        getWidgetsByUserRoles.setIpAddress(ipAddress);
        getWidgetsByUserRoles.setSucceeded(succeeded);
        eventPublisher.publishEvent(getWidgetsByUserRoles);
    }

    public void publishGeRolesByWidgets(List<RolesGroupByWidgetDto> source, Authentication authentication, String ipAddress,
            boolean succeeded)
    {

        log.debug("Publishing a widget event. Get all Roles per Widgets Event");

        GetRolesByWidgetsEvent getRolesByWidgetsEvent = new GetRolesByWidgetsEvent(source);
        String user = authentication.getName();
        getRolesByWidgetsEvent.setUserId(user);
        getRolesByWidgetsEvent.setIpAddress(ipAddress);
        getRolesByWidgetsEvent.setSucceeded(succeeded);
        eventPublisher.publishEvent(getRolesByWidgetsEvent);
    }

    public void publishSetAuthorizedWidgetRolesEvent(RolesGroupByWidgetDto source, Authentication authentication, String ipAddress,
            boolean succeeded)
    {

        log.debug("Publishing a widget event. Get all Roles per Widgets Event");

        SetAuthorizedWidgetRolesEvent setAuthorizedWidgetRolesEvent = new SetAuthorizedWidgetRolesEvent(source);
        String user = authentication.getName();
        setAuthorizedWidgetRolesEvent.setUserId(user);
        setAuthorizedWidgetRolesEvent.setIpAddress(ipAddress);
        setAuthorizedWidgetRolesEvent.setSucceeded(succeeded);
        eventPublisher.publishEvent(setAuthorizedWidgetRolesEvent);
    }
}
