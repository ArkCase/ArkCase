package com.armedia.acm.plugins.ecm.model;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
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
import com.armedia.acm.core.AcmParentObjectInfo;
import com.armedia.acm.core.AcmStatefulEntity;
import com.armedia.acm.data.AcmEntity;
import com.armedia.acm.data.AcmLegacySystemEntity;
import com.armedia.acm.data.converter.BooleanToStringConverter;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociation;
import com.armedia.acm.service.objectlock.model.AcmObjectLock;
import com.armedia.acm.services.participants.model.AcmAssignedObject;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.tag.model.AcmAssociatedTag;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.common.base.Objects;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "acm_file")
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "className", defaultImpl = EcmFile.class)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "cm_class_name", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("com.armedia.acm.plugins.ecm.model.EcmFile")
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class EcmFile implements AcmEntity, Serializable, AcmObject, AcmStatefulEntity, AcmLegacySystemEntity,
        AcmParentObjectInfo, AcmAssignedObject
{
    private static final long serialVersionUID = -5177153023458655846L;
    private static final String OBJECT_TYPE = "FILE";

    @Id
    @TableGenerator(name = "acm_file_gen", table = "acm_file_id", pkColumnName = "cm_seq_name", valueColumnName = "cm_seq_num", pkColumnValue = "acm_file", initialValue = 100, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "acm_file_gen")
    @Column(name = "cm_file_id")
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

    @Column(name = "cm_file_cmis_repository_id", nullable = false)
    private String cmisRepositoryId;

    @Column(name = "cm_version_series_id")
    private String versionSeriesId;

    @Column(name = "cm_file_name")
    private String fileName;

    @Column(name = "cm_file_active_version_mime_type")
    private String fileActiveVersionMimeType;

    @Column(name = "cm_file_active_version_name_extension")
    private String fileActiveVersionNameExtension;

    @ManyToOne
    @JoinColumn(name = "cm_folder_id")
    private AcmFolder folder;

    @ManyToOne
    @JoinColumn(name = "cm_container_id")
    private AcmContainer container;

    @Column(name = "cm_file_type")
    private String fileType;

    @Column(name = "cm_file_lang")
    private String fileLang;

    @Column(name = "cm_file_active_version_tag")
    private String activeVersionTag;

    @Column(name = "cm_file_category")
    private String category = "Document";

    @Column(name = "cm_page_count")
    private Integer pageCount = 0;

    @Column(name = "cm_file_source")
    private String fileSource;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "file")
    @OrderBy("created ASC")
    private List<EcmFileVersion> versions = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "cm_parent_object_id")
    private List<AcmAssociatedTag> tags = new ArrayList<>();

    @Column(name = "cm_security_field")
    private String securityField;

    @Column(name = "cm_object_type", insertable = true, updatable = false)
    private String objectType = OBJECT_TYPE;

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumns({ @JoinColumn(name = "cm_file_id", referencedColumnName = "cm_object_id", updatable = false, insertable = false),
            @JoinColumn(name = "cm_object_type", referencedColumnName = "cm_object_type", updatable = false, insertable = false) })
    private AcmObjectLock lock;

    @Column(name = "cm_legacy_system_id")
    private String legacySystemId;

    @Lob
    @Column(name = "cm_file_description")
    private String description;

    @Column(name = "cm_class_name")
    private String className = this.getClass().getName();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumns({ @JoinColumn(name = "cm_object_id", referencedColumnName = "cm_file_id"),
            @JoinColumn(name = "cm_object_type", referencedColumnName = "cm_object_type") })
    private List<AcmParticipant> participants = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "cm_file_organization_association")
    private ObjectAssociation organizationAssociation;

    @ManyToOne
    @JoinColumn(name = "cm_file_person_association")
    private ObjectAssociation personAssociation;

    @Column(name = "cm_file_restricted_flag", nullable = false)
    @Convert(converter = BooleanToStringConverter.class)
    private Boolean restricted = Boolean.FALSE;

    @Column(name = "cm_file_is_link", nullable = false)
    @Convert(converter = BooleanToStringConverter.class)
    private Boolean link = Boolean.FALSE;
    /**
     * This property is used to be able to make distinction between two or more EcmFile objects before these objects
     * to be persisted in database (before to take unique identifier from database)
     *
     * By default is null and don't need to be set with every creation of EcmFile instance. Developers can use for their
     * needs
     */

    @Column(name = "cm_file_is_duplicate", nullable = false)
    @Convert(converter = BooleanToStringConverter.class)
    private Boolean duplicate = Boolean.FALSE;

    @Transient
    private String uuid;

    @Transient
    private String mailAddress;

    @PrePersist
    protected void beforeInsert()
    {
        if (getStatus() == null || getStatus().trim().isEmpty())
        {
            setStatus("ACTIVE");
        }

        fixChildPointers();
        setDefaultCmisRepositoryId();
    }

    @PreUpdate
    protected void beforeUpdate()
    {
        fixChildPointers();
        setDefaultCmisRepositoryId();
    }

    private void fixChildPointers()
    {
        for (EcmFileVersion version : getVersions())
        {
            version.setFile(this);
        }

        for (AcmParticipant ap : getParticipants())
        {
            ap.setObjectId(getId());
            ap.setObjectType(getObjectType());
        }

        if (personAssociation != null)
        {
            personAssociation.setParentId(getId());
            personAssociation.setParentName(getFileName());
        }

        if (organizationAssociation != null)
        {
            organizationAssociation.setParentId(getId());
            organizationAssociation.setParentName(getFileName());
        }
    }

    protected void setDefaultCmisRepositoryId()
    {
        if (getCmisRepositoryId() == null)
        {
            setCmisRepositoryId(EcmFileConstants.DEFAULT_CMIS_REPOSITORY_ID);
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

    @Override
    public List<AcmParticipant> getParticipants()
    {
        return participants;
    }

    @Override
    public void setParticipants(List<AcmParticipant> participants)
    {
        this.participants = participants;
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

    public String getFileActiveVersionMimeType()
    {
        return fileActiveVersionMimeType;
    }

    public void setFileActiveVersionMimeType(String fileActiveVersionMimeType)
    {
        this.fileActiveVersionMimeType = fileActiveVersionMimeType;
    }

    public String getFileActiveVersionNameExtension()
    {
        return fileActiveVersionNameExtension;
    }

    public void setFileActiveVersionNameExtension(String fileActiveVersionNameExtension)
    {
        this.fileActiveVersionNameExtension = fileActiveVersionNameExtension;
    }

    public String getFileType()
    {
        return fileType;
    }

    public void setFileType(String fileType)
    {
        this.fileType = fileType;
    }

    public String getFileLang()
    {
        return fileLang;
    }

    public void setFileLang(String fileLang)
    {
        this.fileLang = fileLang;
    }

    public String getCmisRepositoryId()
    {
        return cmisRepositoryId;
    }

    public void setCmisRepositoryId(String cmisRepositoryId)
    {
        this.cmisRepositoryId = cmisRepositoryId;
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

    public List<AcmAssociatedTag> getTags()
    {
        return tags;
    }

    public void setTags(List<AcmAssociatedTag> tags)
    {
        this.tags = tags;
    }

    public Integer getPageCount()
    {
        return pageCount;
    }

    public void setPageCount(Integer pageCount)
    {
        this.pageCount = pageCount;
    }

    public String getFileSource()
    {
        return fileSource;
    }

    public void setFileSource(String fileSource)
    {
        this.fileSource = fileSource;
    }

    public String getSecurityField()
    {
        return securityField;
    }

    public void setSecurityField(String securityField)
    {
        this.securityField = securityField;
    }

    @JsonIgnore
    @Override
    public String getObjectType()
    {
        return objectType;
    }

    @JsonIgnore
    @Override
    public Long getId()
    {
        return fileId;
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this);
    }

    public AcmObjectLock getLock()
    {
        return lock;
    }

    public void setLock(AcmObjectLock lock)
    {
        this.lock = lock;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    @Override
    public String getLegacySystemId()
    {
        return legacySystemId;
    }

    @Override
    public void setLegacySystemId(String legacySystemId)
    {
        this.legacySystemId = legacySystemId;
    }

    public String getClassName()
    {
        return className;
    }

    public void setClassName(String className)
    {
        this.className = className;
    }

    public ObjectAssociation getOrganizationAssociation()
    {
        return organizationAssociation;
    }

    public void setOrganizationAssociation(ObjectAssociation organizationAssociation)
    {
        this.organizationAssociation = organizationAssociation;
    }

    public ObjectAssociation getPersonAssociation()
    {
        return personAssociation;
    }

    public void setPersonAssociation(ObjectAssociation personAssociation)
    {
        this.personAssociation = personAssociation;
    }

    @Override
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public Long getParentObjectId()
    {
        return getContainer() != null ? getContainer().getContainerObjectId() : null;
    }

    @Override
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public String getParentObjectType()
    {
        return getContainer() != null ? getContainer().getContainerObjectType() : null;
    }

    /**
     * Retrieve file extension (without the dot character).
     *
     * @return file extension
     */
    @JsonIgnore
    public String getFileExtension()
    {
        String fileExtension = FilenameUtils.getExtension(getFileActiveVersionNameExtension());
        if (fileExtension == null || fileExtension.isEmpty())
        {
            fileExtension = FilenameUtils.getExtension(getFileName());
        }
        return fileExtension;
    }

    @Override
    public Boolean getRestricted()
    {
        return restricted;
    }

    public void setRestricted(Boolean restricted)
    {
        this.restricted = restricted;
    }

    public Boolean isLink()
    {
        return link;
    }

    public void setLink(Boolean link)
    {
        this.link = link;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        EcmFile ecmFile = (EcmFile) o;
        return Objects.equal(fileId, ecmFile.fileId);
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(fileId);
    }

    public String getUuid()
    {
        return uuid;
    }

    public void setUuid(String uuid)
    {
        this.uuid = uuid;
    }

    public String getMailAddress()
    {
        return mailAddress;
    }

    public void setMailAddress(String mailAddress)
    {
        this.mailAddress = mailAddress;
    }

    public Boolean isDuplicate() {
        return duplicate;
    }

    public void setDuplicate(Boolean duplicate) {
        this.duplicate = duplicate;
    }
}
