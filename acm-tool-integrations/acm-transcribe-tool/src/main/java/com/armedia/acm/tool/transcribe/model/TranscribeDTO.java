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

import com.armedia.acm.tool.mediaengine.model.MediaEngineDTO;

import java.io.InputStream;
import java.util.List;

/**
 * Created by Vladimir Cherepnalkovski
 */
public class TranscribeDTO extends MediaEngineDTO
{
    private List<TranscribeItemDTO> transcribeItems;
    private Long id;
    private InputStream transcribeEcmFile;
    private long wordCount;
    private int confidence;
    private String message;

    @Override
    public Long getId()
    {
        return id;
    }

    @Override
    public void setId(Long id)
    {
        this.id = id;
    }

    public InputStream getTranscribeEcmFile()
    {
        return transcribeEcmFile;
    }

    public void setTranscribeEcmFile(InputStream transcribeEcmFile)
    {
        this.transcribeEcmFile = transcribeEcmFile;
    }

    public long getWordCount()
    {
        return wordCount;
    }

    public void setWordCount(long wordCount)
    {
        this.wordCount = wordCount;
    }

    public int getConfidence()
    {
        return confidence;
    }

    public void setConfidence(int confidence)
    {
        this.confidence = confidence;
    }

    public List<TranscribeItemDTO> getTranscribeItems()
    {
        return transcribeItems;
    }

    public void setTranscribeItems(List<TranscribeItemDTO> transcribeItems)
    {
        this.transcribeItems = transcribeItems;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }
}
