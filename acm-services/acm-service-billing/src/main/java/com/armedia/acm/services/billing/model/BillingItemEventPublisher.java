package com.armedia.acm.services.billing.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

/**
 * @author sasko.tanaskoski
 *
 */
public class BillingItemEventPublisher implements ApplicationEventPublisherAware
{
    private transient final Logger log = LoggerFactory.getLogger(getClass());
    private ApplicationEventPublisher applicationEventPublisher;

    public void publishNoteEvent(BillingItemEvent event)
    {
        getApplicationEventPublisher().publishEvent(event);
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
