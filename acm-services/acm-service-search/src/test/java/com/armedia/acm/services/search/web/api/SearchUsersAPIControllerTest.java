package com.armedia.acm.services.search.web.api;

/*-
 * #%L
 * ACM Service: Search
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.armedia.acm.services.search.model.solr.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.easymock.EasyMockSupport;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

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

    private Logger log = LogManager.getLogger(getClass());

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

    @Test
    public void search_userId() throws Exception
    {

        String jsonResponse = "{\"response\": {\"start\": 0, \"rows\": 10, \"numFound\": 0} }";

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user").atLeastOnce();

        expect(mockExecuteSolrQuery.getResultsByPredefinedQuery(mockAuthentication,
                SolrCore.ADVANCED_SEARCH,
                "object_type_s:USER AND status_lcs:VALID AND (first_name_lcs:keyword OR last_name_lcs:keyword)  AND -object_id_s:task-owner AND object_id_s:test-user",
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
                get("/api/v1/plugin/search/usersSearch?start=0&n=10&sortDirection=DESC&searchKeyword=keyword&exclude=task-owner&userId=test-user")
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
