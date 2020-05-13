package com.armedia.acm.tool.comprehendmedical.service;

/*-
 * #%L
 * acm-comprehend-medical
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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

import com.armedia.acm.configuration.service.ConfigurationPropertyService;
import com.armedia.acm.crypto.AcmCryptoUtils;
import com.armedia.acm.crypto.exceptions.AcmEncryptionException;
import com.armedia.acm.crypto.properties.AcmEncryptablePropertyUtilsImpl;
import com.armedia.acm.tool.comprehendmedical.model.AWSCredentialsConfiguration;

public class AWSComprehendMedicalCredentialsConfigurationService
{
    private AWSCredentialsConfiguration awsCredentialsConfig;

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

    public AWSCredentialsConfiguration loadCredentialsProperties()
    {
        return awsCredentialsConfig;
    }

    public ConfigurationPropertyService getConfigurationPropertyService()
    {
        return configurationPropertyService;
    }

    public void setConfigurationPropertyService(ConfigurationPropertyService configurationPropertyService)
    {
        this.configurationPropertyService = configurationPropertyService;
    }

    public AWSCredentialsConfiguration getAwsCredentialsConfig()
    {
        return awsCredentialsConfig;
    }

    public void setAwsCredentialsConfig(AWSCredentialsConfiguration awsCredentialsConfig)
    {
        this.awsCredentialsConfig = awsCredentialsConfig;
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
