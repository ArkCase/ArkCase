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

import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.ldap.search.LdapUserSearch;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.security.ldap.userdetails.LdapUserDetailsService;

/**
 * @author ncuculova
 *
 */
public class AcmLdapUserDetailsService extends LdapUserDetailsService
{
    private AcmLdapSyncConfig acmLdapSyncConfig;
    private static final Logger log = LogManager.getLogger(AcmLdapUserDetailsService.class);

    /**
     * @param userSearch {@link LdapUserSearch}
     */
    public AcmLdapUserDetailsService(LdapUserSearch userSearch)
    {
        super(userSearch);
    }

    /**
     * @param userSearch {@link LdapUserSearch}
     * @param authoritiesPopulator {@link LdapAuthoritiesPopulator}
     */
    public AcmLdapUserDetailsService(LdapUserSearch userSearch, LdapAuthoritiesPopulator authoritiesPopulator)
    {
        super(userSearch, authoritiesPopulator);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.security.ldap.userdetails.LdapUserDetailsService#loadUserByUsername(java.lang.String)
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        String truncatedUsername = StringUtils.substringBeforeLast(username, "@");
        log.info("Authenticate user [{}] to LDAP without user domain [{}]", username, truncatedUsername);
        AcmUserDetailsContextMapper userDetailsContextMapper = new AcmUserDetailsContextMapper(acmLdapSyncConfig);
        setUserDetailsMapper(userDetailsContextMapper);
        return super.loadUserByUsername(truncatedUsername);
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
