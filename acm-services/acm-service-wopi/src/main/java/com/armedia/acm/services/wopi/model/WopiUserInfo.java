package com.armedia.acm.services.wopi.model;

public class WopiUserInfo
{
    private final String fullName;
    private final String id;
    private final String lang;

    public WopiUserInfo(String fullName, String id, String lang)
    {
        this.fullName = fullName;
        this.id = id;
        this.lang = lang;
    }

    public String getFullName()
    {
        return fullName;
    }

    public String getId()
    {
        return id;
    }

    public String getLang()
    {
        return lang;
    }
}
