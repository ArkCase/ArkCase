package com.armedia.acm.plugins.complaint.model;

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociation;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import com.armedia.acm.services.users.model.AcmParticipant;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;


import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by armdev on 4/4/14.
 */
@Entity
@Table(name = "acm_complaint")
public class Complaint implements Serializable, AcmObject
{
    private static final long serialVersionUID = -1154137631399833851L;
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @Id
    @Column(name = "cm_complaint_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "cm_originator_id")
    private PersonAssociation originator;

    /**
     * This field is only used when the complaint is created. Usually it will be null.  Use the ecmFolderId
     * to get the CMIS object ID of the complaint folder.
     */
    @Transient
    private String ecmFolderPath;

    /**
     * CMIS object ID of the folder where the complaint's attachments/content files are stored.
     */
    @Column(name = "cm_complaint_ecm_folder_id")
    private String ecmFolderId;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "cm_parent_id")
    private Collection<ObjectAssociation> childObjects = new ArrayList<>();

    /**
     * These approvers are added by the web application and they become the assignees of the Activiti business process.
     * They are not persisted to the database.
     */
    @Transient
    private List<String> approvers;
    
    @OneToMany (cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "cm_person_assoc_parent_id")
    private List<PersonAssociation> personAssociations = new ArrayList<>();

    @OneToMany (cascade = {CascadeType.ALL})
    @JoinColumn(name = "cm_object_id")
    private List<AcmParticipant> participants = new ArrayList<>();

    @Column(name = "cm_due_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dueDate;

    @Column(name = "cm_tag")
    private String tag;
    
    @Column(name = "cm_frequency")
    private String frequency;
    
    @Column(name = "cm_location")
    private String location;
        
    @PrePersist
    protected void beforeInsert()
    {
        if ( log.isDebugEnabled() )
        {
            log.debug("In beforeInsert()");
        }
        setCreated(new Date());
        setModified(new Date());

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
            ap.setObjectType("COMPLAINT");
        }
    }

    @PreUpdate
    protected void beforeUpdate()
    {
        if ( log.isDebugEnabled() )
        {
            log.debug("In beforeUpdate()");
        }
        setModified(new Date());

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

    public Date getCreated()
    {
        return created;
    }

    /**
     * Sets the created date of this Complaint and of any nested objects.
     * @param created
     */
    public void setCreated(Date created)
    {
        this.created = created;

        if ( getOriginator() != null )
        {
            getOriginator().setCreated(created);
        }

        for ( ObjectAssociation oa : childObjects )
        {
            if ( oa.getCreated() == null )
            {
                oa.setCreated(created);
            }
        }
        for ( PersonAssociation pa : personAssociations )
        {
            if ( pa.getCreated() == null )
            {
                pa.setCreated(created);
            }
        }

        for ( AcmParticipant ap : getParticipants() )
        {
            ap.setCreated(created);
        }
    }

    public String getCreator()
    {
        return creator;
    }

    /**
     * Sets the creator of this Complaint and of any nested objects.
     * @param creator
     */
    public void setCreator(String creator)
    {
        this.creator = creator;

        if ( getOriginator() != null )
        {
            getOriginator().setCreator(creator);
        }

        for ( ObjectAssociation oa : childObjects )
        {
            if ( oa.getCreator() == null )
            {
                oa.setCreator(creator);
            }
        }
        
        for ( PersonAssociation pa : personAssociations )
        {
            if ( pa.getCreator() == null )
            {
                pa.setCreator(creator);
            }
        }

        for ( AcmParticipant ap : getParticipants() )
        {
            ap.setCreator(creator);
        }
    }

    public Date getModified()
    {
        return modified;
    }

    /**
     * Sets the modified date of this Complaint and of any nested objects.
     * @param modified
     */
    public void setModified(Date modified)
    {
        this.modified = modified;

        if ( getOriginator() != null )
        {
            getOriginator().setModified(modified);
        }

        for ( ObjectAssociation oa : childObjects )
        {
            if ( oa.getModified() == null )
            {
                oa.setModified(modified);
            }
        }
        for ( PersonAssociation pa : personAssociations)
        {
            if ( pa.getModified() == null )
            {
                pa.setModified(modified);
            }
        }

        for ( AcmParticipant ap : getParticipants() )
        {
            ap.setModified(modified);
        }

   }

    public String getModifier()
    {
        return modifier;
    }

    /**
     * Sets the modifier of this Complaint and of any nested objects.
     * @param modifier
     */
    public void setModifier(String modifier)
    {
        log.info("setting modifier to: '" + modifier + "'");
        this.modifier = modifier;

        if ( getOriginator() != null )
        {
            getOriginator().setModifier(modifier);
        }

        for ( ObjectAssociation oa : childObjects )
        {
            if ( oa.getModifier() == null )
            {
                oa.setModifier(modifier);
            }
        }
        
        for ( PersonAssociation pa : personAssociations )
        {
            if ( pa.getModifier() == null )
            {
                pa.setModifier(modifier);
            }
        }

        for ( AcmParticipant ap : getParticipants() )
        {
            ap.setModifier(modifier);
        }

    }

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

    public String getEcmFolderId()
    {
        return ecmFolderId;
    }

    public void setEcmFolderId(String ecmFolderId)
    {
        if ( log.isDebugEnabled() )
        {
            log.debug("Set folder ID to '" + ecmFolderId + "'");
        }
        this.ecmFolderId = ecmFolderId;
    }

    public Collection<ObjectAssociation> getChildObjects()
    {
        return Collections.unmodifiableCollection(childObjects);
    }

    public void addChildObject(ObjectAssociation childObject)
    {
        childObjects.add(childObject);
        childObject.setParentName(getComplaintNumber());
        childObject.setParentType("COMPLAINT");
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
        return "COMPLAINT";
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
        personAssoc.setParentType("COMPLAINT");
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

    public String getLocation() 
    {
        return location;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }

    public List<AcmParticipant> getParticipants()
    {
        return participants;
    }

    public void setParticipants(List<AcmParticipant> participants)
    {
        this.participants = participants;
    }
}
