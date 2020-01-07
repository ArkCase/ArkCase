package com.armedia.acm.plugins.person.service;

/*-
 * #%L
 * ACM Default Plugin: Person
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

import com.armedia.acm.auth.AuthenticationUtils;
import com.armedia.acm.objectdiff.model.AcmDiff;
import com.armedia.acm.objectdiff.service.AcmDiffService;
import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.plugins.person.model.OrganizationEvent;
import com.fasterxml.jackson.core.JsonProcessingException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

public class OrganizationEventPublisher implements ApplicationEventPublisherAware
{
    private transient final Logger log = LogManager.getLogger(getClass());
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

    public void publishOrganizationUpsertEvent(Organization updatedOrganization, Organization oldOrganization, boolean newOrganization,
            boolean succeeded)
    {
        log.debug("Publishing a organization event.");
        String ipAddress = AuthenticationUtils.getUserIpAddress();
        OrganizationEvent organizationEvent = new OrganizationEvent(updatedOrganization);
        organizationEvent.setIpAddress(ipAddress);
        if (newOrganization)
        {
            organizationEvent.setEventStatus("created");
        }
        else
        {
            AcmDiff acmDiff = acmDiffService.compareObjects(oldOrganization, updatedOrganization);
            if (acmDiff != null)
            {
                try
                {
                    organizationEvent.setDiffDetailsAsJson(acmDiff.getChangesAsListJson());
                }
                catch (JsonProcessingException e)
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
