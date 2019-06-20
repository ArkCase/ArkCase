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

import com.armedia.acm.auth.okta.model.OktaAPIConstants;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.spring.SpringContextHolder;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.ProviderNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.util.Collection;
import java.util.Map;

/**
 * Cycle through the configured authentication provider. If one of them works, map the provider's groups to ACM groups.
 */
public class AcmAuthenticationManager implements AuthenticationManager
{
    private SpringContextHolder springContextHolder;
    private AcmGrantedAuthoritiesMapper authoritiesMapper;
    private DefaultAuthenticationEventPublisher authenticationEventPublisher;
    private UserDao userDao;
    private Logger log = LogManager.getLogger(getClass());

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException
    {
        Exception lastException = null;

        try
        {
            String principal = authentication.getName();

            Map<String, AuthenticationProvider> providerMap = getSpringContextHolder().getAllBeansOfType(AuthenticationProvider.class);
            Authentication providerAuthentication = null;
            for (Map.Entry<String, AuthenticationProvider> providerEntry : providerMap.entrySet())
            {
                try
                {
                    if (providerEntry.getValue() instanceof AcmLdapAuthenticationProvider)
                    {
                        if (principal.isEmpty())
                        {
                            throw new BadCredentialsException("Empty Username");
                        }
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
                            : new AuthenticationServiceException(ExceptionUtils.getRootCauseMessage(lastException),
                                    lastException);
                }
                getAuthenticationEventPublisher().publishAuthenticationFailure(ae, authentication);
                log.debug("Detailed exception: ", lastException);
                lastException = ae;
                throw ae;
            }
        }
        catch (RuntimeException e)
        {
            if (e instanceof NoProviderFoundException)
                throw e;
            else if (lastException != null)
                throw new AuthenticationServiceException(ExceptionUtils.getRootCauseMessage(lastException));
            else
            {
                throw new InternalAuthenticationServiceException("Unknown server error", e);
            }
        }
        // didn't get an exception, or an authentication either, so we can throw a provider not found exception,
        // since
        // either there are no providers, or no providers can handle the incoming authentication
        throw new NoProviderFoundException("Authentication problem. Please contact your administrator.");

    }

    public AcmAuthentication getAcmAuthentication(Authentication providerAuthentication)
    {
        log.info("Checking the authenticated user: [{}] in system", providerAuthentication.getName());
        AcmUser user = getUserDao().findByUserId(providerAuthentication.getName());

        if (user == null)
        {
            throw new AuthenticationServiceException("Provided credentials are not valid");
        }

        Collection<AcmGrantedAuthority> acmAuths = getAuthoritiesMapper().mapAuthorities(providerAuthentication.getAuthorities());

        // Collection with LDAP and ADHOC authority groups that the user belongs to
        Collection<AcmGrantedAuthority> acmAuthsGroups = getAuthoritiesMapper().getAuthorityGroups(user);

        // Collection with application roles for LDAP and ADHOC groups/subgroups that the user belongs to
        Collection<AcmGrantedAuthority> acmAuthsRoles = getAuthoritiesMapper().mapAuthorities(acmAuthsGroups);

        // Add to all
        acmAuths.addAll(acmAuthsGroups);
        acmAuths.addAll(acmAuthsRoles);

        log.debug("Granting [{}] role 'ROLE_PRE_AUTHENTICATED'", providerAuthentication.getName());
        acmAuths.add(new AcmGrantedAuthority(OktaAPIConstants.ROLE_PRE_AUTHENTICATED));

        return new AcmAuthentication(acmAuths, providerAuthentication.getCredentials(), providerAuthentication.getDetails(),
                providerAuthentication.isAuthenticated(), user.getUserId(), user.getIdentifier());
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

    public DefaultAuthenticationEventPublisher getAuthenticationEventPublisher()
    {
        return authenticationEventPublisher;
    }

    public void setAuthenticationEventPublisher(DefaultAuthenticationEventPublisher authenticationEventPublisher)
    {
        this.authenticationEventPublisher = authenticationEventPublisher;
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

}
