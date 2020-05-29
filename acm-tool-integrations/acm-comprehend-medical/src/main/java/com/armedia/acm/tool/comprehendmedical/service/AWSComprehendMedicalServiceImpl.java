package com.armedia.acm.tool.comprehendmedical.service;

/*-
 * #%L
 * acm-comprehend-medical
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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

import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.comprehendmedical.AWSComprehendMedical;
import com.amazonaws.services.comprehendmedical.AWSComprehendMedicalClientBuilder;
import com.amazonaws.services.comprehendmedical.model.*;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.armedia.acm.tool.comprehendmedical.credentials.AWSComprehendMedicalCredentialsProviderChain;
import com.armedia.acm.tool.comprehendmedical.model.AWSComprehendMedicalConfiguration;
import com.armedia.acm.tool.comprehendmedical.model.ComprehendMedicalConstants;
import com.armedia.acm.tool.comprehendmedical.model.ComprehendMedicineDTO;
import com.armedia.acm.tool.mediaengine.exception.CreateMediaEngineToolException;
import com.armedia.acm.tool.mediaengine.exception.GetMediaEngineToolException;
import com.armedia.acm.tool.mediaengine.model.MediaEngineDTO;
import com.armedia.acm.tool.mediaengine.model.MediaEngineStatusType;
import com.armedia.acm.tool.mediaengine.service.MediaEngineIntegrationEventPublisher;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 05/12/2020
 */
public class AWSComprehendMedicalServiceImpl implements ComprehendMedicalService
{
    private final Logger LOG = LogManager.getLogger(getClass());
    private AmazonS3 s3Client;
    private AWSComprehendMedical awsComprehendMedicalClient;
    private AWSComprehendMedicalConfigurationService awsComprehendMedicalConfigurationService;
    private AWSComprehendMedicalCredentialsConfigurationService awsComprehendMedicalCredentialsConfigurationService;
    private MediaEngineIntegrationEventPublisher mediaEngineIntegrationEventPublisher;

    public void init() {
        AWSComprehendMedicalConfiguration configuration = getAwsComprehendMedicalConfigurationService().getAwsComprehendMedicalConfiguration();

        AWSComprehendMedicalCredentialsProviderChain credentialsProviderChain = new AWSComprehendMedicalCredentialsProviderChain(
                getAwsComprehendMedicalCredentialsConfigurationService());

        s3Client = AmazonS3ClientBuilder.standard().withCredentials(credentialsProviderChain)
                .withRegion(Regions.fromName(configuration.getRegion())).build();

        awsComprehendMedicalClient = AWSComprehendMedicalClientBuilder.standard().withCredentials(credentialsProviderChain).withRegion(Regions.fromName(configuration.getRegion())).build();
    }

    @Override
    public MediaEngineDTO create(MediaEngineDTO mediaEngineDTO) throws CreateMediaEngineToolException
    {
        AWSComprehendMedicalConfiguration configuration = getAwsComprehendMedicalConfigurationService().getAwsComprehendMedicalConfiguration();

        checkIfMediaExist(mediaEngineDTO, configuration.getBucket());
        uploadMedia(mediaEngineDTO);
        StartEntitiesDetectionV2JobResult result = startComprehendMedicalJob(mediaEngineDTO);

        mediaEngineDTO.setJobId(result.getJobId());

        return mediaEngineDTO;
    }

    @Override
    public MediaEngineDTO get(String remoteId, Map<String, Object> props) throws GetMediaEngineToolException
    {
        if (StringUtils.isNotEmpty(remoteId) && props != null && props.containsKey("jobId") && StringUtils.isNotEmpty((String) props.get("jobId")))
        {
            try
            {
                DescribeEntitiesDetectionV2JobRequest request = new DescribeEntitiesDetectionV2JobRequest();
                request.setJobId((String) props.get("jobId"));

                DescribeEntitiesDetectionV2JobResult result = getAwsComprehendMedicalClient().describeEntitiesDetectionV2Job(request);

                String resultStatus = result.getComprehendMedicalAsyncJobProperties().getJobStatus();
                String message = result.getComprehendMedicalAsyncJobProperties().getMessage();

                ComprehendMedicineDTO comprehendMedicineDTO = new ComprehendMedicineDTO();
                if (JobStatus.COMPLETED.toString().equals(resultStatus) || JobStatus.PARTIAL_SUCCESS.toString().equals(resultStatus))
                {
                    comprehendMedicineDTO.setOutput(getOutput(remoteId, result.getComprehendMedicalAsyncJobProperties().getOutputDataConfig().getS3Key()));
                    comprehendMedicineDTO.setStatus(MediaEngineStatusType.COMPLETED.toString());
                    comprehendMedicineDTO.setRemoteId(remoteId);
                    comprehendMedicineDTO.setMessage(message);
                }
                else
                {
                    String status = MediaEngineStatusType.PROCESSING.toString();
                    switch (JobStatus.fromValue(resultStatus))
                    {
                        case SUBMITTED:
                        case IN_PROGRESS:
                            status = MediaEngineStatusType.PROCESSING.toString();
                            break;
                        case FAILED:
                        case STOP_REQUESTED:
                        case STOPPED:
                            status = MediaEngineStatusType.FAILED.toString();
                            break;
                    }
                    comprehendMedicineDTO.setMessage(message);
                    comprehendMedicineDTO.setStatus(status);
                    comprehendMedicineDTO.setRemoteId(remoteId);
                }

                return comprehendMedicineDTO;
            }
            catch (AmazonServiceException e)
            {
                throw new GetMediaEngineToolException(String.format("Unable to get comprehend medicine job on Amazon. REASON=[%s].", e.getMessage()),
                        e);
            }
        }
        throw new GetMediaEngineToolException("Unable to get comprehend medicine job on Amazon. Remote ID not provided.");
    }

    @Override
    public boolean purge(MediaEngineDTO mediaEngineDTO)
    {
        try
        {
            AWSComprehendMedicalConfiguration configuration = getAwsComprehendMedicalConfigurationService().getAwsComprehendMedicalConfiguration();
            String key = mediaEngineDTO.getRemoteId();

            ListObjectsRequest listObjectsRequest = new ListObjectsRequest()
                    .withBucketName(configuration.getBucket())
                    .withPrefix(key);

            ObjectListing objectListing = getS3Client().listObjects(listObjectsRequest);

            while (true)
            {
                if (objectListing == null) break;

                for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries())
                {
                    getS3Client().deleteObject(configuration.getBucket(), objectSummary.getKey());
                }

                if (objectListing.isTruncated())
                {
                    objectListing = getS3Client().listNextBatchOfObjects(objectListing);
                }
                else
                {
                    break;
                }
            }

            File tmpFile = mediaEngineDTO.getMediaEcmFileVersion();
            FileUtils.deleteQuietly(tmpFile);

            return true;
        }
        catch (Exception e)
        {
            LOG.error("Error while purging Transcribe information on Amazon side for Transcribe with REMOTE_ID=[{}]. REASON=[{}]",
                    mediaEngineDTO.getRemoteId(), e.getMessage());
            return false;
        }
    }

    private String getOutput(String remoteId, String destination)
    {
        AWSComprehendMedicalConfiguration configuration = getAwsComprehendMedicalConfigurationService().getAwsComprehendMedicalConfiguration();

        S3Object outputObject = getS3Client().getObject(configuration.getBucket(), destination + remoteId + ".out");
        S3ObjectInputStream inputStream = outputObject.getObjectContent();

        String output = "";
        try
        {
            output = IOUtils.toString(inputStream);
        }
        catch (IOException e)
        {
            LOG.error("Unable to take output from InputStream for comprehend medical with ID=[{}]", remoteId, e);
        }

        return output;
    }

    private void checkIfMediaExist(MediaEngineDTO mediaEngineDTO, String bucket) throws CreateMediaEngineToolException
    {
        boolean exist = false;

        try
        {
            exist = getS3Client().doesObjectExist(bucket, mediaEngineDTO.getRemoteId() + "/" + mediaEngineDTO.getRemoteId());
        }
        catch (Exception e)
        {
            String message = String.format("Unable to create Comprehend Medical. REASON=[%s].", e.getMessage());
            getMediaEngineIntegrationEventPublisher().publishFailedEvent(mediaEngineDTO, ComprehendMedicalConstants.COMPREHEND_MEDICAL_SYSTEM_USER, null,
                    true, ComprehendMedicalConstants.SERVICE, message);
            throw new CreateMediaEngineToolException(message, e);
        }

        if (exist)
        {
            String message = String.format("The file with KEY=[%s] already exist on Amazon.", mediaEngineDTO.getRemoteId());
            getMediaEngineIntegrationEventPublisher().publishFailedEvent(mediaEngineDTO, ComprehendMedicalConstants.COMPREHEND_MEDICAL_SYSTEM_USER, null,
                    true, ComprehendMedicalConstants.SERVICE, message);
            throw new CreateMediaEngineToolException(message);
        }
    }

    private PutObjectResult uploadMedia(MediaEngineDTO mediaEngineDTO) throws CreateMediaEngineToolException
    {
        if (mediaEngineDTO != null)
        {
            try (InputStream inputStream = new FileInputStream(mediaEngineDTO.getMediaEcmFileVersion()))
            {
                AWSComprehendMedicalConfiguration configuration = getAwsComprehendMedicalConfigurationService().getAwsComprehendMedicalConfiguration();

                String contentLength = mediaEngineDTO.getProperties().get("fileSize");
                String mimeType = mediaEngineDTO.getProperties().get("mimeType");

                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentLength(Long.valueOf(contentLength));
                metadata.setContentType(mimeType);

                return getS3Client().putObject(configuration.getBucket(), mediaEngineDTO.getRemoteId() + "/" + mediaEngineDTO.getRemoteId(), inputStream, metadata);
            }
            catch (Exception e)
            {
                String message = String.format("Unable to upload media file to Amazon. REASON=[%s].", e.getMessage());
                getMediaEngineIntegrationEventPublisher().publishFailedEvent(mediaEngineDTO, ComprehendMedicalConstants.COMPREHEND_MEDICAL_SYSTEM_USER,
                        null,
                        true, ComprehendMedicalConstants.SERVICE, message);
                throw new CreateMediaEngineToolException(
                        message, e);
            }
        }

        throw new CreateMediaEngineToolException("Unable to upload media file to Amazon. File not provided.");
    }

    private StartEntitiesDetectionV2JobResult startComprehendMedicalJob(MediaEngineDTO mediaEngineDTO) throws CreateMediaEngineToolException
    {
        if (mediaEngineDTO != null)
        {
            try
            {
                AWSComprehendMedicalConfiguration configuration = getAwsComprehendMedicalConfigurationService().getAwsComprehendMedicalConfiguration();

                InputDataConfig inputDataConfig = new InputDataConfig();
                inputDataConfig.setS3Bucket(configuration.getBucket());
                inputDataConfig.setS3Key(mediaEngineDTO.getRemoteId());

                OutputDataConfig outputDataConfig = new OutputDataConfig();
                outputDataConfig.setS3Bucket(configuration.getBucket());
                outputDataConfig.setS3Key(mediaEngineDTO.getRemoteId());

                StartEntitiesDetectionV2JobRequest request = new StartEntitiesDetectionV2JobRequest();
                request.setClientRequestToken(mediaEngineDTO.getRemoteId());
                request.setJobName(mediaEngineDTO.getRemoteId());
                request.setLanguageCode(mediaEngineDTO.getLanguage());
                request.setInputDataConfig(inputDataConfig);
                request.setOutputDataConfig(outputDataConfig);
                request.setDataAccessRoleArn(configuration.getArn());

                return getAwsComprehendMedicalClient().startEntitiesDetectionV2Job(request);
            }
            catch (Exception e)
            {
                String message = String.format("Unable to start comprehend medical job on Amazon. REASON=[%s]", e.getMessage());
                getMediaEngineIntegrationEventPublisher().publishFailedEvent(mediaEngineDTO, ComprehendMedicalConstants.COMPREHEND_MEDICAL_SYSTEM_USER,
                        null,
                        true, ComprehendMedicalConstants.SERVICE, message);
                throw new CreateMediaEngineToolException(
                        message, e);
            }
        }

        throw new CreateMediaEngineToolException("Unable to start comprehend medical job on Amazon. File not provided.");
    }

    public AmazonS3 getS3Client()
    {
        return s3Client;
    }

    public void setS3Client(AmazonS3 s3Client)
    {
        this.s3Client = s3Client;
    }

    public AWSComprehendMedical getAwsComprehendMedicalClient()
    {
        return awsComprehendMedicalClient;
    }

    public void setAwsComprehendMedicalClient(AWSComprehendMedical awsComprehendMedicalClient)
    {
        this.awsComprehendMedicalClient = awsComprehendMedicalClient;
    }

    public AWSComprehendMedicalConfigurationService getAwsComprehendMedicalConfigurationService()
    {
        return awsComprehendMedicalConfigurationService;
    }

    public void setAwsComprehendMedicalConfigurationService(AWSComprehendMedicalConfigurationService awsComprehendMedicalConfigurationService)
    {
        this.awsComprehendMedicalConfigurationService = awsComprehendMedicalConfigurationService;
    }

    public AWSComprehendMedicalCredentialsConfigurationService getAwsComprehendMedicalCredentialsConfigurationService()
    {
        return awsComprehendMedicalCredentialsConfigurationService;
    }

    public void setAwsComprehendMedicalCredentialsConfigurationService(AWSComprehendMedicalCredentialsConfigurationService awsComprehendMedicalCredentialsConfigurationService)
    {
        this.awsComprehendMedicalCredentialsConfigurationService = awsComprehendMedicalCredentialsConfigurationService;
    }

    public MediaEngineIntegrationEventPublisher getMediaEngineIntegrationEventPublisher()
    {
        return mediaEngineIntegrationEventPublisher;
    }

    public void setMediaEngineIntegrationEventPublisher(MediaEngineIntegrationEventPublisher mediaEngineIntegrationEventPublisher)
    {
        this.mediaEngineIntegrationEventPublisher = mediaEngineIntegrationEventPublisher;
    }
}
