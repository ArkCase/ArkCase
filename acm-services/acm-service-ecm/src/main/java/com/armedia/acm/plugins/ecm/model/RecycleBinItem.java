package com.armedia.acm.plugins.ecm.model;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
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

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.data.AcmEntity;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

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
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * @author darko.dimitrievski
 */

@Entity
@Table(name = "acm_recycle_bin_item")
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "className", defaultImpl = RecycleBinItem.class)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "cm_rb_class_name", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("com.armedia.acm.plugins.ecm.model.RecycleBinItem")
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class RecycleBinItem implements AcmEntity, AcmObject
{
    private static final long serialVersionUID = 2571845031587707081L;

    @Id
    @TableGenerator(name = "recycle_bin_item_gen", table = "acm_recycle_bin_item_id", pkColumnName = "cm_seq_name", valueColumnName = "cm_seq_num", pkColumnValue = "acm_recycle_bin_item", initialValue = 100, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "recycle_bin_item_gen")
    @Column(name = "cm_rb_item_id")
    Long id;

    @Column(name = "cm_source_object_id")
    private Long sourceObjectId;

    @Column(name = "cm_source_object_type")
    private String sourceObjectType;

    @Column(name = "cm_source_folder_id")
    private Long sourceFolderId;

    @Column(name = "cm_source_cmis_repository_id")
    private String sourceCmisRepositoryId;

    @Column(name = "cm_rb_item_container_id")
    private Long recycleBinItemContainerId;

    @Column(name = "cm_rb_item_creator")
    private String creator;

    @Column(name = "cm_rb_item_modifier")
    private String modifier;

    @Column(name = "cm_rb_item_created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "cm_rb_item_modified")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    @Column(name = "cm_rb_class_name")
    private String className = this.getClass().getName();

    public RecycleBinItem()
    {

    }

    public RecycleBinItem(Long sourceObjectId, String sourceObjectType, Long sourceFolderId, String sourceCmisRepositoryId,
            Long recycleBinItemContainerId)
    {
        this.sourceObjectId = sourceObjectId;
        this.sourceObjectType = sourceObjectType;
        this.sourceFolderId = sourceFolderId;
        this.sourceCmisRepositoryId = sourceCmisRepositoryId;
        this.recycleBinItemContainerId = recycleBinItemContainerId;
    }

    public Long getSourceObjectId()
    {
        return sourceObjectId;
    }

    public void setSourceObjectId(Long sourceObjectId)
    {
        this.sourceObjectId = sourceObjectId;
    }

    public String getSourceObjectType()
    {
        return sourceObjectType;
    }

    public void setSourceObjectType(String sourceObjectType)
    {
        this.sourceObjectType = sourceObjectType;
    }

    public Long getSourceFolderId()
    {
        return sourceFolderId;
    }

    public void setSourceFolderId(Long sourceFolderId)
    {
        this.sourceFolderId = sourceFolderId;
    }

    public String getSourceCmisRepositoryId()
    {
        return sourceCmisRepositoryId;
    }

    public void setSourceCmisRepositoryId(String sourceCmisRepositoryId)
    {
        this.sourceCmisRepositoryId = sourceCmisRepositoryId;
    }

    public Long getRecycleBinItemContainerId()
    {
        return recycleBinItemContainerId;
    }

    public void setRecycleBinItemContainerId(Long recycleBinItemContainerId)
    {
        this.recycleBinItemContainerId = recycleBinItemContainerId;
    }

    @Override
    public String getObjectType() {
        return RecycleBinConstants.OBJECT_TYPE;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
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
    public String getModifier()
    {
        return modifier;
    }

    @Override
    public void setModifier(String modifier)
    {
        this.modifier = modifier;
    }

    @Override public Date getCreated()
    {
        return created;
    }

    @Override public void setCreated(Date created)
    {
        this.created = created;
    }

    @Override public Date getModified()
    {
        return modified;
    }

    @Override public void setModified(Date modified)
    {
        this.modified = modified;
    }

    public String getClassName()
    {
        return className;
    }

    public void setClassName(String className)
    {
        this.className = className;
    }
}

