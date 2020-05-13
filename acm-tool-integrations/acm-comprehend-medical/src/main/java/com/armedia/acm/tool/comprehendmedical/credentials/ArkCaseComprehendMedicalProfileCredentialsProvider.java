package com.armedia.acm.tool.comprehendmedical.credentials;

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

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.armedia.acm.tool.comprehendmedical.model.AWSCredentialsConfiguration;
import com.armedia.acm.tool.comprehendmedical.service.AWSComprehendMedicalCredentialsConfigurationService;

public class ArkCaseComprehendMedicalProfileCredentialsProvider implements AWSCredentialsProvider
{
    private AWSComprehendMedicalCredentialsConfigurationService awsComprehendMedicalCredentialsConfigurationService;

    public ArkCaseComprehendMedicalProfileCredentialsProvider(AWSComprehendMedicalCredentialsConfigurationService awsComprehendMedicalCredentialsConfigurationService)
    {
        this.awsComprehendMedicalCredentialsConfigurationService = awsComprehendMedicalCredentialsConfigurationService;
    }

    @Override
    public AWSCredentials getCredentials()
    {
        AWSCredentialsConfiguration configuration = null;
        configuration = getAwsComprehendMedicalCredentialsConfigurationService().loadCredentialsProperties();

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
    public void refresh() {

    }

    public AWSComprehendMedicalCredentialsConfigurationService getAwsComprehendMedicalCredentialsConfigurationService()
    {
        return awsComprehendMedicalCredentialsConfigurationService;
    }

    public void setAwsComprehendMedicalCredentialsConfigurationService(AWSComprehendMedicalCredentialsConfigurationService awsComprehendMedicalCredentialsConfigurationService)
    {
        this.awsComprehendMedicalCredentialsConfigurationService = awsComprehendMedicalCredentialsConfigurationService;
    }
}
