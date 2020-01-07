package com.armedia.acm.services.tag.model;

/*-
 * #%L
 * ACM Service: Tag
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

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.data.AcmEntity;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by marjan.stefanoski on 24.03.2015.
 */
@Entity
@Table(name = "acm_associated_tag")
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class AcmAssociatedTag implements AcmEntity, Serializable, AcmObject
{

    public static final String OBJECT_TYPE = "ASSOCIATED_TAG";
    private transient final Logger log = LogManager.getLogger(getClass());

    @Id
    @TableGenerator(name = "acm_associated_tag_gen", table = "acm_associated_tag_id", pkColumnName = "cm_seq_name", valueColumnName = "cm_seq_num", pkColumnValue = "acm_associated_tag", initialValue = 100, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "acm_associated_tag_gen")
    @Column(name = "cm_associated_tag_id")
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "cm_tag_id")
    private AcmTag tag;

    @Column(name = "cm_associated_tag_creator", nullable = false, insertable = true, updatable = false)
    private String creator;

    @Column(name = "cm_associated_tag_created", nullable = false, insertable = true, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "cm_parent_object_id")
    private Long parentId;

    @Column(name = "cm_parent_object_type")
    private String parentType;

    @Column(name = "cm_parent_object_title")
    private String parentTitle;

    @Column(name = "cm_associated_tag_modified", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    @Column(name = "cm_associated_tag_modifier")
    private String modifier;

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
    public Date getCreated()
    {
        return created;
    }

    @Override
    public void setCreated(Date created)
    {
        this.created = created;
    }

    public Long getParentId()
    {
        return parentId;
    }

    public void setParentId(Long parentId)
    {
        this.parentId = parentId;
    }

    public String getParentType()
    {
        return parentType;
    }

    public void setParentType(String parentType)
    {
        this.parentType = parentType;
    }

    public AcmTag getTag()
    {
        return tag;
    }

    public void setTag(AcmTag tag)
    {
        this.tag = tag;
    }

    public Long getTagId()
    {
        return tag.getId();
    }

    @JsonIgnore
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

    @JsonIgnore
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

    @JsonIgnore
    @Override
    public String getObjectType()
    {
        return OBJECT_TYPE;
    }

    @JsonIgnore
    @Override
    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getParentTitle()
    {
        return parentTitle;
    }

    public void setParentTitle(String parentTitle)
    {
        this.parentTitle = parentTitle;
    }
}
