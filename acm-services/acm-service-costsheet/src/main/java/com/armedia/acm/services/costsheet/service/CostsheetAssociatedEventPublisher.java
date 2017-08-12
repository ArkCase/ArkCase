package com.armedia.acm.services.costsheet.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import com.armedia.acm.services.costsheet.model.AcmCostsheetAssociatedEvent;

public class CostsheetAssociatedEventPublisher implements ApplicationEventPublisherAware
{

    private Logger LOG = LoggerFactory.getLogger(getClass());
    
    private ApplicationEventPublisher applicationEventPublisher;
    
    
    public void publishEvent(AcmCostsheetAssociatedEvent event) 
    {
        LOG.debug("Publishing AcmCostsheetAssociated Event");
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
