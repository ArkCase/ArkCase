package com.armedia.acm.services.users.web.api;

/*-
 * #%L
 * ACM Service: Users
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.armedia.acm.core.AcmApplication;
import com.armedia.acm.core.AcmObjectType;
import com.armedia.acm.core.AcmParticipantType;
import com.armedia.acm.core.enums.AcmParticipantTypes;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Test;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

/**
 * Created by armdev on 7/24/14.
 */
public class FindParticipantTypesForObjectTypeNameAPIControllerTest
{
    private MockMvc mockMvc;

    private FindParticipantTypesForObjectTypeNameAPIController unit;

    private Logger log = LogManager.getLogger(getClass());

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

        MvcResult result = mockMvc.perform(get("/api/latest/users/participantTypesForObjectTypeName/{objectTypeName}", objectTypeName)
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
