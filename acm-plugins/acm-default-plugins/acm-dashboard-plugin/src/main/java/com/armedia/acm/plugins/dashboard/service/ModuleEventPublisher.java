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

import com.armedia.acm.plugins.dashboard.model.module.Module;
import com.armedia.acm.plugins.dashboard.model.module.ModuleConstants;
import com.armedia.acm.plugins.dashboard.model.module.ModuleCreatedEvent;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.core.Authentication;

/**
 * Created by marjan.stefanoski on 19.01.2016.
 */
public class ModuleEventPublisher implements ApplicationEventPublisherAware
{
    private transient final Logger log = LogManager.getLogger(getClass());
    private ApplicationEventPublisher eventPublisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        eventPublisher = applicationEventPublisher;
    }

    public void publishModuleCreated(Module source, Authentication authentication, String ipAddress, boolean succeeded)
    {

        log.debug("Publishing a module name event. New Module Name Created Event.");

        String userId = authentication == null ? ModuleConstants.ON_BOOT_USER_NAME : authentication.getName();
        String ipAddr = ipAddress == null ? ModuleConstants.LOOPBACK_IP_ADDRESS : ipAddress;

        ModuleCreatedEvent moduleCreatedEvent = new ModuleCreatedEvent(source, userId);
        moduleCreatedEvent.setIpAddress(ipAddr);
        moduleCreatedEvent.setSucceeded(succeeded);

        eventPublisher.publishEvent(moduleCreatedEvent);
    }

    public ApplicationEventPublisher getEventPublisher()
    {
        return eventPublisher;
    }

    public void setEventPublisher(ApplicationEventPublisher eventPublisher)
    {
        this.eventPublisher = eventPublisher;
    }
}
