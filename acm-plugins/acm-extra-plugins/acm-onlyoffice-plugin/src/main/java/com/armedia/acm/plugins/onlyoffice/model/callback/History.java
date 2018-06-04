package com.armedia.acm.plugins.onlyoffice.model.callback;

import java.util.List;

public class History
{
    private String serverVersion;
    private List<Change> changes;

    public String getServerVersion() {
        return serverVersion;
    }

    public void setServerVersion(String serverVersion) {
        this.serverVersion = serverVersion;
    }

    public List<Change> getChanges() {
        return changes;
    }

    public void setChanges(List<Change> changes) {
        this.changes = changes;
    }
}
