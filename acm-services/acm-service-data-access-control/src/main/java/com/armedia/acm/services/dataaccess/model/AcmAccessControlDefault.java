package com.armedia.acm.services.dataaccess.model;

import com.armedia.acm.data.AcmEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "acm_access_control_default")
public class AcmAccessControlDefault implements Serializable, AcmEntity
{

    private static final long serialVersionUID = 1687973310641520579L;

    @Id
    @Column(name = "cm_access_control_default_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cm_object_type", insertable = true, updatable = false)
    private String objectType;

    @Column(name = "cm_object_state", insertable = true, updatable = false)
    private String objectState;

    @Column(name = "cm_access_level", insertable = true, updatable = false)
    private String accessLevel;

    @Column(name = "cm_accessor_type", insertable = true, updatable = false)
    private String accessorType;

    @Column(name = "cm_access_decision")
    private String accessDecision;

    // need to send a string to the database, but we want a boolean field.  So define the field as a string,
    // and define the getter/setter as boolean.
    @Column(name = "cm_allow_discretionary_update")
    private String allowDiscretionaryUpdate;

    @Column(name = "cm_access_default_created", nullable = false, insertable = true, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "cm_access_default_creator", insertable = true, updatable = false)
    private String creator;

    @Column(name = "cm_access_default_modified", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    @Column(name = "cm_access_default_modifier")
    private String modifier;

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

    public String getAccessDecision()
    {
        return accessDecision;
    }

    public void setAccessDecision(String accessDecision)
    {
        this.accessDecision = accessDecision;
    }

    public Boolean getAllowDiscretionaryUpdate()
    {
        return Boolean.valueOf(allowDiscretionaryUpdate);
    }

    public void setAllowDiscretionaryUpdate(Boolean allowDiscretionaryUpdate)
    {
        this.allowDiscretionaryUpdate = allowDiscretionaryUpdate == null ? null : String.valueOf(allowDiscretionaryUpdate);
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
    public String getModifier()
    {
        return modifier;
    }

    @Override
    public void setModifier(String modifier)
    {
        this.modifier = modifier;
    }
}
