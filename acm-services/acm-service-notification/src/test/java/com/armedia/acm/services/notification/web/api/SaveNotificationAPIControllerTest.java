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

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.armedia.acm.services.notification.dao.NotificationDao;
import com.armedia.acm.services.notification.model.ApplicationNotificationEvent;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.service.NotificationEventPublisher;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.easymock.Capture;
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

import java.util.Date;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-web-acm-web.xml",
        "classpath:/spring/spring-library-notification-plugin-test.xml"
})
public class SaveNotificationAPIControllerTest extends EasyMockSupport
{
    private MockMvc mockMvc;
    private MockHttpSession mockHttpSession;

    private SaveNotificationAPIController unit;
    private NotificationEventPublisher mockEventPublisher;
    private Authentication mockAuthentication;

    private NotificationDao mockNotificationDao;
    private NotificationEventPublisher mockNotificationEventPublisher;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    private Logger log = LogManager.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        mockNotificationDao = createMock(NotificationDao.class);
        mockNotificationEventPublisher = createMock(NotificationEventPublisher.class);
        mockHttpSession = new MockHttpSession();
        mockAuthentication = createMock(Authentication.class);

        unit = new SaveNotificationAPIController();

        unit.setNotificationDao(mockNotificationDao);
        unit.setNotificationEventPublisher(mockNotificationEventPublisher);

        mockMvc = MockMvcBuilders.standaloneSetup(unit)
                .setHandlerExceptionResolvers(exceptionResolver).build();
    }

    @Test
    public void addNotification() throws Exception
    {
        String user = "ann-acm";

        Notification incomingNotification = new Notification();

        incomingNotification.setId(700L);
        incomingNotification.setCreator("testCreator");
        incomingNotification.setCreated(new Date());
        incomingNotification.setUser(user);
        incomingNotification.setStatus("status");
        incomingNotification.setAction("action");
        incomingNotification.setModifier("modifier");
        incomingNotification.setModified(new Date());
        incomingNotification.setData("data");
        incomingNotification.setType("type");
        incomingNotification.setNote("note");

        Capture<Notification> notificationToSave = new Capture<>();
        Capture<ApplicationNotificationEvent> capturedEvent = new Capture<>();

        expect(mockNotificationDao.save(capture(notificationToSave))).andReturn(incomingNotification);
        mockNotificationEventPublisher.publishNotificationEvent(capture(capturedEvent));
        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("userName").atLeastOnce();

        replayAll();

        ObjectMapper objectMapper = new ObjectMapper();
        String in = objectMapper.writeValueAsString(incomingNotification);

        log.debug("Input JSON: " + in);

        MvcResult result = mockMvc.perform(
                post("/api/latest/plugin/notification")
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(mockAuthentication)
                        .content(in))
                .andReturn();

        verifyAll();

        assertEquals(incomingNotification.getId(), notificationToSave.getValue().getId());
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());

    }

    @Test
    public void addNotification_exception() throws Exception
    {

        String user = "ann-acm";

        Notification incomingNotification = new Notification();

        incomingNotification.setId(700L);
        incomingNotification.setCreator("testCreator");
        incomingNotification.setCreated(new Date());
        incomingNotification.setUser(user);
        incomingNotification.setStatus("status");
        incomingNotification.setAction("action");
        incomingNotification.setModifier("modifier");
        incomingNotification.setModified(new Date());
        incomingNotification.setData("data");
        incomingNotification.setType("type");
        incomingNotification.setNote("note");

        Capture<Notification> notificationToSave = new Capture<>();
        Capture<ApplicationNotificationEvent> capturedEvent = new Capture<>();

        expect(mockNotificationDao.save(capture(notificationToSave))).andThrow(new RuntimeException("testException"));
        mockNotificationEventPublisher.publishNotificationEvent(capture(capturedEvent));
        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("userName").atLeastOnce();

        replayAll();

        ObjectMapper objectMapper = new ObjectMapper();
        String in = objectMapper.writeValueAsString(incomingNotification);

        log.debug("Input JSON: " + in);

        MvcResult result = mockMvc.perform(
                post("/api/latest/plugin/notification")
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(mockAuthentication)
                        .content(in))
                .andReturn();

        log.info("results: " + result.getResponse().getStatus());

        verifyAll();

        assertEquals(incomingNotification.getId(), notificationToSave.getValue().getId());
    }
}
