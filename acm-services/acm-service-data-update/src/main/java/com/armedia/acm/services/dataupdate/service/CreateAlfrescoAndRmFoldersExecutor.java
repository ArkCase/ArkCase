package com.armedia.acm.services.dataupdate.service;

/*-
 * #%L
 * ACM Service: Data Update Service
 * %%
 * Copyright (C) 2014 - 2021 ArkCase LLC
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


import com.armedia.acm.camelcontext.arkcase.cmis.ArkCaseCMISConstants;
import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.plugins.alfrescorma.exception.AlfrescoServiceException;
import com.armedia.acm.plugins.alfrescorma.service.AlfrescoRecordsService;
import com.armedia.acm.plugins.ecm.model.EcmFileConfig;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CreateAlfrescoAndRmFoldersExecutor implements AcmDataUpdateExecutor
{

    public static final String CASE_FILES_FOLDER_NAME = "Case Files";
    public static final String COMPLAINTS_FOLDER_NAME = "Complaints";
    public static final String DOCUMENT_REPOSITORIES_FOLDER_NAME = "Document Repositories";
    public static final String TASKS_FOLDER_NAME = "Tasks";
    public static final String CONSULTATIONS_FOLDER_NAME = "Consultations";
    public static final String EXPENSES_FOLDER_NAME = "Expenses";
    public static final String PEOPLE_FOLDER_NAME = "People";
    public static final String RECYCLE_BIN_FOLDER_NAME = "Recycle Bin";
    public static final String TIMESHEETS_FOLDER_NAME = "Timesheets";
    public static final String USER_PROFILE_FOLDER_NAME = "User Profile";
    public static final String BUSINESS_PROCESSES_FOLDER_NAME = "Business Processes";

    private EcmFileService ecmFileService;
    private EcmFileConfig ecmFileConfig;
    private AlfrescoRecordsService alfrescoRecordsService;

    private final Logger log = LogManager.getLogger(getClass());

    @Override
    public String getUpdateId()
    {
        return "create-alfresco-and-rm-folders";
    }

    @Override
    public void execute()
    {
        try
        {
            log.info("Creating Alfresco folders");

            String defaultBasePath = getEcmFileConfig().getDefaultBasePath();

            String caseFilesFolderName = defaultBasePath + "/" + CASE_FILES_FOLDER_NAME;
            getEcmFileService().createFolder(caseFilesFolderName, ArkCaseCMISConstants.DEFAULT_CMIS_REPOSITORY_ID);

            String complaintsFolderName = defaultBasePath + "/" + COMPLAINTS_FOLDER_NAME;
            getEcmFileService().createFolder(complaintsFolderName, ArkCaseCMISConstants.DEFAULT_CMIS_REPOSITORY_ID);

            String documentRepositoriesFolderName = defaultBasePath + "/" + DOCUMENT_REPOSITORIES_FOLDER_NAME;
            getEcmFileService().createFolder(documentRepositoriesFolderName, ArkCaseCMISConstants.DEFAULT_CMIS_REPOSITORY_ID);

            String expensesFolderName = defaultBasePath + "/" + EXPENSES_FOLDER_NAME;
            getEcmFileService().createFolder(expensesFolderName, ArkCaseCMISConstants.DEFAULT_CMIS_REPOSITORY_ID);

            String peopleFolderName = defaultBasePath + "/" + PEOPLE_FOLDER_NAME;
            getEcmFileService().createFolder(peopleFolderName, ArkCaseCMISConstants.DEFAULT_CMIS_REPOSITORY_ID);

            String recycleBinFolderName = defaultBasePath + "/" + RECYCLE_BIN_FOLDER_NAME;
            getEcmFileService().createFolder(recycleBinFolderName, ArkCaseCMISConstants.DEFAULT_CMIS_REPOSITORY_ID);

            String tasksFolderName = defaultBasePath + "/" + TASKS_FOLDER_NAME;
            getEcmFileService().createFolder(tasksFolderName, ArkCaseCMISConstants.DEFAULT_CMIS_REPOSITORY_ID);

            String timesheetsFolderName = defaultBasePath + "/" + TIMESHEETS_FOLDER_NAME;
            getEcmFileService().createFolder(timesheetsFolderName, ArkCaseCMISConstants.DEFAULT_CMIS_REPOSITORY_ID);

            String userProfileFolderName = defaultBasePath + "/" + USER_PROFILE_FOLDER_NAME;
            getEcmFileService().createFolder(userProfileFolderName, ArkCaseCMISConstants.DEFAULT_CMIS_REPOSITORY_ID);

            String businessProcessesFolderName = defaultBasePath + "/" + BUSINESS_PROCESSES_FOLDER_NAME;
            getEcmFileService().createFolder(businessProcessesFolderName, ArkCaseCMISConstants.DEFAULT_CMIS_REPOSITORY_ID);

            String consultationsFolderName = defaultBasePath + "/" + CONSULTATIONS_FOLDER_NAME;
            getEcmFileService().createFolder(consultationsFolderName, ArkCaseCMISConstants.DEFAULT_CMIS_REPOSITORY_ID);

            log.info("Creating Alfresco folders successfully");

            log.info("Creating record categories");

            getAlfrescoRecordsService().createNewRecordCategoryInBaseFolder(CASE_FILES_FOLDER_NAME);
            getAlfrescoRecordsService().createNewRecordCategoryInBaseFolder(COMPLAINTS_FOLDER_NAME);
            getAlfrescoRecordsService().createNewRecordCategoryInBaseFolder(DOCUMENT_REPOSITORIES_FOLDER_NAME);
            getAlfrescoRecordsService().createNewRecordCategoryInBaseFolder(TASKS_FOLDER_NAME);
            getAlfrescoRecordsService().createNewRecordCategoryInBaseFolder(CONSULTATIONS_FOLDER_NAME);

            log.info("Creating record categories successfully");
        }
        catch (AcmCreateObjectFailedException | AlfrescoServiceException e)
        {
            log.error("Error on creating Alfresco structure and RM structure");
        }
    }

    public EcmFileService getEcmFileService()
    {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
    }

    public EcmFileConfig getEcmFileConfig()
    {
        return ecmFileConfig;
    }

    public void setEcmFileConfig(EcmFileConfig ecmFileConfig)
    {
        this.ecmFileConfig = ecmFileConfig;
    }

    public AlfrescoRecordsService getAlfrescoRecordsService()
    {
        return alfrescoRecordsService;
    }

    public void setAlfrescoRecordsService(AlfrescoRecordsService alfrescoRecordsService)
    {
        this.alfrescoRecordsService = alfrescoRecordsService;
    }
}
