package com.armedia.acm.plugins.dashboard.service;

import com.armedia.acm.auth.AcmAuthenticationDetails;
import com.armedia.acm.plugins.dashboard.model.Dashboard;
import com.armedia.acm.plugins.dashboard.model.DashboardCreatedEvent;
import com.armedia.acm.plugins.dashboard.model.DashboardPersistenceEvent;
import com.armedia.acm.plugins.dashboard.model.DashboardUpdatedEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.core.Authentication;

public class DashboardEventPublisher implements ApplicationEventPublisherAware
{

    private transient final Logger log = LoggerFactory.getLogger(getClass());
    private ApplicationEventPublisher eventPublisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        eventPublisher = applicationEventPublisher;
    }

    public void publishDashboardEvent(Dashboard source, Authentication authentication, boolean newDashboard, boolean succeeded)
    {
        log.debug("Publishing a dashboard event.");

        DashboardPersistenceEvent dashboardPersistenceEvent = newDashboard ? new DashboardCreatedEvent(source)
                : new DashboardUpdatedEvent(source);
        dashboardPersistenceEvent.setSucceeded(succeeded);
        if (authentication.getDetails() != null && authentication.getDetails() instanceof AcmAuthenticationDetails)
        {
            dashboardPersistenceEvent.setIpAddress(((AcmAuthenticationDetails) authentication.getDetails()).getRemoteAddress());
        }
        eventPublisher.publishEvent(dashboardPersistenceEvent);
    }

}
