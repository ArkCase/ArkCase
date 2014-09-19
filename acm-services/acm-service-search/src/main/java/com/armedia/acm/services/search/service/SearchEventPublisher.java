package com.armedia.acm.services.search.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import com.armedia.acm.plugins.search.model.ApplicationSearchEvent;

public class SearchEventPublisher implements ApplicationEventPublisherAware {
    
    private ApplicationEventPublisher applicationEventPublisher;
    
    private transient final Logger log = LoggerFactory.getLogger(getClass());
    
    public void publishSearchEvent(ApplicationSearchEvent event)
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
