package com.armedia.acm.services.signature.service;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.core.Authentication;

import com.armedia.acm.services.signature.model.ApplicationSignatureEvent;
import com.armedia.acm.services.signature.model.Signature;

public class SignatureEventPublisher  implements ApplicationEventPublisherAware
{
    private ApplicationEventPublisher applicationEventPublisher;
    
    private transient final Logger log = LoggerFactory.getLogger(getClass());
    
    public void publishSignatureEvent(ApplicationSignatureEvent event)
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
