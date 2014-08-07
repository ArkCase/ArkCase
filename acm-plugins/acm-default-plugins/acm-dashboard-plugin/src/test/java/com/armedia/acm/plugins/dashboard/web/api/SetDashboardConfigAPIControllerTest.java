package com.armedia.acm.plugins.dashboard.web.api;

import com.armedia.acm.plugins.dashboard.dao.DashboardDao;
import com.armedia.acm.plugins.dashboard.model.Dashboard;
import com.armedia.acm.plugins.dashboard.model.DashboardDto;
import com.armedia.acm.plugins.dashboard.service.DashboardEventPublisher;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import org.codehaus.jackson.map.ObjectMapper;
import org.easymock.Capture;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-web-acm-web.xml",
        "classpath:/spring/spring-library-dashboard-plugin-test.xml"
})
public class SetDashboardConfigAPIControllerTest extends EasyMockSupport {

    private MockMvc mockMvc;
    private MockHttpSession mockHttpSession;

    private SetDashboardConfigAPIController unit;

    private DashboardDao mockDashboardDao;
    private UserDao mockUserDao;
    private DashboardEventPublisher mockDashboardEventPublisher;
    private Authentication mockAuthentication;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp() throws Exception {

        mockDashboardDao = createMock(DashboardDao.class);
        mockUserDao = createMock(UserDao.class);
        mockDashboardEventPublisher = createMock(DashboardEventPublisher.class);
        mockHttpSession = new MockHttpSession();
        mockAuthentication = createMock(Authentication.class);

        unit = new SetDashboardConfigAPIController();

        unit.setDashboardDao(mockDashboardDao);
        unit.setEventPublisher(mockDashboardEventPublisher);
        unit.setUserDao(mockUserDao);
        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(exceptionResolver).build();

    }

    @Test
    public void setDashboardConfig() throws Exception {

        String userId = "ann-acm";

        Dashboard dashboard = new Dashboard();
        dashboard.setDashobardConfig("UPDATE TEST");

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

        expect(mockUserDao.findByUserId(userId)).andReturn(user);
        expect(mockDashboardDao.getDashboardConfigForUser(user)).andReturn(dashboard);
        expect(mockDashboardDao.setDasboardConfigForUser(eq(user), capture(savedDashboardDto))).andReturn(1);

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

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("ann-acm").atLeastOnce();

        // when the JSON can't be converted to a Complaint POJO, Spring MVC will not even call our controller method.
        // so we can't raise a failure event.  None of our services should be called, so there are no
        // expectations.
        replayAll();

        mockMvc.perform(
                post("/api/latest/plugin/dashboard/set")
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(mockAuthentication)
                        .content(notDashboardJson))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN));

        verifyAll();
    }
}
