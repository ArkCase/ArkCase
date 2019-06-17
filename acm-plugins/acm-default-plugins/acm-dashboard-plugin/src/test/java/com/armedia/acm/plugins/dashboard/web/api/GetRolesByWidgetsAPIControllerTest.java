package com.armedia.acm.plugins.dashboard.web.api;

/*-
 * #%L
 * ACM Default Plugin: Dashboard
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

import static junit.framework.TestCase.assertNotNull;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.plugins.dashboard.dao.WidgetDao;
import com.armedia.acm.plugins.dashboard.exception.AcmWidgetException;
import com.armedia.acm.plugins.dashboard.model.widget.RolesGroupByWidgetDto;
import com.armedia.acm.plugins.dashboard.model.widget.Widget;
import com.armedia.acm.plugins.dashboard.model.widget.WidgetRoleName;
import com.armedia.acm.plugins.dashboard.service.DashboardService;
import com.armedia.acm.plugins.dashboard.service.WidgetEventPublisher;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmRole;
import com.armedia.acm.services.users.model.AcmUser;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by marjan.stefanoski on 10/7/2014.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-web-acm-web.xml",
        "classpath:/spring/spring-library-dashboard-plugin-test.xml"
})
public class GetRolesByWidgetsAPIControllerTest extends EasyMockSupport
{

    private MockMvc mockMvc;
    private MockHttpSession mockHttpSession;

    private GetRolesByWidgetsAPIController unit;

    private WidgetDao mockWidgetDao;
    private UserDao mockUserDao;
    private WidgetEventPublisher mockWidgetEventPublisher;
    private Authentication mockAuthentication;
    private DashboardService mockDashboardService;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    private Logger log = LogManager.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        mockWidgetDao = createMock(WidgetDao.class);
        mockUserDao = createMock(UserDao.class);
        mockWidgetEventPublisher = createMock(WidgetEventPublisher.class);
        mockHttpSession = new MockHttpSession();
        mockAuthentication = createMock(Authentication.class);
        mockDashboardService = createMock(DashboardService.class);

        unit = new GetRolesByWidgetsAPIController();

        unit.setWidgetDao(mockWidgetDao);
        unit.setEventPublisher(mockWidgetEventPublisher);
        unit.setUserDao(mockUserDao);
        unit.setDashboardService(mockDashboardService);

        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(exceptionResolver).build();
    }

    @Test
    public void getRolesByWidgets_noWidgetRolesExistYet() throws Exception
    {
        String ipAddress = "ipAddress";
        Long widgetId = 500L;
        String widgetName = "TEST";

        AcmUser user = new AcmUser();
        user.setUserId("ann-acm");

        Widget returned = new Widget();
        returned.setWidgetId(widgetId);
        returned.setWidgetName(widgetName);

        AcmRole notAuthRole = new AcmRole();
        notAuthRole.setRoleName("ROLE_ADMINISTRATOR");

        List<WidgetRoleName> notAuthRoles = new ArrayList<>();
        notAuthRoles.add(new WidgetRoleName(notAuthRole.getRoleName()));

        RolesGroupByWidgetDto rolesGroupByWidgetDto = new RolesGroupByWidgetDto();
        rolesGroupByWidgetDto.setWidgetName(widgetName);
        rolesGroupByWidgetDto.setName(widgetName);
        rolesGroupByWidgetDto.setWidgetNotAuthorizedRoles(notAuthRoles);
        rolesGroupByWidgetDto.setWidgetAuthorizedRoles(new ArrayList<>());

        Capture<List<RolesGroupByWidgetDto>> captureRoles = EasyMock.newCapture();

        mockHttpSession.setAttribute("acm_ip_address", ipAddress);

        expect(mockWidgetDao.getRolesGroupByWidget()).andReturn(Collections.emptyList());
        expect(mockDashboardService.addNotAuthorizedRolesPerWidget(Collections.emptyList())).andReturn(Arrays.asList(rolesGroupByWidgetDto))
                .anyTimes();

        mockDashboardService.raiseGetEvent(eq(mockAuthentication), eq(mockHttpSession), capture(captureRoles), eq(true));
        expectLastCall().once();

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("ann-acm").atLeastOnce();

        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/v1/plugin/dashboard/widgets/rolesByWidget/all")
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .session(mockHttpSession)
                        .principal(mockAuthentication))
                .andReturn();

        verifyAll();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE));
        String json = result.getResponse().getContentAsString();
        log.info("results: {}", json);
        ObjectMapper objectMapper = new ObjectMapper();
        List<RolesGroupByWidgetDto> rolesGroupByWidgetDtos = objectMapper.readValue(json,
                objectMapper.getTypeFactory().constructParametricType(List.class, RolesGroupByWidgetDto.class));
        assertEquals(1, rolesGroupByWidgetDtos.size());

        RolesGroupByWidgetDto found = captureRoles.getValue().get(0);
        assertEquals(rolesGroupByWidgetDto.getWidgetName(), found.getWidgetName());
    }

    @Test
    public void getRolesByWidgets() throws Exception
    {
        String ipAddress = "ipAddress";
        Long widgetId = 500L;
        String widgetName = "TEST";

        AcmUser user = new AcmUser();
        user.setUserId("ann-acm");

        Widget returned = new Widget();
        returned.setWidgetId(widgetId);
        returned.setWidgetName(widgetName);

        AcmRole userAuthRole = new AcmRole();
        userAuthRole.setRoleName("ROLE_ADMINISTRATOR");

        AcmRole userNotAuthRole = new AcmRole();
        userNotAuthRole.setRoleName("ROLE_CALLCENTER");

        List<WidgetRoleName> authRoles = new ArrayList<>();
        authRoles.add(new WidgetRoleName(userAuthRole.getRoleName()));

        List<WidgetRoleName> notAuthRoles = new ArrayList<>();
        notAuthRoles.add(new WidgetRoleName(userNotAuthRole.getRoleName()));
        Capture<List<RolesGroupByWidgetDto>> captureRoles = EasyMock.newCapture();

        RolesGroupByWidgetDto rolesGroupByWidgetDto = new RolesGroupByWidgetDto();
        rolesGroupByWidgetDto.setWidgetName(widgetName);
        rolesGroupByWidgetDto.setName(widgetName);
        rolesGroupByWidgetDto.setWidgetNotAuthorizedRoles(notAuthRoles);
        rolesGroupByWidgetDto.setWidgetAuthorizedRoles(authRoles);

        mockHttpSession.setAttribute("acm_ip_address", ipAddress);

        expect(mockWidgetDao.getRolesGroupByWidget()).andReturn(Arrays.asList(rolesGroupByWidgetDto));
        expect(mockDashboardService.addNotAuthorizedRolesPerWidget(Arrays.asList(rolesGroupByWidgetDto)))
                .andReturn(Arrays.asList(rolesGroupByWidgetDto))
                .anyTimes();

        mockDashboardService.raiseGetEvent(eq(mockAuthentication), eq(mockHttpSession), capture(captureRoles),
                eq(true));
        expectLastCall().once();

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("ann-acm").atLeastOnce();

        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/v1/plugin/dashboard/widgets/rolesByWidget/all")
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .session(mockHttpSession)
                        .principal(mockAuthentication))
                .andReturn();

        verifyAll();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE));

        String json = result.getResponse().getContentAsString();

        log.info("results: {}", json);

        ObjectMapper objectMapper = new ObjectMapper();

        List<RolesGroupByWidgetDto> rolesGroupByWidgetDtos = objectMapper.readValue(json,
                objectMapper.getTypeFactory().constructParametricType(List.class, RolesGroupByWidgetDto.class));

        assertEquals(1, rolesGroupByWidgetDtos.size());

        RolesGroupByWidgetDto rolesGroupByWidget = rolesGroupByWidgetDtos.get(0);

        assertEquals(rolesGroupByWidget.getWidgetName(), rolesGroupByWidgetDto.getWidgetName());
        assertEquals(rolesGroupByWidget.getWidgetAuthorizedRoles().get(0).getName(),
                rolesGroupByWidgetDto.getWidgetAuthorizedRoles().get(0).getName());
    }

    @Test
    public void getRolesByWidgets_exception() throws Exception
    {
        String ipAddress = "ipAddress";

        AcmUser user = new AcmUser();
        user.setUserId("ann-acm");

        mockHttpSession.setAttribute("acm_ip_address", ipAddress);

        expect(mockWidgetDao.getRolesGroupByWidget()).andThrow(new AcmObjectNotFoundException("objectType", 600L, "test exception")).once();

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("ann-acm").atLeastOnce();

        replayAll();

        Exception exception = null;

        MvcResult result = mockMvc.perform(
                get("/api/v1/plugin/dashboard/widgets/rolesByWidget/all")
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .session(mockHttpSession)
                        .principal(mockAuthentication))
                .andReturn();

        verifyAll();

        assertNotNull(result.getResolvedException());
        assertTrue(result.getResolvedException() instanceof AcmWidgetException);
    }
}
