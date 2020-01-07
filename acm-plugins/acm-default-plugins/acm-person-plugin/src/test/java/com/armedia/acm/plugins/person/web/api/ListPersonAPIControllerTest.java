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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import com.armedia.acm.plugins.person.dao.PersonAssociationDao;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
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

import javax.persistence.QueryTimeoutException;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-web-acm-web.xml",
        "classpath:/spring/spring-library-person-plugin-api-test.xml"
})
public class ListPersonAPIControllerTest extends EasyMockSupport
{
    private MockMvc mockMvc;
    private Authentication mockAuthentication;
    private PersonAssociationDao mockPersonAssociationDao;
    private MockHttpSession mockHttpSession;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    private ListPersonAPIController unit;

    private Logger log = LogManager.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        unit = new ListPersonAPIController();

        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(exceptionResolver).build();

        mockAuthentication = createMock(Authentication.class);
        mockPersonAssociationDao = createMock(PersonAssociationDao.class);
        mockHttpSession = new MockHttpSession();

        unit.setPersonAssociationDao(mockPersonAssociationDao);
    }

    @Test
    public void ListPerson() throws Exception
    {
        Long parentId = 1329L;
        String parentType = "COMPLAINT";

        Person person = new Person();

        person.setId(700l);
        person.setModifier("testModifier");
        person.setCreator("testCreator");
        person.setCreated(new Date());
        person.setModified(new Date());
        person.setFamilyName("Person");
        person.setGivenName("ACM");
        person.setStatus("testStatus");

        PersonAssociation persAssoc = new PersonAssociation();
        persAssoc.setId(600l);
        persAssoc.setParentId(1329L);
        persAssoc.setPersonType("COMPLAINT");
        persAssoc.setPerson(person);
        persAssoc.setPersonType("Subject");
        persAssoc.setPersonDescription("long and athletic");
        persAssoc.setModifier("testModifier");
        persAssoc.setCreator("testCreator");
        persAssoc.setCreated(new Date());
        persAssoc.setModified(new Date());

        Person prsn = new Person();
        prsn.setFamilyName("testPerson");

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user");

        expect(mockPersonAssociationDao.findPersonByParentIdAndParentType(parentType, parentId)).andReturn(Arrays.asList(prsn));

        mockHttpSession.setAttribute("acm_ip_address", "acm_ip_address");

        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/v1/plugin/person/list/{parentType}/{parentId}", parentType, parentId)
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .session(mockHttpSession)
                        .principal(mockAuthentication))
                .andReturn();

        verifyAll();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE));

        String json = result.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        List<Person> fromJson = mapper.readValue(
                json,
                mapper.getTypeFactory().constructParametricType(List.class, Person.class));

        assertNotNull(fromJson);
        assertEquals(1, fromJson.size());

        log.info("person Id", fromJson.size());
        log.info("person Family name", fromJson.get(0).getFamilyName());
    }

    @Test
    public void listPerson_exception() throws Exception
    {
        Long parentId = 1329L;
        String parentType = "COMPLAINT";

        Person person = new Person();

        person.setId(700l);
        person.setModifier("testModifier");
        person.setCreator("testCreator");
        person.setCreated(new Date());
        person.setModified(new Date());
        person.setFamilyName("Person");
        person.setGivenName("ACM");
        person.setStatus("testStatus");

        PersonAssociation persAssoc = new PersonAssociation();
        persAssoc.setId(600l);
        persAssoc.setParentId(1329L);
        persAssoc.setPersonType("COMPLAINT");
        persAssoc.setPerson(person);
        persAssoc.setPersonType("Subject");
        persAssoc.setPersonDescription("long and athletic");
        persAssoc.setModifier("testModifier");
        persAssoc.setCreator("testCreator");
        persAssoc.setCreated(new Date());
        persAssoc.setModified(new Date());

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user");

        expect(mockPersonAssociationDao.findPersonByParentIdAndParentType(parentType, parentId))
                .andThrow(new QueryTimeoutException("test exception"));

        mockHttpSession.setAttribute("acm_ip_address", "acm_ip_address");

        replayAll();

        mockMvc.perform(
                get("/api/v1/plugin/person/list/{parentType}/{parentId}", parentType, parentId)
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .session(mockHttpSession)
                        .principal(mockAuthentication))
                .andExpect(content().contentType(MediaType.TEXT_PLAIN));

        verifyAll();
    }
}
