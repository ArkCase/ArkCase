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

import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.AcmUserState;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import com.armedia.acm.services.users.model.ldap.ActiveDirectoryLdapSearchConfig;

import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;

public class AcmActiveDirectoryAuthenticationProvider extends
        ActiveDirectoryAuthenticationProvider
{

    private UserDao userDao;
    private LdapSyncService ldapSyncService;
    private AcmLdapSyncConfig acmLdapSyncConfig;

    /**
     * @param domain
     *            the domain name (may be null or empty)
     * @param contextSource
     * @param authoritiesPopulator
     * @param activeDirectoryLdapSearchConfig
     */
    public AcmActiveDirectoryAuthenticationProvider(String domain, ContextSource contextSource,
            LdapAuthoritiesPopulator authoritiesPopulator, ActiveDirectoryLdapSearchConfig activeDirectoryLdapSearchConfig)
    {
        super(domain, contextSource, authoritiesPopulator, activeDirectoryLdapSearchConfig);
    }

    @Override
    protected DirContextOperations doAuthentication(UsernamePasswordAuthenticationToken authentication)
    {
        DirContextOperations dirContextOperations = super.doAuthentication(authentication);

        AcmUser user = getUserDao().findByUserId(authentication.getName());

        if (user == null || AcmUserState.VALID != user.getUserState())
        {
            getLdapSyncService().ldapUserSync(authentication.getName(), acmLdapSyncConfig);
        }

        return dirContextOperations;
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public LdapSyncService getLdapSyncService()
    {
        return ldapSyncService;
    }

    public void setLdapSyncService(LdapSyncService ldapSyncService)
    {
        this.ldapSyncService = ldapSyncService;
    }

    public AcmLdapSyncConfig getAcmLdapSyncConfig()
    {
        return acmLdapSyncConfig;
    }

    public void setAcmLdapSyncConfig(AcmLdapSyncConfig acmLdapSyncConfig)
    {
        this.acmLdapSyncConfig = acmLdapSyncConfig;
    }
}