package com.armedia.acm.plugins.ecm.web.api;

import com.armedia.acm.plugins.ecm.service.impl.EcmFileServiceImpl;
import com.armedia.acm.services.search.model.SearchConstants;
import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.search.service.SearchResults;
import org.apache.commons.io.IOUtils;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.net.URLEncoder;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Created by riste.tutureski on 9/14/2015.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-library-ecm-plugin-test.xml"
})
public class GetTotalPageCountAPIControllerTest extends EasyMockSupport
{
    private Logger LOG = LoggerFactory.getLogger(getClass());

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
        expect(mockExecuteSolrQuery.getResultsByPredefinedQuery(mockAuthentication, SolrCore.QUICK_SEARCH, query, 0, 50, SearchConstants.PROPERTY_OBJECT_ID_S + " DESC")).andReturn(searchResult);
        expect(mockExecuteSolrQuery.getResultsByPredefinedQuery(mockAuthentication, SolrCore.QUICK_SEARCH, query, 50, 50, SearchConstants.PROPERTY_OBJECT_ID_S + " DESC")).andReturn(noResultsSearchResult);

        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/latest/service/ecm/totalpagecount/{parentObjectType}/{parentObjectId}?fileTypes={fileTypes}&mimeTypes={mimeTypes}", "CASE_FILE", 10, "authorization,abstract", "application/pdf")
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
        expect(mockExecuteSolrQuery.getResultsByPredefinedQuery(mockAuthentication, SolrCore.QUICK_SEARCH, query, 0, 50, SearchConstants.PROPERTY_OBJECT_ID_S + " DESC")).andReturn(searchResult);
        expect(mockExecuteSolrQuery.getResultsByPredefinedQuery(mockAuthentication, SolrCore.QUICK_SEARCH, query, 50, 50, SearchConstants.PROPERTY_OBJECT_ID_S + " DESC")).andReturn(noResultsSearchResult);

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
