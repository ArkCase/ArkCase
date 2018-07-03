package com.armedia.acm.services.wopi.model;

public class WopiLockInfo
{
    private Long lockId;

    private Long expiryEpochSeconds;

    public WopiLockInfo(Long lockId, Long expiryEpochSeconds)
    {
        this.lockId = lockId;
        this.expiryEpochSeconds = expiryEpochSeconds;
    }

    public Long getLockId()
    {
        return lockId;
    }

    public Long getExpiryEpochSeconds()
    {
        return expiryEpochSeconds;
    }
}
