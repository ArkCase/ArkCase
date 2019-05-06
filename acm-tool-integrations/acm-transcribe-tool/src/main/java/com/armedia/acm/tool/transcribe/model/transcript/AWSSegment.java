package com.armedia.acm.tool.transcribe.model.transcript;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by Vladimir Cherepnalkovski
 */
public class AWSSegment
{
    @JsonProperty("start_time")
    private String startTime;

    @JsonProperty("speaker_label")
    private String speakerLabel;

    @JsonProperty("end_time")
    private String endTime;

    @JsonProperty("items")
    private List<AWSSpeakerItem> items;

    public String getStartTime()
    {
        return startTime;
    }

    public void setStartTime(String startTime)
    {
        this.startTime = startTime;
    }

    public String getSpeakerLabel()
    {
        return speakerLabel;
    }

    public void setSpeakerLabel(String speakerLabel)
    {
        this.speakerLabel = speakerLabel;
    }

    public String getEndTime()
    {
        return endTime;
    }

    public void setEndTime(String endTime)
    {
        this.endTime = endTime;
    }

    public List<AWSSpeakerItem> getItems()
    {
        return items;
    }

    public void setItems(List<AWSSpeakerItem> items)
    {
        this.items = items;
    }
}
