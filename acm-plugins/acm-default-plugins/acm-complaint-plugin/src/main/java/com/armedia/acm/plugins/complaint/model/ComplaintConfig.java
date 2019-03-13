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

import com.armedia.acm.pluginmanager.service.AcmPluginConfigBean;
import com.armedia.acm.plugins.ecm.service.SupportsFileTypes;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.beans.factory.annotation.Value;

import java.util.Set;

public class ComplaintConfig implements SupportsFileTypes, AcmPluginConfigBean
{
    @JsonProperty("complaint.plugin.email.folder.relative.path")
    @Value("${complaint.plugin.email.folder.relative.path}")
    private String emailFolderRelativePath;

    @JsonProperty("complaint.plugin.email.regex.complaint_number")
    @Value("${complaint.plugin.email.regex.complaint_number}")
    private String emailRegexComplaintNumber;

    @JsonProperty("complaint.plugin.email.regex.object_type")
    @Value("${complaint.plugin.email.regex.object_type}")
    private String emailRegexObjectType;

    @JsonProperty("complaint.plugin.email.handler.enabled")
    @Value("${complaint.plugin.email.handler.enabled}")
    private Boolean emailHandlerEnabled;

    @JsonProperty("complaint.plugin.auto_create_calendar_folder")
    @Value("${complaint.plugin.auto_create_calendar_folder}")
    private Boolean autoCreateCalendarFolder;

    @JsonProperty("complaint.plugin.delete_calendar_folder_after_complaint_closed")
    @Value("${complaint.plugin.delete_calendar_folder_after_complaint_closed}")
    private Boolean deleteCalendarFolderAfterComplaintClosed;

    @JsonProperty("complaint.plugin.status.closed")
    @Value("${complaint.plugin.status.closed}")
    private String statusClosed;

    @JsonProperty("complaint.plugin.workflow.enabled")
    @Value("${complaint.plugin.workflow.enabled}")
    private Boolean workflowEnabled;

    @JsonProperty("complaint.plugin.workflow.trigger.eventName")
    @Value("${complaint.plugin.workflow.trigger.eventName}")
    private String workflowTriggerEventName;

    @JsonProperty("complaint.plugin.workflow.activiti.name")
    @Value("${complaint.plugin.workflow.activiti.name}")
    private String workflowActivitiName;

    @JsonProperty("complaint.plugin.workflow.activiti.inApproval.stateName")
    @Value("${complaint.plugin.workflow.activiti.inApproval.stateName}")
    private String workflowActivitiInApprovalStateName;

    @JsonProperty("complaint.plugin.workflow.activiti.approved.stateName")
    @Value("${complaint.plugin.workflow.activiti.approved.stateName}")
    private String workflowActivitiApprovedStateName;

    @JsonProperty("complaint.plugin.ephesoft.load.documents.seconds")
    @Value("${complaint.plugin.ephesoft.load.documents.seconds}")
    private Integer ephesoftLoadDocumentsSeconds;

    @JsonProperty("complaint.plugin.search.tree.filter")
    @Value("${complaint.plugin.search.tree.filter}")
    private String searchTreeFilter;

    @JsonProperty("complaint.plugin.search.tree.sort")
    @Value("${complaint.plugin.search.tree.sort}")
    private String searchTreeSort;

    @JsonProperty("complaint.plugin.search.tree.searchQuery")
    @Value("${complaint.plugin.search.tree.searchQuery}")
    private String searchTreeSearchQuery;

    @JsonProperty("complaint.plugin.fileTypes")
    @Value("${complaint.plugin.fileTypes}")
    private String supportedFileTypes;

    public String getEmailFolderRelativePath()
    {
        return emailFolderRelativePath;
    }

    public void setEmailFolderRelativePath(String emailFolderRelativePath)
    {
        this.emailFolderRelativePath = emailFolderRelativePath;
    }

    public String getEmailRegexComplaintNumber()
    {
        return emailRegexComplaintNumber;
    }

    public void setEmailRegexComplaintNumber(String emailRegexComplaintNumber)
    {
        this.emailRegexComplaintNumber = emailRegexComplaintNumber;
    }

    public String getEmailRegexObjectType()
    {
        return emailRegexObjectType;
    }

    public void setEmailRegexObjectType(String emailRegexObjectType)
    {
        this.emailRegexObjectType = emailRegexObjectType;
    }

    public Boolean getEmailHandlerEnabled()
    {
        return emailHandlerEnabled;
    }

    public void setEmailHandlerEnabled(Boolean emailHandlerEnabled)
    {
        this.emailHandlerEnabled = emailHandlerEnabled;
    }

    public Boolean getAutoCreateCalendarFolder()
    {
        return autoCreateCalendarFolder;
    }

    public void setAutoCreateCalendarFolder(Boolean autoCreateCalendarFolder)
    {
        this.autoCreateCalendarFolder = autoCreateCalendarFolder;
    }

    public Boolean getDeleteCalendarFolderAfterComplaintClosed()
    {
        return deleteCalendarFolderAfterComplaintClosed;
    }

    public void setDeleteCalendarFolderAfterComplaintClosed(Boolean deleteCalendarFolderAfterComplaintClosed)
    {
        this.deleteCalendarFolderAfterComplaintClosed = deleteCalendarFolderAfterComplaintClosed;
    }

    public String getStatusClosed()
    {
        return statusClosed;
    }

    public void setStatusClosed(String statusClosed)
    {
        this.statusClosed = statusClosed;
    }

    public Boolean getWorkflowEnabled()
    {
        return workflowEnabled;
    }

    public void setWorkflowEnabled(Boolean workflowEnabled)
    {
        this.workflowEnabled = workflowEnabled;
    }

    public String getWorkflowTriggerEventName()
    {
        return workflowTriggerEventName;
    }

    public void setWorkflowTriggerEventName(String workflowTriggerEventName)
    {
        this.workflowTriggerEventName = workflowTriggerEventName;
    }

    public String getWorkflowActivitiName()
    {
        return workflowActivitiName;
    }

    public void setWorkflowActivitiName(String workflowActivitiName)
    {
        this.workflowActivitiName = workflowActivitiName;
    }

    public String getWorkflowActivitiInApprovalStateName()
    {
        return workflowActivitiInApprovalStateName;
    }

    public void setWorkflowActivitiInApprovalStateName(String workflowActivitiInApprovalStateName)
    {
        this.workflowActivitiInApprovalStateName = workflowActivitiInApprovalStateName;
    }

    public String getWorkflowActivitiApprovedStateName()
    {
        return workflowActivitiApprovedStateName;
    }

    public void setWorkflowActivitiApprovedStateName(String workflowActivitiApprovedStateName)
    {
        this.workflowActivitiApprovedStateName = workflowActivitiApprovedStateName;
    }

    public Integer getEphesoftLoadDocumentsSeconds()
    {
        return ephesoftLoadDocumentsSeconds;
    }

    public void setEphesoftLoadDocumentsSeconds(Integer ephesoftLoadDocumentsSeconds)
    {
        this.ephesoftLoadDocumentsSeconds = ephesoftLoadDocumentsSeconds;
    }

    public void setSearchTreeFilter(String searchTreeFilter)
    {
        this.searchTreeFilter = searchTreeFilter;
    }

    public void setSearchTreeSort(String searchTreeSort)
    {
        this.searchTreeSort = searchTreeSort;
    }

    public String getSearchTreeSearchQuery()
    {
        return searchTreeSearchQuery;
    }

    public void setSearchTreeSearchQuery(String searchTreeSearchQuery)
    {
        this.searchTreeSearchQuery = searchTreeSearchQuery;
    }

    public String getSupportedFileTypes()
    {
        return supportedFileTypes;
    }

    public void setSupportedFileTypes(String supportedFileTypes)
    {
        this.supportedFileTypes = supportedFileTypes;
    }

    @Override
    public Set<String> getFileTypes()
    {
        return getFileTypes(supportedFileTypes);
    }

    @Override
    public String getSearchTreeFilter()
    {
        return searchTreeFilter;
    }

    @Override
    public String getSearchTreeQuery()
    {
        return searchTreeSearchQuery;
    }

    @Override
    public String getSearchTreeSort()
    {
        return searchTreeSort;
    }
}
