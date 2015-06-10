package com.armedia.acm.plugins.ecm.model;

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.data.AcmEntity;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociation;
import com.armedia.acm.services.participants.model.AcmAssignedObject;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
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
@Table(name = "acm_folder")
public class AcmFolder implements AcmEntity, Serializable, AcmObject, AcmAssignedObject
{

    private static final String OBJECT_TYPE = "FOLDER";
    private static final long serialVersionUID = -1087924246860797061L;

    @Id
    @Column(name = "cm_folder_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cm_folder_created", nullable = false, insertable = true, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "cm_folder_creator", insertable = true, updatable = false)
    private String creator;

    @Column(name = "cm_folder_modified", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    @Column(name = "cm_folder_modifier")
    private String modifier;

    @Column(name = "cm_folder_name")
    private String name;

    @Column(name = "cm_cmis_folder_id")
    private String cmisFolderId;

    @Column(name = "cm_parent_folder_id")
    private Long parentFolderId;

    @Column(name = "cm_folder_status")
    private String status = "ACTIVE";

    @Column(name = "cm_object_type", insertable = true, updatable = false)
    private String objectType = AcmFolderConstants.OBJECT_FOLDER_TYPE;

    @OneToMany(cascade = {CascadeType.ALL})
    @JoinColumns({
            @JoinColumn(name = "cm_object_id"),
            @JoinColumn(name = "cm_object_type", referencedColumnName = "cm_object_type")
    })
    private List<AcmParticipant> participants = new ArrayList<>();

    // looking up the parent folder's participants makes the data access control so much easier
    @OneToMany(cascade = {})
    @JoinColumns({
            @JoinColumn(name = "cm_object_id", referencedColumnName = "cm_parent_folder_id", insertable = false, updatable = false),
            @JoinColumn(name = "cm_object_type", referencedColumnName = "cm_object_type", insertable = false, updatable = false)
    })
    private List<AcmParticipant> parentFolderParticipants = new ArrayList<>();

    @PrePersist
    protected void beforeInsert()
    {
        setupChildPointers();
    }

    @PreUpdate
    protected void beforeUpdate()
    {
        setupChildPointers();
    }

    protected void setupChildPointers()
    {
        for ( AcmParticipant ap : getParticipants() )
        {
            ap.setObjectId(getId());
            ap.setObjectType(getObjectType());
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
    public String getObjectType() {
        return objectType;
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

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getCmisFolderId()
    {
        return cmisFolderId;
    }

    public void setCmisFolderId(String cmisFolderId)
    {
        this.cmisFolderId = cmisFolderId;
    }

    public Long getParentFolderId()
    {
        return parentFolderId;
    }

    public void setParentFolderId(Long parentFolderId)
    {
        this.parentFolderId = parentFolderId;
    }

    @Override
    public List<AcmParticipant> getParticipants()
    {
        return participants;
    }

    public void setParticipants(List<AcmParticipant> participants)
    {
        this.participants = participants;
    }

    public List<AcmParticipant> getParentFolderParticipants()
    {
        return parentFolderParticipants;
    }

    public void setParentFolderParticipants(List<AcmParticipant> parentFolderParticipants)
    {
        this.parentFolderParticipants = parentFolderParticipants;
    }

    @Override
    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
