package com.armedia.acm.configuration.core.propertysource;

/*-
 * #%L
 * ACM Tool Integrations: Configuration Library
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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

import com.armedia.acm.configuration.model.AcmLdapBeanSyncEvent;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

/**
 * @author Mario Gjurcheski 5/21/2020
 */
public class AcmLdapBeanSyncEventPublisher implements ApplicationEventPublisherAware
{

    private ApplicationEventPublisher eventPublisher;

    public void publishAcmLdapBeanSyncEventAdded(AcmLdapBeanSyncEvent event)
    {
        eventPublisher.publishEvent(event);
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        eventPublisher = applicationEventPublisher;
    }
}
