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

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.armedia.acm.spring.SpringContextHolder;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;
import java.util.TreeMap;

public class LdapAuthenticateManagerTest extends EasyMockSupport
{
    private LdapAuthenticateManager unit;

    private SpringContextHolder mockContextHolder;
    private LdapAuthenticateService mockFirstService;
    private LdapAuthenticateService mockSecondService;

    @Before
    public void setUp() throws Exception
    {
        mockContextHolder = createMock(SpringContextHolder.class);
        mockFirstService = createMock(LdapAuthenticateService.class);
        mockSecondService = createMock(LdapAuthenticateService.class);

        unit = new LdapAuthenticateManager();

        unit.setSpringContextHolder(mockContextHolder);
    }

    @Test
    public void authenticate_firstOneWorks()
    {
        String userName = "userName";
        String password = "password";
        Map<String, LdapAuthenticateService> ldapAuthServiceMap = getLdapAuthServiceMap();

        expect(mockContextHolder.getAllBeansOfType(LdapAuthenticateService.class)).andReturn(ldapAuthServiceMap);
        expect(mockFirstService.authenticate(userName, password)).andReturn(Boolean.TRUE);

        replayAll();

        Boolean authenticated = unit.authenticate(userName, password);

        verifyAll();

        assertTrue(authenticated);
    }

    @Test
    public void authenticate_secondWorks()
    {
        String userName = "userName";
        String password = "password";
        Map<String, LdapAuthenticateService> ldapAuthServiceMap = getLdapAuthServiceMap();

        expect(mockContextHolder.getAllBeansOfType(LdapAuthenticateService.class)).andReturn(ldapAuthServiceMap);
        expect(mockFirstService.authenticate(userName, password)).andReturn(Boolean.FALSE);
        expect(mockSecondService.authenticate(userName, password)).andReturn(Boolean.TRUE);

        replayAll();

        Boolean authenticated = unit.authenticate(userName, password);

        verifyAll();

        assertTrue(authenticated);
    }

    @Test
    public void authenticate_noneWorks()
    {
        String userName = "userName";
        String password = "password";
        Map<String, LdapAuthenticateService> ldapAuthServiceMap = getLdapAuthServiceMap();

        expect(mockContextHolder.getAllBeansOfType(LdapAuthenticateService.class)).andReturn(ldapAuthServiceMap);
        expect(mockFirstService.authenticate(userName, password)).andReturn(Boolean.FALSE);
        expect(mockSecondService.authenticate(userName, password)).andReturn(Boolean.FALSE);

        replayAll();

        Boolean authenticated = unit.authenticate(userName, password);

        verifyAll();

        assertFalse(authenticated);
    }

    private Map<String, LdapAuthenticateService> getLdapAuthServiceMap()
    {
        Map<String, LdapAuthenticateService> ldapAuthServiceMap = new TreeMap<>();
        ldapAuthServiceMap.put("A", mockFirstService);
        ldapAuthServiceMap.put("B", mockSecondService);
        return ldapAuthServiceMap;
    }
}
