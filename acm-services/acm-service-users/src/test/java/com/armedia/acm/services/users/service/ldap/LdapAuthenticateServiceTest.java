package com.armedia.acm.services.users.service.ldap;

import com.armedia.acm.services.users.dao.ldap.SpringLdapDao;
import com.armedia.acm.services.users.model.ldap.AcmLdapAuthenticateConfig;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ldap.core.LdapTemplate;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertTrue;

public class LdapAuthenticateServiceTest extends EasyMockSupport
{
    private LdapAuthenticateService unit;

    private SpringLdapDao mockLdapDao;
    private LdapTemplate mockLdapTemplate;

    @Before
    public void setUp()
    {
        mockLdapDao = createMock(SpringLdapDao.class);
        mockLdapTemplate = createMock(LdapTemplate.class);

        unit = new LdapAuthenticateService();
        unit.setLdapDao(mockLdapDao);
    }

    @Test
    public void authenticate()
    {
    	String userName = "userName";
    	String password = "password";
    	String searchBase = "searchBase";
    	String userIdAttributeName = "userIdAttributeName";
    	String filter = "(" + userIdAttributeName + "=" + userName + ")";
    	
        AcmLdapAuthenticateConfig config = new AcmLdapAuthenticateConfig();
        config.setSearchBase(searchBase);
        config.setUserIdAttributeName(userIdAttributeName);
        
        unit.setLdapAuthenticateConfig(config);

        expect(mockLdapDao.buildLdapTemplate(config)).andReturn(mockLdapTemplate);
        expect(mockLdapTemplate.authenticate(searchBase, filter, password)).andReturn(true);

        replayAll();

        Boolean isAuthenticated = unit.authenticate(userName, password);

        verifyAll();

        assertTrue(isAuthenticated.booleanValue());
    }

}
