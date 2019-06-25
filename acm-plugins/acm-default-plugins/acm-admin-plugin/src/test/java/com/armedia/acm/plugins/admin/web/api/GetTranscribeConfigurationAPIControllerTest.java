package com.armedia.acm.plugins.admin.web.api;

/*-
 * #%L
 * ACM Default Plugin: admin
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

import com.armedia.acm.services.mediaengine.exception.GetConfigurationException;
import com.armedia.acm.services.transcribe.model.TranscribeConfiguration;
import com.armedia.acm.services.transcribe.service.ArkCaseTranscribeService;
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

import java.math.BigDecimal;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 02/28/2018
 */
@RunWith(MockitoJUnitRunner.class)
public class GetTranscribeConfigurationAPIControllerTest extends EasyMockSupport
{
    private Logger LOG = LogManager.getLogger(getClass());

    private MockMvc mockMvc;

    @Mock
    private ArkCaseTranscribeService mockArkCaseTranscribeService;

    @Mock
    private Authentication mockAuthentication;

    @InjectMocks
    private GetTranscribeConfigurationAPIController controller;

    @Before
    public void setUp()
    {
        controller.setArkCaseTranscribeService(mockArkCaseTranscribeService);

        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void getConfiguration() throws Exception
    {
        TranscribeConfiguration configuration = new TranscribeConfiguration();
        configuration.setEnabled(true);
        configuration.setAutomaticEnabled(true);
        configuration.setNewMediaEngineForNewVersion(false);
        configuration.setCopyMediaEngineForNewVersion(true);
        configuration.setCost(new BigDecimal("0.001"));
        configuration.setConfidence(80);
        configuration.setNumberOfFilesForProcessing(10);
        configuration.setWordCountPerItem(30);
        configuration.setProvider("AWS");

        when(mockAuthentication.getName()).thenReturn("user");
        when(mockArkCaseTranscribeService.getConfiguration()).thenReturn(configuration);

        MvcResult result = mockMvc.perform(get("/api/v1/plugin/admin/transcribe/configuration")
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                .contentType(MediaType.APPLICATION_JSON)
                .principal(mockAuthentication))
                .andReturn();

        verify(mockAuthentication).getName();
        verify(mockArkCaseTranscribeService).getConfiguration();

        String responseString = result.getResponse().getContentAsString();
        LOG.info("Results: " + responseString);

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());

        ObjectMapper objectMapper = new ObjectMapper();
        TranscribeConfiguration responseConfiguration = objectMapper.readValue(responseString, TranscribeConfiguration.class);

        assertNotNull(responseConfiguration);
        assertEquals(configuration, responseConfiguration);
    }

    @Test
    public void getConfiguration_Exception() throws Exception
    {
        GetConfigurationException exception = new GetConfigurationException("error");

        when(mockAuthentication.getName()).thenReturn("user");
        when(mockArkCaseTranscribeService.getConfiguration()).thenThrow(exception);

        try
        {
            mockMvc.perform(get("/api/v1/plugin/admin/transcribe/configuration")
                    .accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .principal(mockAuthentication))
                    .andReturn();
        }
        catch (Exception e)
        {
            assertNotNull(e);
            assertTrue(e.getCause() instanceof GetConfigurationException);
            assertEquals("error", e.getCause().getMessage());
        }
    }
}
