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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.springframework.beans.factory.annotation.Value;

/**
 * Created by Vladimir Cherepnalkovski
 */
@JsonSerialize(as = AWSTranscribeConfiguration.class)
public class AWSTranscribeConfiguration
{
    @JsonProperty("aws.config.bucket")
    @Value("${aws.config.bucket}")
    private String bucket;

    @JsonProperty("aws.config.region")
    @Value("${aws.config.region}")
    private String region;

    @JsonProperty("aws.config.host")
    @Value("${aws.config.host}")
    private String host;

    @JsonProperty("aws.config.profile")
    @Value("${aws.config.profile}")
    private String profile;

    @JsonProperty("aws.config.maxSpeakerLabels")
    @Value("${aws.config.maxSpeakerLabels}")
    private int maxSpeakerLabels;

    @JsonProperty("aws.config.showSpeakerLabels")
    @Value("${aws.config.showSpeakerLabels}")
    private boolean showSpeakerLabels;

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

    public int getMaxSpeakerLabels()
    {
        return maxSpeakerLabels;
    }

    public void setMaxSpeakerLabels(int maxSpeakerLabels)
    {
        this.maxSpeakerLabels = maxSpeakerLabels;
    }

    public boolean isShowSpeakerLabels()
    {
        return showSpeakerLabels;
    }

    public void setShowSpeakerLabels(boolean showSpeakerLabels)
    {
        this.showSpeakerLabels = showSpeakerLabels;
    }
}
