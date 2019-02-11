package com.armedia.acm.services.transcribe.model;

/*-
 * #%L
 * ACM Service: Transcribe
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
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

import com.armedia.acm.services.mediaengine.annotation.ConfigurationProperties;
import com.armedia.acm.services.mediaengine.annotation.ConfigurationProperty;
import com.armedia.acm.services.mediaengine.model.MediaEngineConfiguration;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 02/28/2018
 */
@ConfigurationProperties(path = "${user.home}/.arkcase/acm/transcribe.properties")
public class TranscribeConfiguration extends MediaEngineConfiguration
{
    @ConfigurationProperty(key = "transcribe.word.count.per.item")
    private int wordCountPerItem;

    @ConfigurationProperty(key = "transcribe.allowed.media.duration.in.seconds")
    private long allowedMediaDuration;

    @ConfigurationProperty(key = "transcribe.silent.between.words.in.seconds")
    private BigDecimal silentBetweenWords;

    public int getWordCountPerItem()
    {
        return wordCountPerItem;
    }

    public void setWordCountPerItem(int wordCountPerItem)
    {
        this.wordCountPerItem = wordCountPerItem;
    }

    public long getAllowedMediaDuration()
    {
        return allowedMediaDuration;
    }

    public void setAllowedMediaDuration(long allowedMediaDuration)
    {
        this.allowedMediaDuration = allowedMediaDuration;
    }

    public BigDecimal getSilentBetweenWords()
    {
        return silentBetweenWords;
    }

    public void setSilentBetweenWords(BigDecimal silentBetweenWords)
    {
        this.silentBetweenWords = silentBetweenWords;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;
        TranscribeConfiguration that = (TranscribeConfiguration) o;
        return wordCountPerItem == that.wordCountPerItem &&
                allowedMediaDuration == that.allowedMediaDuration &&
                Objects.equals(silentBetweenWords, that.silentBetweenWords);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), wordCountPerItem, allowedMediaDuration, silentBetweenWords);
    }

    @Override
    public String toString()
    {
        return "TranscribeConfiguration{" +
                "wordCountPerItem=" + wordCountPerItem +
                ", allowedMediaDuration=" + allowedMediaDuration +
                ", silentBetweenWords=" + silentBetweenWords +
                '}';
    }
}
