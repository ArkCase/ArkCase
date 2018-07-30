package com.armedia.acm.services.wopi.model;

public class WopiSessionInfo
{
    private final String accessToken;
    private final String userId;
    private final String fileId;
    private final boolean readOnly;
    private final boolean userCanWrite;

    public WopiSessionInfo(String accessToken, String userId, String fileId, boolean readOnly, boolean userCanWrite)
    {
        this.accessToken = accessToken;
        this.userId = userId;
        this.fileId = fileId;
        this.readOnly = readOnly;
        this.userCanWrite = userCanWrite;
    }

    public String getAccessToken()
    {
        return accessToken;
    }

    public String getUserId()
    {
        return userId;
    }

    public String getFileId()
    {
        return fileId;
    }

    public boolean isReadOnly()
    {
        return readOnly;
    }

    public boolean isUserCanWrite()
    {
        return userCanWrite;
    }
}
