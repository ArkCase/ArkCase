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
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.armedia.acm.plugins.casefile.model.CaseByStatusDto;
import com.armedia.acm.services.search.model.SearchConstants;
import com.armedia.acm.services.search.model.solr.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.fasterxml.jackson.databind.ObjectMapper;

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
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by marjan.stefanoski on 10/13/2014.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-web-acm-web.xml",
        "classpath:/spring/spring-library-case-plugin-unit-test.xml"
})
public class GetCasesByStatusAPIControllerTest extends EasyMockSupport
{

    private MockMvc mockMvc;
    private MockHttpSession mockHttpSession;

    private GetCasesByStatusAPIController unit;

    private ExecuteSolrQuery mockExecuteSolrQuery;
    private Authentication mockAuthentication;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    private Logger log = LogManager.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        mockExecuteSolrQuery = createMock(ExecuteSolrQuery.class);
        mockHttpSession = new MockHttpSession();
        mockAuthentication = createMock(Authentication.class);

        unit = new GetCasesByStatusAPIController();

        unit.setExecuteSolrQuery(mockExecuteSolrQuery);

        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(exceptionResolver).build();
    }

    @Test
    public void allCasesByStatusTest() throws Exception
    {
        String facetQuery = "object_type_s:CASE_FILE&rows=0&fl=id&wt=json&indent=true&facet=true&facet.mincount=1&facet.field="
                + SearchConstants.PROPERTY_STATUS;

        InputStream facetInputStream = getClass().getClassLoader()
                .getResourceAsStream("SolrFacetResponseGetNumberOfCaseFilesByStatusTest.json");
        String facetSolrResponse = IOUtils.toString(facetInputStream, Charset.forName("UTF-8"));

        expect(mockAuthentication.getName()).andReturn("user");
        expect(mockExecuteSolrQuery.getResultsByPredefinedQuery(mockAuthentication, SolrCore.QUICK_SEARCH, facetQuery, 0, 1, ""))
                .andReturn(facetSolrResponse);

        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/latest/plugin/casebystatus/{timePeriod}", "all")
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .principal(mockAuthentication)
                        .session(mockHttpSession))
                .andReturn();

        verifyAll();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE));

        String returned = result.getResponse().getContentAsString();

        log.info("results: " + returned);

        ObjectMapper objectMapper = new ObjectMapper();

        List<CaseByStatusDto> caseByStatusDtos = objectMapper.readValue(returned,
                objectMapper.getTypeFactory().constructParametrizedType(List.class, List.class, CaseByStatusDto.class));

        assertEquals(8, caseByStatusDtos.size());

        assertEquals("DRAFT", caseByStatusDtos.get(0).getStatus());
        assertEquals(1757, caseByStatusDtos.get(0).getCount());
        assertEquals("Transcribe", caseByStatusDtos.get(1).getStatus());
        assertEquals(1044, caseByStatusDtos.get(1).getCount());
        assertEquals("Archive", caseByStatusDtos.get(2).getStatus());
        assertEquals(216, caseByStatusDtos.get(2).getCount());
        assertEquals("Rejected", caseByStatusDtos.get(3).getStatus());
        assertEquals(101, caseByStatusDtos.get(3).getCount());
        assertEquals("Quality Control", caseByStatusDtos.get(4).getStatus());
        assertEquals(94, caseByStatusDtos.get(4).getCount());
        assertEquals("Fulfill", caseByStatusDtos.get(5).getStatus());
        assertEquals(84, caseByStatusDtos.get(5).getCount());
        assertEquals("Distribution", caseByStatusDtos.get(6).getStatus());
        assertEquals(67, caseByStatusDtos.get(6).getCount());
        assertEquals("Billing", caseByStatusDtos.get(7).getStatus());
        assertEquals(59, caseByStatusDtos.get(7).getCount());
    }
}
