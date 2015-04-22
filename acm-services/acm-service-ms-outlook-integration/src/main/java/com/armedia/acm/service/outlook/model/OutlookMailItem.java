package com.armedia.acm.service.outlook.model;

import microsoft.exchange.webservices.data.property.complex.EmailAddress;

/**
 * Created by armdev on 4/20/15.
 */
public class OutlookMailItem extends OutlookItem
{

    private String from;
    private String sender;
    private Boolean read;

    public void setFrom(String from)
    {
        this.from = from;
    }

    public String getFrom()
    {
        return from;
    }

    public void setSender(String sender)
    {
        this.sender = sender;
    }

    public void setRead(Boolean read)
    {
        this.read = read;
    }

    public Boolean getRead()
    {
        return read;
    }
}
