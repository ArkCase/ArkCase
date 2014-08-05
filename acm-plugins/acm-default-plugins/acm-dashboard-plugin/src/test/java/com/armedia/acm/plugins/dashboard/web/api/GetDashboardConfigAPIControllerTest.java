package com.armedia.acm.plugins.dashboard.web.api;

import com.armedia.acm.plugins.dashboard.dao.DashboardDao;
import com.armedia.acm.plugins.dashboard.model.Dashboard;
import com.armedia.acm.plugins.dashboard.service.DashboardEventPublisher;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import org.codehaus.jackson.map.ObjectMapper;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import javax.persistence.PersistenceException;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by marst on 8/1/14.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-web-acm-web.xml",
        "classpath:/spring/spring-library-dashboard-plugin-test.xml"
})
public class GetDashboardConfigAPIControllerTest extends EasyMockSupport {

    private MockMvc mockMvc;
    private MockHttpSession mockHttpSession;

    private GetDashboardConfigAPIController unit;

    private DashboardDao mockDashboardDao;
    private UserDao mockUserDao;
    private DashboardEventPublisher mockDashboardEventPublisher;
    private Authentication mockAuthentication;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        mockDashboardDao = createMock(DashboardDao.class);
        mockUserDao = createMock(UserDao.class);
        mockDashboardEventPublisher = createMock(DashboardEventPublisher.class);
        mockHttpSession = new MockHttpSession();
        mockAuthentication = createMock(Authentication.class);

        unit = new GetDashboardConfigAPIController();

        unit.setDashboardDao(mockDashboardDao);
        unit.setEventPublisher(mockDashboardEventPublisher);

        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(exceptionResolver).build();
    }

    @Test
    public void getDashboardConfig() throws Exception
    {
        String ipAddress = "ipAddress";
        String userId = "ann-acm";
        String dashboardConfig = "TEST";

        Dashboard returned = new Dashboard();
        returned.setDashboardOwner(mockUserDao.findByUserId(userId));
        returned.setDashobardConfig(dashboardConfig);

        mockHttpSession.setAttribute("acm_ip_address", ipAddress);

        expect(mockDashboardDao.getDashboardConfigForUser(userId)).andReturn(returned);
        mockDashboardEventPublisher.publishGetDashboardByUserIdEvent(
                eq(returned),
                eq(mockAuthentication),
                eq(ipAddress),
                eq(true));

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("ann-acm");

        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/v1/plugin/dashboard/get/{userId}", userId)
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .session(mockHttpSession)
                        .principal(mockAuthentication))
                .andReturn();

        verifyAll();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE));

        String json = result.getResponse().getContentAsString();

        log.info("results: " + json);

        Dashboard fromJson = new ObjectMapper().readValue(json, Dashboard.class);

        assertNotNull(fromJson);
        assertEquals(returned.getDashobardConfig(), fromJson.getDashobardConfig());
    }

    @Test
    public void getDashboardConfig_notFound() throws Exception
    {
        String ipAddress = "ipAddress";
        String userId = "ann-acm";

        mockHttpSession.setAttribute("acm_ip_address", ipAddress);

        expect(mockDashboardDao.getDashboardConfigForUser(userId)).andThrow(new PersistenceException());
        mockDashboardEventPublisher.publishGetDashboardByUserIdEvent(
                anyObject(Dashboard.class),
                eq(mockAuthentication),
                eq(ipAddress),
                eq(false));

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user");

        replayAll();

        mockMvc.perform(
                get("/api/v1/plugin/dashboard/get/{userId}", userId)
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .session(mockHttpSession)
                        .principal(mockAuthentication))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN));

        verifyAll();
    }


}
