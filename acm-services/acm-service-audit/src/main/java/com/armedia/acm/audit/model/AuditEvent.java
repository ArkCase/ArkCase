package com.armedia.acm.audit.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import java.util.Date;

@Entity
@Table(name = "acm_audit_log")
public class AuditEvent
{

    @Id
    @Column(name = "cm_audit_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date eventDate;

    @Column(name = "cm_audit_user")
    private String userId;

    @Column(name = "cm_audit_activity")
    private String fullEventType;

    @Column(name = "cm_audit_activity_result")
    private String eventResult;

    @Column(name = "cm_audit_ip_address")
    private String ipAddress;

    @Column(name = "cm_object_type")
    private String objectType;

    @Column(name = "cm_object_id")
    private Long objectId;


    public Date getEventDate()
    {
        return eventDate;
    }

    public void setEventDate(Date eventDate)
    {
        this.eventDate = eventDate;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public String getFullEventType()
    {
        return fullEventType;
    }

    public void setFullEventType(String fullEventType)
    {
        this.fullEventType = fullEventType;
    }

    public String getEventResult()
    {
        return eventResult;
    }

    public void setEventResult(String eventResult)
    {
        this.eventResult = eventResult;
    }

    public String getIpAddress()
    {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress)
    {
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

    @Transient
    public String getEventType()
    {
        int lastDot = getFullEventType().lastIndexOf('.');
        if ( lastDot >= 0 )
        {
            return getFullEventType().substring(lastDot + 1, getFullEventType().length());
        }
        return getFullEventType();
    }
}
