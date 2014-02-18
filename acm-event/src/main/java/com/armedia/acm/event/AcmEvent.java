package com.armedia.acm.event;

import org.springframework.context.ApplicationEvent;

import java.util.Date;

public abstract class AcmEvent extends ApplicationEvent
{
    private String eventType;
    private String userId;
    private Date eventDate;
    private boolean succeeded;
    private String ipAddress;

    public AcmEvent(Object source)
    {
        super(source);
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public boolean isSucceeded() {
        return succeeded;
    }

    public void setSucceeded(boolean succeeded) {
        this.succeeded = succeeded;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
