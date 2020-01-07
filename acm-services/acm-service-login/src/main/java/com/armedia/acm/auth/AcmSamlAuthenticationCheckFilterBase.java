package com.armedia.acm.auth;

/*-
 * #%L
 * ACM Service: User Login and Authentication
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Base class for filters that clears the {@link Authentication} object from the {@link SecurityContext}. Only used in
 * the Single Sign-On
 * scenario.
 * <p>
 * Created by Bojan Milenkoski on 12.4.2016
 */
abstract class AcmSamlAuthenticationCheckFilterBase extends GenericFilterBean
{
    private static final String FILTER_APPLIED = "__spring_security_filterAuthenticationCheck_filterApplied";
    private static final String LAST_AUTHENTICATION_CHECK = "acmLastAuthenticationCheck";
    private Logger log = LogManager.getLogger(getClass());
    private long authenticationCheckIntervalInSeconds;
    private RequestCache requestCache = new HttpSessionRequestCache();

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

        if (request != null)
        {
            // filter is executing, set FILTER_APPLIED to true
            request.setAttribute(FILTER_APPLIED, Boolean.TRUE);

            if (!userAuthenticated())
            {
                // all non authenticated requests should be redirected to '/samllogin' in order to save the hash in
                // session
                // storage on the
                // client browser. This is used for non-Rest calls. See: AcmSamlAuthenticationCheckFilter class
                if (!((HttpServletRequest) request).getRequestURI().equals(request.getServletContext().getContextPath() + "/samllogin")
                        && !((HttpServletRequest) request).getRequestURI()
                                .startsWith(request.getServletContext().getContextPath() + "/saml/"))
                {
                    if (shouldRedirectToLoginPage())
                    {
                        // save last requested URL in session
                        requestCache.saveRequest((HttpServletRequest) request, (HttpServletResponse) response);
                        ((HttpServletResponse) response).sendRedirect(request.getServletContext().getContextPath() + "/samllogin");
                        return;
                    }
                }

                chain.doFilter(request, response);
                return;
            }

            LocalDateTime now = LocalDateTime.now();
            log.debug("Now: {}", now.toString());

            HttpSession session = ((HttpServletRequest) request).getSession();

            setLastAuthenticationCheckTime(session, now);

            // check user authentication against IDP only on GET methods and when LAST_AUTHENTICATION_CHECK has expired
            if ("GET".equalsIgnoreCase(((HttpServletRequest) request).getMethod()) && timeToReauthenticate(session, now))
            {
                log.debug("Reauthenticating user!");
                session.setAttribute(LAST_AUTHENTICATION_CHECK, LocalDateTime.now());
                SecurityContextHolder.getContext().setAuthentication(null);
            }

            chain.doFilter(request, response);
        }

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

    private boolean userAuthenticated()
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

    abstract public boolean shouldRedirectToLoginPage();

    /**
     * @return the requestCache
     */
    public RequestCache getRequestCache()
    {
        return requestCache;
    }

    /**
     * @param requestCache
     *            the requestCache to set
     */
    public void setRequestCache(RequestCache requestCache)
    {
        this.requestCache = requestCache;
    }
}
