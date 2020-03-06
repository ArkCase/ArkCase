package com.armedia.acm.audit.model;

/*-
 * #%L
 * ACM Service: Audit Library
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.beans.factory.annotation.Value;

import java.util.Arrays;
import java.util.List;

public class AuditConfig
{
    @JsonProperty("audit.plugin.database.enabled")
    @Value("${audit.plugin.database.enabled}")
    private Boolean databaseEnabled;

    @JsonProperty("audit.plugin.systemlog.enabled")
    @Value("${audit.plugin.systemlog.enabled}")
    private Boolean systemLogEnabled;

    @JsonProperty("audit.plugin.systemlog.windows.eventlog.eventId")
    @Value("${audit.plugin.systemlog.windows.eventlog.eventId}")
    private Integer systemLogWindowsEventLogEventId;

    @JsonProperty("audit.plugin.systemlog.syslog.protocol")
    @Value("${audit.plugin.systemlog.syslog.protocol}")
    private String systemLogSysLogProtocol;

    @JsonProperty("audit.plugin.systemlog.syslog.host")
    @Value("${audit.plugin.systemlog.syslog.host}")
    private String systemLogSysLogHost;

    @JsonProperty("audit.plugin.systemlog.syslog.port")
    @Value("${audit.plugin.systemlog.syslog.port}")
    private Integer systemLogSysLogPort;

    @JsonProperty("audit.plugin.batch.run")
    @Value("${audit.plugin.batch.run}")
    private Boolean batchRun;

    @JsonProperty("audit.plugin.purge.days")
    @Value("${audit.plugin.purge.days}")
    private Integer purgeDays;

    @JsonProperty("audit.plugin.purge.days")
    @Value("${audit.plugin.quartz.purge.days}")
    private Integer quartzAuditPurgeDays;

    @JsonProperty("audit.plugin.quartz.purge.number")
    @Value("${audit.plugin.quartz.purge.number}")
    private Integer quartzAuditPurgeNumber;

    @JsonProperty("audit.plugin.requests.logging.enabled")
    @Value("${audit.plugin.requests.logging.enabled}")
    private Boolean requestsLoggingEnabled;

    @JsonProperty("audit.plugin.requests.logging.headers.enabled")
    @Value("${audit.plugin.requests.logging.headers.enabled}")
    private Boolean requestsLoggingHeadersEnabled;

    @JsonProperty("audit.plugin.requests.logging.cookies.enabled")
    @Value("${audit.plugin.requests.logging.cookies.enabled}")
    private Boolean requestsLoggingCookiesEnabled;

    @JsonProperty("audit.plugin.requests.logging.body.enabled")
    @Value("${audit.plugin.requests.logging.body.enabled}")
    private Boolean requestsLoggingBodyEnabled;

    @JsonProperty("audit.plugin.database.changes.logging.enabled")
    @Value("${audit.plugin.database.changes.logging.enabled}")
    private Boolean databaseChangesLoggingEnabled;

    @JsonProperty("audit.plugin.database.changes.logging.fieldvalues.enabled")
    @Value("${audit.plugin.database.changes.logging.fieldvalues.enabled}")
    private Boolean databaseChangesLoggingFieldValuesEnabled;

    @JsonProperty("audit.plugin.activiti.logging.enabled")
    @Value("${audit.plugin.activiti.logging.enabled}")
    private Boolean activitiLoggingEnabled;

    @JsonProperty("audit.plugin.activiti.logging.entity.events.enabled")
    @Value("${audit.plugin.activiti.logging.entity.events.enabled}")
    private Boolean activitiLoggingEntityEventsEnabled;

    @JsonProperty("audit.plugin.activiti.logging.entity.events.object.enabled")
    @Value("${audit.plugin.activiti.logging.entity.events.object.enabled}")
    private boolean activitiLoggingEntityEventsObjectEnabled;

    @JsonProperty("audit.plugin.contentTypesToLog")
    @Value("${audit.plugin.contentTypesToLog}")
    private String contentTypesToLogString;

    @JsonProperty("audit.plugin.AUDIT_REPORT")
    @Value("${audit.plugin.AUDIT_REPORT}")
    private String auditReportUrl;

    @JsonProperty("audit.plugin.AUDIT_CRITERIA")
    @Value("${audit.plugin.AUDIT_CRITERIA}")
    private String auditCriteria;

    @JsonProperty("CASE_FILE.history.event.types")
    @Value("${CASE_FILE.history.event.types}")
    private String caseFileHistoryEventsArrayString;

    @JsonProperty("COMPLAINT.history.event.types")
    @Value("${COMPLAINT.history.event.types}")
    private String complaintHistoryEventsArrayString;

    @JsonProperty("TASK.history.event.types")
    @Value("${TASK.history.event.types}")
    private String taskHistoryEventsArrayString;

    @JsonProperty("DOC_REPO.history.event.types")
    @Value("${DOC_REPO.history.event.types}")
    private String docRepoHistoryEventsArrayString;

    @JsonProperty("PERSON.history.event.types")
    @Value("${PERSON.history.event.types}")
    private String personHistoryEventsArrayString;

    @JsonProperty("ORGANIZATION.history.event.types")
    @Value("${ORGANIZATION.history.event.types}")
    private String organizationaHistroyEventsArrayString;

    public Boolean getDatabaseEnabled()
    {
        return databaseEnabled;
    }

    public void setDatabaseEnabled(Boolean databaseEnabled)
    {
        this.databaseEnabled = databaseEnabled;
    }

    public Boolean getSystemLogEnabled()
    {
        return systemLogEnabled;
    }

    public void setSystemLogEnabled(Boolean systemLogEnabled)
    {
        this.systemLogEnabled = systemLogEnabled;
    }

    public Integer getSystemLogWindowsEventLogEventId()
    {
        return systemLogWindowsEventLogEventId;
    }

    public void setSystemLogWindowsEventLogEventId(Integer systemLogWindowsEventLogEventId)
    {
        this.systemLogWindowsEventLogEventId = systemLogWindowsEventLogEventId;
    }

    public String getSystemLogSysLogProtocol()
    {
        return systemLogSysLogProtocol;
    }

    public void setSystemLogSysLogProtocol(String systemLogSysLogProtocol)
    {
        this.systemLogSysLogProtocol = systemLogSysLogProtocol;
    }

    public String getSystemLogSysLogHost()
    {
        return systemLogSysLogHost;
    }

    public void setSystemLogSysLogHost(String systemLogSysLogHost)
    {
        this.systemLogSysLogHost = systemLogSysLogHost;
    }

    public Integer getSystemLogSysLogPort()
    {
        return systemLogSysLogPort;
    }

    public void setSystemLogSysLogPort(Integer systemLogSysLogPort)
    {
        this.systemLogSysLogPort = systemLogSysLogPort;
    }

    public Boolean getBatchRun()
    {
        return batchRun;
    }

    public void setBatchRun(Boolean batchRun)
    {
        this.batchRun = batchRun;
    }

    public Integer getPurgeDays()
    {
        return purgeDays;
    }

    public void setPurgeDays(Integer purgeDays)
    {
        this.purgeDays = purgeDays;
    }

    public Integer getQuartzAuditPurgeNumber()
    {
        return quartzAuditPurgeNumber;
    }

    public void setQuartzAuditPurgeNumber(Integer quartzAuditPurgeNumber)
    {
        this.quartzAuditPurgeNumber = quartzAuditPurgeNumber;
    }

    public Integer getQuartzAuditPurgeDays()
    {
        return quartzAuditPurgeDays;
    }

    public void setQuartzAuditPurgeDays(Integer quartzAuditPurgeDays)
    {
        this.quartzAuditPurgeDays = quartzAuditPurgeDays;
    }

    public Boolean getRequestsLoggingEnabled()
    {
        return requestsLoggingEnabled;
    }

    public void setRequestsLoggingEnabled(Boolean requestsLoggingEnabled)
    {
        this.requestsLoggingEnabled = requestsLoggingEnabled;
    }

    public Boolean getRequestsLoggingHeadersEnabled()
    {
        return requestsLoggingHeadersEnabled;
    }

    public void setRequestsLoggingHeadersEnabled(Boolean requestsLoggingHeadersEnabled)
    {
        this.requestsLoggingHeadersEnabled = requestsLoggingHeadersEnabled;
    }

    public Boolean getRequestsLoggingCookiesEnabled()
    {
        return requestsLoggingCookiesEnabled;
    }

    public void setRequestsLoggingCookiesEnabled(Boolean requestsLoggingCookiesEnabled)
    {
        this.requestsLoggingCookiesEnabled = requestsLoggingCookiesEnabled;
    }

    public Boolean getRequestsLoggingBodyEnabled()
    {
        return requestsLoggingBodyEnabled;
    }

    public void setRequestsLoggingBodyEnabled(Boolean requestsLoggingBodyEnabled)
    {
        this.requestsLoggingBodyEnabled = requestsLoggingBodyEnabled;
    }

    public Boolean getDatabaseChangesLoggingEnabled()
    {
        return databaseChangesLoggingEnabled;
    }

    public void setDatabaseChangesLoggingEnabled(Boolean databaseChangesLoggingEnabled)
    {
        this.databaseChangesLoggingEnabled = databaseChangesLoggingEnabled;
    }

    public Boolean getDatabaseChangesLoggingFieldValuesEnabled()
    {
        return databaseChangesLoggingFieldValuesEnabled;
    }

    public void setDatabaseChangesLoggingFieldValuesEnabled(Boolean databaseChangesLoggingFieldValuesEnabled)
    {
        this.databaseChangesLoggingFieldValuesEnabled = databaseChangesLoggingFieldValuesEnabled;
    }

    public Boolean getActivitiLoggingEnabled()
    {
        return activitiLoggingEnabled;
    }

    public void setActivitiLoggingEnabled(Boolean activitiLoggingEnabled)
    {
        this.activitiLoggingEnabled = activitiLoggingEnabled;
    }

    public Boolean getActivitiLoggingEntityEventsEnabled()
    {
        return activitiLoggingEntityEventsEnabled;
    }

    public void setActivitiLoggingEntityEventsEnabled(Boolean activitiLoggingEntityEventsEnabled)
    {
        this.activitiLoggingEntityEventsEnabled = activitiLoggingEntityEventsEnabled;
    }

    public boolean isActivitiLoggingEntityEventsObjectEnabled()
    {
        return activitiLoggingEntityEventsObjectEnabled;
    }

    public void setActivitiLoggingEntityEventsObjectEnabled(boolean activitiLoggingEntityEventsObjectEnabled)
    {
        this.activitiLoggingEntityEventsObjectEnabled = activitiLoggingEntityEventsObjectEnabled;
    }

    public String getContentTypesToLogString()
    {
        return contentTypesToLogString;
    }

    public void setContentTypesToLogString(String contentTypesToLogString)
    {
        this.contentTypesToLogString = contentTypesToLogString;
    }

    public List<String> getContentTypesToLog()
    {
        return Arrays.asList(contentTypesToLogString.split(","));
    }

    public String getAuditReportUrl()
    {
        return auditReportUrl;
    }

    public void setAuditReportUrl(String auditReportUrl)
    {
        this.auditReportUrl = auditReportUrl;
    }

    public String getAuditCriteria()
    {
        return auditCriteria;
    }

    public void setAuditCriteria(String auditCriteria)
    {
        this.auditCriteria = auditCriteria;
    }

    public String getCaseFileHistoryEventsArrayString()
    {
        return caseFileHistoryEventsArrayString;
    }

    public void setCaseFileHistoryEventsArrayString(String caseFileHistoryEventsArrayString)
    {
        this.caseFileHistoryEventsArrayString = caseFileHistoryEventsArrayString;
    }

    public String getComplaintHistoryEventsArrayString()
    {
        return complaintHistoryEventsArrayString;
    }

    public void setComplaintHistoryEventsArrayString(String complaintHistoryEventsArrayString)
    {
        this.complaintHistoryEventsArrayString = complaintHistoryEventsArrayString;
    }

    public String getTaskHistoryEventsArrayString()
    {
        return taskHistoryEventsArrayString;
    }

    public void setTaskHistoryEventsArrayString(String taskHistoryEventsArrayString)
    {
        this.taskHistoryEventsArrayString = taskHistoryEventsArrayString;
    }

    public String getDocRepoHistoryEventsArrayString()
    {
        return docRepoHistoryEventsArrayString;
    }

    public void setDocRepoHistoryEventsArrayString(String docRepoHistoryEventsArrayString)
    {
        this.docRepoHistoryEventsArrayString = docRepoHistoryEventsArrayString;
    }

    public String getPersonHistoryEventsArrayString()
    {
        return personHistoryEventsArrayString;
    }

    public void setPersonHistoryEventsArrayString(String personHistoryEventsArrayString)
    {
        this.personHistoryEventsArrayString = personHistoryEventsArrayString;
    }

    public String getOrganizationaHistroyEventsArrayString()
    {
        return organizationaHistroyEventsArrayString;
    }

    public void setOrganizationaHistroyEventsArrayString(String organizationaHistroyEventsArrayString)
    {
        this.organizationaHistroyEventsArrayString = organizationaHistroyEventsArrayString;
    }

    @JsonIgnore
    public String getEventTypeByKey(String objectType)
    {
        switch (objectType)
        {
        case "COMPLAINT":
            return complaintHistoryEventsArrayString;
        case "CASE_FILE":
            return caseFileHistoryEventsArrayString;
        case "TASK":
            return taskHistoryEventsArrayString;
        case "DOC_REPO":
            return docRepoHistoryEventsArrayString;
        }
        return "";
    }
}
