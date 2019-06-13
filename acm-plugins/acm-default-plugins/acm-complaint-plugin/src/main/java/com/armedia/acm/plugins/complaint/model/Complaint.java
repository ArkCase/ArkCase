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

import com.armedia.acm.core.AcmNotifiableEntity;
import com.armedia.acm.core.AcmNotificationReceiver;
import com.armedia.acm.core.AcmObjectNumber;
import com.armedia.acm.core.AcmStatefulEntity;
import com.armedia.acm.core.AcmTitleEntity;
import com.armedia.acm.data.AcmEntity;
import com.armedia.acm.data.AcmLegacySystemEntity;
import com.armedia.acm.data.converter.BooleanToStringConverter;
import com.armedia.acm.plugins.addressable.model.PostalAddress;
import com.armedia.acm.plugins.casefile.model.Disposition;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmContainerEntity;
import com.armedia.acm.plugins.objectassociation.model.AcmChildObjectEntity;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociation;
import com.armedia.acm.plugins.person.model.AcmObjectOriginator;
import com.armedia.acm.plugins.person.model.OrganizationAssociation;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import com.armedia.acm.services.participants.model.AcmAssignedObject;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.sequence.annotation.AcmSequence;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

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
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Created by armdev on 4/4/14.
 */
@Entity
@Table(name = "acm_complaint")
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "className", defaultImpl = Complaint.class)
@DiscriminatorColumn(name = "cm_class_name", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("com.armedia.acm.plugins.complaint.model.Complaint")
@JsonPropertyOrder(value = { "complaintId", "personAssociations", "originator" })
@JsonIdentityInfo(generator = JSOGGenerator.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Complaint implements Serializable, AcmAssignedObject, AcmEntity, AcmContainerEntity, AcmChildObjectEntity,
        AcmLegacySystemEntity, AcmNotifiableEntity, AcmStatefulEntity, AcmTitleEntity, AcmObjectNumber, AcmObjectOriginator
{
    private static final long serialVersionUID = -1154137631399833851L;
    private transient final Logger log = LogManager.getLogger(getClass());

    @Id
    @TableGenerator(name = "complaint_gen", table = "acm_complaint_id", pkColumnName = "cm_seq_name", valueColumnName = "cm_seq_num", pkColumnValue = "acm_complaint", initialValue = 100, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "complaint_gen")
    @Column(name = "cm_complaint_id")
    private Long complaintId;

    @Column(name = "cm_complaint_number", insertable = true, updatable = false)
    @AcmSequence(sequenceName = "complaintNumberSequence")
    private String complaintNumber;

    @Column(name = "cm_complaint_type")
    private String complaintType;

    @Column(name = "cm_complaint_priority")
    private String priority;

    @Column(name = "cm_complaint_title")
    @Size(min = 1)
    private String complaintTitle;

    @Lob
    @Column(name = "cm_complaint_details")
    private String details;

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

    @Transient
    private PersonAssociation originator;

    /**
     * This field is only used when the complaint is created. Usually it will be null. Use the container to get the CMIS
     * object ID of the complaint folder.
     */
    @Transient
    private String ecmFolderPath;

    /**
     * Container folder where the complaint's attachments/content files are stored.
     */
    @OneToOne
    @JoinColumn(name = "cm_container_id")
    private AcmContainer container = new AcmContainer();

    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.REFRESH })
    @JoinColumns({ @JoinColumn(name = "cm_parent_id", referencedColumnName = "cm_complaint_id"),
            @JoinColumn(name = "cm_parent_type", referencedColumnName = "cm_object_type") })
    private Collection<ObjectAssociation> childObjects = new ArrayList<>();

    /**
     * These approvers are added by the web application and they become the assignees of the Activiti business process.
     * They are not persisted to the database.
     */
    @Transient
    private List<String> approvers;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumns({ @JoinColumn(name = "cm_person_assoc_parent_id", referencedColumnName = "cm_complaint_id"),
            @JoinColumn(name = "cm_person_assoc_parent_type", referencedColumnName = "cm_object_type") })
    @OrderBy("created ASC")
    private List<PersonAssociation> personAssociations = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumns({ @JoinColumn(name = "cm_parent_id", referencedColumnName = "cm_complaint_id"),
            @JoinColumn(name = "cm_parent_type", referencedColumnName = "cm_object_type") })
    @OrderBy("created ASC")
    private List<OrganizationAssociation> organizationAssociations = new ArrayList<>();

    @Column(name = "cm_object_type", insertable = true, updatable = false)
    private String objectType = ComplaintConstants.OBJECT_TYPE;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumns({ @JoinColumn(name = "cm_object_id"), @JoinColumn(name = "cm_object_type", referencedColumnName = "cm_object_type") })
    private List<AcmParticipant> participants = new ArrayList<>();

    @Column(name = "cm_due_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dueDate;

    @Column(name = "cm_tag")
    private String tag;

    @Column(name = "cm_frequency")
    private String frequency;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "acm_complaint_postal_address", joinColumns = {
            @JoinColumn(name = "cm_complaint_id", referencedColumnName = "cm_complaint_id") }, inverseJoinColumns = {
                    @JoinColumn(name = "cm_address_id", referencedColumnName = "cm_address_id") })
    private List<PostalAddress> addresses = new ArrayList<>();

    /**
     * Complaint disposition is set only when the close complaint request is approved. Until then, the requested
     * disposition (if any) is linked from the acm_close_complaint_request table (CloseComplaintRequest POJO).
     */
    @OneToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "cm_disposition_id", insertable = false, updatable = true)
    private Disposition disposition;

    @Column(name = "cm_complaint_restricted_flag", nullable = false)
    @Convert(converter = BooleanToStringConverter.class)
    private Boolean restricted = false;

    @Column(name = "cm_class_name")
    private String className = this.getClass().getName();

    @Column(name = "cm_legacy_system_id")
    private String legacySystemId;

    /**
     * PostalAddress which is default
     */
    @ManyToOne
    @JoinColumn(name = "cm_default_address")
    private PostalAddress defaultAddress;

    @PrePersist
    protected void beforeInsert()
    {
        if (getStatus() == null || getStatus().trim().isEmpty())
        {
            setStatus("DRAFT");
        }

        if (getOriginator() != null)
        {
            personAssociationResolver(getOriginator());
        }

        setupChildPointers();
    }

    private void setupChildPointers()
    {
        for (ObjectAssociation childObject : childObjects)
        {
            childObject.setParentId(complaintId);
        }
        for (PersonAssociation persAssoc : personAssociations)
        {
            personAssociationResolver(persAssoc);
        }
        for (AcmParticipant ap : getParticipants())
        {
            ap.setObjectId(getComplaintId());
            ap.setObjectType(getObjectType());
        }
        for (OrganizationAssociation oa : getOrganizationAssociations())
        {
            oa.setParentId(getComplaintId());
            oa.setParentTitle(getComplaintNumber());
            oa.setParentType(getObjectType());
        }

        if (getContainer() != null)
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

        if (getContainer() != null)
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

    @Override
    @JsonIgnore
    public String getTitle()
    {
        return complaintTitle;
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

    public PostalAddress getDefaultAddress()
    {
        return defaultAddress;
    }

    public void setDefaultAddress(PostalAddress defaultAddress)
    {
        this.defaultAddress = defaultAddress;
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

    @Override
    public void setStatus(String status)
    {
        this.status = status;
    }

    public PersonAssociation getOriginator()
    {
        if (getPersonAssociations() == null)
        {
            return null;
        }

        Optional<PersonAssociation> found = getPersonAssociations().stream()
                .filter(personAssociation -> "Initiator".equalsIgnoreCase(personAssociation.getPersonType())).findFirst();

        if (found.isPresent())
        {
            originator = found.get();
        }

        return originator;
    }

    public void setOriginator(PersonAssociation originator)
    {
        this.originator = originator;

        if (getPersonAssociations() == null)
        {
            setPersonAssociations(new ArrayList<>());
        }

        if (originator != null)
        {

            Optional<PersonAssociation> found = getPersonAssociations().stream()
                    .filter(personAssociation -> "Initiator".equalsIgnoreCase(personAssociation.getPersonType())).findFirst();

            if (!found.isPresent())
            {
                getPersonAssociations().add(originator);
            }
        }
    }

    public String getEcmFolderPath()
    {
        return ecmFolderPath;
    }

    public void setEcmFolderPath(String ecmFolderPath)
    {
        this.ecmFolderPath = ecmFolderPath;
    }

    @Override
    public Collection<ObjectAssociation> getChildObjects()
    {
        return Collections.unmodifiableCollection(childObjects);
    }

    @Override
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

    public void setPersonAssociations(List<PersonAssociation> personAssociations)
    {
        this.personAssociations = personAssociations;
    }

    private void personAssociationResolver(PersonAssociation personAssoc)
    {
        personAssoc.setParentId(getComplaintId());
        personAssoc.setParentType(ComplaintConstants.OBJECT_TYPE);
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

    public List<PostalAddress> getAddresses()
    {
        return addresses;
    }

    public void setAddresses(List<PostalAddress> addresses)
    {
        this.addresses = addresses;
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

    public Disposition getDisposition()
    {
        return disposition;
    }

    public void setDisposition(Disposition disposition)
    {
        this.disposition = disposition;
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

    public String getClassName()
    {
        return className;
    }

    public void setClassName(String className)
    {
        this.className = className;
    }

    @Override
    public AcmContainer getContainer()
    {
        return container;
    }

    @Override
    public void setContainer(AcmContainer container)
    {
        if (container != null)
        {
            container.setContainerObjectType(getObjectType());
        }

        this.container = container;
    }

    @Override
    public String toString()
    {
        return "Complaint{" + "complaintId=" + complaintId + ", complaintNumber='" + complaintNumber + '\'' + ", complaintType='"
                + complaintType + '\'' + ", priority='" + priority + '\''
                + ", complaintTitle='" + complaintTitle + '\'' + ", details='" + details + '\'' + ", incidentDate=" + incidentDate
                + ", created=" + created + ", creator='" + creator + '\''
                + ", modified=" + modified + ", modifier='" + modifier + '\'' + ", status='" + status + '\'' + ", originator=" + originator
                + ", ecmFolderPath='" + ecmFolderPath + '\''
                + ", container=" + container + ", childObjects=" + childObjects + ", approvers=" + approvers + ", personAssociations="
                + personAssociations + ", participants=" + participants
                + ", dueDate=" + dueDate + ", tag='" + tag + '\'' + ", frequency='" + frequency + '\'' + ", addresses=" + addresses
                + ", disposition=" + disposition + ", restricted=" + restricted + ", legacySystemId='" + legacySystemId + "'}";
    }

    @Override
    public String getLegacySystemId()
    {
        return legacySystemId;
    }

    @Override
    public void setLegacySystemId(String legacySystemId)
    {
        this.legacySystemId = legacySystemId;
    }

    @Override
    @JsonIgnore
    public Set<AcmNotificationReceiver> getReceivers()
    {
        Set<AcmNotificationReceiver> receivers = new HashSet<>();
        receivers.addAll(participants);
        return receivers;
    }

    @Override
    @JsonIgnore
    public String getNotifiableEntityTitle()
    {
        return complaintNumber;
    }

    @JsonIgnore
    public String getAssigneeGroup()
    {
        String groupName = null;
        AcmParticipant owningGroup = getParticipants().stream().filter(p -> ComplaintConstants.OWNING_GROUP.equals(p.getParticipantType()))
                .findFirst().orElse(null);
        AcmParticipant assignee = getParticipants().stream().filter(p -> ComplaintConstants.ASSIGNEE.equals(p.getParticipantType()))
                .findFirst().orElse(null);
        if (owningGroup != null && assignee != null && assignee.getParticipantLdapId().isEmpty())
        {
            groupName = owningGroup.getParticipantLdapId();
        }
        return groupName;
    }

    @Override
    @JsonIgnore
    public String getNotifiableEntityNumber() {
        return complaintNumber;
    }

    public List<OrganizationAssociation> getOrganizationAssociations()
    {
        return organizationAssociations;
    }

    public void setOrganizationAssociations(List<OrganizationAssociation> organizationAssociations)
    {
        this.organizationAssociations = organizationAssociations;
    }

    @Override
    public String getAcmObjectNumber()
    {
        return getComplaintNumber();
    }

    @Override
    public PersonAssociation getAcmObjectOriginator()
    {
        return getOriginator();
    }
}
