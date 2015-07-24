package com.armedia.acm.plugins.ecm.model;

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.data.AcmEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "acm_file_version")
public class EcmFileVersion implements AcmEntity, Serializable, AcmObject
{
    private static final String OBJECT_TYPE = "FILE_VERSION";
    private static final long serialVersionUID = 1281659634956850724L;

    @Id
    @TableGenerator(name = "acm_file_version_gen",
            table = "acm_file_version_id",
            pkColumnName = "cm_seq_name",
            valueColumnName = "cm_seq_num",
            pkColumnValue = "acm_file_version",
            initialValue = 100,
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "acm_file_version_gen")
    @Column(name = "cm_file_version_id")
    private Long id;

    @Column(name = "cm_file_version_created", nullable = false, insertable = true, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "cm_file_version_creator", insertable = true, updatable = false)
    private String creator;

    @Column(name = "cm_file_version_modified", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    @Column(name = "cm_file_version_modifier")
    private String modifier;

    @Column(name = "cm_cmis_object_id")
    private String cmisObjectId;

    @Column(name = "cm_file_version_version_tag")
    private String versionTag;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "cm_file_id")
    private EcmFile file;

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

    @Override
    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getCmisObjectId()
    {
        return cmisObjectId;
    }

    public void setCmisObjectId(String cmisObjectId)
    {
        this.cmisObjectId = cmisObjectId;
    }

    public String getVersionTag()
    {
        return versionTag;
    }

    public void setVersionTag(String versionTag)
    {
        this.versionTag = versionTag;
    }

    public EcmFile getFile()
    {
        return file;
    }

    public void setFile(EcmFile file)
    {
        this.file = file;
    }

    @JsonIgnore
    @Override
    public String getObjectType() {
        return OBJECT_TYPE;
    }


}
