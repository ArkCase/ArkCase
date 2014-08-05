package com.armedia.acm.plugins.dashboard.service;

import com.armedia.acm.auth.AcmAuthenticationDetails;
import com.armedia.acm.plugins.dashboard.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.core.Authentication;

public class DashboardEventPublisher implements ApplicationEventPublisherAware {

    private ApplicationEventPublisher eventPublisher;
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
     eventPublisher = applicationEventPublisher;
    }

    public void publishDashboardEvent(Dashboard source, Authentication authentication, boolean newDashboard, boolean succeeded) {
        if (log.isDebugEnabled()) {
            log.debug("Publishing a dashboard event.");
        }
        DashboardPersistenceEvent dashboardPersistenceEvent = newDashboard ? new DasboardCreatedEvent(source) : new DashboardUpdatedEvent(source);
        dashboardPersistenceEvent.setSucceeded(succeeded);
        if(authentication.getDetails()!=null && authentication.getDetails() instanceof AcmAuthenticationDetails) {
            dashboardPersistenceEvent.setIpAddress(((AcmAuthenticationDetails) authentication.getDetails()).getRemoteAddress());
        }
        eventPublisher.publishEvent(dashboardPersistenceEvent);
    }
    public void publishGetDashboardByUserIdEvent(Dashboard source, Authentication authentication, String ipAddress, boolean succeeded) {

        GetDashbordByUserIdEvent getDashbordByUserIdEvent = new GetDashbordByUserIdEvent(source);
        String user = authentication.getName();
        getDashbordByUserIdEvent.setUserId(user);
        getDashbordByUserIdEvent.setIpAddress(ipAddress);
        getDashbordByUserIdEvent.setSucceeded(succeeded);
        eventPublisher.publishEvent(getDashbordByUserIdEvent);
    }
}
