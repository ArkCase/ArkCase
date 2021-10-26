package com.armedia.acm.plugins.objectassociation.model;

/*-
 * #%L
 * ACM Default Plugin: Object Associations
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.armedia.acm.data.AcmEntity;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "acm_object_association")
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "className", defaultImpl = ObjectAssociation.class)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "cm_class_name", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("com.armedia.acm.plugins.objectassociation.model.ObjectAssociation")
@JsonIdentityInfo(generator = JSOGGenerator.class)

@JsonIgnoreProperties(ignoreUnknown = true)
public class ObjectAssociation implements AcmEntity, Serializable
{

    private static final long serialVersionUID = -7218267489007339202L;

    @Id
    @Column(name = "cm_association_id")
    @TableGenerator(name = "acm_object_association_gen", table = "acm_object_association_id", pkColumnName = "cm_seq_name", valueColumnName = "cm_seq_num", pkColumnValue = "acm_object_association", initialValue = 100, allocationSize = 1)
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

    @Column(name = "cm_target_title")
    private String targetTitle;

    @Column(name = "cm_description")
    private String description;

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "cm_inverse_association_id")
    private ObjectAssociation inverseAssociation;

    @Column(name = "cm_class_name")
    private String className = this.getClass().getName();

    @PrePersist
    protected void beforeInsert()
    {
        if (getStatus() == null || getStatus().trim().isEmpty())
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

    public String getTargetTitle()
    {
        return targetTitle;
    }

    public void setTargetTitle(String targetTitle)
    {
        this.targetTitle = targetTitle;
    }

    public ObjectAssociation getInverseAssociation()
    {
        return inverseAssociation;
    }

    public void setInverseAssociation(ObjectAssociation inverseAssociation)
    {
        this.inverseAssociation = inverseAssociation;
    }

    public String getClassName()
    {
        return className;
    }

    public void setClassName(String className)
    {
        this.className = className;
    }

    @Override
    public boolean equals(Object obj)
    {
        Objects.requireNonNull(obj, "Comparable object must not be null");
        if (!(obj instanceof ObjectAssociation))
            return false;
        ObjectAssociation other = (ObjectAssociation) obj;
        if (this.getAssociationId() == null || other.getAssociationId() == null)
            return false;
        return getAssociationId().equals(other.getAssociationId());
    }

    @Override
    public int hashCode()
    {
        if (getAssociationId() == null)
            return super.hashCode();
        else
            return getAssociationId().hashCode();
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }
}
