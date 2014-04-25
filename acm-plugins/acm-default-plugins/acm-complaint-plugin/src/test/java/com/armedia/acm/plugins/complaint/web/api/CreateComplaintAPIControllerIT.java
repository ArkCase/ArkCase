package com.armedia.acm.plugins.complaint.web.api;


import com.armedia.acm.plugins.complaint.model.Complaint;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public class CreateComplaintAPIControllerIT
{
    private MockMvc mockMvc;

    private CreateComplaintAPIController unit;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        unit = new CreateComplaintAPIController();
        mockMvc = MockMvcBuilders.standaloneSetup(unit).build();
    }

    @Test
    public void createComplaint() throws Exception
    {
        Complaint complaint = new Complaint();
        complaint.setComplaintId(500L);

        ObjectMapper objectMapper = new ObjectMapper();
        String in = objectMapper.writeValueAsString(complaint);

        log.debug("Input JSON: " + in);

        MvcResult result = mockMvc.perform(
            post("/api/latest/complaint.json")
                    .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(in))
                .andReturn();

        log.info("results: " + result.getResponse().getContentAsString());
    }
}
