package com.armedia.acm.plugins.ecm.model;

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.data.AcmEntity;
import com.armedia.acm.services.tag.model.AcmAssociatedTag;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "acm_file")
public class EcmFile implements AcmEntity, Serializable, AcmObject
{
    private static final long serialVersionUID = -5177153023458655846L;
    private static final String OBJECT_TYPE = "FILE";

    @Id
    @Column(name = "cm_file_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fileId;

    @Column(name = "cm_file_status")
    private String status;

    @Column(name = "cm_file_created", nullable = false, insertable = true, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "cm_file_creator", insertable = true, updatable = false)
    private String creator;

    @Column(name = "cm_file_modified", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    @Column(name = "cm_file_modifier")
    private String modifier;

    @Column(name = "cm_version_series_id")
    private String versionSeriesId;

    @Column(name = "cm_file_name")
    private String fileName;

    @Column(name = "cm_file_mime_type")
    private String fileMimeType;


    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cm_folder_id")
    private AcmFolder folder;

    @ManyToOne
    @JoinColumn(name = "cm_container_id")
    private AcmContainer container;

    @Column(name = "cm_file_type")
    private String fileType;

    @Column(name = "cm_file_active_version_tag")
    private String activeVersionTag;

    @Column(name = "cm_file_category")
    private String category = "Document";

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy ="file")
    private List<EcmFileVersion> versions = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name="cm_parent_id")
    private List<AcmAssociatedTag> tags = new ArrayList<>();

    @PrePersist
    protected void beforeInsert()
    {
        if ( getStatus() == null || getStatus().trim().isEmpty() )
        {
            setStatus("ACTIVE");
        }

        fixChildPointers();
    }

    @PreUpdate
    protected void beforeUpdate()
    {
        fixChildPointers();
    }

    private void fixChildPointers()
    {
        for ( EcmFileVersion version : getVersions() )
        {
            version.setFile(this);
        }
    }

    public Long getFileId()
    {
        return fileId;
    }

    public void setFileId(Long fileId)
    {
        this.fileId = fileId;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
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

    public String getFileName()
    {
        return fileName;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    public String getFileMimeType()
    {
        return fileMimeType;
    }

    public void setFileMimeType(String fileMimeType)
    {
        this.fileMimeType = fileMimeType;
    }

    public String getFileType()
    {
        return fileType;
    }

    public void setFileType(String fileType)
    {
        this.fileType = fileType;
    }

    public String getVersionSeriesId()
    {
        return versionSeriesId;
    }

    public void setVersionSeriesId(String versionSeriesId)
    {
        this.versionSeriesId = versionSeriesId;
    }

    public AcmFolder getFolder()
    {
        return folder;
    }

    public void setFolder(AcmFolder folder)
    {
        this.folder = folder;
    }

    public String getActiveVersionTag()
    {
        return activeVersionTag;
    }

    public void setActiveVersionTag(String activeVersionTag)
    {
        this.activeVersionTag = activeVersionTag;
    }

    public String getCategory()
    {
        return category;
    }

    public void setCategory(String category)
    {
        this.category = category;
    }

    public List<EcmFileVersion> getVersions()
    {
        return versions;
    }

    public void setVersions(List<EcmFileVersion> versions)
    {
        this.versions = versions;
    }

    public AcmContainer getContainer()
    {
        return container;
    }

    public void setContainer(AcmContainer container)
    {
        this.container = container;
    }

    public List<AcmAssociatedTag> getTags() {
        return tags;
    }

    public void setTags(List<AcmAssociatedTag> tags) {
        this.tags = tags;
    }

    @JsonIgnore
    @Override
    public String getObjectType() {
        return OBJECT_TYPE;
    }

    @JsonIgnore
    @Override
    public Long getId() {
        return fileId;
    }
}
