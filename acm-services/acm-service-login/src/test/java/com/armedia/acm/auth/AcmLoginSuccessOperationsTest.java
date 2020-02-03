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

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

import com.armedia.acm.core.AcmApplication;
import com.armedia.acm.core.model.ApplicationConfig;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.ApplicationRolesToPrivilegesConfig;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AcmLoginSuccessOperationsTest extends EasyMockSupport
{
    private final String roleAdd = "role_add";
    private final String roleAdmin = "role_admin";
    private final String wildCardRole = "ADMIN@*";
    private final String privilegeAdd = "add";

    private AcmLoginSuccessOperations unit;

    private HttpSession mockSession;
    private Authentication mockAuthentication;
    private HttpServletRequest mockRequest;
    private ApplicationRolesToPrivilegesConfig mockRolesToPrivilegesConfig;
    private UserDao mockUserDao;

    @Before
    public void setUp()
    {
        unit = new AcmLoginSuccessOperations();
        mockRolesToPrivilegesConfig = new ApplicationRolesToPrivilegesConfig();

        mockSession = createMock(HttpSession.class);
        mockRequest = createMock(HttpServletRequest.class);
        mockAuthentication = createMock(Authentication.class);
        mockUserDao = createMock(UserDao.class);

        unit.setRolesToPrivilegesConfig(mockRolesToPrivilegesConfig);
        unit.setUserDao(mockUserDao);
        unit.setObjectConverter(ObjectConverter.createObjectConverterForTests());
    }

    @Test
    public void addAcmUserToSession() throws Exception
    {
        String userId = "userId";
        AcmUser theUser = new AcmUser();

        expect(mockAuthentication.getName()).andReturn(userId);
        expect(mockRequest.getSession(true)).andReturn(mockSession);
        expect(mockUserDao.findByUserId(userId)).andReturn(theUser);
        mockSession.setAttribute("acm_user", theUser);

        replayAll();

        unit.addAcmUserToSession(mockRequest, mockAuthentication);

        verifyAll();
    }

    @Test
    public void addAcmApplicationObjectToSession()
    {
        AcmApplication app = new AcmApplication();
        ApplicationConfig appConfig = new ApplicationConfig();
        appConfig.setIssueCollectorFlag(true);

        unit.setAcmApplication(app);
        unit.setApplicationConfig(appConfig);

        expect(mockRequest.getSession(true)).andReturn(mockSession);
        mockSession.setAttribute("issue_collector_flag", true);
        mockSession.setAttribute("acm_application", app);
        mockSession.setAttribute("acm_object_types", "[]");

        replayAll();

        unit.addAcmApplicationToSession(mockRequest);

        verifyAll();

    }

    @Test
    public void addUserIdToSession()
    {
        String userId = "userId";

        expect(mockRequest.getSession(true)).andReturn(mockSession);
        mockSession.setAttribute("acm_username", userId);

        replayAll();

        unit.addUserIdToSession(mockRequest, userId);

        verifyAll();
    }

    @Test
    public void addUserPrivilegesToSession()
    {
        String roleAdd = "ROLE_ADD";
        String privilege = "privilege";
        Map<String, Boolean> privilegeMap = new HashMap<>();
        privilegeMap.put(privilege, Boolean.TRUE);

        List<Object> privilegeList = Arrays.asList(privilege);

        AcmGrantedAuthority authority = new AcmGrantedAuthority(roleAdd);
        expect((List<AcmGrantedAuthority>) mockAuthentication.getAuthorities()).andReturn(Arrays.asList(authority)).atLeastOnce();

        Map<String, List<Object>> rolesToPrivileges = new HashMap<>();
        rolesToPrivileges.put(roleAdd, privilegeList);
        mockRolesToPrivilegesConfig.setRolesToPrivileges(rolesToPrivileges);

        expect(mockRequest.getSession(true)).andReturn(mockSession);

        mockSession.setAttribute("acm_privileges", privilegeMap);

        replayAll();

        unit.addPrivilegesToSession(mockRequest, mockAuthentication);

        verifyAll();

    }

    @Test
    public void addUserLoginMessageToSessionTest()
    {
        AcmUser mockUser = new AcmUser();
        mockUser.setPasswordExpirationDate(LocalDate.now().plusDays(3));
        expect(mockRequest.getSession(false)).andReturn(mockSession);
        expect(mockSession.getAttribute("acm_user")).andReturn(mockUser);
        mockSession.setAttribute("acm_user_message",
                "Your password expires in 3 day(s), please change it before expiration date.");

        replayAll();

        unit.setPasswordExpirationSessionAttribute(mockRequest);

        verifyAll();
    }

    @Test
    public void noUserLoginMessageToSessionTest() throws Exception
    {
        AcmUser mockUser = new AcmUser();
        mockUser.setPasswordExpirationDate(LocalDate.now().plusDays(11));
        expect(mockRequest.getSession(false)).andReturn(mockSession);
        expect(mockSession.getAttribute("acm_user")).andReturn(mockUser);

        replayAll();

        unit.setPasswordExpirationSessionAttribute(mockRequest);

        verifyAll();
    }

    @Test
    public void getPrivilegesForRole()
    {
        createRolesToPrivilegesConfiguration();

        List<Object> privileges = unit.getPrivilegesForRole(roleAdd);

        assertEquals(1, privileges.size());

        assertEquals(privilegeAdd, privileges.get(0));

    }

    @Test
    public void getPrivilegesForWildCardRole()
    {
        createRolesToPrivilegesConfiguration();

        List<Object> privileges = unit.getPrivilegesForRole("ADMIN@ARMEDIA.COM");

        assertEquals(1, privileges.size());

        assertEquals(privilegeAdd, privileges.get(0));

    }

    @Test
    public void getPrivilegesForRole_noPrivileges()
    {

        List<Object> privileges = unit.getPrivilegesForRole(roleAdd);

        assertEquals(0, privileges.size());

    }

    private void createRolesToPrivilegesConfiguration()
    {
        Map<String, List<Object>> rolesToPrivileges = new HashMap<>();
        rolesToPrivileges.put(roleAdd, Arrays.asList(privilegeAdd));
        rolesToPrivileges.put(roleAdmin, Arrays.asList(privilegeAdd));
        rolesToPrivileges.put(wildCardRole, Arrays.asList(privilegeAdd));

        mockRolesToPrivilegesConfig.setRolesToPrivileges(rolesToPrivileges);
    }

}
