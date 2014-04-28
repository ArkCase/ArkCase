package com.armedia.acm.plugins.complaint.web.api;

import com.armedia.acm.pluginmanager.AcmPlugin;
import org.codehaus.jackson.map.ObjectMapper;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.Map;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

/**
 * Created by armdev on 4/28/14.
 */
public class GetComplaintTypesAPIControllerTest extends EasyMockSupport
{
    private MockMvc mockMvc;

    private GetComplaintTypesAPIController unit;

    private AcmPlugin mockComplaintPlugin;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        unit = new GetComplaintTypesAPIController();

        mockMvc = MockMvcBuilders.standaloneSetup(unit).build();
        mockComplaintPlugin = createMock(AcmPlugin.class);

        unit.setComplaintPlugin(mockComplaintPlugin);
    }

    @Test
    public void getComplaintTypes() throws Exception
    {
        Map<String, Object> complaintProps = new HashMap<>();
        complaintProps.put("complaint.complaintTypes", "Type 1,Type 2,Type 3");

        expect(mockComplaintPlugin.getPluginProperties()).andReturn(complaintProps);

        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/latest/plugin/complaint/types")
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andReturn();

        verifyAll();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE));

        String returned = result.getResponse().getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();
        String[] types = objectMapper.readValue(returned, String[].class);

        assertEquals(3, types.length);

        log.info("results: " + returned);
    }

}
