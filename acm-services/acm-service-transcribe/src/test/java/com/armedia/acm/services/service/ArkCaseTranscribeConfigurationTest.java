package com.armedia.acm.services.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.armedia.acm.services.transcribe.model.TranscribeConfiguration;
import com.armedia.acm.services.transcribe.service.ArkCaseTranscribeServiceImpl;
import com.armedia.acm.services.transcribe.service.TranscribeConfigurationService;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;

/**
 * Created by Vladimir Cherepnalkovski
 */
@RunWith(MockitoJUnitRunner.class)
public class ArkCaseTranscribeConfigurationTest extends EasyMockSupport
{
    private ArkCaseTranscribeServiceImpl arkCaseTranscribeService;

    @Mock
    private TranscribeConfigurationService transcribeConfigurationService;

    @Mock
    private TranscribeConfiguration transcribeConfiguration;

    @Before
    public void setUp()
    {
        arkCaseTranscribeService = new ArkCaseTranscribeServiceImpl();
        arkCaseTranscribeService.setTranscribeConfigurationService(transcribeConfigurationService);
    }

    @Test
    public void isTranscribeOn_true() throws Exception
    {
        transcribeConfiguration = new TranscribeConfiguration();
        transcribeConfiguration.setEnabled(true);

        when(transcribeConfigurationService.loadProperties()).thenReturn(transcribeConfiguration);

        boolean allow = arkCaseTranscribeService.isServiceEnabled();

        verify(transcribeConfigurationService).loadProperties();

        assertTrue(allow);
    }

    @Test
    public void isTranscribeOn_false() throws Exception
    {
        transcribeConfiguration = new TranscribeConfiguration();
        transcribeConfiguration.setEnabled(false);

        when(transcribeConfigurationService.loadProperties()).thenReturn(transcribeConfiguration);

        boolean allow = arkCaseTranscribeService.isServiceEnabled();

        verify(transcribeConfigurationService).loadProperties();

        assertFalse(allow);
    }

    @Test
    public void isAutomaticTranscribeOn_true() throws Exception
    {
        transcribeConfiguration = new TranscribeConfiguration();
        transcribeConfiguration.setEnabled(true);
        transcribeConfiguration.setAutomaticEnabled(true);

        when(transcribeConfigurationService.loadProperties()).thenReturn(transcribeConfiguration);

        boolean allow = arkCaseTranscribeService.isAutomaticOn();

        verify(transcribeConfigurationService).loadProperties();

        assertTrue(allow);
    }

    @Test
    public void isAutomaticTranscribeOn_false() throws Exception
    {
        transcribeConfiguration = new TranscribeConfiguration();
        transcribeConfiguration.setEnabled(true);
        transcribeConfiguration.setAutomaticEnabled(false);

        when(transcribeConfigurationService.loadProperties()).thenReturn(transcribeConfiguration);

        boolean allow = arkCaseTranscribeService.isAutomaticOn();

        verify(transcribeConfigurationService).loadProperties();

        assertFalse(allow);
    }

    @Test
    public void getConfiguration() throws Exception
    {
        transcribeConfiguration = new TranscribeConfiguration();
        transcribeConfiguration.setEnabled(true);
        transcribeConfiguration.setAutomaticEnabled(false);
        transcribeConfiguration.setConfidence(20);
        transcribeConfiguration.setProvider("AWS");

        when(transcribeConfigurationService.loadProperties()).thenReturn(transcribeConfiguration);

        TranscribeConfiguration configuration = arkCaseTranscribeService.getConfiguration();

        verify(transcribeConfigurationService).loadProperties();

        assertNotNull(configuration);
        assertEquals(transcribeConfiguration.getConfidence(), configuration.getConfidence());
        assertEquals(transcribeConfiguration.getProvider(), configuration.getProvider());
    }

    @Test
    public void saveConfiguration() throws Exception
    {
        TranscribeConfiguration configuration = new TranscribeConfiguration();
        configuration.setEnabled(false);
        configuration.setAutomaticEnabled(false);
        configuration.setNewMediaEngineForNewVersion(false);
        configuration.setCopyMediaEngineForNewVersion(true);
        configuration.setCost(new BigDecimal("0.0000000001"));
        configuration.setConfidence(80);
        configuration.setNumberOfFilesForProcessing(10);
        configuration.setWordCountPerItem(20);
        configuration.setProvider("AWS");
        configuration.setProviderPurgeAttempts(5);
        configuration.setAllowedMediaDuration(7200);
        configuration.setSilentBetweenWords(new BigDecimal("2"));

        arkCaseTranscribeService.saveConfiguration(configuration);

        verify(transcribeConfigurationService).saveProperties(configuration);
    }

}
