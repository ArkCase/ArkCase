package com.armedia.acm.services.notification.model;

/*-
 * #%L
 * ACM Service: Notification
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
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

import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.beans.factory.annotation.Value;

public class NotificationConfig
{
    @JsonProperty("notification.purge.days")
    @Value("${notification.purge.days}")
    private Integer purgeDays;

    @JsonProperty("notification.user.batch.frequency")
    @Value("${notification.user.batch.frequency}")
    private Integer userBatchFrequency;

    @JsonProperty("notification.user.batch.run")
    @Value("${notification.user.batch.run}")
    private Boolean userBatchRun;

    @JsonProperty("notification.user.batch.size")
    @Value("${notification.user.batch.size}")
    private Integer userBatchSize;

    @JsonProperty("notification.email.template.path")
    @Value("${notification.email.template.path}")
    private String emailTemplatePath;

    @JsonProperty("notification.search.filters")
    @Value("${notification.search.filters}")
    private String searchFilters;

    @JsonProperty("notification.search.name")
    @Value("${notification.search.name}")
    private String searchName;

    @JsonProperty("notification.arkcase.url.base")
    @Value("${notification.arkcase.url.base}")
    private String baseUrl;

    @JsonProperty("notification.arkcase.port")
    @Value("${notification.arkcase.port}")
    private Integer port;

    @JsonProperty("notification.arkcase.url")
    @Value("${notification.arkcase.url}")
    private String url;

    @JsonProperty("notification.CASE_FILE.label")
    @Value("${notification.CASE_FILE.label}")
    private String caseFileLabel;

    @JsonProperty("notification.COMPLAINT.label")
    @Value("${notification.COMPLAINT.label}")
    private String complaintLabel;

    @JsonProperty("notification.FILE.label")
    @Value("${notification.FILE.label}")
    private String fileLabel;

    @JsonProperty("notification.TASK.label")
    @Value("${notification.TASK.label}")
    private String taskLabel;

    @JsonProperty("notification.PARTICIPANT.label")
    @Value("${notification.PARTICIPANT.label}")
    private String participantLabel;

    @JsonProperty("notification.NOTE.label")
    @Value("${notification.NOTE.label}")
    private String noteLabel;

    @JsonProperty("notification.PERSON-ASSOCIATION.label")
    @Value("${notification.PERSON-ASSOCIATION.label}")
    private String personAssociationLabel;

    @JsonProperty("notification.DOC_REPO.label")
    @Value("${notification.DOC_REPO.label}")
    private String docRepoLabel;

    public String getLabelForObjectType(String objectType)
    {
        switch (objectType)
        {
        case "TASK":
            return taskLabel;
        case "CASE_FILE":
            return caseFileLabel;
        case "COMPLAINT":
            return complaintLabel;
        case "FILE":
            return fileLabel;
        case "PARTICIPANT":
            return participantLabel;
        case "NOTE":
            return noteLabel;
        case "PERSON-ASSOCIATION":
            return personAssociationLabel;
        case "DOC_REPO":
            return docRepoLabel;
        default:
            return objectType;
        }
    }

    public Integer getPurgeDays()
    {
        return purgeDays;
    }

    public void setPurgeDays(Integer purgeDays)
    {
        this.purgeDays = purgeDays;
    }

    public Integer getUserBatchFrequency()
    {
        return userBatchFrequency;
    }

    public void setUserBatchFrequency(Integer userBatchFrequency)
    {
        this.userBatchFrequency = userBatchFrequency;
    }

    public Boolean getUserBatchRun()
    {
        return userBatchRun;
    }

    public void setUserBatchRun(Boolean userBatchRun)
    {
        this.userBatchRun = userBatchRun;
    }

    public Integer getUserBatchSize()
    {
        return userBatchSize;
    }

    public void setUserBatchSize(Integer userBatchSize)
    {
        this.userBatchSize = userBatchSize;
    }

    public String getEmailTemplatePath()
    {
        return emailTemplatePath;
    }

    public void setEmailTemplatePath(String emailTemplatePath)
    {
        this.emailTemplatePath = emailTemplatePath;
    }

    public String getSearchFilters()
    {
        return searchFilters;
    }

    public void setSearchFilters(String searchFilters)
    {
        this.searchFilters = searchFilters;
    }

    public String getSearchName()
    {
        return searchName;
    }

    public void setSearchName(String searchName)
    {
        this.searchName = searchName;
    }

    public String getBaseUrl()
    {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl)
    {
        this.baseUrl = baseUrl;
    }

    public Integer getPort()
    {
        return port;
    }

    public void setPort(Integer port)
    {
        this.port = port;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getCaseFileLabel()
    {
        return caseFileLabel;
    }

    public void setCaseFileLabel(String caseFileLabel)
    {
        this.caseFileLabel = caseFileLabel;
    }

    public String getComplaintLabel()
    {
        return complaintLabel;
    }

    public void setComplaintLabel(String complaintLabel)
    {
        this.complaintLabel = complaintLabel;
    }

    public String getFileLabel()
    {
        return fileLabel;
    }

    public void setFileLabel(String fileLabel)
    {
        this.fileLabel = fileLabel;
    }

    public String getTaskLabel()
    {
        return taskLabel;
    }

    public void setTaskLabel(String taskLabel)
    {
        this.taskLabel = taskLabel;
    }

    public String getParticipantLabel()
    {
        return participantLabel;
    }

    public void setParticipantLabel(String participantLabel)
    {
        this.participantLabel = participantLabel;
    }

    public String getNoteLabel()
    {
        return noteLabel;
    }

    public void setNoteLabel(String noteLabel)
    {
        this.noteLabel = noteLabel;
    }

    public String getPersonAssociationLabel()
    {
        return personAssociationLabel;
    }

    public void setPersonAssociationLabel(String personAssociationLabel)
    {
        this.personAssociationLabel = personAssociationLabel;
    }

    public String getDocRepoLabel()
    {
        return docRepoLabel;
    }

    public void setDocRepoLabel(String docRepoLabel)
    {
        this.docRepoLabel = docRepoLabel;
    }
}
