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

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.armedia.acm.plugins.dashboard.dao.WidgetDao;
import com.armedia.acm.plugins.dashboard.model.widget.Widget;
import com.armedia.acm.plugins.dashboard.service.DashboardPropertyReader;
import com.armedia.acm.plugins.dashboard.service.DashboardService;
import com.armedia.acm.plugins.dashboard.service.WidgetEventPublisher;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.service.AcmUserRoleService;
import com.fasterxml.jackson.databind.ObjectMapper;

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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by marjan.stefanoski on 9/24/2014.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-web-acm-web.xml",
        "classpath:/spring/spring-library-dashboard-plugin-test.xml"
})
public class GetWidgetsByUserRolesAPIControllerTest extends EasyMockSupport
{

    private MockMvc mockMvc;
    private MockHttpSession mockHttpSession;

    private GetWidgetsByUserRolesAPIController unit;

    private WidgetDao mockWidgetDao;
    private AcmUserRoleService mockUserRoleService;
    private WidgetEventPublisher mockWidgetEventPublisher;
    private Authentication mockAuthentication;
    private DashboardPropertyReader mockDashboardPropertyReader;
    private DashboardService mockDashboardService;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    private Logger log = LogManager.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        mockWidgetDao = createMock(WidgetDao.class);
        mockWidgetEventPublisher = createMock(WidgetEventPublisher.class);
        mockHttpSession = new MockHttpSession();
        mockAuthentication = createMock(Authentication.class);
        mockDashboardPropertyReader = createMock(DashboardPropertyReader.class);
        mockDashboardService = createMock(DashboardService.class);
        mockUserRoleService = createMock(AcmUserRoleService.class);

        unit = new GetWidgetsByUserRolesAPIController();

        unit.setWidgetDao(mockWidgetDao);
        unit.setEventPublisher(mockWidgetEventPublisher);
        unit.setDashboardPropertyReader(mockDashboardPropertyReader);
        unit.setDashboardService(mockDashboardService);
        unit.setUserRoleService(mockUserRoleService);

        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(exceptionResolver).build();
    }

    @Test
    public void getAllWidgetsByRole() throws Exception
    {
        String ipAddress = "ipAddress";
        Long widgetId = 500L;
        String widgetName = "TEST";

        AcmUser user = new AcmUser();
        user.setUserId("ann-acm");

        Widget returned = new Widget();
        returned.setWidgetId(widgetId);
        returned.setWidgetName(widgetName);

        mockHttpSession.setAttribute("acm_ip_address", ipAddress);

        Set<String> userRoles = new HashSet<>(Arrays.asList("ROLE_ADMINISTRATOR"));

        expect(mockUserRoleService.getUserRoles("ann-acm")).andReturn(userRoles);
        expect(mockWidgetDao.getAllWidgetsByRoles(userRoles)).andReturn(Arrays.asList(returned));
        expect(mockDashboardService.onlyUniqueValues(Arrays.asList(returned))).andReturn(Arrays.asList(returned));
        mockWidgetEventPublisher.publishGetWidgetsByUserRoles(
                eq(Arrays.asList(returned)),
                eq(mockAuthentication),
                eq(ipAddress),
                eq(true));
        expect(mockDashboardPropertyReader.getDashboardWidgetsOnly()).andReturn(Arrays.asList(returned));

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("ann-acm").atLeastOnce();

        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/v1/plugin/dashboard/widgets/get")
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .session(mockHttpSession)
                        .principal(mockAuthentication))
                .andReturn();

        verifyAll();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE));

        String json = result.getResponse().getContentAsString();

        log.info("results: [{}]", json);

        ObjectMapper objectMapper = new ObjectMapper();

        List<Widget> foundTasks = objectMapper.readValue(json,
                objectMapper.getTypeFactory().constructParametricType(List.class, Widget.class));

        assertEquals(1, foundTasks.size());
    }
}
