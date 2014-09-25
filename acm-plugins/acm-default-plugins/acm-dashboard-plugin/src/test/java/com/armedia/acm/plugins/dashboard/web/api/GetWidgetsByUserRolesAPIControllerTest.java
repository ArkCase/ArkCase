package com.armedia.acm.plugins.dashboard.web.api;


import com.armedia.acm.plugins.dashboard.dao.WidgetDao;
import com.armedia.acm.plugins.dashboard.model.widget.Widget;
import com.armedia.acm.plugins.dashboard.service.WidgetEventPublisher;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmRole;
import com.armedia.acm.services.users.model.AcmUser;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.Arrays;
import java.util.List;

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Created by marjan.stefanoski on 9/24/2014.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-web-acm-web.xml",
        "classpath:/spring/spring-library-dashboard-plugin-test.xml"
})
public class GetWidgetsByUserRolesAPIControllerTest extends EasyMockSupport {

    private MockMvc mockMvc;
    private MockHttpSession mockHttpSession;

    private GetWidgetsByUserRolesAPIController unit;

    private WidgetDao mockWidgetDao;
    private UserDao mockUserDao;
    private WidgetEventPublisher mockWidgetEventPublisher;
    private Authentication mockAuthentication;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        mockWidgetDao = createMock(WidgetDao.class);
        mockUserDao = createMock(UserDao.class);
        mockWidgetEventPublisher = createMock(WidgetEventPublisher.class);
        mockHttpSession = new MockHttpSession();
        mockAuthentication = createMock(Authentication.class);


        unit = new GetWidgetsByUserRolesAPIController();

        unit.setWidgetDao(mockWidgetDao);
        unit.setEventPublisher(mockWidgetEventPublisher);
        unit.setUserDao(mockUserDao);

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

        AcmRole userRole = new AcmRole();
        userRole.setRoleName("ROLE_ADMINISTRATOR");

        mockHttpSession.setAttribute("acm_ip_address", ipAddress);

        expect(mockWidgetDao.getAllWidgetsByRoles(Arrays.asList(userRole))).andReturn(Arrays.asList(returned));
        expect(mockUserDao.findAllRolesByUser(user.getUserId())).andReturn(Arrays.asList(userRole));
        mockWidgetEventPublisher.publishGetWidgetsByUserRoles(
                eq(Arrays.asList(returned)),
                eq(mockAuthentication),
                eq(ipAddress),
                eq(true));

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

        log.info("results: " + json);

        ObjectMapper objectMapper = new ObjectMapper();

        List<Widget> foundTasks = objectMapper.readValue(json,
                objectMapper.getTypeFactory().constructParametricType(List.class, Widget.class));

        assertEquals(1, foundTasks.size());
    }
}
