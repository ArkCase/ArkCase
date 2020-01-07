package com.armedia.acm.services.ocr.model;

/*-
 * #%L
 * acm-ocr
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

import com.armedia.acm.core.AcmStatefulEntity;
import com.armedia.acm.data.AcmEntity;
import com.armedia.acm.services.mediaengine.model.MediaEngine;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

import javax.persistence.AssociationOverride;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
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
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import java.io.Serializable;

/**
 * Created by Vladimir Cherepnalkovski
 */
@Entity
@Table(name = "acm_ocr")
@AttributeOverrides({
        @AttributeOverride(name = "remoteId", column = @Column(name = "cm_ocr_remote_id")),
        @AttributeOverride(name = "type", column = @Column(name = "cm_ocr_type")),
        @AttributeOverride(name = "language", column = @Column(name = "cm_ocr_language")),
        @AttributeOverride(name = "mediaEcmFileVersion", column = @Column(name = "cm_ocr_file_version_id")),
        @AttributeOverride(name = "status", column = @Column(name = "cm_ocr_status")),
        @AttributeOverride(name = "processId", column = @Column(name = "cm_ocr_process_id")),
        @AttributeOverride(name = "creator", column = @Column(name = "cm_ocr_creator")),
        @AttributeOverride(name = "created", column = @Column(name = "cm_ocr_created")),
        @AttributeOverride(name = "modifier", column = @Column(name = "cm_ocr_modifier")),
        @AttributeOverride(name = "modified", column = @Column(name = "cm_ocr_modified")),
        @AttributeOverride(name = "class_name", column = @Column(name = "cm_ocr_class_name"))
})
@AssociationOverride(name = "mediaEcmFileVersion", joinColumns = @JoinColumn(name = "cm_ocr_file_version_id"))
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "className", defaultImpl = OCR.class)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "cm_ocr_class_name", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("com.armedia.acm.services.ocr.model.OCR")
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class OCR extends MediaEngine implements AcmEntity, AcmStatefulEntity, Serializable
{
    @Id
    @TableGenerator(name = "ocr_gen", table = "acm_ocr_id", pkColumnName = "cm_seq_name", valueColumnName = "cm_seq_num", pkColumnValue = "acm_ocr", initialValue = 100, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "ocr_gen")
    @Column(name = "cm_ocr_id")
    private Long id;
    @Column(name = "cm_ocr_class_name")
    private String className = this.getClass().getName();

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

    @Override
    public String getObjectType()
    {
        return OCRConstants.OBJECT_TYPE;
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
}
