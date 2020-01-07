package com.armedia.acm.services.users.service;

/*-
 * #%L
 * ACM Service: Users
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

import com.armedia.acm.services.users.model.event.AdHocGroupDeletedEvent;
import com.armedia.acm.services.users.model.event.LdapGroupCreatedEvent;
import com.armedia.acm.services.users.model.event.LdapGroupDeletedEvent;
import com.armedia.acm.services.users.model.group.AcmGroup;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.scheduling.annotation.Async;

public class AcmGroupEventPublisher implements ApplicationEventPublisherAware
{

    private transient final Logger log = LogManager.getLogger(getClass());

    private ApplicationEventPublisher applicationEventPublisher;

    @Async
    public void publishLdapGroupDeletedEvent(AcmGroup source)
    {
        log.debug("Publishing LDAP group: [{}] deleted event.", source.getName());
        LdapGroupDeletedEvent event = new LdapGroupDeletedEvent(source);
        event.setSucceeded(true);
        applicationEventPublisher.publishEvent(event);
    }

    @Async
    public void publishLdapGroupCreatedEvent(AcmGroup source)
    {
        log.debug("Publishing LDAP group: [{}] created event.", source.getName());
        LdapGroupCreatedEvent event = new LdapGroupCreatedEvent(source, source.getName());
        event.setSucceeded(true);
        applicationEventPublisher.publishEvent(event);
    }

    @Async
    public void publishAdHocGroupDeletedEvent(AcmGroup source)
    {
        log.debug("Publishing ADHOC group: [{}] deleted event.", source.getName());
        AdHocGroupDeletedEvent event = new AdHocGroupDeletedEvent(source);
        event.setSucceeded(true);
        applicationEventPublisher.publishEvent(event);
    }

    public ApplicationEventPublisher getApplicationEventPublisher()
    {
        return applicationEventPublisher;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
