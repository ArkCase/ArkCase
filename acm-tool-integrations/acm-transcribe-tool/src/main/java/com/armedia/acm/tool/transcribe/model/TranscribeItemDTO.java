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

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Vladimir Cherepnalkovski
 */
public class TranscribeItemDTO
{

    private Long id;

    private TranscribeDTO transcribe;

    private BigDecimal startTime;

    private BigDecimal endTime;

    private int confidence;

    private Boolean corrected = Boolean.FALSE;

    private String text;

    private String creator;

    private Date created;

    private String modifier;

    private Date modified;

    private String className = this.getClass().getName();

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public TranscribeDTO getTranscribe()
    {
        return transcribe;
    }

    public void setTranscribe(TranscribeDTO transcribe)
    {
        this.transcribe = transcribe;
    }

    public BigDecimal getStartTime()
    {
        return startTime;
    }

    public void setStartTime(BigDecimal startTime)
    {
        if (startTime == null)
        {
            startTime = new BigDecimal("0");
        }
        this.startTime = startTime;
    }

    public BigDecimal getEndTime()
    {
        return endTime;
    }

    public void setEndTime(BigDecimal endTime)
    {
        if (endTime == null)
        {
            endTime = new BigDecimal("0");
        }
        this.endTime = endTime;
    }

    public int getConfidence()
    {
        return confidence;
    }

    public void setConfidence(int confidence)
    {
        this.confidence = confidence;
    }

    public Boolean getCorrected()
    {
        return corrected;
    }

    public void setCorrected(Boolean corrected)
    {
        this.corrected = corrected;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public String getCreator()
    {
        return creator;
    }

    public void setCreator(String creator)
    {
        this.creator = creator;
    }

    public Date getCreated()
    {
        return created;
    }

    public void setCreated(Date created)
    {
        this.created = created;
    }

    public String getModifier()
    {
        return modifier;
    }

    public void setModifier(String modifier)
    {
        this.modifier = modifier;
    }

    public Date getModified()
    {
        return modified;
    }

    public void setModified(Date modified)
    {
        this.modified = modified;
    }

    public String getClassName()
    {
        return className;
    }

    public void setClassName(String className)
    {
        this.className = className;
    }

}
