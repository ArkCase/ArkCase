package com.armedia.acm.plugins.dashboard.service;

import com.armedia.acm.auth.AcmAuthenticationDetails;
import com.armedia.acm.plugins.dashboard.model.*;
import com.armedia.acm.plugins.dashboard.model.widget.*;
import com.armedia.acm.plugins.dashboard.web.api.GetWidgetsByUserRoles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.core.Authentication;

import java.util.List;

/**
 * Created by marjan.stefanoski on 9/20/2014.
 */
public class WidgetEventPublisher implements ApplicationEventPublisherAware {


    private ApplicationEventPublisher eventPublisher;
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        eventPublisher = applicationEventPublisher;
    }

    public void publishWidgetdEvent(Widget source, Authentication authentication, boolean newWidget, boolean succeeded) {
        if (log.isDebugEnabled()) {
            log.debug("Publishing a widget event.");
        }
        WidgetPersistenceEvent widgetPersistenceEvent = newWidget ? new WidgetCreatedEvent(source) : new WidgetUpdatedEvent(source);
        widgetPersistenceEvent.setSucceeded(succeeded);
        if(authentication.getDetails()!=null && authentication.getDetails() instanceof AcmAuthenticationDetails) {
            widgetPersistenceEvent.setIpAddress(((AcmAuthenticationDetails) authentication.getDetails()).getRemoteAddress());
        }
        eventPublisher.publishEvent(widgetPersistenceEvent);
    }
    public void publishGetWidgetsByUserRoles(List<Widget> source, Authentication authentication, String ipAddress, boolean succeeded) {
        if (log.isDebugEnabled()) {
            log.debug("Publishing a widget event. Get Widgets by User Roles Event");
        }
        GetWidgetsByUserRolesEvent getWidgetsByUserRoles = new GetWidgetsByUserRolesEvent(source);
        String user = authentication.getName();
        getWidgetsByUserRoles.setUserId(user);
        getWidgetsByUserRoles.setIpAddress(ipAddress);
        getWidgetsByUserRoles.setSucceeded(succeeded);
        eventPublisher.publishEvent(getWidgetsByUserRoles);
    }
}
