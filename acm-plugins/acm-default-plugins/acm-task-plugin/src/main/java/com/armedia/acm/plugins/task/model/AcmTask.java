package com.armedia.acm.plugins.task.model;

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
import java.util.List;

public class AcmTask implements AcmAssignedObject, Serializable
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
    private String owner;					//creator
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

    private AcmContainer container;

    /**
     * ecmFolderPath is a transient property; it is set by business rules when the task is created, and is used
     * to create the right folder.  For existing tasks, container has the folderId.
     */
    private transient String ecmFolderPath;
    private Long parentObjectId;
    private String parentObjectType;

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

    public String getAttachedToObjectName() {
        return attachedToObjectName;
    }

    public void setAttachedToObjectName(String attachedToObjectName) {
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

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
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

    @Override
    public String toString()
    {
        return "AcmTask{" +
                "taskId=" + taskId +
                ", priority='" + priority + '\'' +
                ", title='" + title + '\'' +
                ", dueDate=" + dueDate +
                ", attachedToObjectType='" + attachedToObjectType + '\'' +
                ", attachedToObjectName='" + attachedToObjectName + '\'' +
                ", attachedToObjectId=" + attachedToObjectId +
                ", assignee='" + assignee + '\'' +
                ", owner='" + owner + '\'' +
                ", businessProcessName='" + businessProcessName + '\'' +
                ", adhocTask=" + adhocTask +
                ", completed=" + completed +
                ", status='" + status + '\'' +
                ", percentComplete=" + percentComplete +
                ", details='" + details + '\'' +
                ", createDate=" + createDate +
                ", taskStartDate=" + taskStartDate +
                ", taskFinishedDate=" + taskFinishedDate +
                ", taskDurationInMillis=" + taskDurationInMillis +
                ", workflowRequestType='" + workflowRequestType + '\'' +
                ", workflowRequestId='" + workflowRequestId + '\'' +
                ", reviewDocumentPdfRenditionId=" + reviewDocumentPdfRenditionId +
                ", reviewDocumentFormXmlId=" + reviewDocumentFormXmlId +
                '}';
    }

    public void setDocumentUnderReview(EcmFile documentUnderReview)
    {
        this.documentUnderReview = documentUnderReview;
    }

    public EcmFile getDocumentUnderReview()
    {
        return documentUnderReview;
    }

    public void setChildObjects(List<ObjectAssociation> childObjects)
    {
        this.childObjects = childObjects;
    }

    public List<ObjectAssociation> getChildObjects()
    {
        return childObjects;
    }

    public Long getBusinessProcessId()
    {
        return businessProcessId;
    }

    public void setBusinessProcessId(Long businessProcessId)
    {
        this.businessProcessId = businessProcessId;
    }

    public void setOutcomeName(String outcomeName)
    {
        this.outcomeName = outcomeName;
    }

    public String getOutcomeName()
    {
        return outcomeName;
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

    public void setParentObjectId(Long parentObjectId)
    {
        this.parentObjectId = parentObjectId;
    }

    public Long getParentObjectId()
    {
        return parentObjectId;
    }

    public void setParentObjectType(String parentObjectType)
    {
        this.parentObjectType = parentObjectType;
    }

    public String getParentObjectType()
    {
        return parentObjectType;
    }
}
