package com.armedia.acm.plugins.alfrescorma.model;

/*-
 * #%L
 * ACM Extra Plugin: Alfresco RMA Integration
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

import java.math.BigDecimal;

public class AlfrescoRmaConfig
{
    @JsonProperty("alfresco_rma_module_version")
    @Value("${alfresco_rma_module_version}")
    private BigDecimal moduleVersion;

    @JsonProperty("alfresco_rma_integration_enabled")
    @Value("${alfresco_rma_integration_enabled}")
    private Boolean integrationEnabled;

    @JsonProperty("alfresco_rma_declare_records_on_case_close")
    @Value("${alfresco_rma_declare_records_on_case_close}")
    private Boolean declareRecordsOnCaseClose;

    @JsonProperty("alfresco_rma_declare_records_on_complaint_close")
    @Value("${alfresco_rma_declare_records_on_complaint_close}")
    private Boolean declareRecordsOnComplaintClose;

    @JsonProperty("alfresco_rma_create_record_folder_on_complaint_create")
    @Value("${alfresco_rma_create_record_folder_on_complaint_create}")
    private Boolean createRecordFolderOnComplaintCreate;

    @JsonProperty("alfresco_rma_declare_record_folder_on_file_upload")
    @Value("${alfresco_rma_declare_record_folder_on_file_upload}")
    private Boolean declareRecordFolderOnFileUpload;

    @JsonProperty("alfresco_rma_declare_record_folder_on_file_declare")
    @Value("${alfresco_rma_declare_record_folder_on_file_declare}")
    private Boolean declareRecordFolderOnFileDeclare;

    @JsonProperty("alfresco_rma_declare_record_folder_on_folder_declare")
    @Value("${alfresco_rma_declare_record_folder_on_folder_declare}")
    private Boolean declareRecordFolderOnFolderDeclare;

    @JsonProperty("alfresco_rma_declare_file_record_on_declare_request")
    @Value("${alfresco_rma_declare_file_record_on_declare_request}")
    private Boolean declareFileRecordOnDeclareRequest;

    @JsonProperty("alfresco_rma_declare_folder_record_on_declare_request")
    @Value("${alfresco_rma_declare_folder_record_on_declare_request}")
    private Boolean declareFolderRecordOnDeclareRequest;

    @JsonProperty("rma_default_originator_org")
    @Value("#{'${rma_default_originator_org}' ?: 'Armedia LLC'}")
    private String defaultOriginatorOrg;

    @JsonProperty("rma_root_folder")
    @Value("${rma_root_folder}")
    private String rootFolder;

    @JsonProperty("rma_categoryFolder_COMPLAINT")
    @Value("${rma_categoryFolder_COMPLAINT}")
    private String categoryFolderComplaint;

    @JsonProperty("rma_categoryFolder_CASE_FILE")
    @Value("${rma_categoryFolder_CASE_FILE}")
    private String categoryFolderCaseFile;

    @JsonProperty("rma_categoryFolder_CONSULTATION")
    @Value("${rma_categoryFolder_CONSULTATION}")
    private String categoryFolderConsultation;

    @JsonProperty("rma_categoryFolder_TASK")
    @Value("${rma_categoryFolder_TASK}")
    private String categoryFolderTask;

    @JsonProperty("alfresco_rma_case_closed_statuses")
    @Value("${alfresco_rma_case_closed_statuses}")
    private String closedStatuses;

    public String getCategoryFolderForObject(String objectType)
    {
        switch (objectType)
        {
        case "COMPLAINT":
            return categoryFolderComplaint;
        case "CASE_FILE":
            return categoryFolderCaseFile;
        case "TASK":
            return categoryFolderTask;
        case "CONSULTATION":
            return categoryFolderConsultation;
        default:
            return "";
        }
    }

    public BigDecimal getModuleVersion()
    {
        return moduleVersion;
    }

    public void setModuleVersion(BigDecimal moduleVersion)
    {
        this.moduleVersion = moduleVersion;
    }

    public Boolean getIntegrationEnabled()
    {
        return integrationEnabled;
    }

    public void setIntegrationEnabled(Boolean integrationEnabled)
    {
        this.integrationEnabled = integrationEnabled;
    }

    public Boolean getDeclareRecordsOnCaseClose()
    {
        return declareRecordsOnCaseClose;
    }

    public void setDeclareRecordsOnCaseClose(Boolean declareRecordsOnCaseClose)
    {
        this.declareRecordsOnCaseClose = declareRecordsOnCaseClose;
    }

    public Boolean getDeclareRecordsOnComplaintClose()
    {
        return declareRecordsOnComplaintClose;
    }

    public void setDeclareRecordsOnComplaintClose(Boolean declareRecordsOnComplaintClose)
    {
        this.declareRecordsOnComplaintClose = declareRecordsOnComplaintClose;
    }

    public Boolean getCreateRecordFolderOnComplaintCreate()
    {
        return createRecordFolderOnComplaintCreate;
    }

    public void setCreateRecordFolderOnComplaintCreate(Boolean createRecordFolderOnComplaintCreate)
    {
        this.createRecordFolderOnComplaintCreate = createRecordFolderOnComplaintCreate;
    }

    public Boolean getDeclareRecordFolderOnFileUpload()
    {
        return declareRecordFolderOnFileUpload;
    }

    public void setDeclareRecordFolderOnFileUpload(Boolean declareRecordFolderOnFileUpload)
    {
        this.declareRecordFolderOnFileUpload = declareRecordFolderOnFileUpload;
    }

    public Boolean getDeclareRecordFolderOnFileDeclare()
    {
        return declareRecordFolderOnFileDeclare;
    }

    public void setDeclareRecordFolderOnFileDeclare(Boolean declareRecordFolderOnFileDeclare)
    {
        this.declareRecordFolderOnFileDeclare = declareRecordFolderOnFileDeclare;
    }

    public Boolean getDeclareRecordFolderOnFolderDeclare()
    {
        return declareRecordFolderOnFolderDeclare;
    }

    public void setDeclareRecordFolderOnFolderDeclare(Boolean declareRecordFolderOnFolderDeclare)
    {
        this.declareRecordFolderOnFolderDeclare = declareRecordFolderOnFolderDeclare;
    }

    public String getDefaultOriginatorOrg()
    {
        return defaultOriginatorOrg;
    }

    public void setDefaultOriginatorOrg(String defaultOriginatorOrg)
    {
        this.defaultOriginatorOrg = defaultOriginatorOrg;
    }

    public String getRootFolder()
    {
        return rootFolder;
    }

    public void setRootFolder(String rootFolder)
    {
        this.rootFolder = rootFolder;
    }

    public String getCategoryFolderComplaint()
    {
        return categoryFolderComplaint;
    }

    public void setCategoryFolderComplaint(String categoryFolderComplaint)
    {
        this.categoryFolderComplaint = categoryFolderComplaint;
    }

    public String getCategoryFolderCaseFile()
    {
        return categoryFolderCaseFile;
    }

    public void setCategoryFolderCaseFile(String categoryFolderCaseFile)
    {
        this.categoryFolderCaseFile = categoryFolderCaseFile;
    }

    public String getCategoryFolderConsultation()
    {
        return categoryFolderConsultation;
    }

    public void setCategoryFolderConsultation(String categoryFolderConsultation)
    {
        this.categoryFolderConsultation = categoryFolderConsultation;
    }

    public String getCategoryFolderTask()
    {
        return categoryFolderTask;
    }

    public void setCategoryFolderTask(String categoryFolderTask)
    {
        this.categoryFolderTask = categoryFolderTask;
    }

    public String getClosedStatuses()
    {
        return closedStatuses;
    }

    public void setClosedStatuses(String closedStatuses)
    {
        this.closedStatuses = closedStatuses;
    }

    public Boolean getDeclareFileRecordOnDeclareRequest()
    {
        return declareFileRecordOnDeclareRequest;
    }

    public void setDeclareFileRecordOnDeclareRequest(Boolean declareFileRecordOnDeclareRequest)
    {
        this.declareFileRecordOnDeclareRequest = declareFileRecordOnDeclareRequest;
    }

    public Boolean getDeclareFolderRecordOnDeclareRequest()
    {
        return declareFolderRecordOnDeclareRequest;
    }

    public void setDeclareFolderRecordOnDeclareRequest(Boolean declareFolderRecordOnDeclareRequest)
    {
        this.declareFolderRecordOnDeclareRequest = declareFolderRecordOnDeclareRequest;
    }
}
