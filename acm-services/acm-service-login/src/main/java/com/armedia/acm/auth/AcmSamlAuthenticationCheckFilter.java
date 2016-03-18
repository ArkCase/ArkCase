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

    private long authenticationCheckInterval;

    private static final String FILTER_APPLIED = "__spring_security_filterAuthenticationCheck_filterApplied";
    private static final String LAST_AUTHENTICATION_CHECK = "acmLastAuthenticationCheck";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
    {
        // if this is not a Single Sign-On scenario (samlAuthenticationProvider is null) don't use the filter
        ApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        if (applicationContext.getBean("samlAuthenticationProvider") == null)
        {
            chain.doFilter(request, response);
            return;
        }

        boolean debug = log.isDebugEnabled();
        if (debug)
        {
            log.debug("AcmAuthenticationCheckFilter started!");
        }
        if ((request != null) && (request.getAttribute(FILTER_APPLIED) != null))
        {
            chain.doFilter(request, response);
        }
        else
        {
            if (request != null)
            {
                request.setAttribute(FILTER_APPLIED, Boolean.TRUE);
            }
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if ((authentication == null) || (!authentication.isAuthenticated()))
        {
            if (debug)
            {
                log.debug("User not authenticated yet!");
            }
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = ((HttpServletRequest) request).getSession();

        LocalDateTime now = LocalDateTime.now();
        if (debug)
        {
            log.debug("Now: " + now.toString());
        }
        if (session.getAttribute(LAST_AUTHENTICATION_CHECK) == null)
        {
            log.info("Setting LAST_AUTHENTICATION_CHECK: " + now.toString());
            session.setAttribute(LAST_AUTHENTICATION_CHECK, now);
        }

        // check user authentication against IDP if LAST_AUTHENTICATION_CHECK has expired
        LocalDateTime lastAuthenticationCheck = (LocalDateTime) session.getAttribute(LAST_AUTHENTICATION_CHECK);
        if (debug)
        {
            log.debug("LAST_AUTHENTICATION_CHECK: " + lastAuthenticationCheck.toString());
        }
        if (lastAuthenticationCheck.plusSeconds(authenticationCheckInterval).isBefore(now))
        {
            if (debug)
            {
                log.debug("LAST_AUTHENTICATION_CHECK.plusSeconds(authenticationCheckInterval): "
                        + lastAuthenticationCheck.plusSeconds(authenticationCheckInterval));
            }
            session.setAttribute(LAST_AUTHENTICATION_CHECK, LocalDateTime.now());
            SecurityContextHolder.getContext().setAuthentication(null);
        }

        chain.doFilter(request, response);
    }

    /**
     * @return the authenticationCheckInterval
     */
    public long getAuthenticationCheckInterval()
    {
        return authenticationCheckInterval;
    }

    /**
     * @param authenticationCheckInterval
     *            the authenticationCheckInterval to set
     */
    public void setAuthenticationCheckInterval(long authenticationCheckInterval)
    {
        this.authenticationCheckInterval = authenticationCheckInterval;
    }
}
