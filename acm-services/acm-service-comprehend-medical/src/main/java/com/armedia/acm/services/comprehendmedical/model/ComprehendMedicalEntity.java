package com.armedia.acm.services.comprehendmedical.model;

/*-
 * #%L
 * ACM Service: Comprehend Medical
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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
import com.armedia.acm.core.AcmStatefulEntity;
import com.armedia.acm.data.AcmEntity;
import com.armedia.acm.services.mediaengine.exception.CreateMediaEngineException;
import com.armedia.acm.services.mediaengine.exception.SaveMediaEngineException;
import com.armedia.acm.tool.comprehendmedical.model.ComprehendMedicalConstants;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 05/12/2020
 */

@Entity
@Table(name = "acm_comprehend_medical_entity")
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "className", defaultImpl = ComprehendMedicalEntity.class)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "cm_comprehend_medical_entity_class_name", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("com.armedia.acm.services.comprehendmedical.model.ComprehendMedicalEntity")
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class ComprehendMedicalEntity implements AcmObject, AcmEntity, Serializable
{
    @Id
    @TableGenerator(name = "comprehend_medical_entity_gen", table = "acm_comprehend_medical_entity_id", pkColumnName = "cm_seq_name", valueColumnName = "cm_seq_num", pkColumnValue = "acm_comprehend_medical_entity", initialValue = 100, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "comprehend_medical_entity_gen")
    @Column(name = "cm_comprehend_medical_entity_id")
    private Long id;

    @ManyToOne(cascade = { CascadeType.REFRESH, CascadeType.DETACH, CascadeType.PERSIST }, optional = false)
    @JoinColumn(name = "cm_comprehend_medical_id", nullable = false)
    private ComprehendMedical comprehendMedical;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "comprehendMedicalEntity", orphanRemoval = true)
    private List<ComprehendMedicalEntityAttribute> attributes;

    @Column(name = "cm_comprehend_medical_entity_text")
    private String text;

    @Column(name = "cm_comprehend_medical_entity_category")
    private String category;

    @Column(name = "cm_comprehend_medical_entity_type")
    private String type;

    @Column(name = "cm_comprehend_medical_entity_class_name")
    private String className = this.getClass().getName();

    @Column(name = "cm_comprehend_medical_entity_creator")
    private String creator;

    @Column(name = "cm_comprehend_medical_entity_created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "cm_comprehend_medical_entity_modifier")
    private String modifier;

    @Column(name = "cm_comprehend_medical_entity_modified")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    @PrePersist
    protected void beforeInsert() throws CreateMediaEngineException
    {
        setUpAttributes();
    }

    @PreUpdate
    protected void beforeUpdate() throws SaveMediaEngineException
    {
        setUpAttributes();
    }

    private void setUpAttributes()
    {
        if (getAttributes() != null && !getAttributes().isEmpty())
        {
            getAttributes().forEach(item -> item.setComprehendMedicalEntity(this));
        }
    }

    @Override
    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public ComprehendMedical getComprehendMedical()
    {
        return comprehendMedical;
    }

    public void setComprehendMedical(ComprehendMedical comprehendMedical)
    {
        this.comprehendMedical = comprehendMedical;
    }

    public List<ComprehendMedicalEntityAttribute> getAttributes()
    {
        return attributes;
    }

    public void setAttributes(List<ComprehendMedicalEntityAttribute> attributes)
    {
        this.attributes = attributes;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public String getCategory()
    {
        return category;
    }

    public void setCategory(String category)
    {
        this.category = category;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
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
    public String getObjectType()
    {
        return ComprehendMedicalConstants.ENTITY_OBJECT_TYPE;
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
