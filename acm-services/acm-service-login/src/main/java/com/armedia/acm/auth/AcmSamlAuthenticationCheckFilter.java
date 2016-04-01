package com.armedia.acm.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Filter that clears the {@link Authentication} object from the {@link SecurityContext} and causes authentication check of the user against
 * ADFS server. Only used in the Single Sign-On scenario.
 * <p>
 * Created by Bojan Milenkoski on 14.3.2016
 */
public class AcmSamlAuthenticationCheckFilter extends GenericFilterBean
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private long authenticationCheckIntervalInSeconds;

    private static final String FILTER_APPLIED = "__spring_security_filterAuthenticationCheck_filterApplied";
    private static final String LAST_AUTHENTICATION_CHECK = "acmLastAuthenticationCheck";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
    {
        // if this is not a Single Sign-On scenario (samlAuthenticationProvider is null) don't use the filter
        // this filter should be executed only when the request comes from the client, not when the response is served
        if (!samlSSOEnabled() || filterApplied(request))
        {
            chain.doFilter(request, response);
            return;
        }

        log.debug("AcmSamlAuthenticationCheckFilter started!");

        // filter is executing, set FILTER_APPLIED to true
        request.setAttribute(FILTER_APPLIED, Boolean.TRUE);

        if (!userAuthentcated())
        {
            chain.doFilter(request, response);
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        log.debug("Now: {}", now.toString());

        HttpSession session = ((HttpServletRequest) request).getSession();

        setLastAuthenticationCheckTime(session, now);

        // check user authentication against IDP if LAST_AUTHENTICATION_CHECK has expired
        if (timeToReauthenticate(session, now))
        {
            log.debug("Reauthenticating user!");
            session.setAttribute(LAST_AUTHENTICATION_CHECK, LocalDateTime.now());
            SecurityContextHolder.getContext().setAuthentication(null);
        }

        chain.doFilter(request, response);
    }

    private boolean timeToReauthenticate(HttpSession session, LocalDateTime now)
    {
        LocalDateTime lastAuthenticationCheck = (LocalDateTime) session.getAttribute(LAST_AUTHENTICATION_CHECK);
        log.debug("LAST_AUTHENTICATION_CHECK: {}", lastAuthenticationCheck.toString());

        return lastAuthenticationCheck.plusSeconds(authenticationCheckIntervalInSeconds).isBefore(now);
    }

    private void setLastAuthenticationCheckTime(HttpSession session, LocalDateTime now)
    {
        if (session.getAttribute(LAST_AUTHENTICATION_CHECK) == null)
        {
            log.debug("Setting LAST_AUTHENTICATION_CHECK: {}", now.toString());
            session.setAttribute(LAST_AUTHENTICATION_CHECK, now);
        }
    }

    private boolean userAuthentcated()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean authenticated = (authentication != null) && authentication.isAuthenticated();

        if (authenticated)
        {
            log.debug("User: {}  authenticated.", authentication.getName());
        }
        else
        {
            log.debug("User not authenticated yet!");
        }

        return authenticated;
    }

    private boolean filterApplied(ServletRequest request)
    {
        return (request != null) && (request.getAttribute(FILTER_APPLIED) != null);
    }

    private boolean samlSSOEnabled()
    {
        // TODO Check profile
        ApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        return applicationContext.getBean("samlAuthenticationProvider") != null;
    }

    /**
     * @return the authenticationCheckIntervalInSeconds
     */
    public long getAuthenticationCheckIntervalInSeconds()
    {
        return authenticationCheckIntervalInSeconds;
    }

    /**
     * @param authenticationCheckIntervalInSeconds
     *            the authenticationCheckIntervalInSeconds to set
     */
    public void setAuthenticationCheckIntervalInSeconds(long authenticationCheckIntervalInSeconds)
    {
        this.authenticationCheckIntervalInSeconds = authenticationCheckIntervalInSeconds;
    }
}
