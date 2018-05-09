package com.armedia.acm.services.users.service.ldap;

/*-
 * #%L
 * ACM Service: Users
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

import com.armedia.acm.spring.SpringContextHolder;

import java.util.Map;

/**
 * Ldap Authenticate Manager cycles through all available LdapAuthenticateService beans to
 * authenticate a userName and password provided.
 */
public class LdapAuthenticateManager
{
    private SpringContextHolder springContextHolder;

    public Boolean authenticate(String userName, String password)
    {
        Map<String, LdapAuthenticateService> ldapAuthServiceMap = getSpringContextHolder().getAllBeansOfType(LdapAuthenticateService.class);

        Boolean authenticated = Boolean.FALSE;
        for (Map.Entry<String, LdapAuthenticateService> ldapAuthService : ldapAuthServiceMap.entrySet())
        {
            authenticated = ldapAuthService.getValue().authenticate(userName, password);
            if (authenticated)
            {
                break;
            }
        }
        return authenticated;
    }

    public SpringContextHolder getSpringContextHolder()
    {
        return springContextHolder;
    }

    public void setSpringContextHolder(SpringContextHolder springContextHolder)
    {
        this.springContextHolder = springContextHolder;
    }

}
