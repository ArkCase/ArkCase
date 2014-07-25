package com.armedia.acm.services.users.web.api;

import com.armedia.acm.core.AcmApplication;
import com.armedia.acm.core.AcmObjectType;
import com.armedia.acm.core.AcmParticipantType;
import com.armedia.acm.core.enums.AcmParticipantTypes;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Created by armdev on 7/24/14.
 */
public class FindParticipantTypesForObjectTypeNameAPIControllerTest
{
    private MockMvc mockMvc;

    private FindParticipantTypesForObjectTypeNameAPIController unit;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {

        unit = new FindParticipantTypesForObjectTypeNameAPIController();

        mockMvc = MockMvcBuilders.standaloneSetup(unit).build();
    }

    @Test
    public void participantTypesForObjectTypeName() throws Exception
    {
        String objectTypeName = "objectTypeName";

        AcmParticipantType participantType = new AcmParticipantType();
        participantType.setType(AcmParticipantTypes.SINGLE_USER);
        participantType.setName("Object Type Agent");
        participantType.setDescription("The Agent for this Object Type");
        participantType.setRequiredOnACL(true);

        AcmObjectType objectType = new AcmObjectType();
        objectType.setName(objectTypeName);
        objectType.setParticipantTypes(Arrays.asList(participantType));


        AcmApplication app = new AcmApplication();
        app.setBusinessObjects(Arrays.asList(objectType));

        unit.setAcmApplication(app);

        MvcResult result =
                mockMvc.perform(get("/api/latest/users/participantTypesForObjectTypeName/{objectTypeName}", objectTypeName)
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8"))
                        .andReturn();

        String jsonString = result.getResponse().getContentAsString();
        log.debug("Got JSON: " + jsonString);

        ObjectMapper om = new ObjectMapper();
        JavaType javaType = om.getTypeFactory().constructParametricType(List.class, AcmParticipantType.class);
        List<AcmParticipantType> found = om.readValue(jsonString, javaType);

        assertEquals(objectType.getParticipantTypes().size(), found.size());

        AcmParticipantType foundParticipantType = found.get(0);
        assertEquals(participantType.getName(), foundParticipantType.getName());


    }


}
