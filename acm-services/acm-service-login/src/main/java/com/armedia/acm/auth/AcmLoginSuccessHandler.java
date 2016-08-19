package com.armedia.acm.auth;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AcmLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private AcmLoginSuccessOperations loginSuccessOperations;
    private SessionRegistry sessionRegistry;
    private SessionAuthenticationStrategy sessionAuthenticationStrategy;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws ServletException, IOException
    {
        if ( log.isDebugEnabled() )
        {
            log.debug("Authentication details is of type: " + (authentication.getDetails() == null ? null :
                    authentication.getDetails().getClass().getName()) );
        }

        getLoginSuccessOperations().onSuccessfulAuthentication(request, authentication);

        super.onAuthenticationSuccess(request, response, authentication);
        sessionRegistry.registerNewSession(request.getSession().getId(), authentication.getPrincipal());
        sessionAuthenticationStrategy.onAuthentication(authentication, request, response);
    }


    public AcmLoginSuccessOperations getLoginSuccessOperations()
    {
        return loginSuccessOperations;
    }

    public void setLoginSuccessOperations(AcmLoginSuccessOperations loginSuccessOperations)
    {
        this.loginSuccessOperations = loginSuccessOperations;
    }

    public void setSessionRegistry(SessionRegistry sessionRegistry)
    {
        this.sessionRegistry = sessionRegistry;
    }

    public void setSessionAuthenticationStrategy(SessionAuthenticationStrategy sessionAuthenticationStrategy)
    {
        this.sessionAuthenticationStrategy = sessionAuthenticationStrategy;
    }
}
