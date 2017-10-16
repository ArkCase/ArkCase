package com.armedia.acm.plugins.casefile.web.api;

import com.armedia.acm.plugins.casefile.model.AcmQueue;
import com.armedia.acm.services.search.model.SearchConstants;
import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Map;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Created by riste.tutureski on 9/22/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-library-case-plugin-test.xml"
})
public class GetNumberOfActiveCaseFilesByQueueAPIControllerTest extends EasyMockSupport
{
    private Logger LOG = LoggerFactory.getLogger(getClass());

    private MockMvc mockMvc;
    private GetNumberOfActiveCaseFilesByQueueAPIController unit;
    private Authentication mockAuthentication;
    private ExecuteSolrQuery mockExecuteSolrQuery;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    @Before
    public void setUp() throws Exception
    {
        unit = new GetNumberOfActiveCaseFilesByQueueAPIController();

        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(exceptionResolver).build();

        mockAuthentication = createMock(Authentication.class);
        mockExecuteSolrQuery = createMock(ExecuteSolrQuery.class);

        unit.setExecuteSolrQuery(mockExecuteSolrQuery);
    }

    @Test
    public void testNumberOfActiveCaseFilesByQueue() throws Exception
    {
        AcmQueue transcribe = new AcmQueue(1L, "Transcribe", 1);
        AcmQueue fulfillOrder = new AcmQueue(2L, "Fulfill Order", 2);
        AcmQueue qcOrder = new AcmQueue(3L, "QC Order", 3);
        AcmQueue billing = new AcmQueue(4L, "Billing", 4);
        AcmQueue distribution = new AcmQueue(5L, "Distribution", 5);
        AcmQueue nonCompliance = new AcmQueue(6L, "Non-Compliance", 6);

        String queuesQuery = "object_type_s:QUEUE&sort=" + SearchConstants.PROPERTY_QUEUE_ORDER + " ASC";
        String facetQuery = "object_type_s:CASE_FILE AND " + SearchConstants.PROPERTY_QUEUE_NAME_S + ":*&rows=1&fl=id&wt=json&indent=true&facet=true&facet.field=" + SearchConstants.PROPERTY_QUEUE_NAME_S;

        InputStream queuesIputStream = getClass().getClassLoader().getResourceAsStream("SolrQueuesResponseGetNumberOfCaseFilesByQueueTest.json");
        String queuesSolrResponse = IOUtils.toString(queuesIputStream, Charset.forName("UTF-8"));

        InputStream facetInputStream = getClass().getClassLoader().getResourceAsStream("SolrFacetResponseGetNumberOfCaseFilesByQueueTest.json");
        String facetSolrResponse = IOUtils.toString(facetInputStream, Charset.forName("UTF-8"));

        expect(mockAuthentication.getName()).andReturn("user");
        expect(mockExecuteSolrQuery.getResultsByPredefinedQuery(mockAuthentication, SolrCore.QUICK_SEARCH, queuesQuery, 0, 50, "")).andReturn(queuesSolrResponse);
        expect(mockExecuteSolrQuery.getResultsByPredefinedQuery(mockAuthentication, SolrCore.QUICK_SEARCH, queuesQuery, 50, 50, "")).andReturn(null);
        expect(mockExecuteSolrQuery.getResultsByPredefinedQuery(mockAuthentication, SolrCore.QUICK_SEARCH, facetQuery, 0, 1, "")).andReturn(facetSolrResponse);

        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/latest/plugin/casefile/number/by/queue")
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(mockAuthentication)
        ).andReturn();

        verifyAll();

        LOG.info("Results: " + result.getResponse().getContentAsString());

        ObjectMapper objectMapper = new ObjectMapper();

        Map<String, Long> resultMap = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<Map<String, Long>>()
        {
        });

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertEquals(6, resultMap.size());

        assertEquals(transcribe.getName(), ((Map.Entry) resultMap.entrySet().toArray()[0]).getKey());
        assertEquals(fulfillOrder.getName(), ((Map.Entry) resultMap.entrySet().toArray()[1]).getKey());
        assertEquals(qcOrder.getName(), ((Map.Entry) resultMap.entrySet().toArray()[2]).getKey());
        assertEquals(billing.getName(), ((Map.Entry) resultMap.entrySet().toArray()[3]).getKey());
        assertEquals(distribution.getName(), ((Map.Entry) resultMap.entrySet().toArray()[4]).getKey());
        assertEquals(nonCompliance.getName(), ((Map.Entry) resultMap.entrySet().toArray()[5]).getKey());

        assertEquals(90L, ((Map.Entry) resultMap.entrySet().toArray()[0]).getValue());
        assertEquals(17L, ((Map.Entry) resultMap.entrySet().toArray()[1]).getValue());
        assertEquals(2L, ((Map.Entry) resultMap.entrySet().toArray()[2]).getValue());
        assertEquals(0L, ((Map.Entry) resultMap.entrySet().toArray()[3]).getValue());
        assertEquals(4L, ((Map.Entry) resultMap.entrySet().toArray()[4]).getValue());
        assertEquals(4L, ((Map.Entry) resultMap.entrySet().toArray()[5]).getValue());
    }
}
