package com.armedia.acm.plugins.onlyoffice.model;

import com.armedia.acm.plugins.onlyoffice.model.callback.History;
import com.armedia.acm.plugins.onlyoffice.model.config.User;

public class DocumentHistoryVersion extends History
{
    private String created;
    private String key;
    private User user;
    private String version;

    public String getCreated()
    {
        return created;
    }

    public void setCreated(String created)
    {
        this.created = created;
    }

    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

}
