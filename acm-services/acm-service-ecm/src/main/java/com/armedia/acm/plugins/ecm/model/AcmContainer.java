package com.armedia.acm.plugins.ecm.model;

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.data.AcmEntity;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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

@Entity
@Table(name = "acm_container")
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class AcmContainer implements AcmEntity, Serializable, AcmObject
{

    private static final String OBJECT_TYPE = "CONTAINER";
    private static final long serialVersionUID = 2571845031587707081L;

    @Id
    @TableGenerator(name = "acm_container_gen",
            table = "acm_container_id",
            pkColumnName = "cm_seq_name",
            valueColumnName = "cm_seq_num",
            pkColumnValue = "acm_container",
            initialValue = 100,
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "acm_container_gen")
    @Column(name = "cm_container_id")
    private Long id;

    @Column(name = "cm_container_created", nullable = false, insertable = true, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "cm_container_creator", insertable = true, updatable = false)
    private String creator;

    @Column(name = "cm_container_modified", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    @Column(name = "cm_container_modifier")
    private String modifier;

    @Column(name = "cm_object_type", insertable = true, updatable = false, nullable = false)
    private String containerObjectType;

    @Column(name = "cm_object_id", insertable = true, updatable = false, nullable = false)
    private Long containerObjectId;

    @Column(name = "cm_object_title")
    private String containerObjectTitle;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cm_folder_id")
    private AcmFolder folder;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cm_attachment_folder_id")
    private AcmFolder attachmentFolder;

    @Column(name = "cm_outlook_folder_id", nullable = true)
    private String calendarFolderId;

    @Column(name = "cm_cmis_repository_id", nullable = false)
    private String cmisRepositoryId;

    @PrePersist
    protected void beforeInsert()
    {
        setDefaultCmisRepositoryId();
    }

    @PreUpdate
    protected void beforeUpdate()
    {
        setDefaultCmisRepositoryId();
    }

    protected void setDefaultCmisRepositoryId()
    {
        if (getCmisRepositoryId() == null)
        {
            setCmisRepositoryId(EcmFileConstants.DEFAULT_CMIS_REPOSITORY_ID);
        }
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

    @JsonIgnore
    @Override
    public String getObjectType()
    {
        return OBJECT_TYPE;
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

    public String getContainerObjectType()
    {
        return containerObjectType;
    }

    public void setContainerObjectType(String containerObjectType)
    {
        this.containerObjectType = containerObjectType;
    }

    public Long getContainerObjectId()
    {
        return containerObjectId;
    }

    public void setContainerObjectId(Long containerObjectId)
    {
        this.containerObjectId = containerObjectId;
    }

    public AcmFolder getFolder()
    {
        return folder;
    }

    public void setFolder(AcmFolder folder)
    {
        this.folder = folder;
    }

    public AcmFolder getAttachmentFolder()
    {
        return attachmentFolder;
    }

    public void setAttachmentFolder(AcmFolder attachmentFolder)
    {
        this.attachmentFolder = attachmentFolder;
    }

    public String getContainerObjectTitle()
    {
        return containerObjectTitle;
    }

    public void setContainerObjectTitle(String containerObjectTitle)
    {
        this.containerObjectTitle = containerObjectTitle;
    }

    public String getCalendarFolderId()
    {
        return calendarFolderId;
    }

    public void setCalendarFolderId(String calendarFolderId)
    {
        this.calendarFolderId = calendarFolderId;
    }

    public String getCmisRepositoryId()
    {
        return cmisRepositoryId;
    }

    public void setCmisRepositoryId(String cmisRepositoryId)
    {
        this.cmisRepositoryId = cmisRepositoryId;
    }

    @Override
    public String toString()
    {
        return "AcmContainer{" +
                "id=" + id +
                ", created=" + created +
                ", creator='" + creator + '\'' +
                ", modified=" + modified +
                ", modifier='" + modifier + '\'' +
                ", containerObjectType='" + containerObjectType + '\'' +
                ", containerObjectId=" + containerObjectId +
                ", containerObjectTitle='" + containerObjectTitle + '\'' +
                ", folder=" + folder +
                ", attachmentFolder=" + attachmentFolder +
                ", calendarFolderId='" + calendarFolderId + '\'' +
                ", cmisRepositoryId='" + cmisRepositoryId + '\'' +
                '}';
    }
}
