package com.armedia.acm.services.notification.web.api;

import com.armedia.acm.services.notification.dao.NotificationDao;
import com.armedia.acm.services.notification.model.Notification;
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

import javax.persistence.QueryTimeoutException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-web-acm-web.xml",
        "classpath:/spring/spring-library-notification-plugin-test.xml"
})
public class ListAllNotificationsAPIControllerTest extends EasyMockSupport
{
    private MockMvc mockMvc;
    private Authentication mockAuthentication;
    private NotificationDao mockNotificationDao;
    private MockHttpSession mockHttpSession;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    private ListAllNotificationsAPIController unit;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {

        mockNotificationDao = createMock(NotificationDao.class);
        mockHttpSession = new MockHttpSession();
        mockAuthentication = createMock(Authentication.class);
        unit = new ListAllNotificationsAPIController();

        unit.setNotificationDao(mockNotificationDao);
        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(exceptionResolver).build();
    }

    @Test
    public void ListNotification() throws Exception
    {
        String user = "ann-acm";

        Notification notificationAnn = new Notification();
        /*Notification notificationIan = new Notification();

        Notification notificationEveryone = new Notification();*/


        notificationAnn.setId(700L);
        notificationAnn.setCreator("testCreator");
        notificationAnn.setCreated(new Date());
        notificationAnn.setUser(user);
        notificationAnn.setStatus("status");
        notificationAnn.setAction("action");
        notificationAnn.setModifier("modifier");
        notificationAnn.setModified(new Date());
        notificationAnn.setData("data");
        notificationAnn.setAuto("auto");
        notificationAnn.setNote("note");

        /*notificationEveryone.setId(700L);
        notificationEveryone.setCreator("testCreator");
        notificationEveryone.setCreated(new Date());
        notificationEveryone.setUser("EVERYONE");
        notificationEveryone.setStatus("status");
        notificationEveryone.setAction("action");
        notificationEveryone.setComment("comment");
        notificationEveryone.setModifier("modifier");
        notificationEveryone.setModified(new Date());
        notificationEveryone.setData("data");
        notificationEveryone.setAuto("auto");
        notificationEveryone.setNote("note");

        notificationIan.setId(700L);
        notificationIan.setCreator("testCreator");
        notificationIan.setCreated(new Date());
        notificationIan.setUser("ian");
        notificationIan.setStatus("status");
        notificationIan.setAction("action");
        notificationIan.setComment("comment");
        notificationIan.setModifier("modifier");
        notificationIan.setModified(new Date());
        notificationIan.setData("data");
        notificationIan.setAuto("auto");
        notificationIan.setNote("note");
*/

        List<Notification> notificationList = new ArrayList<>();
        notificationList.add(notificationAnn);
        /*notificationList.add(notificationEveryone);
        notificationList.add(notificationIan);*/




        mockHttpSession.setAttribute("acm_ip_address", "ipAddress");
        expect(mockNotificationDao.listNotifications(user)).andReturn(notificationList);
        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("userName").atLeastOnce();

        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/v1/plugin/notification/{user}", user)
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .session(mockHttpSession)
                        .principal(mockAuthentication))
                .andReturn();

        verifyAll();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE));

        String returned = result.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        List<Notification> fromReturnedNotificationList = mapper.readValue(
                returned,
                mapper.getTypeFactory().constructParametricType(List.class, Notification.class));

        assertNotNull(fromReturnedNotificationList);
        assertEquals(fromReturnedNotificationList.size(),1);
        assertEquals(fromReturnedNotificationList.get(0).getUser(), user);
        //assertEquals(fromReturnedNotificationList.get(1).getUser(), user);


        log.info("notification size : ", fromReturnedNotificationList.size());
    }

    @Test
    public void listNotification_exception() throws Exception
    {
        String user = "ann-acm";

        Notification notification = new Notification();

        notification.setId(700L);
        notification.setCreator("testCreator");
        notification.setCreated(new Date());
        notification.setUser(user);
        notification.setStatus("status");
        notification.setAction("action");
        notification.setModifier("modifier");
        notification.setModified(new Date());
        notification.setData("data");
        notification.setAuto("auto");
        notification.setNote("note");

        List<Notification> notificationList = new ArrayList<>();
        notificationList.add(notification);


        mockHttpSession.setAttribute("acm_ip_address", "ipAddress");

        expect(mockNotificationDao.listNotifications(user)).andThrow(new QueryTimeoutException("test exception"));
        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("userName").atLeastOnce();

        replayAll();

        mockMvc.perform(
                get("/api/v1/plugin/notification/{user}",user)
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .session(mockHttpSession)
                        .principal(mockAuthentication))
                .andReturn();

        verifyAll();
    }
}
