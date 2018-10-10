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

import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.services.authenticationtoken.model.AuthenticationToken;
import com.armedia.acm.services.authenticationtoken.model.AuthenticationTokenConstants;
import com.armedia.acm.services.authenticationtoken.service.AuthenticationTokenService;
import com.armedia.acm.services.search.model.SearchConstants;
import com.armedia.acm.services.search.service.SearchResults;
import com.armedia.acm.services.users.service.group.GroupService;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.json.JSONArray;
import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedCredentialsNotFoundException;
import org.springframework.security.web.authentication.preauth.x509.SubjectDnX509PrincipalExtractor;
import org.springframework.security.web.authentication.preauth.x509.X509PrincipalExtractor;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Lookup cached authentications for token requests (token requests are requests that include an acm_ticket in the
 * query string), and setup the user session for both token requests and basic authentication requests.
 * <p>
 * These two functions must be in the same class since both tasks must be done in the Spring Security basic
 * authentication filter, and Spring Security allows only one filter in the Basic Authentication filter position.
 * <p>
 * If token request handling was done in some other filter position (e.g. as the pre-auth filter), then Spring
 * Security always causes an HTTP redirect to be sent to the client, so the client has to issue another request
 * (being sure to include the session cookie) that goes through another authentication chain. But by placing the token
 * handling in the basic authentication position, there is no redirect and the requested URL is activated right away.
 */
public class AcmBasicAndTokenAuthenticationFilter extends BasicAuthenticationFilter
{
    private final Logger log = LoggerFactory.getLogger(getClass());
    private AcmLoginSuccessOperations loginSuccessOperations;
    private AcmLoginSuccessEventListener loginSuccessEventListener;
    private AuthenticationTokenService authenticationTokenService;
    private MuleContextManager muleContextManager;
    private AcmGrantedAuthoritiesMapper acmGrantedAuthoritiesMapper;
    private GroupService groupService;

    /**
     * Constructor.
     *
     * @param authenticationManager
     *            authentication requests submitted here
     */
    public AcmBasicAndTokenAuthenticationFilter(AuthenticationManager authenticationManager)
    {
        super(authenticationManager);
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException
    {
        final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) res;

        AuthRequestType authRequestType = detectAuthRequestType(request);
        switch (authRequestType)
        {
        case AUTH_REQUEST_TYPE_TOKEN:
            tokenAuthentication(request, response);
            break;
        case AUTH_REQUEST_TYPE_EMAIL_TOKEN:
            emailTokenAuthentication(request);
            break;
        case AUTH_REQUEST_TYPE_CLIENT_CERT:
            certificateAuthentication(request);
            break;
        case AUTH_REQUEST_TYPE_BASIC:
            basicAuthentication(request, response, chain);
            return; // need to return here, Spring filter forwards down the chain itself
        case AUTH_REQUEST_TYPE_OTHER:
        default:
            break;
        }
        chain.doFilter(request, response);
    }

    @Override
    public void onSuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, Authentication authResult)
            throws IOException
    {
        super.onSuccessfulAuthentication(request, response, authResult);

        log.debug("[{}] has successfully logged in", authResult.getName());

        getLoginSuccessOperations().onSuccessfulAuthentication(request, authResult);

        InteractiveAuthenticationSuccessEvent event = new InteractiveAuthenticationSuccessEvent(authResult, getClass());
        getLoginSuccessEventListener().onApplicationEvent(event);
    }

    /**
     * ArkCase token authentication handler.
     *
     * @param request
     *            HTTP servlet request
     * @param response
     *            HTTP servlet response
     * @throws IOException
     *             on error
     */
    private void tokenAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        try
        {
            String token = ServletRequestUtils.getStringParameter(request, "acm_ticket");
            log.trace("Starting token authentication using acm_ticket [{}]", token);
            Authentication auth = getAuthenticationTokenService().getAuthenticationForToken(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
            onSuccessfulAuthentication(request, response, auth);
            log.trace("User [{}] successfully authenticated using acm_ticket [{}]", auth.getName(), token);
        }
        catch (IllegalArgumentException | ServletRequestBindingException e)
        {
            SecurityContextHolder.clearContext();
            log.warn("Authentication request failed", e);

            AuthenticationException authenticationException = new PreAuthenticatedCredentialsNotFoundException(e.getMessage(), e);
            onUnsuccessfulAuthentication(request, response, authenticationException);

            // by calling setStatus, and then NOT calling doFilter, processing stops and the client will get a 401
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
        }
    }

    /**
     * ArkCase email token authentication handler, used for file download.
     *
     * @param request
     *            HTTP servlet request
     * @throws ServletException
     *             on error
     */
    private void emailTokenAuthentication(HttpServletRequest request) throws ServletException
    {
        String emailToken = ServletRequestUtils.getStringParameter(request, "acm_email_ticket");
        if (emailToken != null)
        {
            AuthenticationToken authenticationToken = authenticationTokenService.findByKey(emailToken);
            if (authenticationToken != null)
            {
                if ((AuthenticationTokenConstants.ACTIVE).equals(authenticationToken.getStatus()))
                {
                    String fileId = ServletRequestUtils.getStringParameter(request, "ecmFileId");
                    if (emailToken.equals(authenticationToken.getKey())
                            && Objects.equals(fileId, authenticationToken.getFileId().toString()))
                    {
                        log.trace("Starting token authentication for email links using acm_email_ticket [{}]", emailToken);
                        int days = Days.daysBetween(new DateTime(authenticationToken.getCreated()), new DateTime()).getDays();
                        // token expires after 3 days
                        if (days > AuthenticationTokenService.EMAIL_TICKET_EXPIRATION_DAYS)
                        {
                            authenticationToken.setStatus(AuthenticationTokenConstants.EXPIRED);
                            authenticationToken.setModifier(authenticationToken.getCreator());
                            authenticationToken.setModified(new Date());
                            authenticationTokenService.saveAuthenticationToken(authenticationToken);
                            log.warn("Authentication token acm_email_ticket [{}] for user [{}] expired", emailToken,
                                    authenticationToken.getCreator());
                            return;
                        }
                        try
                        {
                            authenticateUser(request, authenticationToken.getCreator());
                            log.trace("User [{}] successfully authenticated using acm_email_ticket [{}]",
                                    authenticationToken.getCreator(), emailToken);
                        }
                        catch (MuleException e)
                        {
                            log.warn("User [{}] failed authenticating using acm_email_ticket [{}]", authenticationToken.getCreator(),
                                    emailToken);
                        }
                    }
                }
            }
        }
    }

    /**
     * ArkCase client certificate authentication handler.
     *
     * @param request
     *            HTTP servlet request
     */
    private void certificateAuthentication(HttpServletRequest request)
    {
        log.trace("Starting client certificate authentication");
        X509Certificate clientCert = extractX509ClientCertificate(request);
        // if using client certificate and not already authenticated
        if (SecurityContextHolder.getContext().getAuthentication() != null && SecurityContextHolder.getContext().getAuthentication()
                .isAuthenticated())
        {
            return;
        }
        // extract userId (Common Name attribute) from the certifcate
        X509PrincipalExtractor principalExtractor = new SubjectDnX509PrincipalExtractor();
        String userId = (String) principalExtractor.extractPrincipal(clientCert);
        try
        {
            authenticateUser(request, userId);
            log.trace("User [{}] successfully authenticated using client certificate", userId);
        }
        catch (MuleException e)
        {
            log.warn("User [{}] failed authenticating using client certificate", userId);
        }
    }

    /**
     * ArkCase basic authentication handler.
     *
     * @param request
     *            HTTP servlet request
     * @param response
     *            HTTP servlet response
     * @param chain
     *            Filter chain
     */
    private void basicAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException
    {
        log.trace("Starting basic authentication");
        // let Spring Security's native basic authentication do the work.
        super.doFilter(request, response, chain);
    }

    /**
     * Create user authentication token.
     *
     * @param request
     *            HTTP servlet request
     * @param userId
     *            user identifier
     * @throws MuleException
     *             on error while retrieving LDAP groups
     */
    private void authenticateUser(HttpServletRequest request, String userId) throws MuleException
    {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userId, userId);
        String ldapGroups = groupService.getLdapGroupsForUser(usernamePasswordAuthenticationToken);
        if (ldapGroups != null)
        {
            SearchResults searchResults = new SearchResults();
            JSONArray docs = searchResults.getDocuments(ldapGroups);
            List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
            if (docs != null)
            {
                for (int i = 0; i < docs.length(); i++)
                {
                    String groupName = searchResults.extractString(docs.getJSONObject(i), SearchConstants.PROPERTY_NAME);
                    List<String> ascendants = searchResults.extractStringList(docs.getJSONObject(i), SearchConstants.PROPERTY_ASCENDANTS);
                    GrantedAuthority authority = new SimpleGrantedAuthority(groupName);
                    grantedAuthorities.add(authority);
                    if (ascendants != null)
                    {
                        ascendants.stream()
                                .map(SimpleGrantedAuthority::new)
                                .forEach(grantedAuthorities::add);
                    }
                }
            }
            grantedAuthorities.addAll(acmGrantedAuthoritiesMapper.mapAuthorities(grantedAuthorities));
            Authentication authentication = new UsernamePasswordAuthenticationToken(userId, userId, grantedAuthorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);

            loginSuccessOperations.onSuccessfulAuthentication(request, authentication);

            InteractiveAuthenticationSuccessEvent event = new InteractiveAuthenticationSuccessEvent(authentication, getClass());
            loginSuccessEventListener.onApplicationEvent(event);
        }
    }

    /**
     * Extract client certificate from the request.
     *
     * @param request
     *            incoming HTTP request
     * @return the client certificate
     */
    private X509Certificate extractX509ClientCertificate(HttpServletRequest request)
    {
        X509Certificate x509Certificate = null;
        X509Certificate[] certs = (X509Certificate[]) request.getAttribute("javax.servlet.request.X509Certificate");

        if (certs != null && certs.length > 0)
        {
            x509Certificate = certs[0];
            log.debug("X.509 client authentication certificate [{}]", x509Certificate);
        }
        return x509Certificate;
    }

    /**
     * Detect authentication request type.
     *
     * @param request
     *            HTTP Servlet Request
     * @return detected authentication request type
     */
    private AuthRequestType detectAuthRequestType(HttpServletRequest request)
    {
        AuthRequestType authRequestType = AuthRequestType.AUTH_REQUEST_TYPE_OTHER;

        if (request.getParameter("acm_ticket") != null)
        {
            log.trace("Token authentication requested");
            authRequestType = AuthRequestType.AUTH_REQUEST_TYPE_TOKEN;
        }
        else if (request.getParameter("acm_email_ticket") != null)
        {
            log.trace("Email token authentication requested");
            authRequestType = AuthRequestType.AUTH_REQUEST_TYPE_EMAIL_TOKEN;
        }
        else if (request.getAttribute("javax.servlet.request.X509Certificate") != null)
        {
            log.trace("Client Certificate authentication requested");
            authRequestType = AuthRequestType.AUTH_REQUEST_TYPE_CLIENT_CERT;
        }
        else if (request.getHeader("Authorization") != null && request.getHeader("Authorization").startsWith("Basic "))
        {
            log.trace("Basic authentication requested");
            authRequestType = AuthRequestType.AUTH_REQUEST_TYPE_BASIC;
        }
        else
        {
            log.trace("Neither token, basic nor certificate authentication requested, skipping");
        }

        return authRequestType;
    }

    public AcmLoginSuccessOperations getLoginSuccessOperations()
    {
        return loginSuccessOperations;
    }

    public void setLoginSuccessOperations(AcmLoginSuccessOperations loginSuccessOperations)
    {
        this.loginSuccessOperations = loginSuccessOperations;
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

    public MuleContextManager getMuleContextManager()
    {
        return muleContextManager;
    }

    public void setMuleContextManager(MuleContextManager muleContextManager)
    {
        this.muleContextManager = muleContextManager;
    }

    public AcmGrantedAuthoritiesMapper getAcmGrantedAuthoritiesMapper()
    {
        return acmGrantedAuthoritiesMapper;
    }

    public void setAcmGrantedAuthoritiesMapper(AcmGrantedAuthoritiesMapper acmGrantedAuthoritiesMapper)
    {
        this.acmGrantedAuthoritiesMapper = acmGrantedAuthoritiesMapper;
    }

    public GroupService getGroupService()
    {
        return groupService;
    }

    public void setGroupService(GroupService groupService)
    {
        this.groupService = groupService;
    }

    private enum AuthRequestType
    {
        AUTH_REQUEST_TYPE_TOKEN,
        AUTH_REQUEST_TYPE_EMAIL_TOKEN,
        AUTH_REQUEST_TYPE_CLIENT_CERT,
        AUTH_REQUEST_TYPE_BASIC,
        AUTH_REQUEST_TYPE_OTHER
    }
}
