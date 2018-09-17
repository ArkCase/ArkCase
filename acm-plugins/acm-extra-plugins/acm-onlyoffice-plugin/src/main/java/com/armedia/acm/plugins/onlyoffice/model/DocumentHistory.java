package com.armedia.acm.plugins.onlyoffice.model;

import java.util.List;

public class DocumentHistory
{
    private List<DocumentHistoryVersion> history;
    private String currentVersion;

    public List<DocumentHistoryVersion> getHistory()
    {
        return history;
    }

    public void setHistory(List<DocumentHistoryVersion> history)
    {
        this.history = history;
    }

    public String getCurrentVersion()
    {
        return currentVersion;
    }

    public void setCurrentVersion(String currentVersion)
    {
        this.currentVersion = currentVersion;
    }
}
