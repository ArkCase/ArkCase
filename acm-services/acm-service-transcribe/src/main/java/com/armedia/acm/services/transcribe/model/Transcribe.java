package com.armedia.acm.services.transcribe.model;

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.core.AcmStatefulEntity;
import com.armedia.acm.data.AcmEntity;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 02/27/2018
 */
@Entity
@Table(name = "acm_transcribe")
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "className", defaultImpl = Transcribe.class)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "cm_transcribe_class_name", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("com.armedia.acm.services.transcribe.model.Transcribe")
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class Transcribe implements AcmObject, AcmEntity, AcmStatefulEntity, Serializable
{
    @Id
    @TableGenerator(name = "transcribe_gen", table = "acm_transcribe_id", pkColumnName = "cm_seq_name", valueColumnName = "cm_seq_num", pkColumnValue = "acm_transcribe", initialValue = 100, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "transcribe_gen")
    @Column(name = "cm_transcribe_id")
    private Long id;

    @Column(name = "cm_transcribe_remote_id")
    private String remoteId;

    @Column(name = "cm_transcribe_type")
    private String type;

    @Column(name = "cm_transcribe_language")
    private String language;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "transcribe", orphanRemoval = true)
    private List<TranscribeItem> transcribeItems;

    @OneToOne
    @JoinColumn(name = "cm_transcribe_media_file_id")
    private EcmFile mediaEcmFile;

    @OneToOne
    @JoinColumn(name = "cm_transcribe_media_file_version_id")
    private EcmFileVersion mediaEcmFileVersion;

    @OneToOne
    @JoinColumn(name = "cm_transcribe_file_id")
    private EcmFile transcribeEcmFile;

    @Column(name = "cm_transcribe_status")
    private String status;

    @Column(name = "cm_transcribe_process_id")
    private String processId;

    @Column(name = "cm_transcribe_word_count")
    private long wordCount;

    @Column(name = "cm_transcribe_creator")
    private String creator;

    @Column(name = "cm_transcribe_created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "cm_transcribe_modifier")
    private String modifier;

    @Column(name = "cm_transcribe_modified")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    @Column(name = "cm_transcribe_class_name")
    private String className = this.getClass().getName();

    @PrePersist
    protected void beforeInsert()
    {
        setUpTranscribeItems();
    }

    @PreUpdate
    protected void beforeUpdate()
    {
        setUpTranscribeItems();
    }

    private void setUpTranscribeItems()
    {
        if (getTranscribeItems() != null)
        {
            getTranscribeItems().forEach(item -> item.setTranscribe(this));
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

    public String getRemoteId()
    {
        return remoteId;
    }

    public void setRemoteId(String remoteId)
    {
        this.remoteId = remoteId;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getLanguage()
    {
        return language;
    }

    public void setLanguage(String language)
    {
        this.language = language;
    }

    public List<TranscribeItem> getTranscribeItems()
    {
        return transcribeItems;
    }

    public void setTranscribeItems(List<TranscribeItem> transcribeItems)
    {
        this.transcribeItems = transcribeItems;
    }

    public EcmFile getMediaEcmFile()
    {
        return mediaEcmFile;
    }

    public void setMediaEcmFile(EcmFile mediaEcmFile)
    {
        this.mediaEcmFile = mediaEcmFile;
    }

    public EcmFileVersion getMediaEcmFileVersion()
    {
        return mediaEcmFileVersion;
    }

    public void setMediaEcmFileVersion(EcmFileVersion mediaEcmFileVersion)
    {
        this.mediaEcmFileVersion = mediaEcmFileVersion;
    }

    public EcmFile getTranscribeEcmFile()
    {
        return transcribeEcmFile;
    }

    public void setTranscribeEcmFile(EcmFile transcribeEcmFile)
    {
        this.transcribeEcmFile = transcribeEcmFile;
    }

    public String getProcessId()
    {
        return processId;
    }

    public void setProcessId(String processId)
    {
        this.processId = processId;
    }

    public long getWordCount()
    {
        return wordCount;
    }

    public void setWordCount(long wordCount)
    {
        this.wordCount = wordCount;
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

    @Override
    public String getStatus()
    {
        return status;
    }

    @Override
    public void setStatus(String status)
    {
        this.status = status;
    }

    @Override
    public String getObjectType()
    {
        return TranscribeConstants.OBJECT_TYPE;
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
