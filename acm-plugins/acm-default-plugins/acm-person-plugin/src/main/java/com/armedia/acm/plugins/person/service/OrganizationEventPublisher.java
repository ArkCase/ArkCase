package com.armedia.acm.plugins.person.service;

import com.armedia.acm.auth.AuthenticationUtils;
import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.plugins.person.model.OrganizationAddEvent;
import com.armedia.acm.plugins.person.model.OrganizationModifiedEvent;
import com.armedia.acm.plugins.person.model.OrganizationPersistenceEvent;
import com.armedia.acm.plugins.person.model.OrganizationUpdatedEvent;
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

    public void publishOrganizationEvent(Organization source, String ipAddress, boolean newOrganization, boolean succeeded)
    {
        log.debug("Publishing a organization event.");
        OrganizationPersistenceEvent organizationPersistenceEvent =
                newOrganization ? new OrganizationAddEvent(source) : new OrganizationUpdatedEvent(source, ipAddress);
        organizationPersistenceEvent.setSucceeded(succeeded);
        eventPublisher.publishEvent(organizationPersistenceEvent);
    }

    public void publishOrganizationEvent(Organization source, boolean newOrganization, boolean succeeded)
    {
        log.debug("Publishing a organization event.");
        String ipAddress = AuthenticationUtils.getUserIpAddress();
        OrganizationModifiedEvent organizationPersistenceEvent = new OrganizationModifiedEvent(source, ipAddress);
        if (newOrganization)
        {
            organizationPersistenceEvent.setEventAction("created");
        } else
        {
            organizationPersistenceEvent.setEventAction("updated");
        }
        organizationPersistenceEvent.setSucceeded(succeeded);
        eventPublisher.publishEvent(organizationPersistenceEvent);
    }

}
