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

import com.armedia.acm.services.search.exception.SolrException;
import com.armedia.acm.services.search.service.ChildDocumentsSearchService;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.easymock.Capture;
import org.easymock.EasyMockSupport;
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

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-web-acm-web.xml",
        "classpath:/spring/spring-library-search-web-api-test.xml"
})
public class SearchChildrenAPIControllerTest extends EasyMockSupport
{
    private MockMvc mockMvc;
    private Authentication mockAuthentication;
    private ExecuteSolrQuery mockExecuteSolrQuery;
    private ChildDocumentsSearchService mockChildDocumentsSearchService;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    private SearchChildrenAPIController unit;

    private Logger log = LogManager.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        unit = new SearchChildrenAPIController();

        mockExecuteSolrQuery = createMock(ExecuteSolrQuery.class);

        unit.setExecuteSolrQuery(mockExecuteSolrQuery);

        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(exceptionResolver).build();

        mockAuthentication = createMock(Authentication.class);

        mockChildDocumentsSearchService = createMock(ChildDocumentsSearchService.class);

        unit.setChildDocumentsSearchService(mockChildDocumentsSearchService);
    }

    @Test
    public void children() throws Exception
    {
        String parentType = "COMPLAINT";
        Long parentId = 999L;
        String childType = "TASK";
        boolean activeOnly = false;
        boolean exceptDeletedOnly = true;

        String solrResponse = "{ \"solrResponse\": \"this is a test response.\" }";

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user").atLeastOnce();

        Capture<Authentication> captureAuthentication = Capture.newInstance();
        expect(mockChildDocumentsSearchService.searchChildren(eq(parentType), eq(parentId), eq(childType),
                eq(activeOnly),
                eq(exceptDeletedOnly), eq(null), eq(""), eq(0), eq(10),
                capture(captureAuthentication))).andReturn(solrResponse);

        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/v1/plugin/search/children")
                        .param("parentId", "999")
                        .param("parentType", parentType)
                        .param("childType", childType)
                        .principal(mockAuthentication))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=ISO-8859-1"))
                .andReturn();

        verifyAll();

        String jsonString = result.getResponse().getContentAsString();

        log.debug("Got JSON: " + jsonString);

        assertEquals(solrResponse, jsonString);

    }

    @Test
    public void childrenAdvanced() throws Exception
    {
        String parentType = "COMPLAINT";
        Long parentId = 999L;
        String childType = "TASK";
        boolean activeOnly = false;
        boolean exceptDeletedOnly = true;

        String solrResponse = "{ \"solrResponse\": \"this is a test response.\" }";

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user").atLeastOnce();

        Capture<Authentication> captureAuthentication = Capture.newInstance();
        expect(mockChildDocumentsSearchService.searchChildren(eq(parentType), eq(parentId), eq(childType),
                eq(activeOnly),
                eq(exceptDeletedOnly), eq(null), eq(""), eq(0), eq(10),
                capture(captureAuthentication))).andReturn(solrResponse);

        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/v1/plugin/search/children/advanced")
                        .param("parentId", "999")
                        .param("parentType", parentType)
                        .param("childType", childType)

                        .principal(mockAuthentication))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=ISO-8859-1"))
                .andReturn();

        verifyAll();

        String jsonString = result.getResponse().getContentAsString();

        log.debug("Got JSON: " + jsonString);

        assertEquals(solrResponse, jsonString);
    }

    @Test
    public void children_exception() throws Exception
    {

        String parentType = "COMPLAINT";
        Long parentId = 999L;
        String childType = "TASK";
        boolean activeOnly = false;
        boolean exceptDeletedOnly = true;

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user").atLeastOnce();

        Capture<Authentication> captureAuthentication = Capture.newInstance();
        expect(mockChildDocumentsSearchService.searchChildren(eq(parentType), eq(parentId), eq(childType),
                eq(activeOnly),
                eq(exceptDeletedOnly), eq(null), eq(""), eq(0), eq(10),
                capture(captureAuthentication))).andThrow(new SolrException("test Exception"));

        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/v1/plugin/search/children")
                        .param("parentId", "999")
                        .param("parentType", parentType)
                        .param("childType", childType)
                        .principal(mockAuthentication))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN))
                .andReturn();

        verifyAll();

        String response = result.getResponse().getContentAsString();

        log.debug("Got error: " + response);

    }

}
