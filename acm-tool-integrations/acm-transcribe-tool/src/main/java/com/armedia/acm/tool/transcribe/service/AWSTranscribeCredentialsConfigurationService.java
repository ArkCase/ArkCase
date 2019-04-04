package com.armedia.acm.tool.transcribe.service;

import com.armedia.acm.configuration.service.ConfigurationPropertyService;
import com.armedia.acm.core.exceptions.AcmEncryptionException;
import com.armedia.acm.crypto.AcmCryptoUtils;
import com.armedia.acm.crypto.properties.AcmEncryptablePropertyUtilsImpl;
import com.armedia.acm.tool.transcribe.model.AWSCredentialsConfiguration;

/**
 * Created by Vladimir Cherepnalkovski
 */
public class AWSTranscribeCredentialsConfigurationService
{
    private AWSCredentialsConfiguration AWSCredentialsConfig;

    private ConfigurationPropertyService configurationPropertyService;

    private AcmCryptoUtils acmCryptoUtils;

    AcmEncryptablePropertyUtilsImpl encryptionProperties;

    public void saveCredentialsProperties(AWSCredentialsConfiguration awsCredentialsConfig) throws AcmEncryptionException
    {
        String accessKeyId = encryptionProperties.encryptPropertyValue(awsCredentialsConfig.getAwsAccessKeyId());
        String secretAccessKeyId = encryptionProperties.encryptPropertyValue(awsCredentialsConfig.getAwsSecretAccessKey());

        awsCredentialsConfig.setAwsAccessKeyId(accessKeyId);
        awsCredentialsConfig.setAwsSecretAccessKey(secretAccessKeyId);

        configurationPropertyService.updateProperties(awsCredentialsConfig);
    }

    public AWSCredentialsConfiguration loadCredentialsProperties() throws AcmEncryptionException
    {
        String accessKeyId = encryptionProperties.decryptPropertyValue(getAWSCredentialsConfig().getAwsAccessKeyId());
        String secretAccessKeyId = encryptionProperties.decryptPropertyValue(getAWSCredentialsConfig().getAwsSecretAccessKey());

        AWSCredentialsConfiguration config = new AWSCredentialsConfiguration();
        config.setAwsAccessKeyId(new String(accessKeyId));
        config.setAwsSecretAccessKey(new String(secretAccessKeyId));

        return config;
    }

    public ConfigurationPropertyService getConfigurationPropertyService()
    {
        return configurationPropertyService;
    }

    public void setConfigurationPropertyService(ConfigurationPropertyService configurationPropertyService)
    {
        this.configurationPropertyService = configurationPropertyService;
    }

    public AWSCredentialsConfiguration getAWSCredentialsConfig()
    {
        return AWSCredentialsConfig;
    }

    public void setAWSCredentialsConfig(AWSCredentialsConfiguration AWSCredentialsConfig)
    {
        this.AWSCredentialsConfig = AWSCredentialsConfig;
    }

    public AcmCryptoUtils getAcmCryptoUtils()
    {
        return acmCryptoUtils;
    }

    public void setAcmCryptoUtils(AcmCryptoUtils acmCryptoUtils)
    {
        this.acmCryptoUtils = acmCryptoUtils;
    }

    public AcmEncryptablePropertyUtilsImpl getEncryptionProperties()
    {
        return encryptionProperties;
    }

    public void setEncryptionProperties(AcmEncryptablePropertyUtilsImpl encryptionProperties)
    {
        this.encryptionProperties = encryptionProperties;
    }
}
