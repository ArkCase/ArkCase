package com.armedia.acm.tool.transcribe.service;

import com.armedia.acm.configuration.service.ConfigurationPropertyService;
import com.armedia.acm.core.exceptions.AcmEncryptionException;
import com.armedia.acm.crypto.AcmCryptoUtils;
import com.armedia.acm.tool.transcribe.model.AWSCredentialsConfiguration;

import java.util.Base64;

/**
 * Created by Vladimir Cherepnalkovski
 */
public class AWSTranscribeCredentialsConfigurationService
{
    private AWSCredentialsConfiguration AWSCredentialsConfig;

    private ConfigurationPropertyService configurationPropertyService;

    private AcmCryptoUtils acmCryptoUtils;

    private static final String sha256Hex = "0954a45393869026bc6a3804771b87aa9c07ad6f6a2a3c0ae030ea4a7ce34743";

    public void saveCredentialsProperties(AWSCredentialsConfiguration AWSCredentialsConfig) throws AcmEncryptionException
    {
        byte[] encryptedAccessKeyIdBytes = getAcmCryptoUtils().encryptData(sha256Hex.getBytes(),
                AWSCredentialsConfig.getAwsAccessKeyId().getBytes(), true);

        String encryptedAccessKeyId = Base64.getEncoder().encodeToString(encryptedAccessKeyIdBytes);

        byte[] encryptedSecurityAccessKeyBytes = getAcmCryptoUtils().encryptData(sha256Hex.getBytes(),
                AWSCredentialsConfig.getAwsSecretAccessKey().getBytes(), true);

        String encryptedSecurityAccessKey = Base64.getEncoder().encodeToString(encryptedSecurityAccessKeyBytes);

        AWSCredentialsConfig.setAwsAccessKeyId(encryptedAccessKeyId);
        AWSCredentialsConfig.setAwsSecretAccessKey(encryptedSecurityAccessKey);

        configurationPropertyService.updateProperties(AWSCredentialsConfig);
    }

    public AWSCredentialsConfiguration loadCredentialsProperties() throws AcmEncryptionException
    {
        byte[] decryptedAccessKeyId = getAcmCryptoUtils().decryptData(sha256Hex.getBytes(),
                Base64.getDecoder().decode(getAWSCredentialsConfig().getAwsAccessKeyId().getBytes()), true);

        byte[] decryptedSecretAccessKey = getAcmCryptoUtils().decryptData(sha256Hex.getBytes(),
                Base64.getDecoder().decode(getAWSCredentialsConfig().getAwsSecretAccessKey().getBytes()), true);

        AWSCredentialsConfiguration config = new AWSCredentialsConfiguration();
        config.setAwsAccessKeyId(new String(decryptedAccessKeyId));
        config.setAwsSecretAccessKey(new String(decryptedSecretAccessKey));

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
}
