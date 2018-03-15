package com.armedia.acm.services.transcribe.provider.aws.credentials;

import com.amazonaws.auth.*;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 03/12/2018
 */
public class ArkCaseAWSCredentialsProviderChain extends AWSCredentialsProviderChain
{
    public ArkCaseAWSCredentialsProviderChain(String path, String profile)
    {
        // Keep backward compatibility with Default Amazon Credential provider chain.
        // Use the same order of credential providers, just override "ProfileCredentialsProvider"
        // to change the path of the credentials file
        super(new EnvironmentVariableCredentialsProvider(),
              new SystemPropertiesCredentialsProvider(),
              new ProfileCredentialsProvider(path, profile),
              new EC2ContainerCredentialsProviderWrapper());
    }
}
