package com.armedia.acm.plugins.objectassociation.model;

import com.armedia.acm.data.AcmEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "acm_object_association")
public class ObjectAssociation implements AcmEntity, Serializable
{

    private static final long serialVersionUID = -7218267489007339202L;

    @Id
    @Column(name = "cm_association_id")
    @TableGenerator(name = "acm_object_association_gen",
            table = "acm_object_association_id",
            pkColumnName = "cm_seq_name",
            valueColumnName = "cm_seq_num",
            pkColumnValue = "acm_object_association",
            initialValue = 100,
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "acm_object_association_gen")
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

    @Column(name = "cm_target_subtype")
    private String targetSubtype;

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

    @Column(name = "cm_target_category")
    private String category;

    @Column(name = "cm_association_type")
    private String associationType = "OWNERSHIP";

    @PrePersist
    protected void beforeInsert()
    {
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

    public String getTargetSubtype()
    {
        return targetSubtype;
    }

    public void setTargetSubtype(String targetSubtype)
    {
        this.targetSubtype = targetSubtype;
    }

    public String getCategory()
    {
        return category;
    }

    public void setCategory(String category)
    {
        this.category = category;
    }

    public String getAssociationType()
    {
        return associationType;
    }

    public void setAssociationType(String associationType)
    {
        this.associationType = associationType;
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

    @Override
    public boolean equals(Object obj) {
        Objects.requireNonNull(obj, "Comparable object must not be null");
        if (!(obj instanceof ObjectAssociation))
            return false;
        ObjectAssociation other = (ObjectAssociation) obj;
        if (this.getAssociationId() == null || other.getAssociationId() == null)
            return false;
        return getAssociationId().equals(other.getAssociationId());
    }

    @Override
    public int hashCode() {
        if (getAssociationId() == null)
            return super.hashCode();
        else
            return getAssociationId().hashCode();
    }
}
