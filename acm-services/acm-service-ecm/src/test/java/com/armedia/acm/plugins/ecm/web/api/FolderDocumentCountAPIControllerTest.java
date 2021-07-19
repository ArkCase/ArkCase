package com.armedia.acm.plugins.ecm.web.api;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.armedia.acm.services.search.model.solr.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.search.service.SearchResults;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.easymock.EasyMockSupport;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

/**
 * Created by manoj.dhungana on 7/23/2015.
 */

public class FolderDocumentCountAPIControllerTest extends EasyMockSupport
{

    private MockMvc mockMvc;
    private FolderDocumentCountAPIController unit;
    private Authentication mockAuthentication;
    private MockHttpSession mockHttpSession;
    private ExecuteSolrQuery mockSolrQuery;
    private SearchResults mockSearchResults;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    private Logger log = LogManager.getLogger(getClass());

    @Before
    public void setUP() throws Exception
    {
        mockAuthentication = createMock(Authentication.class);
        mockHttpSession = new MockHttpSession();
        mockSolrQuery = createMock(ExecuteSolrQuery.class);
        mockSearchResults = createMock(SearchResults.class);

        unit = new FolderDocumentCountAPIController();
        unit.setExecuteSolrQuery(mockSolrQuery);
        unit.setSearchResults(mockSearchResults);

        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(exceptionResolver).build();
    }

    @Test
    public void listFileFolderByCategory_success() throws Exception
    {

        String parentObjectType = "CASE_FILE";
        Long parentObjectId = 234L;
        String topLevelSearchResult = "mockTopLevelSearchResult";
        String topLevelFolderResult = "mockTopLevelFolderResult";
        String subFolderSearchResult = "mockSubFolderSearchResult";
        String topLevelFacetFields = "{object_type_s:[\n" +
                "        FILE,5,\n" +
                "        FOLDER,1,\n" +
                "        ASSOCIATED_TAG,0,\n" +
                "        CASE_FILE,0,\n" +
                "        COMPLAINT,0,\n" +
                "        CONTAINER,0,\n" +
                "        COSTSHEET,0,\n" +
                "        DISPOSITION,0,\n" +
                "        GROUP,0,\n" +
                "        NOTIFICATION,0,\n" +
                "        PERSON,0,\n" +
                "        SUBSCRIPTION,0,\n" +
                "        SUBSCRIPTION_EVENT,0,\n" +
                "        TASK,0,\n" +
                "        TIMESHEET,0,\n" +
                "        USER,0]},";
        JSONObject topLevelFacets = new JSONObject(topLevelFacetFields);
        String topLevelFolderDocs = "[{\"name\": \"Folder One\", \"object_type_s\": \"FOLDER\", \"object_id_s\": \"500\"}]";
        JSONArray topLevelFolders = new JSONArray(topLevelFolderDocs);
        String subFolderFacetFields = "{object_type_s:[\n" +
                "        FILE,45,\n" +
                "        FOLDER,0,\n" +
                "        ASSOCIATED_TAG,0,\n" +
                "        CASE_FILE,0,\n" +
                "        COMPLAINT,0,\n" +
                "        CONTAINER,0,\n" +
                "        COSTSHEET,0,\n" +
                "        DISPOSITION,0,\n" +
                "        GROUP,0,\n" +
                "        NOTIFICATION,0,\n" +
                "        PERSON,0,\n" +
                "        SUBSCRIPTION,0,\n" +
                "        SUBSCRIPTION_EVENT,0,\n" +
                "        TASK,0,\n" +
                "        TIMESHEET,0,\n" +
                "        USER,0]},";
        JSONObject subFolderFacets = new JSONObject(subFolderFacetFields);

        String url = "/api/v1/service/ecm/folder/counts/" + parentObjectType + "/" + parentObjectId;
        log.info("Rest endpoint : " + url);

        expect(mockAuthentication.getName()).andReturn("userName").atLeastOnce();

        // facet query for top level folder
        expect(mockSolrQuery.getResultsByPredefinedQuery(
                mockAuthentication,
                SolrCore.ADVANCED_SEARCH,
                "{!join from=folder_id_i to=parent_folder_id_i}object_type_s:CONTAINER AND parent_object_id_i:" + parentObjectId
                        + " AND parent_type_s:" + parentObjectType,
                0,
                1,
                "object_id_s ASC",
                "facet.field=object_type_s&facet=true&fq=object_type_s:FILE OR object_type_s:FOLDER&fq=hidden_b:false&fl=name,object_type_s,object_id_s"))
                        .andReturn(topLevelSearchResult);

        expect(mockSearchResults.getFacetFields(topLevelSearchResult)).andReturn(topLevelFacets);

        // sub-folder query for top level folder
        expect(mockSolrQuery.getResultsByPredefinedQuery(
                mockAuthentication,
                SolrCore.ADVANCED_SEARCH,
                "{!join from=folder_id_i to=parent_folder_id_i}object_type_s:CONTAINER AND parent_object_id_i:" + parentObjectId
                        + " AND parent_type_s:" + parentObjectType,
                0,
                1,
                "object_id_s ASC",
                "fq=object_type_s:FOLDER&fq=hidden_b:false&fl=name,object_id_s")).andReturn(topLevelFolderResult);
        expect(mockSearchResults.getDocuments(topLevelFolderResult)).andReturn(topLevelFolders);

        // facet query for the sub folder
        expect(mockSolrQuery.getResultsByPredefinedQuery(
                mockAuthentication,
                SolrCore.ADVANCED_SEARCH,
                "parent_folder_id_i:500",
                0,
                1,
                "object_id_s ASC",
                "facet.field=object_type_s&facet=true&fq=object_type_s:FILE OR object_type_s:FOLDER&fq=hidden_b:false&fl=name,object_type_s,id"))
                        .andReturn(subFolderSearchResult);
        expect(mockSearchResults.getFacetFields(subFolderSearchResult)).andReturn(subFolderFacets);

        replayAll();

        assertNotNull(mockMvc);

        MvcResult result = mockMvc.perform(
                get(url)
                        .principal(mockAuthentication)
                        .session(mockHttpSession))
                .andExpect(status().isOk())
                .andReturn();

        verifyAll();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE));

        String json = result.getResponse().getContentAsString();

        assertNotNull(json);
        log.info("results: " + json);

        JSONObject results = new JSONObject(json);

        assertTrue(results.has("base"));

        // should have 5 files in the base (root) folder, since the topLevelFacetFields has the value 5 for "FILE"
        assertEquals(5, results.getInt("base"));

        assertTrue(results.has("Folder One"));
        assertEquals(45, results.getInt("Folder One"));
    }
}
