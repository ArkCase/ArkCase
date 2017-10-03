package com.armedia.acm.auth;

import com.armedia.acm.core.model.AcmEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.core.Authentication;

public class AcmLoginFailureEventListener implements ApplicationEventPublisherAware, ApplicationListener<AbstractAuthenticationFailureEvent>
{
    private ApplicationEventPublisher applicationEventPublisher;
    private Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void onApplicationEvent(AbstractAuthenticationFailureEvent authenticationFailureEvent)
    {
        Authentication auth = authenticationFailureEvent.getAuthentication();
        log.debug("got a failed login event for username: [{}]", auth.getName());
        AcmEvent loginEvent = new LoginEvent(auth);
        loginEvent.setSucceeded(false);
        getApplicationEventPublisher().publishEvent(loginEvent);
    }

    public ApplicationEventPublisher getApplicationEventPublisher()
    {
        return applicationEventPublisher;
    }

}
