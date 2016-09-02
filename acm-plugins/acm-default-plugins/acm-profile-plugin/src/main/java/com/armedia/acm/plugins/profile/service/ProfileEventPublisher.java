package com.armedia.acm.plugins.profile.service;

import com.armedia.acm.auth.AcmAuthenticationDetails;
import com.armedia.acm.plugins.profile.model.OutlookPasswordChangedEvent;
import com.armedia.acm.plugins.profile.model.UserOrg;
import com.armedia.acm.plugins.profile.model.UserOrgCreatedEvent;
import com.armedia.acm.plugins.profile.model.UserOrgPersistentEvent;
import com.armedia.acm.plugins.profile.model.UserOrgUpdateEvent;
import com.armedia.acm.service.outlook.model.OutlookPassword;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.core.Authentication;

public class ProfileEventPublisher implements ApplicationEventPublisherAware
{

    private transient final Logger log = LoggerFactory.getLogger(getClass());
    private ApplicationEventPublisher eventPublisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        eventPublisher = applicationEventPublisher;
    }

    public void publishProfileEvent(UserOrg source, Authentication authentication, boolean newUserOrg, boolean succeeded)
    {
        log.debug("Publishing a widget event.");
        UserOrgPersistentEvent userOrgPersistenceEvent = newUserOrg ? new UserOrgCreatedEvent(source) : new UserOrgUpdateEvent(source);
        userOrgPersistenceEvent.setSucceeded(succeeded);
        if (authentication.getDetails() != null && authentication.getDetails() instanceof AcmAuthenticationDetails)
        {
            userOrgPersistenceEvent.setIpAddress(((AcmAuthenticationDetails) authentication.getDetails()).getRemoteAddress());
        }
        eventPublisher.publishEvent(userOrgPersistenceEvent);
    }

    public void outlookPasswordSavedEvent(OutlookPassword source, Authentication auth, String ipAddress, boolean succeeded)
    {
        OutlookPasswordChangedEvent event = new OutlookPasswordChangedEvent(source, auth.getName(), ipAddress, succeeded);
        eventPublisher.publishEvent(event);
    }
}
