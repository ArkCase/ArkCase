package com.armedia.acm.services.users.model.tracker;

public class UserTracker
{

    private String ipAddress;

    public UserTracker(String ipAddress)
    {
        this.ipAddress = ipAddress;
    }

    public String getIpAddress()
    {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress)
    {
        this.ipAddress = ipAddress;
    }

}
