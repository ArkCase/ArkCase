package com.armedia.acm.plugins.dashboard.service;

import com.armedia.acm.plugins.dashboard.model.userPreference.UserPreference;
import com.armedia.acm.plugins.dashboard.model.userPreference.UserPreferenceCreatedEvent;
import com.armedia.acm.plugins.dashboard.model.userPreference.UserPreferenceDeletedEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

/**
 * Created by marjan.stefanoski on 18.01.2016.
 */
public class UserPreferenceEventPublisher implements ApplicationEventPublisherAware
{
    private ApplicationEventPublisher eventPublisher;
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        eventPublisher = applicationEventPublisher;
    }

    public void publishUserPreferenceCreated(UserPreference source, String ipAddress, boolean succeeded)
    {

        log.debug("Publishing a User Preference event. New User Preferred Widget Added Event.");
        UserPreferenceCreatedEvent userPreferenceCreatedEvent = new UserPreferenceCreatedEvent(source);

        userPreferenceCreatedEvent.setIpAddress(ipAddress);
        userPreferenceCreatedEvent.setSucceeded(succeeded);

        eventPublisher.publishEvent(userPreferenceCreatedEvent);
    }

    public void publishUserPreferenceDeleted(UserPreference source, String ipAddress, boolean succeeded)
    {
        log.debug("Publishing a User Preference event. User Preferred Widget Removed/Deleted Event.");

        UserPreferenceDeletedEvent userPreferenceDeletedEvent = new UserPreferenceDeletedEvent(source);

        userPreferenceDeletedEvent.setIpAddress(ipAddress);
        userPreferenceDeletedEvent.setSucceeded(succeeded);

        eventPublisher.publishEvent(userPreferenceDeletedEvent);
    }

}
