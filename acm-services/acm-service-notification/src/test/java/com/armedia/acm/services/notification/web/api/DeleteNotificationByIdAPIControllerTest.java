//package com.armedia.acm.plugins.notification.web.api;
//
//import com.armedia.acm.plugins.notification.dao.NotificationAssociationDao;
//import com.armedia.acm.plugins.notification.dao.NotificationDao;
//
//import static org.easymock.EasyMock.*;
//import org.easymock.EasyMockSupport;
//import static org.junit.Assert.assertEquals;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.mock.web.MockHttpSession;
//import org.springframework.security.core.Authentication;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = {
//    "classpath:/spring/spring-web-acm-web.xml",
//    "classpath:/spring/spring-library-notification-plugin-test.xml"
//})
//
//public class DeleteNotificationByIdAPIControllerTest extends EasyMockSupport
//{
//
//    private MockMvc mockMvc;
//    private MockHttpSession mockHttpSession;
//
//    private DeleteNotificationByNotificationIdAPIController unit;
//
//    private NotificationDao mockNotificationDao;
//    private NotificationAssociationDao mockNotificationAssociationDao;
//    private Authentication mockAuthentication;
//
//    @Autowired
//    private ExceptionHandlerExceptionResolver exceptionResolver;
//
//    private Logger log = LoggerFactory.getLogger(getClass());
//
//    @Before
//    public void setUp() throws Exception {
//        mockNotificationDao = createMock(NotificationDao.class);
//        mockHttpSession = new MockHttpSession();
//        mockAuthentication = createMock(Authentication.class);
//
//        unit = new DeleteNotificationByNotificationIdAPIController();
//
//        unit.setNotificationDao(mockNotificationDao);
//        unit.setNotificationAssociationDao(mockNotificationAssociationDao);
//        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(exceptionResolver).build();
//    }
//
//    @Test
//    public void deleteNotificationById() throws Exception
//    {
//          Long notificationId =958L;
//
//        mockNotificationDao.deleteNotificationById(notificationId);
//
//        // MVC test classes must call getName() somehow
//        expect(mockAuthentication.getName()).andReturn("user");
//
//        replayAll();
//
//        MvcResult result = mockMvc.perform(
//               delete("/api/v1/plugin/notification/delete/{notificationId}", notificationId)
//                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
//                        .session(mockHttpSession)
//                        .principal(mockAuthentication))
//                .andReturn();
//
//        verifyAll();
//        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
//
//    }
//
//    @Test
//    public void deleteNotificationById_notFound() throws Exception {
//
//          Long notificationId =958L;
//
//        mockNotificationDao.deleteNotificationById(notificationId);
//
//        // MVC test classes must call getName() somehow
//        expect(mockAuthentication.getName()).andReturn("user");
//
//        replayAll();
//
//        mockMvc.perform(
//                delete("/api/v1/plugin/notification/delete/{notificationId}", notificationId)
//                .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
//                .principal(mockAuthentication));
//
//        verifyAll();
//    }
//
//}
