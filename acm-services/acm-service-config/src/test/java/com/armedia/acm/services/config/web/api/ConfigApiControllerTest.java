package com.armedia.acm.services.config.web.api;


import com.armedia.acm.services.config.model.AcmConfig;
import com.armedia.acm.services.config.model.AppConfig;
import com.armedia.acm.services.config.model.PropertyConfig;
import com.fasterxml.jackson.databind.JsonNode;
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
import java.util.List;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-web-acm-web.xml",
        "classpath:/spring/spring-library-config-plugin-test.xml"
})
public class ConfigApiControllerTest extends EasyMockSupport
{
    private List<AcmConfig> mockConfigList;
    private AppConfig mockConfig1;
    private PropertyConfig mockConfig2;
    private PropertyConfig mockConfig3;
    private MockMvc mockMvc;
    private Authentication mockAuthentication;
    private MockHttpSession mockHttpSession;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    private ConfigApiController unit;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        mockConfig1 = createMock(AppConfig. class);
        mockConfig2 = createMock(PropertyConfig. class);
        mockConfig3 = createMock(PropertyConfig. class);

        mockConfigList = new ArrayList<AcmConfig>();
        mockConfigList.add(mockConfig1);
        mockConfigList.add(mockConfig2);
        mockConfigList.add(mockConfig3);

        mockHttpSession = new MockHttpSession();
        mockAuthentication = createMock(Authentication.class);

        unit = new ConfigApiController();
        unit.setConfigList(mockConfigList);

        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(exceptionResolver).build();
    }

    @Test
    public void getConfig1() throws Exception
    {
        mockHttpSession.setAttribute("acm_ip_address", "ipAddress");

        expect(mockConfig1.getConfigName()).andReturn("config1");
        expect(mockConfig1.getConfigAsJson()).andReturn("{\"some1\":\"value1\"}");

        expect(mockAuthentication.getName()).andReturn("userName").atLeastOnce();

        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/v1/service/config/{name}", "config1")
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .session(mockHttpSession)
                        .principal(mockAuthentication))
                .andReturn();

        verifyAll();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE));

        String returned = result.getResponse().getContentAsString();
        assertNotNull(returned);
        log.info("getConfig1(), returned=", returned);

        ObjectMapper om = new ObjectMapper();
        JsonNode actualObj = om.readTree(returned);
        JsonNode someNode = actualObj.path("some1");
        String someValue = someNode.textValue();
        assertEquals(someValue, "value1");
    }

    @Test
    public void getConfig2() throws Exception
    {
        mockHttpSession.setAttribute("acm_ip_address", "ipAddress");

        expect(mockConfig1.getConfigName()).andReturn("config1");
        expect(mockConfig2.getConfigName()).andReturn("config2");
        expect(mockConfig2.getConfigAsJson()).andReturn("{\"some2\":\"value2\"}");

        expect(mockAuthentication.getName()).andReturn("userName").atLeastOnce();

        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/v1/service/config/{name}", "config2")
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .session(mockHttpSession)
                        .principal(mockAuthentication))
                .andReturn();

        verifyAll();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE));

        String returned = result.getResponse().getContentAsString();
        assertNotNull(returned);
        log.info("getConfig2(), returned=", returned);

        ObjectMapper om = new ObjectMapper();
        JsonNode actualObj = om.readTree(returned);
        JsonNode someNode = actualObj.path("some2");
        String someValue = someNode.textValue();
        assertEquals(someValue, "value2");
    }

    @Test
    public void getConfig3() throws Exception
    {
        mockHttpSession.setAttribute("acm_ip_address", "ipAddress");

        expect(mockConfig1.getConfigName()).andReturn("config1");
        expect(mockConfig2.getConfigName()).andReturn("config2");
        expect(mockConfig3.getConfigName()).andReturn("config3");
        expect(mockConfig3.getConfigAsJson()).andReturn("{\"some3\":\"value3\"}");

        expect(mockAuthentication.getName()).andReturn("userName").atLeastOnce();

        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/v1/service/config/{name}", "config3")
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .session(mockHttpSession)
                        .principal(mockAuthentication))
                .andReturn();

        verifyAll();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE));

        String returned = result.getResponse().getContentAsString();
        assertNotNull(returned);
        log.info("getConfig3(), returned=", returned);

        ObjectMapper om = new ObjectMapper();
        JsonNode actualObj = om.readTree(returned);
        JsonNode someNode = actualObj.path("some3");
        String someValue = someNode.textValue();
        assertEquals(someValue, "value3");
    }

    @Test
    public void getConfig4() throws Exception
    {
        mockHttpSession.setAttribute("acm_ip_address", "ipAddress");

        expect(mockConfig1.getConfigName()).andReturn("config1");
        expect(mockConfig2.getConfigName()).andReturn("config2");
        expect(mockConfig3.getConfigName()).andReturn("config3");

        expect(mockAuthentication.getName()).andReturn("userName").atLeastOnce();

        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/v1/service/config/{name}", "config_no_such")
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .session(mockHttpSession)
                        .principal(mockAuthentication))
                .andReturn();

        verifyAll();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE));

        String returned = result.getResponse().getContentAsString();
        assertEquals(returned, "{}");
        log.info("returned=", returned);
    }
}
