package com.armedia.acm.tool.transcribe;

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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.transcribe.AmazonTranscribe;
import com.amazonaws.services.transcribe.model.GetTranscriptionJobResult;
import com.amazonaws.services.transcribe.model.LimitExceededException;
import com.amazonaws.services.transcribe.model.StartTranscriptionJobResult;
import com.amazonaws.services.transcribe.model.Transcript;
import com.amazonaws.services.transcribe.model.TranscriptionJob;
import com.amazonaws.services.transcribe.model.TranscriptionJobStatus;
import com.armedia.acm.files.propertymanager.PropertyFileManager;
import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.tool.mediaengine.exception.CreateMediaEngineToolException;
import com.armedia.acm.tool.mediaengine.service.MediaEngineIntegrationEventPublisher;
import com.armedia.acm.tool.transcribe.model.AWSTranscribeConfiguration;
import com.armedia.acm.tool.transcribe.model.TranscribeDTO;
import com.armedia.acm.tool.transcribe.service.AWSTranscribeConfigurationService;
import com.armedia.acm.tool.transcribe.service.AWSTranscribeServiceImpl;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mule.api.MuleMessage;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 03/13/2018
 */
@RunWith(PowerMockRunner.class)
@PowerMockIgnore("javax.management.*")
@PrepareForTest(AWSTranscribeServiceImpl.class)
public class AWSTranscribeServiceTest
{
    private AWSTranscribeServiceImpl awsTranscribeService;

    @Mock
    private AWSTranscribeConfigurationService awsTranscribeConfigurationService;

    @Mock
    private PropertyFileManager propertyFileManager;

    @Mock
    private AmazonS3 s3Client;

    @Mock
    private AmazonTranscribe transcribeClient;

    @Mock
    private FileInputStream inputStream;

    @Mock
    private MediaEngineIntegrationEventPublisher mediaEngineIntegrationEventPublisher;

    @Mock
    private MuleContextManager muleContextManager;

    @Mock
    private MuleMessage muleMessage;

    @Before
    public void setUp()
    {
        awsTranscribeService = new AWSTranscribeServiceImpl();
        awsTranscribeService.setS3Client(s3Client);
        awsTranscribeService.setTranscribeClient(transcribeClient);
        awsTranscribeService.setAwsTranscribeConfigurationService(awsTranscribeConfigurationService);
        awsTranscribeService.setMediaEngineIntegrationEventPublisher(mediaEngineIntegrationEventPublisher);
        awsTranscribeService.setMuleContextManager(muleContextManager);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void create() throws Exception
    {
        Map<String, String> transcribeProps = new HashMap<>();
        transcribeProps.put("extension", ".mp4");
        transcribeProps.put("fileSize", "1000");
        transcribeProps.put("mimeType", "video/mp4");

        File file = mock(File.class);
        FileInputStream fileStream = mock(FileInputStream.class);

        TranscribeDTO transcribe = new TranscribeDTO();
        transcribe.setId(102L);
        transcribe.setType("AUTOMATIC");
        transcribe.setRemoteId("remote-id");
        transcribe.setLanguage("en-US");
        transcribe.setMediaEcmFileVersion(file);
        transcribe.setProperties(transcribeProps);

        AWSTranscribeConfiguration configuration = new AWSTranscribeConfiguration();
        configuration.setBucket("bucket");
        configuration.setRegion("region");
        configuration.setHost("host");
        configuration.setProfile("profile");

        when(awsTranscribeConfigurationService.getAWSTranscribeConfig()).thenReturn(configuration);
        PowerMockito.whenNew(FileInputStream.class).withArguments(file).thenReturn(fileStream);
        when(s3Client.doesObjectExist((String) configuration.getBucket(),
                transcribe.getRemoteId() + transcribe.getProperties().get("extension"))).thenReturn(false);
        when(s3Client.putObject(eq((String) configuration.getBucket()),
                eq(transcribe.getRemoteId() + transcribe.getProperties().get("extension")), any(), any()))
                        .thenReturn(new PutObjectResult());
        when(transcribeClient.startTranscriptionJob(any())).thenReturn(new StartTranscriptionJobResult());

        awsTranscribeService.create(transcribe);

        verify(awsTranscribeConfigurationService, times(3)).getAWSTranscribeConfig();
        verify(s3Client).doesObjectExist((String) configuration.getBucket(),
                transcribe.getRemoteId() + transcribe.getProperties().get("extension"));
        verify(s3Client).putObject(eq((String) configuration.getBucket()),
                eq(transcribe.getRemoteId() + transcribe.getProperties().get("extension")), any(), any());
        verify(transcribeClient).startTranscriptionJob(any());
    }

    @Test
    public void create_Amazon_Object_Already_Exist() throws Exception
    {

        Map<String, String> transcribeProps = new HashMap<>();
        transcribeProps.put("extension", ".mp4");
        transcribeProps.put("fileSize", "1000");
        transcribeProps.put("mimeType", "video/mp4");

        File file = mock(File.class);
        FileInputStream fileStream = mock(FileInputStream.class);

        TranscribeDTO transcribe = new TranscribeDTO();
        transcribe.setId(102L);
        transcribe.setType("AUTOMATIC");
        transcribe.setRemoteId("remote-id");
        transcribe.setMediaEcmFileVersion(file);
        transcribe.setProperties(transcribeProps);

        AWSTranscribeConfiguration configuration = new AWSTranscribeConfiguration();
        configuration.setBucket("bucket");
        configuration.setRegion("region");
        configuration.setHost("host");
        configuration.setProfile("profile");

        String key = transcribe.getRemoteId() + transcribe.getProperties().get("extension");
        String expectedErrorMessage = "The file with KEY=[" + key + "] already exist on Amazon.";

        when(awsTranscribeConfigurationService.getAWSTranscribeConfig()).thenReturn(configuration);
        when(s3Client.doesObjectExist((String) configuration.getBucket(),
                transcribe.getRemoteId() + transcribe.getProperties().get("extension"))).thenReturn(true);

        try
        {
            awsTranscribeService.create(transcribe);
        }
        catch (Exception e)
        {
            verify(awsTranscribeConfigurationService).getAWSTranscribeConfig();
            verify(s3Client).doesObjectExist((String) configuration.getBucket(),
                    transcribe.getRemoteId() + transcribe.getProperties().get("extension"));

            assertTrue(e instanceof CreateMediaEngineToolException);
            assertEquals(expectedErrorMessage, e.getMessage());
        }
    }

    @Test
    public void create_Error_While_Upload() throws Exception
    {
        Map<String, String> transcribeProps = new HashMap<>();
        transcribeProps.put("extension", ".mp4");
        transcribeProps.put("fileSize", "1000");
        transcribeProps.put("mimeType", "video/mp4");

        AWSTranscribeConfiguration configuration = new AWSTranscribeConfiguration();
        configuration.setBucket("bucket");
        configuration.setRegion("region");
        configuration.setHost("host");
        configuration.setProfile("profile");

        File file = mock(File.class);
        FileInputStream fileStream = mock(FileInputStream.class);

        TranscribeDTO transcribe = new TranscribeDTO();
        transcribe.setId(102L);
        transcribe.setType("AUTOMATIC");
        transcribe.setRemoteId("remote-id");
        transcribe.setMediaEcmFileVersion(file);
        transcribe.setProperties(transcribeProps);

        String expectedErrorMessage = "Unable to upload media file to Amazon. REASON=[error (Service: null; Status Code: 0; Error Code: null; Request ID: null)].";

        when(awsTranscribeConfigurationService.getAWSTranscribeConfig()).thenReturn(configuration);
        PowerMockito.whenNew(FileInputStream.class).withArguments(file).thenReturn(fileStream);
        when(s3Client.doesObjectExist((String) configuration.getBucket(),
                transcribe.getRemoteId() + transcribe.getProperties().get("extension"))).thenReturn(false);
        when(s3Client.putObject(eq((String) configuration.getBucket()),
                eq(transcribe.getRemoteId() + transcribe.getProperties().get("extension")), eq(inputStream), any()))
                        .thenThrow(new AmazonServiceException("error"));

        try
        {
            awsTranscribeService.create(transcribe);
        }
        catch (Exception e)
        {
            verify(awsTranscribeConfigurationService, times(1)).getAWSTranscribeConfig();
            verify(s3Client).doesObjectExist((String) configuration.getBucket(),
                    transcribe.getRemoteId() + transcribe.getProperties().get("extension"));
            verify(s3Client).putObject(eq((String) configuration.getBucket()),
                    eq(transcribe.getRemoteId() + transcribe.getProperties().get("extension")), any(),
                    any());

            assertTrue(e instanceof CreateMediaEngineToolException);
            assertEquals(expectedErrorMessage, e.getMessage());
        }
    }

    @Test
    public void create_Error_Start_Transcribe_Job() throws Exception
    {
        Map<String, String> transcribeProps = new HashMap<>();
        transcribeProps.put("extension", ".mp4");
        transcribeProps.put("fileSize", "1000");
        transcribeProps.put("mimeType", "video/mp4");

        AWSTranscribeConfiguration configuration = new AWSTranscribeConfiguration();
        configuration.setBucket("bucket");
        configuration.setRegion("region");
        configuration.setHost("host");
        configuration.setProfile("profile");

        File file = mock(File.class);
        FileInputStream fileStream = mock(FileInputStream.class);

        TranscribeDTO transcribe = new TranscribeDTO();
        transcribe.setId(102L);
        transcribe.setType("AUTOMATIC");
        transcribe.setRemoteId("remote-id");
        transcribe.setMediaEcmFileVersion(file);
        transcribe.setProperties(transcribeProps);

        String expectedErrorMessage = "Unable to start transcribe job on Amazon. REASON=[error (Service: null; Status Code: 0; Error Code: null; Request ID: null)]";

        when(awsTranscribeConfigurationService.getAWSTranscribeConfig()).thenReturn(configuration);
        PowerMockito.whenNew(FileInputStream.class).withArguments(file).thenReturn(fileStream);
        when(s3Client.doesObjectExist((String) configuration.getBucket(),
                transcribe.getRemoteId() + transcribe.getProperties().get("extension"))).thenReturn(false);
        when(s3Client.putObject(eq((String) configuration.getBucket()),
                eq(transcribe.getRemoteId() + transcribe.getProperties().get("extension")), eq(inputStream), any()))
                        .thenReturn(new PutObjectResult());
        when(transcribeClient.startTranscriptionJob(any())).thenThrow(new LimitExceededException("error"));

        try
        {
            awsTranscribeService.create(transcribe);
        }
        catch (Exception e)
        {
            verify(awsTranscribeConfigurationService, times(3)).getAWSTranscribeConfig();
            verify(s3Client).doesObjectExist((String) configuration.getBucket(),
                    transcribe.getRemoteId() + transcribe.getProperties().get("extension"));
            verify(s3Client).putObject(eq((String) configuration.getBucket()),
                    eq(transcribe.getRemoteId() + transcribe.getProperties().get("extension")), any(),
                    any());
            verify(transcribeClient).startTranscriptionJob(any());

            assertTrue(e instanceof CreateMediaEngineToolException);
            assertEquals(expectedErrorMessage, e.getMessage());
        }
    }

    @Test
    public void getMultipleSpeakers() throws Exception
    {
        String remoteId = "transcribe-remote-id";
        String jsonString = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("aws/output/asrOutputMultipleSpeakers.json"));

        Transcript transcript = new Transcript();
        transcript.setTranscriptFileUri("https://www.amazon.test.example.com");

        AWSTranscribeConfiguration configuration = new AWSTranscribeConfiguration();
        configuration.setShowSpeakerLabels(true);
        configuration.setMaxSpeakerLabels(10);

        TranscriptionJob transcriptionJob = new TranscriptionJob();
        transcriptionJob.setTranscriptionJobStatus(TranscriptionJobStatus.COMPLETED.toString());
        transcriptionJob.setTranscript(transcript);

        GetTranscriptionJobResult getTranscriptionJobResult = new GetTranscriptionJobResult();
        getTranscriptionJobResult.setTranscriptionJob(transcriptionJob);

        when(awsTranscribeConfigurationService.getAWSTranscribeConfig()).thenReturn(configuration);
        when(transcribeClient.getTranscriptionJob(any())).thenReturn(getTranscriptionJobResult);
        when(muleContextManager.send("vm://getProviderTranscribe.in", "www.amazon.test.example.com")).thenReturn(muleMessage);
        when(muleMessage.getInboundProperty("getProviderTranscribeException")).thenReturn(null);
        when(muleMessage.getPayloadAsString()).thenReturn(jsonString);

        Map<String, Object> props = new HashMap<>();
        props.put("wordCountPerItem", 20);
        props.put("silentBetweenWords", BigDecimal.valueOf(2));

        TranscribeDTO transcribe = (TranscribeDTO) awsTranscribeService.get(remoteId, props);

        verify(transcribeClient).getTranscriptionJob(any());
        verify(muleContextManager).send("vm://getProviderTranscribe.in", "www.amazon.test.example.com");
        verify(muleMessage).getInboundProperty("getProviderTranscribeException");
        verify(muleMessage).getPayloadAsString();

        assertNotNull(transcribe);
        assertNotNull(transcribe.getTranscribeItems());
        assertEquals(8, transcribe.getTranscribeItems().size());
        assertEquals(new BigDecimal("1.44"), transcribe.getTranscribeItems().get(0).getStartTime());
        assertEquals(new BigDecimal("8.45"), transcribe.getTranscribeItems().get(0).getEndTime());
        assertEquals(99, transcribe.getTranscribeItems().get(0).getConfidence());
        assertEquals("[spk_0]: welcome to English in a minute. Most of us know it's better to do or say something after we think",
                transcribe.getTranscribeItems().get(0).getText());
        assertEquals(new BigDecimal("56.01"), transcribe.getTranscribeItems().get(7).getStartTime());
        assertEquals(new BigDecimal("58.94"), transcribe.getTranscribeItems().get(7).getEndTime());
        assertEquals(99, transcribe.getTranscribeItems().get(7).getConfidence());
        assertEquals("[spk_0]: activity. And that's English in a minute.",
                transcribe.getTranscribeItems().get(7).getText());

        // There is one item plus, because of the speaker label
        assertEquals(21, transcribe.getTranscribeItems().get(0).getText().split(" ").length);

        // There is a silent between words, new item is created + speaker label
        assertEquals(11, transcribe.getTranscribeItems().get(4).getText().split(" ").length);

        // ... Last item length + speaker label
        assertEquals(8, transcribe.getTranscribeItems().get(7).getText().split(" ").length);
    }

    @Test
    public void getSingleSpeaker() throws Exception
    {
        String remoteId = "transcribe-remote-id";
        String jsonString = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("aws/output/asrOutputSingleSpeaker.json"));

        Transcript transcript = new Transcript();
        transcript.setTranscriptFileUri("https://www.amazon.test.example.com");

        TranscriptionJob transcriptionJob = new TranscriptionJob();
        transcriptionJob.setTranscriptionJobStatus(TranscriptionJobStatus.COMPLETED.toString());
        transcriptionJob.setTranscript(transcript);

        AWSTranscribeConfiguration configuration = new AWSTranscribeConfiguration();
        configuration.setShowSpeakerLabels(false);

        GetTranscriptionJobResult getTranscriptionJobResult = new GetTranscriptionJobResult();
        getTranscriptionJobResult.setTranscriptionJob(transcriptionJob);

        when(awsTranscribeConfigurationService.getAWSTranscribeConfig()).thenReturn(configuration);
        when(transcribeClient.getTranscriptionJob(any())).thenReturn(getTranscriptionJobResult);
        when(muleContextManager.send("vm://getProviderTranscribe.in", "www.amazon.test.example.com")).thenReturn(muleMessage);
        when(muleMessage.getInboundProperty("getProviderTranscribeException")).thenReturn(null);
        when(muleMessage.getPayloadAsString()).thenReturn(jsonString);

        Map<String, Object> props = new HashMap<>();
        props.put("wordCountPerItem", 20);
        props.put("silentBetweenWords", BigDecimal.valueOf(2));

        TranscribeDTO transcribe = (TranscribeDTO) awsTranscribeService.get(remoteId, props);

        verify(transcribeClient).getTranscriptionJob(any());
        verify(muleContextManager).send("vm://getProviderTranscribe.in", "www.amazon.test.example.com");
        verify(muleMessage).getInboundProperty("getProviderTranscribeException");
        verify(muleMessage).getPayloadAsString();

        assertNotNull(transcribe);
        assertNotNull(transcribe.getTranscribeItems());
        assertEquals(22, transcribe.getTranscribeItems().size());
        assertEquals(new BigDecimal("1.390"), transcribe.getTranscribeItems().get(0).getStartTime());
        assertEquals(new BigDecimal("7.720"), transcribe.getTranscribeItems().get(0).getEndTime());
        assertEquals(98, transcribe.getTranscribeItems().get(0).getConfidence());
        assertEquals("I've often said that i wish people could realize all their dreams and wealth fame and so that they could",
                transcribe.getTranscribeItems().get(0).getText());
        assertEquals(new BigDecimal("169.120"), transcribe.getTranscribeItems().get(21).getStartTime());
        assertEquals(new BigDecimal("177.730"), transcribe.getTranscribeItems().get(21).getEndTime());
        assertEquals(61, transcribe.getTranscribeItems().get(21).getConfidence());
        assertEquals("Wait", transcribe.getTranscribeItems().get(21).getText());

        assertEquals(props.get("wordCountPerItem"), transcribe.getTranscribeItems().get(0).getText().split(" ").length);

        // There is a silent between words, new item is created
        assertEquals(13, transcribe.getTranscribeItems().get(1).getText().split(" ").length);

        // ... Also at the end there is silent between words. New item is created
        assertEquals(1, transcribe.getTranscribeItems().get(21).getText().split(" ").length);
    }

    public AWSTranscribeConfigurationService getAwsTranscribeConfigurationService()
    {
        return awsTranscribeConfigurationService;
    }

    public void setAwsTranscribeConfigurationService(AWSTranscribeConfigurationService awsTranscribeConfigurationService)
    {
        this.awsTranscribeConfigurationService = awsTranscribeConfigurationService;
    }
}
