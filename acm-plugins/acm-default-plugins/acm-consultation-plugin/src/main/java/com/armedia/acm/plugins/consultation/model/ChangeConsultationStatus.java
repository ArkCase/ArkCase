package com.armedia.acm.plugins.consultation.model;

/*-
 * #%L
 * ACM Default Plugin: Consultation
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on May, 2020
 */
@Entity
@Table(name = "acm_change_consultation_status")
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class ChangeConsultationStatus implements Serializable, AcmObject, AcmEntity, AcmParentObjectInfo
{
    @Id
    @TableGenerator(name = "change_consultation_status_gen", table = "acm_change_consultation_status_id", pkColumnName = "cm_seq_name", valueColumnName = "cm_seq_num", pkColumnValue = "acm_change_consultation_status", initialValue = 100, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "change_consultation_status_gen")
    @Column(name = "cm_change_consultation_status_id")
    private Long id;

    @Column(name = "cm_consultation_id")
    private Long consultationId;

    @Column(name = "cm_change_consultation_status_status")
    private String status = "ACTIVE";

    @Transient
    private String consultationResolution;

    @Transient
    private LocalDate changeDate;

    @Column(name = "cm_object_type", insertable = true, updatable = false)
    private String objectType = ChangeConsultationStatusConstants.OBJECT_TYPE;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumns({
            @JoinColumn(name = "cm_object_id"),
            @JoinColumn(name = "cm_object_type", referencedColumnName = "cm_object_type")
    })
    private List<AcmParticipant> participants = new ArrayList<>();

    @Column(name = "cm_change_consultation_status_created", nullable = false, insertable = true, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "cm_change_consultation_status_creator", insertable = true, updatable = false)
    private String creator;

    @Column(name = "cm_change_consultation_status_modified", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    @Column(name = "cm_change_consultation_status_modifier")
    private String modifier;

    @Transient
    private boolean changeConsultationStatusFlow;

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
            ap.setObjectType(ChangeConsultationStatusConstants.OBJECT_TYPE);
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

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
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
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public Long getParentObjectId()
    {
        return consultationId;
    }

    @Override
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public String getParentObjectType()
    {
        return ConsultationConstants.OBJECT_TYPE;
    }

    public LocalDate getChangeDate()
    {
        return changeDate;
    }

    public void setChangeDate(LocalDate changeDate)
    {
        this.changeDate = changeDate;
    }

    public Long getConsultationId()
    {
        return consultationId;
    }

    public void setConsultationId(Long consultationId)
    {
        this.consultationId = consultationId;
    }

    public String getConsultationResolution()
    {
        return consultationResolution;
    }

    public void setConsultationResolution(String consultationResolution)
    {
        this.consultationResolution = consultationResolution;
    }

    public void setObjectType(String objectType)
    {
        this.objectType = objectType;
    }

    public boolean isChangeConsultationStatusFlow()
    {
        return changeConsultationStatusFlow;
    }

    public void setChangeConsultationStatusFlow(boolean changeConsultationStatusFlow)
    {
        this.changeConsultationStatusFlow = changeConsultationStatusFlow;
    }
}
