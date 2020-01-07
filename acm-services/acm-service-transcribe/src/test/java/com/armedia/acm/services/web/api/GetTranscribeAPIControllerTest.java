package com.armedia.acm.services.web.api;

/*-
 * #%L
 * ACM Service: Transcribe
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.armedia.acm.plugins.ecm.model.EcmFileVersion;
import com.armedia.acm.services.mediaengine.exception.GetMediaEngineException;
import com.armedia.acm.services.mediaengine.model.MediaEngineType;
import com.armedia.acm.services.transcribe.model.Transcribe;
import com.armedia.acm.services.transcribe.service.ArkCaseTranscribeServiceImpl;
import com.armedia.acm.services.transcribe.web.api.GetTranscribeAPIController;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 03/09/2018
 */
@RunWith(MockitoJUnitRunner.class)
public class GetTranscribeAPIControllerTest extends EasyMockSupport
{
    private Logger LOG = LogManager.getLogger(getClass());

    private MockMvc mockMvc;

    @Mock
    private ArkCaseTranscribeServiceImpl mockArkCaseTranscribeService;

    @Mock
    private Authentication mockAuthentication;

    @InjectMocks
    private GetTranscribeAPIController controller;

    @Before
    public void setUp()
    {
        controller.setArkCaseTranscribeService(mockArkCaseTranscribeService);

        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void getById() throws Exception
    {
        EcmFileVersion version = new EcmFileVersion();
        version.setId(102L);

        Transcribe transcribe = new Transcribe();
        transcribe.setId(101L);
        transcribe.setRemoteId("remoteId");
        transcribe.setType(MediaEngineType.AUTOMATIC.toString());
        transcribe.setStatus("status");
        transcribe.setMediaEcmFileVersion(version);
        transcribe.setProcessId("processId");
        transcribe.setLanguage("language");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        when(mockAuthentication.getName()).thenReturn("user");
        when(mockArkCaseTranscribeService.get(transcribe.getId())).thenReturn(transcribe);

        MvcResult result = mockMvc.perform(get("/api/v1/service/transcribe/{id}", transcribe.getId())
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                .contentType(MediaType.APPLICATION_JSON)
                .principal(mockAuthentication))
                .andReturn();

        verify(mockAuthentication).getName();
        verify(mockArkCaseTranscribeService).get(transcribe.getId());

        String responseString = result.getResponse().getContentAsString();
        LOG.info("Results: " + responseString);

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());

        Transcribe transcribeResult = objectMapper.readValue(responseString, Transcribe.class);

        assertNotNull(transcribeResult);
    }

    @Test
    public void getByMediaVersionId() throws Exception
    {
        EcmFileVersion version = new EcmFileVersion();
        version.setId(102L);

        Transcribe transcribe = new Transcribe();
        transcribe.setId(101L);
        transcribe.setRemoteId("remoteId");
        transcribe.setType(MediaEngineType.AUTOMATIC.toString());
        transcribe.setStatus("status");
        transcribe.setMediaEcmFileVersion(version);
        transcribe.setProcessId("processId");
        transcribe.setLanguage("language");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        when(mockAuthentication.getName()).thenReturn("user");
        when(mockArkCaseTranscribeService.getByMediaVersionId(version.getId())).thenReturn(transcribe);

        MvcResult result = mockMvc.perform(get("/api/v1/service/transcribe/media/{mediaVersionId}", version.getId())
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                .contentType(MediaType.APPLICATION_JSON)
                .principal(mockAuthentication))
                .andReturn();

        verify(mockAuthentication).getName();
        verify(mockArkCaseTranscribeService).getByMediaVersionId(version.getId());

        String responseString = result.getResponse().getContentAsString();
        LOG.info("Results: " + responseString);

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());

        Transcribe transcribeResult = objectMapper.readValue(responseString, Transcribe.class);

        assertNotNull(transcribeResult);
    }

    @Test
    public void getById_Exception() throws Exception
    {
        GetMediaEngineException exception = new GetMediaEngineException("error");

        EcmFileVersion version = new EcmFileVersion();
        version.setId(102L);

        Transcribe transcribe = new Transcribe();
        transcribe.setId(101L);
        transcribe.setRemoteId("remoteId");
        transcribe.setType(MediaEngineType.AUTOMATIC.toString());
        transcribe.setStatus("status");
        transcribe.setMediaEcmFileVersion(version);
        transcribe.setProcessId("processId");
        transcribe.setLanguage("language");

        when(mockAuthentication.getName()).thenReturn("user");
        when(mockArkCaseTranscribeService.get(transcribe.getId())).thenReturn(transcribe);

        try
        {
            mockMvc.perform(get("/api/v1/service/transcribe/{id}", transcribe.getId())
                    .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .principal(mockAuthentication))
                    .andReturn();
        }
        catch (Exception e)
        {
            assertNotNull(e);
            assertTrue(e.getCause() instanceof GetMediaEngineException);
            assertEquals(exception.getMessage(), e.getCause().getMessage());
        }
    }

    @Test
    public void getByMediaVersionId_Exception() throws Exception
    {
        GetMediaEngineException exception = new GetMediaEngineException("error");

        EcmFileVersion version = new EcmFileVersion();
        version.setId(102L);

        Transcribe transcribe = new Transcribe();
        transcribe.setId(101L);
        transcribe.setRemoteId("remoteId");
        transcribe.setType(MediaEngineType.AUTOMATIC.toString());
        transcribe.setStatus("status");
        transcribe.setMediaEcmFileVersion(version);
        transcribe.setProcessId("processId");
        transcribe.setLanguage("language");

        when(mockAuthentication.getName()).thenReturn("user");
        when(mockArkCaseTranscribeService.getByMediaVersionId(version.getId())).thenReturn(transcribe);

        try
        {
            mockMvc.perform(get("/api/v1/service/transcribe/media/{mediaVersionId}", version.getId())
                    .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .principal(mockAuthentication))
                    .andReturn();
        }
        catch (Exception e)
        {
            assertNotNull(e);
            assertTrue(e.getCause() instanceof GetMediaEngineException);
            assertEquals(exception.getMessage(), e.getCause().getMessage());
        }
    }
}
