package com.armedia.acm.plugins.casefile.model;

/*-
 * #%L
 * ACM Default Plugin: Case File
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
import com.armedia.acm.data.AcmAssignee;
import com.armedia.acm.data.converter.BooleanToStringConverter;
import com.armedia.acm.data.converter.LocalDateConverter;
import com.armedia.acm.data.converter.LocalDateTimeConverter;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmContainerEntity;
import com.armedia.acm.plugins.objectassociation.model.AcmChildObjectEntity;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociation;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociationConstants;
import com.armedia.acm.plugins.person.model.AcmObjectOriginator;
import com.armedia.acm.plugins.person.model.OrganizationAssociation;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import com.armedia.acm.service.milestone.model.AcmMilestone;
import com.armedia.acm.service.objectlock.model.AcmObjectLock;
import com.armedia.acm.services.participants.model.AcmAssignedObject;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.sequence.annotation.AcmSequence;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

import org.springframework.format.annotation.DateTimeFormat;

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
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.Lob;
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
import javax.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Entity
@Table(name = "acm_case_file")
@XmlRootElement(name = "caseFile")
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "className", defaultImpl = CaseFile.class)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "cm_class_name", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("com.armedia.acm.plugins.casefile.model.CaseFile")
@JsonPropertyOrder(value = { "id", "personAssociations", "originator" })
@JsonIdentityInfo(generator = JSOGGenerator.class)
@JsonIgnoreProperties(ignoreUnknown = true)

public class CaseFile implements Serializable, AcmAssignedObject, AcmEntity,
        AcmContainerEntity, AcmChildObjectEntity, AcmLegacySystemEntity, AcmNotifiableEntity, AcmStatefulEntity, AcmTitleEntity,
        AcmObjectNumber, AcmObjectOriginator, AcmAssignee
{
    private static final long serialVersionUID = -6035628455385955008L;

    @Id
    @TableGenerator(name = "case_file_gen", table = "acm_case_file_id", pkColumnName = "cm_seq_name", valueColumnName = "cm_seq_num", pkColumnValue = "acm_case_file", initialValue = 100, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "case_file_gen")
    @Column(name = "cm_case_id")
    private Long id;

    @Column(name = "cm_case_number")
    @AcmSequence(sequenceName = "caseNumberSequence")
    private String caseNumber;

    @Column(name = "cm_case_type")
    private String caseType;

    @Column(name = "cm_case_title")
    @Size(min = 1)
    private String title;

    @Column(name = "cm_case_status")
    private String status;

    @Lob
    @Column(name = "cm_case_details")
    private String details;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "cm_case_incident_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date incidentDate;

    @Column(name = "cm_case_created", nullable = false, insertable = true, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "cm_case_creator", insertable = true, updatable = false)
    private String creator;

    @Column(name = "cm_case_modified", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    @Column(name = "cm_case_modifier")
    private String modifier;

    @Column(name = "cm_case_closed")
    @Temporal(TemporalType.TIMESTAMP)
    private Date closed;

    @Column(name = "cm_case_disposition")
    private String disposition;

    @Column(name = "cm_case_priority")
    private String priority;

    @Column(name = "cm_case_external_flag")
    private boolean external = false;

    @Column(name = "cm_object_type", insertable = true, updatable = false)
    private String objectType = CaseFileConstants.OBJECT_TYPE;

    @Column(name = "cm_class_name")
    private String className = this.getClass().getName();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumns({ @JoinColumn(name = "cm_object_id"), @JoinColumn(name = "cm_object_type", referencedColumnName = "cm_object_type") })
    private List<AcmParticipant> participants = new ArrayList<>();

    @Column(name = "cm_due_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dueDate;

    @Transient
    private ChangeCaseStatus changeCaseStatus;

    /**
     * These approvers are added by the web application and they become the assignees of the Activiti business process.
     * They are not persisted to the database.
     */
    @Transient
    private List<String> approvers;

    /**
     * This field is only used when the case file is created. Usually it will be null. Use the container to get the CMIS
     * object ID of the case file folder.
     */
    @Transient
    private String ecmFolderPath;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumns({ @JoinColumn(name = "cm_person_assoc_parent_id", referencedColumnName = "cm_case_id"),
            @JoinColumn(name = "cm_person_assoc_parent_type", referencedColumnName = "cm_object_type") })
    @OrderBy("created ASC")
    private List<PersonAssociation> personAssociations = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumns({ @JoinColumn(name = "cm_parent_id", referencedColumnName = "cm_case_id"),
            @JoinColumn(name = "cm_parent_type", referencedColumnName = "cm_object_type") })
    @OrderBy("created ASC")
    private List<OrganizationAssociation> organizationAssociations = new ArrayList<>();

    /**
     * Milestones are read-only in the parent object; use the milestone service to add them.
     */
    @OneToMany
    @JoinColumn(name = "cm_milestone_object_id", updatable = false, insertable = false)
    private List<AcmMilestone> milestones = new ArrayList<>();

    @Column(name = "cm_case_restricted_flag", nullable = false)
    @Convert(converter = BooleanToStringConverter.class)
    private Boolean restricted = Boolean.FALSE;

    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.REFRESH })
    @JoinColumns({ @JoinColumn(name = "cm_parent_id"), @JoinColumn(name = "cm_parent_type", referencedColumnName = "cm_object_type") })
    private Collection<ObjectAssociation> childObjects = new ArrayList<>();

    /**
     * Container folder where the case file's attachments/content files are stored.
     */
    @OneToOne
    @JoinColumn(name = "cm_container_id")
    private AcmContainer container = new AcmContainer();

    @Column(name = "cm_courtroom_name")
    private String courtroomName;

    @Column(name = "cm_responsible_organization")
    private String responsibleOrganization;

    @Column(name = "cm_next_court_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date nextCourtDate;

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumns({ @JoinColumn(name = "cm_case_id", referencedColumnName = "cm_object_id", updatable = false, insertable = false),
            @JoinColumn(name = "cm_object_type", referencedColumnName = "cm_object_type", updatable = false, insertable = false) })
    private AcmObjectLock lock;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cm_queue_id")
    private AcmQueue queue;

    @Column(name = "cm_queue_enter_date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime queueEnterDate;

    @Column(name = "cm_response_due_date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Convert(converter = LocalDateConverter.class)
    private LocalDate responseDueDate;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cm_previous_queue_id")
    private AcmQueue previousQueue;

    @Column(name = "cm_security_field")
    private String securityField;

    @Column(name = "cm_legacy_system_id")
    private String legacySystemId;

    @Column(name = "cm_case_denied_flag")
    @Convert(converter = BooleanToStringConverter.class)
    private Boolean deniedFlag = Boolean.FALSE;

    @PrePersist
    protected void beforeInsert()
    {
        if (getStatus() == null || getStatus().trim().isEmpty())
        {
            setStatus("DRAFT");
        }

        setupChildPointers();
    }

    private void setupChildPointers()
    {
        for (ObjectAssociation childObject : childObjects)
        {
            childObject.setParentId(getId());
            childObject.setParentName(getCaseNumber());
            childObject.setParentType(getObjectType());
        }
        for (PersonAssociation persAssoc : personAssociations)
        {
            personAssociationResolver(persAssoc);
        }
        for (OrganizationAssociation orgAssoc : organizationAssociations)
        {
            orgAssoc.setParentId(getId());
            orgAssoc.setParentType(getObjectType());
            orgAssoc.setParentTitle(getCaseNumber());
        }
        for (AcmParticipant ap : getParticipants())
        {
            ap.setObjectId(getId());
            ap.setObjectType(getObjectType());
        }

        if (getContainer() != null)
        {
            getContainer().setContainerObjectId(getId());
            getContainer().setContainerObjectType(getObjectType());
            getContainer().setContainerObjectTitle(getCaseNumber());
        }
    }

    @PreUpdate
    protected void beforeUpdate()
    {
        setupChildPointers();
    }

    private void personAssociationResolver(PersonAssociation personAssoc)
    {
        personAssoc.setParentId(getId());
        personAssoc.setParentType(getObjectType());
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
        childObject.setParentName(getCaseNumber());
        childObject.setParentType(getObjectType());
        childObject.setParentId(getId());
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
            return found.get();
        }

        return null;
    }

    public void setOriginator(PersonAssociation originator)
    {

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

    @Override
    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getCaseNumber()
    {
        return caseNumber;
    }

    public void setCaseNumber(String caseNumber)
    {
        this.caseNumber = caseNumber;
    }

    public String getCaseType()
    {
        return caseType;
    }

    public void setCaseType(String caseType)
    {
        this.caseType = caseType;
    }

    @Override
    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
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

    public String getEcmFolderPath()
    {
        return ecmFolderPath;
    }

    public void setEcmFolderPath(String ecmFolderPath)
    {
        this.ecmFolderPath = ecmFolderPath;
    }

    public Date getClosed()
    {
        return closed;
    }

    public void setClosed(Date closed)
    {
        this.closed = closed;
    }

    public String getDisposition()
    {
        return disposition;
    }

    public void setDisposition(String disposition)
    {
        this.disposition = disposition;
    }

    public String getPriority()
    {
        return priority;
    }

    public void setPriority(String priority)
    {
        this.priority = priority;
    }

    /**
     * @return the external
     */
    public boolean isExternal()
    {
        return external;
    }

    /**
     * @param external
     *            the external to set
     */
    public void setExternal(boolean external)
    {
        this.external = external;
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

    @JsonGetter
    public List<ObjectAssociation> getReferences()
    {
        List<ObjectAssociation> retval = new ArrayList<>();

        if (getChildObjects() != null)
        {
            for (ObjectAssociation child : childObjects)
            {
                if (ObjectAssociationConstants.REFFERENCE_TYPE.equals(child.getAssociationType()))
                {
                    retval.add(child);
                }
            }
        }

        return retval;
    }

    public ChangeCaseStatus getChangeCaseStatus()
    {
        return changeCaseStatus;
    }

    public void setChangeCaseStatus(ChangeCaseStatus changeCaseStatus)
    {
        this.changeCaseStatus = changeCaseStatus;
    }

    public List<String> getApprovers()
    {
        return approvers;
    }

    public void setApprovers(List<String> approvers)
    {
        this.approvers = approvers;
    }

    public Date getDueDate()
    {
        return dueDate;
    }

    public void setDueDate(Date dueDate)
    {
        this.dueDate = dueDate;
    }

    public List<PersonAssociation> getPersonAssociations()
    {
        return personAssociations;
    }

    public void setPersonAssociations(List<PersonAssociation> personAssociations)
    {
        this.personAssociations = personAssociations;
    }

    public List<AcmMilestone> getMilestones()
    {
        return milestones;
    }

    public void setMilestones(List<AcmMilestone> milestones)
    {
        this.milestones = milestones;
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

    public String getCourtroomName()
    {
        return courtroomName;
    }

    public void setCourtroomName(String courtroomName)
    {
        this.courtroomName = courtroomName;
    }

    public String getResponsibleOrganization()
    {
        return responsibleOrganization;
    }

    public void setResponsibleOrganization(String responsibleOrganization)
    {
        this.responsibleOrganization = responsibleOrganization;
    }

    public Date getNextCourtDate()
    {
        return nextCourtDate;
    }

    public void setNextCourtDate(Date nextCourtDate)
    {
        this.nextCourtDate = nextCourtDate;
    }

    public String getClassName()
    {
        return className;
    }

    public void setClassName(String className)
    {
        this.className = className;
    }

    public AcmObjectLock getLock()
    {
        return lock;
    }

    public void setLock(AcmObjectLock lock)
    {
        this.lock = lock;
    }

    public AcmQueue getQueue()
    {
        return queue;
    }

    public void setQueue(AcmQueue queue)
    {
        this.queue = queue;
    }

    public String getSecurityField()
    {
        return securityField;
    }

    public void setSecurityField(String securityField)
    {
        this.securityField = securityField;
    }

    @Override
    @JsonIgnore
    public String getObjectType()
    {
        return objectType;
    }

    @Override
    public String toString()
    {
        return "CaseFile{" +
                "id=" + id +
                ", caseNumber='" + caseNumber + '\'' +
                ", caseType='" + caseType + '\'' +
                ", title='" + title + '\'' +
                ", status='" + status + '\'' +
                ", details='" + details + '\'' +
                ", incidentDate=" + incidentDate +
                ", created=" + created +
                ", creator='" + creator + '\'' +
                ", modified=" + modified +
                ", modifier='" + modifier + '\'' +
                ", closed=" + closed +
                ", disposition='" + disposition + '\'' +
                ", priority='" + priority + '\'' +
                ", objectType='" + objectType + '\'' +
                ", className='" + className + '\'' +
                ", participants=" + participants +
                ", dueDate=" + dueDate +
                ", changeCaseStatus=" + changeCaseStatus +
                ", approvers=" + approvers +
                ", ecmFolderPath='" + ecmFolderPath + '\'' +
                ", personAssociations=" + personAssociations +
                ", organizationAssociations=" + organizationAssociations +
                ", milestones=" + milestones +
                ", restricted=" + restricted +
                ", childObjects=" + childObjects +
                ", container=" + container +
                ", courtroomName='" + courtroomName + '\'' +
                ", responsibleOrganization='" + responsibleOrganization + '\'' +
                ", nextCourtDate=" + nextCourtDate +
                ", lock=" + lock +
                ", queue=" + queue +
                ", queueEnterDate=" + queueEnterDate +
                ", responseDueDate=" + responseDueDate +
                ", previousQueue=" + previousQueue +
                ", securityField='" + securityField + '\'' +
                ", legacySystemId='" + legacySystemId + '\'' +
                '}';
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

    /**
     * @return the deniedFlag
     */
    public Boolean getDeniedFlag()
    {
        return deniedFlag;
    }

    /**
     * @param deniedFlag
     *            the deniedFlag to set
     */
    public void setDeniedFlag(Boolean deniedFlag)
    {
        this.deniedFlag = deniedFlag;
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
        return caseNumber;
    }

    @JsonIgnore
    public String getAssigneeGroup()
    {
        String groupName = null;
        AcmParticipant owningGroup = getParticipants().stream()
                .filter(p -> CaseFileConstants.OWNING_GROUP.equals(p.getParticipantType())).findFirst().orElse(null);
        AcmParticipant assignee = getParticipants().stream().filter(p -> CaseFileConstants.ASSIGNEE.equals(p.getParticipantType()))
                .findFirst().orElse(null);
        if (owningGroup != null && assignee != null && assignee.getParticipantLdapId().isEmpty())
        {
            groupName = owningGroup.getParticipantLdapId();
        }
        return groupName;
    }

    @Override
    @JsonIgnore
    public String getAssigneeLdapId()
    {
        return getParticipants().stream().filter(p -> CaseFileConstants.ASSIGNEE.equals(p.getParticipantType()))
                .findFirst().map(p -> p.getParticipantLdapId()).orElse(null);
    }

    @Override
    @JsonIgnore
    public String getAssigneeGroupId()
    {
        return getParticipants().stream().filter(p -> CaseFileConstants.OWNING_GROUP.equals(p.getParticipantType()))
                .findFirst().map(p -> p.getParticipantLdapId()).orElse(null);
    }
    
    @Override
    @JsonIgnore
    public String getNotifiableEntityNumber()
    {
        return caseNumber;
    }

    public LocalDateTime getQueueEnterDate()
    {
        return queueEnterDate;
    }

    public void setQueueEnterDate(LocalDateTime queueEnterDate)
    {
        this.queueEnterDate = queueEnterDate;
    }

    public LocalDate getResponseDueDate()
    {
        return responseDueDate;
    }

    public void setResponseDueDate(LocalDate responseDueDate)
    {
        this.responseDueDate = responseDueDate;
    }

    public AcmQueue getPreviousQueue()
    {
        return previousQueue;
    }

    public void setPreviousQueue(AcmQueue previousQueue)
    {
        this.previousQueue = previousQueue;
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
        return getCaseNumber();
    }

    @Override
    public PersonAssociation getAcmObjectOriginator()
    {
        return getOriginator();
    }
}
