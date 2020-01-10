package com.armedia.acm.services.search.model;

/*-
 * #%L
 * ACM Service: Search
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

import com.armedia.acm.core.AcmUserAuthorityContext;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class AcmAuthenticationMock implements Authentication, AcmUserAuthorityContext
{
    private final Collection<AcmGrantedAuthorityMock> authorities;
    private final Object credentials;
    private final Object principal;
    private final Long userIdentifier;
    private final String name;
    private Object details;
    private boolean authenticated;

    public AcmAuthenticationMock(Authentication authentication)
    {
        this.authorities = getAuthMock();
        this.credentials = authentication.getCredentials();
        this.principal = authentication.getPrincipal();
        this.name = authentication.getName();
        this.details = authentication.getDetails();
        this.authenticated = authentication.isAuthenticated();
        this.userIdentifier = 0L;
    }

    public AcmAuthenticationMock(Collection<AcmGrantedAuthorityMock> authorities,
                                 Object credentials,
                                 Object details,
                                 boolean authenticated,
                                 String principal, Long userId)
    {
        this.authorities = authorities;
        this.credentials = credentials;
        this.details = details;
        this.principal = principal;
        this.authenticated = authenticated;
        this.name = principal;
        this.userIdentifier = userId;
    }

    public AcmAuthenticationMock(Collection<AcmGrantedAuthorityMock> authorities,
                                 Object credentials,
                                 Object details,
                                 boolean authenticated,
                                 String principal)
    {
        this.authorities = authorities;
        this.credentials = credentials;
        this.details = details;
        this.principal = principal;
        this.authenticated = authenticated;
        this.name = principal;
        this.userIdentifier = 0L;
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

    public Long getUserIdentifier()
    {
        return userIdentifier;
    }

    @Override
    public Set<Long> getGroupAuthorities()
    {
        return authorities.stream()
                .filter(it -> it instanceof AcmGrantedGroupAuthorityMock)
                .map(it -> ((AcmGrantedGroupAuthorityMock) it).getGroupId())
                .collect(Collectors.toSet());
    }

    @Override
    public Long getUserIdentity()
    {
        return getUserIdentifier();
    }

    public Collection<AcmGrantedAuthorityMock> getAuthMock()
    {
        Collection<AcmGrantedAuthorityMock> mock = new HashSet<>();
        AcmGrantedGroupAuthorityMock authorityMock = new AcmGrantedGroupAuthorityMock("acm-user", 100L);
        mock.add(authorityMock);
        return mock;
    }
}
