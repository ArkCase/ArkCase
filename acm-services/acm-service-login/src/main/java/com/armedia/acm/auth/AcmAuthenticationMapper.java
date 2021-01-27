package com.armedia.acm.auth;

/*-
 * #%L
 * ACM Service: User Login and Authentication
 * %%
 * Copyright (C) 2014 - 2021 ArkCase LLC
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;

import java.util.Collection;

public class AcmAuthenticationMapper
{
    private AcmGrantedAuthoritiesMapper authoritiesMapper;
    private UserDao userDao;
    private static final Logger log = LogManager.getLogger(AcmAuthenticationManager.class);

    public AcmAuthentication getAcmAuthentication(Authentication providerAuthentication)
    {
        log.info("Checking the authenticated user: [{}] in system", providerAuthentication.getName());
        AcmUser user = userDao.findByUserId(providerAuthentication.getName());

        if (user == null)
        {
            throw new AuthenticationServiceException("Provided credentials are not valid");
        }

        Collection<AcmGrantedAuthority> acmAuths = authoritiesMapper.mapAuthorities(providerAuthentication.getAuthorities());

        // Collection with LDAP and ADHOC authority groups that the user belongs to
        Collection<AcmGrantedAuthority> acmAuthsGroups = authoritiesMapper.getAuthorityGroups(user);

        // Collection with application roles for LDAP and ADHOC groups/subgroups that the user belongs to
        Collection<AcmGrantedAuthority> acmAuthsRoles = authoritiesMapper.mapAuthorities(acmAuthsGroups);

        // Add to all
        acmAuths.addAll(acmAuthsGroups);
        acmAuths.addAll(acmAuthsRoles);

        log.debug("Granting [{}] role 'ROLE_PRE_AUTHENTICATED'", providerAuthentication.getName());
        acmAuths.add(new AcmGrantedAuthority(OktaAPIConstants.ROLE_PRE_AUTHENTICATED));

        return new AcmAuthentication(acmAuths, providerAuthentication.getCredentials(), providerAuthentication.getDetails(),
                providerAuthentication.isAuthenticated(), user.getUserId(), user.getIdentifier());
    }

    public AcmGrantedAuthoritiesMapper getAuthoritiesMapper()
    {
        return authoritiesMapper;
    }

    public void setAuthoritiesMapper(AcmGrantedAuthoritiesMapper authoritiesMapper)
    {
        this.authoritiesMapper = authoritiesMapper;
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
