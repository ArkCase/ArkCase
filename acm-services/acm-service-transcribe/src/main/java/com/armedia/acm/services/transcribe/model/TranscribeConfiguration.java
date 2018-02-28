package com.armedia.acm.services.transcribe.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 02/28/2018
 */
public class TranscribeConfiguration implements Serializable
{
    private boolean enabled;
    private boolean automaticEnabled;
    private boolean newTranscriptionForNewVersion;
    private boolean copyTranscriptionForNewVersion;
    private double cost;
    private int confidence;
    private int numberOfFilesForProcessing;
    private int wordCountPerItem;
    private TranscribeServiceProvider provider;
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

    public double getCost()
    {
        return cost;
    }

    public void setCost(double cost)
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
}
