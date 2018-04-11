package com.armedia.acm.services.transcribe.provider.aws.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.transcribe.AmazonTranscribe;
import com.amazonaws.services.transcribe.AmazonTranscribeClientBuilder;
import com.amazonaws.services.transcribe.model.*;
import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;
import com.armedia.acm.plugins.ecm.service.EcmFileTransaction;
import com.armedia.acm.services.transcribe.exception.CreateTranscribeException;
import com.armedia.acm.services.transcribe.exception.GetConfigurationException;
import com.armedia.acm.services.transcribe.exception.GetTranscribeException;
import com.armedia.acm.services.transcribe.exception.SaveConfigurationException;
import com.armedia.acm.services.transcribe.model.*;
import com.armedia.acm.services.transcribe.provider.aws.credentials.ArkCaseAWSCredentialsProviderChain;
import com.armedia.acm.services.transcribe.provider.aws.model.AWSTranscribeConfiguration;
import com.armedia.acm.services.transcribe.provider.aws.model.transcript.AWSTranscript;
import com.armedia.acm.services.transcribe.provider.aws.model.transcript.AWSTranscriptAlternative;
import com.armedia.acm.services.transcribe.provider.aws.model.transcript.AWSTranscriptItem;
import com.armedia.acm.services.transcribe.service.TranscribeConfigurationPropertiesService;
import com.armedia.acm.services.transcribe.service.TranscribeEventPublisher;
import com.armedia.acm.services.transcribe.service.TranscribeService;
import com.armedia.acm.services.transcribe.utils.TranscribeUtils;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 03/12/2018
 */
public class AWSTranscribeService implements TranscribeService
{
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private AmazonS3 s3Client;
    private AmazonTranscribe transcribeClient;
    private EcmFileTransaction ecmFileTransaction;
    private AWSTranscribeConfigurationPropertiesService awsTranscribeConfigurationPropertiesService;
    private MuleContextManager muleContextManager;
    private TranscribeConfigurationPropertiesService transcribeConfigurationPropertiesService;
    private String credentialConfigurationFileLocation;
    private TranscribeEventPublisher transcribeEventPublisher;

    public void init() throws GetConfigurationException
    {
        AWSTranscribeConfiguration configuration = getConfiguration();
        ArkCaseAWSCredentialsProviderChain credentialsProviderChain = new ArkCaseAWSCredentialsProviderChain(getCredentialConfigurationFileLocation(), configuration.getProfile());
        s3Client = AmazonS3ClientBuilder.standard().withCredentials(credentialsProviderChain).withRegion(Regions.fromName(configuration.getRegion())).build();
        transcribeClient = AmazonTranscribeClientBuilder.standard().withCredentials(credentialsProviderChain).withRegion(Regions.fromName(configuration.getRegion())).build();
    }

    @Override
    @Async
    public Transcribe create(Transcribe transcribe) throws CreateTranscribeException
    {
        AWSTranscribeConfiguration configuration = null;
        try
        {
            configuration = getConfiguration();
        }
        catch (GetConfigurationException e)
        {
            getTranscribeEventPublisher().publish(transcribe, TranscribeActionType.PROVIDER_FAILED.toString());
            throw new CreateTranscribeException(String.format("Transcribe failed to create on Amazon. REASON=[%s]", e.getMessage()), e);
        }

        String key = transcribe.getRemoteId() + transcribe.getMediaEcmFileVersion().getVersionFileNameExtension();
        if (getS3Client().doesObjectExist(configuration.getBucket(), key))
        {
            getTranscribeEventPublisher().publish(transcribe, TranscribeActionType.PROVIDER_FAILED.toString());
            throw new CreateTranscribeException(String.format("The file with KEY=[%s] already exist on Amazon.", key));
        }

        uploadMedia(transcribe);
        startTranscribeJob(transcribe);

        return transcribe;
    }

    @Override
    public Transcribe get(String remoteId) throws GetTranscribeException
    {
        if (StringUtils.isNotEmpty(remoteId))
        {
            try
            {
                GetTranscriptionJobRequest request = new GetTranscriptionJobRequest();
                request.setTranscriptionJobName(remoteId);

                GetTranscriptionJobResult result = getTranscribeClient().getTranscriptionJob(request);
                String resultStatus = result.getTranscriptionJob().getTranscriptionJobStatus();

                Transcribe transcribe = new Transcribe();
                if (TranscriptionJobStatus.COMPLETED.toString().equals(resultStatus))
                {
                    transcribe.setTranscribeItems(generateTranscribeItems(result));
                    transcribe.setStatus(TranscribeStatusType.COMPLETED.toString());
                    transcribe.setRemoteId(remoteId);
                }
                else
                {
                    String status = TranscribeStatusType.PROCESSING.toString();
                    switch (TranscriptionJobStatus.fromValue(resultStatus))
                    {
                        case IN_PROGRESS:
                            status = TranscribeStatusType.PROCESSING.toString();
                            break;
                        case FAILED:
                            status = TranscribeStatusType.FAILED.toString();
                            break;
                    }
                    transcribe.setStatus(status);
                    transcribe.setRemoteId(remoteId);
                }

                return transcribe;
            }
            catch (MuleException | AmazonServiceException e)
            {
                throw new GetTranscribeException(String.format("Unable to get transcribe job on Amazon. REASON=[%s].", e.getMessage()), e);
            }
        }

        throw new GetTranscribeException("Unable to get transcribe job on Amazon. Remote ID not provided.");
    }

    @Override
    public List<Transcribe> getAll() throws GetTranscribeException
    {
        return null;
    }

    @Override
    public List<Transcribe> getAllByStatus(String status) throws GetTranscribeException
    {
        return null;
    }

    @Override
    public List<Transcribe> getPage(int start, int n) throws GetTranscribeException
    {
        return null;
    }

    @Override
    public List<Transcribe> getPageByStatus(int start, int n, String status) throws GetTranscribeException
    {
        return null;
    }

    public AWSTranscribeConfiguration getConfiguration() throws GetConfigurationException
    {
        return getAwsTranscribeConfigurationPropertiesService().get();
    }

    public AWSTranscribeConfiguration saveConfiguration(AWSTranscribeConfiguration configuration) throws SaveConfigurationException
    {
        return getAwsTranscribeConfigurationPropertiesService().save(configuration);
    }

    private PutObjectResult uploadMedia(Transcribe transcribe) throws CreateTranscribeException
    {
        if (transcribe != null)
        {
            try
            {
                AWSTranscribeConfiguration configuration = getConfiguration();

                EcmFileVersion ecmFileVersion = transcribe.getMediaEcmFileVersion();
                EcmFile ecmFile = ecmFileVersion.getFile();

                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentLength(ecmFileVersion.getFileSizeBytes());
                metadata.setContentType(ecmFileVersion.getVersionMimeType());

                InputStream inputStream = getEcmFileTransaction().downloadFileTransactionAsInputStream(ecmFile);

                String key = transcribe.getRemoteId() + ecmFileVersion.getVersionFileNameExtension();

                return getS3Client().putObject(configuration.getBucket(), key, inputStream, metadata);
            }
            catch (Exception e)
            {
                getTranscribeEventPublisher().publish(transcribe, TranscribeActionType.PROVIDER_FAILED.toString());
                throw new CreateTranscribeException(String.format("Unable to upload media file to Amazon. REASON=[%s].", e.getMessage()), e);
            }
        }

        throw new CreateTranscribeException("Unable to upload media file to Amazon. Transcribe not provided.");
    }

    private StartTranscriptionJobResult startTranscribeJob(Transcribe transcribe) throws CreateTranscribeException
    {
        if (transcribe != null)
        {
            try
            {
                AWSTranscribeConfiguration configuration = getConfiguration();

                String mediaType = TranscribeUtils.extractMediaType(transcribe.getMediaEcmFileVersion().getVersionMimeType());
                String key = transcribe.getRemoteId() + transcribe.getMediaEcmFileVersion().getVersionFileNameExtension();

                Media media = new Media();
                media.setMediaFileUri(configuration.getHost() + "/" + configuration.getBucket() + "/" + key);

                StartTranscriptionJobRequest request = new StartTranscriptionJobRequest();
                request.setTranscriptionJobName(transcribe.getRemoteId());
                request.setLanguageCode(transcribe.getLanguage());
                request.setMediaFormat(mediaType);
                request.setMedia(media);

                return getTranscribeClient().startTranscriptionJob(request);
            }
            catch (Exception e)
            {
                getTranscribeEventPublisher().publish(transcribe, TranscribeActionType.PROVIDER_FAILED.toString());
                throw new CreateTranscribeException(String.format("Unable to start transcribe job on Amazon. REASON=[%s]", e.getMessage()), e);
            }
        }

        throw new CreateTranscribeException("Unable to start transcribe job on Amazon. Transcribe not provided.");
    }

    private List<TranscribeItem> generateTranscribeItems(GetTranscriptionJobResult result) throws MuleException
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

    private List<TranscribeItem> convertJsonStringToListOfTranscribeItems(String jsonString) throws GetConfigurationException
    {
        TranscribeConfiguration configuration = getTranscribeConfigurationPropertiesService().get();
        List<TranscribeItem> items = new ArrayList<>();

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
            for (int i = 0; i < size; i++)
            {
                AWSTranscriptItem awsTranscriptItem = awsTranscriptItems.get(i);
                boolean punctuation = "punctuation".equalsIgnoreCase(awsTranscriptItem.getType());

                if (!punctuation)
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

                if (awsTranscriptItem.getAlternatives() != null && awsTranscriptItem.getAlternatives().size() > 0)
                {
                    AWSTranscriptAlternative awsTranscriptAlternative = getBestAWSTranscriptAlternative(awsTranscriptItem.getAlternatives());

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

                if (counter >= configuration.getWordCountPerItem() || i == size - 1)
                {
                    if (!isNextPunctuation(i, size, awsTranscriptItems))
                    {
                        TranscribeItem item = new TranscribeItem();
                        item.setStartTime(startTime);
                        item.setEndTime(endTime);
                        item.setText(text);

                        int conf = confidence.intValue() == 0 || confidenceCounter == 0 ? 0 : confidence.multiply(new BigDecimal(100)).intValue() / confidenceCounter;
                        item.setConfidence(conf);

                        items.add(item);

                        counter = 0;
                        startTime = null;
                        endTime = null;
                        confidence = new BigDecimal("0");
                        confidenceCounter = 0;
                        text = "";
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
        if (i <= size - 2 && awsTranscriptItems.get(i+1) != null && "punctuation".equalsIgnoreCase(awsTranscriptItems.get(i+1).getType()))
        {
            return true;
        }
        return false;
    }

    private AWSTranscriptAlternative getBestAWSTranscriptAlternative(List<AWSTranscriptAlternative> alternatives)
    {
        if (alternatives == null || alternatives.size() == 0)
        {
            return null;
        }

        if (alternatives.size() == 1)
        {
            return alternatives.get(0);
        }

        alternatives.sort((AWSTranscriptAlternative a1, AWSTranscriptAlternative a2) -> (toIntWithWholePrecision(a2.getConfidence()) - toIntWithWholePrecision(a1.getConfidence())));

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

    public EcmFileTransaction getEcmFileTransaction()
    {
        return ecmFileTransaction;
    }

    public void setEcmFileTransaction(EcmFileTransaction ecmFileTransaction)
    {
        this.ecmFileTransaction = ecmFileTransaction;
    }

    public AWSTranscribeConfigurationPropertiesService getAwsTranscribeConfigurationPropertiesService()
    {
        return awsTranscribeConfigurationPropertiesService;
    }

    public MuleContextManager getMuleContextManager()
    {
        return muleContextManager;
    }

    public void setMuleContextManager(MuleContextManager muleContextManager)
    {
        this.muleContextManager = muleContextManager;
    }

    public void setAwsTranscribeConfigurationPropertiesService(AWSTranscribeConfigurationPropertiesService awsTranscribeConfigurationPropertiesService)
    {
        this.awsTranscribeConfigurationPropertiesService = awsTranscribeConfigurationPropertiesService;
    }

    public TranscribeConfigurationPropertiesService getTranscribeConfigurationPropertiesService()
    {
        return transcribeConfigurationPropertiesService;
    }

    public void setTranscribeConfigurationPropertiesService(TranscribeConfigurationPropertiesService transcribeConfigurationPropertiesService)
    {
        this.transcribeConfigurationPropertiesService = transcribeConfigurationPropertiesService;
    }

    public String getCredentialConfigurationFileLocation()
    {
        return credentialConfigurationFileLocation;
    }

    public void setCredentialConfigurationFileLocation(String credentialConfigurationFileLocation)
    {
        this.credentialConfigurationFileLocation = credentialConfigurationFileLocation;
    }

    public TranscribeEventPublisher getTranscribeEventPublisher()
    {
        return transcribeEventPublisher;
    }

    public void setTranscribeEventPublisher(TranscribeEventPublisher transcribeEventPublisher)
    {
        this.transcribeEventPublisher = transcribeEventPublisher;
    }
}
