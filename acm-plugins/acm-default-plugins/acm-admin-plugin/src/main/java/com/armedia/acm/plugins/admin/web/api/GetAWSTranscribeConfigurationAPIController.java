package com.armedia.acm.plugins.admin.web.api;

/*-
 * #%L
 * ACM Default Plugin: admin
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
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

import com.armedia.acm.core.exceptions.AcmEncryptionException;
import com.armedia.acm.tool.transcribe.model.AWSConfiguration;
import com.armedia.acm.tool.transcribe.model.AWSCredentialsConfiguration;
import com.armedia.acm.tool.transcribe.model.AWSTranscribeConfiguration;
import com.armedia.acm.tool.transcribe.service.AWSTranscribeConfigurationService;
import com.armedia.acm.tool.transcribe.service.AWSTranscribeCredentialsConfigurationService;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Vladimir Cherepnalkovski
 */
@Controller
@RequestMapping({ "/api/v1/plugin/admin/transcribe/aws/configuration", "/api/latest/plugin/admin/transcribe/aws/configuration" })
public class GetAWSTranscribeConfigurationAPIController
{
    private AWSTranscribeCredentialsConfigurationService awsTranscribeCredentialsConfigurationService;
    private AWSTranscribeConfigurationService awsTranscribeConfigurationService;

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AWSConfiguration getConfiguration() throws AcmEncryptionException
    {
        AWSTranscribeConfiguration awsTranscribeConfiguration = getAwsTranscribeConfigurationService()
                .loadAWSProperties();
        AWSCredentialsConfiguration credentialsConfiguration = getAwsTranscribeCredentialsConfigurationService()
                .loadCredentialsProperties();

        AWSConfiguration configuration = new AWSConfiguration();
        configuration.setAwsTranscribeConfiguration(awsTranscribeConfiguration);
        configuration.setCredentialsConfiguration(credentialsConfiguration);

        return configuration;
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

    public AWSTranscribeConfigurationService getAwsTranscribeConfigurationService()
    {
        return awsTranscribeConfigurationService;
    }

    public void setAwsTranscribeConfigurationService(AWSTranscribeConfigurationService awsTranscribeConfigurationService)
    {
        this.awsTranscribeConfigurationService = awsTranscribeConfigurationService;
    }
}
