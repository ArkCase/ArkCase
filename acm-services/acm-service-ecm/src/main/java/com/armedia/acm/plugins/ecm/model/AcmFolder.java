package com.armedia.acm.plugins.ecm.model;

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.data.AcmEntity;
import com.armedia.acm.services.participants.model.AcmAssignedObject;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.fasterxml.jackson.annotation.JsonIgnore;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.eclipse.persistence.annotations.CloneCopyPolicy;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
@Table(name = "acm_folder")
// This custom clone policy is needed because by default EclipseLink ignores transient properties;
// but we need the @Transient parentFolderParticipants in order to set the folder access control. The custom
// clone policy preserves this transient policy. Unfortunately standard JPA doesn't support this feature
// so we have to use an EclipseLink annotation. "copy" refers to the copy method in this class.
@CloneCopyPolicy(method = "copy", workingCopyMethod = "copy")
public class AcmFolder implements AcmEntity, Serializable, AcmObject, AcmAssignedObject
{

    private static final long serialVersionUID = -1087924246860797061L;

    @Id
    @TableGenerator(name = "acm_folder_gen", table = "acm_folder_id", pkColumnName = "cm_seq_name", valueColumnName = "cm_seq_num", pkColumnValue = "acm_folder", initialValue = 100, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "acm_folder_gen")
    @Column(name = "cm_folder_id")
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

    @ManyToOne
    @JoinColumn(name = "cm_parent_folder_id")
    private AcmFolder parentFolder;

    @JsonIgnore
    @OneToMany(mappedBy = "parentFolder")
    private List<AcmFolder> childrenFolders;

    @Column(name = "cm_folder_status")
    private String status = "ACTIVE";

    @Column(name = "cm_object_type", insertable = true, updatable = false)
    private String objectType = AcmFolderConstants.OBJECT_FOLDER_TYPE;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumns({ @JoinColumn(name = "cm_object_id"), @JoinColumn(name = "cm_object_type", referencedColumnName = "cm_object_type") })
    private List<AcmParticipant> participants = new ArrayList<>();

    // Knowing the parent folder's participants makes the folder assignment rules easier. But, if they are mapped
    // as oneToMany instead of Transient, EclipseLink will try to persist them, even if the OneToMany annotation
    // is marked as updateable=false, insertable=false. Persisting the parent's participants causes so much trouble
    // I made this @Transient
    @Transient
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
        for (AcmParticipant ap : getParticipants())
        {
            ap.setObjectId(getId());
            ap.setObjectType(getObjectType());
        }
    }

    // Since the @CloneCopyPolicy on this class says "copy", EclipseLink will call this method when it needs to
    // clone an AcmFolder; EclipseLink uses clones extensively, for instance to compute changes between current
    // database state and an object being saved. This method must return a new AcmFolder, not 'this'.
    protected AcmFolder copy()
    {
        AcmFolder retval = new AcmFolder();
        retval.setCmisFolderId(getCmisFolderId());
        retval.setName(getName());
        retval.setParentFolderParticipants(getParentFolderParticipants());
        retval.setId(getId());
        retval.setParentFolder(getParentFolder());
        retval.setCreated(getCreated());
        retval.setCreator(getCreator());
        retval.setModified(getModified());
        retval.setModifier(getModifier());
        retval.setParticipants(getParticipants());
        retval.setStatus(getStatus());

        return retval;
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

    public AcmFolder getParentFolder()
    {
        return parentFolder;
    }

    public void setParentFolder(AcmFolder parentFolder)
    {
        this.parentFolder = parentFolder;
    }

    public List<AcmFolder> getChildrenFolders()
    {
        return childrenFolders;
    }

    public void setChildrenFolders(List<AcmFolder> childrenFolders)
    {
        this.childrenFolders = childrenFolders;
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
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this);
    }
}
