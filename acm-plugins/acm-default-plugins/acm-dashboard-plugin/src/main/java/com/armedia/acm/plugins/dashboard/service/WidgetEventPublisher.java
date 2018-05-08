package com.armedia.acm.plugins.dashboard.service;

import com.armedia.acm.auth.AcmAuthenticationDetails;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.core.Authentication;

import java.util.List;

/**
 * Created by marjan.stefanoski on 9/20/2014.
 */
public class WidgetEventPublisher implements ApplicationEventPublisherAware
{

    private transient final Logger log = LoggerFactory.getLogger(getClass());
    private ApplicationEventPublisher eventPublisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        eventPublisher = applicationEventPublisher;
    }

    public void publishWidgetEvent(Widget source, Authentication authentication, boolean newWidget, boolean succeeded)
    {

        log.debug("Publishing a widget event.");
        WidgetPersistenceEvent widgetPersistenceEvent = newWidget ? new WidgetCreatedEvent(source) : new WidgetUpdatedEvent(source);
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
