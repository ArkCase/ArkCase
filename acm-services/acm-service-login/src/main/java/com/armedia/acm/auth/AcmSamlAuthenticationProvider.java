package com.armedia.acm.auth;

/*-
 * #%L
 * ACM Service: User Login and Authentication
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
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

import com.armedia.acm.services.users.model.ldap.MapperUtils;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.providers.ExpiringUsernameAuthenticationToken;
import org.springframework.security.saml.SAMLAuthenticationProvider;

public class AcmSamlAuthenticationProvider extends SAMLAuthenticationProvider
{
    private String userDomain;
    private String userPrefix;

    private final static Logger log = LogManager.getLogger(AcmSamlAuthenticationProvider.class);

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException
    {
        ExpiringUsernameAuthenticationToken token = (ExpiringUsernameAuthenticationToken) super.authenticate(authentication);
        String principal = token.getName();

        String acmPrincipal = MapperUtils.buildPrincipalName(principal, userPrefix, userDomain);

        if (!acmPrincipal.equals(principal))
        {
            log.info("Authenticated principal with configured prefix [{}] and domain [{}] is [{}]", userPrefix, userDomain,
                    acmPrincipal);
            ExpiringUsernameAuthenticationToken result = new ExpiringUsernameAuthenticationToken(token.getTokenExpiration(),
                    acmPrincipal, token.getCredentials(), token.getAuthorities());
            result.setDetails(token.getDetails());
            return result;
        }
        log.info("Principal [{}] authenticated successfully", principal);
        return token;
    }

    public void setUserDomain(String userDomain)
    {
        this.userDomain = userDomain;
    }

    public void setUserPrefix(String userPrefix)
    {
        this.userPrefix = userPrefix;
    }
}
