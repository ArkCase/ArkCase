package com.armedia.acm.plugins.objectlockplugin.web.api;

import static junit.framework.Assert.assertEquals;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.armedia.acm.service.objectlock.model.AcmObjectLock;
import com.armedia.acm.service.objectlock.service.AcmObjectLockService;
import com.armedia.acm.service.objectlock.service.AcmObjectLockingManager;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import java.util.Date;

/**
 * Created by nebojsha on 28.10.2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = { "classpath*:/spring/spring-web-object-lock-api-test.xml" })
public class AcmObjectLockAPIControllerTest extends EasyMockSupport
{

    @Autowired
    WebApplicationContext wac;
    @Autowired
    MockHttpSession session;
    @Autowired
    MockHttpServletRequest request;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;
    private Authentication mockAuthentication;

    private MockMvc mockMvc;
    private AcmObjectLockService objectLockServiceMock;
    private AcmObjectLockingManager objectLockingManagerMock;

    @Autowired
    private AcmObjectLockAPIController objectLockAPIController;

    @Before
    public void setup()
    {
        objectLockServiceMock = createMock(AcmObjectLockService.class);
        objectLockingManagerMock = createMock(AcmObjectLockingManager.class);
        mockAuthentication = createMock(Authentication.class);
        objectLockAPIController.setObjectLockService(objectLockServiceMock);
        objectLockAPIController.setObjectLockingManager(objectLockingManagerMock);
        this.mockMvc = MockMvcBuilders.standaloneSetup(objectLockAPIController).setHandlerExceptionResolvers(exceptionResolver).build();
    }

    @Test
    public void testLockObject() throws Exception
    {
        final String user = "user";
        final Date expiry = new Date();
        expect(mockAuthentication.getName()).andReturn(user).anyTimes();

        Capture<Authentication> authenticationCapture = EasyMock.newCapture();
        expect(objectLockingManagerMock.acquireObjectLock(eq(1123l), eq("CASE_FILE"), eq("OBJECT_LOCK"), eq(null), eq(true),
                eq(user)))
                        .andAnswer(() -> {
                            AcmObjectLock lock = new AcmObjectLock();
                            lock.setCreator(user);
                            lock.setId(1l);
                            lock.setObjectId(1123l);
                            lock.setObjectType("CASE_FILE");
                            lock.setExpiry(expiry);
                            return lock;
                        });

        session.setAttribute("acm_ip_address", "127.0.0.1");

        replayAll();

        MvcResult result = mockMvc.perform(put("/api/v1/plugin/CASE_FILE/1123/lock").session(session).principal(mockAuthentication))
                .andExpect(status().isOk()).andReturn();

        ObjectMapper mapper = new ObjectMapper();
        AcmObjectLock objLock = mapper.readValue(result.getResponse().getContentAsString(), AcmObjectLock.class);

        assertEquals(Long.valueOf(1l), objLock.getId());
        assertEquals(Long.valueOf(1123l), objLock.getObjectId());
        assertEquals("CASE_FILE", objLock.getObjectType());
        assertEquals(user, objLock.getCreator());
        assertEquals(expiry, objLock.getExpiry());

        verifyAll();
    }

    @Test
    public void testUnlockObject() throws Exception
    {
        final String user = "user";
        expect(mockAuthentication.getName()).andReturn(user).anyTimes();

        objectLockingManagerMock.releaseObjectLock(1123l, "CASE_FILE", "OBJECT_LOCK", true, user, null);
        expectLastCall();

        session.setAttribute("acm_ip_address", "127.0.0.1");
        replayAll();

        MvcResult result = mockMvc.perform(delete("/api/v1/plugin/CASE_FILE/1123/lock").session(session).principal(mockAuthentication))
                .andExpect(status().isOk()).andReturn();

        verifyAll();
    }
}