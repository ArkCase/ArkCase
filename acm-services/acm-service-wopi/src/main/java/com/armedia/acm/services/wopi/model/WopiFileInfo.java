package com.armedia.acm.services.wopi.model;

public class WopiFileInfo
{
    private Long id;

    private String name;

    private String extension;

    private String ownerId;

    private String version;

    private Long size;

    private String userId;

    private Boolean userCanWrite;

    public WopiFileInfo(Long id, String name, String extension, String ownerId, String version, Long size,
            String userId, Boolean userCanWrite)
    {
        this.id = id;
        this.name = name;
        this.extension = extension;
        this.ownerId = ownerId;
        this.version = version;
        this.size = size;
        this.userId = userId;
        this.userCanWrite = userCanWrite;
    }

    public Long getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public String getExtension()
    {
        return extension;
    }

    public String getOwnerId()
    {
        return ownerId;
    }

    public String getVersion()
    {
        return version;
    }

    public Long getSize()
    {
        return size;
    }

    public String getUserId()
    {
        return userId;
    }

    public Boolean getUserCanWrite()
    {
        return userCanWrite;
    }
}
