package com.armedia.acm.plugins.person.web.api;

import com.armedia.acm.plugins.addressable.model.PostalAddress;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonAlias;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import com.armedia.acm.plugins.person.service.PersonEventPublisher;
import com.armedia.acm.plugins.person.service.SavePersonTransaction;
import java.util.Date;
import org.codehaus.jackson.map.ObjectMapper;
import org.easymock.Capture;

import static org.easymock.EasyMock.*;
import org.easymock.EasyMockSupport;
import static org.junit.Assert.*;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-web-acm-web.xml",
        "classpath:/spring/spring-library-person-plugin-test.xml"
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

    private Logger log = LoggerFactory.getLogger(getClass());

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
        
        PersonAlias personAlias = new PersonAlias();
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
        person.getPersonAssociations().add(perAssoc);


        Person saved = new Person();
        saved.setId(person.getId());
        
        ObjectMapper objectMapper = new ObjectMapper();
        String in = objectMapper.writeValueAsString(person);

        log.debug("Input JSON: " + in);

        Capture<Person> found = new Capture<>();

        expect(mockSaveTransaction.savePerson(capture(found), eq(mockAuthentication))).andReturn(saved);
        mockEventPublisher.publishPersonEvent(capture(found), eq(mockAuthentication), eq(false), eq(true));

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

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user");

        // when the JSON can't be converted to a Person POJO, Spring MVC will not even call our controller method.
        // so we can't raise a failure event.  None of our services should be called, so there are no
        // expectations.

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
        person.getPersonAssociations().add(perAssoc);
        Person saved = new Person();
        saved.setId(person.getId());

        ObjectMapper objectMapper = new ObjectMapper();
        String in = objectMapper.writeValueAsString(person);

        log.debug("Input JSON: " + in);

        Capture<Person> found = new Capture<>();

        expect(mockSaveTransaction.savePerson(capture(found), eq(mockAuthentication))).
                andThrow(new CannotCreateTransactionException("testException"));
        mockEventPublisher.publishPersonEvent(capture(found), eq(mockAuthentication), eq(false), eq(false));

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
