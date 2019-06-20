package com.armedia.acm.services.notification.web.api;

/*-
 * #%L
 * ACM Service: Notification
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

import com.armedia.acm.services.notification.dao.NotificationDao;
import com.armedia.acm.services.notification.model.Notification;
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

import javax.persistence.QueryTimeoutException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    private Logger log = LogManager.getLogger(getClass());

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
        /*
         * Notification notificationIan = new Notification();
         * Notification notificationEveryone = new Notification();
         */

        notificationAnn.setId(700L);
        notificationAnn.setCreator("testCreator");
        notificationAnn.setCreated(new Date());
        notificationAnn.setUser(user);
        notificationAnn.setStatus("status");
        notificationAnn.setAction("action");
        notificationAnn.setModifier("modifier");
        notificationAnn.setModified(new Date());
        notificationAnn.setData("data");
        notificationAnn.setType("type");
        notificationAnn.setNote("note");

        /*
         * notificationEveryone.setId(700L);
         * notificationEveryone.setCreator("testCreator");
         * notificationEveryone.setCreated(new Date());
         * notificationEveryone.setUser("EVERYONE");
         * notificationEveryone.setStatus("status");
         * notificationEveryone.setAction("action");
         * notificationEveryone.setComment("comment");
         * notificationEveryone.setModifier("modifier");
         * notificationEveryone.setModified(new Date());
         * notificationEveryone.setData("data");
         * notificationEveryone.setAuto("auto");
         * notificationEveryone.setNote("note");
         * notificationIan.setId(700L);
         * notificationIan.setCreator("testCreator");
         * notificationIan.setCreated(new Date());
         * notificationIan.setUser("ian");
         * notificationIan.setStatus("status");
         * notificationIan.setAction("action");
         * notificationIan.setComment("comment");
         * notificationIan.setModifier("modifier");
         * notificationIan.setModified(new Date());
         * notificationIan.setData("data");
         * notificationIan.setAuto("auto");
         * notificationIan.setNote("note");
         */

        List<Notification> notificationList = new ArrayList<>();
        notificationList.add(notificationAnn);
        /*
         * notificationList.add(notificationEveryone);
         * notificationList.add(notificationIan);
         */

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
        assertEquals(fromReturnedNotificationList.size(), 1);
        assertEquals(fromReturnedNotificationList.get(0).getUser(), user);
        // assertEquals(fromReturnedNotificationList.get(1).getCaseFileUser(), user);

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
        notification.setType("type");
        notification.setNote("note");

        List<Notification> notificationList = new ArrayList<>();
        notificationList.add(notification);

        mockHttpSession.setAttribute("acm_ip_address", "ipAddress");

        expect(mockNotificationDao.listNotifications(user)).andThrow(new QueryTimeoutException("test exception"));
        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("userName").atLeastOnce();

        replayAll();

        mockMvc.perform(
                get("/api/v1/plugin/notification/{user}", user)
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .session(mockHttpSession)
                        .principal(mockAuthentication))
                .andReturn();

        verifyAll();
    }
}
