package com.armedia.acm.services.search.web.api;

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

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-web-acm-web.xml",
        "classpath:/spring/spring-library-search-web-api-test.xml"
})
public class PersonSearchByNameAndContactMethodAPIControllerTest extends EasyMockSupport
{
    private MockMvc mockMvc;
    private Authentication mockAuthentication;
    private MuleClient mockMuleClient;
    private MuleMessage mockMuleMessage;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    private PersonSearchByNameAndContactMethodAPIController unit;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        unit = new PersonSearchByNameAndContactMethodAPIController();

        mockMuleClient = createMock(MuleClient.class);
        mockMuleMessage = createMock(MuleMessage.class);

        unit.setMuleClient(mockMuleClient);

        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(exceptionResolver).build();

        mockAuthentication = createMock(Authentication.class);
    }

    @Test
    public void person() throws Exception
    {

        String name = "test name";
        String contactMethod = "contact method";
        String solrResponse = "{ \"solrResponse\": \"this is a test response.\" }";

        final String encodedContactMethodJoin = URLEncoder.encode("{!join from=id to=contact_method_ss}");
        String query = "object_type_s:PERSON AND name:" + URLEncoder.encode(name) + " AND " +
                encodedContactMethodJoin + "value_parseable:" + URLEncoder.encode(contactMethod);
        String sort = "last_name_lcs ASC, first_name_lcs ASC";

        query = query.replaceAll(" ", "+");
        sort = sort.replaceAll(" ", "+");

        Map<String, Object> headers = new HashMap<>();
        headers.put("query", query);
        headers.put("sort", sort);
        headers.put("firstRow", 0);
        headers.put("maxRows", 10);

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user").atLeastOnce();

        expect(mockMuleClient.send("vm://advancedSearchQuery.in", "", headers)).andReturn(mockMuleMessage);
        expect(mockMuleMessage.getPayload()).andReturn(solrResponse).atLeastOnce();

        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/v1/plugin/search/personSearch?name=" + name + "&cm=" + contactMethod)
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

        String name = "test name";
        String contactMethod = "contact method";

        final String encodedContactMethodJoin = URLEncoder.encode("{!join from=id to=contact_method_ss}");
        String query = "object_type_s:PERSON AND name:" + URLEncoder.encode(name) + " AND " +
                encodedContactMethodJoin + "value_parseable:" + URLEncoder.encode(contactMethod);
        String sort = "last_name_lcs ASC, first_name_lcs ASC";
        query = query.replaceAll(" ", "+");
        sort = sort.replaceAll(" ", "+");

        Map<String, Object> headers = new HashMap<>();
        headers.put("query", query);
        headers.put("sort", sort);
        headers.put("firstRow", 0);
        headers.put("maxRows", 10);

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user").atLeastOnce();

        expect(mockMuleClient.send("vm://advancedSearchQuery.in", "", headers)).
                andThrow(new DefaultMuleException("test Exception"));

        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/v1/plugin/search/personSearch?name=" + name + "&cm=" + contactMethod)
                        .principal(mockAuthentication))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN))
                .andReturn();

        verifyAll();

        String response = result.getResponse().getContentAsString();

        log.debug("Got error: " + response);

    }

}
