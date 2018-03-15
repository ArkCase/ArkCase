package com.armedia.acm.services.transcribe.provider.aws.credentials;

import com.amazonaws.auth.*;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 03/12/2018
 */
public class ArkCaseAWSCredentialsProviderChain extends AWSCredentialsProviderChain
{
    public ArkCaseAWSCredentialsProviderChain()
    {
        // Keep backward compatibility with Default Amazon Credential provider chain.
        // Use the same order of credential providers, just override "ProfileCredentialsProvider"
        // to change the path of the credentials file
        super(new EnvironmentVariableCredentialsProvider(),
              new SystemPropertiesCredentialsProvider(),
              new PropertiesFileCredentialsProvider(System.getProperty("user.home") + "/.arkcase/acm/aws/credentials.properties"),
              new EC2ContainerCredentialsProviderWrapper());
    }
}
