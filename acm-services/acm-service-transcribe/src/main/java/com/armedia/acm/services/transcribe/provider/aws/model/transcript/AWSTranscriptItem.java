package com.armedia.acm.services.transcribe.provider.aws.model.transcript;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 03/17/2018
 */
public class AWSTranscriptItem
{
    @JsonProperty("start_time")
    private String startTime;

    @JsonProperty("end_time")
    private String endTime;

    @JsonProperty("alternatives")
    private List<AWSTranscriptAlternative> alternatives;

    @JsonProperty("type")
    private String type;

    public String getStartTime()
    {
        return startTime;
    }

    public void setStartTime(String startTime)
    {
        this.startTime = startTime;
    }

    public String getEndTime()
    {
        return endTime;
    }

    public void setEndTime(String endTime)
    {
        this.endTime = endTime;
    }

    public List<AWSTranscriptAlternative> getAlternatives()
    {
        return alternatives;
    }

    public void setAlternatives(List<AWSTranscriptAlternative> alternatives)
    {
        this.alternatives = alternatives;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }
}
