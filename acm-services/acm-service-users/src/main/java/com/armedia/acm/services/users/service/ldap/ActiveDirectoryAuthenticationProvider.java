/*
 * #%L
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software.
 * If the software was purchased under a paid ArkCase license, the terms of
 * the paid license agreement will prevail. Otherwise, the software is
 * provided under the following open source license terms:
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package com.armedia.acm.services.users.service.ldap;

import com.armedia.acm.services.users.model.ldap.ActiveDirectoryLdapSearchConfig;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.ldap.NamingException;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.ldap.SpringSecurityLdapTemplate;
import org.springframework.security.ldap.authentication.AbstractLdapAuthenticationProvider;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;

import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;

import java.util.Collection;

/**
 * Specialized LDAP authentication provider which uses Active Directory configuration
 * conventions.
 * <p>
 * It will authenticate using the Active Directory {@code userPrincipalName}
 * in the form {@code username@domain}. If the username does not already end with the
 * domain name, the {@code userPrincipalName} will be built by appending the configured
 * domain name to the username supplied in the authentication request. If no domain name
 * is configured, it is assumed that the username will always contain the domain name.
 * <p>
 * The user authorities are obtained by delegating to a {@link LdapAuthoritiesPopulator}.
 *
 * <h3>Active Directory Sub-Error Codes</h3>
 *
 * When an authentication fails, resulting in a standard LDAP 49 error code, Active
 * Directory also supplies its own sub-error codes within the error message. These will be
 * used to provide additional log information on why an authentication has failed. Typical
 * examples are
 *
 * <ul>
 * <li>525 - user not found</li>
 * <li>52e - invalid credentials</li>
 * <li>530 - not permitted to logon at this time</li>
 * <li>532 - password expired</li>
 * <li>533 - account disabled</li>
 * <li>701 - account expired</li>
 * <li>773 - user must reset password</li>
 * <li>775 - account locked</li>
 * </ul>
 *
 */
public class ActiveDirectoryAuthenticationProvider extends
        AbstractLdapAuthenticationProvider
{
    private Logger LOGGER = LogManager.getLogger(getClass());
    private final String domain;
    private final ContextSource contextSource;
    private final LdapAuthoritiesPopulator authoritiesPopulator;
    private final ActiveDirectoryLdapSearchConfig activeDirectoryLdapSearchConfig;

    /**
     * @param domain
     *            the domain name (may be null or empty)
     */
    public ActiveDirectoryAuthenticationProvider(String domain, ContextSource contextSource, LdapAuthoritiesPopulator authoritiesPopulator,
            ActiveDirectoryLdapSearchConfig activeDirectoryLdapSearchConfig)
    {
        this.domain = StringUtils.isBlank(domain) ? domain.toLowerCase() : null;
        this.contextSource = contextSource;
        this.authoritiesPopulator = authoritiesPopulator;
        this.activeDirectoryLdapSearchConfig = activeDirectoryLdapSearchConfig;
    }

    @Override
    protected DirContextOperations doAuthentication(UsernamePasswordAuthenticationToken auth)
    {
        // Get username and password
        String username = auth.getName();
        String password = (String) auth.getCredentials();

        // Bind as provided user and return DirContext
        DirContext ctx = getContextSource().getContext(createBindPrincipal(username), password);

        try
        {
            // Search for user and return DirContextOperations
            return searchForUser(ctx, StringUtils.substringBeforeLast(username, "@"));
        }
        catch (NamingException e)
        {
            LOGGER.error("Failed to locate directory entry for authenticated user: [{}]", username, e);
            throw badCredentials(e);
        }
        finally
        {
            LdapUtils.closeContext(ctx);
        }
    }

    /**
     * Delegate retrieving authorities to {@link LdapAuthoritiesPopulator}
     */
    @Override
    protected Collection<? extends GrantedAuthority> loadUserAuthorities(DirContextOperations userData, String username, String password)
    {
        return getAuthoritiesPopulator().getGrantedAuthorities(userData, username);
    }

    private BadCredentialsException badCredentials()
    {
        return new BadCredentialsException(messages.getMessage(
                "LdapAuthenticationProvider.badCredentials", "Bad credentials"));
    }

    private BadCredentialsException badCredentials(Throwable cause)
    {
        return (BadCredentialsException) badCredentials().initCause(cause);
    }

    private DirContextOperations searchForUser(DirContext context, String username) throws NamingException
    {
        SearchControls searchControls = getActiveDirectoryLdapSearchConfig().getSearchControls();

        try
        {
            return SpringSecurityLdapTemplate.searchForSingleEntryInternal(context, searchControls,
                    getActiveDirectoryLdapSearchConfig().getSearchBase(), getActiveDirectoryLdapSearchConfig().getSearchFilter(),
                    new Object[] { username });
        }
        catch (IncorrectResultSizeDataAccessException incorrectResults)
        {
            // Search should never return multiple results if properly configured - just rethrow
            if (incorrectResults.getActualSize() != 0)
            {
                throw incorrectResults;
            }
            // If we found no results, then the username/password did not match
            // Do not send user not found message to client side, send invalid password instead.
            AcmActiveDirectoryAuthenticationException userNameNotFoundException = new AcmActiveDirectoryAuthenticationException(
                    ActiveDirectoryUtils.subCodeToLogMessage(ActiveDirectoryError.INVALID_PASSWORD.getCode()),
                    incorrectResults);
            LOGGER.error("Unable to authenticate user - {}", ExceptionUtils.getRootCauseMessage(userNameNotFoundException));
            throw badCredentials(userNameNotFoundException);
        }
        catch (javax.naming.NamingException e)
        {
            throw LdapUtils.convertLdapException(e);
        }
    }

    private String createBindPrincipal(String username)
    {
        if (domain == null || username.toLowerCase().endsWith(domain))
        {
            return username;
        }

        return username + "@" + domain;
    }

    public ContextSource getContextSource()
    {
        return contextSource;
    }

    public LdapAuthoritiesPopulator getAuthoritiesPopulator()
    {
        return authoritiesPopulator;
    }

    public ActiveDirectoryLdapSearchConfig getActiveDirectoryLdapSearchConfig()
    {
        return activeDirectoryLdapSearchConfig;
    }
}