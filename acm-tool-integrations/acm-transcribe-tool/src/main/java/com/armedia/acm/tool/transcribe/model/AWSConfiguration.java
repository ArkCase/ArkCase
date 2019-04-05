package com.armedia.acm.tool.transcribe.model;

/**
 * Created by Vladimir Cherepnalkovski
 */
public class AWSConfiguration
{
    private AWSCredentialsConfiguration credentialsConfiguration;
    private AWSTranscribeConfiguration awsTranscribeConfiguration;

    public AWSCredentialsConfiguration getCredentialsConfiguration()
    {
        return credentialsConfiguration;
    }

    public void setCredentialsConfiguration(AWSCredentialsConfiguration credentialsConfiguration)
    {
        this.credentialsConfiguration = credentialsConfiguration;
    }

    public AWSTranscribeConfiguration getAwsTranscribeConfiguration()
    {
        return awsTranscribeConfiguration;
    }

    public void setAwsTranscribeConfiguration(AWSTranscribeConfiguration awsTranscribeConfiguration)
    {
        this.awsTranscribeConfiguration = awsTranscribeConfiguration;
    }
}
