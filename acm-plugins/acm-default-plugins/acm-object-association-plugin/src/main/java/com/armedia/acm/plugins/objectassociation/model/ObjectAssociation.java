package com.armedia.acm.plugins.objectassociation.model;

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
import java.util.Date;

@Entity
@Table(name = "acm_object_association")
public class ObjectAssociation
{

    @Id
    @Column(name = "cm_association_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long associationId;

    @Column(name = "cm_association_status")
    private String status;

    @Column(name = "cm_parent_name")
    private String parentName;

    @Column(name = "cm_parent_type")
    private String parentType;

    @Column(name = "cm_parent_id")
    private Long parentId;

    @Column(name = "cm_target_name")
    private String targetName;

    @Column(name = "cm_target_type")
    private String targetType;

    @Column(name = "cm_target_id")
    private Long targetId;

    @Column(name = "cm_object_assn_created", nullable = false, insertable = true, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "cm_object_assn_creator", insertable = true, updatable = false)
    private String creator;

    @Column(name = "cm_object_assn_modified", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    @Column(name = "cm_object_assn_modifier")
    private String modifier;

    @PrePersist
    protected void beforeInsert()
    {
        setCreated(new Date());
        setModified(new Date());

        if ( getStatus() == null || getStatus().trim().isEmpty() )
        {
            setStatus("ACTIVE");
        }
    }

    @PreUpdate
    protected void beforeUpdate()
    {
        setModified(new Date());
    }

    public Long getAssociationId()
    {
        return associationId;
    }

    public void setAssociationId(Long associationId)
    {
        this.associationId = associationId;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getParentName()
    {
        return parentName;
    }

    public void setParentName(String parentName)
    {
        this.parentName = parentName;
    }

    public String getParentType()
    {
        return parentType;
    }

    public void setParentType(String parentType)
    {
        this.parentType = parentType;
    }

    public Long getParentId()
    {
        return parentId;
    }

    public void setParentId(Long parentId)
    {
        this.parentId = parentId;
    }

    public String getTargetName()
    {
        return targetName;
    }

    public void setTargetName(String targetName)
    {
        this.targetName = targetName;
    }

    public String getTargetType()
    {
        return targetType;
    }

    public void setTargetType(String targetType)
    {
        this.targetType = targetType;
    }

    public Long getTargetId()
    {
        return targetId;
    }

    public void setTargetId(Long targetId)
    {
        this.targetId = targetId;
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
