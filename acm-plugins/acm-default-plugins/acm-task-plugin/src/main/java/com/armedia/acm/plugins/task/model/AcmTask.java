package com.armedia.acm.plugins.task.model;

import com.armedia.acm.core.AcmNotifiableEntity;
import com.armedia.acm.core.AcmNotificationReceiver;
import com.armedia.acm.core.AcmParentObjectInfo;
import com.armedia.acm.data.AcmLegacySystemEntity;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociation;
import com.armedia.acm.services.participants.model.AcmAssignedObject;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.search.model.SearchConstants;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AcmTask implements AcmAssignedObject, Serializable, AcmLegacySystemEntity, AcmParentObjectInfo, AcmNotifiableEntity
{
    private static final long serialVersionUID = 8087833770464474147L;

    private Long taskId;
    private String priority;
    private String title;

    @JsonFormat(pattern = SearchConstants.ISO_DATE_FORMAT)
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
    private Integer percentComplete;
    private String details;

    @JsonFormat(pattern = SearchConstants.ISO_DATE_FORMAT)
    private Date createDate;

    @JsonFormat(pattern = SearchConstants.ISO_DATE_FORMAT)
    private Date taskStartDate;

    @JsonFormat(pattern = SearchConstants.ISO_DATE_FORMAT)
    private Date taskFinishedDate;
    private Long taskDurationInMillis;
    private String workflowRequestType;
    private Long workflowRequestId;
    private Long reviewDocumentPdfRenditionId;
    private Long reviewDocumentFormXmlId;
    private EcmFile documentUnderReview;
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

    public void setParentObjectId(Long parentObjectId)
    {
        this.parentObjectId = parentObjectId;
    }

    @Override
    public Long getParentObjectId()
    {
        return parentObjectId;
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

    public void setStatus(String status)
    {
        this.status = status;
    }

    public Integer getPercentComplete()
    {
        return percentComplete;
    }

    public void setPercentComplete(Integer percentComplete)
    {
        this.percentComplete = percentComplete;
    }

    public String getDetails()
    {
        return details;
    }

    public void setDetails(String details)
    {
        this.details = details;
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

    public Long getReviewDocumentPdfRenditionId()
    {
        return reviewDocumentPdfRenditionId;
    }

    public void setReviewDocumentPdfRenditionId(Long reviewDocumentPdfRenditionId)
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
                + ", details='" + details + '\'' + ", createDate=" + createDate + ", taskStartDate=" + taskStartDate + ", taskFinishedDate="
                + taskFinishedDate + ", taskDurationInMillis=" + taskDurationInMillis + ", workflowRequestType='" + workflowRequestType
                + '\'' + ", workflowRequestId=" + workflowRequestId + ", reviewDocumentPdfRenditionId=" + reviewDocumentPdfRenditionId
                + ", reviewDocumentFormXmlId=" + reviewDocumentFormXmlId + ", documentUnderReview=" + documentUnderReview
                + ", childObjects=" + childObjects + ", reworkInstructions='" + reworkInstructions + '\'' + ", outcomeName='" + outcomeName
                + '\'' + ", availableOutcomes=" + availableOutcomes + ", taskOutcome=" + taskOutcome + ", participants=" + participants
                + ", container=" + container + ", ecmFolderPath='" + ecmFolderPath + '\'' + ", parentObjectId=" + parentObjectId
                + ", parentObjectType='" + parentObjectType + '\'' + ", parentObjectTitle='" + parentObjectTitle + '\''
                + ", candidateGroups=" + candidateGroups + ", nextAssignee='" + nextAssignee + '\'' + ", legacySystemId='" + legacySystemId
                + '\'' + '}';
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
}
