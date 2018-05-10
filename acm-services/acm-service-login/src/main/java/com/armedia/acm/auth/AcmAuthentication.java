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

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class AcmAuthentication implements Authentication
{
    private final Collection<AcmGrantedAuthority> authorities;
    private final Object credentials;
    private final Object principal;
    private final String name;
    private Object details;
    private boolean authenticated;

    public AcmAuthentication(Collection<AcmGrantedAuthority> authorities,
            Object credentials,
            Object details,
            boolean authenticated,
            String userId)
    {
        this.authorities = authorities;
        this.credentials = credentials;
        this.details = details;
        this.principal = userId;
        this.authenticated = authenticated;
        this.name = userId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities()
    {
        return authorities;
    }

    @Override
    public Object getCredentials()
    {
        return credentials;
    }

    @Override
    public Object getDetails()
    {
        return details;
    }

    public void setDetails(Object details)
    {
        this.details = details;
    }

    @Override
    public Object getPrincipal()
    {
        return principal;
    }

    @Override
    public boolean isAuthenticated()
    {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean authenticated) throws IllegalArgumentException
    {
        this.authenticated = authenticated;
    }

    @Override
    public String getName()
    {
        return name;
    }
}
