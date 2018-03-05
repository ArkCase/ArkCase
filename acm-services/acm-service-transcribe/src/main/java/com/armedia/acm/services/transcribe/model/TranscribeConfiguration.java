package com.armedia.acm.services.transcribe.model;

import com.armedia.acm.services.transcribe.annotation.TranscribeConfigurationProperties;
import com.armedia.acm.services.transcribe.annotation.TranscribeConfigurationProperty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 02/28/2018
 */
@TranscribeConfigurationProperties(path = "${user.home}/.arkcase/acm/transcribe.properties")
public class TranscribeConfiguration implements Serializable
{
    @TranscribeConfigurationProperty(key = "transcribe.enabled")
    private boolean enabled;

    @TranscribeConfigurationProperty(key = "transcribe.automatic.enabled")
    private boolean automaticEnabled;

    @TranscribeConfigurationProperty(key = "transcribe.new.transcribe.for.new.version")
    private boolean newTranscriptionForNewVersion;

    @TranscribeConfigurationProperty(key = "transcribe.copy.transcribe.for.new.version")
    private boolean copyTranscriptionForNewVersion;

    @TranscribeConfigurationProperty(key = "transcribe.cost")
    private BigDecimal cost;

    @TranscribeConfigurationProperty(key = "transcribe.confidence")
    private int confidence;

    @TranscribeConfigurationProperty(key = "transcribe.number.of.files.for.processing")
    private int numberOfFilesForProcessing;

    @TranscribeConfigurationProperty(key = "transcribe.word.count.per.item")
    private int wordCountPerItem;

    @TranscribeConfigurationProperty(key = "transcribe.provider")
    private TranscribeServiceProvider provider;

    @TranscribeConfigurationProperty(key = "transcribe.providers", write = false)
    private List<TranscribeServiceProvider> providers;

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

    public boolean isNewTranscriptionForNewVersion()
    {
        return newTranscriptionForNewVersion;
    }

    public void setNewTranscriptionForNewVersion(boolean newTranscriptionForNewVersion)
    {
        if (newTranscriptionForNewVersion)
        {
            setCopyTranscriptionForNewVersion(false);
        }
        this.newTranscriptionForNewVersion = newTranscriptionForNewVersion;
    }

    public boolean isCopyTranscriptionForNewVersion()
    {
        return copyTranscriptionForNewVersion;
    }

    public void setCopyTranscriptionForNewVersion(boolean copyTranscriptionForNewVersion)
    {
        if (copyTranscriptionForNewVersion)
        {
            setNewTranscriptionForNewVersion(false);
        }
        this.copyTranscriptionForNewVersion = copyTranscriptionForNewVersion;
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

    public int getWordCountPerItem()
    {
        return wordCountPerItem;
    }

    public void setWordCountPerItem(int wordCountPerItem)
    {
        this.wordCountPerItem = wordCountPerItem;
    }

    public TranscribeServiceProvider getProvider()
    {
        return provider;
    }

    public void setProvider(TranscribeServiceProvider provider)
    {
        this.provider = provider;
    }

    public List<TranscribeServiceProvider> getProviders()
    {
        return providers;
    }

    public void setProviders(List<TranscribeServiceProvider> providers)
    {
        this.providers = providers;
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
                newTranscriptionForNewVersion == that.newTranscriptionForNewVersion &&
                copyTranscriptionForNewVersion == that.copyTranscriptionForNewVersion &&
                that.cost != null ? that.cost.equals(cost) : cost == null &&
                confidence == that.confidence &&
                numberOfFilesForProcessing == that.numberOfFilesForProcessing &&
                wordCountPerItem == that.wordCountPerItem &&
                provider == that.provider;
    }

    @Override
    public int hashCode()
    {

        return Objects.hash(enabled, automaticEnabled, newTranscriptionForNewVersion, copyTranscriptionForNewVersion, cost, confidence,
                numberOfFilesForProcessing, wordCountPerItem, provider);
    }

    @Override
    public String toString()
    {
        return "TranscribeConfiguration{" +
                "enabled=" + enabled +
                ", automaticEnabled=" + automaticEnabled +
                ", newTranscriptionForNewVersion=" + newTranscriptionForNewVersion +
                ", copyTranscriptionForNewVersion=" + copyTranscriptionForNewVersion +
                ", cost=" + cost +
                ", confidence=" + confidence +
                ", numberOfFilesForProcessing=" + numberOfFilesForProcessing +
                ", wordCountPerItem=" + wordCountPerItem +
                ", provider=" + provider +
                '}';
    }
}
