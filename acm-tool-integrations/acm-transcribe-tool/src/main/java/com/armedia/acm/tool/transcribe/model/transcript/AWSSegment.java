package com.armedia.acm.tool.transcribe.model.transcript;

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
