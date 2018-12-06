/**
 *
 */
package com.armedia.acm.services.costsheet.model;

/*-
 * #%L
 * ACM Service: Costsheet
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
import com.armedia.acm.core.AcmStatefulEntity;
import com.armedia.acm.data.AcmEntity;
import com.armedia.acm.data.converter.BooleanToStringConverter;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmContainerEntity;
import com.armedia.acm.services.participants.model.AcmAssignedObject;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.users.model.AcmUser;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
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
 */
@Entity
@Table(name = "acm_costsheet")
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "className", defaultImpl = AcmCostsheet.class)
@DiscriminatorColumn(name = "cm_class_name", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("com.armedia.acm.services.costsheet.model.AcmCostsheet")
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class AcmCostsheet implements Serializable, AcmObject, AcmEntity, AcmStatefulEntity, AcmParentObjectInfo, AcmContainerEntity, AcmAssignedObject
{

    private static final long serialVersionUID = 6290288826480329085L;

    @Id
    @TableGenerator(name = "acm_costsheet_gen", table = "acm_costsheet_id", pkColumnName = "cm_seq_name", valueColumnName = "cm_seq_num", pkColumnValue = "acm_costsheet", initialValue = 100, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "acm_costsheet_gen")
    @Column(name = "cm_costsheet_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cm_costsheet_user_id")
    private AcmUser user;

    @Column(name = "cm_costsheet_object_id")
    private Long parentId;

    @Column(name = "cm_costsheet_object_type")
    private String parentType;

    @Column(name = "cm_costsheet_object_number")
    private String parentNumber;

    @Column(name = "cm_costsheet_number")
    private String costsheetNumber;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "costsheet")
    private List<AcmCost> costs = new ArrayList<>();

    @Column(name = "cm_costsheet_status")
    private String status;

    @Lob
    @Column(name = "cm_costsheet_details")
    private String details;

    @Column(name = "cm_costsheet_title")
    private String title;

    @Column(name = "cm_costsheet_creator")
    private String creator;

    @Column(name = "cm_costsheet_created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "cm_costsheet_modifier")
    private String modifier;

    @Column(name = "cm_costsheet_modified")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    @Column(name = "cm_costsheet_restricted_flag", nullable = false)
    @Convert(converter = BooleanToStringConverter.class)
    private Boolean restricted = Boolean.FALSE;

    @Column(name = "cm_object_type", insertable = true, updatable = false)
    private String objectType = CostsheetConstants.OBJECT_TYPE;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumns({
            @JoinColumn(name = "cm_object_id"),
            @JoinColumn(name = "cm_object_type", referencedColumnName = "cm_object_type")
    })
    private List<AcmParticipant> participants = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "cm_container_id")
    private AcmContainer container = new AcmContainer();

    @Column(name = "cm_class_name")
    private String className = this.getClass().getName();

    /**
     * This field is only used when the cost sheet is created. Usually it will be null. Use the container to get the
     * CMIS
     * object ID of the cost sheet folder.
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
        if (getCosts() != null)
        {
            for (AcmCost time : getCosts())
            {
                time.setCostsheet(this);
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
                getContainer().setContainerObjectTitle(getCostsheetNumber());
            }
        }
    }

    public String getCostsheetNumber()
    {
        return costsheetNumber;
    }

    public void setCostsheetNumber(String costsheetNumber)
    {
        this.costsheetNumber = costsheetNumber;
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

    public Long getParentId()
    {
        return parentId;
    }

    public void setParentId(Long parentId)
    {
        this.parentId = parentId;
    }

    public String getParentType()
    {
        return parentType;
    }

    public void setParentType(String parentType)
    {
        this.parentType = parentType;
    }

    public String getParentNumber()
    {
        return parentNumber;
    }

    public void setParentNumber(String parentNumber)
    {
        this.parentNumber = parentNumber;
    }

    public List<AcmCost> getCosts()
    {
        return costs;
    }

    public void setCosts(List<AcmCost> costs)
    {
        this.costs = costs;
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

    @Override
    public List<AcmParticipant> getParticipants()
    {
        return participants;
    }

    @Override
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

    public String getClassName()
    {
        return className;
    }

    public void setClassName(String className)
    {
        this.className = className;
    }

    @Override
    public Boolean getRestricted()
    {
        return restricted;
    }

    public void setRestricted(Boolean restricted)
    {
        this.restricted = restricted;
    }

    @Override
    @JsonIgnore
    public String getObjectType()
    {
        return objectType;
    }

    @Override
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public Long getParentObjectId()
    {
        return parentId;
    }

    @Override
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public String getParentObjectType()
    {
        return parentType;
    }

    public String getEcmFolderPath()
    {
        return ecmFolderPath;
    }

    public void setEcmFolderPath(String ecmFolderPath)
    {
        this.ecmFolderPath = ecmFolderPath;
    }

    @JsonIgnore
    public Double calculateBalance()
    {
        Double balance = 0.0;
        for (AcmCost acmCost : getCosts())
        {
            if (acmCost.getValue() > 0)
            {
                balance += acmCost.getValue();
            }
        }
        return balance;
    }
}
