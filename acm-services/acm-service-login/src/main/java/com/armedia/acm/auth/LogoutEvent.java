package com.armedia.acm.auth;


import org.springframework.security.core.Authentication;

public class LogoutEvent extends AcmAbstractAuthenticationEvent
{
    private static final String EVENT_TYPE = "com.armedia.acm.logout";

    public LogoutEvent(Authentication authentication)
    {
        super(authentication);

        setSucceeded(true);

    }

    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }
}
