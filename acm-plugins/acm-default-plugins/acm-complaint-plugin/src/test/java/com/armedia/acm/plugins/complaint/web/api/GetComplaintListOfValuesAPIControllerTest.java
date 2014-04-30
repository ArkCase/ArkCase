package com.armedia.acm.plugins.complaint.web.api;

import com.armedia.acm.configuration.ListOfValuesService;
import com.armedia.acm.configuration.LookupTableDescriptor;
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

import java.util.Arrays;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

/**
 * Created by armdev on 4/28/14.
 */
public class GetComplaintListOfValuesAPIControllerTest extends EasyMockSupport
{
    private MockMvc mockMvc;

    private GetComplaintListOfValuesAPIController unit;

    private ListOfValuesService mockListOfValuesService;

    private Logger log = LoggerFactory.getLogger(getClass());

    private LookupTableDescriptor priorityDescriptor = new LookupTableDescriptor();
    private LookupTableDescriptor typeDescriptor = new LookupTableDescriptor();

    @Before
    public void setUp() throws Exception
    {
        unit = new GetComplaintListOfValuesAPIController();

        mockMvc = MockMvcBuilders.standaloneSetup(unit).build();
        mockListOfValuesService = createMock(ListOfValuesService.class);

        priorityDescriptor.setTableName("priorityTable");
        typeDescriptor.setTableName("typeTable");

        unit.setListOfValuesService(mockListOfValuesService);
        unit.setPriorityDescriptor(priorityDescriptor);
        unit.setTypesDescriptor(typeDescriptor);
    }

    @Test
    public void getComplaintTypes() throws Exception
    {
        List<String> typeList = Arrays.asList("Type 1", "Type 2", "Type 3");

        expect(mockListOfValuesService.lookupListOfStringValues(typeDescriptor)).andReturn(typeList);

        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/latest/plugin/complaint/types")
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andReturn();

        verifyAll();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE));

        String returned = result.getResponse().getContentAsString();

        log.info("results: " + returned);

        ObjectMapper objectMapper = new ObjectMapper();

        String types[] = objectMapper.readValue(returned, String[].class);

        assertEquals(3, types.length);


    }

    @Test
    public void getComplaintPriorities() throws Exception
    {
        List<String> priorityList = Arrays.asList("Cold", "Medium", "Hot");

        expect(mockListOfValuesService.lookupListOfStringValues(priorityDescriptor)).andReturn(priorityList);

        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/latest/plugin/complaint/priorities")
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andReturn();

        verifyAll();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE));

        String returned = result.getResponse().getContentAsString();

        log.info("results: " + returned);

        ObjectMapper objectMapper = new ObjectMapper();

        String priorities[] = objectMapper.readValue(returned, String[].class);

        assertEquals(3, priorities.length);


    }

}
