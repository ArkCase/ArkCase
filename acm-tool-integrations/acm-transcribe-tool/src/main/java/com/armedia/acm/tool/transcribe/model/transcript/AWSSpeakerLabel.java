package com.armedia.acm.tool.transcribe.model.transcript;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by Vladimir Cherepnalkovski
 */
public class AWSSpeakerLabel
{

    @JsonProperty("speakers")
    private Integer numberOfSpeakers;

    @JsonProperty("segments")
    private List<AWSSegment> segments;

    public Integer getNumberOfSpeakers()
    {
        return numberOfSpeakers;
    }

    public void setNumberOfSpeakers(Integer numberOfSpeakers)
    {
        this.numberOfSpeakers = numberOfSpeakers;
    }

    public List<AWSSegment> getSegments()
    {
        return segments;
    }

    public void setSegments(List<AWSSegment> segments)
    {
        this.segments = segments;
    }
}
