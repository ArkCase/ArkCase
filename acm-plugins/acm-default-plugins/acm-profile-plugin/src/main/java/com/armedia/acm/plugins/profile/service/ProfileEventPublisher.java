package com.armedia.acm.plugins.profile.service;

import com.armedia.acm.auth.AcmAuthenticationDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.core.Authentication;

/**
 * Created by marjan.stefanoski on 16.10.2014.
 */
public class ProfileEventPublisher implements ApplicationEventPublisherAware {

    private ApplicationEventPublisher eventPublisher;
    private transient final Logger log = LoggerFactory.getLogger(getClass());


    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        eventPublisher = applicationEventPublisher;
    }


}
