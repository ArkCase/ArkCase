package com.armedia.acm.services.transcribe.web.api;

import com.armedia.acm.plugins.ecm.model.EcmFileVersion;
import com.armedia.acm.services.transcribe.exception.CreateTranscribeException;
import com.armedia.acm.services.transcribe.model.Transcribe;
import com.armedia.acm.services.transcribe.model.TranscribeType;
import com.armedia.acm.services.transcribe.service.ArkCaseTranscribeService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 03/09/2018
 */
@RunWith(MockitoJUnitRunner.class)
public class CreateTranscribeAPIControllerTest extends EasyMockSupport
{
    private Logger LOG = LoggerFactory.getLogger(getClass());

    private MockMvc mockMvc;

    @Mock
    private ArkCaseTranscribeService mockArkCaseTranscribeService;

    @Mock
    private Authentication mockAuthentication;

    @InjectMocks
    private CreateTranscribeAPIController controller;

    @Before
    public void setUp()
    {
        controller.setArkCaseTranscribeService(mockArkCaseTranscribeService);

        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void createAutomatic() throws Exception
    {
        EcmFileVersion version = new EcmFileVersion();
        version.setId(102l);

        Transcribe transcribe = new Transcribe();
        transcribe.setId(101l);
        transcribe.setRemoteId("remoteId");
        transcribe.setType(TranscribeType.AUTOMATIC.toString());
        transcribe.setStatus("status");
        transcribe.setMediaEcmFileVersion(version);
        transcribe.setProcessId("processId");
        transcribe.setLanguage("language");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        when(mockAuthentication.getName()).thenReturn("user");
        when(mockArkCaseTranscribeService.create(version.getId(), TranscribeType.AUTOMATIC)).thenReturn(transcribe);

        MvcResult result = mockMvc.perform(post("/api/v1/service/transcribe/{mediaVersionId}/automatic", version.getId())
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                .contentType(MediaType.APPLICATION_JSON)
                .principal(mockAuthentication))
                .andReturn();

        verify(mockAuthentication).getName();
        verify(mockArkCaseTranscribeService).create(version.getId(), TranscribeType.AUTOMATIC);

        String responseString = result.getResponse().getContentAsString();
        LOG.info("Results: " + responseString);

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());

        Transcribe transcribeResult = objectMapper.readValue(responseString, Transcribe.class);

        assertNotNull(transcribeResult);
    }

    @Test
    public void createAutomatic_Exception() throws Exception
    {

        CreateTranscribeException exception = new CreateTranscribeException("error");

        when(mockAuthentication.getName()).thenReturn("user");
        when(mockArkCaseTranscribeService.create(101l, TranscribeType.AUTOMATIC)).thenThrow(exception);

        try
        {
            mockMvc.perform(post("/api/v1/service/transcribe/{mediaVersionId}/automatic", 101l)
                    .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .principal(mockAuthentication))
                    .andReturn();
        }
        catch (Exception e)
        {
            assertNotNull(e);
            assertTrue(e.getCause() instanceof CreateTranscribeException);
            assertEquals("error", e.getCause().getMessage());
        }
    }
}
