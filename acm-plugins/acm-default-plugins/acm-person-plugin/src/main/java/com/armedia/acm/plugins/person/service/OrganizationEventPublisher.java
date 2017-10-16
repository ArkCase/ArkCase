package com.armedia.acm.plugins.person.service;

import com.armedia.acm.auth.AuthenticationUtils;
import com.armedia.acm.objectdiff.model.AcmDiff;
import com.armedia.acm.objectdiff.service.AcmDiffService;
import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.plugins.person.model.OrganizationEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

public class OrganizationEventPublisher implements ApplicationEventPublisherAware
{
    private transient final Logger log = LoggerFactory.getLogger(getClass());
    private ApplicationEventPublisher eventPublisher;
    private AcmDiffService acmDiffService;

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

    public void publishOrganizationUpsertEvent(Organization updatedOrganization, Organization oldOrganization, boolean newOrganization, boolean succeeded)
    {
        log.debug("Publishing a organization event.");
        String ipAddress = AuthenticationUtils.getUserIpAddress();
        OrganizationEvent organizationEvent = new OrganizationEvent(updatedOrganization);
        organizationEvent.setIpAddress(ipAddress);
        if (newOrganization)
        {
            organizationEvent.setEventStatus("created");
        } else
        {
            AcmDiff acmDiff = acmDiffService.compareObjects(oldOrganization, updatedOrganization);
            if (acmDiff != null)
            {
                try
                {
                    organizationEvent.setDiffDetailsAsJson(acmDiff.getChangesAsListJson());
                } catch (JsonProcessingException e)
                {
                    log.warn("can't process diff details for [{}].", updatedOrganization, e);
                }
            }
            organizationEvent.setEventStatus("updated");
        }
        organizationEvent.setSucceeded(succeeded);
        eventPublisher.publishEvent(organizationEvent);
    }

    public void setAcmDiffService(AcmDiffService acmDiffService)
    {
        this.acmDiffService = acmDiffService;
    }
}
