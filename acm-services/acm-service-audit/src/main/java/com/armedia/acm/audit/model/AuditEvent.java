package com.armedia.acm.audit.model;

import com.armedia.acm.data.converter.UUIDToStringConverter;
import com.google.common.base.Joiner;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@NotAudited
@Table(name = "acm_audit_log")
public class AuditEvent
{

    private static DateTimeFormatter dtf = ISODateTimeFormat.dateTime();

    @Id
    @TableGenerator(name = "acm_audit_log_gen", table = "acm_audit_log_id", pkColumnName = "cm_seq_name", valueColumnName = "cm_seq_num", pkColumnValue = "acm_audit_log", initialValue = 100, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "acm_audit_log_gen")
    @Column(name = "cm_audit_id")
    private Long id;

    @Column(name = "cm_audit_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date eventDate;

    @Column(name = "cm_audit_user")
    private String userId;

    @Column(name = "cm_audit_activity")
    private String fullEventType;

    @Column(name = "cm_audit_description")
    private String eventDescription;

    @Column(name = "cm_audit_activity_result")
    private String eventResult;

    @Column(name = "cm_audit_ip_address")
    private String ipAddress;

    @Column(name = "cm_object_type")
    private String objectType;

    @Column(name = "cm_object_id")
    private Long objectId;

    @Column(name = "cm_parent_object_type")
    private String parentObjectType;

    @Column(name = "cm_parent_object_id")
    private Long parentObjectId;

    @Column(name = "cm_audit_status")
    private String status;

    @Column(name = "cm_audit_track_id")
    private String trackId;

    @Column(name = "cm_audit_request_id")
    @Convert(converter = UUIDToStringConverter.class)
    private UUID requestId;

    @ElementCollection
    @CollectionTable(name = "acm_audit_log_property", joinColumns = @JoinColumn(name = "cm_audit_id"))
    @MapKeyColumn(name = "cm_audit_property_name")
    @Column(name = "cm_audit_property_value")
    private Map<String, String> eventProperties = new HashMap<>();

    @ManyToOne(optional = true)
    @JoinColumn(name = "cm_audit_activity", referencedColumnName = "cm_key", nullable = true, insertable = false, updatable = false)
    private AcmAuditLookup auditLookup;

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

    public String getEventDescription()
    {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription)
    {
        this.eventDescription = eventDescription;
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

    public String getParentObjectType()
    {
        return parentObjectType;
    }

    public void setParentObjectType(String parentObjectType)
    {
        this.parentObjectType = parentObjectType;
    }

    public Long getParentObjectId()
    {
        return parentObjectId;
    }

    public void setParentObjectId(Long parentObjectId)
    {
        this.parentObjectId = parentObjectId;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getTrackId()
    {
        return trackId;
    }

    public void setTrackId(String trackId)
    {
        this.trackId = trackId;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    @Transient
    public String getEventType()
    {
        // check if auditLookup is not null and get the type
        if (getAuditLookup() != null)
            return getAuditLookup().getAuditBuisinessName();

        /*
         * int lastDot = getFullEventType().lastIndexOf('.'); if (lastDot >= 0) { return
         * getFullEventType().substring(lastDot + 1, getFullEventType().length()); }
         */
        return getFullEventType();
    }

    public UUID getRequestId()
    {
        return requestId;
    }

    public void setRequestId(UUID requestId)
    {
        this.requestId = requestId;
    }

    public AcmAuditLookup getAuditLookup()
    {
        return auditLookup;
    }

    public void setAuditLookupMap(AcmAuditLookup auditLookup)
    {
        this.auditLookup = auditLookup;
    }

    public Map<String, String> getEventProperties()
    {
        return eventProperties;
    }

    public void setEventProperties(Map<String, String> eventProperties)
    {
        this.eventProperties = eventProperties;
    }

    @Override
    public String toString()
    {
        String eventPropertiesString = null;
        if (getEventProperties().size() > 0)
        {
            eventPropertiesString = Joiner.on(" | ").withKeyValueSeparator(": ").useForNull("").join(getEventProperties());
        }
        return "Event date/time: " + dtf.print(getEventDate().getTime()) + " | Originating IP address: " + getIpAddress() + " | RequestId: "
                + getRequestId()
                /// trackId is not used anymore
                // + " | Track Id: " + getTrackId()
                + " | User: " + getUserId() + " | Event type: " + getFullEventType() + " | Event result: " + getEventResult()
                + " | Object type: " + getObjectType() + " | Object Id: " + getObjectId() + " | Status: " + getStatus()
                + (eventPropertiesString == null ? "" : " | " + eventPropertiesString);
    }
}
