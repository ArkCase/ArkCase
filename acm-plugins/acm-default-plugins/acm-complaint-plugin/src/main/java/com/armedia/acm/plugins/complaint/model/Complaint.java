package com.armedia.acm.plugins.complaint.model;

import com.armedia.acm.data.AcmEntity;
import com.armedia.acm.data.converter.BooleanToStringConverter;
import com.armedia.acm.plugins.addressable.model.PostalAddress;
import com.armedia.acm.plugins.casefile.model.Disposition;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmContainerEntity;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociation;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import com.armedia.acm.services.participants.model.AcmAssignedObject;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.fasterxml.jackson.annotation.JsonIgnore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by armdev on 4/4/14.
 */
@Entity
@Table(name = "acm_complaint")
public class Complaint implements Serializable, AcmAssignedObject, AcmEntity, AcmContainerEntity
{
    private static final long serialVersionUID = -1154137631399833851L;
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @Id
    @TableGenerator(name = "complaint_gen",
            table = "acm_complaint_id",
            pkColumnName = "cm_seq_name",
            valueColumnName = "cm_seq_num",
            pkColumnValue = "acm_complaint",
            initialValue = 100,
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "complaint_gen")
    @Column(name = "cm_complaint_id")
    private Long complaintId;

    @Column(name = "cm_complaint_number", insertable = true, updatable = false)
    private String complaintNumber;

    @Column(name = "cm_complaint_type")
    private String complaintType;

    @Column(name = "cm_complaint_priority")
    private String priority;

    @Column(name = "cm_complaint_title")
    private String complaintTitle;

    @Lob
    @Column(name = "cm_complaint_details")
    private String details;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "cm_complaint_incident_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date incidentDate;

    @Column(name = "cm_complaint_created", nullable = false, insertable = true, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "cm_complaint_creator", insertable = true, updatable = false)
    private String creator;

    @Column(name = "cm_complaint_modified", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    @Column(name = "cm_complaint_modifier")
    private String modifier;

    @Column(name = "cm_complaint_status")
    private String status;

    // the same person could originate many complaints, but each complaint is originated by
    // only one person, so a ManyToOne mapping makes sense here.
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cm_originator_id")
    private PersonAssociation originator;

    /**
     * This field is only used when the complaint is created. Usually it will be null.  Use the container
     * to get the CMIS object ID of the complaint folder.
     */
    @Transient
    private String ecmFolderPath;

    /**
     * Container folder where the complaint's attachments/content files are stored.
     */
    @OneToOne
    @JoinColumn(name = "cm_container_id")
    private AcmContainer container = new AcmContainer();

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumns({
            @JoinColumn(name = "cm_parent_id", referencedColumnName = "cm_complaint_id"),
            @JoinColumn(name = "cm_parent_type", referencedColumnName = "cm_object_type")
    })
    private Collection<ObjectAssociation> childObjects = new ArrayList<>();

    /**
     * These approvers are added by the web application and they become the assignees of the Activiti business process.
     * They are not persisted to the database.
     */
    @Transient
    private List<String> approvers;
    
    @OneToMany (cascade = CascadeType.ALL)
    @JoinColumns({
            @JoinColumn(name = "cm_person_assoc_parent_id", referencedColumnName = "cm_complaint_id"),
            @JoinColumn(name = "cm_person_assoc_parent_type", referencedColumnName = "cm_object_type")
    })
    @OrderBy("created ASC")
    private List<PersonAssociation> personAssociations = new ArrayList<>();

    @Column(name = "cm_object_type", insertable = true, updatable = false)
    private String objectType = ComplaintConstants.OBJECT_TYPE;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval=true)
    @JoinColumns({
            @JoinColumn(name = "cm_object_id"),
            @JoinColumn(name = "cm_object_type", referencedColumnName = "cm_object_type")
    })
    private List<AcmParticipant> participants = new ArrayList<>();

    @Column(name = "cm_due_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dueDate;

    @Column(name = "cm_tag")
    private String tag;
    
    @Column(name = "cm_frequency")
    private String frequency;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cm_address_id")
    private PostalAddress location;

    /**
     * Complaint disposition is set only when the close complaint request is approved.
     * Until then, the requested disposition (if any) is linked from the acm_close_complaint_request table
     * (CloseComplaintRequest POJO).
     */
    @OneToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "cm_disposition_id", insertable = false, updatable = true)
    private Disposition disposition;

    @Column(name = "cm_complaint_restricted_flag", nullable = false)
    @Convert(converter = BooleanToStringConverter.class)
    private Boolean restricted = false;
        
    @PrePersist
    protected void beforeInsert()
    {
        if ( getStatus() == null || getStatus().trim().isEmpty() )
        {
            setStatus("DRAFT");
        }

        if ( getOriginator() != null )
        {
            personAssociationResolver(getOriginator());
        }

        setupChildPointers();
    }

    private void setupChildPointers()
    {
        for ( ObjectAssociation childObject : childObjects )
        {
            childObject.setParentId(complaintId);
        }
        for ( PersonAssociation persAssoc : personAssociations)
        {
            personAssociationResolver(persAssoc);
        }
        for ( AcmParticipant ap : getParticipants() )
        {
            ap.setObjectId(getComplaintId());
            ap.setObjectType(getObjectType());
        }

        if ( getContainer() != null )
        {
            getContainer().setContainerObjectId(getComplaintId());
            getContainer().setContainerObjectType(getObjectType());

            log.debug("Setting container object title to: " + getComplaintNumber());
            getContainer().setContainerObjectTitle(getComplaintNumber());
        }
    }

    @PreUpdate
    protected void beforeUpdate()
    {
        setupChildPointers();
    }

    public Long getComplaintId()
    {
        return complaintId;
    }

    public void setComplaintId(Long complaintId)
    {
        this.complaintId = complaintId;
    }

    public String getComplaintNumber()
    {
        return complaintNumber;
    }

    public void setComplaintNumber(String complaintNumber)
    {
        this.complaintNumber = complaintNumber;

        if ( getContainer() != null )
        {
            getContainer().setContainerObjectTitle(complaintNumber);
        }
    }

    public String getComplaintType()
    {
        return complaintType;
    }

    public void setComplaintType(String complaintType)
    {
        this.complaintType = complaintType;
    }

    public String getPriority()
    {
        return priority;
    }

    public void setPriority(String priority)
    {
        this.priority = priority;
    }

    public String getComplaintTitle()
    {
        return complaintTitle;
    }

    public void setComplaintTitle(String complaintTitle)
    {
        this.complaintTitle = complaintTitle;
    }

    public String getDetails()
    {
        return details;
    }

    public void setDetails(String details)
    {
        this.details = details;
    }

    public Date getIncidentDate()
    {
        return incidentDate;
    }

    public void setIncidentDate(Date incidentDate)
    {
        this.incidentDate = incidentDate;
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
    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public PersonAssociation getOriginator()
    {
        return originator;
    }

    public void setOriginator(PersonAssociation originator)
    {
        this.originator = originator;
    }

    public String getEcmFolderPath()
    {
        return ecmFolderPath;
    }

    public void setEcmFolderPath(String ecmFolderPath)
    {
        this.ecmFolderPath = ecmFolderPath;
    }

    public Collection<ObjectAssociation> getChildObjects()
    {
        return Collections.unmodifiableCollection(childObjects);
    }

    public void addChildObject(ObjectAssociation childObject)
    {
        childObjects.add(childObject);
        childObject.setParentName(getComplaintNumber());
        childObject.setParentType(ComplaintConstants.OBJECT_TYPE);
        childObject.setParentId(getComplaintId());
    }

    public List<String> getApprovers()
    {
        return approvers;
    }

    public void setApprovers(List<String> approvers)
    {
        this.approvers = approvers;
    }

    @Override
    @JsonIgnore
    public String getObjectType()
    {
        return objectType;
    }

    @Override
    @JsonIgnore
    public Long getId()
    {
        return complaintId;
    }

    public List<PersonAssociation> getPersonAssociations() 
    {
        return personAssociations;
    }

    public void setPersonAssociations(List<PersonAssociation> personAssociations) {
        this.personAssociations = personAssociations;
    }   
    
    private void personAssociationResolver (PersonAssociation personAssoc)
    {
        personAssoc.setParentId(getComplaintId());
        personAssoc.setParentType(ComplaintConstants.OBJECT_TYPE);

        if (personAssoc.getPerson().getPersonAssociations() == null)
        {
        	personAssoc.getPerson().setPersonAssociations(new ArrayList<PersonAssociation>());
        }
        
        personAssoc.getPerson().getPersonAssociations().addAll(Arrays.asList(personAssoc));
    }
       
    public Date getDueDate()
    {
        return dueDate;
    }
    
    public void setDueDate(Date dueDate) 
    {
        this.dueDate = dueDate;
    }
    
    public String getTag()
    {
        return tag;
}

    public void setTag(String tag)
    {
        this.tag = tag;
    }

    public String getFrequency() 
    {
        return frequency;
    }

    public void setFrequency(String frequency)
    {
        this.frequency = frequency;
    }

    public PostalAddress getLocation() {
		return location;
	}

	public void setLocation(PostalAddress location) {
		this.location = location;
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

    public Disposition getDisposition()
    {
        return disposition;
    }

    public void setDisposition(Disposition disposition)
    {
        this.disposition = disposition;
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
    public AcmContainer getContainer()
    {
        return container;
    }

    @Override
    public void setContainer(AcmContainer container)
    {
        if ( container != null )
        {
            container.setContainerObjectType(getObjectType());
        }

        this.container = container;
    }

    @Override
    public String toString()
    {
        return "Complaint{" +
                "complaintId=" + complaintId +
                ", complaintNumber='" + complaintNumber + '\'' +
                ", complaintType='" + complaintType + '\'' +
                ", priority='" + priority + '\'' +
                ", complaintTitle='" + complaintTitle + '\'' +
                ", details='" + details + '\'' +
                ", incidentDate=" + incidentDate +
                ", created=" + created +
                ", creator='" + creator + '\'' +
                ", modified=" + modified +
                ", modifier='" + modifier + '\'' +
                ", status='" + status + '\'' +
                ", originator=" + originator +
                ", ecmFolderPath='" + ecmFolderPath + '\'' +
                ", container=" + container +
                ", childObjects=" + childObjects +
                ", approvers=" + approvers +
                ", personAssociations=" + personAssociations +
                ", participants=" + participants +
                ", dueDate=" + dueDate +
                ", tag='" + tag + '\'' +
                ", frequency='" + frequency + '\'' +
                ", location=" + location +
                ", disposition=" + disposition +
                ", restricted=" + restricted +
                '}';
    }
}
