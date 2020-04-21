package com.armedia.acm.services.users.service.ldap;

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

import com.armedia.acm.services.ldap.syncer.AcmLdapSyncEvent;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.AcmUserState;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.annotation.Profile;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.authentication.LdapAuthenticator;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.stereotype.Component;

import javax.naming.Name;

/**
 * Created by riste.tutureski on 4/11/2016.
 */
@Component
@Profile("ldap,kerberos,externalAuth,externalSaml")
public class AcmLdapAuthenticationProvider extends LdapAuthenticationProvider implements ApplicationEventPublisherAware
{
    private UserDao userDao;
    private LdapSyncService ldapSyncService;
    private ApplicationEventPublisher eventPublisher;
    private AcmLdapSyncConfig acmLdapSyncConfig;

    public AcmLdapAuthenticationProvider(LdapAuthenticator authenticator, LdapAuthoritiesPopulator authoritiesPopulator)
    {
        super(authenticator, authoritiesPopulator);
    }

    public AcmLdapAuthenticationProvider(LdapAuthenticator authenticator)
    {
        super(authenticator);
    }

    @Override
    protected DirContextOperations doAuthentication(UsernamePasswordAuthenticationToken authentication)
    {
        String principal = StringUtils.substringBeforeLast(authentication.getName(), "@");

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(principal, authentication.getCredentials());

        DirContextOperations dirContextOperations = super.doAuthentication(token);

        Name dn = dirContextOperations.getDn();
        AcmUser user = getUserDao().findByUserId(authentication.getName());

        if (user == null || AcmUserState.VALID != user.getUserState())
        {
            eventPublisher.publishEvent(new AcmLdapSyncEvent(principal));
            getLdapSyncService().syncUserByDn(dn.toString(), acmLdapSyncConfig);
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

    /*
     * (non-Javadoc)
     * @see org.springframework.context.ApplicationEventPublisherAware#setApplicationEventPublisher(org.springframework.
     * context.ApplicationEventPublisher)
     */
    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        eventPublisher = applicationEventPublisher;
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
