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

import com.armedia.acm.core.AcmStatefulEntity;
import com.armedia.acm.data.AcmEntity;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.services.mediaengine.exception.CreateMediaEngineException;
import com.armedia.acm.services.mediaengine.exception.SaveMediaEngineException;
import com.armedia.acm.services.mediaengine.model.MediaEngine;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.AssociationOverride;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 02/27/2018
 */
@Entity
@Table(name = "acm_transcribe")
@AttributeOverrides({
        @AttributeOverride(name = "remoteId", column = @Column(name = "cm_transcribe_remote_id")),
        @AttributeOverride(name = "type", column = @Column(name = "cm_transcribe_type")),
        @AttributeOverride(name = "language", column = @Column(name = "cm_transcribe_language")),
        @AttributeOverride(name = "mediaEcmFileVersion", column = @Column(name = "cm_media_file_version_id")),
        @AttributeOverride(name = "status", column = @Column(name = "cm_transcribe_status")),
        @AttributeOverride(name = "processId", column = @Column(name = "cm_transcribe_process_id")),
        @AttributeOverride(name = "creator", column = @Column(name = "cm_transcribe_creator")),
        @AttributeOverride(name = "created", column = @Column(name = "cm_transcribe_created")),
        @AttributeOverride(name = "modifier", column = @Column(name = "cm_transcribe_modifier")),
        @AttributeOverride(name = "modified", column = @Column(name = "cm_transcribe_modified")),
        @AttributeOverride(name = "class_name", column = @Column(name = "cm_transcribe_class_name"))
})
@AssociationOverride(name = "mediaEcmFileVersion", joinColumns = @JoinColumn(name = "cm_media_file_version_id"))
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "className", defaultImpl = Transcribe.class)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "cm_transcribe_class_name", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("com.armedia.acm.services.transcribe.model.Transcribe")
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class Transcribe extends MediaEngine implements AcmEntity, AcmStatefulEntity, Serializable
{
    @Id
    @TableGenerator(name = "transcribe_gen", table = "acm_transcribe_id", pkColumnName = "cm_seq_name", valueColumnName = "cm_seq_num", pkColumnValue = "acm_transcribe", initialValue = 100, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "transcribe_gen")
    @Column(name = "cm_transcribe_id")
    private Long id;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "transcribe", orphanRemoval = true)
    private List<TranscribeItem> transcribeItems;

    @OneToOne
    @JoinColumn(name = "cm_transcribe_file_id")
    private EcmFile transcribeEcmFile;

    @Column(name = "cm_transcribe_word_count")
    private long wordCount;

    @Column(name = "cm_transcribe_confidence")
    private int confidence;

    @Column(name = "cm_transcribe_class_name")
    private String className = this.getClass().getName();

    @PrePersist
    protected void beforeInsert() throws CreateMediaEngineException
    {
        setUpTranscribeItems();
    }

    @PreUpdate
    protected void beforeUpdate() throws SaveMediaEngineException
    {
        setUpTranscribeItems();
    }

    private void setUpTranscribeItems()
    {
        wordCount = 0;
        confidence = 0;
        if (getTranscribeItems() != null && !getTranscribeItems().isEmpty())
        {
            getTranscribeItems().forEach(item -> {
                item.setTranscribe(this);
                if (StringUtils.isNotEmpty(item.getText()))
                {
                    String[] textAsArray = item.getText().split(" ");
                    wordCount += textAsArray.length;
                }

                confidence += item.getConfidence();
            });

            confidence = confidence / getTranscribeItems().size();
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

    public List<TranscribeItem> getTranscribeItems()
    {
        if (transcribeItems != null)
        {
            Collections.sort(transcribeItems);
        }
        return transcribeItems;
    }

    public void setTranscribeItems(List<TranscribeItem> transcribeItems)
    {
        if (transcribeItems != null)
        {
            Collections.sort(transcribeItems);
        }
        this.transcribeItems = transcribeItems;
    }

    public EcmFile getTranscribeEcmFile()
    {
        return transcribeEcmFile;
    }

    public void setTranscribeEcmFile(EcmFile transcribeEcmFile)
    {
        this.transcribeEcmFile = transcribeEcmFile;
    }

    public long getWordCount()
    {
        return wordCount;
    }

    public void setWordCount(long wordCount)
    {
        this.wordCount = wordCount;
    }

    public int getConfidence()
    {
        return confidence;
    }

    public void setConfidence(int confidence)
    {
        this.confidence = confidence;
    }

    @Override
    public String getObjectType()
    {
        return TranscribeConstants.OBJECT_TYPE;
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
