package com.armedia.acm.services.users.service;

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

import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import com.armedia.acm.services.users.model.ldap.AcmUserContextMapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.LdapUserDetailsMapper;

import java.util.Collection;

/**
 * @author ncuculova
 *
 */
public class AcmUserDetailsContextMapper extends LdapUserDetailsMapper
{
    private AcmLdapSyncConfig acmLdapSyncConfig;
    private static final Logger log = LogManager.getLogger(AcmUserContextMapper.class);

    /**
     * @param acmLdapSyncConfig
     *            LDAP directory properties configuration
     */
    public AcmUserDetailsContextMapper(AcmLdapSyncConfig acmLdapSyncConfig)
    {
        this.acmLdapSyncConfig = acmLdapSyncConfig;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.springframework.security.ldap.userdetails.LdapUserDetailsMapper#mapUserFromContext(org.springframework.ldap.
     * core.DirContextOperations, java.lang.String, java.util.Collection)
     */
    @Override
    public UserDetails mapUserFromContext(DirContextOperations ctx, String username, Collection<? extends GrantedAuthority> authorities)
    {
        String usernameWithDomain = String.format("%s@%s", username, acmLdapSyncConfig.getUserDomain());
        log.info("Map authenticated user [{}] from LDAP context", usernameWithDomain);
        return super.mapUserFromContext(ctx, usernameWithDomain, authorities);
    }

}
