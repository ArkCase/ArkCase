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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.armedia.acm.plugins.ecm.service.impl.EcmFileServiceImpl;
import com.armedia.acm.services.search.model.SearchConstants;
import com.armedia.acm.services.search.model.solr.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.search.service.SearchResults;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by riste.tutureski on 9/14/2015.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-library-ecm-plugin-test.xml"
})
public class GetTotalPageCountAPIControllerTest extends EasyMockSupport
{
    private Logger LOG = LogManager.getLogger(getClass());

    private MockMvc mockMvc;
    private GetTotalPageCountAPIController unit;
    private EcmFileServiceImpl ecmFileService;
    private Authentication mockAuthentication;
    private ExecuteSolrQuery mockExecuteSolrQuery;

    @Autowired
    private ExceptionHandlerExceptionResolver filePluginExceptionResolver;

    @Before
    public void setUp() throws Exception
    {
        unit = new GetTotalPageCountAPIController();
        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(filePluginExceptionResolver).build();
        ecmFileService = new EcmFileServiceImpl();

        mockAuthentication = createMock(Authentication.class);
        mockExecuteSolrQuery = createMock(ExecuteSolrQuery.class);

        ecmFileService.setSolrQuery(mockExecuteSolrQuery);
        ecmFileService.setSearchResults(new SearchResults());
        unit.setEcmFileService(ecmFileService);
    }

    @Test
    public void getTotalPageCountTest() throws Exception
    {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/TotalPageCountSolrResponse.json");
        String searchResult = IOUtils.toString(inputStream, "UTF-8");

        InputStream noResultsInputStream = getClass().getClassLoader().getResourceAsStream("json/NoResultsTotalPageCountSolrResponse.json");
        String noResultsSearchResult = IOUtils.toString(noResultsInputStream, "UTF-8");

        String query = SearchConstants.PROPERTY_OBJECT_TYPE + ":FILE AND " +
                SearchConstants.PROPERTY_FILE_TYPE + ":(authorization OR abstract) AND " +
                SearchConstants.PROPERTY_MIME_TYPE + ":application/pdf AND " +
                SearchConstants.PROPERTY_PARENT_OBJECT_TYPE_S + ":CASE_FILE AND " +
                SearchConstants.PROPERTY_PARENT_OBJECT_ID_I + ":10";

        expect(mockAuthentication.getName()).andReturn("user");
        expect(mockExecuteSolrQuery.getResultsByPredefinedQuery(mockAuthentication, SolrCore.ADVANCED_SEARCH, query, 0, 50,
                SearchConstants.PROPERTY_OBJECT_ID_S + " DESC")).andReturn(searchResult);
        expect(mockExecuteSolrQuery.getResultsByPredefinedQuery(mockAuthentication, SolrCore.ADVANCED_SEARCH, query, 50, 50,
                SearchConstants.PROPERTY_OBJECT_ID_S + " DESC")).andReturn(noResultsSearchResult);

        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/latest/service/ecm/totalpagecount/{parentObjectType}/{parentObjectId}?fileTypes={fileTypes}&mimeTypes={mimeTypes}",
                        "CASE_FILE", 10, "authorization,abstract", "application/pdf")
                                .principal(mockAuthentication)
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        verifyAll();

        closeInputStream(inputStream);

        LOG.info("Results: {}", result.getResponse().getContentAsString());

        int totalPageCount = Integer.parseInt(result.getResponse().getContentAsString());

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertEquals(11, totalPageCount);
    }

    @Test
    public void getTotalPageCountAllFileTypesAllMimeTypesTest() throws Exception
    {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("json/TotalPageCountSolrResponse.json");
        String searchResult = IOUtils.toString(inputStream, "UTF-8");

        InputStream noResultsInputStream = getClass().getClassLoader().getResourceAsStream("json/NoResultsTotalPageCountSolrResponse.json");
        String noResultsSearchResult = IOUtils.toString(noResultsInputStream, "UTF-8");

        String query = SearchConstants.PROPERTY_OBJECT_TYPE + ":FILE AND " +
                SearchConstants.PROPERTY_PARENT_OBJECT_TYPE_S + ":CASE_FILE AND " +
                SearchConstants.PROPERTY_PARENT_OBJECT_ID_I + ":10";

        expect(mockAuthentication.getName()).andReturn("user");
        expect(mockExecuteSolrQuery.getResultsByPredefinedQuery(mockAuthentication, SolrCore.ADVANCED_SEARCH, query, 0, 50,
                SearchConstants.PROPERTY_OBJECT_ID_S + " DESC")).andReturn(searchResult);
        expect(mockExecuteSolrQuery.getResultsByPredefinedQuery(mockAuthentication, SolrCore.ADVANCED_SEARCH, query, 50, 50,
                SearchConstants.PROPERTY_OBJECT_ID_S + " DESC")).andReturn(noResultsSearchResult);

        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/latest/service/ecm/totalpagecount/{parentObjectType}/{parentObjectId}", "CASE_FILE", 10)
                        .principal(mockAuthentication)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        verifyAll();

        closeInputStream(inputStream);

        LOG.info("Results: {}", result.getResponse().getContentAsString());

        int totalPageCount = Integer.parseInt(result.getResponse().getContentAsString());

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertEquals(11, totalPageCount);
    }

    private void closeInputStream(InputStream inputStream)
    {
        if (inputStream != null)
        {
            try
            {
                inputStream.close();
            }
            catch (IOException e)
            {
                LOG.warn("Cannot close input stream in integration test");
            }

        }
    }
}
