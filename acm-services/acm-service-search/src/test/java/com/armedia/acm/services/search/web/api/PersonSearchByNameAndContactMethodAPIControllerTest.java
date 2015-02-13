package com.armedia.acm.services.search.web.api;

import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mule.api.DefaultMuleException;
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

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    private ExecuteSolrQuery mockExecuteSolrQuery;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    private PersonSearchByNameAndContactMethodAPIController unit;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        unit = new PersonSearchByNameAndContactMethodAPIController();

        mockExecuteSolrQuery = createMock(ExecuteSolrQuery.class);

        unit.setExecuteSolrQuery(mockExecuteSolrQuery);

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

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user").atLeastOnce();

        expect(mockExecuteSolrQuery.getResultsByPredefinedQuery(mockAuthentication, SolrCore.ADVANCED_SEARCH, query,
                0, 10, sort)).andReturn(solrResponse);

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

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user").atLeastOnce();

        expect(mockExecuteSolrQuery.getResultsByPredefinedQuery(mockAuthentication, SolrCore.ADVANCED_SEARCH, query, 0, 10, sort)).
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
