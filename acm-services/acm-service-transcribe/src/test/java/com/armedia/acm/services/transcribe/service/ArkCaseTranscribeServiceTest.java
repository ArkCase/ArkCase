package com.armedia.acm.services.transcribe.service;

import com.armedia.acm.files.propertymanager.PropertyFileManager;
import com.armedia.acm.services.transcribe.exception.GetTranscribeConfigurationException;
import com.armedia.acm.services.transcribe.model.TranscribeConfiguration;
import com.armedia.acm.services.transcribe.model.TranscribeServiceProvider;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 03/01/2018
 */
@RunWith(MockitoJUnitRunner.class)
public class ArkCaseTranscribeServiceTest extends EasyMockSupport
{
    private ArkCaseTranscribeService arkCaseTranscribeService;
    private TranscribeConfigurationPropertiesService transcribeConfigurationPropertiesService;

    @Mock
    private PropertyFileManager propertyFileManager;

    @Before
    public void setUp()
    {
        transcribeConfigurationPropertiesService = new TranscribeConfigurationPropertiesService();
        transcribeConfigurationPropertiesService.setPropertyFileManager(propertyFileManager);

        arkCaseTranscribeService = new ArkCaseTranscribeService();
        arkCaseTranscribeService.setTranscribeConfigurationService(transcribeConfigurationPropertiesService);
    }

    @Test
    public void get() throws Exception
    {
        Map<String, Object> properties = new HashMap<>();
        properties.put("transcribe.enabled", "false");
        properties.put("transcribe.automatic.enabled", "false");
        properties.put("transcribe.new.transcribe.for.new.version", "false");
        properties.put("transcribe.copy.transcribe.for.new.version", "true");
        properties.put("transcribe.cost", "0.000000001");
        properties.put("transcribe.confidence", "80");
        properties.put("transcribe.number.of.files.for.processing", "10");
        properties.put("transcribe.word.count.per.item", "20");
        properties.put("transcribe.provider", "AWS");
        properties.put("transcribe.providers", "AWS");

        when(propertyFileManager.loadMultiple(any(), any())).thenReturn(properties);

        TranscribeConfiguration configuration = transcribeConfigurationPropertiesService.get();

        verify(propertyFileManager).loadMultiple(any(), any());

        assertNotNull(configuration);
        assertEquals(Integer.parseInt((String) properties.get("transcribe.confidence")), configuration.getConfidence());
        assertEquals(properties.get("transcribe.provider"), configuration.getProvider().toString());
        assertEquals(1, configuration.getProviders().size());
        assertEquals("AWS", configuration.getProviders().get(0).toString());
    }

    @Test
    public void get_Exception() throws Exception
    {
        // Setting "transcribe.cost" to some text will throw exception - invalid number format
        Map<String, Object> properties = new HashMap<>();
        properties.put("transcribe.enabled", "false");
        properties.put("transcribe.automatic.enabled", "false");
        properties.put("transcribe.new.transcribe.for.new.version", "false");
        properties.put("transcribe.copy.transcribe.for.new.version", "true");
        properties.put("transcribe.cost", "some text");
        properties.put("transcribe.confidence", "80");
        properties.put("transcribe.number.of.files.for.processing", "10");
        properties.put("transcribe.word.count.per.item", "20");
        properties.put("transcribe.provider", "AWS");
        properties.put("transcribe.providers", "AWS");

        when(propertyFileManager.loadMultiple(any(), any())).thenReturn(properties);

        try
        {
            transcribeConfigurationPropertiesService.get();
        }
        catch (Exception e)
        {
            verify(propertyFileManager).loadMultiple(any(), any());

            assertNotNull(e);
            assertTrue(e instanceof GetTranscribeConfigurationException);
            assertTrue(e.getCause() instanceof NumberFormatException);
        }
    }

    @Test
    public void save() throws Exception
    {
        List<TranscribeServiceProvider> providers = new ArrayList<>();
        providers.add(TranscribeServiceProvider.AWS);

        TranscribeConfiguration configuration = new TranscribeConfiguration();
        configuration.setEnabled(false);
        configuration.setAutomaticEnabled(false);
        configuration.setNewTranscriptionForNewVersion(false);
        configuration.setCopyTranscriptionForNewVersion(true);
        configuration.setCost(new BigDecimal("0.0000000001"));
        configuration.setConfidence(80);
        configuration.setNumberOfFilesForProcessing(10);
        configuration.setWordCountPerItem(20);
        configuration.setProvider(TranscribeServiceProvider.AWS);
        configuration.setProviders(providers);

        doNothing().when(propertyFileManager).storeMultiple(any(), any(), eq(false));

        TranscribeConfiguration saved = transcribeConfigurationPropertiesService.save(configuration);

        verify(propertyFileManager).storeMultiple(any(), any(), eq(false));

        assertNotNull(saved);
        assertEquals(configuration.getConfidence(), saved.getConfidence());
        assertEquals(configuration.getProvider().toString(), saved.getProvider().toString());
        assertEquals(configuration.getProviders().size(), saved.getProviders().size());
        assertEquals(configuration.getProviders().get(0).toString(), saved.getProviders().get(0).toString());
    }
}
