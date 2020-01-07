package com.armedia.acm.tool.transcribe.credentials;

/*-
 * #%L
 * acm-transcribe-tool
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
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
        configuration = getAwsTranscribeCredentialsConfigurationService().loadCredentialsProperties();

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
