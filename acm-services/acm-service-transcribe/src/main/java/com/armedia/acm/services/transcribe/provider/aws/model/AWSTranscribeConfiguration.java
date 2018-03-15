package com.armedia.acm.services.transcribe.provider.aws.model;

import com.armedia.acm.services.transcribe.annotation.ConfigurationProperties;
import com.armedia.acm.services.transcribe.annotation.ConfigurationProperty;

import java.io.Serializable;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 03/12/2018
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
