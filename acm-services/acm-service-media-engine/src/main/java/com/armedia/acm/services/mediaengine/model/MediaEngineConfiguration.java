package com.armedia.acm.services.mediaengine.model;

/*-
 * #%L
 * ACM Service: Media engine
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

import com.armedia.acm.services.mediaengine.annotation.ConfigurationProperty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 02/28/2018
 */

public class MediaEngineConfiguration implements Serializable
{
    @ConfigurationProperty(key = "mediaengine.enabled")
    private boolean enabled;

    @ConfigurationProperty(key = "mediaengine.automatic.enabled")
    private boolean automaticEnabled;

    @ConfigurationProperty(key = "mediaengine.new.mediaengine.for.new.version")
    private boolean newMediaEngineForNewVersion;

    @ConfigurationProperty(key = "mediaengine.copy.mediaengine.for.new.version")
    private boolean copyMediaEngineForNewVersion;

    @ConfigurationProperty(key = "mediaengine.cost")
    private BigDecimal cost;

    @ConfigurationProperty(key = "mediaengine.confidence")
    private int confidence;

    @ConfigurationProperty(key = "mediaengine.number.of.files.for.processing")
    private int numberOfFilesForProcessing;

    @ConfigurationProperty(key = "mediaengine.service")
    private MediaEngineServices service;

    @ConfigurationProperty(key = "mediaengine.providers", write = false)
    private List<String> providers;

    @ConfigurationProperty(key = "mediaengine.provider.purge.attempts")
    private int providerPurgeAttempts;

    @ConfigurationProperty(key = "mediaengine.excludedFileTypes")
    private String excludedFileTypes;

    @ConfigurationProperty(key = "mediaengine.provider")
    private String provider;

    public String getProvider()
    {
        return provider;
    }

    public void setProvider(String provider)
    {
        this.provider = provider;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public boolean isAutomaticEnabled()
    {
        return automaticEnabled;
    }

    public void setAutomaticEnabled(boolean automaticEnabled)
    {
        this.automaticEnabled = automaticEnabled;
    }

    public boolean isNewMediaEngineForNewVersion()
    {
        return newMediaEngineForNewVersion;
    }

    public void setNewMediaEngineForNewVersion(boolean newMediaEngineForNewVersion)
    {
        if (newMediaEngineForNewVersion)
        {
            setCopyMediaEngineForNewVersion(false);
        }
        this.newMediaEngineForNewVersion = newMediaEngineForNewVersion;
    }

    public boolean isCopyMediaEngineForNewVersion()
    {
        return copyMediaEngineForNewVersion;
    }

    public void setCopyMediaEngineForNewVersion(boolean copyMediaEngineForNewVersion)
    {
        if (copyMediaEngineForNewVersion)
        {
            setNewMediaEngineForNewVersion(false);
        }
        this.copyMediaEngineForNewVersion = copyMediaEngineForNewVersion;
    }

    public BigDecimal getCost()
    {
        return cost;
    }

    public void setCost(BigDecimal cost)
    {
        this.cost = cost;
    }

    public int getConfidence()
    {
        return confidence;
    }

    public void setConfidence(int confidence)
    {
        this.confidence = confidence;
    }

    public int getNumberOfFilesForProcessing()
    {
        return numberOfFilesForProcessing;
    }

    public void setNumberOfFilesForProcessing(int numberOfFilesForProcessing)
    {
        this.numberOfFilesForProcessing = numberOfFilesForProcessing;
    }

    public MediaEngineServices getService()
    {
        return service;
    }

    public void setService(MediaEngineServices service)
    {
        this.service = service;
    }

    public List<String> getProviders()
    {
        return providers;
    }

    public void setProviders(List<String> providers)
    {
        this.providers = providers;
    }

    public int getProviderPurgeAttempts()
    {
        return providerPurgeAttempts;
    }

    public void setProviderPurgeAttempts(int providerPurgeAttempts)
    {
        this.providerPurgeAttempts = providerPurgeAttempts;
    }

    public String getExcludedFileTypes()
    {
        return excludedFileTypes;
    }

    public void setExcludedFileTypes(String excludedFileTypes)
    {
        this.excludedFileTypes = excludedFileTypes;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        MediaEngineConfiguration that = (MediaEngineConfiguration) o;
        return enabled == that.enabled &&
                automaticEnabled == that.automaticEnabled &&
                newMediaEngineForNewVersion == that.newMediaEngineForNewVersion &&
                copyMediaEngineForNewVersion == that.copyMediaEngineForNewVersion &&
                that.cost != null ? that.cost.equals(cost)
                        : cost == null &&
                                confidence == that.confidence &&
                                numberOfFilesForProcessing == that.numberOfFilesForProcessing &&
                                service == that.service &&
                                providerPurgeAttempts == that.providerPurgeAttempts &&
                                excludedFileTypes == that.excludedFileTypes;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(enabled, automaticEnabled, newMediaEngineForNewVersion, copyMediaEngineForNewVersion, cost, confidence,
                numberOfFilesForProcessing, service, providerPurgeAttempts, excludedFileTypes);
    }

    @Override
    public String toString()
    {
        return "MediaEngineConfiguration{" +
                "enabled=" + enabled +
                ", automaticEnabled=" + automaticEnabled +
                ", newMediaEngineForNewVersion=" + newMediaEngineForNewVersion +
                ", copyMediaEngineForNewVersion=" + copyMediaEngineForNewVersion +
                ", cost=" + cost +
                ", confidence=" + confidence +
                ", numberOfFilesForProcessing=" + numberOfFilesForProcessing +
                ", service=" + service +
                ", providerPurgeAttempts=" + providerPurgeAttempts +
                ", excludedFileTypes=" + excludedFileTypes +
                '}';
    }
}
