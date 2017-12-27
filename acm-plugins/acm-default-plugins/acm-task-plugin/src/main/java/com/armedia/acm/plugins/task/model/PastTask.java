package com.armedia.acm.plugins.task.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PastTask implements Serializable{

    private String groupName;
    private Date approvalDate;
    private String approverId;
    private String addedBy;
    private int maxTaskDurationInDays;
    private String approverFullName;
    private String approverDecision;
    private String taskName;
    private String details;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Date getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(Date approvalDate) {
        this.approvalDate = approvalDate;
    }

    public String getApproverId() {
        return approverId;
    }

    public void setApproverId(String approverId) {
        this.approverId = approverId;
    }

    public String getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
    }

    public int getMaxTaskDurationInDays() {
        return maxTaskDurationInDays;
    }

    public void setMaxTaskDurationInDays(int maxTaskDurationInDays) {
        this.maxTaskDurationInDays = maxTaskDurationInDays;
    }

    public String getApproverFullName() {
        return approverFullName;
    }

    public void setApproverFullName(String approverFullName) {
        this.approverFullName = approverFullName;
    }

    public String getApproverDecision() {
        return approverDecision;
    }

    public void setApproverDecision(String approverDecision) {
        this.approverDecision = approverDecision;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
