package com.armedia.acm.pentaho.config;

/*-
 * #%L
 * ACM Default Plugin: report
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

import com.armedia.acm.configuration.annotations.MapValue;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

public class PentahoReportsConfig
{

    public static final String REPORT_CONFIG_PROP_KEY = "report.config.reports";

    @JsonProperty("report.plugin.PENTAHO_SERVER_URL")
    @Value("${report.plugin.PENTAHO_SERVER_URL}")
    private String serverUrl;

    @JsonProperty("report.plugin.PENTAHO_SERVER_PORT")
    @Value("${report.plugin.PENTAHO_SERVER_PORT}")
    private Integer serverPort;

    @JsonProperty("report.plugin.PENTAHO_SERVER_INTERNAL_URL")
    @Value("${report.plugin.PENTAHO_SERVER_INTERNAL_URL}")
    private String serverInternalUrl;

    @JsonProperty("report.plugin.PENTAHO_SERVER_INTERNAL_PORT")
    @Value("${report.plugin.PENTAHO_SERVER_INTERNAL_PORT}")
    private Integer serverInternalPort;

    @JsonProperty("report.plugin.PENTAHO_SERVER_USER")
    @Value("${report.plugin.PENTAHO_SERVER_USER}")
    private String serverUser;

    @JsonProperty("report.plugin.PENTAHO_SERVER_PASSWORD")
    @Value("${report.plugin.PENTAHO_SERVER_PASSWORD}")
    private String serverPassword;

    @JsonProperty("report.plugin.PENTAHO_REPORTS_URL")
    @Value("${report.plugin.PENTAHO_REPORTS_URL}")
    private String reportsUrl;

    @JsonProperty("report.plugin.PENTAHO_REPORT_URL_TEMPLATE")
    @Value("${report.plugin.PENTAHO_REPORT_URL_TEMPLATE}")
    private String reportUrlTemplate;

    @JsonProperty("report.plugin.PENTAHO_VIEW_REPORT_URL_PRPTI_TEMPLATE")
    @Value("${report.plugin.PENTAHO_VIEW_REPORT_URL_PRPTI_TEMPLATE}")
    private String viewReportUrlPrptiTemplate;

    @JsonProperty("report.plugin.PENTAHO_VIEW_DASHBOARD_REPORT_URL_TEMPLATE")
    @Value("${report.plugin.PENTAHO_VIEW_DASHBOARD_REPORT_URL_TEMPLATE}")
    private String viewDashboardReportUrlTemplate;

    @JsonProperty("report.plugin.PENTAHO_VIEW_ANALYSIS_REPORT_URL_TEMPLATE")
    @Value("${report.plugin.PENTAHO_VIEW_ANALYSIS_REPORT_URL_TEMPLATE}")
    private String viewAnalysisReportUrlTemplate;

    @JsonProperty("report.plugin.PENTAHO_SCHEDULE_INPUT_FOLDER")
    @Value("${report.plugin.PENTAHO_SCHEDULE_INPUT_FOLDER}")
    private String scheduleInputFolder;

    @JsonProperty("report.plugin.PENTAHO_SCHEDULE_OUTPUT_FOLDER")
    @Value("${report.plugin.PENTAHO_SCHEDULE_OUTPUT_FOLDER}")
    private String scheduleOutputFolder;

    @JsonProperty("report.plugin.PENTAHO_DOWNLOAD_API")
    @Value("${report.plugin.PENTAHO_DOWNLOAD_API}")
    private String downloadApi;

    @JsonProperty("report.plugin.PENTAHO_FILE_PROPERTIES_API")
    @Value("${report.plugin.PENTAHO_FILE_PROPERTIES_API}")
    private String filePropertiesApi;

    @JsonProperty("report.plugin.PENTAHO_REMOVE_FILE_API")
    @Value("${report.plugin.PENTAHO_REMOVE_FILE_API}")
    private String removeFileApi;

    @JsonProperty("report.plugin.PENTAHO_SCHEDULE_API")
    @Value("${report.plugin.PENTAHO_SCHEDULE_API}")
    private String scheduleApi;

    @JsonProperty("report.plugin.PENTAHO_RETRIEVE_SCHEDULES_API")
    @Value("${report.plugin.PENTAHO_RETRIEVE_SCHEDULES_API}")
    private String retrieveSchedulesApi;

    @JsonProperty("report.plugin.PENTAHO_DELETE_SCHEDULE_API")
    @Value("${report.plugin.PENTAHO_DELETE_SCHEDULE_API}")
    private String deleteScheduleApi;

    @JsonProperty("report.plugin.PENTAHO_REPORT_DOCUMENT_REPOSITORY_NAME")
    @Value("${report.plugin.PENTAHO_REPORT_DOCUMENT_REPOSITORY_NAME}")
    private String reportDocumentRepositoryName;

    @JsonProperty("report.plugin.REPORT_TYPES")
    @Value("${report.plugin.REPORT_TYPES}")
    private String reportTypes;

    @JsonProperty("report.plugin.REPORT_RECURRENCE")
    @Value("${report.plugin.REPORT_RECURRENCE}")
    private String reportRecurrence;

    @JsonProperty("report.plugin.REPORT_OUTPUT_TYPES")
    @Value("${report.plugin.REPORT_OUTPUT_TYPES}")
    private String reportOutputTypes;

    @JsonProperty("report.plugin.CMIS_STORE_REPORT_USER")
    @Value("${report.plugin.CMIS_STORE_REPORT_USER}")
    private String cmisStoreReportUser;

    @JsonProperty("report.plugin.PENTAHO_REPORT_URL")
    @Value("${report.plugin.PENTAHO_REPORT_URL}")
    private String reportUrl;

    private Map<String, String> reports;

    public String getServerUrl()
    {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl)
    {
        this.serverUrl = serverUrl;
    }

    public Integer getServerPort()
    {
        return serverPort;
    }

    public void setServerPort(Integer serverPort)
    {
        this.serverPort = serverPort;
    }

    public Integer getServerInternalPort()
    {
        return serverInternalPort;
    }

    public void setServerInternalPort(Integer serverInternalPort)
    {
        this.serverInternalPort = serverInternalPort;
    }

    public String getServerInternalUrl()
    {
        return serverInternalUrl;
    }

    public void setServerInternalUrl(String serverInternalUrl)
    {
        this.serverInternalUrl = serverInternalUrl;
    }

    public String getServerUser()
    {
        return serverUser;
    }

    public void setServerUser(String serverUser)
    {
        this.serverUser = serverUser;
    }

    public String getServerPassword()
    {
        return serverPassword;
    }

    public void setServerPassword(String serverPassword)
    {
        this.serverPassword = serverPassword;
    }

    public String getReportsUrl()
    {
        return reportsUrl;
    }

    public void setReportsUrl(String reportsUrl)
    {
        this.reportsUrl = reportsUrl;
    }

    public String getReportUrlTemplate()
    {
        return reportUrlTemplate;
    }

    public void setReportUrlTemplate(String reportUrlTemplate)
    {
        this.reportUrlTemplate = reportUrlTemplate;
    }

    public String getViewReportUrlPrptiTemplate()
    {
        return viewReportUrlPrptiTemplate;
    }

    public void setViewReportUrlPrptiTemplate(String viewReportUrlPrptiTemplate)
    {
        this.viewReportUrlPrptiTemplate = viewReportUrlPrptiTemplate;
    }

    public String getViewDashboardReportUrlTemplate()
    {
        return viewDashboardReportUrlTemplate;
    }

    public void setViewDashboardReportUrlTemplate(String viewDashboardReportUrlTemplate)
    {
        this.viewDashboardReportUrlTemplate = viewDashboardReportUrlTemplate;
    }

    public String getViewAnalysisReportUrlTemplate()
    {
        return viewAnalysisReportUrlTemplate;
    }

    public void setViewAnalysisReportUrlTemplate(String viewAnalysisReportUrlTemplate)
    {
        this.viewAnalysisReportUrlTemplate = viewAnalysisReportUrlTemplate;
    }

    public String getScheduleInputFolder()
    {
        return scheduleInputFolder;
    }

    public void setScheduleInputFolder(String scheduleInputFolder)
    {
        this.scheduleInputFolder = scheduleInputFolder;
    }

    public String getScheduleOutputFolder()
    {
        return scheduleOutputFolder;
    }

    public void setScheduleOutputFolder(String scheduleOutputFolder)
    {
        this.scheduleOutputFolder = scheduleOutputFolder;
    }

    public String getDownloadApi()
    {
        return downloadApi;
    }

    public void setDownloadApi(String downloadApi)
    {
        this.downloadApi = downloadApi;
    }

    public String getFilePropertiesApi()
    {
        return filePropertiesApi;
    }

    public void setFilePropertiesApi(String filePropertiesApi)
    {
        this.filePropertiesApi = filePropertiesApi;
    }

    public String getRemoveFileApi()
    {
        return removeFileApi;
    }

    public void setRemoveFileApi(String removeFileApi)
    {
        this.removeFileApi = removeFileApi;
    }

    public String getScheduleApi()
    {
        return scheduleApi;
    }

    public void setScheduleApi(String scheduleApi)
    {
        this.scheduleApi = scheduleApi;
    }

    public String getRetrieveSchedulesApi()
    {
        return retrieveSchedulesApi;
    }

    public void setRetrieveSchedulesApi(String retrieveSchedulesApi)
    {
        this.retrieveSchedulesApi = retrieveSchedulesApi;
    }

    public String getDeleteScheduleApi()
    {
        return deleteScheduleApi;
    }

    public void setDeleteScheduleApi(String deleteScheduleApi)
    {
        this.deleteScheduleApi = deleteScheduleApi;
    }

    public String getReportDocumentRepositoryName()
    {
        return reportDocumentRepositoryName;
    }

    public void setReportDocumentRepositoryName(String reportDocumentRepositoryName)
    {
        this.reportDocumentRepositoryName = reportDocumentRepositoryName;
    }

    public String getReportTypes()
    {
        return reportTypes;
    }

    public void setReportTypes(String reportTypes)
    {
        this.reportTypes = reportTypes;
    }

    public String getReportRecurrence()
    {
        return reportRecurrence;
    }

    public void setReportRecurrence(String reportRecurrence)
    {
        this.reportRecurrence = reportRecurrence;
    }

    public String getReportOutputTypes()
    {
        return reportOutputTypes;
    }

    public void setReportOutputTypes(String reportOutputTypes)
    {
        this.reportOutputTypes = reportOutputTypes;
    }

    public String getCmisStoreReportUser()
    {
        return cmisStoreReportUser;
    }

    public void setCmisStoreReportUser(String cmisStoreReportUser)
    {
        this.cmisStoreReportUser = cmisStoreReportUser;
    }

    @MapValue(value = "report.config.reports")
    public Map<String, String> getReports()
    {
        return reports;
    }

    public void setReports(Map<String, String> reports)
    {
        this.reports = reports;
    }

    public String getReportUrl()
    {
        return reportUrl;
    }

    public void setReportUrl(String reportUrl)
    {
        this.reportUrl = reportUrl;
    }
}
