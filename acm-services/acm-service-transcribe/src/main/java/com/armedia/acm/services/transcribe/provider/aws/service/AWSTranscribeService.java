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
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;
import com.armedia.acm.plugins.ecm.service.EcmFileTransaction;
import com.armedia.acm.services.transcribe.exception.CreateTranscribeException;
import com.armedia.acm.services.transcribe.exception.GetConfigurationException;
import com.armedia.acm.services.transcribe.exception.GetTranscribeException;
import com.armedia.acm.services.transcribe.exception.SaveConfigurationException;
import com.armedia.acm.services.transcribe.model.Transcribe;
import com.armedia.acm.services.transcribe.provider.aws.credentials.ArkCaseAWSCredentialsProviderChain;
import com.armedia.acm.services.transcribe.provider.aws.model.AWSTranscribeConfiguration;
import com.armedia.acm.services.transcribe.service.TranscribeService;
import com.armedia.acm.services.transcribe.utils.TranscribeUtils;
import org.mule.api.MuleException;
import org.springframework.scheduling.annotation.Async;

import java.io.InputStream;
import java.util.List;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 03/12/2018
 */
public class AWSTranscribeService implements TranscribeService
{
    private AmazonS3 s3Client;
    private AmazonTranscribe transcribeClient;
    private EcmFileTransaction ecmFileTransaction;
    private AWSTranscribeConfigurationPropertiesService awsTranscribeConfigurationPropertiesService;

    public void init() throws GetConfigurationException
    {
        AWSTranscribeConfiguration configuration = (AWSTranscribeConfiguration) getConfiguration();
        ArkCaseAWSCredentialsProviderChain credentialsProviderChain = new ArkCaseAWSCredentialsProviderChain();
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
            configuration = (AWSTranscribeConfiguration)getConfiguration();
        }
        catch (GetConfigurationException e)
        {
            throw new CreateTranscribeException(String.format("Transcribe failed to create on Amazon. REASON=[%s]", e.getMessage()), e);
        }

        String key = transcribe.getRemoteId() + transcribe.getMediaEcmFileVersion().getVersionFileNameExtension();
        if (getS3Client().doesObjectExist(configuration.getBucket(), key))
        {
            throw new CreateTranscribeException(String.format("The file with KEY=[%s] already exist on Amazon.", key));
        }

        PutObjectResult putObjectResult = uploadMedia(transcribe);
        if (putObjectResult != null)
        {
            startTranscribeJob(transcribe);
        }

        return transcribe;
    }

    @Override
    public Transcribe get(String remoteId) throws GetTranscribeException
    {
        return null;
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
            catch (GetConfigurationException | MuleException | AmazonServiceException e)
            {
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
            catch (GetConfigurationException | BadRequestException | LimitExceededException | InternalFailureException | ConflictException e)
            {
                throw new CreateTranscribeException(String.format("Unable to start transcribe job on Amazon. REASON=[%s]", e.getMessage()), e);
            }
        }

        throw new CreateTranscribeException("Unable to start transcribe job on Amazon. Transcribe not provided.");
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

    public void setAwsTranscribeConfigurationPropertiesService(AWSTranscribeConfigurationPropertiesService awsTranscribeConfigurationPropertiesService)
    {
        this.awsTranscribeConfigurationPropertiesService = awsTranscribeConfigurationPropertiesService;
    }
}
