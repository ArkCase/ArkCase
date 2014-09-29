package com.armedia.acm.plugins.person.web.api;

import com.armedia.acm.plugins.addressable.model.PostalAddress;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonAlias;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import com.armedia.acm.plugins.person.service.PersonAssociationEventPublisher;
import com.armedia.acm.plugins.person.service.SavePersonAssociationTransaction;
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
public class SavePersonAssociationAPIControllerTest extends EasyMockSupport
{
    private MockMvc mockMvc;

    private SavePersonAssociationAPIController unit;
    private SavePersonAssociationTransaction mockSaveTransaction;
    private PersonAssociationEventPublisher mockEventPublisher;
    private Authentication mockAuthentication;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        unit = new SavePersonAssociationAPIController();
        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(exceptionResolver).build();

        mockSaveTransaction = createMock(SavePersonAssociationTransaction.class);
        mockEventPublisher = createMock(PersonAssociationEventPublisher.class);
        mockAuthentication = createMock(Authentication.class);

        unit.setPersonAssociationTransaction(mockSaveTransaction);
        unit.setPersonAssociationEventPublisher(mockEventPublisher);
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

        log.debug("Input JSON: " + in);

        Capture<PersonAssociation> found = new Capture<>();

        expect(mockSaveTransaction.savePersonAsssociation(capture(found), eq(mockAuthentication))).andReturn(saved);
        mockEventPublisher.publishPersonAssociationEvent(capture(found), eq(mockAuthentication), eq(false), eq(true));

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user");

        replayAll();

        MvcResult result = mockMvc.perform(
            post("/api/latest/plugin/personAssociation")
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
        String notPersonAssociationJson = "{ \"user\": \"com\" }";

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user");

        // when the JSON can't be converted to a PersonAssociation POJO, Spring MVC will not even call our controller method.
        // so we can't raise a failure event.  None of our services should be called, so there are no
        // expectations.

        replayAll();

        mockMvc.perform(
                post("/api/latest/plugin/personAssociation")
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(mockAuthentication)
                        .content(notPersonAssociationJson))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN));

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

        log.debug("Input JSON: " + in);

        Capture<PersonAssociation> found = new Capture<>();

        expect(mockSaveTransaction.savePersonAsssociation(capture(found), eq(mockAuthentication))).
                andThrow(new CannotCreateTransactionException("testException"));
        mockEventPublisher.publishPersonAssociationEvent(capture(found), eq(mockAuthentication), eq(false), eq(false));

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user");

        replayAll();

        mockMvc.perform(
                post("/api/latest/plugin/personAssociation")
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(mockAuthentication)
                        .content(in))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN));

        verifyAll();

        assertEquals(perAssoc.getId(), found.getValue().getId());

    }

}
