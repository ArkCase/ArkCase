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
import com.amazonaws.services.transcribe.model.Settings;
import com.amazonaws.services.transcribe.model.StartTranscriptionJobRequest;
import com.amazonaws.services.transcribe.model.StartTranscriptionJobResult;
import com.amazonaws.services.transcribe.model.TranscriptionJobStatus;
import com.armedia.acm.tool.mediaengine.exception.CreateMediaEngineToolException;
import com.armedia.acm.tool.mediaengine.exception.GetMediaEngineToolException;
import com.armedia.acm.tool.mediaengine.model.MediaEngineDTO;
import com.armedia.acm.tool.mediaengine.model.MediaEngineStatusType;
import com.armedia.acm.tool.mediaengine.service.MediaEngineIntegrationEventPublisher;
import com.armedia.acm.tool.transcribe.credentials.ArkCaseAWSCredentialsProviderChain;
import com.armedia.acm.tool.transcribe.model.AWSTranscribeConfiguration;
import com.armedia.acm.tool.transcribe.model.TranscribeConstants;
import com.armedia.acm.tool.transcribe.model.TranscribeDTO;
import com.armedia.acm.tool.transcribe.model.TranscribeItemDTO;
import com.armedia.acm.tool.transcribe.model.transcript.AWSSegment;
import com.armedia.acm.tool.transcribe.model.transcript.AWSSpeakerItem;
import com.armedia.acm.tool.transcribe.model.transcript.AWSTranscript;
import com.armedia.acm.tool.transcribe.model.transcript.AWSTranscriptAlternative;
import com.armedia.acm.tool.transcribe.model.transcript.AWSTranscriptItem;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Async;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Vladimir Cherepnalkovski
 */
public class AWSTranscribeServiceImpl implements TranscribeIntegrationService
{

    private static final String AWS_TRANSCRIBE_PLUGIN = "AWS_TRANSCRIBE";
    private static final String JOB_NOT_FOUND = "The requested job couldn't be found. Check the job name and try your request again.";
    private final Logger LOG = LogManager.getLogger(getClass());
    private AmazonS3 s3Client;
    private AmazonTranscribe transcribeClient;
    private MediaEngineIntegrationEventPublisher mediaEngineIntegrationEventPublisher;
    private AWSTranscribeConfigurationService awsTranscribeConfigurationService;
    private AWSTranscribeCredentialsConfigurationService awsTranscribeCredentialsConfigurationService;

    public static String extractMediaType(String mimeType)
    {
        if (mimeType != null && mimeType.contains("/") && !mimeType.endsWith("/") && mimeType.chars().filter(c -> c == '/').count() == 1)
        {
            return mimeType.substring(mimeType.indexOf("/") + 1, mimeType.length());
        }

        return "";
    }

    public void init()
    {
        AWSTranscribeConfiguration configuration = getAwsTranscribeConfigurationService().getAWSTranscribeConfig();

        ArkCaseAWSCredentialsProviderChain credentialsProviderChain = new ArkCaseAWSCredentialsProviderChain(
                getAwsTranscribeCredentialsConfigurationService());

        s3Client = AmazonS3ClientBuilder.standard().withCredentials(credentialsProviderChain)
                .withRegion(Regions.fromName(configuration.getRegion())).build();
        transcribeClient = AmazonTranscribeClientBuilder.standard().withCredentials(credentialsProviderChain)
                .withRegion(Regions.fromName(configuration.getRegion())).build();
    }

    @Override
    @Async
    public MediaEngineDTO create(MediaEngineDTO mediaEngineDTO) throws CreateMediaEngineToolException
    {
        AWSTranscribeConfiguration configuration = null;

        configuration = getAwsTranscribeConfigurationService().getAWSTranscribeConfig();

        checkIfMediaExist(mediaEngineDTO, configuration.getBucket());
        uploadMedia(mediaEngineDTO);
        startTranscribeJob(mediaEngineDTO);

        return mediaEngineDTO;
    }

    @Override
    public MediaEngineDTO get(String remoteId, Map<String, Object> props) throws GetMediaEngineToolException
    {
        if (StringUtils.isNotEmpty(remoteId))
        {
            TranscribeDTO transcribeDTO = new TranscribeDTO();
            try
            {
                GetTranscriptionJobRequest request = new GetTranscriptionJobRequest();
                request.setTranscriptionJobName(remoteId);

                GetTranscriptionJobResult result = getTranscribeClient().getTranscriptionJob(request);
                String resultStatus = result.getTranscriptionJob().getTranscriptionJobStatus();
                String failureReason = result.getTranscriptionJob().getFailureReason();

                if (TranscriptionJobStatus.COMPLETED.toString().equals(resultStatus))
                {
                    transcribeDTO.setTranscribeItems(generateTranscribeItems(result, props));
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
                    transcribeDTO.setMessage(failureReason);
                    transcribeDTO.setStatus(status);
                    transcribeDTO.setRemoteId(remoteId);
                }

                return transcribeDTO;
            }
            catch (AmazonServiceException e)
            {
                if (e.getStatusCode() == 400 && e.getErrorMessage().equalsIgnoreCase(JOB_NOT_FOUND))
                {
                    transcribeDTO.setMessage(e.getErrorMessage());
                    transcribeDTO.setStatus(MediaEngineStatusType.FAILED.toString());
                    transcribeDTO.setRemoteId(remoteId);

                    return transcribeDTO;
                }
                else
                {
                    throw new GetMediaEngineToolException(
                            String.format("Unable to get transcribe job on Amazon. REASON=[%s].", e.getMessage()),
                            e);
                }
            }
        }
        throw new GetMediaEngineToolException("Unable to get transcribe job on Amazon. Remote ID not provided.");
    }

    @Override
    public boolean purge(MediaEngineDTO mediaEngineDTO)
    {
        try
        {
            AWSTranscribeConfiguration configuration = getAwsTranscribeConfigurationService().getAWSTranscribeConfig();
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
                AWSTranscribeConfiguration configuration = getAwsTranscribeConfigurationService().getAWSTranscribeConfig();

                String mediaType = extractMediaType(mediaEngineDTO.getProperties().get("mimeType"));
                String key = mediaEngineDTO.getRemoteId() + mediaEngineDTO.getProperties().get("extension");

                Media media = new Media();
                media.setMediaFileUri(configuration.getHost() + "/" + configuration.getBucket() + "/" + key);

                StartTranscriptionJobRequest request = new StartTranscriptionJobRequest();
                request.setTranscriptionJobName(mediaEngineDTO.getRemoteId());
                request.setLanguageCode(mediaEngineDTO.getLanguage());
                request.setMediaFormat(mediaType);
                request.setMedia(media);

                // According to Amazon, valid range for MaxSpeakerLabels field is minimum value of 2,
                // maximum value of 10.
                if (configuration.isShowSpeakerLabels() && configuration.getMaxSpeakerLabels() > 1
                        && configuration.getMaxSpeakerLabels() < 11)
                {
                    Settings settings = new Settings();
                    settings.setShowSpeakerLabels(configuration.isShowSpeakerLabels());
                    settings.setMaxSpeakerLabels(configuration.getMaxSpeakerLabels());

                    request.setSettings(settings);
                }

                return getTranscribeClient().startTranscriptionJob(request);
            }
            catch (Exception e)
            {
                String message = String.format("Unable to start transcribe job on Amazon. REASON=[%s]", e.getMessage());
                getMediaEngineIntegrationEventPublisher().publishFailedEvent(mediaEngineDTO, TranscribeConstants.TRANSCRIBE_SYSTEM_USER,
                        null,
                        true, TranscribeConstants.SERVICE, message);
                throw new CreateMediaEngineToolException(
                        message, e);
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
                AWSTranscribeConfiguration configuration = getAwsTranscribeConfigurationService().getAWSTranscribeConfig();

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
                String message = String.format("Unable to upload media file to Amazon. REASON=[%s].", e.getMessage());
                getMediaEngineIntegrationEventPublisher().publishFailedEvent(mediaEngineDTO, TranscribeConstants.TRANSCRIBE_SYSTEM_USER,
                        null,
                        true, TranscribeConstants.SERVICE, message);
                throw new CreateMediaEngineToolException(
                        message, e);
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
            String message = String.format("Unable to create Transcribe. REASON=[%s].", e.getMessage());
            getMediaEngineIntegrationEventPublisher().publishFailedEvent(mediaEngineDTO, TranscribeConstants.TRANSCRIBE_SYSTEM_USER, null,
                    true, TranscribeConstants.SERVICE, message);
            throw new CreateMediaEngineToolException(message, e);
        }

        if (exist)
        {
            String message = String.format("The file with KEY=[%s] already exist on Amazon.", key);
            getMediaEngineIntegrationEventPublisher().publishFailedEvent(mediaEngineDTO, TranscribeConstants.TRANSCRIBE_SYSTEM_USER, null,
                    true, TranscribeConstants.SERVICE, message);
            throw new CreateMediaEngineToolException(message);
        }
    }

    private List<TranscribeItemDTO> generateTranscribeItems(GetTranscriptionJobResult result, Map<String, Object> props)
    {
        if (result != null && result.getTranscriptionJob().getTranscript() != null)
        {
            String url = result.getTranscriptionJob().getTranscript().getTranscriptFileUri();
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpGet request = new HttpGet(url);

            try (CloseableHttpResponse response = httpClient.execute(request))
            {
                LOG.debug("Response status from Amazon S3 = [{}]", response.getStatusLine().toString());

                HttpEntity entity = response.getEntity();
                String resultTr = EntityUtils.toString(entity);

                return convertJsonStringToListOfTranscribeItems(resultTr, props);
            }
            catch (Exception e)
            {
                LOG.debug("Unable to get transcribe job on Amazon. REASON=[%s].", e.getMessage(), e);
            }
        }

        return null;
    }

    private List<TranscribeItemDTO> convertJsonStringToListOfTranscribeItems(String jsonString, Map<String, Object> props)
    {
        List<TranscribeItemDTO> items = new ArrayList<>();
        Integer wordCountPerItem = (Integer) props.get("wordCountPerItem");

        BigDecimal silentBetweenWords = (BigDecimal) props.get("silentBetweenWords");

        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            AWSTranscript awsTranscript = objectMapper.readValue(jsonString, AWSTranscript.class);

            AWSTranscribeConfiguration configuration = getAwsTranscribeConfigurationService().getAWSTranscribeConfig();

            List<AWSSpeakerItem> speakerItems = new ArrayList<>();
            List<AWSSegment> segments;
            if (configuration.isShowSpeakerLabels())
            {
                segments = awsTranscript.getResult().getSpeakerLabels().getSegments();
                for (AWSSegment segment : segments)
                {
                    speakerItems.addAll(segment.getItems());
                }
            }

            int counter = 0;
            BigDecimal startTime = null;
            BigDecimal endTime = null;
            BigDecimal confidence = new BigDecimal("0");
            int confidenceCounter = 0;
            String text = "";
            List<AWSTranscriptItem> awsTranscriptItems = awsTranscript.getResult().getItems();
            int size = awsTranscriptItems.size();
            boolean silentDetected = false;
            String prevSpeaker = "";
            String currentSpeaker = "";
            String currentSpeakerText = "";
            for (int i = 0; i < size; i++)
            {
                AWSTranscriptItem awsTranscriptItem = awsTranscriptItems.get(i);
                boolean punctuation = "punctuation".equalsIgnoreCase(awsTranscriptItem.getType());

                for (AWSSpeakerItem speakerItem : speakerItems)
                {
                    if (!punctuation && awsTranscriptItem.getStartTime().equals(speakerItem.getStartTime())
                            && awsTranscriptItem.getEndTime().equals(speakerItem.getEndTime()))
                    {
                        currentSpeaker = speakerItem.getSpeakerLabel();
                        if (currentSpeaker.equals(prevSpeaker))
                        {
                            currentSpeakerText = "";
                        }
                        else
                        {
                            prevSpeaker = currentSpeaker;
                            currentSpeakerText = " [" + currentSpeaker + "]:";
                        }
                        break;
                    }
                }

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
                    silentDetected = isSilentBetweenWordsBiggerThanConfigured(i, size, awsTranscriptItems, silentBetweenWords);
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
                        text = (text + currentSpeakerText + textDelimiter + awsTranscriptAlternative.getContent()).trim();
                        currentSpeakerText = "";
                    }
                }

                if (counter >= wordCountPerItem || i == size - 1 || silentDetected)
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
                        prevSpeaker = "";
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

    private boolean isSilentBetweenWordsBiggerThanConfigured(int i, int size, List<AWSTranscriptItem> awsTranscriptItems,
            BigDecimal silentBetweenWords)
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

    public AWSTranscribeConfigurationService getAwsTranscribeConfigurationService()
    {
        return awsTranscribeConfigurationService;
    }

    public void setAwsTranscribeConfigurationService(AWSTranscribeConfigurationService awsTranscribeConfigurationService)
    {
        this.awsTranscribeConfigurationService = awsTranscribeConfigurationService;
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

    public AWSTranscribeCredentialsConfigurationService getAwsTranscribeCredentialsConfigurationService()
    {
        return awsTranscribeCredentialsConfigurationService;
    }

    public void setAwsTranscribeCredentialsConfigurationService(
            AWSTranscribeCredentialsConfigurationService awsTranscribeCredentialsConfigurationService)
    {
        this.awsTranscribeCredentialsConfigurationService = awsTranscribeCredentialsConfigurationService;
    }
}
