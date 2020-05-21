package com.armedia.acm.tool.comprehendmedical;

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

import com.amazonaws.services.comprehendmedical.AWSComprehendMedical;
import com.amazonaws.services.comprehendmedical.AWSComprehendMedicalClient;
import com.amazonaws.services.comprehendmedical.model.*;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.armedia.acm.tool.comprehendmedical.model.AWSComprehendMedicalConfiguration;
import com.armedia.acm.tool.comprehendmedical.model.ComprehendMedicineDTO;
import com.armedia.acm.tool.comprehendmedical.service.AWSComprehendMedicalConfigurationService;
import com.armedia.acm.tool.comprehendmedical.service.AWSComprehendMedicalServiceImpl;
import com.armedia.acm.tool.mediaengine.model.MediaEngineStatusType;
import com.armedia.acm.tool.mediaengine.service.MediaEngineIntegrationEventPublisher;
import org.apache.commons.io.IOUtils;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 05/12/2020
 */
@RunWith(PowerMockRunner.class)
@PowerMockIgnore({ "javax.management.*", "javax.net.ssl.*" })
@PrepareForTest({ AWSComprehendMedicalServiceImpl.class, HttpClients.class, EntityUtils.class })
public class AWSComprehendMedicalServiceTest
{
    private AWSComprehendMedicalServiceImpl awsComprehendMedicalService;

    @Mock
    private AWSComprehendMedicalConfigurationService awsComprehendMedicalConfigurationService;

    @Mock
    private AmazonS3 s3Client;

    @Mock
    private AWSComprehendMedical comprehendMedicalClient;

    @Mock
    private FileInputStream inputStream;

    @Mock
    private MediaEngineIntegrationEventPublisher mediaEngineIntegrationEventPublisher;

    @Before
    public void setUp()
    {
        awsComprehendMedicalService = new AWSComprehendMedicalServiceImpl();
        awsComprehendMedicalService.setS3Client(s3Client);
        awsComprehendMedicalService.setAwsComprehendMedicalClient(comprehendMedicalClient);
        awsComprehendMedicalService.setAwsComprehendMedicalConfigurationService(awsComprehendMedicalConfigurationService);
        awsComprehendMedicalService.setMediaEngineIntegrationEventPublisher(mediaEngineIntegrationEventPublisher);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void create() throws Exception
    {
        Map<String, String> props = new HashMap<>();
        props.put("extension", ".txt");
        props.put("fileSize", "1000");
        props.put("mimeType", "text/plain");

        File file = mock(File.class);
        FileInputStream fileStream = mock(FileInputStream.class);

        ComprehendMedicineDTO comprehendMedicineDTO = new ComprehendMedicineDTO();
        comprehendMedicineDTO.setId(102L);
        comprehendMedicineDTO.setType("AUTOMATIC");
        comprehendMedicineDTO.setRemoteId("remote-id");
        comprehendMedicineDTO.setLanguage("en-US");
        comprehendMedicineDTO.setMediaEcmFileVersion(file);
        comprehendMedicineDTO.setProperties(props);

        AWSComprehendMedicalConfiguration configuration = new AWSComprehendMedicalConfiguration();
        configuration.setBucket("bucket");
        configuration.setRegion("region");
        configuration.setHost("host");
        configuration.setProfile("profile");

        when(awsComprehendMedicalConfigurationService.getAwsComprehendMedicalConfiguration()).thenReturn(configuration);
        PowerMockito.whenNew(FileInputStream.class).withArguments(file).thenReturn(fileStream);
        when(s3Client.doesObjectExist((String) configuration.getBucket(),
                comprehendMedicineDTO.getRemoteId() + "/" + comprehendMedicineDTO.getRemoteId())).thenReturn(false);
        when(s3Client.putObject(eq((String) configuration.getBucket()),
                eq(comprehendMedicineDTO.getRemoteId() + "/" + comprehendMedicineDTO.getRemoteId()), any(), any()))
                        .thenReturn(new PutObjectResult());
        when(comprehendMedicalClient.startEntitiesDetectionV2Job(any())).thenReturn(new StartEntitiesDetectionV2JobResult());

        awsComprehendMedicalService.create(comprehendMedicineDTO);

        verify(awsComprehendMedicalConfigurationService, times(3)).getAwsComprehendMedicalConfiguration();
        verify(s3Client).doesObjectExist((String) configuration.getBucket(),
                comprehendMedicineDTO.getRemoteId() + "/" + comprehendMedicineDTO.getRemoteId());
        verify(s3Client).putObject(eq((String) configuration.getBucket()),
                eq(comprehendMedicineDTO.getRemoteId() + "/" + comprehendMedicineDTO.getRemoteId()), any(), any());
        verify(comprehendMedicalClient).startEntitiesDetectionV2Job(any());
    }

    @Test
    public void get_COMPLETED() throws Exception
    {
        String remoteId = "comprehend-medicine-remote-id";
        String message = "";
        S3ObjectInputStream stream = new S3ObjectInputStream(getClass().getClassLoader().getResourceAsStream("aws/output/comprehendMedicalOutput.json"), null);
        String output = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("aws/output/comprehendMedicalOutput.json"));

        AWSComprehendMedicalConfiguration configuration = new AWSComprehendMedicalConfiguration();
        configuration.setBucket("bucket");
        configuration.setRegion("region");
        configuration.setHost("host");
        configuration.setProfile("profile");

        OutputDataConfig outputDataConfig = new OutputDataConfig();
        outputDataConfig.setS3Key("key");

        ComprehendMedicalAsyncJobProperties props = new ComprehendMedicalAsyncJobProperties();
        props.setJobStatus(JobStatus.COMPLETED.toString());
        props.setMessage(message);
        props.setOutputDataConfig(outputDataConfig);

        DescribeEntitiesDetectionV2JobResult result = new DescribeEntitiesDetectionV2JobResult();
        result.setComprehendMedicalAsyncJobProperties(props);

        S3Object obj = new S3Object();
        obj.setObjectContent(stream);

        when(comprehendMedicalClient.describeEntitiesDetectionV2Job(any())).thenReturn(result);
        when(awsComprehendMedicalConfigurationService.getAwsComprehendMedicalConfiguration()).thenReturn(configuration);
        when(s3Client.getObject(eq((String) configuration.getBucket()), eq(outputDataConfig.getS3Key() + remoteId + ".out"))).thenReturn(obj);

        Map<String, Object> properties = new HashMap<>();
        properties.put("jobId", "jobId");

        ComprehendMedicineDTO dto = (ComprehendMedicineDTO) awsComprehendMedicalService.get(remoteId, properties);

        verify(comprehendMedicalClient).describeEntitiesDetectionV2Job(any());
        verify(awsComprehendMedicalConfigurationService).getAwsComprehendMedicalConfiguration();
        verify(s3Client).getObject(eq((String) configuration.getBucket()), eq(outputDataConfig.getS3Key() + remoteId + ".out"));

        Assert.assertNotNull(dto);
        Assert.assertEquals(remoteId, dto.getRemoteId());
        Assert.assertEquals(MediaEngineStatusType.COMPLETED.toString(), dto.getStatus());
        Assert.assertEquals(message, dto.getMessage());
        Assert.assertEquals(output, dto.getOutput());
    }

    @Test
    public void get_PARTIAL_SUCCESS() throws Exception
    {
        String remoteId = "comprehend-medicine-remote-id";
        String message = "";
        S3ObjectInputStream stream = new S3ObjectInputStream(getClass().getClassLoader().getResourceAsStream("aws/output/comprehendMedicalOutput.json"), null);
        String output = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("aws/output/comprehendMedicalOutput.json"));

        AWSComprehendMedicalConfiguration configuration = new AWSComprehendMedicalConfiguration();
        configuration.setBucket("bucket");
        configuration.setRegion("region");
        configuration.setHost("host");
        configuration.setProfile("profile");

        OutputDataConfig outputDataConfig = new OutputDataConfig();
        outputDataConfig.setS3Key("key");

        ComprehendMedicalAsyncJobProperties props = new ComprehendMedicalAsyncJobProperties();
        props.setJobStatus(JobStatus.PARTIAL_SUCCESS.toString());
        props.setMessage(message);
        props.setOutputDataConfig(outputDataConfig);

        DescribeEntitiesDetectionV2JobResult result = new DescribeEntitiesDetectionV2JobResult();
        result.setComprehendMedicalAsyncJobProperties(props);

        S3Object obj = new S3Object();
        obj.setObjectContent(stream);

        when(comprehendMedicalClient.describeEntitiesDetectionV2Job(any())).thenReturn(result);
        when(awsComprehendMedicalConfigurationService.getAwsComprehendMedicalConfiguration()).thenReturn(configuration);
        when(s3Client.getObject(eq((String) configuration.getBucket()), eq(outputDataConfig.getS3Key() + remoteId + ".out"))).thenReturn(obj);

        Map<String, Object> properties = new HashMap<>();
        properties.put("jobId", "jobId");

        ComprehendMedicineDTO dto = (ComprehendMedicineDTO) awsComprehendMedicalService.get(remoteId, properties);

        verify(comprehendMedicalClient).describeEntitiesDetectionV2Job(any());
        verify(awsComprehendMedicalConfigurationService).getAwsComprehendMedicalConfiguration();
        verify(s3Client).getObject(eq((String) configuration.getBucket()), eq(outputDataConfig.getS3Key() + remoteId + ".out"));

        Assert.assertNotNull(dto);
        Assert.assertEquals(remoteId, dto.getRemoteId());
        Assert.assertEquals(MediaEngineStatusType.COMPLETED.toString(), dto.getStatus());
        Assert.assertEquals(message, dto.getMessage());
        Assert.assertEquals(output, dto.getOutput());
    }

    @Test
    public void get_SUBMITTED() throws Exception
    {
        String remoteId = "comprehend-medicine-remote-id";
        String message = "submitted";

        AWSComprehendMedicalConfiguration configuration = new AWSComprehendMedicalConfiguration();
        configuration.setBucket("bucket");
        configuration.setRegion("region");
        configuration.setHost("host");
        configuration.setProfile("profile");

        ComprehendMedicalAsyncJobProperties props = new ComprehendMedicalAsyncJobProperties();
        props.setJobStatus(JobStatus.SUBMITTED.toString());
        props.setMessage(message);

        DescribeEntitiesDetectionV2JobResult result = new DescribeEntitiesDetectionV2JobResult();
        result.setComprehendMedicalAsyncJobProperties(props);

        when(comprehendMedicalClient.describeEntitiesDetectionV2Job(any())).thenReturn(result);

        Map<String, Object> properties = new HashMap<>();
        properties.put("jobId", "jobId");

        ComprehendMedicineDTO dto = (ComprehendMedicineDTO) awsComprehendMedicalService.get(remoteId, properties);

        verify(comprehendMedicalClient).describeEntitiesDetectionV2Job(any());

        Assert.assertNotNull(dto);
        Assert.assertEquals(remoteId, dto.getRemoteId());
        Assert.assertEquals(MediaEngineStatusType.PROCESSING.toString(), dto.getStatus());
        Assert.assertEquals(message, dto.getMessage());
    }

    @Test
    public void get_IN_PROGRESS() throws Exception
    {
        String remoteId = "comprehend-medicine-remote-id";
        String message = "in progress";

        AWSComprehendMedicalConfiguration configuration = new AWSComprehendMedicalConfiguration();
        configuration.setBucket("bucket");
        configuration.setRegion("region");
        configuration.setHost("host");
        configuration.setProfile("profile");

        ComprehendMedicalAsyncJobProperties props = new ComprehendMedicalAsyncJobProperties();
        props.setJobStatus(JobStatus.IN_PROGRESS.toString());
        props.setMessage(message);

        DescribeEntitiesDetectionV2JobResult result = new DescribeEntitiesDetectionV2JobResult();
        result.setComprehendMedicalAsyncJobProperties(props);

        when(comprehendMedicalClient.describeEntitiesDetectionV2Job(any())).thenReturn(result);

        Map<String, Object> properties = new HashMap<>();
        properties.put("jobId", "jobId");

        ComprehendMedicineDTO dto = (ComprehendMedicineDTO) awsComprehendMedicalService.get(remoteId, properties);

        verify(comprehendMedicalClient).describeEntitiesDetectionV2Job(any());

        Assert.assertNotNull(dto);
        Assert.assertEquals(remoteId, dto.getRemoteId());
        Assert.assertEquals(MediaEngineStatusType.PROCESSING.toString(), dto.getStatus());
        Assert.assertEquals(message, dto.getMessage());
    }

    @Test
    public void get_FAILED() throws Exception
    {
        String remoteId = "comprehend-medicine-remote-id";
        String message = "failed";

        AWSComprehendMedicalConfiguration configuration = new AWSComprehendMedicalConfiguration();
        configuration.setBucket("bucket");
        configuration.setRegion("region");
        configuration.setHost("host");
        configuration.setProfile("profile");

        ComprehendMedicalAsyncJobProperties props = new ComprehendMedicalAsyncJobProperties();
        props.setJobStatus(JobStatus.FAILED.toString());
        props.setMessage(message);

        DescribeEntitiesDetectionV2JobResult result = new DescribeEntitiesDetectionV2JobResult();
        result.setComprehendMedicalAsyncJobProperties(props);

        when(comprehendMedicalClient.describeEntitiesDetectionV2Job(any())).thenReturn(result);

        Map<String, Object> properties = new HashMap<>();
        properties.put("jobId", "jobId");

        ComprehendMedicineDTO dto = (ComprehendMedicineDTO) awsComprehendMedicalService.get(remoteId, properties);

        verify(comprehendMedicalClient).describeEntitiesDetectionV2Job(any());

        Assert.assertNotNull(dto);
        Assert.assertEquals(remoteId, dto.getRemoteId());
        Assert.assertEquals(MediaEngineStatusType.FAILED.toString(), dto.getStatus());
        Assert.assertEquals(message, dto.getMessage());
    }

    @Test
    public void get_STOP_REQUESTED() throws Exception
    {
        String remoteId = "comprehend-medicine-remote-id";
        String message = "stop requested";

        AWSComprehendMedicalConfiguration configuration = new AWSComprehendMedicalConfiguration();
        configuration.setBucket("bucket");
        configuration.setRegion("region");
        configuration.setHost("host");
        configuration.setProfile("profile");

        ComprehendMedicalAsyncJobProperties props = new ComprehendMedicalAsyncJobProperties();
        props.setJobStatus(JobStatus.FAILED.toString());
        props.setMessage(message);

        DescribeEntitiesDetectionV2JobResult result = new DescribeEntitiesDetectionV2JobResult();
        result.setComprehendMedicalAsyncJobProperties(props);

        when(comprehendMedicalClient.describeEntitiesDetectionV2Job(any())).thenReturn(result);

        Map<String, Object> properties = new HashMap<>();
        properties.put("jobId", "jobId");

        ComprehendMedicineDTO dto = (ComprehendMedicineDTO) awsComprehendMedicalService.get(remoteId, properties);

        verify(comprehendMedicalClient).describeEntitiesDetectionV2Job(any());

        Assert.assertNotNull(dto);
        Assert.assertEquals(remoteId, dto.getRemoteId());
        Assert.assertEquals(MediaEngineStatusType.FAILED.toString(), dto.getStatus());
        Assert.assertEquals(message, dto.getMessage());
    }

    @Test
    public void get_STOPPED() throws Exception
    {
        String remoteId = "comprehend-medicine-remote-id";
        String message = "stopped";

        AWSComprehendMedicalConfiguration configuration = new AWSComprehendMedicalConfiguration();
        configuration.setBucket("bucket");
        configuration.setRegion("region");
        configuration.setHost("host");
        configuration.setProfile("profile");

        ComprehendMedicalAsyncJobProperties props = new ComprehendMedicalAsyncJobProperties();
        props.setJobStatus(JobStatus.FAILED.toString());
        props.setMessage(message);

        DescribeEntitiesDetectionV2JobResult result = new DescribeEntitiesDetectionV2JobResult();
        result.setComprehendMedicalAsyncJobProperties(props);

        when(comprehendMedicalClient.describeEntitiesDetectionV2Job(any())).thenReturn(result);

        Map<String, Object> properties = new HashMap<>();
        properties.put("jobId", "jobId");

        ComprehendMedicineDTO dto = (ComprehendMedicineDTO) awsComprehendMedicalService.get(remoteId, properties);

        verify(comprehendMedicalClient).describeEntitiesDetectionV2Job(any());

        Assert.assertNotNull(dto);
        Assert.assertEquals(remoteId, dto.getRemoteId());
        Assert.assertEquals(MediaEngineStatusType.FAILED.toString(), dto.getStatus());
        Assert.assertEquals(message, dto.getMessage());
    }

    public AWSComprehendMedicalConfigurationService getAwsComprehendMedicalConfigurationService()
    {
        return awsComprehendMedicalConfigurationService;
    }

    public void setAwsComprehendMedicalConfigurationService(AWSComprehendMedicalConfigurationService awsComprehendMedicalConfigurationService)
    {
        this.awsComprehendMedicalConfigurationService = awsComprehendMedicalConfigurationService;
    }
}
