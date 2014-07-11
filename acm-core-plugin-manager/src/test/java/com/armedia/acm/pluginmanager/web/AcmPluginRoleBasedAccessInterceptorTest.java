package com.armedia.acm.pluginmanager.web;

import com.armedia.acm.core.exceptions.AcmNotAuthorizedException;
import com.armedia.acm.pluginmanager.model.AcmPlugin;
import com.armedia.acm.pluginmanager.model.AcmPluginPrivilege;
import com.armedia.acm.pluginmanager.model.AcmPluginUrlPrivilege;
import com.armedia.acm.pluginmanager.service.AcmPluginManager;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

/**
 * Created by armdev on 5/14/14.
 */
public class AcmPluginRoleBasedAccessInterceptorTest extends EasyMockSupport
{
    private HttpServletResponse mockResponse;
    private HttpServletRequest mockRequest;
    private HttpSession mockSession;

    private AcmPluginRoleBasedAccessInterceptor unit;
    private AcmPluginManager acmPluginManager;

    @Before
    public void setUp()
    {
        mockResponse = createMock(HttpServletResponse.class);
        mockRequest = createMock(HttpServletRequest.class);
        mockSession = createMock(HttpSession.class);

        unit = new AcmPluginRoleBasedAccessInterceptor();

        acmPluginManager = new AcmPluginManager();
        unit.setAcmPluginManager(acmPluginManager);
    }

    @Test
    public void preHandle_noSession() throws Exception
    {
        expect(mockRequest.getServletPath()).andReturn("/url").atLeastOnce();
        expect(mockRequest.getMethod()).andReturn(HttpMethod.GET.name());

        expect(mockRequest.getSession(false)).andReturn(null);

        replayAll();

        boolean proceed = unit.preHandle(mockRequest, mockResponse, null);

        verifyAll();

        assertTrue(proceed);
    }

    @Test
    public void preHandle_noPrivileges() throws Exception
    {
        expect(mockRequest.getServletPath()).andReturn("/url").atLeastOnce();
        expect(mockRequest.getMethod()).andReturn(HttpMethod.GET.name());

        expect(mockRequest.getSession(false)).andReturn(mockSession);
        expect(mockSession.getAttribute("acm_privileges")).andReturn(null);

        replayAll();

        try
        {
            unit.preHandle(mockRequest, mockResponse, null);
            fail("Should have gotten an unauthorized exception");
        }
        catch (AcmNotAuthorizedException e)
        {
            // ok - expected
        }

        verifyAll();

    }

    @Test
    public void preHandle_emptyPrivileges() throws Exception
    {
        expect(mockRequest.getSession(false)).andReturn(mockSession);
        expect(mockSession.getAttribute("acm_privileges")).andReturn(Collections.emptyMap());

        expect(mockRequest.getServletPath()).andReturn("/url").atLeastOnce();
        expect(mockRequest.getMethod()).andReturn(HttpMethod.GET.name());

        replayAll();

        try
        {
            unit.preHandle(mockRequest, mockResponse, null);
            fail("Should have gotten an unauthorized exception");
        }
        catch (AcmNotAuthorizedException e)
        {
            // ok - expected
        }

        verifyAll();
    }

    @Test
    public void preHandle_wrongPrivileges() throws Exception
    {
        AcmPluginPrivilege privilege = new AcmPluginPrivilege();
        privilege.setPrivilegeName("privilegeName");
        AcmPluginUrlPrivilege urlPrivilege = new AcmPluginUrlPrivilege();
        urlPrivilege.setHttpMethod(HttpMethod.GET);
        urlPrivilege.setUrl("/url");
        urlPrivilege.setRequiredPrivilege(privilege);

        AcmPlugin plugin = new AcmPlugin();
        plugin.setUrlPrivileges(Arrays.asList(urlPrivilege));
        acmPluginManager.registerPlugin(plugin);

        expect(mockRequest.getSession(false)).andReturn(mockSession);

        Map<String, Boolean> userPrivs = new HashMap<>();
        userPrivs.put("anotherPrivilege", Boolean.TRUE);

        expect(mockSession.getAttribute("acm_privileges")).andReturn(userPrivs);

        expect(mockRequest.getServletPath()).andReturn("/url").atLeastOnce();
        expect(mockRequest.getMethod()).andReturn(HttpMethod.GET.name());

        replayAll();

        try
        {
            unit.preHandle(mockRequest, mockResponse, null);
            fail("Should have gotten an unauthorized exception");
        }
        catch (AcmNotAuthorizedException e)
        {
            // ok - expected
        }

        verifyAll();


    }

    @Test
    public void preHandle_rightPrivileges() throws Exception
    {
        AcmPluginPrivilege privilege = new AcmPluginPrivilege();
        privilege.setPrivilegeName("privilegeName");
        AcmPluginUrlPrivilege urlPrivilege = new AcmPluginUrlPrivilege();
        urlPrivilege.setHttpMethod(HttpMethod.GET);
        urlPrivilege.setUrl("/url");
        urlPrivilege.setRequiredPrivilege(privilege);

        AcmPlugin plugin = new AcmPlugin();
        plugin.setUrlPrivileges(Arrays.asList(urlPrivilege));
        acmPluginManager.registerPlugin(plugin);

        expect(mockRequest.getSession(false)).andReturn(mockSession);

        Map<String, Boolean> userPrivs = new HashMap<>();
        userPrivs.put("privilegeName", Boolean.TRUE);

        expect(mockSession.getAttribute("acm_privileges")).andReturn(userPrivs);

        expect(mockRequest.getServletPath()).andReturn("/url").atLeastOnce();
        expect(mockRequest.getMethod()).andReturn(HttpMethod.GET.name());

        replayAll();

        boolean proceed = unit.preHandle(mockRequest, mockResponse, null);

        verifyAll();

        assertTrue(proceed);
    }


}
