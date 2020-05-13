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

import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.EC2ContainerCredentialsProviderWrapper;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.auth.SystemPropertiesCredentialsProvider;
import com.armedia.acm.tool.comprehendmedical.service.AWSComprehendMedicalCredentialsConfigurationService;

public class AWSComprehendMedicalCredentialsProviderChain extends AWSCredentialsProviderChain
{
    public AWSComprehendMedicalCredentialsProviderChain(AWSComprehendMedicalCredentialsConfigurationService awsComprehendMedicalCredentialsConfigurationService)
    {
        // Keep backward compatibility with Default Amazon Credential provider chain.
        // Use the same order of credential providers, just override "ProfileCredentialsProvider"
        // to change the path of the credentials file
        super(new EnvironmentVariableCredentialsProvider(),
                new SystemPropertiesCredentialsProvider(),
                new ArkCaseComprehendMedicalProfileCredentialsProvider(awsComprehendMedicalCredentialsConfigurationService),
                new EC2ContainerCredentialsProviderWrapper());
    }
}
