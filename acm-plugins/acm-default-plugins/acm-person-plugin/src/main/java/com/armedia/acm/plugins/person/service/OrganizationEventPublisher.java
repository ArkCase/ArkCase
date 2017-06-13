package com.armedia.acm.plugins.person.service;

import com.armedia.acm.auth.AuthenticationUtils;
import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.plugins.person.model.OrganizationEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

public class OrganizationEventPublisher implements ApplicationEventPublisherAware
{
    private transient final Logger log = LoggerFactory.getLogger(getClass());
    private ApplicationEventPublisher eventPublisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        eventPublisher = applicationEventPublisher;
    }

    public void publishEvent(OrganizationEvent event)
    {
        eventPublisher.publishEvent(event);
    }

    public void publishOrganizationViewedEvent(Organization source, boolean succeeded)
    {
        log.debug("Publishing a Document Repository viewed event.");
        OrganizationEvent event = new OrganizationEvent(source, "viewed");
        event.setSucceeded(succeeded);
        event.setIpAddress(AuthenticationUtils.getUserIpAddress());
        eventPublisher.publishEvent(event);
    }

    public void publishOrganizationUpsertEvent(Organization source, boolean newOrganization, boolean succeeded)
    {
        log.debug("Publishing a organization event.");
        String ipAddress = AuthenticationUtils.getUserIpAddress();
        OrganizationEvent organizationEvent = new OrganizationEvent(source);
        organizationEvent.setIpAddress(ipAddress);
        if (newOrganization)
        {
            organizationEvent.setEventStatus("created");
        } else
        {
            organizationEvent.setEventStatus("updated");
        }
        organizationEvent.setSucceeded(succeeded);
        eventPublisher.publishEvent(organizationEvent);
    }

}
