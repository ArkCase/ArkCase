package com.armedia.acm.services.note.model;

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.core.AcmParentObjectInfo;
import com.armedia.acm.data.AcmEntity;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "acm_note")
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "className", defaultImpl = Note.class)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "cm_class_name", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("com.armedia.acm.services.note.model.Note")
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class Note implements Serializable, AcmObject, AcmEntity, AcmParentObjectInfo
{
    private static final long serialVersionUID = -1154137631399833851L;

    @Id
    @TableGenerator(name = "acm_note_gen",
            table = "acm_note_id",
            pkColumnName = "cm_seq_name",
            valueColumnName = "cm_seq_num",
            pkColumnValue = "acm_note",
            initialValue = 100,
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "acm_note_gen")
    @Column(name = "cm_note_id")
    private Long id;

    @Lob
    @Column(name = "cm_note_text")
    private String note;

    @Column(name = "cm_note_type")
    private String type;

    @Column(name = "cm_note_creator", nullable = false, insertable = true, updatable = false)
    private String creator;

    @Column(name = "cm_note_created", nullable = false, insertable = true, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "cm_parent_object_id")
    private Long parentId;

    @Column(name = "cm_parent_object_type")
    private String parentType;

    @Column(name = "cm_parent_object_title")
    private String parentTitle;

    @Column(name = "cm_note_tag")
    private String tag;

    @Column(name = "cm_note_modifier", nullable = false)
    private String modifier;

    @Column(name = "cm_note_modified", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    @Column(name = "cm_note_author")
    private String author;

    @Column(name = "cm_class_name")
    private String className = this.getClass().getName();

    @PrePersist
    public void beforeInsert()
    {
        Date today = new Date();
        setCreated(today);

        if (null == getType())
        {
            setType(NoteConstants.NOTE_GENERAL);
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

    public String getNote()
    {
        return note;
    }

    public void setNote(String note)
    {
        this.note = note;
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

    public String getTag()
    {
        return tag;
    }

    public void setTag(String tag)
    {
        this.tag = tag;
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
    @JsonIgnore
    public String getObjectType()
    {
        return NoteConstants.OBJECT_TYPE;
    }

    @Override
    @JsonIgnore
    public Long getParentObjectId()
    {
        return parentId;
    }

    @Override
    @JsonIgnore
    public String getParentObjectType()
    {
        return parentType;
    }

    public String getParentTitle() {return parentTitle; }

    public void setParentTitle(String parentTitle)
    {
        this.parentTitle = parentTitle;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
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
