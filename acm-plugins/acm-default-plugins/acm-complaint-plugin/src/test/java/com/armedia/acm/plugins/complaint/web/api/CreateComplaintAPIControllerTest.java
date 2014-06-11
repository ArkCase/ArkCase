package com.armedia.acm.plugins.complaint.web.api;


import com.armedia.acm.plugins.addressable.model.PostalAddress;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.service.ComplaintEventPublisher;
import com.armedia.acm.plugins.complaint.service.SaveComplaintTransaction;
import com.armedia.acm.plugins.person.model.Person;
import org.codehaus.jackson.map.ObjectMapper;
import org.easymock.Capture;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public class CreateComplaintAPIControllerTest extends EasyMockSupport
{
    private MockMvc mockMvc;

    private CreateComplaintAPIController unit;
    private SaveComplaintTransaction mockSaveTransaction;
    private ComplaintEventPublisher mockEventPublisher;
    private Authentication mockAuthentication;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        unit = new CreateComplaintAPIController();
        mockMvc = MockMvcBuilders.standaloneSetup(unit).build();

        mockSaveTransaction = createMock(SaveComplaintTransaction.class);
        mockEventPublisher = createMock(ComplaintEventPublisher.class);
        mockAuthentication = createMock(Authentication.class);

        unit.setComplaintTransaction(mockSaveTransaction);
        unit.setEventPublisher(mockEventPublisher);
    }

    @Test
    public void createComplaint() throws Exception
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
        complaint.setOriginator(person);

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

        // should not be any calls made to our services
        replayAll();

        MvcResult result = mockMvc.perform(
                post("/api/latest/plugin/complaint")
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(mockAuthentication)
                        .content(notComplaintJson))
                .andReturn();



        log.info("results: " + result.getResponse().getContentAsString());
        log.info("result code: " + result.getResponse().getStatus());

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());

        log.info("failing results: " + result.getResponse().getContentAsString());



    }
}
