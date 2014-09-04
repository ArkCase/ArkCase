package com.armedia.acm.services.users.service.ldap;

import com.armedia.acm.spring.SpringContextHolder;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import java.util.Map;
import java.util.TreeMap;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

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
        Map<String, LdapAuthenticateService> ldapAuthServiceMap = new TreeMap<String, LdapAuthenticateService>();
        ldapAuthServiceMap.put("A", mockFirstService);
        ldapAuthServiceMap.put("B", mockSecondService);
        return ldapAuthServiceMap;
    }
}

