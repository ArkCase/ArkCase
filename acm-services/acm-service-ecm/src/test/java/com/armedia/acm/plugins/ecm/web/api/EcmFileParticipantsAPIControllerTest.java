package com.armedia.acm.plugins.ecm.web.api;

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
