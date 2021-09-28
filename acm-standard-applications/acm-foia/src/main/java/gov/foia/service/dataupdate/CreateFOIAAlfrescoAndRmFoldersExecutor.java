package gov.foia.service.dataupdate;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
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
import com.armedia.acm.services.dataupdate.service.AcmDataUpdateExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CreateFOIAAlfrescoAndRmFoldersExecutor implements AcmDataUpdateExecutor
{

    public static final String REQUESTS_FOLDER_NAME = "Requests";

    private EcmFileService ecmFileService;
    private EcmFileConfig ecmFileConfig;
    private AlfrescoRecordsService alfrescoRecordsService;

    private final Logger log = LogManager.getLogger(getClass());

    @Override
    public String getUpdateId()
    {
        return "create-foia-alfresco-and-rm-folders";
    }

    @Override
    public void execute()
    {
        try
        {
            log.info("Creating FOIA Alfresco folders");

            String defaultBasePath = getEcmFileConfig().getDefaultBasePath();

            String requestsFolderName = defaultBasePath + "/" + REQUESTS_FOLDER_NAME;
            getEcmFileService().createFolder(requestsFolderName, ArkCaseCMISConstants.DEFAULT_CMIS_REPOSITORY_ID);

            log.info("Creating FOIA Alfresco folders successfully");

            log.info("Creating FOIA record categories");

            getAlfrescoRecordsService().createNewRecordCategoryInBaseFolder(REQUESTS_FOLDER_NAME);

            log.info("Creating FOIA record categories successfully");
        }
        catch (AcmCreateObjectFailedException | AlfrescoServiceException e)
        {
            log.error("Error on creating FOIA Alfresco structure and RM structure");
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
