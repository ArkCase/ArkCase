package com.armedia.acm.plugins.complaint.model;

import com.armedia.acm.plugins.person.model.Person;
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
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;


/**
 * Created by armdev on 4/4/14.
 */
@Entity
@Table(name = "acm_complaint")
public class Complaint implements Serializable
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
    private Person originator;

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




    public Complaint()
    {
    }

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
    }

    @PreUpdate
    protected void beforeUpdate()
    {
        if ( log.isDebugEnabled() )
        {
            log.debug("In beforeUpdate()");
        }
        setModified(new Date());
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
        this.modifier = modifier;

        if ( getOriginator() != null )
        {
            getOriginator().setModifier(modifier);
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

    public Person getOriginator()
    {
        if ( originator == null )
        {
            originator = new Person();
        }

        return originator;
    }

    public void setOriginator(Person originator)
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
}
