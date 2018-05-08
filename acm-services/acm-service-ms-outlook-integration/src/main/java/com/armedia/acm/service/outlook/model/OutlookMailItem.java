package com.armedia.acm.service.outlook.model;

/**
 * Created by armdev on 4/20/15.
 */
public class OutlookMailItem extends OutlookItem
{

    private String from;
    private String sender;
    private Boolean read;

    public String getFrom()
    {
        return from;
    }

    public void setFrom(String from)
    {
        this.from = from;
    }

    public void setSender(String sender)
    {
        this.sender = sender;
    }

    public Boolean getRead()
    {
        return read;
    }

    public void setRead(Boolean read)
    {
        this.read = read;
    }
}
