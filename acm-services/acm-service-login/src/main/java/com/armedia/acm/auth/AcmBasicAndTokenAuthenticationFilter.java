package com.armedia.acm.auth;

import com.armedia.acm.services.authenticationtoken.service.AuthenticationTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedCredentialsNotFoundException;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.bind.ServletRequestUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Lookup cached authentications for token requests (token requests are requests that include an acm_ticket in the
 * query string), and setup the user session for both token requests and basic authentication requests.
 *
 * These two functions must be in the same class since both tasks must be done in the Spring Security basic
 * authentication filter, and Spring Security allows only one filter in the Basic Authentication filter position.
 *
 * If token request handling was done in some other filter position (e.g. as the pre-auth filter), then Spring
 * Security always causes an HTTP redirect to be sent to the client, so the client has to issue another request
 * (being sure to include the session cookie) that goes through another authentication chain.  But by placing the token
 * handling in the basic authentication position, there is no redirect and the requested URL is activated right away.
 *
 */
public class AcmBasicAndTokenAuthenticationFilter extends BasicAuthenticationFilter
{
    private final Logger log = LoggerFactory.getLogger(getClass());
    private AcmLoginSuccessOperations loginSuccessOperations;
    private AcmLoginSuccessEventListener loginSuccessEventListener;
    private AuthenticationTokenService authenticationTokenService;

    public AcmBasicAndTokenAuthenticationFilter(AuthenticationManager authenticationManager)
    {
        super(authenticationManager);
    }

    @Override
    public void onSuccessfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authResult) throws IOException
    {
        super.onSuccessfulAuthentication(request, response, authResult);

        if  ( log.isDebugEnabled() )
        {
            log.debug(authResult.getName() + " has logged in via basic authentication.");
        }

        getLoginSuccessOperations().onSuccessfulAuthentication(request, authResult);

        InteractiveAuthenticationSuccessEvent event = new InteractiveAuthenticationSuccessEvent(authResult, getClass());
        getLoginSuccessEventListener().onApplicationEvent(event);

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException
    {
        final boolean trace = logger.isTraceEnabled();
        final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) res;

        // see whether this request is either a Basic Authentication request or a token request
        boolean basicAuthRequest = isBasicAuthRequest(request);

        String token = ServletRequestUtils.getStringParameter(request, "acm_ticket");
        boolean tokenRequest = token != null;

        if ( ! tokenRequest && ! basicAuthRequest)
        {
            if ( trace )
            {
                log.trace("neither token nor basic - skipping.");
            }
            chain.doFilter(request, response);
            return;
        }

        if ( basicAuthRequest )
        {
            if ( trace )
            {
                log.trace("switching to basic auth");
            }
            // let Spring Security's native basic authentication do the work.
            super.doFilter(req, res, chain);
            return;
        }

        // if we get here, it's a token request
        try
        {
            if ( trace )
            {
                log.trace("starting token auth");
            }
            Authentication auth;
            try
            {
                auth = getAuthenticationTokenService().getAuthenticationForToken(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
                onSuccessfulAuthentication(request, response, auth);
            }
            catch (IllegalArgumentException e)
            {
                throw new PreAuthenticatedCredentialsNotFoundException(e.getMessage(), e);
            }
        }
        catch (AuthenticationException failed)
        {
            SecurityContextHolder.clearContext();
            if ( trace )
            {
                logger.trace("Authentication request failed: " + failed);
            }

            onUnsuccessfulAuthentication(request, response, failed);

            if ( isIgnoreFailure() )
            {
                chain.doFilter(request, response);
            }
            else
            {
                getAuthenticationEntryPoint().commence(request, response, failed);
            }

            return;
        }

        chain.doFilter(request, response);


    }

    private boolean isBasicAuthRequest(HttpServletRequest request)
    {
        String header = request.getHeader("Authorization");
        return header != null && header.startsWith("Basic ");
    }

    public void setLoginSuccessOperations(AcmLoginSuccessOperations loginSuccessOperations)
    {
        this.loginSuccessOperations = loginSuccessOperations;
    }

    public AcmLoginSuccessOperations getLoginSuccessOperations()
    {
        return loginSuccessOperations;
    }

    public AcmLoginSuccessEventListener getLoginSuccessEventListener()
    {
        return loginSuccessEventListener;
    }

    public void setLoginSuccessEventListener(AcmLoginSuccessEventListener loginSuccessEventListener)
    {
        this.loginSuccessEventListener = loginSuccessEventListener;
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
