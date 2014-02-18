package com.armedia.acm.auth;


import com.armedia.acm.event.AcmEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;

public class AcmLoginSuccessEventListener
        implements ApplicationEventPublisherAware, ApplicationListener<InteractiveAuthenticationSuccessEvent>
{
    private ApplicationEventPublisher applicationEventPublisher;
    private Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void onApplicationEvent(InteractiveAuthenticationSuccessEvent authenticationSuccessEvent)
    {
        Authentication auth = authenticationSuccessEvent.getAuthentication();

        AcmEvent loginEvent = new LoginEvent(auth);
        loginEvent.setSucceeded(true);
        getApplicationEventPublisher().publishEvent(loginEvent);
    }

    public ApplicationEventPublisher getApplicationEventPublisher()
    {
        return applicationEventPublisher;
    }
}
