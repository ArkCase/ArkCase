package com.armedia.acm.plugins.task.model;

/*-
 * #%L
 * ACM Default Plugin: Tasks
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
import com.armedia.acm.core.AcmParentObjectInfo;
import com.armedia.acm.core.AcmStatefulEntity;
import com.armedia.acm.data.AcmLegacySystemEntity;
import com.armedia.acm.data.BuckslipFutureTask;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociation;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociationConstants;
import com.armedia.acm.services.participants.model.AcmAssignedObject;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.search.model.SearchConstants;
import com.armedia.acm.services.users.model.AcmUser;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@JsonIdentityInfo(generator = JSOGGenerator.class)
public class AcmTask implements AcmAssignedObject, Serializable, AcmLegacySystemEntity, AcmParentObjectInfo, AcmNotifiableEntity, AcmStatefulEntity
{
    private static final long serialVersionUID = 8087833770464474147L;

    private Long taskId;
    private String priority;
    @Size(min = 1)
    private String title;

    @NotNull
    private Date dueDate;
    private String attachedToObjectType;
    private String attachedToObjectName;
    private Long attachedToObjectId;
    private String assignee;
    private String owner; // creator
    private String businessProcessName;
    private Long businessProcessId;
    private boolean adhocTask;
    private boolean completed;
    private String status;
    private String pendingStatus;
    private Integer percentComplete;
    private String details;
    private String type;

    private Date createDate;
    private Date taskStartDate;

    @JsonFormat(pattern = SearchConstants.ISO_DATE_FORMAT)
    private Date taskFinishedDate;
    private Long taskDurationInMillis;
    private String workflowRequestType;
    private Long workflowRequestId;
    private String reviewDocumentPdfRenditionId;
    private Long reviewDocumentFormXmlId;
    private EcmFile documentUnderReview;
    private List<EcmFile> documentsToReview;
    private List<ObjectAssociation> childObjects;
    private String reworkInstructions;
    private String outcomeName;
    private List<TaskOutcome> availableOutcomes = new ArrayList<>();
    private TaskOutcome taskOutcome;
    private List<AcmParticipant> participants;
    private Set<AcmNotificationReceiver> receivers = new HashSet<>();

    private AcmContainer container;

    /**
     * ecmFolderPath is a transient property; it is set by business rules when the task is created, and is used to
     * create the right folder. For existing tasks, container has the folderId.
     */
    private transient String ecmFolderPath;
    private Long parentObjectId;
    private String parentObjectType;
    private String parentObjectTitle;
    private String parentObjectName;
    private transient String taskNotes;

    /**
     * Names of the groups whose members can claim this task. If the task has candidate group, but does not have an
     * assignee, then any member of any of the candidate group can claim the task. When a user claims a task, then the
     * task is assigned to that user, and is not part of the group bucket any more.
     */
    private List<String> candidateGroups = new ArrayList<>();

    /**
     * User ID of the next person who should be responsible for the business process. Many workflows allow the current
     * responsible party to select the next person who should have it. This field is used to store the next person.
     */
    private String nextAssignee;

    private String legacySystemId;

    private boolean buckslipTask;

    /**
     * @deprecated use buckslipFutureTasks
     */
    @Deprecated
    private List<AcmUser> buckslipFutureApprovers;

    private List<BuckslipFutureTask> buckslipFutureTasks;

    private String buckslipPastApprovers;

    // TODO: we might need this field as a column in the database and use it in Drools rules
    private Boolean restricted = false;

    public AcmContainer getContainer()
    {
        return container;
    }

    public void setContainer(AcmContainer container)
    {
        this.container = container;
    }

    public String getEcmFolderPath()
    {
        return ecmFolderPath;
    }

    public void setEcmFolderPath(String ecmFolderPath)
    {
        this.ecmFolderPath = ecmFolderPath;
    }

    public Long getTaskId()
    {
        return taskId;
    }

    public void setTaskId(Long taskId)
    {
        this.taskId = taskId;
    }

    public String getPriority()
    {
        return priority;
    }

    public void setPriority(String priority)
    {
        this.priority = priority;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public Date getDueDate()
    {
        return dueDate;
    }

    public void setDueDate(Date dueDate)
    {
        this.dueDate = dueDate;
    }

    public String getAttachedToObjectType()
    {
        return attachedToObjectType;
    }

    public void setAttachedToObjectType(String attachedToObjectType)
    {
        this.attachedToObjectType = attachedToObjectType;
    }

    public Long getAttachedToObjectId()
    {
        return attachedToObjectId;
    }

    public void setAttachedToObjectId(Long attachedToObjectId)
    {
        this.attachedToObjectId = attachedToObjectId;
    }

    @Override
    public Long getParentObjectId()
    {
        return parentObjectId;
    }

    public void setParentObjectId(Long parentObjectId)
    {
        this.parentObjectId = parentObjectId;
    }

    @Override
    public String getParentObjectType()
    {
        return parentObjectType;
    }

    public void setParentObjectType(String parentObjectType)
    {
        this.parentObjectType = parentObjectType;
    }

    public String getAssignee()
    {
        return assignee;
    }

    public void setAssignee(String assignee)
    {
        this.assignee = assignee;
    }

    public String getBusinessProcessName()
    {
        return businessProcessName;
    }

    public void setBusinessProcessName(String businessProcessName)
    {
        this.businessProcessName = businessProcessName;
    }

    public boolean isAdhocTask()
    {
        return adhocTask;
    }

    public void setAdhocTask(boolean adhocTask)
    {
        this.adhocTask = adhocTask;
    }

    public boolean isCompleted()
    {
        return completed;
    }

    public void setCompleted(boolean completed)
    {
        this.completed = completed;
    }

    public Date getTaskStartDate()
    {
        return taskStartDate;
    }

    public void setTaskStartDate(Date taskStartDate)
    {
        this.taskStartDate = taskStartDate;
    }

    public Date getTaskFinishedDate()
    {
        return taskFinishedDate;
    }

    public void setTaskFinishedDate(Date taskFinishedDate)
    {
        this.taskFinishedDate = taskFinishedDate;
    }

    public Long getTaskDurationInMillis()
    {
        return taskDurationInMillis;
    }

    public void setTaskDurationInMillis(Long taskDurationInMillis)
    {
        this.taskDurationInMillis = taskDurationInMillis;
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

    public String getPendingStatus()
    {
        return pendingStatus;
    }

    public void setPendingStatus(String pendingStatus)
    {
        this.pendingStatus = pendingStatus;
    }

    public Integer getPercentComplete()
    {
        return percentComplete != null && percentComplete > 0 ? percentComplete : 0;
    }

    public void setPercentComplete(Integer percentComplete)
    {

        this.percentComplete = percentComplete != null && percentComplete > 0 ? percentComplete : 0;
    }

    public String getDetails()
    {
        return details;
    }

    public void setDetails(String details)
    {
        this.details = details;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getAttachedToObjectName()
    {
        return attachedToObjectName;
    }

    public void setAttachedToObjectName(String attachedToObjectName)
    {
        this.attachedToObjectName = attachedToObjectName;
    }

    @Override
    @JsonIgnore
    public String getObjectType()
    {
        return "TASK";
    }

    @Override
    @JsonIgnore
    public Long getId()
    {
        return taskId;
    }

    public String getOwner()
    {
        return owner;
    }

    public void setOwner(String owner)
    {
        this.owner = owner;
    }

    public Date getCreateDate()
    {
        return createDate;
    }

    public void setCreateDate(Date createDate)
    {
        this.createDate = createDate;
    }

    public String getWorkflowRequestType()
    {
        return workflowRequestType;
    }

    public void setWorkflowRequestType(String workflowRequestType)
    {
        this.workflowRequestType = workflowRequestType;
    }

    public Long getWorkflowRequestId()
    {
        return workflowRequestId;
    }

    public void setWorkflowRequestId(Long workflowRequestId)
    {
        this.workflowRequestId = workflowRequestId;
    }

    public String getReviewDocumentPdfRenditionId()
    {
        return reviewDocumentPdfRenditionId;
    }

    public void setReviewDocumentPdfRenditionId(String reviewDocumentPdfRenditionId)
    {
        this.reviewDocumentPdfRenditionId = reviewDocumentPdfRenditionId;
    }

    public Long getReviewDocumentFormXmlId()
    {
        return reviewDocumentFormXmlId;
    }

    public void setReviewDocumentFormXmlId(Long reviewDocumentFormXmlId)
    {
        this.reviewDocumentFormXmlId = reviewDocumentFormXmlId;
    }

    public EcmFile getDocumentUnderReview()
    {
        return documentUnderReview;
    }

    public void setDocumentUnderReview(EcmFile documentUnderReview)
    {
        this.documentUnderReview = documentUnderReview;
    }

    public List<EcmFile> getDocumentsToReview()
    {
        return documentsToReview;
    }

    public void setDocumentsToReview(List<EcmFile> documentsToReview)
    {
        this.documentsToReview = documentsToReview;
    }

    public List<ObjectAssociation> getChildObjects()
    {
        return childObjects;
    }

    public void setChildObjects(List<ObjectAssociation> childObjects)
    {
        this.childObjects = childObjects;
    }

    public Long getBusinessProcessId()
    {
        return businessProcessId;
    }

    public void setBusinessProcessId(Long businessProcessId)
    {
        this.businessProcessId = businessProcessId;
    }

    public String getOutcomeName()
    {
        return outcomeName;
    }

    public void setOutcomeName(String outcomeName)
    {
        this.outcomeName = outcomeName;
    }

    public List<TaskOutcome> getAvailableOutcomes()
    {
        return availableOutcomes;
    }

    public void setAvailableOutcomes(List<TaskOutcome> availableOutcomes)
    {
        this.availableOutcomes = availableOutcomes;
    }

    public String getReworkInstructions()
    {
        return reworkInstructions;
    }

    public void setReworkInstructions(String reworkInstructions)
    {
        this.reworkInstructions = reworkInstructions;
    }

    public TaskOutcome getTaskOutcome()
    {
        return taskOutcome;
    }

    public void setTaskOutcome(TaskOutcome taskOutcome)
    {
        this.taskOutcome = taskOutcome;
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

    public String getParentObjectTitle()
    {
        return parentObjectTitle;
    }

    public void setParentObjectTitle(String parentObjectTitle)
    {
        this.parentObjectTitle = parentObjectTitle;
    }

    public String getParentObjectName()
    {
        return parentObjectName;
    }

    public void setParentObjectName(String parentObjectName)
    {
        this.parentObjectName = parentObjectName;
    }

    public String getNextAssignee()
    {
        return nextAssignee;
    }

    public void setNextAssignee(String nextAssignee)
    {
        this.nextAssignee = nextAssignee;
    }

    public List<String> getCandidateGroups()
    {
        return candidateGroups;
    }

    public void setCandidateGroups(List<String> candidateGroups)
    {
        this.candidateGroups = candidateGroups;
    }

    @Override
    public String toString()
    {
        return "AcmTask{" + "taskId=" + taskId + ", priority='" + priority + '\'' + ", title='" + title + '\'' + ", dueDate=" + dueDate
                + ", attachedToObjectType='" + attachedToObjectType + '\'' + ", attachedToObjectName='" + attachedToObjectName + '\''
                + ", attachedToObjectId=" + attachedToObjectId + ", assignee='" + assignee + '\'' + ", owner='" + owner + '\''
                + ", businessProcessName='" + businessProcessName + '\'' + ", businessProcessId=" + businessProcessId + ", adhocTask="
                + adhocTask + ", completed=" + completed + ", status='" + status + '\'' + ", percentComplete=" + percentComplete
                + ", details='" + details + '\'' + ", type='" + type + '\'' + ", createDate=" + createDate + ", taskStartDate=" + taskStartDate + ", taskFinishedDate="
                + taskFinishedDate + ", taskDurationInMillis=" + taskDurationInMillis + ", workflowRequestType='" + workflowRequestType
                + '\'' + ", workflowRequestId=" + workflowRequestId + ", reviewDocumentPdfRenditionId=" + reviewDocumentPdfRenditionId
                + ", reviewDocumentFormXmlId=" + reviewDocumentFormXmlId + ", documentUnderReview=" + documentUnderReview
                + ", childObjects=" + childObjects + ", reworkInstructions='" + reworkInstructions + '\'' + ", outcomeName='" + outcomeName
                + '\'' + ", availableOutcomes=" + availableOutcomes + ", taskOutcome=" + taskOutcome + ", participants=" + participants
                + ", container=" + container + ", ecmFolderPath='" + ecmFolderPath + '\'' + ", parentObjectId=" + parentObjectId
                + ", parentObjectType='" + parentObjectType + '\'' + ", parentObjectTitle='" + parentObjectTitle + '\''
                + ", candidateGroups=" + candidateGroups + ", nextAssignee='" + nextAssignee + '\'' + ", legacySystemId='" + legacySystemId
                + ", documentsToReview=" + documentsToReview + '\'' + '}';
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

    public boolean isBuckslipTask()
    {
        return buckslipTask;
    }

    public void setBuckslipTask(boolean buckslipTask)
    {
        this.buckslipTask = buckslipTask;
    }

    public List<AcmUser> getBuckslipFutureApprovers()
    {
        return buckslipFutureApprovers;
    }

    public void setBuckslipFutureApprovers(List<AcmUser> buckslipFutureApprovers)
    {
        this.buckslipFutureApprovers = buckslipFutureApprovers;
    }

    public String getBuckslipPastApprovers()
    {
        return buckslipPastApprovers;
    }

    public void setBuckslipPastApprovers(String buckslipPastApprovers)
    {
        this.buckslipPastApprovers = buckslipPastApprovers;
    }

    @Override
    @JsonIgnore
    public Set<AcmNotificationReceiver> getReceivers()
    {
        if (participants != null)
        {
            receivers.addAll(participants);
        }
        return receivers;
    }

    @Override
    @JsonIgnore
    public String getNotifiableEntityTitle()
    {
        return title;
    }

    public List<ObjectAssociation> getReferences()
    {
        if (getChildObjects() != null)
        {
            return getChildObjects().stream().filter(child -> ObjectAssociationConstants.REFFERENCE_TYPE.equals(child.getAssociationType()))
                    .collect(Collectors.toList());
        }

        return new ArrayList<>();
    }

    public List<BuckslipFutureTask> getBuckslipFutureTasks()
    {
        return buckslipFutureTasks;
    }

    public void setBuckslipFutureTasks(List<BuckslipFutureTask> buckslipFutureTasks)
    {
        this.buckslipFutureTasks = buckslipFutureTasks;
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

    @JsonIgnore
    public String getTaskNotes() {
        return taskNotes;
    }

    public void setTaskNotes(String taskNotes) {
        this.taskNotes = taskNotes;
    }
}
