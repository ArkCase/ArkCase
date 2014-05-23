package com.armedia.acm.auth;

import com.armedia.acm.core.AcmApplication;
import com.armedia.acm.pluginmanager.service.AcmPluginManager;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.easymock.EasyMock.*;

/**
 * Created by dmiller on 3/18/14.
 */
public class AcmLoginSuccessHandlerTest extends EasyMockSupport
{
    private AcmLoginSuccessHandler unit;

    private HttpSession mockSession;
    private Authentication mockAuthentication;
    private HttpServletRequest mockRequest;
    private AcmPluginManager mockPluginManager;

    @Before
    public void setUp()
    {
        unit = new AcmLoginSuccessHandler();

        mockSession = createMock(HttpSession.class);
        mockRequest = createMock(HttpServletRequest.class);
        mockAuthentication = createMock(Authentication.class);
        mockPluginManager = createMock(AcmPluginManager.class);

        unit.setAcmPluginManager(mockPluginManager);
    }

    @Test
    public void addAcmApplicationObjectToSession() throws Exception
    {
        AcmApplication app = new AcmApplication();
        unit.setAcmApplication(app);

        expect(mockRequest.getSession(true)).andReturn(mockSession);
        mockSession.setAttribute("acm_application", app);

        replayAll();

        unit.addAcmApplicationToSession(mockRequest);

        verifyAll();

    }

    @Test
    public void addUserIdToSession() throws Exception
    {
        String userId = "userId";

        expect(mockAuthentication.getName()).andReturn(userId);
        expect(mockRequest.getSession(true)).andReturn(mockSession);
        mockSession.setAttribute("acm_username", userId);

        replayAll();

        unit.addUserIdToSession(mockRequest, mockAuthentication);

        verifyAll();
    }


    @Test
    public void addUserPrivilegesToSession() throws Exception
    {
        String roleAdd = "ROLE_ADD";
        String privilege = "privilege";
        Map<String, Boolean> privilegeMap = new HashMap<>();
        privilegeMap.put(privilege, Boolean.TRUE);

        List<String> privilegeList = Arrays.asList(privilege);

        AcmGrantedAuthority authority = new AcmGrantedAuthority(roleAdd);
        expect((List<AcmGrantedAuthority>) mockAuthentication.getAuthorities()).andReturn(Arrays.asList(authority)).atLeastOnce();

        expect(mockPluginManager.getPrivilegesForRole(roleAdd)).andReturn(privilegeList);

        expect(mockRequest.getSession(true)).andReturn(mockSession);

        mockSession.setAttribute("acm_privileges", privilegeMap);

        replayAll();

        unit.addPrivilegesToSession(mockRequest, mockAuthentication);

        verifyAll();

    }
}
