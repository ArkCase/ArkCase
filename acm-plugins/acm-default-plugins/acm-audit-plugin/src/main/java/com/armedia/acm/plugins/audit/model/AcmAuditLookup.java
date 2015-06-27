package com.armedia.acm.plugins.audit.model;

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.data.AcmEntity;
import com.armedia.acm.plugins.audit.service.AuditConstants;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "acm_audit_event_type_lu")
public class AcmAuditLookup implements Serializable, AcmObject, AcmEntity {

    private static final long serialVersionUID = 2464137631399833851L;

    @Id
    @Column(name = "cm_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long auditLookupId;

    @Column(name = "cm_key")
    private String auditEventName;

    @Column(name = "cm_value")
    private String auditBuisinessName;

    @Column(name = "cm_order")
    private Long order;

    @Column(name = "cm_status")
    private String auditStatus;


    @Column(name = "cm_creator", nullable = false, insertable = true, updatable = false)
    private String creator;

    @Column(name = "cm_modifier", nullable = false, insertable = true, updatable = true)
    private String modifier;

    @Column(name = "cm_created", nullable = false, insertable = true, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "cm_modified", nullable = false, insertable = true, updatable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;


    public Long getAuditLookupId() {
        return auditLookupId;
    }

    public void setAuditLookupId(Long auditLookupId) {
        this.auditLookupId = auditLookupId;
    }

    public String getAuditBuisinessName() {
        return auditBuisinessName;
    }

    public void setAuditBuisinessName(String auditBuisinessName) {
        this.auditBuisinessName = auditBuisinessName;
    }

    public Long getOrder() {
        return order;
    }

    public void setOrder(Long order) {
        this.order = order;
    }

    public String getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(String auditStatus) {
        this.auditStatus = auditStatus;
    }

    public String getAuditEventName() {
        return auditEventName;
    }

    public void setAuditEventName(String auditEventName) {
        this.auditEventName = auditEventName;
    }

    @Override
    public Date getModified() {
        return modified;
    }

    @Override
    public void setModified(Date modified) {
        this.modified = modified;
    }

    @Override
    public Date getCreated() {
        return created;
    }

    @Override
    public void setCreated(Date created) {
        this.created = created;
    }

    @Override
    public String getModifier() {
        return modifier;
    }

    @Override
    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    @Override
    public String getCreator() {
        return creator;
    }

    @Override
    public void setCreator(String creator) {
        this.creator = creator;
    }

    @Override
    public String getObjectType() {
        return AuditConstants.OBJECT_TYPE;
    }

    @Override
    public Long getId() {
        return auditLookupId;
    }
}
