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
import com.armedia.acm.plugins.complaint.model.ComplaintConstants;
import com.armedia.acm.plugins.complaint.model.ComplaintCreatedEvent;

import org.apache.chemistry.opencmis.client.api.Folder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

/**
 * Created by armdev on 5/1/14.
 */
public class AcmComplaintFolderListener implements ApplicationListener<ComplaintCreatedEvent>
{
    private transient Logger log = LoggerFactory.getLogger(getClass());
    private AlfrescoRecordsService alfrescoRecordsService;

    @Override
    public void onApplicationEvent(ComplaintCreatedEvent complaintCreatedEvent)
    {
        AlfrescoRmaConfig rmaConfig = alfrescoRecordsService.getRmaConfig();
        boolean proceed = rmaConfig.getIntegrationEnabled() && rmaConfig.getCreateRecordFolderOnComplaintCreate();

        if (!proceed)
        {
            return;
        }

        if (!complaintCreatedEvent.isSucceeded())
        {
            log.trace("Returning - complaint creation was not successful");
            return;
        }

        try
        {
            Folder categoryFolder = getAlfrescoRecordsService().findFolder(ComplaintConstants.OBJECT_TYPE);
            getAlfrescoRecordsService().createOrFindRecordFolder(complaintCreatedEvent.getComplaintNumber(), categoryFolder);
        }
        catch (AlfrescoServiceException e)
        {
            log.error("Could not create record folder for complaint: {}", e.getMessage(), e);
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
