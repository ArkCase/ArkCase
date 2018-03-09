package com.armedia.acm.plugins.admin.web.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.armedia.acm.services.transcribe.exception.GetTranscribeConfigurationException;
import com.armedia.acm.services.transcribe.model.TranscribeConfiguration;
import com.armedia.acm.services.transcribe.model.TranscribeServiceProvider;
import com.armedia.acm.services.transcribe.service.ArkCaseTranscribeService;
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

import java.math.BigDecimal;
import java.util.Arrays;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 02/28/2018
 */
@RunWith(MockitoJUnitRunner.class)
public class GetTranscribeConfigurationAPIControllerTest extends EasyMockSupport
{
    private Logger LOG = LoggerFactory.getLogger(getClass());

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
        configuration.setNewTranscriptionForNewVersion(false);
        configuration.setCopyTranscriptionForNewVersion(true);
        configuration.setCost(new BigDecimal("0.001"));
        configuration.setConfidence(80);
        configuration.setNumberOfFilesForProcessing(10);
        configuration.setWordCountPerItem(30);
        configuration.setProvider(TranscribeServiceProvider.AWS);
        configuration.setProviders(Arrays.asList(TranscribeServiceProvider.AWS));

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
        GetTranscribeConfigurationException exception = new GetTranscribeConfigurationException("error");

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
            assertTrue(e.getCause() instanceof GetTranscribeConfigurationException);
            assertEquals("error", e.getCause().getMessage());
        }
    }
}
