package com.armedia.acm.plugins.complaint.model;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Entity
@Table(name = "acm_complaint")
public class ComplaintListView
{
    /**
     * ComplaintListView is for lists; it can never be saved, but JPA insists on a writeable ID mapping.
     * JPA spec does not allow for a read-only entity.
     */
    @Id
    @Column(name = "cm_complaint_id", insertable = true, updatable = false)
    private Long complaintId;

    @Column(name = "cm_complaint_number", insertable = false, updatable = false)
    private String complaintNumber;

    @Column(name = "cm_complaint_type", insertable = false, updatable = false)
    private String complaintType;

    @Column(name = "cm_complaint_priority", insertable = false, updatable = false)
    private String priority;

    @Column(name = "cm_complaint_title", insertable = false, updatable = false)
    private String complaintTitle;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "cm_complaint_incident_date", insertable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date incidentDate;

    @Column(name = "cm_complaint_created", insertable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "cm_complaint_creator", insertable = false, updatable = false)
    private String creator;

    @Column(name = "cm_complaint_modified", insertable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    @Column(name = "cm_complaint_modifier", insertable = false, updatable = false)
    private String modifier;

    @Column(name = "cm_complaint_status", insertable = false, updatable = false)
    private String status;

    @Column(name = "cm_complaint_ecm_folder_id", insertable = false, updatable = false)
    private String ecmFolderId;

    @PrePersist
    public void saveNotAllowed()
    {
        throw new IllegalStateException("ComplaintListView objects cannot be persisted.");
    }

    public Long getComplaintId()
    {
        return complaintId;
    }

    public String getComplaintNumber()
    {
        return complaintNumber;
    }

    public String getComplaintType()
    {
        return complaintType;
    }

    public String getPriority()
    {
        return priority;
    }

    public String getComplaintTitle()
    {
        return complaintTitle;
    }

    public Date getIncidentDate()
    {
        return incidentDate;
    }

    public Date getCreated()
    {
        return created;
    }

    public String getCreator()
    {
        return creator;
    }

    public Date getModified()
    {
        return modified;
    }

    public String getModifier()
    {
        return modifier;
    }

    public String getStatus()
    {
        return status;
    }

    public String getEcmFolderId()
    {
        return ecmFolderId;
    }

    protected void setComplaintId(Long complaintId)
    {
        this.complaintId = complaintId;
    }

    protected void setComplaintNumber(String complaintNumber)
    {
        this.complaintNumber = complaintNumber;
    }

    protected void setComplaintType(String complaintType)
    {
        this.complaintType = complaintType;
    }

    protected void setPriority(String priority)
    {
        this.priority = priority;
    }

    protected void setComplaintTitle(String complaintTitle)
    {
        this.complaintTitle = complaintTitle;
    }

    protected void setIncidentDate(Date incidentDate)
    {
        this.incidentDate = incidentDate;
    }

    protected void setCreated(Date created)
    {
        this.created = created;
    }

    protected void setCreator(String creator)
    {
        this.creator = creator;
    }

    protected void setModified(Date modified)
    {
        this.modified = modified;
    }

    protected void setModifier(String modifier)
    {
        this.modifier = modifier;
    }

    protected void setStatus(String status)
    {
        this.status = status;
    }

    protected void setEcmFolderId(String ecmFolderId)
    {
        this.ecmFolderId = ecmFolderId;
    }
}
