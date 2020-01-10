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

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.armedia.acm.pluginmanager.model.AcmPlugin;
import com.armedia.acm.plugins.dashboard.model.Dashboard;
import com.armedia.acm.plugins.dashboard.model.DashboardConstants;
import com.armedia.acm.plugins.dashboard.model.DashboardDto;
import com.armedia.acm.plugins.dashboard.service.DashboardEventPublisher;
import com.armedia.acm.plugins.dashboard.service.DashboardPropertyReader;
import com.armedia.acm.plugins.dashboard.service.DashboardService;
import com.armedia.acm.services.users.model.AcmUser;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by marst on 8/1/14.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-web-acm-web.xml",
        "classpath:/spring/spring-library-dashboard-plugin-test.xml"
})
public class GetDashboardConfigAPIControllerTest extends EasyMockSupport
{

    private MockMvc mockMvc;
    private MockHttpSession mockHttpSession;

    private GetDashboardConfigAPIController unit;

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

        unit = new GetDashboardConfigAPIController();

        unit.setDashboardService(mockDashboardService);
        unit.setEventPublisher(mockDashboardEventPublisher);
        unit.setDashboardPropertyReader(mockDashboardPropertyReader);

        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(exceptionResolver).build();
    }

    @Test
    public void getDashboardConfig() throws Exception
    {
        String ipAddress = "ipAddress";
        String userId = "ann-acm";
        String dashboardConfig = "{\r\n" + 
        		"    \"fruit\": \"Apple\",\r\n" + 
        		"    \"size\": \"Large\",\r\n" + 
        		"    \"color\": \"Red\"\r\n" + 
        		"}";

        AcmUser user = new AcmUser();
        user.setUserId(userId);

        DashboardDto returned = new DashboardDto();
        returned.setUserId(userId);
        returned.setDashboardConfig(dashboardConfig);

        Dashboard ret = new Dashboard();
        ret.setDashboardOwner(user);
        ret.setDashboardConfig(dashboardConfig);

        List<String> retList = new ArrayList<>();
        retList.add(DashboardConstants.DASHBOARD_MODULE_NAME);

        mockHttpSession.setAttribute("acm_ip_address", ipAddress);

        Map<String, Object> prop = new HashMap<>();
        prop.put("key", "value");

        expect(mockDashboardPropertyReader.getModuleNameList()).andReturn(retList);
        expect(mockDashboardService.getDashboardConfigForUserAndModuleName(user, DashboardConstants.DASHBOARD_MODULE_NAME)).andReturn(ret);
        expect(mockDashboardService.getUserByUserId(userId)).andReturn(user);
        expect(mockDashboardService.prepareDashboardDto(ret, false, DashboardConstants.DASHBOARD_MODULE_NAME)).andReturn(returned);

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("ann-acm").atLeastOnce();

        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/v1/plugin/dashboard/get")
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .session(mockHttpSession)
                        .principal(mockAuthentication))
                .andReturn();

        verifyAll();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE));

        String json = result.getResponse().getContentAsString();

        log.info("results: " + json);

        DashboardDto fromJson = new ObjectMapper().readValue(json, DashboardDto.class);

        assertNotNull(fromJson);
        assertEquals(returned.getDashboardConfig(), fromJson.getDashboardConfig());
    }
}
