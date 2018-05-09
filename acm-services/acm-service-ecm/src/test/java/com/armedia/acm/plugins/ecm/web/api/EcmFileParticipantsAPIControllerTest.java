package com.armedia.acm.plugins.ecm.web.api;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
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

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.plugins.ecm.service.impl.EcmFileParticipantService;
import com.armedia.acm.services.participants.model.AcmParticipant;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-web-acm-web.xml",
        "classpath:/spring/spring-library-ecm-plugin-test.xml"
})
public class EcmFileParticipantsAPIControllerTest extends EasyMockSupport
{
    private MockMvc mockMvc;
    private EcmFileParticipantsAPIController unit;

    private Authentication mockAuthentication;
    private EcmFileParticipantService mockFileParticipantService;

    @Autowired
    private ExceptionHandlerExceptionResolver filePluginExceptionResolver;

    @Before
    public void setUp() throws Exception
    {
        unit = new EcmFileParticipantsAPIController();
        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(filePluginExceptionResolver).build();

        mockAuthentication = createMock(Authentication.class);
        mockFileParticipantService = createMock(EcmFileParticipantService.class);

        unit.setFileParticipantService(mockFileParticipantService);
    }

    @Test
    public void testSaveParticipantsForFile() throws Exception
    {
        // given
        Long objectId = 1L;
        List<AcmParticipant> participants = new ArrayList<>(0);
        expect(mockAuthentication.getName()).andReturn("user");
        expect(mockFileParticipantService.setFileParticipants(objectId, participants)).andReturn(participants);
        String participantsJson = "[]";

        // when
        replayAll();
        MvcResult result = mockMvc.perform(
                post("/api/latest/service/ecm/participants/FILE/{objectId}", objectId)
                        .principal(mockAuthentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(participantsJson))
                .andExpect(status().isOk()).andReturn();

        // then
        verifyAll();
        List<AcmParticipant> returnedParticipants = ObjectConverter.createJSONUnmarshallerForTests()
                .unmarshallCollection(result.getResponse().getContentAsString(), List.class, AcmParticipant.class);
        assertTrue(returnedParticipants.size() == participants.size());

    }

    @Test
    public void testSaveParticipantsForFolder() throws Exception
    {
        // given
        Long objectId = 1L;
        List<AcmParticipant> participants = new ArrayList<>(0);
        expect(mockAuthentication.getName()).andReturn("user");
        expect(mockFileParticipantService.setFolderParticipants(objectId, participants)).andReturn(participants);
        String participantsJson = "[]";

        // when
        replayAll();
        MvcResult result = mockMvc.perform(
                post("/api/latest/service/ecm/participants/FOLDER/{objectId}", objectId)
                        .principal(mockAuthentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(participantsJson))
                .andExpect(status().isOk()).andReturn();

        // then
        verifyAll();
        List<AcmParticipant> returnedParticipants = ObjectConverter.createJSONUnmarshallerForTests()
                .unmarshallCollection(result.getResponse().getContentAsString(), List.class, AcmParticipant.class);
        assertTrue(returnedParticipants.size() == participants.size());

    }
}
