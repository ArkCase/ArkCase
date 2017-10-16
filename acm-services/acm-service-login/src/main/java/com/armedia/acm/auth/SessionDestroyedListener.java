package com.armedia.acm.auth;

import com.armedia.acm.services.authenticationtoken.service.AuthenticationTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.session.SessionDestroyedEvent;

/**
 * Clear authentication tickets once session has ended
 **/
public class SessionDestroyedListener implements ApplicationListener<SessionDestroyedEvent>
{
    private transient final Logger LOG = LoggerFactory.getLogger(getClass());
    private AuthenticationTokenService authenticationTokenService;

    @Override
    public void onApplicationEvent(SessionDestroyedEvent sessionDestroyedEvent)
    {
        String invalidatedSessionId = sessionDestroyedEvent.getId();
        if (invalidatedSessionId != null)
        {
            LOG.debug("Invalidate all acm_tickets for session with id:{}", invalidatedSessionId);
            authenticationTokenService.purgeTokenForAuthenticationPerSession(invalidatedSessionId);
        }
    }

    public AuthenticationTokenService getAuthenticationTokenService()
    {
        return authenticationTokenService;
    }

    public void setAuthenticationTokenService(AuthenticationTokenService authenticationTokenService)
    {
        this.authenticationTokenService = authenticationTokenService;
    }
}