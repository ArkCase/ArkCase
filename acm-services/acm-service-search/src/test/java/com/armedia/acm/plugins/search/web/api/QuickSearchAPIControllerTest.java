package com.armedia.acm.plugins.search.web.api;

import com.armedia.acm.services.search.web.api.QuickSearchAPIController;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mule.api.DefaultMuleException;
import org.mule.api.MuleMessage;
import org.mule.api.client.MuleClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import java.util.HashMap;
import java.util.Map;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-web-acm-web.xml",
        "classpath:/spring/spring-library-search-web-api-test.xml"
})
public class QuickSearchAPIControllerTest extends EasyMockSupport
{
    private MockMvc mockMvc;
    private Authentication mockAuthentication;
    private MuleClient mockMuleClient;
    private MuleMessage mockMuleMessage;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    private QuickSearchAPIController unit;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        unit = new QuickSearchAPIController();

        mockMuleClient = createMock(MuleClient.class);
        mockMuleMessage = createMock(MuleMessage.class);

        unit.setMuleClient(mockMuleClient);

        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(exceptionResolver).build();

        mockAuthentication = createMock(Authentication.class);
    }

    @Test
    public void quickSearch() throws Exception
    {

        String query = "query";
        String solrResponse = "{ \"solrResponse\": \"this is a test response.\" }";

        Map<String, Object> headers = new HashMap<>();
        headers.put("query", query);
        headers.put("sort", "");
        headers.put("firstRow", 0);
        headers.put("maxRows", 10);

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user").atLeastOnce();

        expect(mockMuleClient.send("vm://quickSearchQuery.in", "", headers)).andReturn(mockMuleMessage);
        expect(mockMuleMessage.getPayload()).andReturn(solrResponse).atLeastOnce();

        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/v1/plugin/search/quickSearch?q=" + query)
                        .principal(mockAuthentication))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        verifyAll();

        String jsonString = result.getResponse().getContentAsString();

        log.debug("Got JSON: " + jsonString);

        assertEquals(solrResponse, jsonString);


    }

    @Test
    public void quickSearch_exception() throws Exception
    {

        String query = "query";

        Map<String, Object> headers = new HashMap<>();
        headers.put("query", query);
        headers.put("sort", "");
        headers.put("firstRow", 0);
        headers.put("maxRows", 10);

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user").atLeastOnce();

        expect(mockMuleClient.send("vm://quickSearchQuery.in", "", headers)).
                andThrow(new DefaultMuleException("test Exception"));

        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/v1/plugin/search/quickSearch?q=" + query)
                        .principal(mockAuthentication))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN))
                .andReturn();

        verifyAll();

        String response = result.getResponse().getContentAsString();

        log.debug("Got error: " + response);

    }

}
