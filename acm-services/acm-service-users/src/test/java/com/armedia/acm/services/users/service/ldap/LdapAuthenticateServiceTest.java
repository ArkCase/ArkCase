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
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.dao.ldap.SpringLdapDao;
import com.armedia.acm.services.users.dao.ldap.SpringLdapUserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.PasswordResetToken;
import com.armedia.acm.services.users.model.ldap.AcmLdapActionFailedException;
import com.armedia.acm.services.users.model.ldap.AcmLdapAuthenticateConfig;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import com.armedia.acm.services.users.model.ldap.LdapUser;

import org.easymock.EasyMockRunner;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.ldap.core.LdapTemplate;

import javax.naming.directory.InvalidAttributeValueException;
import java.time.LocalDate;
import java.time.LocalDateTime;

@RunWith(EasyMockRunner.class)
public class LdapAuthenticateServiceTest extends EasyMockSupport
{
    @TestSubject
    private LdapAuthenticateService unit = new LdapAuthenticateService();

    @Mock
    private SpringLdapDao mockLdapDao;

    @Mock
    private LdapTemplate mockLdapTemplate;

    @Mock
    private SpringLdapUserDao mockLdapUserDao;

    @Mock
    private UserDao mockUserDao;

    @Mock
    private AcmLdapAuthenticateConfig mockLdapAuthenticateConfig;

    @Mock
    private AcmLdapSyncConfig mockLdapSyncConfig;

    private AcmUser mockUser;

    @Before
    public void setUp()
    {
        mockUser = new AcmUser();
        mockUser.setUserDirectoryName("armedia");
        mockUser.setUserId("ann-acm");
        mockUser.setDistinguishedName("cn=ann-acm,dc=arkcase,dc=com");
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

        assertTrue(isAuthenticated);
    }

    @Test
    public void changeUserPasswordSuccessfully() throws AcmLdapActionFailedException, AcmUserActionFailedException, InvalidAttributeValueException {
        String currentPassword = "password";
        String newPassword = "newPassword";

        LdapTemplate mockTemplate = createMock(LdapTemplate.class);

        expect(mockLdapDao.buildLdapTemplate(mockLdapAuthenticateConfig, mockUser.getDistinguishedName(), currentPassword))
                .andReturn(mockTemplate);

        expect(mockUserDao.findByUserId("ann-acm")).andReturn(mockUser);

        mockLdapUserDao.changeUserPassword(mockUser.getDistinguishedName(), currentPassword, newPassword, mockTemplate,
                mockLdapAuthenticateConfig);
        expectLastCall().once();

        expect(mockLdapDao.buildLdapTemplate(mockLdapAuthenticateConfig, mockUser.getDistinguishedName(), newPassword))
                .andReturn(mockTemplate);

        LdapUser userEntry = new LdapUser();
        userEntry.setDistinguishedName("cn=ann-acm,dc=arkcase,dc=com");
        userEntry.setPasswordExpirationDate(LocalDate.of(2017, 1, 1));
        expect(mockLdapUserDao.lookupUser(mockUser.getDistinguishedName(), mockTemplate, mockLdapSyncConfig)).andReturn(userEntry);

        expect(mockUserDao.save(mockUser)).andReturn(mockUser);

        replayAll();

        unit.changeUserPassword("ann-acm", currentPassword, newPassword);

        verifyAll();
        assertEquals(userEntry.getPasswordExpirationDate(), mockUser.getPasswordExpirationDate());
    }

    @Test(expected = AcmUserActionFailedException.class)
    public void changeUserPasswordFailed() throws AcmLdapActionFailedException, AcmUserActionFailedException, InvalidAttributeValueException {
        String currentPassword = "password";
        String newPassword = "newPassword";

        LdapTemplate mockTemplate = createMock(LdapTemplate.class);

        expect(mockLdapDao.buildLdapTemplate(mockLdapAuthenticateConfig, mockUser.getDistinguishedName(), currentPassword))
                .andReturn(mockTemplate);

        expect(mockUserDao.findByUserId("ann-acm")).andReturn(mockUser);

        mockLdapUserDao.changeUserPassword(mockUser.getDistinguishedName(), currentPassword, newPassword, mockTemplate,
                mockLdapAuthenticateConfig);
        expectLastCall().andThrow(new AcmLdapActionFailedException("error"));

        replayAll();

        unit.changeUserPassword("ann-acm", currentPassword, newPassword);

        verifyAll();
    }

    @Test
    public void resetUserPasswordSuccessfully() throws AcmLdapActionFailedException, AcmUserActionFailedException
    {
        String password = "password";
        PasswordResetToken passwordResetToken = new PasswordResetToken();
        String token = passwordResetToken.getToken();
        mockUser.setPasswordResetToken(passwordResetToken);

        LdapTemplate mockTemplate = createMock(LdapTemplate.class);
        expect(mockLdapDao.buildLdapTemplate(mockLdapAuthenticateConfig)).andReturn(mockTemplate);

        expect(mockUserDao.findByPasswordResetToken(token)).andReturn(mockUser);

        mockLdapUserDao.changeUserPasswordWithAdministrator(mockUser.getDistinguishedName(), password, mockTemplate,
                mockLdapAuthenticateConfig);
        expectLastCall().once();

        LdapUser userEntry = new LdapUser();
        userEntry.setDistinguishedName("cn=ann-acm,dc=arkcase,dc=com");
        userEntry.setPasswordExpirationDate(LocalDate.of(2017, 1, 1));
        expect(mockLdapUserDao.lookupUser(mockUser.getDistinguishedName(), mockTemplate, mockLdapSyncConfig)).andReturn(userEntry);

        expect(mockUserDao.save(mockUser)).andReturn(mockUser).times(2);

        replayAll();

        unit.resetUserPassword(token, password);

        verifyAll();
        assertEquals(userEntry.getPasswordExpirationDate(), mockUser.getPasswordExpirationDate());
        assertNull(mockUser.getPasswordResetToken());
    }

    @Test(expected = AcmUserActionFailedException.class)
    public void resetUserPasswordNoSuchUser() throws AcmUserActionFailedException
    {
        String password = "password";
        PasswordResetToken passwordResetToken = new PasswordResetToken();
        String token = passwordResetToken.getToken();
        mockUser.setPasswordResetToken(passwordResetToken);

        expect(mockUserDao.findByPasswordResetToken(token)).andReturn(null);

        replayAll();

        unit.resetUserPassword(token, password);

        verifyAll();
    }

    @Test(expected = AcmUserActionFailedException.class)
    public void resetUserPasswordLdapOperationFailed() throws AcmLdapActionFailedException, AcmUserActionFailedException
    {
        String password = "password";
        PasswordResetToken passwordResetToken = new PasswordResetToken();
        String token = passwordResetToken.getToken();
        mockUser.setPasswordResetToken(passwordResetToken);

        expect(mockUserDao.findByPasswordResetToken(token)).andReturn(mockUser);

        expect(mockUserDao.save(mockUser)).andReturn(mockUser);

        LdapTemplate mockTemplate = createMock(LdapTemplate.class);
        expect(mockLdapDao.buildLdapTemplate(mockLdapAuthenticateConfig)).andReturn(mockTemplate);
        mockLdapUserDao.changeUserPasswordWithAdministrator(mockUser.getDistinguishedName(), password, mockTemplate,
                mockLdapAuthenticateConfig);
        expectLastCall().andThrow(new AcmLdapActionFailedException("LDAP Action Failed Exception"));

        replayAll();

        unit.resetUserPassword(token, password);

        verifyAll();
        assertFalse(mockUser.getPasswordResetToken().getExpiryDate().isAfter(LocalDateTime.now()));
    }
}
