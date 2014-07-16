package com.armedia.acm.event;

import org.springframework.context.ApplicationEvent;

import java.util.Date;
import java.util.Map;

public abstract class AcmEvent extends ApplicationEvent
{
    private String eventType;
    private String userId;
    private Date eventDate;
    private boolean succeeded;
    private String ipAddress;
    private String objectType;
    private Long objectId;
    private Map<String, Object> eventProperties;

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

    public String getObjectType()
    {
        return objectType;
    }

    public void setObjectType(String objectType)
    {
        this.objectType = objectType;
    }

    public Long getObjectId()
    {
        return objectId;
    }

    public void setObjectId(Long objectId)
    {
        this.objectId = objectId;
    }

    public Map<String, Object> getEventProperties()
    {
        return eventProperties;
    }

    public void setEventProperties(Map<String, Object> eventProperties)
    {
        this.eventProperties = eventProperties;
    }
}
