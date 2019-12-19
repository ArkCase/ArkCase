package com.armedia.acm.plugins.casefile.web.api;

/*-
 * #%L
 * ACM Default Plugin: Case File
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
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.armedia.acm.plugins.casefile.service.ActiveCaseFileByQueueService;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.search.service.SearchResults;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by riste.tutureski on 9/22/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-library-case-plugin-unit-test.xml"
})
public class GetNumberOfActiveCaseFilesByQueueAPIControllerTest extends EasyMockSupport
{
    private Logger LOG = LogManager.getLogger(getClass());

    private MockMvc mockMvc;
    private GetNumberOfActiveCaseFilesByQueueAPIController unit;
    private Authentication mockAuthentication;
    private ExecuteSolrQuery mockExecuteSolrQuery;
    private ActiveCaseFileByQueueService mockActiveCaseFileByQueueService;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    @Before
    public void setUp() throws Exception
    {
        unit = new GetNumberOfActiveCaseFilesByQueueAPIController();

        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(exceptionResolver).build();

        mockAuthentication = createMock(Authentication.class);
        mockExecuteSolrQuery = createMock(ExecuteSolrQuery.class);
        mockActiveCaseFileByQueueService = spy(ActiveCaseFileByQueueService.class);
        unit.setActiveCaseFileByQueueService(mockActiveCaseFileByQueueService);
    }

    @Test
    public void testNumberOfActiveCaseFilesByQueue() throws Exception
    {
        List<Object> queuesList = new ArrayList<>();
        List<Object> facetList = new ArrayList<>();
        Map<String, Long> activeCaseFilesByQueue = new HashMap<>();
        init(queuesList, facetList, activeCaseFilesByQueue);
        SearchResults searchResults = new SearchResults();

        expect(mockAuthentication.getName()).andReturn("user");
        when(mockActiveCaseFileByQueueService.getQueues(mockAuthentication, searchResults)).thenReturn(queuesList);
        when(mockActiveCaseFileByQueueService.getFacet(mockAuthentication, searchResults)).thenReturn(facetList);
        when(mockActiveCaseFileByQueueService.getNumberOfActiveCaseFilesByQueue(queuesList, facetList)).thenReturn(activeCaseFilesByQueue);

        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/latest/plugin/casefile/number/by/queue")
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(mockAuthentication))
                .andReturn();

        verifyAll();

        LOG.info("Results: " + result.getResponse().getContentAsString());

        ObjectMapper objectMapper = new ObjectMapper();

        Map<String, Long> resultMap = objectMapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<Map<String, Long>>()
                {
                });

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertEquals(5, resultMap.size());

        assertEquals(activeCaseFilesByQueue.get("Approve"), (Long) 5L);
        assertEquals(activeCaseFilesByQueue.get("Appeal"), (Long) 0L);
        assertEquals(activeCaseFilesByQueue.get("Billing"), (Long) 4L);
        assertEquals(activeCaseFilesByQueue.get("Delete"), (Long) 29L);
        assertEquals(activeCaseFilesByQueue.get("Fulfill"), (Long) 2L);
    }

    private void init(List<Object> queuesList, List<Object> facetList, Map<String, Long> activeCaseFilesByQueue)
    {
        queuesList = Arrays.asList("Approve", ",Appeal", "Billing", "Delete", "Fulfill");
        facetList = Arrays.asList("Approve", 5, "Appeal", 0, "Billing", 4, "Delete", 29, "Fulfill", 2);
        activeCaseFilesByQueue.put("Approve", 5L);
        activeCaseFilesByQueue.put("Appeal", 0L);
        activeCaseFilesByQueue.put("Billing", 4L);
        activeCaseFilesByQueue.put("Delete", 29L);
        activeCaseFilesByQueue.put("Fulfill", 2L);
    }
}
