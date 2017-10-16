package com.armedia.acm.services.users.web.api;

import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import org.apache.commons.httpclient.HttpStatus;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static junit.framework.Assert.assertEquals;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Created by armdev on 5/28/14.
 */
public class GetUsersByGroupAPIControllerTest extends EasyMockSupport
{
    private MockMvc mockMvc;
    private ExecuteSolrQuery mockExecuteSolrQuery;
    private Authentication mockAuthentication;

    private GetUsersByGroupAPIController unit;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        mockExecuteSolrQuery = createMock(ExecuteSolrQuery.class);
        mockAuthentication = createMock(Authentication.class);

        unit = new GetUsersByGroupAPIController();
        unit.setExecuteSolrQuery(mockExecuteSolrQuery);

        mockMvc = MockMvcBuilders.standaloneSetup(unit).build();
    }

    @Test
    public void buildQueryForGroupWithoutSpaceTest() throws Exception
    {

        String response = "response";
        String group = "Group1";
        String expectedQuery = "object_type_s:USER AND status_lcs:VALID AND groups_id_ss:" + group;
        Capture<String> capturedSolrQuery = EasyMock.newCapture();
        expect(mockAuthentication.getName()).andReturn("USER");
        expect(mockExecuteSolrQuery.getResultsByPredefinedQuery(eq(mockAuthentication), eq(SolrCore.ADVANCED_SEARCH), capture(capturedSolrQuery), eq(0), eq(1000), eq(""))).andReturn(response);


        replayAll();

        MvcResult result =
                mockMvc.perform(get("/api/latest/users/by-group/{group}", group)
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .principal(mockAuthentication)
                        .contentType(MediaType.APPLICATION_JSON)).andReturn();

        log.info("results: [{}]", result.getResponse().getContentAsString());

        verifyAll();

        assertEquals(HttpStatus.SC_OK, result.getResponse().getStatus());
        assertEquals(expectedQuery, capturedSolrQuery.getValue());
    }

    @Test
    public void buildQueryForGroupWithSpaceTest() throws Exception
    {

        String response = "response";
        String group = "Group 1";
        String expectedQuery = "object_type_s:USER AND status_lcs:VALID AND groups_id_ss:\"" + group + "\"";
        Capture<String> capturedSolrQuery = EasyMock.newCapture();
        expect(mockAuthentication.getName()).andReturn("USER");
        expect(mockExecuteSolrQuery.getResultsByPredefinedQuery(eq(mockAuthentication), eq(SolrCore.ADVANCED_SEARCH), capture(capturedSolrQuery), eq(0), eq(1000), eq(""))).andReturn(response);


        replayAll();

        MvcResult result =
                mockMvc.perform(get("/api/latest/users/by-group/{group}", group)
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .principal(mockAuthentication)
                        .contentType(MediaType.APPLICATION_JSON)).andReturn();

        log.info("results: [{}]", result.getResponse().getContentAsString());

        verifyAll();

        assertEquals(HttpStatus.SC_OK, result.getResponse().getStatus());
        assertEquals(expectedQuery, capturedSolrQuery.getValue());
    }
}
