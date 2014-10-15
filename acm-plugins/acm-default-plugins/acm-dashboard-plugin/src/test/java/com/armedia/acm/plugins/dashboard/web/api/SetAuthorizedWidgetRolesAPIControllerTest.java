package com.armedia.acm.plugins.dashboard.web.api;

import com.armedia.acm.plugins.dashboard.dao.DashboardDao;
import com.armedia.acm.plugins.dashboard.dao.WidgetDao;
import com.armedia.acm.plugins.dashboard.model.Dashboard;
import com.armedia.acm.plugins.dashboard.model.DashboardDto;
import com.armedia.acm.plugins.dashboard.model.widget.RolesGroupByWidgetDto;
import com.armedia.acm.plugins.dashboard.model.widget.Widget;
import com.armedia.acm.plugins.dashboard.model.widget.WidgetRole;
import com.armedia.acm.plugins.dashboard.model.widget.WidgetRoleName;
import com.armedia.acm.plugins.dashboard.service.DashboardEventPublisher;
import com.armedia.acm.plugins.dashboard.service.WidgetEventPublisher;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmRole;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * Created by marjan.stefanoski on 10/7/2014.
 */


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-web-acm-web.xml",
        "classpath:/spring/spring-library-dashboard-plugin-test.xml"
})
public class SetAuthorizedWidgetRolesAPIControllerTest extends EasyMockSupport {

    private MockMvc mockMvc;
    private MockHttpSession mockHttpSession;

    private SetAuthorizedWidgetRolesAPIController unit;

    private WidgetDao mockWidgetDao;
    private UserDao mockUserDao;
    private WidgetEventPublisher mockWidgetEventPublisher;
    private Authentication mockAuthentication;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp() throws Exception {

        mockWidgetDao = createMock(WidgetDao.class);
        mockUserDao = createMock(UserDao.class);
        mockWidgetEventPublisher = createMock(WidgetEventPublisher.class);
        mockHttpSession = new MockHttpSession();
        mockAuthentication = createMock(Authentication.class);

        unit = new SetAuthorizedWidgetRolesAPIController();

        unit.setWidgetDao(mockWidgetDao);
        unit.setEventPublisher(mockWidgetEventPublisher);
        unit.setUserDao(mockUserDao);

        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(exceptionResolver).build();

    }
    @Test
    public void setRolesGroupByWidget() throws Exception {
//
//        String userId = "user";
//
//        Widget widget = new Widget();
//        widget.setWidgetName("New Widget");
//        widget.setWidgetId(5L);
//
//        AcmRole role = new AcmRole();
//        role.setRoleName("testRole");
//
//        WidgetRole widgetRole = new WidgetRole();
//        widgetRole.setRoleName(role.getRoleName());
//        widgetRole.setWidgetId(widget.getWidgetId());
//
//        List<WidgetRoleName> widgetRoleNames = new ArrayList<>();
//        widgetRoleNames.add(new WidgetRoleName(widgetRole.getRoleName()));
//
//        RolesGroupByWidgetDto rolesGroupByWidgetDto = new RolesGroupByWidgetDto();
//        rolesGroupByWidgetDto.setWidgetName(widget.getWidgetName());
//        rolesGroupByWidgetDto.setWidgetAuthorizedRoles(widgetRoleNames);
//        rolesGroupByWidgetDto.setWidgetNotAuthorizedRoles(new ArrayList<WidgetRoleName>());
//
//        AcmUser user = new AcmUser();
//        user.setUserId(userId);
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        String in = objectMapper.writeValueAsString(rolesGroupByWidgetDto);
//
//        log.debug("Input JSON: " + in);
//        // MVC test classes must call getName() somehow
//        expect(mockAuthentication.getName()).andReturn("user").atLeastOnce();
//
//        Capture<RolesGroupByWidgetDto> savedRolesGroupByWidgetDto = new Capture<>();
//
//        expect(mockUserDao.findByUserId(userId)).andReturn(user);
//        expect(mockWidgetDao.deleteAllWidgetRolesByWidgetName(widget.getWidgetName())).andReturn(1);
//        expect(mockWidgetDao.saveWidgetRole(widgetRole)).andReturn(widgetRole).anyTimes();
//
//        expect(mockWidgetDao.getWidgetByWidgetName(widget.getWidgetName())).andReturn(widget);
//        expect(mockUserDao.findAllRoles()).andReturn(Arrays.asList(role));
//
//       mockWidgetEventPublisher.publishSetAuthorizedWidgetRolesEvent(capture(savedRolesGroupByWidgetDto), eq(mockAuthentication),eq("192.168.0.111"), eq(true));
//
//       replayAll();
//
//        MvcResult result = mockMvc.perform(
//                post("/api/latest/plugin/dashboard/widgets/set")
//                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .principal(mockAuthentication)
//                        .content(in))
//                .andReturn();
//
//        log.info("results: " + result.getResponse().getContentAsString());
//
//        verifyAll();
//        String returned = result.getResponse().getContentAsString();
//
//        RolesGroupByWidgetDto mapped = objectMapper.readValue(returned, RolesGroupByWidgetDto.class);
//
//        assertEquals(rolesGroupByWidgetDto.getWidgetAuthorizedRoles(), mapped.getWidgetAuthorizedRoles());
//        assertEquals(rolesGroupByWidgetDto.getWidgetNotAuthorizedRoles(), mapped.getWidgetNotAuthorizedRoles());
//        assertEquals(rolesGroupByWidgetDto.getWidgetName(), mapped.getWidgetName());
    }
}
