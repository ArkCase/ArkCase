package com.armedia.acm.services.dataaccess.web.api;

import com.armedia.acm.services.dataaccess.model.AcmAccessControlDefault;
import com.armedia.acm.services.dataaccess.model.AcmAccessControlEvent;
import com.armedia.acm.services.dataaccess.service.DataAccessDefaultService;
import org.codehaus.jackson.map.ObjectMapper;
import org.easymock.Capture;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
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
import java.util.Date;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-web-acm-web.xml",
        "classpath:/spring/spring-library-dataaccess-test.xml"
})
public class UpdateAccessControlDefaultControllerTest extends EasyMockSupport
{
    private DataAccessDefaultService mockDataAccessDefaultService;
    private MockMvc mockMvc;
    private Authentication mockAuthentication;
    private MockHttpSession mockHttpSession;
    private ApplicationEventPublisher mockApplicationEventPublisher;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    private UpdateAccessControlDefaultController unit;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        unit = new UpdateAccessControlDefaultController();

        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(exceptionResolver).build();

        mockAuthentication = createMock(Authentication.class);
        mockDataAccessDefaultService = createMock(DataAccessDefaultService.class);
        mockHttpSession = new MockHttpSession();
        mockApplicationEventPublisher = createMock(ApplicationEventPublisher.class);

        unit.setDataAccessDefaultService(mockDataAccessDefaultService);
        unit.setApplicationEventPublisher(mockApplicationEventPublisher);
    }

    @Test
    public void postDefault() throws Exception
    {
        Long accessId = 500L;

        AcmAccessControlDefault in = new AcmAccessControlDefault();
        in.setId(accessId);
        in.setAllowDiscretionaryUpdate(true);
        in.setModifier("modifier");
        in.setAccessDecision("GRANT");
        in.setModified(new Date());
        in.setAccessLevel("accessLevel");
        in.setCreated(new Date());
        in.setCreator("creator");
        in.setObjectState("state");
        in.setObjectType("type");
        in.setAccessorType("accessor");

        ObjectMapper om = new ObjectMapper();
        String inJson = om.writeValueAsString(in);

        Capture<AcmAccessControlDefault> toSave = new Capture<>();
        Capture<AcmAccessControlEvent> event = new Capture<>();

        expect(mockAuthentication.getName()).andReturn("user").atLeastOnce();
        expect(mockDataAccessDefaultService.save(eq(in.getId()), capture(toSave), eq(mockAuthentication))).andReturn(in);
        mockApplicationEventPublisher.publishEvent(capture(event));

        replayAll();

        MvcResult result = mockMvc.perform(
                post("/api/v1/plugin/dataaccess/default/{defaultAccessId}", accessId)
                        .principal(mockAuthentication)
                        .session(mockHttpSession)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(inJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        verifyAll();

        assertTrue(event.getValue().isSucceeded());
        assertEquals(in.getId(), event.getValue().getObjectId());

        assertEquals(in.getId(), toSave.getValue().getId());

    }

    @Test
    public void postDefault_badAccessDecision() throws Exception
    {
        Long accessId = 500L;

        AcmAccessControlDefault in = new AcmAccessControlDefault();
        in.setId(accessId);
        in.setAllowDiscretionaryUpdate(true);
        in.setModifier("modifier");
        in.setAccessDecision("NO_SUCH_VALUE");
        in.setModified(new Date());
        in.setAccessLevel("accessLevel");
        in.setCreated(new Date());
        in.setCreator("creator");
        in.setObjectState("state");
        in.setObjectType("type");
        in.setAccessorType("accessor");

        ObjectMapper om = new ObjectMapper();
        String inJson = om.writeValueAsString(in);

        Capture<AcmAccessControlEvent> event = new Capture<>();

        expect(mockAuthentication.getName()).andReturn("user").atLeastOnce();
        mockApplicationEventPublisher.publishEvent(capture(event));

        replayAll();

        MvcResult result = mockMvc.perform(
                post("/api/v1/plugin/dataaccess/default/{defaultAccessId}", accessId)
                        .principal(mockAuthentication)
                        .session(mockHttpSession)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(inJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN))
                .andReturn();

        verifyAll();

        assertFalse(event.getValue().isSucceeded());
        assertEquals(in.getId(), event.getValue().getObjectId());

        log.debug("Response: " + result.getResponse().getContentAsString());

    }

    @Test
    public void postDefault_saveException() throws Exception
    {
        Long accessId = 500L;

        AcmAccessControlDefault in = new AcmAccessControlDefault();
        in.setId(accessId);
        in.setAllowDiscretionaryUpdate(true);
        in.setModifier("modifier");
        in.setAccessDecision("GRANT");
        in.setModified(new Date());
        in.setAccessLevel("accessLevel");
        in.setCreated(new Date());
        in.setCreator("creator");
        in.setObjectState("state");
        in.setObjectType("type");
        in.setAccessorType("accessor");

        ObjectMapper om = new ObjectMapper();
        String inJson = om.writeValueAsString(in);

        Capture<AcmAccessControlDefault> toSave = new Capture<>();
        Capture<AcmAccessControlEvent> event = new Capture<>();

        expect(mockAuthentication.getName()).andReturn("user").atLeastOnce();
        mockApplicationEventPublisher.publishEvent(capture(event));

        expect(mockDataAccessDefaultService.save(eq(in.getId()), capture(toSave), eq(mockAuthentication))).
                andThrow(new QueryTimeoutException());

        replayAll();

        MvcResult result = mockMvc.perform(
                post("/api/v1/plugin/dataaccess/default/{defaultAccessId}", accessId)
                        .principal(mockAuthentication)
                        .session(mockHttpSession)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(inJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN))
                .andReturn();

        verifyAll();

        assertFalse(event.getValue().isSucceeded());
        assertEquals(in.getId(), event.getValue().getObjectId());

        log.debug("Response: " + result.getResponse().getContentAsString());

    }


}
