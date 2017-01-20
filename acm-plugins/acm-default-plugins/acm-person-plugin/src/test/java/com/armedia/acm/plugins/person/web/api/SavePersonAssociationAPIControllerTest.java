package com.armedia.acm.plugins.person.web.api;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import com.armedia.acm.plugins.person.service.PersonAssociationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.easymock.Capture;
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

import java.util.Date;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/spring/spring-web-acm-web.xml", "classpath:/spring/spring-library-person-plugin-test.xml"})
public class SavePersonAssociationAPIControllerTest extends EasyMockSupport
{
    private MockMvc mockMvc;

    private SavePersonAssociationAPIController unit;

    private Authentication mockAuthentication;

    private PersonAssociationService mockPersonAssociationService;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        unit = new SavePersonAssociationAPIController();
        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(exceptionResolver).build();
        mockPersonAssociationService = createMock(PersonAssociationService.class);
        mockAuthentication = createMock(Authentication.class);
        unit.setPersonAssociationService(mockPersonAssociationService);
    }

    @Test
    public void addPersonAssociation_saveExistingPersonAssociation() throws Exception
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

        PersonAssociation perAssoc = new PersonAssociation();
        perAssoc.setId(998L);
        perAssoc.setParentId(999L);
        perAssoc.setParentType("COMPLAINT");
        perAssoc.setPerson(person);
        perAssoc.setPersonType("Subject");
        perAssoc.setPersonDescription("long and athletic");
        perAssoc.setCreator("testCreator");
        perAssoc.setModifier("testModifier");
        perAssoc.setCreated(new Date());
        perAssoc.setModified(new Date());

        PersonAssociation saved = new PersonAssociation();
        saved.setId(perAssoc.getId());

        ObjectMapper objectMapper = new ObjectMapper();
        String in = objectMapper.writeValueAsString(perAssoc);

        log.debug("Input JSON: {}", in);

        Capture<PersonAssociation> found = Capture.newInstance();

        expect(mockPersonAssociationService.savePersonAssociation(capture(found), eq(mockAuthentication))).andReturn(saved);

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user");

        replayAll();

        MvcResult result = mockMvc.perform(post("/api/latest/plugin/personAssociation")
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                .contentType(MediaType.APPLICATION_JSON)
                .principal(mockAuthentication).content(in)).andReturn();

        log.info("results: {}", result.getResponse().getContentAsString());

        verifyAll();

        assertEquals(saved, found.getValue());

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }

    @Test
    public void invalidInput() throws Exception
    {
        String notPersonAssociationJson = "{ \"user\": \"com\", \"className\": \"com.armedia.acm.plugins.person.model.PersonAssociation\" }";

        Capture<PersonAssociation> found = Capture.newInstance();
        // With upgrading spring version, bad JSON is not the problem for entering the execution in the controller
        expect(mockPersonAssociationService.savePersonAssociation(capture(found), eq(mockAuthentication)))
                .andThrow(new RuntimeException());

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user");

        replayAll();

        mockMvc.perform(post("/api/latest/plugin/personAssociation").accept(MediaType.parseMediaType("application/json;charset=UTF-8")).contentType(MediaType.APPLICATION_JSON)
                .principal(mockAuthentication).content(notPersonAssociationJson)).andExpect(status().isInternalServerError()).andExpect(content().contentType(MediaType.TEXT_PLAIN));

        verifyAll();

    }

    @Test
    public void addPersonAssociation_exception() throws Exception
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

        PersonAssociation perAssoc = new PersonAssociation();
        perAssoc.setId(998L);
        perAssoc.setParentId(999L);
        perAssoc.setParentType("COMPLAINT");
        perAssoc.setPerson(person);
        perAssoc.setPersonType("Subject");
        perAssoc.setPersonDescription("long and athletic");
        perAssoc.setCreator("testCreator");
        perAssoc.setModifier("testModifier");
        perAssoc.setCreated(new Date());
        perAssoc.setModified(new Date());
        perAssoc.setNotes("simple note describing the association");

        PersonAssociation saved = new PersonAssociation();
        saved.setId(perAssoc.getId());

        ObjectMapper objectMapper = new ObjectMapper();
        String in = objectMapper.writeValueAsString(perAssoc);

        log.debug("Input JSON: {}", in);

        Capture<PersonAssociation> found = Capture.newInstance();

        expect(mockPersonAssociationService.savePersonAssociation(capture(found), eq(mockAuthentication)))
                .andThrow(new AcmCreateObjectFailedException("PERSON-ASSOCIATION", "", null));

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user");

        replayAll();

        mockMvc.perform(post("/api/latest/plugin/personAssociation")
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                .contentType(MediaType.APPLICATION_JSON)
                .principal(mockAuthentication).content(in))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN));

        verifyAll();

        assertEquals(perAssoc.getId(), found.getValue().getId());
    }

}
