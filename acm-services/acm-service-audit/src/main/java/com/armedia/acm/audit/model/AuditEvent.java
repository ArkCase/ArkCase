package com.armedia.acm.audit.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "acm_audit_log")
public class AuditEvent {

    @Id
    @TableGenerator(name = "acm_audit_log_gen",
            table = "acm_audit_log_id",
            pkColumnName = "cm_seq_name",
            valueColumnName = "cm_seq_num",
            pkColumnValue = "acm_audit_log",
            initialValue = 100,
            allocationSize = 1)
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

    @Column(name = "cm_audit_activity_result")
    private String eventResult;

    @Column(name = "cm_audit_ip_address")
    private String ipAddress;

    @Column(name = "cm_object_type")
    private String objectType;

    @Column(name = "cm_object_id")
    private Long objectId;

    @Column(name = "cm_audit_status")
    private String status;

    @Column(name = "cm_audit_track_id")
    private String trackId;

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFullEventType() {
        return fullEventType;
    }

    public void setFullEventType(String fullEventType) {
        this.fullEventType = fullEventType;
    }

    public String getEventResult() {
        return eventResult;
    }

    public void setEventResult(String eventResult) {
        this.eventResult = eventResult;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Transient
    public String getEventType() {
        int lastDot = getFullEventType().lastIndexOf('.');
        if (lastDot >= 0) {
            return getFullEventType().substring(lastDot + 1, getFullEventType().length());
        }
        return getFullEventType();
    }
}
