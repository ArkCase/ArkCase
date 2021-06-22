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

import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.service.ldap.AcmActiveDirectoryAuthenticationException;
import com.armedia.acm.services.users.service.ldap.AcmActiveDirectoryAuthenticationProvider;
import com.armedia.acm.services.users.service.ldap.AcmLdapAuthenticationProvider;
import com.armedia.acm.spring.SpringContextHolder;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.ProviderNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.util.Map;

/**
 * Cycle through the configured authentication provider. If one of them works, map the provider's groups to ACM groups.
 */
public class AcmAuthenticationManager implements AuthenticationManager
{
    private SpringContextHolder springContextHolder;
    private AcmAuthenticationMapper authenticationMapper;
    private DefaultAuthenticationEventPublisher authenticationEventPublisher;
    private UserDao userDao;
    private final Logger log = LogManager.getLogger(getClass());

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException
    {
        Exception lastException = null;

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
                    String userDomain = provider.getAcmLdapSyncConfig().getUserDomain();
                    if (principal.endsWith(userDomain))
                    {
                        providerAuthentication = provider.authenticate(authentication);
                    }
                }
                else if (providerEntry.getValue() instanceof AcmActiveDirectoryAuthenticationProvider)
                {
                    if (principal.isEmpty())
                    {
                        throw new BadCredentialsException("Empty Username");
                    }
                    AcmActiveDirectoryAuthenticationProvider provider = (AcmActiveDirectoryAuthenticationProvider) providerEntry
                            .getValue();
                    String userDomain = provider.getAcmLdapSyncConfig().getUserDomain();
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
            log.debug("Processed authentication request for user: {}", providerAuthentication.getName());
            return authenticationMapper.getAcmAuthentication(providerAuthentication);
        }
        if (lastException != null)
        {
            AuthenticationException ae;
            if (lastException instanceof ProviderNotFoundException)
            {
                log.error("No authentication provider found for [{}]", authentication);
                ae = new NoProviderFoundException("Authentication problem. Please contact your administrator.");
            }
            else if (lastException instanceof AuthenticationException && lastException.getCause() != null
                    && lastException.getCause() instanceof AcmActiveDirectoryAuthenticationException)
            {
                log.error("Authentication [{}] failed to authenticate: [{}]", authentication, lastException.getMessage());
                ae = (AuthenticationException) lastException;
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

            else if (ExceptionUtils.getRootCauseMessage(lastException).contains("UnknownHostException"))
            {
                ae = new AuthenticationServiceException(
                        "There was an unknown error in connecting with the authentication services!", lastException);
            }
            else if (lastException instanceof RuntimeException)
            {
                throw new InternalAuthenticationServiceException("Unknown server error", lastException);
            }
            else
            {
                ae = new AuthenticationServiceException(ExceptionUtils.getRootCauseMessage(lastException),
                        lastException);
            }
            getAuthenticationEventPublisher().publishAuthenticationFailure(ae, authentication);
            log.debug("Detailed exception: ", lastException);
            throw ae;
        }
        // didn't get an exception, or an authentication either, so we can throw a provider not found exception,
        // since
        // either there are no providers, or no providers can handle the incoming authentication
        throw new NoProviderFoundException("Authentication problem. Please contact your administrator.");
    }

    public SpringContextHolder getSpringContextHolder()
    {
        return springContextHolder;
    }

    public void setSpringContextHolder(SpringContextHolder springContextHolder)
    {
        this.springContextHolder = springContextHolder;
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

    public AcmAuthenticationMapper getAuthenticationMapper()
    {
        return authenticationMapper;
    }

    public void setAuthenticationMapper(AcmAuthenticationMapper authenticationMapper)
    {
        this.authenticationMapper = authenticationMapper;
    }
}
