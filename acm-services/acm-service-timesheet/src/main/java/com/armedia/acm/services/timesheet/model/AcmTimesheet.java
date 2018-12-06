/**
 * 
 */
package com.armedia.acm.services.timesheet.model;

/*-
 * #%L
 * ACM Service: Timesheet
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
import com.armedia.acm.core.AcmStatefulEntity;
import com.armedia.acm.data.AcmEntity;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmContainerEntity;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.users.model.AcmUser;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
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

/**
 * @author riste.tutureski
 *
 */
@Entity
@Table(name = "acm_timesheet")
@JsonIdentityInfo(generator = JSOGGenerator.class)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "className", defaultImpl = AcmTimesheet.class)
@DiscriminatorColumn(name = "cm_class_name", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("com.armedia.acm.services.timesheet.model.AcmTimesheet")
public class AcmTimesheet implements Serializable, AcmObject, AcmEntity, AcmStatefulEntity, AcmContainerEntity
{

    private static final long serialVersionUID = 3346214028142786165L;

    @Id
    @TableGenerator(name = "acm_timesheet_gen", table = "acm_timesheet_id", pkColumnName = "cm_seq_name", valueColumnName = "cm_seq_num", pkColumnValue = "acm_timesheet", initialValue = 100, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "acm_timesheet_gen")
    @Column(name = "cm_timesheet_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cm_timesheet_user_id")
    private AcmUser user;

    @Column(name = "cm_timesheet_start_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;

    @Column(name = "cm_timesheet_end_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "timesheet")
    private List<AcmTime> times = new ArrayList<>();

    @Column(name = "cm_timesheet_status")
    private String status;

    @Lob
    @Column(name = "cm_timesheet_details")
    private String details;

    @Column(name = "cm_timesheet_title")
    private String title;

    @Column(name = "cm_timesheet_number")
    private String timesheetNumber;

    @Column(name = "cm_timesheet_creator")
    private String creator;

    @Column(name = "cm_timesheet_created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "cm_timesheet_modifier")
    private String modifier;

    @Column(name = "cm_timesheet_modified")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    @Column(name = "cm_object_type", insertable = true, updatable = false)
    private String objectType = TimesheetConstants.OBJECT_TYPE;

    @Column(name = "cm_class_name")
    private String className = this.getClass().getName();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumns({
            @JoinColumn(name = "cm_object_id"),
            @JoinColumn(name = "cm_object_type", referencedColumnName = "cm_object_type")
    })
    private List<AcmParticipant> participants = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "cm_container_id")
    private AcmContainer container = new AcmContainer();

    /**
     * This field is only used when the time sheet is created. Usually it will be null. Use the container to get the
     * CMIS object ID of the time sheet folder.
     */
    @Transient
    private String ecmFolderPath;

    @PrePersist
    protected void beforeInsert()
    {
        setChildPointers();
    }

    @PreUpdate
    protected void beforeUpdate()
    {
        setChildPointers();
    }

    private void setChildPointers()
    {
        if (getTimes() != null)
        {
            for (AcmTime time : getTimes())
            {
                time.setTimesheet(this);
            }
        }

        if (objectType == null)
        {
            objectType = getObjectType();
        }

        if (getParticipants() != null)
        {
            for (AcmParticipant participant : getParticipants())
            {
                participant.setObjectId(getId());
                participant.setObjectType(getObjectType());
            }
        }

        if (getContainer() != null)
        {
            getContainer().setContainerObjectId(getId());
            getContainer().setContainerObjectType(getObjectType());
            if (getContainer().getContainerObjectTitle() == null)
            {
                getContainer().setContainerObjectTitle(getTimesheetNumber());
            }
        }
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

    public AcmUser getUser()
    {
        return user;
    }

    public void setUser(AcmUser user)
    {
        this.user = user;
    }

    public Date getStartDate()
    {
        return startDate;
    }

    public void setStartDate(Date startDate)
    {
        this.startDate = startDate;
    }

    public Date getEndDate()
    {
        return endDate;
    }

    public void setEndDate(Date endDate)
    {
        this.endDate = endDate;
    }

    public List<AcmTime> getTimes()
    {
        return times;
    }

    public void setTimes(List<AcmTime> times)
    {
        this.times = times;
    }

    public String getTimesheetNumber()
    {
        return timesheetNumber;
    }

    public void setTimesheetNumber(String timesheetNumber)
    {
        this.timesheetNumber = timesheetNumber;
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

    public String getDetails()
    {
        return details;
    }

    public void setDetails(String details)
    {
        this.details = details;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
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
    public Date getModified()
    {
        return modified;
    }

    @Override
    public void setModified(Date modified)
    {
        this.modified = modified;
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
    @JsonIgnore
    public String getObjectType()
    {
        return objectType;
    }

    public String getClassName()
    {
        return className;
    }

    public void setClassName(String className)
    {
        this.className = className;
    }

    public String getEcmFolderPath()
    {
        return ecmFolderPath;
    }

    public void setEcmFolderPath(String ecmFolderPath)
    {
        this.ecmFolderPath = ecmFolderPath;
    }
}
