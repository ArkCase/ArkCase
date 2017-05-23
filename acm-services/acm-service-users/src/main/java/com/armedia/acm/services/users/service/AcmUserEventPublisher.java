package com.armedia.acm.services.users.service;

import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.event.LdapUserDeletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

public class AcmUserEventPublisher implements ApplicationEventPublisherAware
{
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    private ApplicationEventPublisher applicationEventPublisher;

    public void publishLdapUserDeletedEvent(AcmUser source)
    {
        if (log.isDebugEnabled())
        {
            log.debug("Publishing Ldap User deleted event.");
        }
        LdapUserDeletedEvent event = new LdapUserDeletedEvent(source, source.getUserId());
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
