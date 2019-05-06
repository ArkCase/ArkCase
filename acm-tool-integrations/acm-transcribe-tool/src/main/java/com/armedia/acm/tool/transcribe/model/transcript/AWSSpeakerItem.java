package com.armedia.acm.tool.transcribe.model.transcript;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Vladimir Cherepnalkovski
 */
public class AWSSpeakerItem
{

    @JsonProperty("start_time")
    private String startTime;

    @JsonProperty("end_time")
    private String endTime;

    @JsonProperty("speaker_label")
    private String speakerLabel;

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

    public String getSpeakerLabel()
    {
        return speakerLabel;
    }

    public void setSpeakerLabel(String speakerLabel)
    {
        this.speakerLabel = speakerLabel;
    }
}
