package com.armedia.acm.auth;

import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.service.group.GroupService;
import com.armedia.acm.spring.SpringContextHolder;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.authentication.ProviderNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Cycle through the configured authentication provider. If one of them works, map the provider's groups to ACM groups.
 */
public class AcmAuthenticationManager implements AuthenticationManager
{
    private SpringContextHolder springContextHolder;
    private AcmGrantedAuthoritiesMapper authoritiesMapper;
    private DefaultAuthenticationEventPublisher authenticationEventPublisher;
    private UserDao userDao;
    private GroupService groupService;
    private Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException
    {
        String principal = authentication.getName();

        Map<String, AuthenticationProvider> providerMap = getSpringContextHolder().getAllBeansOfType(AuthenticationProvider.class);
        Authentication providerAuthentication = null;
        Exception lastException = null;
        for (Map.Entry<String, AuthenticationProvider> providerEntry : providerMap.entrySet())
        {
            try
            {
                if (providerEntry.getValue() instanceof AcmLdapAuthenticationProvider)
                {
                    AcmLdapAuthenticationProvider provider = (AcmLdapAuthenticationProvider) providerEntry.getValue();
                    String userDomain = provider.getLdapSyncService().getLdapSyncConfig().getUserDomain();
                    if (principal.endsWith(userDomain))
                    {
                        providerAuthentication = provider.authenticate(authentication);
                    }
                }
                else
                {
                    providerAuthentication = providerEntry.getValue().authenticate(authentication);
                }

                if (providerAuthentication != null)
                {
                    break;
                }
            }
            catch (Exception ae)
            {
                lastException = ae;
            }
        }

        if (providerAuthentication != null)
        {
            // Spring Security publishes an authentication success event all by itself, so we do not have to raise
            // one here.
            return getAcmAuthentication(providerAuthentication);
        }
        if (lastException != null)
        {
            AuthenticationException ae;
            if (lastException instanceof ProviderNotFoundException)
            {
                ae = new NoProviderFoundException("Authentication problem. Please contact your administrator.");
            }
            else if (lastException instanceof BadCredentialsException)
            {
                if (getUserDao().isUserPasswordExpired(authentication.getName()))
                {
                    ae = new AuthenticationServiceException(
                            "Your password has expired! An email with reset password link was sent to you.", lastException);
                }
                else
                {
                    ae = new AuthenticationServiceException(ExceptionUtils.getRootCauseMessage(lastException), lastException);
                }

            }
            else
            {
                ae = ExceptionUtils.getRootCauseMessage(lastException).contains("UnknownHostException")
                        ? new AuthenticationServiceException(
                                "There was an unknown error in connecting with the authentication services!", lastException)
                        : new AuthenticationServiceException(ExceptionUtils.getRootCauseMessage(lastException), lastException);
            }
            getAuthenticationEventPublisher().publishAuthenticationFailure(ae, authentication);
            log.debug("Detailed exception: ", lastException);
            throw ae;
        }

        // didn't get an exception, or an authentication either, so we can throw a provider not found exception, since
        // either there are no providers, or no providers can handle the incoming authentication

        throw new NoProviderFoundException("Authentication problem. Please contact your administrator.");
    }

    protected AcmAuthentication getAcmAuthentication(Authentication providerAuthentication)
    {
        AcmUser user = getUserDao().findByUserId(providerAuthentication.getName());

        Collection<AcmGrantedAuthority> acmAuths = getAuthoritiesMapper().mapAuthorities(providerAuthentication.getAuthorities());

        // Collection with LDAP and ADHOC authority groups that the user belongs to
        Collection<AcmGrantedAuthority> acmAuthsGroups = getAuthorityGroups(user);

        // Collection with application roles for LDAP and ADHOC groups/subgroups that the user belongs to
        Collection<AcmGrantedAuthority> acmAuthsRoles = getAuthoritiesMapper().mapAuthorities(acmAuthsGroups);

        // Add to all
        acmAuths.addAll(acmAuthsGroups);
        acmAuths.addAll(acmAuthsRoles);

        return new AcmAuthentication(acmAuths, providerAuthentication.getCredentials(), providerAuthentication.getDetails(),
                providerAuthentication.isAuthenticated(), user.getUserId());
    }

    protected Collection<AcmGrantedAuthority> getAuthorityGroups(AcmUser user)
    {
        // All LDAP and ADHOC groups that the user belongs to (all these we are keeping in the database)
        List<AcmGroup> groups = getGroupService().findByUserMember(user);

        Stream<AcmGrantedAuthority> authorityGroups = groups.stream()
                .map(AcmGroup::getName)
                .map(AcmGrantedAuthority::new);

        Stream<AcmGrantedAuthority> authorityAscendantsGroups = groups.stream()
                .flatMap(AcmGroup::getAscendantsStream)
                .map(AcmGrantedAuthority::new);

        return Stream.concat(authorityGroups, authorityAscendantsGroups)
                .collect(Collectors.toSet());
    }

    public SpringContextHolder getSpringContextHolder()
    {
        return springContextHolder;
    }

    public void setSpringContextHolder(SpringContextHolder springContextHolder)
    {
        this.springContextHolder = springContextHolder;
    }

    public AcmGrantedAuthoritiesMapper getAuthoritiesMapper()
    {
        return authoritiesMapper;
    }

    public void setAuthoritiesMapper(AcmGrantedAuthoritiesMapper authoritiesMapper)
    {
        this.authoritiesMapper = authoritiesMapper;
    }

    public void setAuthenticationEventPublisher(DefaultAuthenticationEventPublisher authenticationEventPublisher)
    {
        this.authenticationEventPublisher = authenticationEventPublisher;
    }

    public DefaultAuthenticationEventPublisher getAuthenticationEventPublisher()
    {
        return authenticationEventPublisher;
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
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
