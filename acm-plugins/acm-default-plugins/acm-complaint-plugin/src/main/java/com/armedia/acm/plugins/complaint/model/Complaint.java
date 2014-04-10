package com.armedia.acm.plugins.complaint.model;

import com.armedia.acm.plugins.person.model.Person;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;


/**
 * Created by armdev on 4/4/14.
 */
@Entity
@Table(name = "acm_complaint")
public class Complaint
{
    @Id
    @Column(name = "cm_complaint_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long complaintId;

    @Column(name = "cm_complaint_number")
    private String complaintNumber;

    @Column(name = "cm_complaint_type")
    private String complaintType;

    @Column(name = "cm_complaint_priority")
    private String priority;

    @Column(name = "cm_complaint_title")
    private String complaintTitle;

    @Column(name = "cm_complaint_details")
    private String details;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "cm_complaint_incident_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date incidentDate;

    @Column(name = "cm_complaint_created", nullable = false, insertable = true, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created = new Date();

    @Column(name = "cm_complaint_creator", insertable = true, updatable = false)
    private String creator;

    @Column(name = "cm_complaint_modified", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified = new Date();

    @Column(name = "cm_complaint_modifier")
    private String modifier;

    @Column(name = "cm_complaint_status")
    private String status = "DRAFT";

    // the same person could originate many complaints, but each complaint is originated by
    // only one person, so a ManyToOne mapping makes sense here.
    @ManyToOne
    @JoinColumn(name = "cm_originator_id")
    private Person originator;



    public Complaint()
    {
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

    public void setCreated(Date created)
    {
        this.created = created;
    }

    public String getCreator()
    {
        return creator;
    }

    public void setCreator(String creator)
    {
        this.creator = creator;
    }

    public Date getModified()
    {
        return modified;
    }

    public void setModified(Date modified)
    {
        this.modified = modified;
    }

    public String getModifier()
    {
        return modifier;
    }

    public void setModifier(String modifier)
    {
        this.modifier = modifier;
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
        return originator;
    }

    public void setOriginator(Person originator)
    {
        this.originator = originator;
    }

}
