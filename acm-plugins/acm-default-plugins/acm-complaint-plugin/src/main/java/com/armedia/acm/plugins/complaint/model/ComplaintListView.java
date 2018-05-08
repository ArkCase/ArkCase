package com.armedia.acm.plugins.complaint.model;

/*-
 * #%L
 * ACM Default Plugin: Complaints
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

    @PrePersist
    public void saveNotAllowed()
    {
        throw new IllegalStateException("ComplaintListView objects cannot be persisted.");
    }

    public Long getComplaintId()
    {
        return complaintId;
    }

    protected void setComplaintId(Long complaintId)
    {
        this.complaintId = complaintId;
    }

    public String getComplaintNumber()
    {
        return complaintNumber;
    }

    protected void setComplaintNumber(String complaintNumber)
    {
        this.complaintNumber = complaintNumber;
    }

    public String getComplaintType()
    {
        return complaintType;
    }

    protected void setComplaintType(String complaintType)
    {
        this.complaintType = complaintType;
    }

    public String getPriority()
    {
        return priority;
    }

    protected void setPriority(String priority)
    {
        this.priority = priority;
    }

    public String getComplaintTitle()
    {
        return complaintTitle;
    }

    protected void setComplaintTitle(String complaintTitle)
    {
        this.complaintTitle = complaintTitle;
    }

    public Date getIncidentDate()
    {
        return incidentDate;
    }

    protected void setIncidentDate(Date incidentDate)
    {
        this.incidentDate = incidentDate;
    }

    public Date getCreated()
    {
        return created;
    }

    protected void setCreated(Date created)
    {
        this.created = created;
    }

    public String getCreator()
    {
        return creator;
    }

    protected void setCreator(String creator)
    {
        this.creator = creator;
    }

    public Date getModified()
    {
        return modified;
    }

    protected void setModified(Date modified)
    {
        this.modified = modified;
    }

    public String getModifier()
    {
        return modifier;
    }

    protected void setModifier(String modifier)
    {
        this.modifier = modifier;
    }

    public String getStatus()
    {
        return status;
    }

    protected void setStatus(String status)
    {
        this.status = status;
    }

}
