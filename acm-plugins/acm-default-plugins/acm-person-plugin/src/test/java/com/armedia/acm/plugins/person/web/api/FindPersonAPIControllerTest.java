package com.armedia.acm.plugins.person.web.api;

/*-
 * #%L
 * ACM Default Plugin: Person
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

import com.armedia.acm.plugins.person.dao.PersonAssociationDao;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-web-acm-web.xml",
        "classpath:/spring/spring-library-person-plugin-api-test.xml"
})
public class FindPersonAPIControllerTest extends EasyMockSupport
{
    private MockMvc mockMvc;
    private Authentication mockAuthentication;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    private FindPersonAPIController unit;

    private PersonAssociationDao mockDao;
    private ExecuteSolrQuery mockExecuteSolrQuery;

    private Logger log = LogManager.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        unit = new FindPersonAPIController();

        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(exceptionResolver).build();

        mockDao = createMock(PersonAssociationDao.class);
        mockExecuteSolrQuery = createMock(ExecuteSolrQuery.class);
        mockAuthentication = createMock(Authentication.class);

        unit.setExecuteSolrQuery(mockExecuteSolrQuery);
    }

    @Test
    public void find_byPersonAssociationId() throws Exception
    {
        Long assocId = 500L;

        Person fromDao = new Person();
        fromDao.setFamilyName("Stone");
        fromDao.setGivenName("Sly");

        // expect(mockDao.findPersonByPersonAssociationId(assocId)).andReturn(fromDao);

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user");

        expect(mockExecuteSolrQuery.getResultsByPredefinedQuery(mockAuthentication, SolrCore.ADVANCED_SEARCH,
                "id:500-PERSON-ASSOCIATION", 0, 10, "create_date_tdt DESC")).andReturn("{\"response\": {\"Example\": \"Data\"}}");

        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/latest/plugin/person/find?assocId=" + assocId)
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .principal(mockAuthentication))
                .andReturn();

        verifyAll();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE));

        String returned = result.getResponse().getContentAsString();

        log.info("results: " + returned);
    }

    @Test
    public void find_byPersonAssociationId_empty() throws Exception
    {
        Long assocId = 500L;
        // expect(mockDao.findPersonByPersonAssociationId(assocId)).andThrow(new NoResultException());

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user");

        expect(mockExecuteSolrQuery.getResultsByPredefinedQuery(mockAuthentication, SolrCore.ADVANCED_SEARCH,
                "id:500-PERSON-ASSOCIATION", 0, 10, "create_date_tdt DESC")).andReturn("{\"response\": {}}");

        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/latest/plugin/person/find?assocId=" + assocId)
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .principal(mockAuthentication))
                .andReturn();

        verifyAll();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE));
    }

}
