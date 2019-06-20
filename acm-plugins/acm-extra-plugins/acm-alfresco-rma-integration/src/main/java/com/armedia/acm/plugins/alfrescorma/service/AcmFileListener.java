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

import com.armedia.acm.plugins.alfrescorma.exception.AlfrescoServiceException;
import com.armedia.acm.plugins.alfrescorma.model.AlfrescoRmaConfig;
import com.armedia.acm.plugins.ecm.model.EcmFileAddedEvent;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.ApplicationListener;

/**
 * Created by armdev on 5/1/14.
 */
public class AcmFileListener implements ApplicationListener<EcmFileAddedEvent>
{

    private transient Logger log = LogManager.getLogger(getClass());
    private AlfrescoRecordsService alfrescoRecordsService;

    @Override
    public void onApplicationEvent(EcmFileAddedEvent ecmFileAddedEvent)
    {
        AlfrescoRmaConfig rmaConfig = alfrescoRecordsService.getRmaConfig();
        boolean proceed = rmaConfig.getIntegrationEnabled() && rmaConfig.getDeclareRecordFolderOnFileUpload();

        if (!proceed)
        {
            return;
        }

        if (!ecmFileAddedEvent.isSucceeded())
        {
            log.trace("Returning - file creation was not successful");
            return;
        }

        try
        {
            getAlfrescoRecordsService().declareFileAsRecord(ecmFileAddedEvent.getSource().getContainer(), ecmFileAddedEvent.getEventDate(),
                    ecmFileAddedEvent.getParentObjectName(),
                    rmaConfig.getDefaultOriginatorOrg(),
                    ecmFileAddedEvent.getUserId(), ecmFileAddedEvent.getEcmFileId(), ecmFileAddedEvent.getSource().getStatus(),
                    ecmFileAddedEvent.getObjectId());

        }
        catch (AlfrescoServiceException e)
        {
            log.error("Could not declare file as record: {}", e.getMessage(), e);
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
}
