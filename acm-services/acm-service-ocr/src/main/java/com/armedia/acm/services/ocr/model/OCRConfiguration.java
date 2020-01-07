package com.armedia.acm.services.ocr.model;

/*-
 * #%L
 * ACM Default Plugin: admin
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

@JsonSerialize(as = OCRConfiguration.class)
public class OCRConfiguration implements MediaEngineConfiguration
{
    @JsonProperty("ocr.enabled")
    @Value("${ocr.enabled}")
    private boolean enabled;

    @JsonProperty("ocr.automaticEnabled")
    @Value("${ocr.automaticEnabled}")
    private boolean automaticEnabled;

    @JsonProperty("ocr.newMediaEngineForNewVersion")
    @Value("${ocr.newMediaEngineForNewVersion}")
    private boolean newMediaEngineForNewVersion;

    @JsonProperty("ocr.copyMediaEngineForNewVersion")
    @Value("${ocr.copyMediaEngineForNewVersion}")
    private boolean copyMediaEngineForNewVersion;

    @JsonProperty("ocr.cost")
    @Value("${ocr.cost}")
    private BigDecimal cost;

    @JsonProperty("ocr.confidence")
    @Value("${ocr.confidence}")
    private int confidence;

    @JsonProperty("ocr.numberOfFilesForProcessing")
    @Value("${ocr.numberOfFilesForProcessing}")
    private int numberOfFilesForProcessing;

    @JsonProperty("ocr.service")
    @Value("${ocr.service}")
    private MediaEngineServices service;

    @JsonProperty("ocr.providers")
    @Value("#{'${ocr.providers}'.split(',')}")
    private List<String> providers;

    @JsonProperty("ocr.providerPurgeAttempts")
    @Value("${ocr.providerPurgeAttempts}")
    private int providerPurgeAttempts;

    @JsonProperty("ocr.excludedFileTypes")
    @Value("${ocr.excludedFileTypes}")
    private String excludedFileTypes;

    @JsonProperty("ocr.provider")
    @Value("${ocr.provider}")
    private String provider;

    @JsonProperty("ocr.tempPath")
    @Value("${ocr.tempPath}")
    private String tempPath;

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

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        OCRConfiguration that = (OCRConfiguration) o;
        return enabled == that.enabled &&
                automaticEnabled == that.automaticEnabled &&
                newMediaEngineForNewVersion == that.newMediaEngineForNewVersion &&
                copyMediaEngineForNewVersion == that.copyMediaEngineForNewVersion &&
                confidence == that.confidence &&
                numberOfFilesForProcessing == that.numberOfFilesForProcessing &&
                providerPurgeAttempts == that.providerPurgeAttempts &&
                Objects.equals(cost, that.cost) &&
                service == that.service &&
                Objects.equals(providers, that.providers) &&
                Objects.equals(excludedFileTypes, that.excludedFileTypes) &&
                Objects.equals(provider, that.provider) &&
                Objects.equals(tempPath, that.tempPath);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(enabled, automaticEnabled, newMediaEngineForNewVersion, copyMediaEngineForNewVersion, cost, confidence,
                numberOfFilesForProcessing, service, providers, providerPurgeAttempts, excludedFileTypes, provider, tempPath);
    }

    @Override
    public String toString()
    {
        return "OCRConfiguration{" +
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
                '}';
    }
}
