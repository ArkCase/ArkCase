package com.armedia.acm.plugins.complaint.web.api;


import com.armedia.acm.plugins.addressable.model.PostalAddress;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.service.ComplaintEventPublisher;
import com.armedia.acm.plugins.complaint.service.SaveComplaintTransaction;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import org.codehaus.jackson.map.ObjectMapper;
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
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import java.util.Arrays;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-web-acm-web.xml",
        "classpath:/spring/spring-library-complaint-plugin-test.xml"
})
public class CreateComplaintAPIControllerTest extends EasyMockSupport
{
    private MockMvc mockMvc;

    private CreateComplaintAPIController unit;
    private SaveComplaintTransaction mockSaveTransaction;
    private ComplaintEventPublisher mockEventPublisher;
    private Authentication mockAuthentication;

    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        unit = new CreateComplaintAPIController();
        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(exceptionResolver).build();

        mockSaveTransaction = createMock(SaveComplaintTransaction.class);
        mockEventPublisher = createMock(ComplaintEventPublisher.class);
        mockAuthentication = createMock(Authentication.class);

        unit.setComplaintTransaction(mockSaveTransaction);
        unit.setEventPublisher(mockEventPublisher);
    }

    @Test
    public void createComplaint_saveExistingComplaint() throws Exception
    {
        Complaint complaint = new Complaint();
        complaint.setComplaintId(500L);
        complaint.setComplaintType("complaintType");

        Person person = new Person();
        person.setFamilyName("Jones");
        person.setGivenName("David");
        PostalAddress address = new PostalAddress();
        address.setCity("Falls Church");
        address.setState("VA");
        address.setStreetAddress("8221 Old Courthouse Road");
        person.getAddresses().add(address);

        PersonAssociation personAssoc = new PersonAssociation();
        personAssoc.setPerson(person);
        personAssoc.setPersonDescription("sample Description");
        personAssoc.setPersonType("Originator");

        complaint.setOriginator(personAssoc);
        
        complaint.setApprovers(Arrays.asList("user1", "user2"));

        Complaint saved = new Complaint();
        saved.setComplaintId(complaint.getComplaintId());
        saved.setComplaintNumber("testNumber");

        ObjectMapper objectMapper = new ObjectMapper();
        String in = objectMapper.writeValueAsString(complaint);

        log.debug("Input JSON: " + in);

        Capture<Complaint> found = new Capture<>();

        expect(mockSaveTransaction.saveComplaint(capture(found), eq(mockAuthentication))).andReturn(saved);
        mockEventPublisher.publishComplaintEvent(capture(found), eq(mockAuthentication), eq(false), eq(true));

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user");

        replayAll();

        MvcResult result = mockMvc.perform(
            post("/api/latest/plugin/complaint")
                    .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .principal(mockAuthentication)
                    .content(in))
                .andReturn();

        log.info("results: " + result.getResponse().getContentAsString());

        verifyAll();

        assertEquals(saved, found.getValue());

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());

        String returned = result.getResponse().getContentAsString();

        Complaint mapped = objectMapper.readValue(returned, Complaint.class);
        assertEquals(saved.getComplaintNumber(), mapped.getComplaintNumber());

        assertEquals(complaint.getApprovers(), saved.getApprovers());

    }

    @Test
    public void invalidInput() throws Exception
    {
        String notComplaintJson = "{ \"user\": \"dmiller\" }";

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user");

        // when the JSON can't be converted to a Complaint POJO, Spring MVC will not even call our controller method.
        // so we can't raise a failure event.  None of our services should be called, so there are no
        // expectations.

        replayAll();

        mockMvc.perform(
                post("/api/latest/plugin/complaint")
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(mockAuthentication)
                        .content(notComplaintJson))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN));

        verifyAll();

    }

    @Test
    public void createComplaint_exception() throws Exception
    {
        Complaint complaint = new Complaint();
        complaint.setComplaintId(500L);
        complaint.setComplaintType("complaintType");

        Person person = new Person();
        person.setFamilyName("Jones");
        person.setGivenName("David");
        PostalAddress address = new PostalAddress();
        address.setCity("Falls Church");
        address.setState("VA");
        address.setStreetAddress("8221 Old Courthouse Road");
        person.getAddresses().add(address);

        PersonAssociation personAssoc = new PersonAssociation();
        personAssoc.setPerson(person);
        personAssoc.setPersonDescription("sample Description");
        personAssoc.setPersonType("Originator");

        complaint.setOriginator(personAssoc);
        
        complaint.setApprovers(Arrays.asList("user1", "user2"));

        ObjectMapper objectMapper = new ObjectMapper();
        String in = objectMapper.writeValueAsString(complaint);

        log.debug("Input JSON: " + in);

        Capture<Complaint> found = new Capture<>();

        expect(mockSaveTransaction.saveComplaint(capture(found), eq(mockAuthentication))).
                andThrow(new CannotCreateTransactionException("testException"));
        mockEventPublisher.publishComplaintEvent(capture(found), eq(mockAuthentication), eq(false), eq(false));

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user");

        replayAll();

        mockMvc.perform(
                post("/api/latest/plugin/complaint")
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(mockAuthentication)
                        .content(in))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN));

        verifyAll();

        assertEquals(complaint.getComplaintId(), found.getValue().getComplaintId());

    }

}
