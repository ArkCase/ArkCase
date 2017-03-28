package com.armedia.acm.plugins.documentrepository.model;

import com.armedia.acm.core.AcmNotifiableEntity;
import com.armedia.acm.core.AcmNotificationReceiver;
import com.armedia.acm.core.AcmStatefulEntity;
import com.armedia.acm.data.AcmEntity;
import com.armedia.acm.data.converter.BooleanToStringConverter;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmContainerEntity;
import com.armedia.acm.services.participants.model.AcmAssignedObject;
import com.armedia.acm.services.participants.model.AcmParticipant;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "acm_document_repository")
public class DocumentRepository implements Serializable, AcmAssignedObject, AcmEntity,
        AcmContainerEntity, AcmNotifiableEntity, AcmStatefulEntity
{
    @Id
    @TableGenerator(name = "document_repository_gen", table = "acm_document_repository_id",
            pkColumnName = "cm_seq_name", valueColumnName = "cm_seq_num",
            pkColumnValue = "acm_document_repository", initialValue = 100, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "document_repository_gen")
    @Column(name = "cm_doc_repo_id")
    private Long id;

    @Column(name = "cm_doc_repo_name", unique = true, nullable = false)
    private String name;

    @Column(name = "cm_doc_repo_name_uc", unique = true, nullable = false)
    private String nameUpperCase;

    @Lob
    @Column(name = "cm_doc_repo_details")
    private String details;

    @Column(name = "cm_doc_repo_status", nullable = false)
    private String status;

    @Column(name = "cm_doc_repo_restricted_flag", nullable = false)
    @Convert(converter = BooleanToStringConverter.class)
    private Boolean restricted = Boolean.FALSE;

    @Column(name = "cm_doc_repo_created", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "cm_doc_repo_creator", nullable = false, updatable = false)
    private String creator;

    @Column(name = "cm_doc_repo_modified", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    @Column(name = "cm_doc_repo_modifier", nullable = false)
    private String modifier;

    @Column(name = "cm_object_type", updatable = false)
    private String objectType = DocumentRepositoryConstants.OBJECT_TYPE;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumns({@JoinColumn(name = "cm_object_id"),
            @JoinColumn(name = "cm_object_type", referencedColumnName = "cm_object_type")})
    private List<AcmParticipant> participants = new ArrayList<>();

    /**
     * This field is only used when the document repository is created. Usually it will be null.
     * Use the container to get the CMIS object ID of the document repository folder.
     */
    @Transient
    private String ecmFolderPath;

    /**
     * Container folder where attachments/content files are stored.
     */
    @OneToOne
    @JoinColumn(name = "cm_container_id")
    private AcmContainer container = new AcmContainer();

    @PrePersist
    protected void beforeInsert()
    {
        if (StringUtils.isBlank(getStatus()))
        {
            setStatus("DRAFT");
        }
        setNameUpperCase(getName().toUpperCase());
        setupChildPointers();
    }

    private void setupChildPointers()
    {
        for (AcmParticipant ap : getParticipants())
        {
            ap.setObjectId(getId());
            ap.setObjectType(getObjectType());
        }

        if (getContainer() != null)
        {
            getContainer().setContainerObjectId(getId());
            getContainer().setContainerObjectType(getObjectType());
            getContainer().setContainerObjectTitle(getName());
        }
    }

    @PreUpdate
    protected void beforeUpdate()
    {
        setupChildPointers();
        setNameUpperCase(getName().toUpperCase());
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

    public String getNameUpperCase()
    {
        return nameUpperCase;
    }

    public void setNameUpperCase(String nameUpperCase)
    {
        this.nameUpperCase = nameUpperCase;
    }

    public String getDetails()
    {
        return details;
    }

    public void setDetails(String details)
    {
        this.details = details;
    }

    public Boolean getRestricted()
    {
        return restricted;
    }

    public void setRestricted(Boolean restricted)
    {
        this.restricted = restricted;
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

    @Override
    public String getObjectType()
    {
        return objectType;
    }

    public void setObjectType(String objectType)
    {
        this.objectType = objectType;
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

    public String getEcmFolderPath()
    {
        return ecmFolderPath;
    }

    public void setEcmFolderPath(String ecmFolderPath)
    {
        this.ecmFolderPath = ecmFolderPath;
    }

    @Override
    public AcmContainer getContainer()
    {
        return container;
    }

    @Override
    public void setContainer(AcmContainer container)
    {
        this.container = container;
    }

    @Override
    public Set<AcmNotificationReceiver> getReceivers()
    {
        return new HashSet<>(participants);
    }

    @Override
    public String getNotifiableEntityTitle()
    {
        return name;
    }
}
