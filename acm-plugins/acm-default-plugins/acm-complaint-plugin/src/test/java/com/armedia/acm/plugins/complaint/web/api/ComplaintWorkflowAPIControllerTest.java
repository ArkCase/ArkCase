package com.armedia.acm.plugins.complaint.web.api;

import com.armedia.acm.plugins.addressable.model.PostalAddress;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.service.ComplaintEventPublisher;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.web.api.AcmSpringMvcErrorManager;
import org.codehaus.jackson.map.ObjectMapper;
import org.easymock.Capture;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

/**
 * Created by armdev on 5/30/14.
 */
public class ComplaintWorkflowAPIControllerTest extends EasyMockSupport
{
    private MockMvc mockMvc;
    private ComplaintEventPublisher mockEventPublisher;
    private AcmSpringMvcErrorManager mockErrorManager;
    private Authentication mockAuthentication;

    private ComplaintWorkflowAPIController unit;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        mockEventPublisher = createMock(ComplaintEventPublisher.class);
        mockErrorManager = createMock(AcmSpringMvcErrorManager.class);
        mockAuthentication = createMock(Authentication.class);

        unit = new ComplaintWorkflowAPIController();
        unit.setEventPublisher(mockEventPublisher);
        unit.setErrorManager(mockErrorManager);

        mockMvc = MockMvcBuilders.standaloneSetup(unit).build();


    }

    @Test
    public void startApprovalWorkflow() throws Exception
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

        ObjectMapper objectMapper = new ObjectMapper();
        String in = objectMapper.writeValueAsString(complaint);

        log.debug("Input JSON: " + in);

        Capture<Complaint> capturedComplaint = new Capture<>();

        mockEventPublisher.publishComplaintWorkflowEvent(
                capture(capturedComplaint),
                eq(mockAuthentication),
                eq("acm_ip_address"));

        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user");

        replayAll();

        MockHttpSession mockSession = new MockHttpSession();
        mockSession.setAttribute("acm_ip_address", "acm_ip_address");
        mockMvc.perform(
                post("/api/latest/plugin/complaint/workflow")
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(mockSession)
                        .principal(mockAuthentication)
                        .content(in))
                .andReturn();

        verifyAll();

        Complaint handled = capturedComplaint.getValue();

        assertEquals(complaint.getComplaintNumber(), handled.getComplaintNumber());
    }
}
