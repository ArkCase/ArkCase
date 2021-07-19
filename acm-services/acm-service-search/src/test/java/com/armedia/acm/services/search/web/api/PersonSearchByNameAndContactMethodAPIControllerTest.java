package com.armedia.acm.services.search.web.api;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import com.armedia.acm.services.search.model.solr.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

import java.net.URLEncoder;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/spring-web-acm-web.xml",
        "classpath:/spring/spring-library-search-web-api-test.xml" })
public class PersonSearchByNameAndContactMethodAPIControllerTest extends EasyMockSupport
{
    private MockMvc mockMvc;
    private Authentication mockAuthentication;
    private ExecuteSolrQuery mockExecuteSolrQuery;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    private PersonSearchByNameAndContactMethodAPIController unit;

    private Logger log = LogManager.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        unit = new PersonSearchByNameAndContactMethodAPIController();

        mockExecuteSolrQuery = createMock(ExecuteSolrQuery.class);

        unit.setExecuteSolrQuery(mockExecuteSolrQuery);

        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(exceptionResolver).build();

        mockAuthentication = createMock(Authentication.class);
    }

    @Test
    public void person() throws Exception
    {

        String name = "test name";
        String contactMethod = "contact method";
        String solrResponse = "{ \"solrResponse\": \"this is a test response.\" }";

        final String encodedContactMethodJoin = URLEncoder.encode("{!join from=id to=contact_method_ss}");
        String query = "object_type_s:PERSON AND name:" + URLEncoder.encode(name) + " AND " + encodedContactMethodJoin + "value_parseable:"
                + URLEncoder.encode(contactMethod + "*");
        String sort = "last_name_lcs ASC, first_name_lcs ASC";

        query = query.replaceAll(" ", "+");
        sort = sort.replaceAll(" ", "+");

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user").atLeastOnce();

        expect(mockExecuteSolrQuery.getResultsByPredefinedQuery(mockAuthentication, SolrCore.ADVANCED_SEARCH, query, 0, 10, sort, false))
                .andReturn(solrResponse);

        replayAll();

        MvcResult result = mockMvc
                .perform(get("/api/v1/plugin/search/personSearch?name=" + name + "&cm=" + contactMethod).principal(mockAuthentication))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        verifyAll();

        String jsonString = result.getResponse().getContentAsString();

        log.debug("Got JSON: [{}]", jsonString);

        assertEquals(solrResponse, jsonString);

    }
}
