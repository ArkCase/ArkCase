package com.armedia.acm.tool.transcribe.credentials;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.armedia.acm.core.exceptions.AcmEncryptionException;
import com.armedia.acm.tool.transcribe.model.AWSCredentialsConfiguration;
import com.armedia.acm.tool.transcribe.service.AWSTranscribeCredentialsConfigurationService;

/**
 * Created by Vladimir Cherepnalkovski
 */
public class ArkCaseProfileCredentialsProvider implements AWSCredentialsProvider
{
    private AWSTranscribeCredentialsConfigurationService awsTranscribeCredentialsConfigurationService;

    public ArkCaseProfileCredentialsProvider(AWSTranscribeCredentialsConfigurationService awsTranscribeCredentialsConfigurationService)
    {
        this.awsTranscribeCredentialsConfigurationService = awsTranscribeCredentialsConfigurationService;
    }

    @Override
    public AWSCredentials getCredentials()
    {
        AWSCredentialsConfiguration configuration = null;
        try
        {
            configuration = getAwsTranscribeCredentialsConfigurationService().loadCredentialsProperties();
        }
        catch (AcmEncryptionException e)
        {
            throw new SdkClientException("Unable to load AWS credentials from configuration server (aws.accessKeyId and aws.secretKey)");
        }

        if (configuration != null)
        {
            AWSCredentials credentials = new BasicAWSCredentials(configuration.getAwsAccessKeyId(), configuration.getAwsSecretAccessKey());
            return credentials;
        }
        else
        {
            throw new SdkClientException("Unable to load AWS credentials from configuration server (aws.accessKeyId and aws.secretKey)");
        }
    }

    @Override
    public void refresh()
    {
        // Not implemented
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
