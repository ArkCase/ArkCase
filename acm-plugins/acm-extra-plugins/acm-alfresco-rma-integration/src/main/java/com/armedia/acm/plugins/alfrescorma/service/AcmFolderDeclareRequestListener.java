package com.armedia.acm.plugins.alfrescorma.service;

/*-
 * #%L
 * ACM Extra Plugin: Alfresco RMA Integration
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

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.plugins.alfrescorma.model.AlfrescoRmaPluginConstants;
import com.armedia.acm.plugins.ecm.dao.AcmFolderDao;
import com.armedia.acm.plugins.ecm.model.AcmCmisObjectList;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.AcmFolderConstants;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.model.EcmFolderDeclareRequestEvent;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

public class AcmFolderDeclareRequestListener implements ApplicationListener<EcmFolderDeclareRequestEvent>
{

    private transient Logger log = LoggerFactory.getLogger(getClass());
    private AlfrescoRecordsService alfrescoRecordsService;
    private AcmFolderService acmFolderService;
    private AcmFolderDao acmFolderDao;

    @Override
    public void onApplicationEvent(EcmFolderDeclareRequestEvent ecmFolderDeclareRequestEvent)
    {
        boolean proceed = getAlfrescoRecordsService()
                .checkIntegrationEnabled(AlfrescoRmaPluginConstants.FOLDER_DECLARE_REQUEST_INTEGRATION_KEY);

        if (!proceed)
        {
            return;
        }

        if (!ecmFolderDeclareRequestEvent.isSucceeded())
        {
            if (log.isTraceEnabled())
            {
                log.trace("Returning - folder declaration request was not successful");
            }
            return;
        }
        if (null != ecmFolderDeclareRequestEvent.getSource())
        {
            AcmCmisObjectList acmCmisObjectList = ecmFolderDeclareRequestEvent.getSource();
            getAlfrescoRecordsService().declareAllFilesInFolderAsRecords(acmCmisObjectList, ecmFolderDeclareRequestEvent.getContainer(),
                    ecmFolderDeclareRequestEvent.getEventDate(), ecmFolderDeclareRequestEvent.getParentObjectName());

            AcmFolder folder = getAcmFolderService().findById(acmCmisObjectList.getFolderId());
            try
            {
                if (folder == null)
                {
                    throw new AcmObjectNotFoundException(AcmFolderConstants.OBJECT_FOLDER_TYPE, folder.getId(), "Folder not found", null);
                }
                else
                {
                    folder.setStatus(EcmFileConstants.RECORD);
                    getAcmFolderDao().save(folder);
                    if (log.isDebugEnabled())
                    {
                        log.debug("Folder with ID: " + folder.getId() + " Status is changed to " + EcmFileConstants.RECORD);
                    }
                }
            }
            catch (Exception e)
            {
                if (log.isErrorEnabled())
                {
                    log.error("Status change failed for " + folder.getName() + " folder" + e.getMessage(), e);
                }
            }
        }
        else
        {
            return;
        }
    }

    public AlfrescoRecordsService getAlfrescoRecordsService()
    {
        return alfrescoRecordsService;
    }

    public void setAlfrescoRecordsService(AlfrescoRecordsService alfrescoRecordsService)
    {
        this.alfrescoRecordsService = alfrescoRecordsService;
    }

    public AcmFolderService getAcmFolderService()
    {
        return acmFolderService;
    }

    public void setAcmFolderService(AcmFolderService acmFolderService)
    {
        this.acmFolderService = acmFolderService;
    }

    public AcmFolderDao getAcmFolderDao()
    {
        return acmFolderDao;
    }

    public void setAcmFolderDao(AcmFolderDao acmFolderDao)
    {
        this.acmFolderDao = acmFolderDao;
    }
}
