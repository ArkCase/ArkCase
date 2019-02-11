package com.armedia.acm.tool.transcribe.model;

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

import com.armedia.acm.tool.transcribe.annotation.ConfigurationProperties;
import com.armedia.acm.tool.transcribe.annotation.ConfigurationProperty;

import java.io.Serializable;

/**
 * Created by Vladimir Cherepnalkovski
 */
@ConfigurationProperties(path = "${user.home}/.arkcase/acm/aws/config.properties")
public class AWSTranscribeConfiguration implements Serializable
{
    @ConfigurationProperty(key = "aws.bucket")
    private String bucket;

    @ConfigurationProperty(key = "aws.region")
    private String region;

    @ConfigurationProperty(key = "aws.host")
    private String host;

    @ConfigurationProperty(key = "aws.profile")
    private String profile;

    public String getBucket()
    {
        return bucket;
    }

    public void setBucket(String bucket)
    {
        this.bucket = bucket;
    }

    public String getRegion()
    {
        return region;
    }

    public void setRegion(String region)
    {
        this.region = region;
    }

    public String getHost()
    {
        return host;
    }

    public void setHost(String host)
    {
        this.host = host;
    }

    public String getProfile()
    {
        return profile;
    }

    public void setProfile(String profile)
    {
        this.profile = profile;
    }
}
