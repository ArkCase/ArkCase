package com.armedia.acm.services.users.service;


import com.armedia.acm.services.users.model.event.LdapGroupDeletedEvent;
import com.armedia.acm.services.users.model.group.AcmGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

public class AcmGroupEventPublisher implements ApplicationEventPublisherAware
{

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    private ApplicationEventPublisher applicationEventPublisher;

    public void publishLdapGroupDeletedEvent(AcmGroup source)
    {
        log.debug("Publishing LDAP group: {} deleted event.", source.getName());
        LdapGroupDeletedEvent event = new LdapGroupDeletedEvent(source, source.getName());
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
