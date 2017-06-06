package com.armedia.acm.audit.model;

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.data.AcmEntity;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

@Entity
@NotAudited
@Table(name = "acm_audit_event_type_lu")
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class AcmAuditLookup implements Serializable, AcmObject, AcmEntity
{

    private static final long serialVersionUID = 2464137631399833851L;

    @Id
    @TableGenerator(name = "audit_event_type_lu_gen",
            table = "acm_audit_event_type_lu_id",
            pkColumnName = "cm_seq_name",
            valueColumnName = "cm_seq_num",
            pkColumnValue = "acm_audit_event_type_lu",
            initialValue = 100,
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "audit_event_type_lu_gen")
    @Column(name = "cm_id")
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


    public Long getAuditLookupId()
    {
        return auditLookupId;
    }

    public void setAuditLookupId(Long auditLookupId)
    {
        this.auditLookupId = auditLookupId;
    }

    public String getAuditBuisinessName()
    {
        return auditBuisinessName;
    }

    public void setAuditBuisinessName(String auditBuisinessName)
    {
        this.auditBuisinessName = auditBuisinessName;
    }

    public Long getOrder()
    {
        return order;
    }

    public void setOrder(Long order)
    {
        this.order = order;
    }

    public String getAuditStatus()
    {
        return auditStatus;
    }

    public void setAuditStatus(String auditStatus)
    {
        this.auditStatus = auditStatus;
    }

    public String getAuditEventName()
    {
        return auditEventName;
    }

    public void setAuditEventName(String auditEventName)
    {
        this.auditEventName = auditEventName;
    }

    @Override
    public Date getModified()
    {
        return modified;
    }

    @Override
    public void setModified(Date modified)
    {
        this.modified = modified;
    }

    @Override
    public Date getCreated()
    {
        return created;
    }

    @Override
    public void setCreated(Date created)
    {
        this.created = created;
    }

    @Override
    public String getModifier()
    {
        return modifier;
    }

    @Override
    public void setModifier(String modifier)
    {
        this.modifier = modifier;
    }

    @Override
    public String getCreator()
    {
        return creator;
    }

    @Override
    public void setCreator(String creator)
    {
        this.creator = creator;
    }

    @Override
    public String getObjectType()
    {
        return AuditConstants.OBJECT_TYPE;
    }

    @Override
    public Long getId()
    {
        return auditLookupId;
    }
}
