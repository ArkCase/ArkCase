package com.armedia.acm.services.transcribe.model;

/*-
 * #%L
 * ACM Service: Transcribe
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
import com.armedia.acm.data.converter.BigDecimalConverter;
import com.armedia.acm.data.converter.BooleanToStringConverter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
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
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 02/27/2018
 */
@Entity
@Table(name = "acm_transcribe_item")
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "className", defaultImpl = TranscribeItem.class)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "cm_transcribe_item_class_name", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("com.armedia.acm.services.transcribe.model.TranscribeItem")
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class TranscribeItem implements AcmObject, AcmEntity, Serializable, Comparable<TranscribeItem>
{
    @Id
    @TableGenerator(name = "transcribe_item_gen", table = "acm_transcribe_item_id", pkColumnName = "cm_seq_name", valueColumnName = "cm_seq_num", pkColumnValue = "acm_transcribe_item", initialValue = 100, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "transcribe_item_gen")
    @Column(name = "cm_transcribe_item_id")
    private Long id;

    @ManyToOne(cascade = { CascadeType.REFRESH, CascadeType.DETACH, CascadeType.PERSIST }, optional = false)
    @JoinColumn(name = "cm_transcribe_id", nullable = false)
    private Transcribe transcribe;

    @Column(name = "cm_transcribe_item_start_time")
    @Convert(converter = BigDecimalConverter.class)
    private BigDecimal startTime;

    @Column(name = "cm_transcribe_item_end_time")
    @Convert(converter = BigDecimalConverter.class)
    private BigDecimal endTime;

    @Column(name = "cm_transcribe_item_confidence")
    private int confidence;

    @Column(name = "cm_transcribe_item_corrected")
    @Convert(converter = BooleanToStringConverter.class)
    private Boolean corrected = Boolean.FALSE;

    @Lob
    @Column(name = "cm_transcribe_item_text")
    private String text;

    @Column(name = "cm_transcribe_item_creator")
    private String creator;

    @Column(name = "cm_transcribe_item_created")
    private Date created;

    @Column(name = "cm_transcribe_item_modifier")
    private String modifier;

    @Column(name = "cm_transcribe_item_modified")
    private Date modified;

    @Column(name = "cm_transcribe_item_class_name")
    private String className = this.getClass().getName();

    @Override
    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Transcribe getTranscribe()
    {
        return transcribe;
    }

    public void setTranscribe(Transcribe transcribe)
    {
        this.transcribe = transcribe;
    }

    public BigDecimal getStartTime()
    {
        return startTime;
    }

    public void setStartTime(BigDecimal startTime)
    {
        if (startTime == null)
        {
            startTime = new BigDecimal("0");
        }
        this.startTime = startTime;
    }

    public BigDecimal getEndTime()
    {
        return endTime;
    }

    public void setEndTime(BigDecimal endTime)
    {
        if (endTime == null)
        {
            endTime = new BigDecimal("0");
        }
        this.endTime = endTime;
    }

    public int getConfidence()
    {
        return confidence;
    }

    public void setConfidence(int confidence)
    {
        this.confidence = confidence;
    }

    public Boolean getCorrected()
    {
        return corrected;
    }

    public void setCorrected(Boolean corrected)
    {
        this.corrected = corrected;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
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
    public Date getModified()
    {
        return modified;
    }

    @Override
    public void setModified(Date modified)
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

    @Override
    public String getObjectType()
    {
        return TranscribeConstants.OBJECT_TYPE_ITEM;
    }

    @Override
    public int compareTo(TranscribeItem item)
    {
        // First show nulls items in the list
        if (item == null)
        {
            return 1;
        }

        // If startTime of the caller is null, add it first in the list
        // This step is not necessary because in the setter, I explicitly setting 0 if it's null
        // But let's have as guard
        if (startTime == null)
        {
            return -1;
        }

        // Proceed with regular compareTo
        return startTime.compareTo(item.startTime);
    }
}
