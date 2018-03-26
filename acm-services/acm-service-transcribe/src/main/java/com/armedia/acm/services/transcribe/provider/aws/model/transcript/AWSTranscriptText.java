package com.armedia.acm.services.transcribe.provider.aws.model.transcript;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 03/17/2018
 */
public class AWSTranscriptText
{
    @JsonProperty("transcript")
    private String transcript;

    public String getTranscript()
    {
        return transcript;
    }

    public void setTranscript(String transcript)
    {
        this.transcript = transcript;
    }
}
