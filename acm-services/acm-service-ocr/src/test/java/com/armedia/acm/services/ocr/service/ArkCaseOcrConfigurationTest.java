package com.armedia.acm.services.ocr.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.armedia.acm.services.ocr.model.OCRConfiguration;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Arrays;

/**
 * Created by Vladimir Cherepnalkovski
 */
@RunWith(MockitoJUnitRunner.class)
public class ArkCaseOcrConfigurationTest extends EasyMockSupport
{
    private ArkCaseOCRServiceImpl arkCaseOCRService;

    @Mock
    private OCRConfigurationService ocrConfigurationService;

    @Mock
    private OCRConfiguration ocrConfiguration;

    @Before
    public void setUp()
    {
        arkCaseOCRService = new ArkCaseOCRServiceImpl();
        arkCaseOCRService.setOcrConfigurationService(ocrConfigurationService);
    }

    @Test
    public void isOCRon_true() throws Exception
    {
        ocrConfiguration = new OCRConfiguration();
        ocrConfiguration.setEnabled(true);

        when(ocrConfigurationService.loadProperties()).thenReturn(ocrConfiguration);

        boolean allow = arkCaseOCRService.isServiceEnabled();

        assertTrue(allow);
    }

    @Test
    public void isOCROn_false() throws Exception
    {
        ocrConfiguration = new OCRConfiguration();
        ocrConfiguration.setEnabled(false);

        when(ocrConfigurationService.loadProperties()).thenReturn(ocrConfiguration);

        boolean allow = arkCaseOCRService.isServiceEnabled();

        assertFalse(allow);
    }

    @Test
    public void isAutomaticOCROn_true() throws Exception
    {
        ocrConfiguration = new OCRConfiguration();
        ocrConfiguration.setAutomaticEnabled(true);

        when(ocrConfigurationService.loadProperties()).thenReturn(ocrConfiguration);

        boolean allow = arkCaseOCRService.isAutomaticOn();

        assertTrue(allow);
    }

    @Test
    public void isAutomaticOCROn_false() throws Exception
    {
        ocrConfiguration = new OCRConfiguration();
        ocrConfiguration.setAutomaticEnabled(false);

        when(ocrConfigurationService.loadProperties()).thenReturn(ocrConfiguration);

        boolean allow = arkCaseOCRService.isAutomaticOn();

        assertFalse(allow);
    }

    @Test
    public void saveConfiguration() throws Exception
    {
        OCRConfiguration configuration = new OCRConfiguration();
        configuration.setEnabled(false);
        configuration.setAutomaticEnabled(false);
        configuration.setNewMediaEngineForNewVersion(false);
        configuration.setCopyMediaEngineForNewVersion(true);
        configuration.setCost(new BigDecimal("0.0000000001"));
        configuration.setConfidence(80);
        configuration.setNumberOfFilesForProcessing(10);
        configuration.setProvider("TESSERACT");
        configuration.setProviders(Arrays.asList("TESSERACT"));
        configuration.setProviderPurgeAttempts(5);

        arkCaseOCRService.saveConfiguration(configuration);

        verify(ocrConfigurationService).saveProperties(configuration);
    }

    @Test
    public void getConfiguration() throws Exception
    {
        OCRConfiguration configuration = new OCRConfiguration();
        configuration.setEnabled(false);
        configuration.setAutomaticEnabled(false);
        configuration.setNewMediaEngineForNewVersion(false);
        configuration.setCopyMediaEngineForNewVersion(true);
        configuration.setCost(new BigDecimal("0.0000000001"));
        configuration.setConfidence(80);
        configuration.setNumberOfFilesForProcessing(10);
        configuration.setProvider("TESSERACT");
        configuration.setProviders(Arrays.asList("TESSERACT"));
        configuration.setProviderPurgeAttempts(5);

        when(ocrConfigurationService.loadProperties()).thenReturn(configuration);

        OCRConfiguration config = arkCaseOCRService.getConfiguration();

        verify(ocrConfigurationService).loadProperties();
        assertNotNull(config);
        assertEquals(configuration.getConfidence(), config.getConfidence());
        assertEquals(configuration.getProvider(), config.getProvider());
        assertEquals(1, config.getProviders().size());
    }

}
