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

import com.armedia.acm.services.mediaengine.model.MediaEngineConfiguration;
import com.armedia.acm.services.mediaengine.model.MediaEngineServices;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 02/28/2018
 */

@JsonSerialize(as = TranscribeConfiguration.class)
public class TranscribeConfiguration implements MediaEngineConfiguration
{
    @JsonProperty("transcribe.enabled")
    @Value("${transcribe.enabled}")
    private boolean enabled;

    @JsonProperty("transcribe.automaticEnabled")
    @Value("${transcribe.automaticEnabled}")
    private boolean automaticEnabled;

    @JsonProperty("transcribe.newMediaEngineForNewVersion")
    @Value("${transcribe.newMediaEngineForNewVersion}")
    private boolean newMediaEngineForNewVersion;

    @JsonProperty("transcribe.copyMediaEngineForNewVersion")
    @Value("${transcribe.copyMediaEngineForNewVersion}")
    private boolean copyMediaEngineForNewVersion;

    @JsonProperty("transcribe.cost")
    @Value("${transcribe.cost}")
    private BigDecimal cost;

    @JsonProperty("transcribe.confidence")
    @Value("${transcribe.confidence}")
    private int confidence;

    @JsonProperty("transcribe.numberOfFilesForProcessing")
    @Value("${transcribe.numberOfFilesForProcessing}")
    private int numberOfFilesForProcessing;

    @JsonProperty("transcribe.service")
    @Value("${transcribe.service}")
    private MediaEngineServices service;

    @JsonProperty("transcribe.providers")
    @Value("#{'${transcribe.providers}'.split(',')}")
    private List<String> providers;

    @JsonProperty("transcribe.providerPurgeAttempts")
    @Value("${transcribe.providerPurgeAttempts}")
    private int providerPurgeAttempts;

    @JsonProperty("transcribe.excludedFileTypes")
    @Value("${transcribe.excludedFileTypes}")
    private String excludedFileTypes;

    @JsonProperty("transcribe.provider")
    @Value("${transcribe.provider}")
    private String provider;

    @JsonProperty("transcribe.tempPath")
    @Value("${transcribe.tempPath}")
    private String tempPath;

    @JsonProperty("transcribe.wordCountPerItem")
    @Value("${transcribe.wordCountPerItem}")
    private int wordCountPerItem;

    @JsonProperty("transcribe.allowedMediaDuration")
    @Value("${transcribe.allowedMediaDuration}")
    private long allowedMediaDuration;

    @JsonProperty("transcribe.silentBetweenWords")
    @Value("${transcribe.silentBetweenWords}")
    private BigDecimal silentBetweenWords;

    @Override
    public boolean isEnabled()
    {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    @Override
    public boolean isAutomaticEnabled()
    {
        return automaticEnabled;
    }

    @Override
    public void setAutomaticEnabled(boolean automaticEnabled)
    {
        this.automaticEnabled = automaticEnabled;
    }

    @Override
    public boolean isNewMediaEngineForNewVersion()
    {
        return newMediaEngineForNewVersion;
    }

    @Override
    public void setNewMediaEngineForNewVersion(boolean newMediaEngineForNewVersion)
    {
        this.newMediaEngineForNewVersion = newMediaEngineForNewVersion;
    }

    @Override
    public boolean isCopyMediaEngineForNewVersion()
    {
        return copyMediaEngineForNewVersion;
    }

    @Override
    public void setCopyMediaEngineForNewVersion(boolean copyMediaEngineForNewVersion)
    {
        this.copyMediaEngineForNewVersion = copyMediaEngineForNewVersion;
    }

    @Override
    public BigDecimal getCost()
    {
        return cost;
    }

    @Override
    public void setCost(BigDecimal cost)
    {
        this.cost = cost;
    }

    @Override
    public int getConfidence()
    {
        return confidence;
    }

    @Override
    public void setConfidence(int confidence)
    {
        this.confidence = confidence;
    }

    @Override
    public int getNumberOfFilesForProcessing()
    {
        return numberOfFilesForProcessing;
    }

    @Override
    public void setNumberOfFilesForProcessing(int numberOfFilesForProcessing)
    {
        this.numberOfFilesForProcessing = numberOfFilesForProcessing;
    }

    @Override
    public MediaEngineServices getService()
    {
        return service;
    }

    @Override
    public void setService(MediaEngineServices service)
    {
        this.service = service;
    }

    @Override
    public List<String> getProviders()
    {
        return providers;
    }

    @Override
    public void setProviders(List<String> providers)
    {
        this.providers = providers;
    }

    @Override
    public int getProviderPurgeAttempts()
    {
        return providerPurgeAttempts;
    }

    @Override
    public void setProviderPurgeAttempts(int providerPurgeAttempts)
    {
        this.providerPurgeAttempts = providerPurgeAttempts;
    }

    @Override
    public String getExcludedFileTypes()
    {
        return excludedFileTypes;
    }

    @Override
    public void setExcludedFileTypes(String excludedFileTypes)
    {
        this.excludedFileTypes = excludedFileTypes;
    }

    @Override
    public String getProvider()
    {
        return provider;
    }

    @Override
    public void setProvider(String provider)
    {
        this.provider = provider;
    }

    @Override
    public String getTempPath()
    {
        return tempPath;
    }

    @Override
    public void setTempPath(String tempPath)
    {
        this.tempPath = tempPath;
    }

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
        TranscribeConfiguration that = (TranscribeConfiguration) o;
        return enabled == that.enabled &&
                automaticEnabled == that.automaticEnabled &&
                newMediaEngineForNewVersion == that.newMediaEngineForNewVersion &&
                copyMediaEngineForNewVersion == that.copyMediaEngineForNewVersion &&
                confidence == that.confidence &&
                numberOfFilesForProcessing == that.numberOfFilesForProcessing &&
                providerPurgeAttempts == that.providerPurgeAttempts &&
                wordCountPerItem == that.wordCountPerItem &&
                allowedMediaDuration == that.allowedMediaDuration &&
                Objects.equals(cost, that.cost) &&
                service == that.service &&
                Objects.equals(providers, that.providers) &&
                Objects.equals(excludedFileTypes, that.excludedFileTypes) &&
                Objects.equals(provider, that.provider) &&
                Objects.equals(tempPath, that.tempPath) &&
                Objects.equals(silentBetweenWords, that.silentBetweenWords);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(enabled, automaticEnabled, newMediaEngineForNewVersion, copyMediaEngineForNewVersion, cost, confidence,
                numberOfFilesForProcessing, service, providers, providerPurgeAttempts, excludedFileTypes, provider, tempPath,
                wordCountPerItem, allowedMediaDuration, silentBetweenWords);
    }

    @Override
    public String toString()
    {
        return "TranscribeConfiguration{" +
                "enabled=" + enabled +
                ", automaticEnabled=" + automaticEnabled +
                ", newMediaEngineForNewVersion=" + newMediaEngineForNewVersion +
                ", copyMediaEngineForNewVersion=" + copyMediaEngineForNewVersion +
                ", cost=" + cost +
                ", confidence=" + confidence +
                ", numberOfFilesForProcessing=" + numberOfFilesForProcessing +
                ", service=" + service +
                ", providers=" + providers +
                ", providerPurgeAttempts=" + providerPurgeAttempts +
                ", excludedFileTypes='" + excludedFileTypes + '\'' +
                ", provider='" + provider + '\'' +
                ", tempPath='" + tempPath + '\'' +
                ", wordCountPerItem=" + wordCountPerItem +
                ", allowedMediaDuration=" + allowedMediaDuration +
                ", silentBetweenWords=" + silentBetweenWords +
                '}';
    }
}
