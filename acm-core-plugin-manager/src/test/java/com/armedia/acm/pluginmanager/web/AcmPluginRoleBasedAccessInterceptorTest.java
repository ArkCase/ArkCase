package com.armedia.acm.pluginmanager.web;

import com.armedia.acm.pluginmanager.model.AcmPlugin;
import com.armedia.acm.pluginmanager.model.AcmPluginPrivilege;
import com.armedia.acm.pluginmanager.model.AcmPluginUrlPrivilege;
import com.armedia.acm.pluginmanager.service.AcmPluginManager;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

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
    private ServletOutputStream mockOutputStream;

    private AcmPluginRoleBasedAccessInterceptor unit;
    private AcmPluginManager acmPluginManager;

    @Before
    public void setUp()
    {
        mockResponse = createMock(HttpServletResponse.class);
        mockRequest = createMock(HttpServletRequest.class);
        mockSession = createMock(HttpSession.class);
        mockOutputStream = createMock(ServletOutputStream.class);

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

        insufficientPrivilegeExpectations();

        replayAll();

        boolean proceed = unit.preHandle(mockRequest, mockResponse, null);

        verifyAll();

        assertFalse(proceed);
    }

    @Test
    public void preHandle_insufficientPrivilege() throws Exception
    {
        expect(mockRequest.getSession(false)).andReturn(mockSession);
        expect(mockSession.getAttribute("acm_privileges")).andReturn(Collections.emptyMap());

        expect(mockRequest.getServletPath()).andReturn("/url").atLeastOnce();
        expect(mockRequest.getMethod()).andReturn(HttpMethod.GET.name());
        insufficientPrivilegeExpectations();

        replayAll();

        boolean proceed = unit.preHandle(mockRequest, mockResponse, null);

        verifyAll();

        assertFalse(proceed);
    }

    @Test
    public void preHandle_hasPrivilege() throws Exception
    {
        expect(mockRequest.getSession(false)).andReturn(mockSession);
        expect(mockSession.getAttribute("acm_privileges")).andReturn(Collections.emptyMap());

        expect(mockRequest.getServletPath()).andReturn("/url").atLeastOnce();
        expect(mockRequest.getMethod()).andReturn(HttpMethod.GET.name());

        AcmPluginPrivilege privilege = new AcmPluginPrivilege();
        privilege.setPrivilegeName("privilegeName");
        AcmPluginUrlPrivilege urlPrivilege = new AcmPluginUrlPrivilege();
        urlPrivilege.setHttpMethod(HttpMethod.GET);
        urlPrivilege.setUrl("/url");
        urlPrivilege.setRequiredPrivilege(privilege);

        AcmPlugin plugin = new AcmPlugin();
        plugin.setUrlPrivileges(Arrays.asList(urlPrivilege));
        acmPluginManager.registerPlugin(plugin);


        replayAll();

        boolean proceed = unit.preHandle(mockRequest, mockResponse, null);

        verifyAll();

        assertTrue(proceed);
    }

    protected void insufficientPrivilegeExpectations() throws IOException
    {
        mockResponse.setStatus(HttpStatus.FORBIDDEN.value());
        mockResponse.setContentType(MediaType.TEXT_PLAIN_VALUE);
        mockResponse.setContentLength(anyInt());
        expect(mockResponse.getOutputStream()).andReturn(mockOutputStream).atLeastOnce();
        mockOutputStream.write(anyObject(byte[].class));
        mockOutputStream.flush();
    }
}
