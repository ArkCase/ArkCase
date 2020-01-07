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

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.armedia.acm.plugins.dashboard.model.Dashboard;
import com.armedia.acm.plugins.dashboard.model.DashboardConstants;
import com.armedia.acm.plugins.dashboard.model.DashboardDto;
import com.armedia.acm.plugins.dashboard.service.DashboardEventPublisher;
import com.armedia.acm.plugins.dashboard.service.DashboardPropertyReader;
import com.armedia.acm.plugins.dashboard.service.DashboardService;
import com.armedia.acm.services.users.model.AcmUser;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.easymock.Capture;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-web-acm-web.xml",
        "classpath:/spring/spring-library-dashboard-plugin-test.xml"
})
public class SetDashboardConfigAPIControllerTest extends EasyMockSupport
{

    private MockMvc mockMvc;
    private MockHttpSession mockHttpSession;

    private SetDashboardConfigAPIController unit;

    private DashboardService mockDashboardService;
    private DashboardEventPublisher mockDashboardEventPublisher;
    private Authentication mockAuthentication;
    private DashboardPropertyReader mockDashboardPropertyReader;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    private Logger log = LogManager.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {

        mockDashboardService = createMock(DashboardService.class);
        mockDashboardEventPublisher = createMock(DashboardEventPublisher.class);
        mockHttpSession = new MockHttpSession();
        mockAuthentication = createMock(Authentication.class);
        mockDashboardPropertyReader = createMock(DashboardPropertyReader.class);

        unit = new SetDashboardConfigAPIController();

        unit.setDashboardService(mockDashboardService);
        unit.setEventPublisher(mockDashboardEventPublisher);
        unit.setDashboardPropertyReader(mockDashboardPropertyReader);

        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(exceptionResolver).build();

    }

    @Test
    public void setDashboardConfig() throws Exception
    {

        String userId = "ann-acm";

        Dashboard dashboard = new Dashboard();
        dashboard.setDashboardConfig("UPDATE TEST");

        AcmUser user = new AcmUser();
        user.setUserId(userId);

        DashboardDto dashboardDto = new DashboardDto();
        dashboardDto.setDashboardConfig("UPDATE TEST");

        ObjectMapper objectMapper = new ObjectMapper();
        String in = objectMapper.writeValueAsString(dashboardDto);

        log.debug("Input JSON: " + in);
        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("ann-acm").atLeastOnce();

        Capture<DashboardDto> savedDashboardDto = new Capture<>();
        Capture<Dashboard> publishedDashboard = new Capture<>();

        List<String> retList = new ArrayList<>();
        retList.add(DashboardConstants.DASHBOARD_MODULE_NAME);

        expect(mockDashboardPropertyReader.getModuleNameList()).andReturn(retList);
        expect(mockDashboardService.getUserByUserId(userId)).andReturn(user);
        expect(mockDashboardService.getDashboardConfigForUserAndModuleName(user, DashboardConstants.DASHBOARD_MODULE_NAME))
                .andReturn(dashboard);
        expect(mockDashboardService.setDashboardConfigForUserAndModule(eq(user), capture(savedDashboardDto),
                eq(DashboardConstants.DASHBOARD_MODULE_NAME))).andReturn(1);

        mockDashboardEventPublisher.publishDashboardEvent(capture(publishedDashboard), eq(mockAuthentication), eq(false), eq(true));

        replayAll();

        MvcResult result = mockMvc.perform(
                post("/api/latest/plugin/dashboard/set")
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(mockAuthentication)
                        .content(in))
                .andReturn();

        log.info("results: " + result.getResponse().getContentAsString());

        verifyAll();
        String returned = result.getResponse().getContentAsString();

        DashboardDto mapped = objectMapper.readValue(returned, DashboardDto.class);

        assertEquals(dashboardDto.getDashboardConfig(), mapped.getDashboardConfig());
    }

    @Test
    public void invalidInput() throws Exception
    {
        String notDashboardJson = "{ \"user\": \"dmiller\" }";

        String userId = "ann-acm";

        Dashboard dashboard = new Dashboard();
        dashboard.setDashboardConfig("UPDATE TEST");

        AcmUser user = new AcmUser();
        user.setUserId(userId);

        DashboardDto dashboardDto = new DashboardDto();
        dashboardDto.setDashboardConfig("UPDATE TEST");

        Capture<DashboardDto> savedDashboardDto = new Capture<>();
        Capture<Dashboard> publishedDashboard = new Capture<>();

        List<String> retList = new ArrayList<>();
        retList.add(DashboardConstants.DASHBOARD_MODULE_NAME);

        expect(mockDashboardPropertyReader.getModuleNameList()).andReturn(retList);
        expect(mockDashboardService.getUserByUserId(userId)).andReturn(user);
        expect(mockDashboardService.getDashboardConfigForUserAndModuleName(user, DashboardConstants.DASHBOARD_MODULE_NAME))
                .andReturn(dashboard);

        // With upgrading spring version, bad JSON is not the problem for entering the execution in the controller
        expect(mockDashboardService.setDashboardConfigForUserAndModule(eq(user), capture(savedDashboardDto),
                eq(DashboardConstants.DASHBOARD_MODULE_NAME))).andThrow(new RuntimeException());
        mockDashboardEventPublisher.publishDashboardEvent(capture(publishedDashboard), eq(mockAuthentication), eq(false), eq(false));

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("ann-acm").atLeastOnce();

        // when the JSON can't be converted to a Complaint POJO, Spring MVC will not even call our controller method.
        // so we can't raise a failure event. None of our services should be called, so there are no
        // expectations.
        replayAll();

        mockMvc.perform(
                post("/api/latest/plugin/dashboard/set")
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(mockAuthentication)
                        .content(notDashboardJson))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN));

        verifyAll();
    }
}
