package com.armedia.acm.services.transcribe.provider.aws.model.transcript;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 03/17/2018
 */
public class AWSTranscriptAlternative
{
    @JsonProperty("confidence")
    private String confidence;

    @JsonProperty("content")
    private String content;

    public String getConfidence()
    {
        return confidence;
    }

    public void setConfidence(String confidence)
    {
        this.confidence = confidence;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }
}
