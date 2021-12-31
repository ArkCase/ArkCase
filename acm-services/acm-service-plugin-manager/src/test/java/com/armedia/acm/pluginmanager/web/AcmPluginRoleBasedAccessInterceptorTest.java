package com.armedia.acm.pluginmanager.web;

/*-
 * #%L
 * ACM Service: Plugin Manager
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

import com.armedia.acm.core.exceptions.AcmNotAuthorizedException;
import com.armedia.acm.pluginmanager.model.AcmPlugin;
import com.armedia.acm.pluginmanager.model.AcmPluginPrivilege;
import com.armedia.acm.pluginmanager.model.AcmPluginUrlPrivilege;
import com.armedia.acm.pluginmanager.model.ApplicationPluginPrivilegesConfig;
import com.armedia.acm.services.authenticationtoken.service.AuthenticationTokenService;
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
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.fail;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertTrue;

/**
 * Created by armdev on 5/14/14.
 */
public class AcmPluginRoleBasedAccessInterceptorTest extends EasyMockSupport
{
    private HttpServletResponse mockResponse;
    private HttpServletRequest mockRequest;
    private HttpSession mockSession;
    private AuthenticationTokenService mockTokenService;


    private AcmPluginRoleBasedAccessInterceptor unit;
    private ApplicationPluginPrivilegesConfig pluginPrivilegesConfig;
    @Before
    public void setUp()
    {
        mockResponse = createMock(HttpServletResponse.class);
        mockRequest = createMock(HttpServletRequest.class);
        mockSession = createMock(HttpSession.class);
        mockTokenService = createMock(AuthenticationTokenService.class);

        unit = new AcmPluginRoleBasedAccessInterceptor();
        unit.setAuthenticationTokenService(mockTokenService);
        pluginPrivilegesConfig = new ApplicationPluginPrivilegesConfig();
    }

    @Test
    public void preHandle_noSession() throws Exception
    {
        expect(mockRequest.getQueryString()).andReturn("/url").atLeastOnce();
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
        expect(mockRequest.getQueryString()).andReturn("/url").atLeastOnce();
        expect(mockRequest.getServletPath()).andReturn("/url").atLeastOnce();
        expect(mockRequest.getMethod()).andReturn(HttpMethod.GET.name());

        expect(mockRequest.getSession(false)).andReturn(mockSession);
        expect(mockSession.getAttribute("acm_privileges")).andReturn(null);

        replayAll();

        try
        {
            unit.preHandle(mockRequest, mockResponse, null);
            fail("Expecting AcmNotAuthorizedException but did not happen");
        }
        catch (AcmNotAuthorizedException exception)
        {
            // do nothing, should throw exception
        }

        verifyAll();
    }

    @Test
    public void preHandle_emptyPrivileges() throws Exception
    {
        expect(mockRequest.getSession(false)).andReturn(mockSession);
        expect(mockSession.getAttribute("acm_privileges")).andReturn(Collections.emptyMap());

        expect(mockRequest.getQueryString()).andReturn(null).atLeastOnce();
        expect(mockRequest.getServletPath()).andReturn("/url").atLeastOnce();
        expect(mockRequest.getMethod()).andReturn(HttpMethod.GET.name());

        replayAll();

        try
        {
            unit.setPluginPrivilegesConfig(pluginPrivilegesConfig);
            unit.preHandle(mockRequest, mockResponse, null);
            fail("Expecting AcmNotAuthorizedException but did not happen");
        }
        catch (AcmNotAuthorizedException exception)
        {
            // do nothing, should throw exception
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

        expect(mockRequest.getSession(false)).andReturn(mockSession);

        Map<String, Boolean> userPrivs = new HashMap<>();
        userPrivs.put("anotherPrivilege", Boolean.TRUE);

        expect(mockSession.getAttribute("acm_privileges")).andReturn(userPrivs);

        expect(mockRequest.getQueryString()).andReturn(null).atLeastOnce();
        expect(mockRequest.getServletPath()).andReturn("/url").atLeastOnce();
        expect(mockRequest.getMethod()).andReturn(HttpMethod.GET.name());

        replayAll();

        try
        {
            unit.setPluginPrivilegesConfig(pluginPrivilegesConfig);
            unit.preHandle(mockRequest, mockResponse, null);
            fail("Expecting AcmNotAuthorizedException but did not happen");
        }
        catch (AcmNotAuthorizedException exception)
        {
            // do nothing, should throw exception
        }

        verifyAll();

    }

    @Test
    public void preHandle_rightPrivileges() throws Exception
    {
        expect(mockRequest.getSession(false)).andReturn(mockSession);

        Map<String, Map<String, List<String>>> acmUrlPrivilegeConfig = new HashMap<>();
        Map<String, List<String>> urlHttpMehtods = new HashMap<>();
        urlHttpMehtods.put("GET", Arrays.asList("/url"));
        acmUrlPrivilegeConfig.put("privilegeName", urlHttpMehtods);

        pluginPrivilegesConfig.setPluginConfigPrivileges(acmUrlPrivilegeConfig);

        unit.setPluginPrivilegesConfig(pluginPrivilegesConfig);

        Map<String, Boolean> userPrivs = new HashMap<>();
        userPrivs.put("privilegeName", Boolean.TRUE);

        expect(mockSession.getAttribute("acm_privileges")).andReturn(userPrivs);

        expect(mockRequest.getQueryString()).andReturn(null).atLeastOnce();

        expect(mockRequest.getServletPath()).andReturn("/url").atLeastOnce();
        expect(mockRequest.getMethod()).andReturn(HttpMethod.GET.name());

        replayAll();

        boolean proceed = unit.preHandle(mockRequest, mockResponse, null);

        verifyAll();

        assertTrue(proceed);
    }

}
