package com.armedia.acm.auth;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AcmLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler implements ApplicationEventPublisherAware
{
    private ApplicationEventPublisher applicationEventPublisher;
    private AcmAuthenticationDetailsFactory authenticationDetailsFactory;

    @Override
    public void onLogoutSuccess(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            Authentication authentication) throws IOException, ServletException
    {
        // sometimes we can get here even when there is no actual authentication (e.g. someone
        // has invoked the logout url without having actually logged in)
        if ( authentication != null )
        {

            AcmAuthenticationDetails details = getAuthenticationDetailsFactory().buildDetails(httpServletRequest);
            AcmAuthentication auth = new AcmAuthentication(null, authentication.getCredentials(),
                    details, authentication.isAuthenticated(), authentication.getName());
            getApplicationEventPublisher().publishEvent(new LogoutEvent(auth));
        }

        super.onLogoutSuccess(httpServletRequest, httpServletResponse, authentication);
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public ApplicationEventPublisher getApplicationEventPublisher()
    {
        return applicationEventPublisher;
    }

    public AcmAuthenticationDetailsFactory getAuthenticationDetailsFactory()
    {
        return authenticationDetailsFactory;
    }

    public void setAuthenticationDetailsFactory(AcmAuthenticationDetailsFactory authenticationDetailsFactory)
    {
        this.authenticationDetailsFactory = authenticationDetailsFactory;
    }
}
