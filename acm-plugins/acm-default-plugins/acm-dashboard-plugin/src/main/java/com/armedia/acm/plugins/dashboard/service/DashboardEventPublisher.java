package com.armedia.acm.plugins.dashboard.service;

/*-
 * #%L
 * ACM Default Plugin: Dashboard
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

import com.armedia.acm.auth.AcmAuthenticationDetails;
import com.armedia.acm.plugins.dashboard.model.Dashboard;
import com.armedia.acm.plugins.dashboard.model.DashboardCreatedEvent;
import com.armedia.acm.plugins.dashboard.model.DashboardPersistenceEvent;
import com.armedia.acm.plugins.dashboard.model.DashboardUpdatedEvent;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.core.Authentication;

public class DashboardEventPublisher implements ApplicationEventPublisherAware
{

    private transient final Logger log = LogManager.getLogger(getClass());
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
