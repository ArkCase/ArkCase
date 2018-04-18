package com.armedia.acm.services.users.service;

import com.armedia.acm.services.users.model.event.AdHocGroupDeletedEvent;
import com.armedia.acm.services.users.model.event.LdapGroupCreatedEvent;
import com.armedia.acm.services.users.model.event.LdapGroupDeletedEvent;
import com.armedia.acm.services.users.model.group.AcmGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.scheduling.annotation.Async;

public class AcmGroupEventPublisher implements ApplicationEventPublisherAware
{

    private transient final Logger log = LoggerFactory.getLogger(getClass());

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
    public void publishAdHocGroupDeletedEvent(AcmGroup source) {
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
