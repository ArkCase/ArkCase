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

import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.plugins.person.model.OrganizationEvent;
import com.armedia.acm.plugins.person.model.PersonOrganizationConstants;
import com.armedia.acm.service.objecthistory.service.AcmObjectHistoryService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationListener;

import java.util.List;

/**
 * Created by nick.ferguson on 6/1/2017.
 */
public class OrganizationHistoryListener implements ApplicationListener<OrganizationEvent>
{
    private final Logger log = LogManager.getLogger(getClass());
    private AcmObjectHistoryService acmObjectHistoryService;
    private List<String> nonHistoryGeneratingEvents;

    @Override
    public void onApplicationEvent(OrganizationEvent event)
    {
        log.debug("Organization event raised. Start adding to Object History...");
        if (event != null)
        {
            if (!getNonHistoryGeneratingEvents().contains(event.getEventType()))
            {
                Organization organization = (Organization) event.getSource();

                getAcmObjectHistoryService().save(event.getUserId(), event.getEventType(), organization, organization.getId(),
                        PersonOrganizationConstants.ORGANIZATION_OBJECT_TYPE, event.getEventDate(), event.getIpAddress(),
                        event.isSucceeded());
            }
        }
    }

    public AcmObjectHistoryService getAcmObjectHistoryService()
    {
        return acmObjectHistoryService;
    }

    public void setAcmObjectHistoryService(AcmObjectHistoryService acmObjectHistoryService)
    {
        this.acmObjectHistoryService = acmObjectHistoryService;
    }

    public List<String> getNonHistoryGeneratingEvents()
    {
        return nonHistoryGeneratingEvents;
    }

    public void setNonHistoryGeneratingEvents(List<String> nonHistoryGeneratingEvents)
    {
        this.nonHistoryGeneratingEvents = nonHistoryGeneratingEvents;
    }
}
