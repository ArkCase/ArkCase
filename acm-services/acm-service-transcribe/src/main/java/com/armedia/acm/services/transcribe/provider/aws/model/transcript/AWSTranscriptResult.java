package com.armedia.acm.services.transcribe.provider.aws.model.transcript;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 03/17/2018
 */
public class AWSTranscriptResult
{
    @JsonProperty("transcripts")
    private List<AWSTranscriptText> transcripts;

    @JsonProperty("items")
    private List<AWSTranscriptItem> items;

    public List<AWSTranscriptText> getTranscripts()
    {
        return transcripts;
    }

    public void setTranscripts(List<AWSTranscriptText> transcripts)
    {
        this.transcripts = transcripts;
    }

    public List<AWSTranscriptItem> getItems()
    {
        return items;
    }

    public void setItems(List<AWSTranscriptItem> items)
    {
        this.items = items;
    }
}
