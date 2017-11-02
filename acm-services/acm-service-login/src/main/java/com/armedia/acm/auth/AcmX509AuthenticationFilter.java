package com.armedia.acm.auth;

import com.armedia.acm.services.search.model.SearchConstants;
import com.armedia.acm.services.search.service.SearchResults;
import com.armedia.acm.services.users.service.group.GroupService;
import org.json.JSONArray;
import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.x509.SubjectDnX509PrincipalExtractor;
import org.springframework.security.web.authentication.preauth.x509.X509AuthenticationFilter;
import org.springframework.security.web.authentication.preauth.x509.X509PrincipalExtractor;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

/**
 * Spring Security filter for X.509 Client Authentication.
 * Created by Petar Ilin <petar.ilin@armedia.com> on 23.10.2017.
 */
public class AcmX509AuthenticationFilter extends X509AuthenticationFilter
{
    /**
     * Logger instance.
     */
    private Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Successful login actions.
     */
    private AcmLoginSuccessOperations loginSuccessOperations;

    /**
     * Successful login listener.
     */
    private AcmLoginSuccessEventListener loginSuccessEventListener;

    /**
     * Authorities mapper.
     */
    private AcmGrantedAuthoritiesMapper acmGrantedAuthoritiesMapper;

    /**
     * Group service.
     */
    private GroupService groupService;

    /**
     * Constructor.
     *
     * @param authenticationManager Spring Security Authentication Manager
     */
    public AcmX509AuthenticationFilter(AuthenticationManager authenticationManager)
    {
        setAuthenticationManager(authenticationManager);
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException
    {
        final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) res;

        // check if already authenticated and skip further processing
        if (SecurityContextHolder.getContext().getAuthentication() != null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated())
        {
            chain.doFilter(request, response);
            return;
        }

        try
        {
            // extract client certificate from the request
            X509Certificate[] certs = (X509Certificate[]) request.getAttribute("javax.servlet.request.X509Certificate");

            if (certs != null && certs.length > 0)
            {
                X509Certificate cert = certs[0];
                log.debug("X.509 client authentication certificate [{}]", cert);
                X509PrincipalExtractor principalExtractor = new SubjectDnX509PrincipalExtractor();
                // extract userId (Common Name attribute) from the certifcate
                String userId = (String) principalExtractor.extractPrincipal(cert);
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userId, userId);
                String ldapGroups = groupService.getLdapGroupsForUser(usernamePasswordAuthenticationToken);
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
        } catch (MuleException e)
        {
            log.error("X.509 authentication failed", e);
        }
        chain.doFilter(request, response);
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
