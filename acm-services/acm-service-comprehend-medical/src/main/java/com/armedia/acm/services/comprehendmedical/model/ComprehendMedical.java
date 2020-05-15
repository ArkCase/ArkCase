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

import com.armedia.acm.core.AcmStatefulEntity;
import com.armedia.acm.data.AcmEntity;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.services.mediaengine.exception.CreateMediaEngineException;
import com.armedia.acm.services.mediaengine.exception.SaveMediaEngineException;
import com.armedia.acm.services.mediaengine.model.MediaEngine;
import com.armedia.acm.tool.comprehendmedical.model.ComprehendMedicalConstants;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 05/12/2020
 */

@Entity
@Table(name = "acm_comprehend_medical")
@AttributeOverrides({
        @AttributeOverride(name = "remoteId", column = @Column(name = "cm_comprehend_medical_remote_id")),
        @AttributeOverride(name = "type", column = @Column(name = "cm_comprehend_medical_type")),
        @AttributeOverride(name = "language", column = @Column(name = "cm_comprehend_medical_language")),
        @AttributeOverride(name = "mediaEcmFileVersion", column = @Column(name = "cm_media_file_version_id")),
        @AttributeOverride(name = "status", column = @Column(name = "cm_comprehend_medical_status")),
        @AttributeOverride(name = "processId", column = @Column(name = "cm_comprehend_medical_process_id")),
        @AttributeOverride(name = "creator", column = @Column(name = "cm_comprehend_medical_creator")),
        @AttributeOverride(name = "created", column = @Column(name = "cm_comprehend_medical_created")),
        @AttributeOverride(name = "modifier", column = @Column(name = "cm_comprehend_medical_modifier")),
        @AttributeOverride(name = "modified", column = @Column(name = "cm_comprehend_medical_modified")),
        @AttributeOverride(name = "class_name", column = @Column(name = "cm_comprehend_medical_class_name"))
})
@AssociationOverride(name = "mediaEcmFileVersion", joinColumns = @JoinColumn(name = "cm_media_file_version_id"))
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "className", defaultImpl = ComprehendMedical.class)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "cm_comprehend_medical_class_name", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("com.armedia.acm.services.comprehendmedical.model.ComprehendMedical")
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class ComprehendMedical extends MediaEngine implements AcmEntity, AcmStatefulEntity, Serializable
{
    @Id
    @TableGenerator(name = "comprehend_medical_gen", table = "acm_comprehend_medical_id", pkColumnName = "cm_seq_name", valueColumnName = "cm_seq_num", pkColumnValue = "acm_comprehend_medical", initialValue = 100, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "comprehend_medical_gen")
    @Column(name = "cm_comprehend_medical_id")
    private Long id;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "comprehendMedical", orphanRemoval = true)
    private List<ComprehendMedicalEntity> entities;

    @OneToOne
    @JoinColumn(name = "cm_comprehend_medical_file_id")
    private EcmFile comprehendMedicalEcmFile;

    @Column(name = "cm_comprehend_medical_class_name")
    private String className = this.getClass().getName();

    @Column(name = "cm_comprehend_medical_job_id")
    private String jobId;

    @PrePersist
    protected void beforeInsert() throws CreateMediaEngineException
    {
        setUpEntities();
    }

    @PreUpdate
    protected void beforeUpdate() throws SaveMediaEngineException
    {
        setUpEntities();
    }

    private void setUpEntities()
    {
        if (getEntities() != null && !getEntities().isEmpty())
        {
            getEntities().forEach(item -> item.setComprehendMedical(this));
        }
    }

    @Override
    public Long getId()
    {
        return id;
    }

    @Override
    public void setId(Long id)
    {
        this.id = id;
    }

    public List<ComprehendMedicalEntity> getEntities()
    {
        return entities;
    }

    public void setEntities(List<ComprehendMedicalEntity> entities)
    {
        this.entities = entities;
    }

    public EcmFile getComprehendMedicalEcmFile()
    {
        return comprehendMedicalEcmFile;
    }

    public void setComprehendMedicalEcmFile(EcmFile comprehendMedicalEcmFile)
    {
        this.comprehendMedicalEcmFile = comprehendMedicalEcmFile;
    }

    @Override
    public String getObjectType()
    {
        return ComprehendMedicalConstants.OBJECT_TYPE;
    }

    @Override
    public String getClassName()
    {
        return className;
    }

    @Override
    public void setClassName(String className)
    {
        this.className = className;
    }

    public String getJobId()
    {
        return jobId;
    }

    public void setJobId(String jobId)
    {
        this.jobId = jobId;
    }
}
