package com.armedia.acm.auth;

import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.services.authenticationtoken.dao.AuthenticationTokenDao;
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

/**
 * Lookup cached authentications for token requests (token requests are requests that include an acm_ticket in the
 * query string), and setup the user session for both token requests and basic authentication requests.
 * <p>
 * These two functions must be in the same class since both tasks must be done in the Spring Security basic
 * authentication filter, and Spring Security allows only one filter in the Basic Authentication filter position.
 * <p>
 * If token request handling was done in some other filter position (e.g. as the pre-auth filter), then Spring
 * Security always causes an HTTP redirect to be sent to the client, so the client has to issue another request
 * (being sure to include the session cookie) that goes through another authentication chain.  But by placing the token
 * handling in the basic authentication position, there is no redirect and the requested URL is activated right away.
 */
public class AcmBasicAndTokenAuthenticationFilter extends BasicAuthenticationFilter
{
    private final Logger log = LoggerFactory.getLogger(getClass());
    private AcmLoginSuccessOperations loginSuccessOperations;
    private AcmLoginSuccessEventListener loginSuccessEventListener;
    private AuthenticationTokenService authenticationTokenService;
    private AuthenticationTokenDao authenticationTokenDao;
    private MuleContextManager muleContextManager;
    private AcmGrantedAuthoritiesMapper acmGrantedAuthoritiesMapper;
    private GroupService groupService;

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

        if (log.isDebugEnabled())
        {
            log.debug(authResult.getName() + " has logged in via basic authentication.");
        }

        getLoginSuccessOperations().onSuccessfulAuthentication(request, authResult);

        InteractiveAuthenticationSuccessEvent event = new InteractiveAuthenticationSuccessEvent(authResult, getClass());
        getLoginSuccessEventListener().onApplicationEvent(event);

    }

    public void validateEmailTicketAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException, MuleException
    {
        String emailToken = ServletRequestUtils.getStringParameter(request, "acm_email_ticket");
        String fileId = ServletRequestUtils.getStringParameter(request, "ecmFileId");
        if (emailToken != null)
        {
            List<AuthenticationToken> authenticationTokens = getAuthenticationTokenDao().findAuthenticationTokenByKey(emailToken);
            if (authenticationTokens != null)
            {
                for (AuthenticationToken authenticationToken : authenticationTokens)
                {
                    if ((AuthenticationTokenConstants.ACTIVE).equals(authenticationToken.getStatus()))
                    {
                        if (emailToken.equals(authenticationToken.getKey())
                                && fileId.equals(authenticationToken.getFileId().toString()))
                        {
                            int days = Days.daysBetween(new DateTime(authenticationToken.getCreated()), new DateTime()).getDays();
                            //token expires after 3 days
                            if (days > 3)
                            {
                                authenticationToken.setStatus(AuthenticationTokenConstants.EXPIRED);
                                authenticationToken.setModifier(authenticationToken.getCreator());
                                authenticationToken.setModified(new Date());
                                getAuthenticationTokenDao().save(authenticationToken);
                                return;
                            }
                            try
                            {
                                if (logger.isTraceEnabled())
                                {
                                    log.trace("starting token auth for email links");
                                }
                                try
                                {
                                    Authentication authentication;
                                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(authenticationToken.getCreator(), authenticationToken.getCreator());
                                    String ldapGroups = getGroupService().getLdapGroupsForUser(usernamePasswordAuthenticationToken);
                                    SearchResults searchResults = new SearchResults();
                                    JSONArray docs = searchResults.getDocuments(ldapGroups);
                                    List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
                                    if (docs != null)
                                    {
                                        for (int i = 0; i < docs.length(); i++)
                                        {
                                            grantedAuthorities.add(new SimpleGrantedAuthority(searchResults.extractString(docs.getJSONObject(i), SearchConstants.PROPERTY_NAME)));
                                        }
                                    }
                                    authentication = new UsernamePasswordAuthenticationToken(authenticationToken.getCreator(), authenticationToken.getCreator(), getAcmGrantedAuthoritiesMapper().mapAuthorities(grantedAuthorities));
                                    SecurityContextHolder.getContext().setAuthentication(authentication);
                                    onSuccessfulAuthentication(request, response, authentication);
                                } catch (IllegalArgumentException e)
                                {
                                    throw new PreAuthenticatedCredentialsNotFoundException(e.getMessage(), e);
                                }
                            } catch (AuthenticationException failed)
                            {
                                SecurityContextHolder.clearContext();
                                if (logger.isTraceEnabled())
                                {
                                    logger.trace("Authentication request failed: " + failed);
                                }

                                onUnsuccessfulAuthentication(request, response, failed);

                                if (isIgnoreFailure())
                                {
                                    chain.doFilter(request, response);
                                } else
                                {
                                    getAuthenticationEntryPoint().commence(request, response, failed);
                                }

                                return;
                            }
                        }
                    }
                }
            }
        }
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

        String emailToken = ServletRequestUtils.getStringParameter(request, "acm_email_ticket");
        boolean emailTokenRequest = emailToken != null;

        X509Certificate clientCert = extractX509ClientCertificate(request);
        boolean clientCertRequest = clientCert != null;

        // No token, no basic authentication
        if (!tokenRequest && !basicAuthRequest && !emailTokenRequest && !clientCertRequest)
        {
            if (trace)
            {
                log.trace("neither token nor basic - skipping.");
            }
            chain.doFilter(request, response);
            return;
        }

        //Email token requests
        if (emailTokenRequest)
        {
            try
            {
                validateEmailTicketAuthentication(request, response, chain);
            } catch (MuleException e)
            {
                log.error("Could not validate email ticket" + e.getMessage(), e);
            }
        }

        // Token authentication
        if (tokenRequest)
        {

            if (trace)
            {
                log.trace("starting token auth");
            }
            Authentication auth;
            try
            {
                auth = getAuthenticationTokenService().getAuthenticationForToken(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
                onSuccessfulAuthentication(request, response, auth);
            } catch (IllegalArgumentException e)
            {
                SecurityContextHolder.clearContext();
                if (trace)
                {
                    logger.trace("Authentication request failed", e);
                }

                AuthenticationException authenticationException = new PreAuthenticatedCredentialsNotFoundException(e.getMessage(), e);
                onUnsuccessfulAuthentication(request, response, authenticationException);


                // by calling setStatus, and then NOT calling doFilter, processing stops and the client will get a 401
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                return;
            }
        }


        // Basic authentication
        if (basicAuthRequest)
        {
            if (trace)
            {
                log.trace("switching to basic auth");
            }
            // let Spring Security's native basic authentication do the work.
            super.doFilter(req, res, chain);
            return;
        }

        // if using client certificate and not already authenticated
        if (clientCertRequest &&
                !(SecurityContextHolder.getContext().getAuthentication() != null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()))
        {
            // extract userId (Common Name attribute) from the certifcate
            X509PrincipalExtractor principalExtractor = new SubjectDnX509PrincipalExtractor();
            String userId = (String) principalExtractor.extractPrincipal(clientCert);
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userId, userId);
            String ldapGroups = null;
            try
            {
                ldapGroups = groupService.getLdapGroupsForUser(usernamePasswordAuthenticationToken);
            } catch (MuleException e)
            {
                log.warn("Unable to read LDAP groups for user [{}]", userId);
            }
            if (ldapGroups != null)
            {
                SearchResults searchResults = new SearchResults();
                JSONArray docs = searchResults.getDocuments(ldapGroups);
                List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
                if (docs != null)
                {
                    for (int i = 0; i < docs.length(); i++)
                    {
                        grantedAuthorities.add(new SimpleGrantedAuthority(searchResults.extractString(docs.getJSONObject(i), SearchConstants.PROPERTY_NAME)));
                    }
                }
                Authentication authentication = new UsernamePasswordAuthenticationToken(userId, userId, acmGrantedAuthoritiesMapper.mapAuthorities(grantedAuthorities));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("[{}] has logged in using Client certificate authentication.");

                loginSuccessOperations.onSuccessfulAuthentication(request, authentication);

                InteractiveAuthenticationSuccessEvent event = new InteractiveAuthenticationSuccessEvent(authentication, getClass());
                loginSuccessEventListener.onApplicationEvent(event);
            }
        }

        chain.doFilter(request, response);

    }

    private boolean isBasicAuthRequest(HttpServletRequest request)
    {
        String header = request.getHeader("Authorization");
        return header != null && header.startsWith("Basic ");
    }

    /**
     * Extract client certificate from the request.
     *
     * @param request incoming HTTP request
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

    public AuthenticationTokenDao getAuthenticationTokenDao()
    {
        return authenticationTokenDao;
    }

    public void setAuthenticationTokenDao(AuthenticationTokenDao authenticationTokenDao)
    {
        this.authenticationTokenDao = authenticationTokenDao;
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
}
