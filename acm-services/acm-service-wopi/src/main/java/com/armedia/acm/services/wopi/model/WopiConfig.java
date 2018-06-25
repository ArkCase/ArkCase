package com.armedia.acm.services.wopi.model;

public class WopiConfig
{
    private String wopiHostUrl;
    private String wopiTenantDomain;
    private String wopiTenantProtocol;
    private Long wopiTenantPort;
    private String wopiTenantContext;
    private String wopiTenantAccessTokenParamName;
    private String wopiTenantFileIdParamName;
    private Long wopiLockDuration;

    public String getWopiHostUrl(Long fileId, String accessToken)
    {
        return String.format(wopiHostUrl, fileId, accessToken, wopiTenantProtocol, wopiTenantDomain, wopiTenantPort,
                wopiTenantContext, wopiTenantAccessTokenParamName, wopiTenantFileIdParamName);
    }

    public void setWopiHostUrl(String wopiHostUrl)
    {
        this.wopiHostUrl = wopiHostUrl;
    }

    public String getWopiTenantDomain()
    {
        return wopiTenantDomain;
    }

    public void setWopiTenantDomain(String wopiTenantDomain)
    {
        this.wopiTenantDomain = wopiTenantDomain;
    }

    public String getWopiTenantProtocol()
    {
        return wopiTenantProtocol;
    }

    public void setWopiTenantProtocol(String wopiTenantProtocol)
    {
        this.wopiTenantProtocol = wopiTenantProtocol;
    }

    public Long getWopiTenantPort()
    {
        return wopiTenantPort;
    }

    public void setWopiTenantPort(Long wopiTenantPort)
    {
        this.wopiTenantPort = wopiTenantPort;
    }

    public String getWopiTenantContext()
    {
        return wopiTenantContext;
    }

    public void setWopiTenantContext(String wopiTenantContext)
    {
        this.wopiTenantContext = wopiTenantContext;
    }

    public String getWopiTenantAccessTokenParamName()
    {
        return wopiTenantAccessTokenParamName;
    }

    public void setWopiTenantAccessTokenParamName(String wopiTenantAccessTokenParamName)
    {
        this.wopiTenantAccessTokenParamName = wopiTenantAccessTokenParamName;
    }

    public String getWopiTenantFileIdParamName()
    {
        return wopiTenantFileIdParamName;
    }

    public void setWopiTenantFileIdParamName(String wopiTenantFileIdParamName)
    {
        this.wopiTenantFileIdParamName = wopiTenantFileIdParamName;
    }

    public Long getWopiLockDuration()
    {
        return wopiLockDuration;
    }

    public void setWopiLockDuration(Long wopiLockDuration)
    {
        this.wopiLockDuration = wopiLockDuration;
    }
}
