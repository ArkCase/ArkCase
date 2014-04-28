package com.armedia.acm.plugins.complaint.web.api;


import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.service.SaveComplaintEventPublisher;
import com.armedia.acm.plugins.complaint.service.SaveComplaintTransaction;
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

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public class CreateComplaintAPIControllerTest extends EasyMockSupport
{
    private MockMvc mockMvc;

    private CreateComplaintAPIController unit;
    private SaveComplaintTransaction mockSaveTransaction;
    private SaveComplaintEventPublisher mockEventPublisher;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        unit = new CreateComplaintAPIController();
        mockMvc = MockMvcBuilders.standaloneSetup(unit).build();

        mockSaveTransaction = createMock(SaveComplaintTransaction.class);
        mockEventPublisher = createMock(SaveComplaintEventPublisher.class);

        unit.setComplaintTransaction(mockSaveTransaction);
        unit.setEventPublisher(mockEventPublisher);
    }

    @Test
    public void createComplaint() throws Exception
    {
        Complaint complaint = new Complaint();
        complaint.setComplaintId(500L);
        complaint.setComplaintType("complaintType");

        Complaint saved = new Complaint();
        saved.setComplaintId(complaint.getComplaintId());
        saved.setComplaintNumber("testNumber");

        ObjectMapper objectMapper = new ObjectMapper();
        String in = objectMapper.writeValueAsString(complaint);

        log.debug("Input JSON: " + in);

        Capture<Complaint> found = new Capture<>();

        // in a standalone mock MVC test we have no way to configure the request such that Spring MVC
        // will find the Authentication in the request, so as to call the controller method with a non-null
        // Authentication parameter; although I believe we could do it with a full webapp context test.
        // so we will just assume a null authentication.
        expect(mockSaveTransaction.saveComplaint(capture(found), isNull(Authentication.class))).andReturn(saved);
        mockEventPublisher.publishComplaintEvent(capture(found), isNull(Authentication.class), eq(false), eq(true));

        replayAll();

        MvcResult result = mockMvc.perform(
            post("/api/latest/plugin/complaint")
                    .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(in))
                .andReturn();

        log.info("results: " + result.getResponse().getContentAsString());

        verifyAll();

        assertEquals(saved, found.getValue());

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());

        String returned = result.getResponse().getContentAsString();

        Complaint mapped = objectMapper.readValue(returned, Complaint.class);
        assertEquals(saved.getComplaintNumber(), mapped.getComplaintNumber());

    }

    @Test
    public void invalidInput() throws Exception
    {
        String notComplaintJson = "{ \"user\": \"dmiller\" }";

        // should not be any calls made to our services
        replayAll();

        MvcResult result = mockMvc.perform(
                post("/api/latest/plugin/complaint")
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(notComplaintJson))
                .andReturn();



        log.info("results: " + result.getResponse().getContentAsString());
        log.info("result code: " + result.getResponse().getStatus());

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());

        log.info("failing results: " + result.getResponse().getContentAsString());



    }
}
