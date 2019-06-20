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


import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.armedia.acm.plugins.addressable.model.PostalAddress;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonAlias;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import com.armedia.acm.plugins.person.service.PersonEventPublisher;
import com.armedia.acm.plugins.person.service.SavePersonTransaction;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.easymock.Capture;
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
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import java.util.Date;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-web-acm-web.xml",
        "classpath:/spring/spring-library-person-plugin-api-test.xml"
})
public class SavePersonAPIControllerTest extends EasyMockSupport
{
    private MockMvc mockMvc;

    private SavePersonAPIController unit;
    private SavePersonTransaction mockSaveTransaction;
    private PersonEventPublisher mockEventPublisher;
    private Authentication mockAuthentication;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    private Logger log = LogManager.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        unit = new SavePersonAPIController();
        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(exceptionResolver).build();

        mockSaveTransaction = createMock(SavePersonTransaction.class);
        mockEventPublisher = createMock(PersonEventPublisher.class);
        mockAuthentication = createMock(Authentication.class);

        unit.setPersonTransaction(mockSaveTransaction);
        unit.setEventPublisher(mockEventPublisher);
    }

    @Test
    public void addPerson_saveExistingPerson() throws Exception
    {
        Person p = new Person();
        Person person = new Person();

        person.setId(500L);
        person.setTitle("Dr");
        person.setCreator("testCreator");
        person.setCreated(new Date());
        person.setModified(new Date());
        person.setFamilyName("Person");
        person.setGivenName("ACM");
        person.setStatus("testStatus");

        PostalAddress address = new PostalAddress();
        address.setCity("Falls Church");
        address.setState("VA");
        address.setStreetAddress("8221 Old Courthouse Road");

        PersonAlias personAlias;
        personAlias = new PersonAlias();
        personAlias.setAliasType("Others");
        personAlias.setAliasValue("ACM");
        personAlias.setModifier("testModifier");
        personAlias.setCreator("testCreator");

        PersonAssociation perAssoc = new PersonAssociation();
        perAssoc.setParentId(999L);
        perAssoc.setParentType("COMPLAINT");
        perAssoc.setPerson(p);
        perAssoc.setPersonType("Subject");
        perAssoc.setPersonDescription("long and athletic");
        perAssoc.setModifier("testModifier");
        perAssoc.setCreator("testCreator");
        perAssoc.setCreated(new Date());
        perAssoc.setModified(new Date());

        person.getAddresses().add(address);
        person.getPersonAliases().add(personAlias);

        Person saved = new Person();
        saved.setId(person.getId());

        ObjectMapper objectMapper = new ObjectMapper();
        String in = objectMapper.writeValueAsString(person);

        log.debug("Input JSON: {}", in);

        Capture<Person> found = Capture.newInstance();

        expect(mockSaveTransaction.savePerson(capture(found), eq(mockAuthentication))).andReturn(saved);
        mockEventPublisher.publishPersonUpsertEvents(capture(found), anyObject(), eq(false), eq(true));

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user");

        replayAll();

        MvcResult result = mockMvc.perform(
                post("/api/latest/plugin/person")
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(mockAuthentication)
                        .content(in))
                .andReturn();

        log.info("results: " + result.getResponse().getContentAsString());

        verifyAll();

        assertEquals(saved, found.getValue());

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());

    }

    @Test
    public void invalidInput() throws Exception
    {

        String notPersonJson = "{ \"user\": \"com\" }";

        Capture<Person> found = Capture.newInstance();

        // With upgrading spring version, bad JSON is not the problem for entering the execution in the controller
        expect(mockSaveTransaction.savePerson(capture(found), eq(mockAuthentication))).andThrow(new RuntimeException()).anyTimes();

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user");

        replayAll();

        mockMvc.perform(
                post("/api/latest/plugin/person")
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(mockAuthentication)
                        .content(notPersonJson))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN));

        verifyAll();

    }

    @Test
    public void addPerson_exception() throws Exception
    {
        Person person = new Person();
        person.setId(500L);
        person.setTitle("Dr");
        person.setCreator("testCreator");
        person.setCreated(new Date());
        person.setModified(new Date());
        person.setFamilyName("Person");
        person.setGivenName("ACM");
        person.setStatus("testStatus");

        PostalAddress address = new PostalAddress();
        address.setCity("Falls Church");
        address.setState("VA");
        address.setStreetAddress("8221 Old Courthouse Road");

        PersonAlias personAlias = new PersonAlias();
        personAlias.setAliasType("Others");
        personAlias.setAliasValue("ACM");
        personAlias.setModifier("testModifier");
        personAlias.setCreator("testCreator");

        PersonAssociation perAssoc = new PersonAssociation();
        perAssoc.setParentId(999L);
        perAssoc.setParentType("COMPLAINT");
        perAssoc.setPersonType("Subject");
        perAssoc.setPersonDescription("long and athletic");
        perAssoc.setModifier("testModifier");
        perAssoc.setCreator("testCreator");
        perAssoc.setCreated(new Date());
        perAssoc.setModified(new Date());

        person.getAddresses().add(address);
        person.getPersonAliases().add(personAlias);
        Person saved = new Person();
        saved.setId(person.getId());

        ObjectMapper objectMapper = new ObjectMapper();
        String in = objectMapper.writeValueAsString(person);

        log.debug("Input JSON: {}", in);

        Capture<Person> found = Capture.newInstance();

        expect(mockSaveTransaction.savePerson(capture(found), eq(mockAuthentication)))
                .andThrow(new CannotCreateTransactionException("testException"));
        mockEventPublisher.publishPersonUpsertEvents(capture(found), anyObject(), eq(false), eq(false));

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user");

        replayAll();

        mockMvc.perform(
                post("/api/latest/plugin/person")
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(mockAuthentication)
                        .content(in))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN));

        verifyAll();

        assertEquals(person.getId(), found.getValue().getId());

    }

}
