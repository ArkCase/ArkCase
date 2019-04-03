package com.armedia.acm.tool.transcribe.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.springframework.beans.factory.annotation.Value;

/**
 * Created by Vladimir Cherepnalkovski
 */
@JsonSerialize(as = AWSCredentialsConfiguration.class)
public class AWSCredentialsConfiguration
{
    @JsonProperty("aws.credentials.awsAccessKeyId")
    @Value("${aws.credentials.awsAccessKeyId}")
    private String awsAccessKeyId;

    @JsonProperty("aws.credentials.awsSecretAccessKey")
    @Value("${aws.credentials.awsSecretAccessKey}")
    private String awsSecretAccessKey;

    public String getAwsAccessKeyId()
    {
        return awsAccessKeyId;
    }

    public void setAwsAccessKeyId(String awsAccessKeyId)
    {
        this.awsAccessKeyId = awsAccessKeyId;
    }

    public String getAwsSecretAccessKey()
    {
        return awsSecretAccessKey;
    }

    public void setAwsSecretAccessKey(String awsSecretAccessKey)
    {
        this.awsSecretAccessKey = awsSecretAccessKey;
    }
}
