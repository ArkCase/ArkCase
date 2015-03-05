package com.armedia.acm.services.search.web.api;

import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import org.easymock.EasyMockSupport;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-web-acm-web.xml",
        "classpath:/spring/spring-library-search-web-api-test.xml"
})
public class SearchUsersAPIControllerTest extends EasyMockSupport
{
    private MockMvc mockMvc;
    private Authentication mockAuthentication;
    private ExecuteSolrQuery mockExecuteSolrQuery;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    private SearchUsersAPIController unit;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        unit = new SearchUsersAPIController();

        mockExecuteSolrQuery = createMock(ExecuteSolrQuery.class);

        unit.setExecuteSolrQuery(mockExecuteSolrQuery);

        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(exceptionResolver).build();

        mockAuthentication = createMock(Authentication.class);
    }

    @Test
    public void search_noParams() throws Exception
    {

        String jsonResponse = "{\"response\": {\"start\": 0, \"rows\": 10, \"numFound\": 0} }";

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user").atLeastOnce();

        expect(mockExecuteSolrQuery.getResultsByPredefinedQuery(mockAuthentication,
                SolrCore.ADVANCED_SEARCH,
                "object_type_s:USER AND status_lcs:VALID",
                0,
                10,
                "first_name_lcs ASC, last_name_lcs ASC")).andReturn(jsonResponse);

        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/v1/plugin/search/usersSearch?start=0&n=10&sortDirection=ASC&searchKeyword=&exclude=")
                        .principal(mockAuthentication))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        verifyAll();

        String jsonString = result.getResponse().getContentAsString();

        log.debug("Got JSON: " + jsonString);

        JSONObject response = new JSONObject(jsonString);

        assertFalse(response.getJSONObject("response").has("owner"));




    }

    @Test
    public void search_keywordNoExclude() throws Exception
    {

        String jsonResponse = "{\"response\": {\"start\": 0, \"rows\": 10, \"numFound\": 0} }";

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user").atLeastOnce();

        expect(mockExecuteSolrQuery.getResultsByPredefinedQuery(mockAuthentication,
                SolrCore.ADVANCED_SEARCH,
                "object_type_s:USER AND status_lcs:VALID AND (first_name_lcs:keyword OR last_name_lcs:keyword) ",
                0,
                10,
                "first_name_lcs ASC, last_name_lcs ASC")).andReturn(jsonResponse);

        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/v1/plugin/search/usersSearch?start=0&n=10&sortDirection=ASC&searchKeyword=keyword&exclude=")
                        .principal(mockAuthentication))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        verifyAll();

        String jsonString = result.getResponse().getContentAsString();

        log.debug("Got JSON: " + jsonString);

        JSONObject response = new JSONObject(jsonString);

        assertFalse(response.getJSONObject("response").has("owner"));


    }

    @Test
    public void search_exclude_noKeyword() throws Exception
    {

        String jsonResponse = "{\"response\": {\"start\": 0, \"rows\": 10, \"numFound\": 0} }";

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user").atLeastOnce();

        expect(mockExecuteSolrQuery.getResultsByPredefinedQuery(mockAuthentication,
                SolrCore.ADVANCED_SEARCH,
                "object_type_s:USER AND status_lcs:VALID AND -object_id_s:task-owner",
                0,
                10,
                "first_name_lcs DESC, last_name_lcs DESC")).andReturn(jsonResponse);
        expect(mockExecuteSolrQuery.getResultsByPredefinedQuery(mockAuthentication,
                SolrCore.ADVANCED_SEARCH,
                "object_type_s:USER AND object_id_s:task-owner AND status_lcs:VALID",
                0,
                1,
                "first_name_lcs ASC")).andReturn(jsonResponse);

        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/v1/plugin/search/usersSearch?start=0&n=10&sortDirection=DESC&searchKeyword=&exclude=task-owner")
                        .principal(mockAuthentication))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        verifyAll();

        String jsonString = result.getResponse().getContentAsString();

        log.debug("Got JSON: " + jsonString);

        JSONObject response = new JSONObject(jsonString);

        assertTrue(response.getJSONObject("response").has("owner"));





    }

    @Test
    public void search_exclude_keyword() throws Exception
    {

        String jsonResponse = "{\"response\": {\"start\": 0, \"rows\": 10, \"numFound\": 0} }";

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user").atLeastOnce();

        expect(mockExecuteSolrQuery.getResultsByPredefinedQuery(mockAuthentication,
                SolrCore.ADVANCED_SEARCH,
                "object_type_s:USER AND status_lcs:VALID AND (first_name_lcs:keyword OR last_name_lcs:keyword)  AND -object_id_s:task-owner",
                0,
                10,
                "first_name_lcs DESC, last_name_lcs DESC")).andReturn(jsonResponse);
        expect(mockExecuteSolrQuery.getResultsByPredefinedQuery(mockAuthentication,
                SolrCore.ADVANCED_SEARCH,
                "object_type_s:USER AND object_id_s:task-owner AND status_lcs:VALID",
                0,
                1,
                "first_name_lcs ASC")).andReturn(jsonResponse);

        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/v1/plugin/search/usersSearch?start=0&n=10&sortDirection=DESC&searchKeyword=keyword&exclude=task-owner")
                        .principal(mockAuthentication))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        verifyAll();

        String jsonString = result.getResponse().getContentAsString();

        log.debug("Got JSON: " + jsonString);

        JSONObject response = new JSONObject(jsonString);

        assertTrue(response.getJSONObject("response").has("owner"));


    }
}
