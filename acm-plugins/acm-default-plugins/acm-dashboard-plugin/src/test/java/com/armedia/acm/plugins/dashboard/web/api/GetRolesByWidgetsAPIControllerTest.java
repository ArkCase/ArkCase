package com.armedia.acm.plugins.dashboard.web.api;

import com.armedia.acm.plugins.dashboard.dao.WidgetDao;
import com.armedia.acm.plugins.dashboard.model.widget.RolesGroupByWidgetDto;
import com.armedia.acm.plugins.dashboard.model.widget.Widget;
import com.armedia.acm.plugins.dashboard.model.widget.WidgetRoleName;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Created by marjan.stefanoski on 10/7/2014.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-web-acm-web.xml",
        "classpath:/spring/spring-library-dashboard-plugin-test.xml"
})
public class GetRolesByWidgetsAPIControllerTest extends EasyMockSupport {

    private MockMvc mockMvc;
    private MockHttpSession mockHttpSession;

    private GetRolesByWidgetsAPIController unit;

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


        unit = new GetRolesByWidgetsAPIController();

        unit.setWidgetDao(mockWidgetDao);
        unit.setEventPublisher(mockWidgetEventPublisher);
        unit.setUserDao(mockUserDao);

        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(exceptionResolver).build();
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

        List<WidgetRoleName> authRoles = new ArrayList<WidgetRoleName>();
        authRoles.add(new WidgetRoleName(userAuthRole.getRoleName()));

        List<WidgetRoleName> notAuthRoles = new ArrayList<WidgetRoleName>();
        notAuthRoles.add(new WidgetRoleName(userNotAuthRole.getRoleName()));

        RolesGroupByWidgetDto rolesGroupByWidgetDto  = new RolesGroupByWidgetDto();
        rolesGroupByWidgetDto.setWidgetName(widgetName);
        rolesGroupByWidgetDto.setName(widgetName);
        rolesGroupByWidgetDto.setWidgetNotAuthorizedRoles(notAuthRoles);
        rolesGroupByWidgetDto.setWidgetAuthorizedRoles(authRoles);

        mockHttpSession.setAttribute("acm_ip_address", ipAddress);

        expect(mockWidgetDao.getRolesGroupByWidget()).andReturn(Arrays.asList(rolesGroupByWidgetDto)).anyTimes();
        expect(mockWidgetDao.findAll()).andReturn(Arrays.asList(returned)).anyTimes();
        expect(mockUserDao.findAllRoles()).andReturn(Arrays.asList(userAuthRole)).anyTimes();
        mockWidgetEventPublisher.publishGeRolesByWidgets(
                eq(Arrays.asList(rolesGroupByWidgetDto)),
                eq(mockAuthentication),
                eq(ipAddress),
                eq(true));

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

        log.info("results: " + json);

        ObjectMapper objectMapper = new ObjectMapper();

        List<RolesGroupByWidgetDto> rolesGroupByWidgetDtos = objectMapper.readValue(json,
                objectMapper.getTypeFactory().constructParametricType(List.class, RolesGroupByWidgetDto.class));

        assertEquals(1, rolesGroupByWidgetDtos.size());

        RolesGroupByWidgetDto rolesGroupByWidget = rolesGroupByWidgetDtos.get(0);

        assertEquals(rolesGroupByWidget.getWidgetName(),rolesGroupByWidgetDto.getWidgetName());
        assertEquals(rolesGroupByWidget.getWidgetAuthorizedRoles().get(0).getName(),rolesGroupByWidgetDto.getWidgetAuthorizedRoles().get(0).getName());
    }

}
