package com.armedia.acm.tool.transcribe.service;

/*-
 * #%L
 * acm-transcribe-tool
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
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
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.transcribe.AmazonTranscribe;
import com.amazonaws.services.transcribe.AmazonTranscribeClientBuilder;
import com.amazonaws.services.transcribe.model.GetTranscriptionJobRequest;
import com.amazonaws.services.transcribe.model.GetTranscriptionJobResult;
import com.amazonaws.services.transcribe.model.Media;
import com.amazonaws.services.transcribe.model.StartTranscriptionJobRequest;
import com.amazonaws.services.transcribe.model.StartTranscriptionJobResult;
import com.amazonaws.services.transcribe.model.TranscriptionJobStatus;
import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.tool.mediaengine.exception.CreateMediaEngineToolException;
import com.armedia.acm.tool.mediaengine.exception.GetConfigurationException;
import com.armedia.acm.tool.mediaengine.exception.GetMediaEngineToolException;
import com.armedia.acm.tool.mediaengine.model.MediaEngineDTO;
import com.armedia.acm.tool.mediaengine.model.MediaEngineStatusType;
import com.armedia.acm.tool.mediaengine.service.MediaEngineIntegrationEventPublisher;
import com.armedia.acm.tool.transcribe.credentials.ArkCaseAWSCredentialsProviderChain;
import com.armedia.acm.tool.transcribe.model.AWSTranscribeConfiguration;
import com.armedia.acm.tool.transcribe.model.TranscribeConstants;
import com.armedia.acm.tool.transcribe.model.TranscribeDTO;
import com.armedia.acm.tool.transcribe.model.TranscribeItemDTO;
import com.armedia.acm.tool.transcribe.model.transcript.AWSTranscript;
import com.armedia.acm.tool.transcribe.model.transcript.AWSTranscriptAlternative;
import com.armedia.acm.tool.transcribe.model.transcript.AWSTranscriptItem;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vladimir Cherepnalkovski
 */
public class AWSTranscribeServiceImpl implements TranscribeIntegrationService
{

    private static final String AWS_TRANSCRIBE_PLUGIN = "AWS_TRANSCRIBE";
    private final Logger LOG = LoggerFactory.getLogger(getClass());
    private AmazonS3 s3Client;
    private AmazonTranscribe transcribeClient;
    private MuleContextManager muleContextManager;
    private String credentialConfigurationFileLocation;
    private AWSTranscribeConfigurationPropertiesService awsTranscribeConfigurationPropertiesService;
    private MediaEngineIntegrationEventPublisher mediaEngineIntegrationEventPublisher;

    public static String extractMediaType(String mimeType)
    {
        if (mimeType != null && mimeType.contains("/") && !mimeType.endsWith("/") && mimeType.chars().filter(c -> c == '/').count() == 1)
        {
            return mimeType.substring(mimeType.indexOf("/") + 1, mimeType.length());
        }

        return "";
    }

    public void init() throws GetConfigurationException
    {
        AWSTranscribeConfiguration configuration = getConfiguration();
        ArkCaseAWSCredentialsProviderChain credentialsProviderChain = new ArkCaseAWSCredentialsProviderChain(
                getCredentialConfigurationFileLocation(), configuration.getProfile());
        s3Client = AmazonS3ClientBuilder.standard().withCredentials(credentialsProviderChain)
                .withRegion(Regions.fromName(configuration.getRegion())).build();
        transcribeClient = AmazonTranscribeClientBuilder.standard().withCredentials(credentialsProviderChain)
                .withRegion(Regions.fromName(configuration.getRegion())).build();
    }

    @Override
    @Async
    public void create(MediaEngineDTO mediaEngineDTO) throws CreateMediaEngineToolException
    {
        AWSTranscribeConfiguration configuration = null;

        try
        {
            configuration = getConfiguration();
        }
        catch (GetConfigurationException e)
        {
            getMediaEngineIntegrationEventPublisher().publishFailedEvent(mediaEngineDTO, TranscribeConstants.TRANSCRIBE_SYSTEM_USER, null,
                    true, TranscribeConstants.SERVICE);
            throw new CreateMediaEngineToolException(String.format("Transcribe failed to create on Amazon. REASON=[%s]", e.getMessage()),
                    e);
        }

        checkIfMediaExist(mediaEngineDTO, configuration.getBucket());
        uploadMedia(mediaEngineDTO);
        startTranscribeJob(mediaEngineDTO);
    }

    @Override
    public MediaEngineDTO get(String remoteId, String tempPath) throws GetMediaEngineToolException
    {
        if (StringUtils.isNotEmpty(remoteId))
        {
            try
            {
                GetTranscriptionJobRequest request = new GetTranscriptionJobRequest();
                request.setTranscriptionJobName(remoteId);

                GetTranscriptionJobResult result = getTranscribeClient().getTranscriptionJob(request);
                String resultStatus = result.getTranscriptionJob().getTranscriptionJobStatus();

                TranscribeDTO transcribeDTO = new TranscribeDTO();
                if (TranscriptionJobStatus.COMPLETED.toString().equals(resultStatus))
                {
                    transcribeDTO.setTranscribeItems(generateTranscribeItems(result));
                    transcribeDTO.setStatus(MediaEngineStatusType.COMPLETED.toString());
                    transcribeDTO.setRemoteId(remoteId);
                }
                else
                {
                    String status = MediaEngineStatusType.PROCESSING.toString();
                    switch (TranscriptionJobStatus.fromValue(resultStatus))
                    {
                    case IN_PROGRESS:
                        status = MediaEngineStatusType.PROCESSING.toString();
                        break;
                    case FAILED:
                        status = MediaEngineStatusType.FAILED.toString();
                        break;
                    }
                    transcribeDTO.setStatus(status);
                    transcribeDTO.setRemoteId(remoteId);
                }

                return transcribeDTO;
            }
            catch (AmazonServiceException | MuleException e)
            {
                throw new GetMediaEngineToolException(String.format("Unable to get transcribe job on Amazon. REASON=[%s].", e.getMessage()),
                        e);
            }
        }
        throw new GetMediaEngineToolException("Unable to get transcribe job on Amazon. Remote ID not provided.");
    }

    @Override
    public boolean purge(MediaEngineDTO mediaEngineDTO)
    {
        try
        {
            AWSTranscribeConfiguration configuration = getConfiguration();
            String key = mediaEngineDTO.getRemoteId() + mediaEngineDTO.getProperties().get("extension");
            getS3Client().deleteObject(configuration.getBucket(), key);

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

    private StartTranscriptionJobResult startTranscribeJob(MediaEngineDTO mediaEngineDTO) throws CreateMediaEngineToolException
    {
        if (mediaEngineDTO != null)
        {
            try
            {
                AWSTranscribeConfiguration configuration = getConfiguration();

                String mediaType = extractMediaType(mediaEngineDTO.getProperties().get("mimeType"));
                String key = mediaEngineDTO.getRemoteId() + mediaEngineDTO.getProperties().get("extension");

                Media media = new Media();
                media.setMediaFileUri(configuration.getHost() + "/" + configuration.getBucket() + "/" + key);

                StartTranscriptionJobRequest request = new StartTranscriptionJobRequest();
                request.setTranscriptionJobName(mediaEngineDTO.getRemoteId());
                request.setLanguageCode(mediaEngineDTO.getLanguage());
                request.setMediaFormat(mediaType);
                request.setMedia(media);

                return getTranscribeClient().startTranscriptionJob(request);
            }
            catch (Exception e)
            {
                getMediaEngineIntegrationEventPublisher().publishFailedEvent(mediaEngineDTO, TranscribeConstants.TRANSCRIBE_SYSTEM_USER,
                        null,
                        true, TranscribeConstants.SERVICE);
                throw new CreateMediaEngineToolException(
                        String.format("Unable to start transcribe job on Amazon. REASON=[%s]", e.getMessage()),
                        e);
            }
        }

        throw new CreateMediaEngineToolException("Unable to start transcribe job on Amazon. Transcribe not provided.");
    }

    private PutObjectResult uploadMedia(MediaEngineDTO mediaEngineDTO) throws CreateMediaEngineToolException
    {
        if (mediaEngineDTO != null)
        {
            try (InputStream inputStream = new FileInputStream(mediaEngineDTO.getMediaEcmFileVersion()))
            {
                AWSTranscribeConfiguration configuration = getConfiguration();

                String contentLength = mediaEngineDTO.getProperties().get("fileSize");
                String mimeType = mediaEngineDTO.getProperties().get("mimeType");

                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentLength(Long.valueOf(contentLength));
                metadata.setContentType(mimeType);

                String key = mediaEngineDTO.getRemoteId() + mediaEngineDTO.getProperties().get("extension");

                return getS3Client().putObject(configuration.getBucket(), key, inputStream, metadata);
            }
            catch (Exception e)
            {
                getMediaEngineIntegrationEventPublisher().publishFailedEvent(mediaEngineDTO, TranscribeConstants.TRANSCRIBE_SYSTEM_USER,
                        null,
                        true, TranscribeConstants.SERVICE);
                throw new CreateMediaEngineToolException(
                        String.format("Unable to upload media file to Amazon. REASON=[%s].", e.getMessage()),
                        e);
            }
        }

        throw new CreateMediaEngineToolException("Unable to upload media file to Amazon. Transcribe not provided.");
    }

    private void checkIfMediaExist(MediaEngineDTO mediaEngineDTO, String bucket) throws CreateMediaEngineToolException
    {
        String key = mediaEngineDTO.getRemoteId() + mediaEngineDTO.getProperties().get("extension");
        boolean exist = false;

        try
        {
            exist = getS3Client().doesObjectExist(bucket, key);
        }
        catch (Exception e)
        {
            getMediaEngineIntegrationEventPublisher().publishFailedEvent(mediaEngineDTO, TranscribeConstants.TRANSCRIBE_SYSTEM_USER, null,
                    true, TranscribeConstants.SERVICE);
            throw new CreateMediaEngineToolException(String.format("Unable to create Transcribe. REASON=[%s].", e.getMessage()), e);
        }

        if (exist)
        {
            getMediaEngineIntegrationEventPublisher().publishFailedEvent(mediaEngineDTO, TranscribeConstants.TRANSCRIBE_SYSTEM_USER, null,
                    true, TranscribeConstants.SERVICE);
            throw new CreateMediaEngineToolException(String.format("The file with KEY=[%s] already exist on Amazon.", key));
        }
    }

    private List<TranscribeItemDTO> generateTranscribeItems(GetTranscriptionJobResult result) throws MuleException
    {
        if (result != null && result.getTranscriptionJob().getTranscript() != null)
        {
            String url = result.getTranscriptionJob().getTranscript().getTranscriptFileUri();

            // Mule Flow MUST contain 'https://' string in the definition itself of the endpoint
            String urlWithoutProtocol = url.replace("https://", "");

            MuleMessage message = getMuleContextManager().send("vm://getProviderTranscribe.in", urlWithoutProtocol);

            MuleException muleException = message.getInboundProperty("getProviderTranscribeException");

            if (muleException != null)
            {
                throw muleException;
            }

            try
            {
                return convertJsonStringToListOfTranscribeItems(message.getPayloadAsString());
            }
            catch (Exception e)
            {
                LOG.error("Failed to convert Amazon JSON output to list of TranscribeItem objects. REASON=[{}]", e.getMessage(), e);
            }
        }

        return null;
    }

    private List<TranscribeItemDTO> convertJsonStringToListOfTranscribeItems(String jsonString)
    {
        List<TranscribeItemDTO> items = new ArrayList<>();

        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            AWSTranscript awsTranscript = objectMapper.readValue(jsonString, AWSTranscript.class);

            int counter = 0;
            BigDecimal startTime = null;
            BigDecimal endTime = null;
            BigDecimal confidence = new BigDecimal("0");
            int confidenceCounter = 0;
            String text = "";
            List<AWSTranscriptItem> awsTranscriptItems = awsTranscript.getResult().getItems();
            int size = awsTranscriptItems.size();
            boolean silentDetected = false;
            for (int i = 0; i < size; i++)
            {
                AWSTranscriptItem awsTranscriptItem = awsTranscriptItems.get(i);
                boolean punctuation = "punctuation".equalsIgnoreCase(awsTranscriptItem.getType());

                if (!punctuation && !silentDetected)
                {
                    if (startTime == null && awsTranscriptItem.getStartTime() != null)
                    {
                        startTime = new BigDecimal(awsTranscriptItem.getStartTime());
                    }

                    if (awsTranscriptItem.getEndTime() != null)
                    {
                        endTime = new BigDecimal(awsTranscriptItem.getEndTime());
                    }

                    counter++;
                }

                if (!silentDetected)
                {
                    silentDetected = isSilentBetweenWordsBiggerThanConfigured(i, size, awsTranscriptItems);
                }

                if (awsTranscriptItem.getAlternatives() != null && !awsTranscriptItem.getAlternatives().isEmpty())
                {
                    AWSTranscriptAlternative awsTranscriptAlternative = getBestAWSTranscriptAlternative(
                            awsTranscriptItem.getAlternatives());

                    if (awsTranscriptAlternative != null && awsTranscriptAlternative.getConfidence() != null)
                    {
                        BigDecimal confidenceAsBigDecimal = new BigDecimal(awsTranscriptAlternative.getConfidence());
                        confidence = confidence.add(confidenceAsBigDecimal);
                        confidenceCounter++;
                    }

                    String textDelimiter = !punctuation ? " " : "";
                    if (awsTranscriptAlternative != null && StringUtils.isNotEmpty(awsTranscriptAlternative.getContent()))
                    {
                        text = (text + textDelimiter + awsTranscriptAlternative.getContent()).trim();
                    }
                }

                if (counter >= TranscribeConstants.WORD_COUNT_PER_ITEM || i == size - 1 || silentDetected)
                {
                    if (!isNextPunctuation(i, size, awsTranscriptItems))
                    {
                        TranscribeItemDTO item = new TranscribeItemDTO();
                        item.setStartTime(startTime);
                        item.setEndTime(endTime);
                        item.setText(text);

                        int conf = confidence.multiply(new BigDecimal(100)).intValue() == 0 || confidenceCounter == 0 ? 0
                                : confidence.multiply(new BigDecimal(100)).intValue() / confidenceCounter;
                        item.setConfidence(conf);

                        items.add(item);

                        counter = 0;
                        startTime = null;
                        endTime = null;
                        confidence = new BigDecimal("0");
                        confidenceCounter = 0;
                        text = "";
                        silentDetected = false;
                    }
                }
            }
        }
        catch (IOException e)
        {
            LOG.warn("Could not create AWSTranscript object from JSON string. REASON=[{}], JSON=[{}]", e.getMessage(), jsonString);
        }

        return items;
    }

    private boolean isNextPunctuation(int i, int size, List<AWSTranscriptItem> awsTranscriptItems)
    {
        return (i <= size - 2 && awsTranscriptItems.get(i + 1) != null
                && "punctuation".equalsIgnoreCase(awsTranscriptItems.get(i + 1).getType()));
    }

    private boolean isSilentBetweenWordsBiggerThanConfigured(int i, int size, List<AWSTranscriptItem> awsTranscriptItems)
    {
        if (i <= size - 2 && awsTranscriptItems.get(i) != null && !"punctuation".equalsIgnoreCase(awsTranscriptItems.get(i).getType()))
        {
            int j = i;
            while (isNextPunctuation(j, size, awsTranscriptItems) && j <= size - 2)
            {
                j++;
            }
            if ((j + 1) < size && !"punctuation".equalsIgnoreCase(awsTranscriptItems.get(j + 1).getType()))
            {
                String currentEndTimeAsString = awsTranscriptItems.get(i).getEndTime();
                String nextStartTimeAsString = awsTranscriptItems.get(j + 1).getStartTime();
                BigDecimal silentBetweenWords = BigDecimal.valueOf(TranscribeConstants.SILENT_BETWEEN_WORDS);
                if (StringUtils.isNotEmpty(currentEndTimeAsString) && StringUtils.isNotEmpty(nextStartTimeAsString)
                        && silentBetweenWords != null)
                {
                    BigDecimal currentEndTime = new BigDecimal(currentEndTimeAsString);
                    BigDecimal nextStartTime = new BigDecimal(nextStartTimeAsString);
                    if (nextStartTime.subtract(currentEndTime).compareTo(silentBetweenWords) == 1)
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private AWSTranscriptAlternative getBestAWSTranscriptAlternative(List<AWSTranscriptAlternative> alternatives)
    {
        if (alternatives == null || alternatives.isEmpty())
        {
            return null;
        }

        if (alternatives.size() == 1)
        {
            return alternatives.get(0);
        }

        alternatives.sort((AWSTranscriptAlternative a1, AWSTranscriptAlternative a2) -> (toIntWithWholePrecision(a2.getConfidence())
                - toIntWithWholePrecision(a1.getConfidence())));

        return alternatives.get(0);
    }

    private int toIntWithWholePrecision(String confidence)
    {
        // Confidence is in the format with 4 digits after comma, for example 0.9878
        // Convert it to whole number with all digits to be able to use in lambda sort method
        if (StringUtils.isNotEmpty(confidence))
        {
            BigDecimal conf = new BigDecimal(confidence);
            return conf.multiply(new BigDecimal(10000)).intValue();
        }

        return 0;
    }

    public AWSTranscribeConfiguration getConfiguration() throws GetConfigurationException
    {
        return getAwsTranscribeConfigurationPropertiesService().get();
    }

    public AmazonS3 getS3Client()
    {
        return s3Client;
    }

    public void setS3Client(AmazonS3 s3Client)
    {
        this.s3Client = s3Client;
    }

    public AmazonTranscribe getTranscribeClient()
    {
        return transcribeClient;
    }

    public void setTranscribeClient(AmazonTranscribe transcribeClient)
    {
        this.transcribeClient = transcribeClient;
    }

    public MuleContextManager getMuleContextManager()
    {
        return muleContextManager;
    }

    public void setMuleContextManager(MuleContextManager muleContextManager)
    {
        this.muleContextManager = muleContextManager;
    }

    public String getCredentialConfigurationFileLocation()
    {
        return credentialConfigurationFileLocation;
    }

    public void setCredentialConfigurationFileLocation(String credentialConfigurationFileLocation)
    {
        this.credentialConfigurationFileLocation = credentialConfigurationFileLocation;
    }

    public AWSTranscribeConfigurationPropertiesService getAwsTranscribeConfigurationPropertiesService()
    {
        return awsTranscribeConfigurationPropertiesService;
    }

    public void setAwsTranscribeConfigurationPropertiesService(
            AWSTranscribeConfigurationPropertiesService awsTranscribeConfigurationPropertiesService)
    {
        this.awsTranscribeConfigurationPropertiesService = awsTranscribeConfigurationPropertiesService;
    }

    public String getName()
    {
        return AWS_TRANSCRIBE_PLUGIN;
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
