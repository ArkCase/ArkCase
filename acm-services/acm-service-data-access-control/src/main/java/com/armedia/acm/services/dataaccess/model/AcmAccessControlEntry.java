package com.armedia.acm.services.dataaccess.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "acm_access_control_entry")
public class AcmAccessControlEntry implements Serializable
{
    private static final long serialVersionUID = 6980166708271263205L;

    @Id
    @Column(name = "cm_access_control_entry_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cm_object_type")
    private String objectType;

    @Column(name = "cm_object_id")
    private Long objectId;

    @Column(name = "cm_object_state")
    private String objectState;

    @Column(name = "cm_access_level")
    private String accessLevel;

    @Column(name = "cm_accessor_type")
    private String accessorType;

    @Column(name = "cm_accessor_id")
    private String accessorId;

    @Column(name = "cm_access_decision")
    private String accessDecision;

    @Column(name = "cm_access_decision_reason")
    private String accessDecisionReason;

    // need to send a string to the database, but we want a boolean field.  So define the field as a string,
    // and define the getter/setter as boolean.
    @Column(name = "cm_allow_discretionary_update")
    private String allowDiscretionaryUpdate;

    @Column(name = "cm_access_control_created", nullable = false, insertable = true, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "cm_access_control_creator", insertable = true, updatable = false)
    private String creator;

    @Column(name = "cm_access_control_modified", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    @Column(name = "cm_access_control_modifier")
    private String modifier;

    @PrePersist
    protected void beforeInsert()
    {
        setCreated(new Date());
        setModified(new Date());
    }

    public Boolean getAllowDiscretionaryUpdate()
    {
        return Boolean.valueOf(allowDiscretionaryUpdate);
    }

    public void setAllowDiscretionaryUpdate(Boolean allowDiscretionaryUpdate)
    {
        this.allowDiscretionaryUpdate = allowDiscretionaryUpdate == null ? null : String.valueOf(allowDiscretionaryUpdate);
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
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

    public String getObjectState()
    {
        return objectState;
    }

    public void setObjectState(String objectState)
    {
        this.objectState = objectState;
    }

    public String getAccessLevel()
    {
        return accessLevel;
    }

    public void setAccessLevel(String accessLevel)
    {
        this.accessLevel = accessLevel;
    }

    public String getAccessorType()
    {
        return accessorType;
    }

    public void setAccessorType(String accessorType)
    {
        this.accessorType = accessorType;
    }

    public String getAccessorId()
    {
        return accessorId;
    }

    public void setAccessorId(String accessorId)
    {
        this.accessorId = accessorId;
    }

    public String getAccessDecision()
    {
        return accessDecision;
    }

    public void setAccessDecision(String accessDecision)
    {
        this.accessDecision = accessDecision;
    }

    public String getAccessDecisionReason()
    {
        return accessDecisionReason;
    }

    public void setAccessDecisionReason(String accessDecisionReason)
    {
        this.accessDecisionReason = accessDecisionReason;
    }

    public Date getCreated()
    {
        return created;
    }

    public void setCreated(Date created)
    {
        this.created = created;
    }

    public String getCreator()
    {
        return creator;
    }

    public void setCreator(String creator)
    {
        this.creator = creator;
    }

    public Date getModified()
    {
        return modified;
    }

    public void setModified(Date modified)
    {
        this.modified = modified;
    }

    public String getModifier()
    {
        return modifier;
    }

    public void setModifier(String modifier)
    {
        this.modifier = modifier;
    }
}
