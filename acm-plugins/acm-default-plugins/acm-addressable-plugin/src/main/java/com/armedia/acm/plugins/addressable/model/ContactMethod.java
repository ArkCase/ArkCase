package com.armedia.acm.plugins.addressable.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "acm_contact_method")
public class ContactMethod implements Serializable
{
    private static final long serialVersionUID = 1827685289454605556L;
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @Id
    @Column(name = "cm_contact_method_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cm_contact_method_created", nullable = false, insertable = true, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "cm_contact_method_creator", insertable = true, updatable = false)
    private String creator;

    @Column(name = "cm_contact_method_modified", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    @Column(name = "cm_contact_method_modifier")
    private String modifier;

    @Column(name = "cm_contact_method_status")
    private String status;

    @Column(name = "cm_contact_type")
    private String type;

    @Column(name = "cm_contact_value")
    private String value;

    /**
     * Value to use for modifier if this object is saved.  This value may be set by containing objects
     * even when this address object has not been modified.  If this object is actually updated,
     * this value becomes the modifier.
     */
    @Transient
    private String modifierToBe;

    @PrePersist
    protected void beforeInsert()
    {
        if ( getStatus() == null || getStatus().trim().isEmpty() )
        {
            setStatus("ACTIVE");
        }

        if ( getCreated() == null )
        {
            setCreated(new Date());
        }

        if ( getModified() == null )
        {
            setModified(new Date());
        }
    }

    @PreUpdate
    protected void beforeUpdate()
    {
        setModified(new Date());
        setModifier(getModifierToBe());
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
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

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getModifierToBe()
    {
        return modifierToBe;
    }

    public void setModifierToBe(String modifierToBe)
    {
        this.modifierToBe = modifierToBe;
    }
}
