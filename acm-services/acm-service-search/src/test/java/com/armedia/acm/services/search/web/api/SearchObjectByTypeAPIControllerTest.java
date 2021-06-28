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

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.armedia.acm.services.search.exception.SolrException;
import com.armedia.acm.services.search.model.ApplicationSearchEvent;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrCore;
import com.armedia.acm.services.search.model.solr.SolrResponse;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.search.service.SearchEventPublisher;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-web-acm-web.xml",
        "classpath:/spring/spring-library-search-web-api-test.xml"
})
public class SearchObjectByTypeAPIControllerTest extends EasyMockSupport
{
    private MockMvc mockMvc;
    private Authentication mockAuthentication;
    private MockHttpSession mockHttpSession;

    private ExecuteSolrQuery mockExecuteSolrQuery;
    private SearchEventPublisher mockSearchEventPublisher;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    private SearchObjectByTypeAPIController unit;

    private Logger log = LogManager.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        unit = new SearchObjectByTypeAPIController();

        mockSearchEventPublisher = createMock(SearchEventPublisher.class);
        mockExecuteSolrQuery = createMock(ExecuteSolrQuery.class);

        unit.setExecuteSolrQuery(mockExecuteSolrQuery);

        unit.setSearchEventPublisher(mockSearchEventPublisher);

        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(exceptionResolver).build();

        mockAuthentication = createMock(Authentication.class);
        mockHttpSession = new MockHttpSession();
    }

    @Test
    public void jsonPayload() throws Exception
    {
        // there are docs
        String jsonPayload = "{\"responseHeader\":{\"status\":0,\"QTime\":3,\"params\":{\"sort\":\"\",\"indent\":\"true\",\"start\":\"0\",\"q\":\"object_type_s:Complaint\",\"wt\":\"json\",\"rows\":\"10\"}},\"response\":{\"numFound\":5,\"start\":0,\"docs\":[{\"id\":\"142-Complaint\",\"status_lcs\":\"DRAFT\",\"author\":\"tester\",\"creator_lcs\":\"tester\",\"modifier_lcs\":\"testModifier\",\"last_modified\":\"2014-08-15T17:13:55Z\",\"create_date_tdt\":\"2014-08-15T17:13:55Z\",\"title_t\":\"testTitle\",\"name\":\"20140815_142\",\"object_id_s\":\"142\",\"owner_lcs\":\"tester\",\"object_type_s\":\"Complaint\",\"_version_\":1477062417430085632},{\"id\":\"159-Complaint\",\"status_lcs\":\"DRAFT\",\"author\":\"tester\",\"creator_lcs\":\"tester\",\"modifier_lcs\":\"testModifier\",\"last_modified\":\"2014-08-18T11:51:09Z\",\"create_date_tdt\":\"2014-08-18T11:51:09Z\",\"name\":\"20140818_159\",\"object_id_s\":\"159\",\"owner_lcs\":\"tester\",\"object_type_s\":\"Complaint\",\"_version_\":1477062417682792448},{\"status_lcs\":\"DRAFT\",\"create_date_tdt\":\"2014-08-22T09:27:41Z\",\"title_t\":\"First Complaint\",\"object_id_s\":\"130\",\"owner_lcs\":\"ann-acm\",\"deny_acl_ss\":[\"TEST-DENY-ACL\"],\"object_type_s\":\"Complaint\",\"allow_acl_ss\":[\"TEST-ALLOW-ACL\"],\"id\":\"130-Complaint\",\"modifier_lcs\":\"ann-acm\",\"author\":\"ann-acm\",\"creator_lcs\":\"ann-acm\",\"last_modified\":\"2014-08-22T09:27:41Z\",\"name\":\"20140822_130\",\"_version_\":1478712820530937856},{\"status_lcs\":\"DRAFT\",\"create_date_tdt\":\"2014-09-15T09:16:04Z\",\"title_t\":\"Monday Sept 15\",\"object_id_s\":\"270\",\"owner_lcs\":\"ann-acm\",\"deny_acl_ss\":[\"TEST-DENY-ACL\"],\"object_type_s\":\"Complaint\",\"allow_acl_ss\":[\"TEST-ALLOW-ACL\"],\"id\":\"270-Complaint\",\"modifier_lcs\":\"ann-acm\",\"author\":\"ann-acm\",\"creator_lcs\":\"ann-acm\",\"last_modified\":\"2014-09-15T09:16:04Z\",\"name\":\"20140905_001\",\"_version_\":1479317404993454080},{\"status_lcs\":\"DRAFT\",\"create_date_tdt\":\"2014-09-15T09:33:01Z\",\"title_t\":\"Monday Sept 15 2\",\"object_id_s\":\"275\",\"owner_lcs\":\"ann-acm\",\"deny_acl_ss\":[\"TEST-DENY-ACL\"],\"object_type_s\":\"Complaint\",\"allow_acl_ss\":[\"TEST-ALLOW-ACL\"],\"id\":\"275-Complaint\",\"modifier_lcs\":\"ann-acm\",\"author\":\"ann-acm\",\"creator_lcs\":\"ann-acm\",\"last_modified\":\"2014-09-15T09:33:01Z\",\"name\":\"20140905_002\",\"_version_\":1479318645547991040}]}}";
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        SolrResponse solrResponse = gson.fromJson(jsonPayload, SolrResponse.class);

        List<SolrAdvancedSearchDocument> solrDocs = solrResponse.getResponse().getDocs();

        assertTrue(solrDocs.size() > 0);

        // no docs response
        jsonPayload = "{\"responseHeader\":{\"status\":400,\"QTime\":5,\"params\":{\"sort\":\"\",\"indent\":\"true\",\"start\":\"0\",\"q\":\"object_type_:NOTYPE\",\"wt\":\"json\",\"rows\":\"10\"}},\"error\":{\"msg\":\"undefined field object_type_\",\"code\":400}}";
        solrResponse = gson.fromJson(jsonPayload, SolrResponse.class);
        assertNull(solrResponse.getResponse());
    }

    @Test
    public void searchObjectByType_oneDocumentFound() throws Exception
    {
        String objectType = "COMPLAINT";
        int firstRow = 0;
        int maxRows = 10;
        String sort = "";
        String params = "";

        String query = "object_type_s:" + objectType
                + " AND -status_lcs:COMPLETE AND -status_lcs:DELETE AND -status_lcs:CLOSED AND -status_lcs:CLOSE" +
                " AND -status_lcs:INVALID AND -status_lcs:DELETE AND -status_lcs:INACTIVE";

        String solrResponse = "{\"responseHeader\":{\"status\":0,\"QTime\":3,\"params\":{\"sort\":\"\",\"indent\":\"true\",\"start\":\"0\",\"q\":\"object_type_s:Complaint\",\"wt\":\"json\",\"rows\":\"10\"}},\"response\":{\"numFound\":5,\"start\":0,\"docs\":[{\"id\":\"142-Complaint\",\"status_lcs\":\"DRAFT\",\"author\":\"tester\",\"creator_lcs\":\"tester\",\"modifier_lcs\":\"testModifier\",\"last_modified\":\"2014-08-15T17:13:55Z\",\"create_date_tdt\":\"2014-08-15T17:13:55Z\",\"title_t\":\"testTitle\",\"name\":\"20140815_142\",\"object_id_s\":\"142\",\"owner_lcs\":\"tester\",\"object_type_s\":\"Complaint\",\"_version_\":1477062417430085632}]}}";

        Capture<ApplicationSearchEvent> capturedEvent = EasyMock.newCapture();

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user").atLeastOnce();

        expect(mockExecuteSolrQuery.getResultsByPredefinedQuery(mockAuthentication, SolrCore.ADVANCED_SEARCH, query,
                firstRow, maxRows, sort, params)).andReturn(solrResponse);

        mockSearchEventPublisher.publishSearchEvent(capture(capturedEvent));

        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/v1/plugin/search/{objectType}", objectType)
                        .session(mockHttpSession)
                        .principal(mockAuthentication))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn();

        verifyAll();

        String jsonString = result.getResponse().getContentAsString();

        log.debug("Got JSON [{}]", jsonString);

        assertEquals(solrResponse, jsonString);

    }

    @Test
    public void searchObjectByType_exception() throws Exception
    {
        String objectType = "COMPLAINT";
        int firstRow = 0;
        int maxRows = 10;
        String sort = "";
        String params = "";

        String query = "object_type_s:" + objectType
                + " AND -status_lcs:COMPLETE AND -status_lcs:DELETE AND -status_lcs:CLOSED AND -status_lcs:CLOSE" +
                " AND -status_lcs:INVALID AND -status_lcs:DELETE AND -status_lcs:INACTIVE";

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user").atLeastOnce();
        expect(mockExecuteSolrQuery.getResultsByPredefinedQuery(mockAuthentication, SolrCore.ADVANCED_SEARCH, query,
                firstRow, maxRows, sort, params)).andThrow(new SolrException("Test Exception"));

        replayAll();

        mockMvc.perform(
                get("/api/v1/plugin/search/{objectType}", objectType)
                        .session(mockHttpSession)
                        .principal(mockAuthentication))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN))
                .andReturn();

        verifyAll();
    }

}
