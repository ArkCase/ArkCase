package com.armedia.acm.auth;

import org.springframework.security.core.Authentication;

public class LoginEvent extends AcmAbstractAuthenticationEvent
{

    private static final String EVENT_TYPE = "com.armedia.acm.login";


    public LoginEvent(Authentication authentication)
    {
        super(authentication);
    }

    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }
}
