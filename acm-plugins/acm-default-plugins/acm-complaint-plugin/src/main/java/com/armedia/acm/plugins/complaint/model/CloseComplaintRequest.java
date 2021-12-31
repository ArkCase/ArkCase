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

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.core.AcmParentObjectInfo;
import com.armedia.acm.data.AcmEntity;
import com.armedia.acm.plugins.casefile.model.Disposition;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
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
import java.util.List;

@Entity
@Table(name = "acm_close_complaint_request")
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class CloseComplaintRequest implements Serializable, AcmObject, AcmEntity, AcmParentObjectInfo
{
    private static final long serialVersionUID = -6389711968453289552L;

    @Id
    @TableGenerator(name = "close_complaint_request_id_gen", table = "acm_close_complaint_request_id", pkColumnName = "cm_seq_name", valueColumnName = "cm_seq_num", pkColumnValue = "acm_close_complaint_request", initialValue = 100, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "close_complaint_request_id_gen")
    @Column(name = "cm_close_complaint_id")
    private Long id;

    @Column(name = "cm_complaint_id")
    private Long complaintId;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cm_disposition_id")
    private Disposition disposition;

    @Column(name = "cm_close_complaint_status")
    private String status = "IN APPROVAL";

    @Column(name = "cm_object_type", insertable = true, updatable = false)
    private String objectType = CloseComplaintRequestConstants.OBJECT_TYPE;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumns({
            @JoinColumn(name = "cm_object_id"),
            @JoinColumn(name = "cm_object_type", referencedColumnName = "cm_object_type")
    })
    private List<AcmParticipant> participants = new ArrayList<>();

    @Column(name = "cm_close_complaint_created", nullable = false, insertable = true, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "cm_close_complaint_creator", insertable = true, updatable = false)
    private String creator;

    @Column(name = "cm_close_complaint_modified", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    @Column(name = "cm_close_complaint_modifier")
    private String modifier;

    @Column(name = "cm_close_complaint_description")
    private String description;

    @Transient
    private Long referExternalPersonId;

    @Transient
    private Long referExternalOrganizationId;

    @Transient
    private boolean closeComplaintStatusFlow;

    @PrePersist
    public void beforeInsert()
    {
        setupChildPointers();
    }

    private void setupChildPointers()
    {
        for (AcmParticipant ap : getParticipants())
        {
            ap.setObjectId(getId());
            ap.setObjectType(CloseComplaintRequestConstants.OBJECT_TYPE);
        }
    }

    @PreUpdate
    public void beforeUpdate()
    {
        setupChildPointers();
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

    public Long getComplaintId()
    {
        return complaintId;
    }

    public void setComplaintId(Long complaintId)
    {
        this.complaintId = complaintId;
    }

    public Disposition getDisposition()
    {
        return disposition;
    }

    public void setDisposition(Disposition disposition)
    {
        this.disposition = disposition;
    }

    public String getStatus()
    {
        return status;
    }

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

    public List<AcmParticipant> getParticipants()
    {
        return participants;
    }

    public void setParticipants(List<AcmParticipant> participants)
    {
        this.participants = participants;
    }

    @Override
    public String getObjectType()
    {
        return objectType;
    }

    @Override
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public Long getParentObjectId()
    {
        return complaintId;
    }

    @Override
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public String getParentObjectType()
    {
        return ComplaintConstants.OBJECT_TYPE;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public Long getReferExternalPersonId() {
        return referExternalPersonId;
    }

    public void setReferExternalPersonId(Long referExternalPersonId) {
        this.referExternalPersonId = referExternalPersonId;
    }

    public Long getReferExternalOrganizationId() {
        return referExternalOrganizationId;
    }

    public void setReferExternalOrganizationId(Long referExternalOrganizationId) {
        this.referExternalOrganizationId = referExternalOrganizationId;
    }

    public boolean isCloseComplaintStatusFlow() {
        return closeComplaintStatusFlow;
    }

    public void setCloseComplaintStatusFlow(boolean closeComplaintStatusFlow) {
        this.closeComplaintStatusFlow = closeComplaintStatusFlow;
    }
}
